package student_player;

import boardgame.Move;

import pentago_twist.PentagoCoord;
import pentago_twist.PentagoMove;
import pentago_twist.PentagoPlayer;
import pentago_twist.PentagoBoardState;

import java.util.*;

/** A player file submitted by a student. */
public class StudentPlayer extends PentagoPlayer {

    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public StudentPlayer() {
        super("260736004");
    }

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    public Move chooseMove(PentagoBoardState boardState) {
        // must be instantiated for groupsOfFive to be filled
        MyTools tools = new MyTools();

        int turn = boardState.getTurnNumber();
        if(turn==0) return boardState.getRandomMove();

        // this is essentially the first depth of minimax
        // I do these here so that I can have the final list of scores and evaluate
        List<PentagoCoord> maxPlayerCoords = MyTools.getValidCoords(boardState);
        maxPlayerCoords = MyTools.sortByScore(boardState, maxPlayerCoords);
        int n = maxPlayerCoords.size();
        maxPlayerCoords = maxPlayerCoords.subList(n/2, n);
        List<Integer> maxPlayerCoordScores = new ArrayList<>();
        for(PentagoCoord coord : maxPlayerCoords) {
            int depth = (turn < 6 ? 1 : 2);
            int score = MyTools.minimax(boardState, coord, depth, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
            maxPlayerCoordScores.add(score);
        }

        int maxScore = Collections.max(maxPlayerCoordScores);
        List<Integer>indices = MyTools.getIndicesByScore(maxPlayerCoordScores, maxScore);
        Random rand = new Random();
        int index = indices.get(rand.nextInt(indices.size()));
        PentagoCoord coord = maxPlayerCoords.get(index);
        Move myMove = new PentagoMove(coord, MyTools.scoreForCoord(boardState, coord)[1], MyTools.scoreForCoord(boardState, coord)[2], boardState.getTurnPlayer());
        return myMove;
    }
}