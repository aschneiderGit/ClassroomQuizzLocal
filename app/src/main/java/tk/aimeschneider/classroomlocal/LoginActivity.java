package tk.aimeschneider.classroomlocal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import tk.aimeschneider.classroomcommunity.R;
import tk.aimeschneider.classroomlocal.models_only.Connection;

import static android.support.constraint.Constraints.TAG;
import static tk.aimeschneider.classroomlocal.models_only.Connection.SERVER_KEY;
import static tk.aimeschneider.classroomlocal.models_only.Connection.WEB_CONNECTION_REQUEST;

public class LoginActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    private EditText edtLogin;
    private EditText edtMdp;
    private Spinner spServer;
    private Button loginButton;
    private TextView edtServer;

    private View.OnClickListener btnLoginListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v) {
            pDialog = new ProgressDialog(loginButton.getContext());
            pDialog.setMessage("Connection...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
            try {
                Connection.SetLocalhost(edtServer.getText().toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            String url = WEB_CONNECTION_REQUEST + "?key=" + SERVER_KEY + "&login=" + edtLogin.getText().toString();
            Ion.with(loginButton.getContext())
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
                                    JSONArray jsonUser = new JSONArray(result);
                                    if (Connection.isConnection(edtMdp.getText().toString(), jsonUser.getJSONObject(0))) {
                                        Connection.pseudo = edtLogin.getText().toString();
                                        Connection.addLocalhostTxt();
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    } else
                                        Toast.makeText(LoginActivity.this, "Mauvais login ou mdp", Toast.LENGTH_SHORT).show();
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    });
        }
    };

    private AdapterView.OnItemSelectedListener svSpinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            edtServer.setText(spServer.getItemAtPosition(position).toString());
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edtLogin = (EditText) findViewById(R.id.edtLogin);
        edtMdp = (EditText) findViewById(R.id.edtMdp);
        spServer = (Spinner) findViewById(R.id.spServer);
        edtServer = (EditText) findViewById(R.id.edtServer);
        spServer.setOnItemSelectedListener(svSpinnerListener);
        try {
            Connection.CreateDirectory();
            Connection.ReadLocalhost();
            // set the spinner data programmatically, from a string array or list
            ArrayAdapter<String> adapter = new ArrayAdapter<String>( this, android.R.layout.simple_spinner_item, Connection.ARRAY_LOCALHOST);
            spServer.setAdapter(adapter);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        loginButton = (Button) findViewById(R.id.btnLogin);
        loginButton.setOnClickListener(btnLoginListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}
