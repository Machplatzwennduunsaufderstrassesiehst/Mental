package de.soeiner.mental.exerciseCreators;

/**
 * Created by sven on 25.04.16.
 */

public class MultExerciseCreator extends ArithmeticExerciseCreator {

    public String getName(){return "Großes 1x1";}

    public double getExpectedSolveTime(){
        return 10;
    }

    public String create() {
        int d = (int) Math.sqrt(difficulty);
        if (d > 10) d = 10;
        int a = (int) (Math.random() * (20-d)) + d; // Zahlen zwischen d und 20
        int bmax = 20;
        if (a <= 4) bmax = 100 * d / a;
        int b = (int) (Math.random() * (bmax-d)) + d;
        return createMult(a % 20 + 1, b % bmax + 1);
    }

    @Override
    public ExerciseCreator copy() {
        return new MultExerciseCreator();
    }
}
