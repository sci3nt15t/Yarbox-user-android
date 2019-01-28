package app.yarbax.com;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.concurrent.ExecutionException;

import app.yarbax.com.MyViews.MyAlert;
import app.yarbax.com.Utilities.Putter;

/**
 * Created by shayanrhm on 1/24/19.
 */

public class Profile extends AppCompatActivity {

    Activity act;
    SharedPreferences pref;
    String token;
    SharedPreferences.Editor pref_edit;
    public void goback(){
        finish();
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

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
        toolbar_title.setText("پروفایل");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        act = this;
        pref = getSharedPreferences("mypref",MODE_PRIVATE);
        pref_edit = getSharedPreferences("mypref",MODE_PRIVATE).edit();
        token = pref.getString("token","");
        String name = pref.getString("name","") + " " + pref.getString("last","");
        String email = pref.getString("email","");
        String number = pref.getString("phone","");

        TextView profile_name = (TextView)findViewById(R.id.profile_name);
        profile_name.setText(name);
        TextView profile_point = (TextView)findViewById(R.id.profile_point);
        profile_point.setText(pref.getInt("score",0)+"");
        TextView name_edit = (TextView)findViewById(R.id.profile_edit_name);
        name_edit.setText(name);
        TextView email_edit = (TextView)findViewById(R.id.profile_edit_email);
        email_edit.setText(email);
        TextView number_edit = (TextView)findViewById(R.id.profile_number);
        number_edit.setText(number);

        ImageView edit_name = (ImageView) findViewById(R.id.edit_name);
        edit_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder title = new AlertDialog.Builder(act);
                title.setTitle("لطفا عنوان آدرس را تعیین کنید");

                LinearLayout layout = new LinearLayout(act);
                layout.setOrientation(LinearLayout.VERTICAL);
                final EditText name = new EditText(act);
                name.setHint("نام");
                layout.addView(name);
                final EditText last = new EditText(act);
                last.setHint("نام خانوادگی");
                layout.addView(last);

                title.setView(layout);
                title.setNegativeButton("لفو", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                title.setPositiveButton("تایید", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (name.getText().length() > 0 && last.getText().length() > 0) {
                            Putter put = new Putter();
                            String reuest = "{\n" +
                                    "  \"firstName\": \""+name.getText().toString()+"\",\n" +
                                    "  \"lastName\": \""+last.getText().toString()+"\",\n" +
                                    "  \"email\": \""+email+"\",\n" +
                                    "  \"phoneNumber\": \""+number+"\"\n" +
                                    "}";
                            put.execute("http://api.yarbox.co/api/v1/profile",reuest,token);
                            try {
                                put.get();
                                System.out.println(put.mainresponse);
                                boolean issuccess =  new JSONObject(put.mainresponse).getBoolean("isSuccess");
                                if (issuccess) {
                                    pref_edit.putString("name", name.getText().toString());
                                    pref_edit.putString("last", last.getText().toString());
                                    name_edit.setText(name.getText().toString() + " " +last.getText().toString() );
                                    pref_edit.commit();
                                }else{
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            new MyAlert(act,"خطا!","خطا در دریافت!");
                                        }
                                    });
                                }
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            dialogInterface.dismiss();
                        } else {
                            new MyAlert(act, "خطا!", "لطفا عنوان را انتخاب نمایید!");
                        }

                    }
                });
                title.show();
            }
        });
        ImageView edit_email = (ImageView)findViewById(R.id.edit_email);
        edit_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder title = new AlertDialog.Builder(act);
                title.setTitle("لطفا عنوان آدرس را تعیین کنید");

                LinearLayout layout = new LinearLayout(act);
                layout.setOrientation(LinearLayout.VERTICAL);
                final EditText new_email = new EditText(act);
                new_email.setHint("ایمیل");
                layout.addView(new_email);

                title.setView(layout);
                title.setNegativeButton("لفو", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                title.setPositiveButton("تایید", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (new_email.getText().length() > 0 ) {
                            Putter put = new Putter();
                            String reuest = "{\n" +
                                    "  \"firstName\": \""+pref.getString("last","")+"\",\n" +
                                    "  \"lastName\": \""+pref.getString("last","")+"\",\n" +
                                    "  \"email\": \""+new_email.getText().toString()+"\",\n" +
                                    "  \"phoneNumber\": \""+number+"\"\n" +
                                    "}";
                            put.execute("http://api.yarbox.co/api/v1/profile",reuest,token);
                            try {
                                put.get();
                                System.out.println(put.mainresponse);
                                boolean issuccess =  new JSONObject(put.mainresponse).getBoolean("isSuccess");
                                if (issuccess) {
                                    email_edit.setText(new_email.getText().toString());
                                    pref_edit.putString("email",new_email.getText().toString());
                                    pref_edit.commit();
                                }else{
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            new MyAlert(act,"خطا!","خطا در دریافت!");
                                        }
                                    });
                                }
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            dialogInterface.dismiss();
                        } else {
                            new MyAlert(act, "خطا!", "لطفا عنوان را انتخاب نمایید!");
                        }

                    }
                });
                title.show();
            }
        });

        TextView call_support = (TextView)findViewById(R.id.profile_call_support);
        call_support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent call = new Intent(Intent.ACTION_DIAL);
                call.setData(Uri.parse("tel:02141196" ));
                startActivity(call);
            }
        });

    }
}
