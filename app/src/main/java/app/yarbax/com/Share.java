package app.yarbax.com;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by shayanrhm on 1/1/19.
 */

public class Share extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedinstance){
        super.onCreate(savedinstance);
        setContentView(R.layout.share);

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
        toolbar_title.setText("به اشتراک گذاری");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ImageView btn = (ImageView)findViewById(R.id.sharebtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(android.content.Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(android.content.Intent.EXTRA_TEXT, "سلام یارباکس اپلیکیشن سریع، ساده و کم هزینه ایست که من برای ارسال خرده بار هام به شهرهای مختلف ازش استفاده می کنم. به شما هم پیشنهاد می کنم از طریق سایت http://www.yarbox.co یارباکس رو دانلود کنی");
                startActivity(i);
            }
        });
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }
    public void goback()
    {
        finish();
    }
}
