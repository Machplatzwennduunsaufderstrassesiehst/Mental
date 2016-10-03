package de.soeiner.mental.util;

import com.android.internal.util.Predicate;

import java.util.Arrays;
import java.util.Stack;

/**
 * Pathfinder class
 *
 * @version 2.0
 */
public class Pathfinder<T> {

    /**
     * map which will be modified later
     */
    private T[][] map;

    /**
     * goal is the T to be found in the map
     */
    private T goal;
    /**
     * start is the T to start at in the map
     */
    private T start;

    /**
     * contains only walkable positions that were already checked
     */
    private Stack<int[]> checkedStack = new Stack<>();

    private Predicate<T> walkable = new Predicate<T>() {
        @Override
        public boolean apply(T t) {
            return t == null;
        }
    };

    /**
     * setMap
     *
     * @param map the map to be stored in this Pathfinder object
     */
    public void setMap(T[][] map) {
        this.map = map;
    }

    /**
     * searchPath
     *
     * @param start the start T
     * @param goal  the T to be found in the map
     * @return the modified map as a human readable string
     */
    public Stack<int[]> searchPath(T start, T goal) {
        checkedStack = new Stack<>();
        this.goal = goal;
        this.start = start;

        int[] s = searchT(start);
        Stack<int[]> stack = move(s[0], s[1], s[0], s[1]);
        Object useless = ((stack != null) ? stack.pop() : null); // pop the peak, which is the goal itself
        return stack;
    }

    /**
     * recursive depth first search like method
     * <p/>
     * move from r0,c0 to r1,c1. mark r1,c1 as visited only if there is a path
     * to the 'goal' from r2,c2 which are all the positions next to r1,c1.
     *
     * @param r0 the row index where this method was called from
     * @param c0 the col index where this method was called from
     * @param r1 the row index which should be examined
     * @param c1 the col index which should be examined
     * @return a stack with the shortest path from r1,c1 to the goal, null if there is no path
     */
    private Stack<int[]> move(int r0, int c0, int r1, int c1) {
        int[] pos = {r1, c1};
        //System.out.println(Arrays.toString(pos));
        //System.out.println(isValid(r1, c1));
        //System.out.println(checkPos(r1, c1));
        if (!checkPos(r1, c1)) {
            return null;
        }
        if (map[r1][c1] == goal) {
            return new Stack<>();
        }
        for (int[] p : checkedStack) {
            if (p[0] == r1 && p[1] == c1) {
                return null;
            }
        }
        checkedStack.push(pos);
        Stack<int[]> shortestMoveStack = null;
        // 'direction' array
        int[][] nextPositions = {{r1 + 1, c1}, {r1 - 1, c1}, {r1, c1 + 1}, {r1, c1 - 1}};
        for (int[] nextPosition : nextPositions) {
            //System.out.println("Next Position: " + Arrays.toString(nextPosition));
            int r2 = nextPosition[0];
            int c2 = nextPosition[1];
            if (r2 == r0 && c2 == c0) {
                continue;
            }
            Stack<int[]> moveStack = move(r1, c1, r2, c2);
            if (moveStack != null) {
                if (shortestMoveStack == null || moveStack.size() <= shortestMoveStack.size()) {
                    shortestMoveStack = moveStack;
                    shortestMoveStack.push(pos);
                }
            }
        }
        return shortestMoveStack;
    }


    /**
     * search an object and return its coordinates
     *
     * @param e T to find
     * @return 2-Int array (r,c) T position
     */
    private int[] searchT(T e) {
        for (int r = 0; r < map.length; r++) {
            for (int c = 0; c < map[r].length; c++) {
                if (map[r][c] == e) {
                    return new int[]{r, c};
                }
            }
        }
        return null;
    }

    public void setWalkablePredicate(Predicate<T> walkable) {
        this.walkable = walkable != null ? walkable : this.walkable;
    }

    /**
     * check if a position (r,c) is walkable and in the map's range
     *
     * @param r is the r index to be checked
     * @param c is the c index to be checked
     * @return true or false
     */
    private boolean checkPos(int r, int c) {
        return isValid(r, c) && (walkable.apply(map[r][c]) || map[r][c] == start || map[r][c] == goal);
    }

    /**
     * check if a position (r,c) is in the map's range
     *
     * @param r is the r index to be checked
     * @param c is the c index to be checked
     * @return true or false
     */
    private boolean isValid(int r, int c) {
        return 0 <= r && r < map.length && 0 <= c && c < map[r].length;
    }


    /**
     * toString - returns a String holding grid in a human readable format
     *
     * @param map the map to be searched on
     * @return map as human readable string
     */
    public String toString(T[][] map) {
        String s = "";
        // r: row index, c: column index
        for (T[] row : map) {
            for (T cell : row) {
                s += "" + cell + " ";
            }
            s += "\n";
        }
        s += "\n";
        return s;
    }
}
/*
            
        try {Thread.sleep(100);
            T[][] showMap = new T[map.length][map[0].length];
            for (int r = 0; r < map.length; r++) {
                for (int c = 0; c < map[0].length; c++) {
                    showMap[r][c] = map[r][c];
                }
            }
            showMap[r1][c1] = 'O';
            System.out.println(asString(showMap));
        } catch (Exception e) {}
*/
