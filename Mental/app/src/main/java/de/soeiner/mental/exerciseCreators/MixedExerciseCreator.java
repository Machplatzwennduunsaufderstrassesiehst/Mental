package de.soeiner.mental.exerciseCreators;

/**
 * Created by sven on 25.04.16.
 */
// neuer Vorschlag, das erhöhen von difficulty schlägt hier mehr ins gewicht
public class MixedExerciseCreator extends ArithmeticExerciseCreator {

    public String getName(){return "Gemischte Aufgaben";}

    public double getExpectedSolveTime(){
        return 8;
    }

    public String create() {
        String exercise = "";
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
                createMult(a,b);
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
                    createSub(a,b);
                } else {
                    createAdd(a,b);
                }
            }
        }
        return exercise;
    }
}
