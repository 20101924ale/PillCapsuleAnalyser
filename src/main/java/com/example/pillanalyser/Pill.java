package com.example.pillanalyser;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Pill {
    private String name;
    private int root;
    private ArrayList<Integer> indexes = new ArrayList<Integer>();
    private Color colour;
    private boolean twoColours;

    public Pill(String name, int root, Color color) {
        this.name = name;
        this.root = root;
        this.colour = color;
        this.twoColours = false;
    }

    public boolean isTwoColours() {
        return twoColours;
    }
    public void setTwoColours(boolean twoColours) {
        this.twoColours = twoColours;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getRoot() {
        return root;
    }
    public void setRoot(int root) {
        this.root = root;
    }

    public ArrayList<Integer> getIndexes() {
        return indexes;
    }
    public void setIndexes(ArrayList<Integer> indexes) {
        this.indexes = indexes;
    }

    public Color getColour() {
        return colour;
    }
    public void setColour(Color colour) {
        this.colour = colour;
    }

    @Override
    public String toString() {
        return "Pill{" +
                "name='" + name + '\'' +
                ", root=" + root +
                ", indexes=" + indexes +
                ", colour=" + colour +
                ", isTwoColours=" + twoColours +
                '}';
    }
}
