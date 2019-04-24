package uk.gov.metoffice.hello.message;

import java.util.ArrayList;
import java.util.List;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class AdminArea {

    private int areaId;

    private String name;

    private int threshold;

    private List<Integer> squares = new ArrayList<>();

    public int getAreaId() {
        return areaId;
    }

    public String getName() {
        return name;
    }

    public int getThreshold() {
        return threshold;
    }

    public List<Integer> getSquares() {
        return squares;
    }
}
