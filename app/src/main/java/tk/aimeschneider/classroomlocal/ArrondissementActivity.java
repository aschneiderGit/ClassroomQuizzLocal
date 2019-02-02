package tk.aimeschneider.classroomlocal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import tk.aimeschneider.classroomlocal.MyElements.ButtonArrondissement;
import tk.aimeschneider.classroomcommunity.R;
import tk.aimeschneider.classroomlocal.models_only.Connection;
import tk.aimeschneider.classroomlocal.models_only.Navigation;
import tk.aimeschneider.classroomlocal.models_only.QuestionArrondissement;

import static android.support.constraint.Constraints.TAG;
import static tk.aimeschneider.classroomlocal.models_only.Connection.SERVER_KEY;
import static tk.aimeschneider.classroomlocal.models_only.Connection.WEB_QUESTION_ARRONDISSEMENT_REQUEST;


public class ArrondissementActivity extends AppCompatActivity
        implements  NavigationView.OnNavigationItemSelectedListener {

    private ActionBarDrawerToggle toggle;
    private DrawerLayout dl;
    ProgressDialog pDialog;

    int[] imagesArrondissement = new int[]{ R.drawable.img_district1,R.drawable.img_district2,R.drawable.img_district3,R.drawable.img_district4,R.drawable.img_district5,R.drawable.img_district6,
                                            R.drawable.img_district7,R.drawable.img_district8,R.drawable.img_district9,R.drawable.img_district10,R.drawable.img_district11,R.drawable.img_district12,
                                            R.drawable.img_district13,R.drawable.img_district14,R.drawable.img_district15,R.drawable.img_district16,R.drawable.img_district17,R.drawable.img_district18,
                                            R.drawable.img_district19,R.drawable.img_district20};
    ArrayList<QuestionArrondissement> questionsArond;
    private View.OnClickListener btnArrondListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            v =(ButtonArrondissement)v;
            LanchQuestion((((ButtonArrondissement) v).getQuestionArrond()));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrondissement);
        //Navigation View
        dl = (DrawerLayout) findViewById(R.id.drawerLayoutFragment);
        NavigationView nav = (NavigationView) findViewById(R.id.nvArrondissement);
        toggle = new ActionBarDrawerToggle(this, dl, R.string.open, R.string.close);
        dl.setDrawerListener(toggle);
        toggle.syncState();
        nav.setNavigationItemSelectedListener(this);
        nav.getMenu().performIdentifierAction(R.id.nav_arondissement, 0);

    }

    @Override
    protected void onStart() {
        super.onStart();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        LoadQuestions();
    }

    //Recherche des question dans la base de donnée
    private void LoadQuestions() {
        if (Connection.checkConnection(this)) {
            pDialog = new ProgressDialog(this);
            pDialog.setMessage("Getting list of arrondissement...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
            String url = WEB_QUESTION_ARRONDISSEMENT_REQUEST + "?key=" + SERVER_KEY;

            Ion.with(this)
                    .load(url)
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            pDialog.dismiss();
                            if (result == null)
                                Log.d(TAG, "No response from the server!!!");
                            else {
                                try {
                                    questionsArond = new ArrayList<QuestionArrondissement>();
                                    JSONArray questionsJson = new JSONArray(result);
                                    for (int i = 0; i < questionsJson.length(); i++) {
                                        questionsArond.add(QuestionArrondissement.JsonToQuestionArrondissement(questionsJson.getJSONObject(i)));
                                    }
                                    if (questionsArond.size() != 0) {
                                        setGridViewActivity();
                                    }
                                } catch (JSONException e1) {
                                    Toast.makeText(ArrondissementActivity.this, "Erreur lors de l'accès au question", Toast.LENGTH_SHORT).show();
                                }
                            }

                        }
                    });
        }
    }
    //Creer a gridView
    private void setGridViewActivity()
    {
        //GridView
        GridView gridview = (GridView) findViewById(R.id.gvArrondissement);
        gridview.setAdapter(new ArrondissementAdapter(this));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(ArrondissementActivity.this, "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void LanchQuestion(final QuestionArrondissement qa) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                            LaunchReponse(qa.isJuste(1),qa.getInfo());
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        LaunchReponse(qa.isJuste(0),qa.getInfo());
                        break;
                }
            }

        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(qa.getQuestion()).setPositiveButton(getResources().getString(R.string.yes), dialogClickListener)
                .setNegativeButton(getResources().getString(R.string.no), dialogClickListener).show();
    }
    private void LaunchReponse(boolean juste, String info)
    {
        String text;
        if(juste)
            text = "Bonne réponse !!!";
        else
            text = "Désolé c'est une mauvaise réponse";
        text = text + "\n\n" + info;
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(text);
        alert.setPositiveButton("Ok", null);
        alert.show();
    }

    //Navigation View
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item))
            return true;
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Navigation.onNavigationItemSelected(item,this);
        dl.closeDrawer(GravityCompat.START);
        return true;
    }

    //Arrondisement Adapter
    public class ArrondissementAdapter extends BaseAdapter {
        private Context mContext;

        public ArrondissementAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return questionsArond.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ButtonArrondissement btnView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                btnView = new ButtonArrondissement(mContext);
                FrameLayout.LayoutParams btnLp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100);
                btnView.setLayoutParams(btnLp);
                btnView.setTextColor(getResources().getColor(R.color.text_btn_arrondissement));
                btnView.setOnClickListener(btnArrondListener);

            } else {
                btnView = (ButtonArrondissement) convertView;
            }
            btnView.setQuestionArrond(questionsArond.get(position));
            if (btnView.getQuestionArrond().getArrondissement() ==1)
                btnView.setText("1er arrondissement");
            else if (btnView.getQuestionArrond().getArrondissement()  == 2)
                btnView.setText("2nd arrondissement");
            else
                btnView.setText(Integer.toString(btnView.getQuestionArrond().getArrondissement())+"ème arrondissement");
            btnView.setBackgroundResource(imagesArrondissement[btnView.getQuestionArrond().getArrondissement()-1]);
            return btnView;
        }
    }
}
