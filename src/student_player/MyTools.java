package student_player;

import boardgame.Move;
import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoCoord;
import pentago_twist.PentagoMove;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyTools {

    public static List<List<PentagoCoord>> groupsOfFive = new ArrayList<>();

    /**
     * Constructor to fill the attribute groupsOfFive
     */
    public MyTools() {
        List<PentagoCoord> group = new ArrayList<>();

        // horizontal groups
        for(int x=0; x<6; x++) {
            for(int y=0; y<5; y++)
                group.add(new PentagoCoord(x,y));
            groupsOfFive.add(group);
            group = new ArrayList<>();
            for(int y=1; y<6; y++)
                group.add(new PentagoCoord(x,y));
            groupsOfFive.add(group);
            group = new ArrayList<>();
        }

        // vertical groups
        for(int y=0; y<6; y++) {
            for(int x=0; x<5; x++)
                group.add(new PentagoCoord(x,y));
            groupsOfFive.add(group);
            group = new ArrayList<>();
            for(int x=1; x<6; x++)
                group.add(new PentagoCoord(x,y));
            groupsOfFive.add(group);
            group = new ArrayList<>();
        }

        // negative slope diagonals
        int x=0, y=0;
        while(x<5 && y<5) group.add(new PentagoCoord(x++,y++));
        groupsOfFive.add(group);
        group = new ArrayList<>();

        x=1; y=1;
        while(x<6 && y<6) group.add(new PentagoCoord(x++,y++));
        groupsOfFive.add(group);
        group = new ArrayList<>();

        x=0; y=1;
        while(x<6 && y<6) group.add(new PentagoCoord(x++,y++));
        groupsOfFive.add(group);
        group = new ArrayList<>();

        x=1; y=0;
        while(x<6 && y<6) group.add(new PentagoCoord(x++,y++));
        groupsOfFive.add(group);
        group = new ArrayList<>();

        // positive slope diagonals
        x=0; y=5;
        while(x<5 && y>0) group.add(new PentagoCoord(x++,y--));
        groupsOfFive.add(group);
        group = new ArrayList<>();

        x=1; y=4;
        while(x<6 && y>=0) group.add(new PentagoCoord(x++,y--));
        groupsOfFive.add(group);
        group = new ArrayList<>();

        x=0; y=4;
        while(x<6 && y>=0) group.add(new PentagoCoord(x++,y--));
        groupsOfFive.add(group);
        group = new ArrayList<>();

        x=1; y=5;
        while(x<6 && y>=0) group.add(new PentagoCoord(x++,y--));
        groupsOfFive.add(group);
    }

    /**
     * Calculates the total score for a given coordinate, considering all possible rotations/flip that cn happen in the move
     * also identifies the best possible rotation for the given coordinate
     * @param board current board state
     * @param coord coordinate considered to be played
     * @return {score, rotation/flip quadrant, rotation/flip indicator}
     */
    public static int[] scoreForCoord(PentagoBoardState board, PentagoCoord coord) {
        int player = board.getTurnPlayer();
        int score = 0;
        int maxScore = Integer.MIN_VALUE;
        int[]rotationFlip = new int[2];
        for(int aSwap=0; aSwap<4; aSwap++) {
            for(int bSwap=0; bSwap<2; bSwap++) {
                PentagoMove move = new PentagoMove(coord, aSwap, bSwap, player);
                PentagoBoardState minPlayerBoard = (PentagoBoardState)board.clone();
                minPlayerBoard.processMove(move);
                int rotationScore = scoreByGroups(minPlayerBoard);
                score += rotationScore;
                if(rotationScore > maxScore) {
                    maxScore = rotationScore;
                    rotationFlip[0] = aSwap;
                    rotationFlip[1] = bSwap;
                }
            }
        }
        return new int[]{score, rotationFlip[0], rotationFlip[1]};
    }

    /**
     * find all empty coordinates on the board
     * @param board
     * @return
     */
    public static List<PentagoCoord> getValidCoords(PentagoBoardState board) {
        List<PentagoCoord> coords = new ArrayList<>();
        for(int i=0; i<6; i++) {
            for(int j=0; j<6; j++) {
                if(board.getPieceAt(i,j).equals(PentagoBoardState.Piece.EMPTY))
                    coords.add(new PentagoCoord(i,j));
            }
        }
        return coords;
    }

    /**
     * heuristic calculator using groups of five
     * @param board
     * @return
     */
    public static int scoreByGroups(PentagoBoardState board) {
        int player = 1 - board.getTurnPlayer();
        PentagoBoardState.Piece playerPiece = (player == 0 ? PentagoBoardState.Piece.WHITE : PentagoBoardState.Piece.BLACK);
        int playerScore = 0;
        int opponentScore = 0;

        for(List<PentagoCoord> group : groupsOfFive) {
            int playerCount = 0;
            int opponentCount = 0;
            for(PentagoCoord coord : group) {
                if(board.getPieceAt(coord).equals(playerPiece)) playerCount++;
                else if(board.getPieceAt(coord).equals(PentagoBoardState.Piece.EMPTY)) {}
                else opponentCount++;
            }
            if(playerCount == 5) playerScore += 1000;
            if(opponentCount == 0) playerScore += Math.pow(playerCount,4);
            if(playerCount == 0) opponentScore += Math.pow(opponentCount,3);
        }
        return playerScore - opponentScore;
    }

    /**
     * method to sort all valid coordinates in ascending order with regard to their heuristic score
     * using a pair object that pairs coordiantes with heuristic scores
     * @param board
     * @param currCoords
     * @return
     */
    public static List<PentagoCoord> sortByScore(PentagoBoardState board,List<PentagoCoord> currCoords) {
        List<CoordAndScore> validCoordScores = new ArrayList<>();
        for(PentagoCoord crd : currCoords) {
            Integer score = scoreForCoord(board, crd)[0];
            validCoordScores.add(new CoordAndScore(crd,score));
        }
        Collections.sort(validCoordScores);
        return CoordAndScore.getCoords(validCoordScores);
    }

    /**
     * recursive minimax to calculate scores
     * it calls scoreByGroups to calculate terminal node heurisitcs
     * @param board current state of the board
     * @param coord coordinate in quesition of being played
     * @param depth current depth of the algorithm (desends)
     * @param maximizing:true if calculasting for the maximizing player, false otherwise
     * @param alpha alpha value for alpha-beta pruning
     * @param beta beta value for alpha-beta pruning
     * @return heruistic minimax score for the given coordinate
     */
    public static int minimax(PentagoBoardState board, PentagoCoord coord, int depth, boolean maximizing, int alpha, int beta) {
        int player = board.getTurnPlayer();
        if(depth == 0) return scoreForCoord(board, coord)[0];

        int[]rotation = {scoreForCoord(board, coord)[1], scoreForCoord(board, coord)[2]};
        PentagoMove move = new PentagoMove(coord, rotation[0], rotation[1], player);

        PentagoBoardState copyBoard = (PentagoBoardState)board.clone();
        copyBoard.processMove(move);

        if(copyBoard.gameOver()) return scoreByGroups(copyBoard);

        List<PentagoCoord> validCoords = getValidCoords(copyBoard);
        validCoords = sortByScore(copyBoard, validCoords);
        int size = validCoords.size();
        int n = (int)(size * 0.5);
        int turnLimit = 6;

        if(maximizing) {
            if(copyBoard.getTurnNumber()<turnLimit)
                validCoords = validCoords.subList(n, size);
            int max = Integer.MIN_VALUE;
            for(PentagoCoord crd : validCoords) {
                int val = -minimax(copyBoard, crd, depth-1, false, alpha, beta);
                if(val > max) max = val;
                if(max > alpha) alpha = max;
                if(alpha >= beta) break;
            }
            return max;
        }
        else {
            if(copyBoard.getTurnNumber()<turnLimit)
                validCoords = validCoords.subList(0, n);
            int min = Integer.MAX_VALUE;
            for(PentagoCoord crd : validCoords) {
                int val = -minimax(copyBoard, crd, depth-1, true, alpha, beta);
                if(val < min) min = val;
                if(min < beta) beta = min;
                if(alpha >= beta) break;
            }
            return min;
        }
    }

    /**
     * small helper method to get all occurrences of a value in a list
     * @param values list to be searched in
     * @param score value to look for
     * @return indices of all occurrences of the value in the list
     */
    public static List<Integer> getIndicesByScore(List<Integer> values, int score) {
        List<Integer> indices = new ArrayList<>();
        for(int i=0; i<values.size(); i++) {
            if(values.get(i) == score) indices.add(i);
        }
        return indices;
    }

}