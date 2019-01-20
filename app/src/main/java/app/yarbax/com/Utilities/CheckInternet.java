package app.yarbax.com.Utilities;


import java.io.IOException;

/**
 * Created by shayanrhm on 12/29/18.
 */

public class CheckInternet {

    public boolean check() {
        final String command = "ping -c 1 api.yarbox.co";
        try {
            return Runtime.getRuntime().exec(command).waitFor() == 0;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
