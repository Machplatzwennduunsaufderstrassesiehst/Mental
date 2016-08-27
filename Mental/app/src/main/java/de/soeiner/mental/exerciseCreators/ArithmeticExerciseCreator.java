package de.soeiner.mental.exerciseCreators;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sven on 25.04.16.
 */
public abstract class ArithmeticExerciseCreator extends ExerciseCreator {

    private String exerciseString;
    private int exerciseResult;
    protected ArrayList<Object> previousResults = new ArrayList<Object>();

    public int getFahkinBitchExerciseResetValue() {
        return 10;
    }

    public String getType() {
        return "arithmetic";
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

    public boolean checkAnswer(JSONObject answer) {
        return (exerciseResult == answer.optInt("value"));
    }

    public String getExerciseString() {
        return exerciseString;
    }

    public JSONObject createNext() {
        do {
            this.create();
            System.out.println("create()");
        }
        while (previousResults.contains(exerciseResult) && previousResults.size() < getFahkinBitchExerciseResetValue());
        if (previousResults.size() >= getFahkinBitchExerciseResetValue()) {
            previousResults.clear();
        }
        JSONObject j = new JSONObject();
        previousResults.add(exerciseResult);
        try {
            j.put("type", getType());
            j.put("exerciseString", exerciseString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return j;
    }

    public abstract String create();
}
