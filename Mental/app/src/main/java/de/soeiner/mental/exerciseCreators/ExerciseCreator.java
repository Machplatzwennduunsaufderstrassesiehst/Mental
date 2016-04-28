package de.soeiner.mental.exerciseCreators;

import org.json.JSONObject;

/**
 * Created by sven on 22.03.16.
 */
public abstract class ExerciseCreator {

    protected int startDifficulty = 1; //damit auf wunsch der Schwierigkeitsgrad eingestellt werden kann
    protected JSONObject exerciseObject;
    protected int difficulty = startDifficulty;

    public abstract double getExpectedSolveTime(); //in sekunden

    public int getDifficulty() {
        return difficulty;
    }

    public JSONObject getExerciseObject() {
        return exerciseObject;
    }

    public void setDifficulty(int d){ difficulty = d; }

    public void resetDifficulty() {
        difficulty = startDifficulty;
    }

    public void setStartDifficulty(int d) { startDifficulty = d; }

    public void increaseDifficulty() {
        difficulty++;
    }

    public boolean checkAnswer(JSONObject answer) {
        throw new RuntimeException("checkAnswer ist für den Type " + this.getClass() + "nicht möglich");
    }

    // erstellt die nächste Aufgabe
    public final JSONObject next() {
        exerciseObject = createNext();
        System.out.println("ExerciseObject: " + exerciseObject.toString());
        return exerciseObject;
    }

    // soll man nicht mehr von außen aufrufen
    abstract JSONObject createNext();

    // Zwingt alle exerciseCreator, einen Namen bereitzustellen
    public abstract String getName();

    public abstract ExerciseCreator copy();
}