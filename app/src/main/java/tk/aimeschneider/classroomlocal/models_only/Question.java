package tk.aimeschneider.classroomlocal.models_only;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Question {

    static private String JSON_TEXT = "text";
    static private String JSON_DURATION = "duration";
    static private String JSON_REPONSES = "answers";
    static private String JSON_IS_RIGHT = "is_right";
    private String question;
    private HashMap<String,Integer> reponses;
    private int duration;

    public Question(String question, HashMap<String, Integer> reponses, int duration) {
        this.question = question;
        this.reponses = reponses;
        this.duration = duration;
    }

    public static Question JsonToQuestion(JSONObject json) throws JSONException {

            String question = json.getString(JSON_TEXT);
            int duration = json.getInt(JSON_DURATION);
            JSONArray reponseJson = json.getJSONArray(JSON_REPONSES);
            HashMap<String, Integer> mapReponse = new HashMap<String, Integer>();
            for (int j = 0; j < reponseJson.length(); j++) {
                mapReponse.put(reponseJson.getJSONObject(j).getString(JSON_TEXT), reponseJson.getJSONObject(j).getInt(JSON_IS_RIGHT));
            }
        return new Question(question, mapReponse,duration);
    }
    public String getQuestion() {
        return question;
    }

    public HashMap<String, Integer> getReponses() {
        return reponses;
    }

    public int getDuration() {
        return duration;
    }

    public boolean IsBonneReponse(String reponse)
    {
        return (reponses.get(reponse) == 1);
    }
}
