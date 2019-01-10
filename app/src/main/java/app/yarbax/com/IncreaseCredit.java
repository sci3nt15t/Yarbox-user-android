package app.yarbax.com;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.inputmethodservice.Keyboard;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import app.yarbax.com.MyViews.GrayEditText;
import app.yarbax.com.MyViews.MyAlert;
import app.yarbax.com.Utilities.Getter;

/**
 * Created by shayanrhm on 1/2/19.
 */

public class IncreaseCredit extends AppCompatActivity {

    GrayEditText your_cred;
    TextView bist;
    TextView chel;
    TextView shast;
    TextView hashtad;
    TextView sad;
    TextView sadobist;
    GrayEditText price;
    Button pay;
    int money = 0;
    Activity act;
    Executor exec = Executors.newFixedThreadPool(2);
    String token;
    boolean isOpened = false;


    @Override
    protected void onCreate(Bundle savedinstance)
    {
        super.onCreate(savedinstance);
        setContentView(R.layout.increasecredit);
        SharedPreferences p = getSharedPreferences("mypref",MODE_PRIVATE);
        token = p.getString("token","");
        act = this;
        your_cred = (GrayEditText)findViewById(R.id.your_cred);
        your_cred.setText( " اعتبار شما : " + p.getInt("credit",0) + " تومان ");
        bist = (TextView)findViewById(R.id.twenty);
        bist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                money = 20000;
                price.setText(String.format("%,.0f",(double)money) + " تومان ");
                bist.setBackground(getResources().getDrawable(R.drawable.red_cred_view));
                chel.setBackground(getResources().getDrawable(R.drawable.gray_cred_view));
                shast.setBackground(getResources().getDrawable(R.drawable.gray_cred_view));
                hashtad.setBackground(getResources().getDrawable(R.drawable.gray_cred_view));
                sad.setBackground(getResources().getDrawable(R.drawable.gray_cred_view));
                sadobist.setBackground(getResources().getDrawable(R.drawable.gray_cred_view));
            }
        });

        chel = (TextView)findViewById(R.id.fourty);
        chel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                money = 40000;
                price.setText(String.format("%,.0f",(double) money) + " تومان ");
                bist.setBackground(getResources().getDrawable(R.drawable.gray_cred_view));
                chel.setBackground(getResources().getDrawable(R.drawable.red_cred_view));
                shast.setBackground(getResources().getDrawable(R.drawable.gray_cred_view));
                hashtad.setBackground(getResources().getDrawable(R.drawable.gray_cred_view));
                sad.setBackground(getResources().getDrawable(R.drawable.gray_cred_view));
                sadobist.setBackground(getResources().getDrawable(R.drawable.gray_cred_view));
            }
        });
        shast = (TextView)findViewById(R.id.sixty);
        shast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                money = 60000;
                price.setText(String.format("%,.0f", (double)money) + " تومان ");
                bist.setBackground(getResources().getDrawable(R.drawable.gray_cred_view));
                chel.setBackground(getResources().getDrawable(R.drawable.gray_cred_view));
                shast.setBackground(getResources().getDrawable(R.drawable.red_cred_view));
                hashtad.setBackground(getResources().getDrawable(R.drawable.gray_cred_view));
                sad.setBackground(getResources().getDrawable(R.drawable.gray_cred_view));
                sadobist.setBackground(getResources().getDrawable(R.drawable.gray_cred_view));
            }
        });
        hashtad = (TextView)findViewById(R.id.hashtad);
        hashtad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                money = 80000;
                price.setText(String.format("%,.0f", (double)money) + " تومان ");
                bist.setBackground(getResources().getDrawable(R.drawable.gray_cred_view));
                chel.setBackground(getResources().getDrawable(R.drawable.gray_cred_view));
                shast.setBackground(getResources().getDrawable(R.drawable.gray_cred_view));
                hashtad.setBackground(getResources().getDrawable(R.drawable.red_cred_view));
                sad.setBackground(getResources().getDrawable(R.drawable.gray_cred_view));
                sadobist.setBackground(getResources().getDrawable(R.drawable.gray_cred_view));
            }
        });
        sad = (TextView)findViewById(R.id.sad);
        sad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                money = 100000;
                price.setText(String.format("%,.0f",(double) money) + " تومان ");
                bist.setBackground(getResources().getDrawable(R.drawable.gray_cred_view));
                chel.setBackground(getResources().getDrawable(R.drawable.gray_cred_view));
                shast.setBackground(getResources().getDrawable(R.drawable.gray_cred_view));
                hashtad.setBackground(getResources().getDrawable(R.drawable.gray_cred_view));
                sad.setBackground(getResources().getDrawable(R.drawable.red_cred_view));
                sadobist.setBackground(getResources().getDrawable(R.drawable.gray_cred_view));
            }
        });
        sadobist = (TextView)findViewById(R.id.sadobist);
        sadobist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                money = 120000;
                price.setText(String.format("%,.0f",(double) money) + " تومان ");
                bist.setBackground(getResources().getDrawable(R.drawable.gray_cred_view));
                chel.setBackground(getResources().getDrawable(R.drawable.gray_cred_view));
                shast.setBackground(getResources().getDrawable(R.drawable.gray_cred_view));
                hashtad.setBackground(getResources().getDrawable(R.drawable.gray_cred_view));
                sad.setBackground(getResources().getDrawable(R.drawable.gray_cred_view));
                sadobist.setBackground(getResources().getDrawable(R.drawable.red_cred_view));
            }
        });
        price = (GrayEditText)findViewById(R.id.price);
        price.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                price.setText("");
            }
        });
        price.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (price.getText().length() > 0) {
                    money = Integer.parseInt(price.getText().toString().replace(" تومان ", "").replace(",", ""));
                    price.setText(String.format("%,.0f", (double) money) + " تومان ");
                }
                return false;
            }
        });
        price.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (price.getText().toString().length() > 0)
                    money = Integer.parseInt(price.getText().toString().replace(" تومان ", "").replace(",", ""));


            }
        });
        pay = (Button)findViewById(R.id.pay);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (money != 0) {
                    final ProgressDialog prog = new ProgressDialog(act);
                    prog.setCancelable(false);
                    prog.setTitle("لطفا منتطر بمانید");
                    if (prog.isShowing())
                        prog.dismiss();
                    prog.show();
                    exec.execute(new Runnable() {
                        @Override
                        public void run() {
                            Getter get_gateway = new Getter();
                            get_gateway.execute("http://api.yarbox.co/api/v1/payment/charge?price="+money,token);
                            try {
                                get_gateway.get();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            try {
                                JSONObject redirect = new JSONObject(get_gateway.mainresponse);
                                String red = redirect.getString("redirectTo");
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(red));
                                startActivity(i);
                                finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (prog.isShowing())
                                prog.dismiss();
                            prog.show();
                        }
                    });
                }
                else {
                    new MyAlert(act,"خطا!","مبلغ وارد شده صحیح نیست!");
                }
            }
        });
    }
}
