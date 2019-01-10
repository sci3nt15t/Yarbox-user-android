package app.yarbax.com.Utilities;


import java.io.IOException;

/**
 * Created by shayanrhm on 12/29/18.
 */

public class CheckInternet {

    public boolean check() throws InterruptedException, IOException {
        final String command = "ping -c 1 api.yarbox.co";
        return Runtime.getRuntime().exec(command).waitFor() == 0;
    }
}
