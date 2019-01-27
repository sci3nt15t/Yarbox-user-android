package app.yarbax.com;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import app.yarbax.com.Utilities.MyDb;

/**
 * Created by shayanrhm on 1/20/19.
 */

public class Select_Fav_dest extends AppCompatActivity {

    MyDb db = new MyDb();
    Activity act;
    LinearLayout root;
    PostPack newpack;

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent go_back = new Intent(getApplicationContext(),RecieverAddressDetail.class);
        go_back.putExtra("newpack",newpack);
        startActivity(go_back);
        finish();

    }
    public void goback(){
        Intent go_back = new Intent(getApplicationContext(),RecieverAddressDetail.class);
        go_back.putExtra("newpack",newpack);
        startActivity(go_back);
        finish();
    }

    @Override
    protected void onCreate(Bundle SavedInstance)
    {
        super.onCreate(SavedInstance);
        setContentView(R.layout.select_fav_dest);

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
        toolbar_title.setText("انتخاب آدرس متخب");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Intent i = getIntent();
        newpack = (PostPack) i.getSerializableExtra("newpack");
        act = this;
        root = (LinearLayout)findViewById(R.id.dest_root);
        setupcells("dest");

    }

    public void setupcells(final String location){
        final Cursor crs = db.get("SELECT `title`,`city`,`address`,`number`,`id`,`plaque`,`province`,`name` FROM `fav` WHERE `location` = '"+location+"' ORDER BY `id` DESC");
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
                title.setTextColor(Color.DKGRAY);
                title.setTextSize(20);
                pack.addView(title);

                TextView address = new TextView(act);
                address.setPadding(0, 10, 30, 0);
                address.setTextColor(Color.DKGRAY);
                address.setTextSize(20);
                address.setTextDirection(View.TEXT_DIRECTION_RTL);
                address.setText("آدرس‌ : " + crs.getString(1) + " - " + crs.getString(2) + " - " + crs.getString(3));
                pack.addView(address);

                int address_index = crs.getColumnIndex("address");
                String street = crs.getString(address_index);
                int plaque_index = crs.getColumnIndex("plaque");
                String plaque_str = crs.getString(plaque_index);
                int phone_index = crs.getColumnIndex("number");
                String number = crs.getString(phone_index);
                int name_index = crs.getColumnIndex("name");
                String name_str = crs.getString(name_index);
                int province_index = crs.getColumnIndex("province");
                String province_str = crs.getString(province_index);
                int city_index = crs.getColumnIndex("city");
                String city = crs.getString(city_index);

                final int id = crs.getInt(4);
                pack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        newpack.destination.street = street;
                        newpack.destination.plaque = plaque_str;
                        newpack.destination.receiverPhoneNumber = number;
                        newpack.destination.province = province_str;
                        newpack.destination.city = city;
                        newpack.destination.receiverName = name_str;
                        Intent goto_origin_view = new Intent(getApplicationContext(), RecieverAddressDetail.class);
                        goto_origin_view.putExtra("newpack", newpack);
                        goto_origin_view.putExtra("ischecked",true);
                        startActivity(goto_origin_view);
                        finish();

                    }
                });

                root.addView(pack);
            }
        }
    }
}
