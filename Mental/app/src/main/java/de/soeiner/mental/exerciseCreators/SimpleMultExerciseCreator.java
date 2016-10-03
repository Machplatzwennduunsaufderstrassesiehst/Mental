package de.soeiner.mental.exerciseCreators;

/**
 * Created by sven on 25.04.16.
 */
public class SimpleMultExerciseCreator extends ArithmeticExerciseCreator {

    public String getName() {
        return "Kleines 1x1";
    }

    public double getExpectedSolveTime() {
        return 3;
    }

    public String create() {
        int d = (int) Math.floor(Math.sqrt(difficulty / 2)); // difficulty / 2;
        if (d > 4) d = 4;
        int a = (int) (Math.random() * (10 - d)) + d; // Zahlen zwischen d und 10
        int b = (int) (Math.random() * (10 - d)) + d;
        return createMult(a % 10 + 1, b % 10 + 1);
    }

    public ExerciseCreator copy() {
        return new SimpleMultExerciseCreator();
    }
}
