package com.example.calendarapplication;

import android.content.Context;
import android.widget.Toast;

public class ToastMessage extends Toast {

    private static Toast toast;

    public ToastMessage(Context context, String msg) {
        super(context);

        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }
}