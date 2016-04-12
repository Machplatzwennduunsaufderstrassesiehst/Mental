package de.soeiner.mental;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by sven on 22.03.16.
 */
public abstract class ExerciseCreator {

    protected int startDifficulty = 1; //damit auf wunsch der Schwierigkeitsgrad eingestellt werden kann
    protected String exerciseString = "";
    protected ArrayList<Object> previousAnswers = new ArrayList<Object>();
    protected int difficulty = startDifficulty;
    protected int exerciseResult = 0;

    public String getExerciseString() { return exerciseString; }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int d){ difficulty = d; }

    public void resetDifficulty() {
        difficulty = startDifficulty;
    }

    public void setStartDifficulty(int d) { startDifficulty = d; }

    public void increaseDifficulty() {
        difficulty++;
    }

    public boolean checkAnswer(int playerAnswer) {
        return (exerciseResult == playerAnswer);
    }

    // erstellt die nächste Aufgabe und setzt exerciseResult sowie exerciseString
    public abstract String create();

    // Zwingt alle exerciseCreator, einen Namen bereitzustellen
    public abstract String getName();

    public String createNext() {
        do {
            this.create();
        } while (previousAnswers.contains(exerciseResult));
        if(previousAnswers.size() >= getFahkinBitchExerciseResetValue()) {
            previousAnswers.clear();
        }
        return exerciseString;
    }

    public int getFahkinBitchExerciseResetValue() {
        return 10;
    }

    protected String createSub(int a, int b) {
        exerciseString = a + " - " + b;
        exerciseResult = a - b;
        return exerciseString;
    }

    protected String createMult(int a, int b) {
        exerciseString = a + " * " + b;
        exerciseResult = a * b;
        return exerciseString;
    }

    protected String createAdd(int a, int b) {
        exerciseString = a + " + " + b;
        exerciseResult = a + b;
        return exerciseString;
    }
}

class SimpleMultExerciseCreator extends ExerciseCreator {

    public String getName(){return "Kleines 1x1";}

    public String create() {
        int d = difficulty / 2;
        int a = (int) (Math.random() * (10-d)) + d; // Zahlen zwischen d und 10
        int b = (int) (Math.random() * (10-d)) + d;
        return createMult(a % 10 + 1, b % 10 + 1);
    }
}

class MultExerciseCreator extends ExerciseCreator {

    public String getName(){return "Großes 1x1";}

    public String create() {
        int d = difficulty;
        int a = (int) (Math.random() * (20-d)) + d; // Zahlen zwischen d und 20
        int bmax = 20;
        if (a <= 4) bmax = 100 * d / a;
        int b = (int) (Math.random() * (bmax-d)) + d;
        return createMult(a % 20 + 1, b % bmax + 1);
    }
}

class SquareMultExerciseCreator extends ExerciseCreator {

    public String getName(){return "Quadratzahlen";}

    public String create() {
        int d = 1;
        int a = (int) (Math.random() * 20 +1); // Zahlen zwischen d und 20
        return createMult(a, a);
    }

}

// neuer Vorschlag, das erhöhen von difficulty schlägt hier mehr ins gewicht
class MixedExerciseCreator extends ExerciseCreator {

    public String getName(){return "Gemischte Aufgaben";}

    public String create() {
        String exercise = "";
        int result = 0;
        int d = difficulty * 5;
        start:
        while (exercise.equals("")) {
            int temp;
            int a = (int) (Math.random() * 5 * d / 2 + Math.random() * 20);
            int b = (int) (Math.random() * 5 * d / 2 + Math.random() * 20);

            if (difficulty % 5 == 0) {
                while (a * b < (100 + 8 * difficulty) - (75 + difficulty)) {
                    a += b;
                    b += b;
                }
                while (a * b > 100 + 4 * difficulty) {
                    if (a >= b)
                        a = (int) (a / 2);
                    if (b > a)
                        b = (int) (b / 2);
                }
                result = a * b;
                exercise = a + " * " + b;
            } else {
                if (a < d || b < d) {
                    continue start;
                }
                if (a - b < d) {
                    continue start;
                }
                if (difficulty % 3 == 0) {
                    if (a < b) {
                        temp = a;
                        a = b;
                        b = temp;
                    }
                    result = a - b;
                    exercise = a + " - " + b;
                } else {
                    result = a + b;
                    exercise = a + " + " + b;
                }
            }
        }
        exerciseResult = result;
        exerciseString = exercise;
        return exercise;
    }
}