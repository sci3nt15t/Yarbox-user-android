package app.yarbax.com.MyViews;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by shayanrhm on 12/29/18.
 */

public class MyAlert {

    public MyAlert(Activity activity,String title,String message){
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setCancelable(true);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alert.show();
    }
}
