package app.yarbax.com;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by shayanrhm on 1/1/19.
 */

public class Share extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedinstance){
        super.onCreate(savedinstance);
        setContentView(R.layout.share);
        ImageView btn = (ImageView)findViewById(R.id.sharebtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(android.content.Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(android.content.Intent.EXTRA_TEXT, "http://yarbox.co/");
                startActivity(i);
            }
        });
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }
}
