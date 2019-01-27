package app.yarbax.com;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by shayanrhm on 1/1/19.
 */

public class AboutUs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedinstance){
        super.onCreate(savedinstance);
        setContentView(R.layout.aboutus);

        android.support.v7.widget.Toolbar tool = (android.support.v7.widget.Toolbar)findViewById(R.id.my_toolbar);
        tool.setNavigationIcon(getResources().getDrawable(R.mipmap.back));
        TextView toolbar_title = (TextView)findViewById(R.id.toolbar_title);
        setSupportActionBar(tool);
        tool.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("clicked!");
                goback();
            }
        });
        toolbar_title.setText("درباره ی ما");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }
    public void goback(){
        finish();
    }
}
