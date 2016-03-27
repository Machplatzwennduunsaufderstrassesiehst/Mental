package de.soeiner.mental;

/**
 * Created by sven on 22.03.16.
 */
public abstract class ExerciseCreator {
    protected String exerciseString = "";
    protected String lastExerciseString = "";
    protected int difficulty = 0;
    protected int exerciseResult = 0;

    public String getExerciseString() {
        return exerciseString;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void resetDifficulty() {
        difficulty = 1;
    }

    public void increaseDifficulty() {
        difficulty++;
    }

    public boolean checkAnswer(int playerAnswer) {
        return (exerciseResult == playerAnswer);
    }

    // erstellt die nächste Aufgabe und setzt exerciseResult sowie exerciseString
    public abstract String create();

    public String createNext() {
        String exercise;
        int retry = 0;
        do {
            exercise = create();
            retry++;
        } while (retry <= 3 && exercise.equals(lastExerciseString));
        lastExerciseString = exercise;
        return exercise;
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
    public String create() {
        int d = difficulty / 2;
        int a = (int) (Math.random() * (10-d)) + d; // Zahlen zwischen d und 10
        int b = (int) (Math.random() * (10-d)) + d;
        return createMult(a % 10 + 1, b % 10 + 1);
    }
}

class MultExerciseCreator extends ExerciseCreator {
    public String create() {
        int d = difficulty;
        int a = (int) (Math.random() * (20-d)) + d; // Zahlen zwischen d und 20
        int bmax = 20;
        if (a <= 4) bmax = 100 * d / a;
        int b = (int) (Math.random() * (bmax-d)) + d;
        return createMult(a % 20 + 1, b % bmax + 1);
    }
}

// neuer Vorschlag, das erhöhen von difficulty schlägt hier mehr ins gewicht
class MixedExerciseCreator2 extends ExerciseCreator {
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

class MixedExerciseCreator extends ExerciseCreator {
    public String create() {
        System.out.println("!!!!!!!!!!!!!!DIFFICULTY:  "+(difficulty+50));
        String exercise = "";
        int result = 0;
        start: while (exercise.equals("")) {
            int temp;
            int a = (int) (Math.random() * 5 * (difficulty+50) / 2 + Math.random() * 20);
            int b = (int) (Math.random() * 5 * (difficulty+50) / 2 + Math.random() * 20);

            if ((difficulty+50) % 5 == 0) {
                while (a * b < (100 + 8 * (difficulty+50)) - (75 + (difficulty+50))) {
                    a += b;
                    b += b;
                }
                while (a * b > 100 + 4 * (difficulty+50)) {
                    if (a >= b)
                        a = (int) (a / 2);
                    if (b > a)
                        b = (int) (b / 2);
                }
                result = a * b;
                exercise = a + " * " + b;
            } else {
                if (a < (difficulty+50) || b < (difficulty+50)) {
                    continue start;
                }
                if (a - b < (difficulty+50)) {
                    continue start;
                }
                if ((difficulty+50) % 3 == 0) {
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