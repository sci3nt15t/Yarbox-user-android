package app.yarbax.com.Utilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Network extends AsyncTask<String,String,String>
{
    Context context;
    OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS).build();
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    public String mainresponse = "";
    public Network(Context context){
        this.context = context;
    }
    protected void onPreExecute(){
    }

    @Override
    protected String doInBackground(String... params ) {
        String url = params[0];
        String post = new String();

        for (int i=1;i<params.length;i++)
        {
            if (params.length - i != 1)
                post = post + params[i] + "&";
            else
                post = post + params[i];
        }
        if (post.length() > 0)
        url = url + "?" + post;

        try {
            if (url.length() > 5)
                mainresponse = postreq(url,post);
            else
                throw new IllegalStateException("Cannot execute task:"
                        + " the task has already been executed "
                        + "(a task can be executed only once)");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println(mainresponse);
        return mainresponse;
    }

    protected void onPostExecute(){
    }

    private String postreq(String url, String data) throws IOException {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, data);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}