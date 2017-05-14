package Utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Vibrator;

/**
 * Created by JAPC on 14-05-2017.
 */

public class Helper {



    static public void showDialog(Context context, String title, String message){

        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton("Ok",null)
                .create()
                .show();

    }
    public  static void vibration(Context context,int seconds){

        if(seconds<=0){
            seconds = 1;
        }

        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(seconds*250);
    }


}
