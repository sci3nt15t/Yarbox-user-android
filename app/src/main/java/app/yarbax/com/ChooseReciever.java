package app.yarbax.com;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by shayanrhm on 12/31/18.
 */

public class ChooseReciever extends AppCompatActivity {

    PostPack newpack;

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent go_back = new Intent(getApplicationContext(),SenderAddressDetail.class);
        go_back.putExtra("newpack",newpack);
        startActivity(go_back);
        finish();
    }
    public void goback(){
        Intent go_back = new Intent(getApplicationContext(),SenderAddressDetail.class);
        go_back.putExtra("newpack",newpack);
        startActivity(go_back);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedinstace)
    {
        super.onCreate(savedinstace);
        setContentView(R.layout.choosereciever);

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
        toolbar_title.setText("انتخاب مقصد");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Intent i = getIntent();
        newpack = (PostPack) i.getSerializableExtra("newpack");
        newpack.origin.floor = "a";
        newpack.origin.alley = "a";
        newpack.destination.floor = "a";
        newpack.destination.alley = "a";
        newpack.destination.receiverTelephone = "a";
        newpack.destination.explain = "a";
        LinearLayout DoorToDoor = (LinearLayout)findViewById(R.id.frame1);
        LinearLayout Terminal = (LinearLayout)findViewById(R.id.frame2);
        DoorToDoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goto_dtd = new Intent(getApplicationContext(),RecieverAddressDetail.class);
                goto_dtd.putExtra("newpack",newpack);
                startActivity(goto_dtd);
                finish();
            }
        });
        Terminal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goto_terminal = new Intent(getApplicationContext(),ChoosePort.class);
                goto_terminal.putExtra("newpack",newpack);
                startActivity(goto_terminal);
                finish();
            }
        });

    }
}
