package app.yarbax.com;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import app.yarbax.com.MyViews.GrayEditText;
import app.yarbax.com.Utilities.MyDb;

/**
 * Created by shayanrhm on 1/21/19.
 */

public class Fav_dest_view extends AppCompatActivity {


    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent go_back = new Intent(getApplicationContext(),Fav_address.class);
        startActivity(go_back);
        finish();
    }


    GrayEditText name;
    GrayEditText address;
    GrayEditText number;
    Button delete;
    Button edit;
    MyDb db = new MyDb();
    @Override
    protected void onCreate(Bundle SavedInstance)
    {
        super.onCreate(SavedInstance);
        setContentView(R.layout.fav_dest_view);
        Intent i = getIntent();
        final int id = i.getIntExtra("id",0);
        name = (GrayEditText)findViewById(R.id.fav_dest_view_name);
        address = (GrayEditText)findViewById(R.id.fav_dest_view_address);
        number = (GrayEditText)findViewById(R.id.fav_dest_view_number);
        delete = (Button)findViewById(R.id.fav_dest_view_delete);
        edit = (Button)findViewById(R.id.fav_dest_view_edit);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.insert("DELETE FROM `fav` WHERE `id` = "+id);
                Intent go_back = new Intent(getApplicationContext(),Fav_address.class);
                startActivity(go_back);
                finish();
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goto_fav_dest = new Intent(getApplicationContext(),Fav_dest.class);
                goto_fav_dest.putExtra("id",id);
                System.out.println(id);
                startActivity(goto_fav_dest);
                finish();
            }
        });
        Cursor crs = db.get("SELECT `id`,`name`,`province`,`city`,`plaque`,`number`,`address`,`plaque` FROM `fav` WHERE `id` = "+id);
        crs.moveToFirst();
        name.setText(crs.getString(1));
        address.setText(crs.getString(2) + " " + crs.getString(3) + " " + crs.getString(6) + " " + crs.getString(7));
        number.setText(crs.getString(5));
    }
}
