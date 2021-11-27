package student_player;

import pentago_twist.PentagoCoord;

import java.util.ArrayList;
import java.util.List;

public class CoordAndScore implements Comparable<CoordAndScore> {
    PentagoCoord coord;
    Integer score;

    public CoordAndScore(PentagoCoord coord, Integer score) {
        this.coord = coord;
        this.score = score;
    }
    @Override
    public int compareTo(CoordAndScore cs) {
        return this.score.compareTo(cs.score);
    }

    public static List<PentagoCoord> getCoords(List<CoordAndScore> pairs) {
        List<PentagoCoord> coords = new ArrayList<>();
        for(CoordAndScore pair : pairs)
            coords.add(pair.coord);
        return coords;
    }
}
