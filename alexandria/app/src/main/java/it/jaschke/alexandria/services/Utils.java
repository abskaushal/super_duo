package it.jaschke.alexandria.services;

import android.content.Context;
import android.net.ConnectivityManager;

import it.jaschke.alexandria.zxing.Contents;

/**
 * Created by Abhishek on 19-Mar-16.
 */
public class Utils {

    /**
     * Get the internet available status
     * @param context
     * @return
     */
    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null;
    }
}
