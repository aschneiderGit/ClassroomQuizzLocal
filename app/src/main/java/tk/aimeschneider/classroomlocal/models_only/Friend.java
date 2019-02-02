package tk.aimeschneider.classroomlocal.models_only;

import org.json.JSONException;
import org.json.JSONObject;

public class Friend {

    static private String JSON_PRENOM = "first_name";
    static private String JSON_NOM = "last_name";
    static private String JSON_PRESENT = "is_present";
    static private String JSON_PHOTO_PATH = "photo_path";
    static private String JSON_LAST_SCORE = "last_score";

    private String prenom;
    private String name;
    private int present;
    private String photoPath;
    private int lastScore;

    public Friend(String prenom, String name, int present, String photo_path, int lastScore) {
        this.prenom = prenom;
        this.name = name;
        this.present = present;
        this.photoPath = photo_path;
        this.lastScore = lastScore;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getName() {
        return name;
    }

    public boolean isPresent() {
        return (present==1);
    }

    public String getPhoto_path() {
        return photoPath;
    }


    public int getLastScore() {
        return lastScore;
    }

    public static Friend JsonToFriend(JSONObject json) throws JSONException {

        String prenom = json.getString(JSON_PRENOM);
        String nom = json.getString(JSON_NOM);
        int present = json.getInt(JSON_PRESENT);
        String photo = json.getString(JSON_PHOTO_PATH);
        int lastScore = json.getInt(JSON_LAST_SCORE);
        return new Friend(prenom, nom,present,photo,lastScore);
    }
}
