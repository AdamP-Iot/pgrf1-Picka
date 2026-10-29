package model;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Polygon {
    public List<Line> lines;
    public Integer fillColor = null;

    public Polygon(List<Line> lines) {
        this.lines = lines;
    }

    public List<Line> getLines() {
        return lines;
    }

    public void setLines(List<Line> lines) {
        this.lines = lines;
    }

    public Integer getFillColor() { //pro uchování vyplněných barev
        return fillColor;
    }

    public void setFillColor(Integer fillColor) {
        this.fillColor = fillColor;
    }
}
