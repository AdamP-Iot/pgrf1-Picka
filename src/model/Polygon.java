package model;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Polygon {
    public List<Line> lines;

    public Polygon(List<Line> lines) {
        this.lines = lines;
    }

    public List<Line> getLines() {
        return lines;
    }

    public void setLines(List<Line> lines) {
        this.lines = lines;
    }

}
