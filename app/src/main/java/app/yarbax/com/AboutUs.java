package app.yarbax.com;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by shayanrhm on 1/1/19.
 */

public class AboutUs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedinstance){
        super.onCreate(savedinstance);
        setContentView(R.layout.aboutus);
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }
}
