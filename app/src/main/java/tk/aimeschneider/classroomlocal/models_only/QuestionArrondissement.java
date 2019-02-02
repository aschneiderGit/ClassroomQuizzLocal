package tk.aimeschneider.classroomlocal.models_only;


import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;


public class QuestionArrondissement {

    private static String JSON_QUESTION = "text";
    private static String JSON_REPONSE = "reponse";
    private static String JSON_INFO= "info";
    private static String JSON_ARRONDISSEMENT= "arrondissement";

    private String question;
    private int reponse;
    private String info;
    private int arrondissement;

    public QuestionArrondissement(String q, int r, String i, int a)
    {
        question = q;
        reponse = r;
        info = i;
        arrondissement = a;
    }

    public String getQuestion()
    {
        return question;
    }
    public String getInfo() { return info;}
    public int getArrondissement() {return arrondissement;}
    public boolean isJuste(int r)
    {
        return (r == reponse);
    }



    public static QuestionArrondissement JsonToQuestionArrondissement(JSONObject json) throws JSONException {
        String question = json.get(JSON_QUESTION).toString();
        int reponse = json.getInt(JSON_REPONSE);
        String info = json.get(JSON_INFO).toString();
        int arond = json.getInt(JSON_ARRONDISSEMENT);
        return new QuestionArrondissement(question,reponse,info,arond);
    }
}
