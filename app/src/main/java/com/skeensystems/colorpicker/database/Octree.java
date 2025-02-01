package com.skeensystems.colorpicker.database;

import java.util.ArrayList;

/**
 * see if this is needed for performance
 */
class Octree {
    private final int LEFT_FRONT_BOTTOM = 0;
    private final int LEFT_FRONT_TOP = 1;
    private final int LEFT_BACK_BOTTOM = 2;
    private final int LEFT_BACK_TOP = 3;
    private final int RIGHT_FRONT_BOTTOM = 4;
    private final int RIGHT_FRONT_TOP = 5;
    private final int RIGHT_BACK_BOTTOM = 6;
    private final int RIGHT_BACK_TOP = 7;

    private final ArrayList<DatabaseColour> items;
    private Octree[] subtrees;

    // R = LEFT - RIGHT (x)
    private int centreR;
    // G = FRONT - BACK (y)
    private int centreG;
    // B = BOTTOM - TOP (z)
    private int centreB;

    Octree(ArrayList<DatabaseColour> items, int centreR, int centreB, int centreG) {

        this.items = items;
        this.centreR = centreR;
        this.centreG = centreG;
        this.centreB = centreB;

        if (items.size() > 1) {
            subtrees = new Octree[8];
        }
    }

    public ArrayList<DatabaseColour> getItems() {
        return items;
    }

    public Octree getSubtree(SavedColour colour) {
        if (subtrees == null) return this;
        Octree subtree;
        if (colour.getR() < centreR) {
            if (colour.getG() < centreG) {
                if (colour.getB() < centreB) subtree = subtrees[LEFT_FRONT_BOTTOM];
                else subtree = subtrees[LEFT_FRONT_TOP];
            }
            else {
                if (colour.getB() < centreB) subtree = subtrees[LEFT_BACK_BOTTOM];
                else subtree = subtrees[LEFT_BACK_TOP];
            }
        }
        else {
            if (colour.getG() < centreG) {
                if (colour.getB() < centreB) subtree = subtrees[RIGHT_FRONT_BOTTOM];
                else subtree = subtrees[RIGHT_FRONT_TOP];
            }
            else {
                if (colour.getB() < centreB) subtree = subtrees[RIGHT_BACK_BOTTOM];
                else subtree = subtrees[RIGHT_BACK_TOP];
            }
        }
        if (subtree == null) return this;
        else return subtree.getSubtree(colour);
    }
}
