package app.yarbax.com;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.suke.widget.SwitchButton;

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
    MyDb db = new MyDb();

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent go_back = new Intent(getApplicationContext(),SenderAddress.class);
        go_back.putExtra("newpack",newpack);
        startActivity(go_back);
        finish();
    }
    public void goback(){
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

        SharedPreferences pref = getSharedPreferences("mypref",MODE_PRIVATE);
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
        toolbar_title.setText("اطلاعات فرستنده");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        act = this;
        Intent i = getIntent();
        newpack = (PostPack)i.getSerializableExtra("newpack");

        address = (GrayEditText)findViewById(R.id.sender_address);
        address.setText(newpack.origin.street);
        plaque = (GrayEditText)findViewById(R.id.sender_plaque);
        plaque.setText(newpack.origin.plaque);
        phone = (GrayEditText) findViewById(R.id.sender_phone);
        if (newpack.origin.senderPhoneNumber != null)
            phone.setText(newpack.origin.senderPhoneNumber);
        else
            phone.setText(pref.getString("phone",""));
        ok = (Button)findViewById(R.id.sender_ok);

        SwitchButton add_fav = (SwitchButton)findViewById(R.id.sender_favorit);
        if (i.getBooleanExtra("ischecked",false)) {
            add_fav.setChecked(true);
            add_fav.setEnabled(false);
        }
        add_fav.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                AlertDialog.Builder title = new AlertDialog.Builder(act);
                title.setTitle("لطفا عنوان آدرس را تعیین کنید");
                final EditText text = new EditText(act);
                text.setHint("عنوان");
                title.setView(text);
                title.setNegativeButton("لفو", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        add_fav.setChecked(false);
                    }
                });
                title.setPositiveButton("تایید", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (text.getText().length() > 0) {
                            db.insert("INSERT INTO fav(`location`,`address`,`plaque`,`number`,`lat`,`lng`,`title`,`city`) " +
                                    "VALUES('origin','" + address.getText() + "','" + plaque.getText() + "','" + phone.getText() + "'," + Double.parseDouble(newpack.origin.latitude) + "," + Double.parseDouble(newpack.origin.longitude) + ",'" + text.getText() + "', 'تهران' )");
                            dialogInterface.dismiss();
                            add_fav.setEnabled(false);
                            add_fav.setEnableEffect(false);
                        } else {
                            new MyAlert(act, "خطا!", "لطفا عنوان را انتخاب نمایید!");
                        }

                    }
                });
                title.show();
            }
        });

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
