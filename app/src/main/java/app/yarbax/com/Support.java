package app.yarbax.com;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

/**
 * Created by shayanrhm on 1/18/19.
 */

public class Support extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle SavedInstance){
        super.onCreate(SavedInstance);
        setContentView(R.layout.support);

        Button faq = (Button)findViewById(R.id.faq_btn);
        faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goto_faq = new Intent(getApplicationContext(),Faq.class);
                startActivity(goto_faq);
            }
        });
        ImageButton help = (ImageButton)findViewById(R.id.help_btn);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goto_help = new Intent(getApplicationContext(),Help.class);
                startActivity(goto_help);
            }
        });
        ImageButton call = (ImageButton)findViewById(R.id.support_call);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                        Intent call = new Intent(Intent.ACTION_DIAL);
                        call.setData(Uri.parse("tel:02141196" ));
                        startActivity(call);
            }
        });
    }
}
