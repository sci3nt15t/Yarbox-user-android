package app.yarbax.com;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baoyz.actionsheet.ActionSheet;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.HashSet;
import java.util.Set;

import app.yarbax.com.Utilities.MyDb;

/**
 * Created by shayanrhm on 1/20/19.
 */

public class Select_Fav_origin extends AppCompatActivity {

    MyDb db = new MyDb();
    Activity act;
    LinearLayout root;
    PostPack newpack;

    @Override
    public void onBackPressed(){
        super.onBackPressed();

    }

    @Override
    protected void onCreate(Bundle SavedInstance)
    {
        super.onCreate(SavedInstance);
        setContentView(R.layout.select_fav_origin);
        Intent i = getIntent();
        newpack = (PostPack) i.getSerializableExtra("newpack");
        act = this;
        root = (LinearLayout)findViewById(R.id.origin_root);
        setupcells("origin");

    }

    public void setupcells(final String location){
        final Cursor crs = db.get("SELECT `title`,`city`,`address`,`number`,`id`,`plaque` FROM `fav` WHERE `location` = '"+location+"' ORDER BY `id` DESC");
        System.out.println(crs.getCount());
        root.removeAllViewsInLayout();
        if (crs.getCount() > 0) {
            while (crs.moveToNext()) {
                LinearLayout pack = new LinearLayout(act);
                LinearLayout.LayoutParams packparam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                packparam.setMargins(60, 20, 60, 0);
                pack.setLayoutParams(packparam);
                pack.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                pack.setOrientation(LinearLayout.VERTICAL);
                pack.setBackgroundDrawable(getResources().getDrawable(R.drawable.main_packbox_curve));
                pack.setPadding(0, 0, 0, 60);

                TextView title = new TextView(act);
                title.setPadding(0, 10, 30, 0);
                title.setText("عنوان : " + crs.getString(0));
                title.setTextColor(Color.BLACK);
                title.setTextSize(20);
                pack.addView(title);

                TextView address = new TextView(act);
                address.setPadding(0, 10, 30, 0);
                address.setTextColor(Color.BLACK);
                address.setTextSize(20);
                address.setTextDirection(View.TEXT_DIRECTION_RTL);
                address.setText("ادرس‌ : " + crs.getString(1) + " - " + crs.getString(2) + " - " + crs.getString(3));
                pack.addView(address);

                int address_index = crs.getColumnIndex("address");
                String street = crs.getString(address_index);
                int plaque_index = crs.getColumnIndex("plaque");
                String plaque_str = crs.getString(plaque_index);
                int phone_index = crs.getColumnIndex("number");
                String number = crs.getString(phone_index);
                final int id = crs.getInt(4);
                pack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        newpack.origin.street = street;
                        newpack.origin.plaque = plaque_str;
                        newpack.origin.senderPhoneNumber = number;
                        Intent goto_origin_view = new Intent(getApplicationContext(), SenderAddressDetail.class);
                        goto_origin_view.putExtra("newpack", newpack);
                        startActivity(goto_origin_view);
                        finish();

                    }
                });

                root.addView(pack);
            }
        }
    }
}
