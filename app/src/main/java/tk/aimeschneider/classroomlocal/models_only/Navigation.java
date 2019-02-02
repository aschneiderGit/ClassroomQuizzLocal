package tk.aimeschneider.classroomlocal.models_only;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import tk.aimeschneider.classroomlocal.ArrondissementActivity;
import tk.aimeschneider.classroomlocal.FriendActivity;
import tk.aimeschneider.classroomlocal.MainActivity;
import tk.aimeschneider.classroomlocal.QuizzActivity;
import tk.aimeschneider.classroomcommunity.R;

public class Navigation {


    public static void onNavigationItemSelected(MenuItem item, Context ctx)
    {

        switch (item.getItemId())
        {
            case R.id.nav_home:
            {
                Intent intent = new Intent(ctx, MainActivity.class);
                ctx.startActivity(intent);
                break;
            }
            case R.id.nav_quizz:
            {
                if(ctx.getClass() != QuizzActivity.class)
                {
                    Intent intent = new Intent(ctx, QuizzActivity.class);
                    ctx.startActivity(intent);
                }
                break;
            }
            case R.id.nav_arondissement:
            {
                if(ctx.getClass() != ArrondissementActivity.class)
                {
                    Intent intent = new Intent(ctx, ArrondissementActivity.class);
                    ctx.startActivity(intent);
                }
                break;
            }
            case R.id.nav_friend:
            {
                if (ctx.getClass() != FriendActivity.class)
                {
                    Intent intent = new Intent(ctx, FriendActivity.class);
                    ctx.startActivity(intent);
                }
                break;
            }
        }
    }
}
