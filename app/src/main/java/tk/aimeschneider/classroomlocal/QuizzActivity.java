package tk.aimeschneider.classroomlocal;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

import tk.aimeschneider.classroomcommunity.R;
import tk.aimeschneider.classroomlocal.models_only.Connection;
import tk.aimeschneider.classroomlocal.models_only.Navigation;
import tk.aimeschneider.classroomlocal.models_only.Question;

import static android.support.constraint.Constraints.TAG;
import static java.lang.Thread.sleep;
import static tk.aimeschneider.classroomlocal.models_only.Connection.SERVER_KEY;
import static tk.aimeschneider.classroomlocal.models_only.Connection.WEB_QUESTION_REQUEST;


public class QuizzActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {




    private int index_question = 0;
    private ArrayList<Question> questions;
    private ProgressBar timebar;
    private TextView txtTpsRestant;
    private RelativeLayout rlQuizz;
    private TextView txtQuestion;
    private ProgressTime pt;
    private ArrayList<Button> btnReponses = new ArrayList<Button>();
    private ActionBarDrawerToggle toggle;
    private DrawerLayout dl;
    private ProgressDialog pDialog;

    private View.OnClickListener VerifReponseListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            VerifierReponse((Button)v);
        }
    };

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizz);
        txtQuestion =  findViewById(R.id.txtQuestion);
        rlQuizz = findViewById(R.id.LayoutQuestion);
        dl =  findViewById(R.id.drawerLayoutFragment);
        NavigationView nav = findViewById(R.id.nvQuizz);
        toggle = new ActionBarDrawerToggle(this,dl,R.string.open,R.string.close);
        dl.setDrawerListener(toggle);
        toggle.syncState();
        nav.setNavigationItemSelectedListener(this);
        nav.getMenu().performIdentifierAction(R.id.nav_quizz,0);

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //creer les questions
        LoadQuestions();

    }
    //NAVIGATION VIEW
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (toggle.onOptionsItemSelected(item))
            return true;
        return false;
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        Navigation.onNavigationItemSelected(item,this);
        dl.closeDrawer(GravityCompat.START);
        return true;
    }

    private void LoadQuestions()
    {
        if (Connection.checkConnection(this)) {
            pDialog = new ProgressDialog(this);
            pDialog.setMessage("Getting list of questions...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
            String url = WEB_QUESTION_REQUEST + "?key=" + SERVER_KEY;

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
                                try
                                {
                                    JSONArray questionsJson = new JSONArray(result);
                                    questions = new ArrayList<Question>();
                                    for (int i =0 ; i<questionsJson.length();i++)
                                    {
                                        questions.add(Question.JsonToQuestion( questionsJson.getJSONObject(i)));
                                    }
                                    if ( questions.size() != 0)
                                    {
                                        timebar = (ProgressBar) findViewById(R.id.timebar);
                                        txtTpsRestant = (TextView) findViewById(R.id.txtIntTpsRestant);
                                        SetQuizz();
                                    }
                                } catch (JSONException e1) {
                                    Toast.makeText(QuizzActivity.this, "Erreur lors de l'accès au question", Toast.LENGTH_SHORT).show();
                                }
                            }

                        }
                    });
        }
}

    private void SetQuizz()
    {
        ArrayList<RelativeLayout> rlDelete = new ArrayList<>();
        for(int i =0;i< rlQuizz.getChildCount();i++)
        {
            try
            {
                RelativeLayout rl = (RelativeLayout) rlQuizz.getChildAt(i);
                rlDelete.add(rl);
            }
            catch (Exception e)
            {

            }
        }
        for(RelativeLayout rl : rlDelete)
        {
            rlQuizz.removeView(rl);
        }
        btnReponses.clear();
        txtQuestion.setText(questions.get(index_question).getQuestion());
        HashMap<String,Integer> reponses =  questions.get(index_question).getReponses();
        HashMap<String,Integer> tmpreponses = (HashMap<String, Integer>) questions.get(index_question).getReponses().clone();
        SetBtnReponse(reponses);
        int i = 0;
        for (String key : questions.get(index_question).getReponses().keySet())
        {
            int index_reponse = (int)((Math.random())*  (tmpreponses.size()-1));
            btnReponses.get(i).setText(key);
            tmpreponses.remove(index_reponse);
            i++;
        }
        timebar.setMax(questions.get(index_question).getDuration());
        pt = new ProgressTime();
        pt.execute();

    }

    private void SetBtnReponse(HashMap<String,Integer> reponses)
    {
        for(int i = 0; i< reponses.size(); i++)
        {
            RelativeLayout rlReponse = new RelativeLayout(rlQuizz.getContext());
            Button btnReponse = new Button(rlReponse.getContext());
            btnReponse.setBackgroundResource(R.drawable.border);
            FrameLayout.LayoutParams btnLp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.WRAP_CONTENT);
            btnLp.setMargins(100,35,100,0);
            btnReponse.setLayoutParams(btnLp);
            btnReponse.setPadding(0,25,0,25);
            btnReponse.setOnClickListener(VerifReponseListener);
            btnReponse.setId(View.generateViewId());
            btnReponses.add(btnReponse);
            rlReponse.addView(btnReponse);
            rlReponse.setId(View.generateViewId());
            rlQuizz.addView(rlReponse);
            RelativeLayout.LayoutParams rlLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
            rlLp.setMargins(0,5,0,0);
            if(i ==0)
                rlLp.addRule(RelativeLayout.BELOW, txtQuestion.getId());
            else
            {
                rlLp.addRule(RelativeLayout.BELOW, ((View)btnReponses.get(i - 1).getParent()).getId());
            }
            rlReponse.setLayoutParams(rlLp);
        }
    }

    private  void VerifierReponse(Button b)
    {
        if (questions.get(index_question).IsBonneReponse(b.getText().toString()))
        {
            b.setBackgroundResource(R.drawable.border_true);
            try {
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            pt.cancel(true);
            pt = new ProgressTime();
            pt.execute();
            index_question++;
            if (index_question == questions.size())
                index_question = 0;
            SetQuizz();
        }
        else
        {
            b.setBackgroundResource(R.drawable.border_false);
        }

    }

    private class ProgressTime extends AsyncTask<Void, Integer, Void>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values){
            super.onProgressUpdate(values);
            // Mise à jour de la ProgressBar
            timebar.setProgress(values[0]);
            // changement du temps restant
            txtTpsRestant.setText(String.valueOf(timebar.getMax() - values[0]));
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            int progress = 0;
            while (timebar.getProgress() < timebar.getMax()) {
                if(isCancelled())
                {
                    break;
                }
                try {
                    Thread.sleep(100);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                progress++;
                if(progress%10 == 0)
                    publishProgress(progress/10);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        }
    }


}

///Animation ProgressBar
    /*
    mProgressBar=findViewById(R.id.progressBar);
        mProgressBar.setMax(1000);
        ObjectAnimator animation = ObjectAnimator.ofInt(mProgressBar, "progress", 0, 1000);
        animation.setDuration(30000);
        animation.setInterpolator(new LinearInterpolator());
        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) { }

            @Override
            public void onAnimationEnd(Animator animator) {
                Toast t = Toast.makeText(QuizActivity.this,"Perdu", Toast.LENGTH_LONG);
                t.setGravity(Gravity.CENTER,0,0);
                t.show();
                Intent intent = new Intent(QuizActivity.this, LoginActivity.class);
                startActivity(intent);
            }

            @Override
            public void onAnimationCancel(Animator animator) { }

            @Override
            public void onAnimationRepeat(Animator animator) { }
        });
        animation.start();
     */

    //IMAGE devenu obsolète
    /*private ArrayList<ArrayList<String>> questions =new ArrayList<>   (Arrays.asList(
            new ArrayList<>   (Arrays.asList("0","Quel est la capitale de la France ?", "Paris","Strasbourg","Berlin", "Budapest", "New York", "Sidney", "Agadir")),
            new ArrayList<>   (Arrays.asList("0","Choisis le bon mot", "Manala","Menele","Jean-bonhomme","Manela")),
            new ArrayList<>   (Arrays.asList("1","Où se trouve le rond rouge ?", Integer.toString(R.drawable.circle_red), Integer.toString(R.drawable.circle_green),Integer.toString(R.drawable.triangle_green),Integer.toString(R.drawable.square_red))),
            new ArrayList<>   (Arrays.asList("0","Combien de jours au mois d'août ?", "31","30","30.5","32")) ));
    private final int INDEX_Q = 1;
    private final int INDEX_R1 = 2;
    private final int INDEX_PICTURE = 0;

    dans setQuizz

            ArrayList<String> tmpQuestions = (ArrayList<String>) questions.get(index_question).clone();
        tmpQuestions.remove(INDEX_Q);
        tmpQuestions.remove(INDEX_PICTURE);
        if(questions.get(index_question).get(0) == "0")
        {
            SetBtnReponse(tmpQuestions);
            for (int i = 0; i< btnReponses.size(); i++)
            {
                int index_reponse = (int)((Math.random())*  (tmpQuestions.size()-1));
                btnReponses.get(i).setText(tmpQuestions.get(index_reponse));
                tmpQuestions.remove(index_reponse);
            }
        }
        else
        {
            SetBtnImage(tmpQuestions);
            for (int i = 0; i< btnReponses.size(); i++)
            {
                int index_reponse = (int)((Math.random())*  (tmpQuestions.size()-1));
                btnReponses.get(i).setBackgroundResource(Integer.parseInt(tmpQuestions.get(index_reponse)));
                btnReponses.get(i).setHint(tmpQuestions.get(index_reponse));
                tmpQuestions.remove(index_reponse);
            }
        }





    private void SetBtnImage(ArrayList<String> tmpQuestions)
    {
        RelativeLayout rlReponse = null;
        RelativeLayout rlEmpty = null;
        for(int i =0;i<tmpQuestions.size();i++)
        {
            RelativeLayout.LayoutParams  btnLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT ,RelativeLayout.LayoutParams.WRAP_CONTENT);
            btnLp.setMargins(10,10,0,0);
            RelativeLayout.LayoutParams rlLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            rlLp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
            if (i%2 ==0) {
                rlReponse = new RelativeLayout(rlQuizz.getContext());
                rlReponse.setId(View.generateViewId());
                rlEmpty = new RelativeLayout(rlReponse.getContext());
                rlEmpty.setId(View.generateViewId());
                rlEmpty.setLayoutParams(rlLp);
                rlReponse.addView(rlEmpty);
                btnLp.addRule(RelativeLayout.LEFT_OF, rlEmpty.getId());

            }
            else
            {
                btnLp.addRule(RelativeLayout.RIGHT_OF, rlEmpty.getId());
            }
            Button btnReponse = new Button(rlReponse.getContext());
            btnReponse.setLayoutParams(btnLp);
            btnReponse.setOnClickListener(VerifReponseImageListener);
            btnReponse.setId(View.generateViewId());
            btnReponse.setText("   ");
            btnReponses.add(btnReponse);
            rlReponse.addView(btnReponse);
            if (i==1) {
                rlLp.addRule(RelativeLayout.BELOW, txtQuestion.getId());
                rlLp.setMargins(0, 5, 0, 0);
            }

            if(i%2==1) {
                rlQuizz.addView(rlReponse);
               //
                 if (i%2==1 && i>=2) {
                    rlLp.addRule(RelativeLayout.BELOW, ((View) btnReponses.get(i - 2).getParent()).getId());

                }
            }
            rlReponse.setLayoutParams(rlLp);
        }
    }

        private void VerifierReponseImage(Button b){
        int btnHeight = b.getHeight();
        if (b.getHint().equals(questions.get(index_question).get(INDEX_R1)))
        {
            b.setBackgroundResource(R.drawable.border_true);
            try {
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            pt.cancel(true);
            pt = new ProgressTime();
            pt.execute();
            index_question++;
            SetQuizz();
        }
        else
        {
            b.setBackgroundResource(R.drawable.border_false);
        }
        b.setHeight(btnHeight);

    }*/