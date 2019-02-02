package tk.aimeschneider.classroomlocal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import tk.aimeschneider.classroomcommunity.R;
import tk.aimeschneider.classroomlocal.models_only.Connection;
import tk.aimeschneider.classroomlocal.models_only.Friend;
import tk.aimeschneider.classroomlocal.models_only.Navigation;

import static android.support.constraint.Constraints.TAG;
import static tk.aimeschneider.classroomlocal.models_only.Connection.LOCALHOST;
import static tk.aimeschneider.classroomlocal.models_only.Connection.SERVER_KEY;
import static tk.aimeschneider.classroomlocal.models_only.Connection.WEB_FRIENDS_REQUEST;

public class FriendActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private DrawerLayout dl;
    private ActionBarDrawerToggle toggle;
    ProgressDialog pDialog;

    private ArrayList<Friend> myFriends;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        //Navigation View
        dl = (DrawerLayout) findViewById(R.id.drawerLayoutFragment);
        NavigationView nav = (NavigationView) findViewById(R.id.nvFriend);
        toggle = new ActionBarDrawerToggle(this, dl, R.string.open, R.string.close);
        dl.setDrawerListener(toggle);
        toggle.syncState();
        nav.setNavigationItemSelectedListener(this);
        nav.getMenu().performIdentifierAction(R.id.nav_friend, 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        LoadFriends();
    }

    private void LoadFriends() {
        if (Connection.checkConnection(this)) {
            pDialog = new ProgressDialog(this);
            pDialog.setMessage("Getting list of friends...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
            String url = WEB_FRIENDS_REQUEST+ "?key=" + SERVER_KEY;

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
                                    myFriends = new ArrayList<Friend>();
                                    JSONArray amisJson = new JSONArray(result);
                                    for (int i = 0; i < amisJson.length(); i++) {
                                        myFriends.add(Friend.JsonToFriend(amisJson.getJSONObject(i)));
                                    }
                                    if (myFriends.size() != 0) {
                                        setGridViewFriends();
                                    }
                                } catch (JSONException e1) {
                                    Toast.makeText(FriendActivity.this, "Erreur lors de l'accès aux amis", Toast.LENGTH_SHORT).show();
                                }
                            }

                        }
                    });
        }
    }

    private void setGridViewFriends()
    {
        //GridView
        GridView gridview = (GridView) findViewById(R.id.gvFriends);
        gridview.setAdapter(new FriendActivity.FriendAdapter(this));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(FriendActivity.this, "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Navigation View
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

    //Friend Adapter
    public class FriendAdapter extends BaseAdapter {
        private Context mContext;

        public FriendAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return myFriends.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                view = inflater.inflate(R.layout.friend_square, null);

            } else {
                view = (View) convertView;
            }


            for(int index=0; index<((ViewGroup)view).getChildCount(); ++index)
            {
                View nextChild = ((ViewGroup)view).getChildAt(index);
                for(int subIndex=0; subIndex<((ViewGroup)nextChild).getChildCount(); ++subIndex)
                {
                    View subNextChild = ((ViewGroup)nextChild).getChildAt(subIndex);
                    switch (subNextChild.getId())
                    {
                        case R.id.imgAvatarFriends: {
                            ImageView img = (ImageView) subNextChild;
                            //img.setImageURI(Uri.parse("https://media.wired.com/photos/5a99c6108e71085604e0271b/master/pass/google_diversity_lawsuit-FINAL.jpg"))
                            if (!myFriends.get(position).getPhoto_path().isEmpty())
                                Picasso.with(img.getContext()).load(Connection.HTTP + LOCALHOST + "/" + myFriends.get(position).getPhoto_path()).into(img);
                            break;
                        }
                        case R.id.imgConnectFriend:
                        {
                            if ((myFriends.get(position).isPresent()))
                                subNextChild.setBackgroundResource(R.color.greenFluo);
                            else
                                subNextChild.setBackgroundResource(R.color.redLight);
                            break;
                        }
                        case  R.id.txtLastSoreFriend:
                        {
                            TextView txt =(TextView)subNextChild;
                            txt.setText((getResources().getString(R.string.last_score) + Integer.toString(   myFriends.get(position).getLastScore())));
                            break;
                        }
                        case R.id.rlNomPrenomFriends:
                        {
                            for(int subSubIndex=0; subSubIndex<((ViewGroup)subNextChild).getChildCount(); ++subSubIndex)
                            {
                                View subSubView = ((ViewGroup)subNextChild).getChildAt(subSubIndex);
                                switch (subSubView.getId()) {
                                    case R.id.txtPrenomFriend: {
                                        TextView txt = (TextView) subSubView;
                                        txt.setText(myFriends.get(position).getPrenom());
                                        break;
                                    }
                                    case R.id.txtNomFriend: {
                                        TextView txt = (TextView) subSubView;
                                        txt.setText(myFriends.get(position).getName());
                                        break;
                                    }
                                }
                            }
                        }
                    }

                }
            }

            return view;
        }
    }

    //pour charger l'image des amis
    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is,"");
            return d;
        } catch (Exception e) {
            return null;
        }
    }
}
