package app.yarbax.com;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.stream.Stream;

import app.yarbax.com.MyViews.MyAlert;
import app.yarbax.com.R;
import app.yarbax.com.Utilities.MyDb;

/**
 * Created by shayanrhm on 1/20/19.
 */

public class Fav_origin_detail extends AppCompatActivity {

    Double lat;
    Double lng;
    MyDb db = new MyDb();
    Activity act;
    int id = 0;

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent goto_fav_origin_map = new Intent(act,Fav_origin_map.class);
        if (id != 0)
            goto_fav_origin_map.putExtra("id",id);
        startActivity(goto_fav_origin_map);
        finish();
    }
    @Override
    protected void onCreate(Bundle SavedInstance)
    {
        super.onCreate(SavedInstance);
        setContentView(R.layout.fav_origin_detail);
        act = this;
        Intent i = getIntent();
        id = i.getIntExtra("id",0);
        lat = i.getDoubleExtra("lat",0.0);
        lng = i.getDoubleExtra("lng",0.0);
        final EditText address = (EditText)findViewById(R.id.fav_address);
        final EditText plaque = (EditText)findViewById(R.id.fav_origin_plaque);
        final EditText number = (EditText)findViewById(R.id.fav_origin_phone);
        if (id != 0)
        {
            Cursor crs = db.get("SELECT * FROM `fav` WHERE `id` = "+id);
            crs.moveToFirst();
            int address_index = crs.getColumnIndex("address");
            address.setText(crs.getString(address_index));
            int plaque_index = crs.getColumnIndex("plaque");
            plaque.setText(crs.getString(plaque_index));
            int number_index = crs.getColumnIndex("number");
            number.setText(crs.getString(number_index));
        }


        Button ok = (Button)findViewById(R.id.fav_origin_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lat != 0.0 && lng != 0.0 && address.getText().length() > 0 && plaque.getText().length() > 0 && number.getText().length() > 0)
                {
                    if (id == 0) {
                        AlertDialog.Builder title = new AlertDialog.Builder(act);
                        title.setTitle("لطفا عنوان ادرس را تعیین کنید");
                        final EditText text = new EditText(act);
                        text.setHint("عنوان");
                        title.setView(text);
                        title.setNegativeButton("لفو", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        title.setPositiveButton("تایید", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (text.getText().length() > 0) {
                                    db.insert("INSERT INTO fav(`location`,`address`,`plaque`,`number`,`lat`,`lng`,`title`,`city`) " +
                                            "VALUES('origin','" + address.getText() + "','" + plaque.getText() + "','" + number.getText() + "'," + lat + "," + lng + ",'" + text.getText() + "', 'تهران' )");
                                    Intent go_back = new Intent(act, Fav_address.class);
                                    startActivity(go_back);
                                    finish();
                                } else {
                                    new MyAlert(act, "خطا!", "لطفا عنوان را انتخاب نمایید!");
                                }

                            }
                        });
                        title.show();
                    }else{
                        db.insert("UPDATE `fav` SET `address` = '"+address.getText()+"',`plaque` = '"+plaque.getText()+"',`number`='"+number.getText()+"'," +
                                "`lat` = "+lat+",`lng` = "+lng+" WHERE `id` = "+id);
                        Intent go_back = new Intent(act, Fav_address.class);
                        startActivity(go_back);
                        finish();
                    }
                }else{
                    new MyAlert(act,"خطا!","لطفا تمامی فیلد ها را پر نمایید!");
                }
            }
        });
    }
}
