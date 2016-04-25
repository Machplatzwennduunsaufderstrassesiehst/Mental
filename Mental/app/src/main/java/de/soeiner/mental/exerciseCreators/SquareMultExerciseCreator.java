package de.soeiner.mental.exerciseCreators;

/**
 * Created by sven on 25.04.16.
 */
public class SquareMultExerciseCreator extends ArithmeticExerciseCreator {

    public String getName(){return "Quadratzahlen";}

    public double getExpectedSolveTime(){
        return 4;
    }

    public String create() {
        int a = (int) (Math.random() * 20 + 1); // Zahlen zwischen d und 20
        return createMult(a, a);
    }

}
