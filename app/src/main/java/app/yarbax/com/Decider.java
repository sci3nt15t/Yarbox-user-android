package app.yarbax.com;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by shayanrhm on 1/22/19.
 */

public class Decider extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle SavedInstance)
    {
        super.onCreate(SavedInstance);
        SharedPreferences mypref = getSharedPreferences("mypref",MODE_PRIVATE);
        if (mypref.getString("token","").length() == 0)
        {
            Intent gotosign = new Intent(getApplicationContext(),Signin.class);
            gotosign.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(gotosign);
            finish();
        }else{
            Intent gotomain = new Intent(getApplicationContext(),MainActivity.class);
            gotomain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(gotomain);
            finish();
        }
    }
}
