package app.yarbax.com.Utilities;

import android.content.SharedPreferences;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by shayanrhm on 12/29/18.
 */

public class Getter extends AsyncTask<String, String, String> {

    String url;
    String auth;
    String json;
    String token;
    String post;
    Request request;
    OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build();
    public static MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    public String mainresponse = "";
    @Override
    protected String doInBackground(String... params) {
        url = params[0];
        if (params.length > 1) {
            auth = params[1];
            request = new Request.Builder()
                    .url(url)
                    .addHeader("Content-Type","application/json")
                    .addHeader("Authorization","bearer "+auth)
                    .build();
        }else{
            request = new Request.Builder()
                    .url(url)
                    .addHeader("Content-Type","application/json")
                    .build();
        }
        if (params.length > 2) {
            json = params[2];
        }
        post = new String();


        for (int i=2;i<params.length;i++)
        {
            if (params.length - i != 1)
                post = post + params[i] + "&";
            else
                post = post + params[i];
        }
        url = url + "?" + post;
        try {
            Response response = client.newCall(request).execute();
            mainresponse = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mainresponse;
    }
}
