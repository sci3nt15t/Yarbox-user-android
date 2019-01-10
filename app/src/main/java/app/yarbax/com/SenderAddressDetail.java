package app.yarbax.com;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import app.yarbax.com.MyViews.GrayEditText;
import app.yarbax.com.MyViews.MyAlert;
import app.yarbax.com.Utilities.*;

/**
 * Created by shayanrhm on 12/31/18.
 */

public class SenderAddressDetail extends AppCompatActivity {

    GrayEditText address;
    GrayEditText plaque;
    GrayEditText phone;
    Button ok;
    PostPack newpack;
    Activity act;

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent go_back = new Intent(getApplicationContext(),SenderAddress.class);
        go_back.putExtra("newpack",newpack);
        startActivity(go_back);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedinstace)
    {
        super.onCreate(savedinstace);
        setContentView(R.layout.senderaddressdetail);
        act = this;
        Intent i = getIntent();
        newpack = (PostPack)i.getSerializableExtra("newpack");
        newpack.origin.plaque = "";
        newpack.origin.senderPhoneNumber = "";

        address = (GrayEditText)findViewById(R.id.sender_address);
        address.setText(newpack.origin.street);
        plaque = (GrayEditText)findViewById(R.id.sender_plaque);
        phone = (GrayEditText) findViewById(R.id.sender_phone);
        ok = (Button)findViewById(R.id.sender_ok);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (address.getText().length() != 0 && plaque.getText().toString().length() != 0 && phone.getText().length() != 0){
                    newpack.origin.street = address.getText().toString();
                    newpack.origin.plaque = plaque.getText().toString();
                    newpack.origin.senderPhoneNumber = new extension().ReplaceArabicDigitsWithEnglish(phone.getText().toString());
                    Intent goto_switch = new Intent(getApplicationContext(),ChooseReciever.class);
                    goto_switch.putExtra("newpack",newpack);
                    startActivity(goto_switch);
                    finish();
                }
                else{
                    MyAlert alert = new MyAlert(act,"خطا!","تمامی فیلد هارا پر کنید");
                }
            }
        });
    }
}
