package com.andela.omotoso.bukola.movementtracker.utilities;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

/**
 * Created by GRACE on 1/23/2016.
 */


public class DialogDivider {

    private Context context;
    private Dialog dialog;

    public DialogDivider(Context context, Dialog dialog) {

        this.context = context;
        this.dialog = dialog;
    }

    public void setDialog(Dialog dialog) {

        this.dialog = dialog;
    }

    public void setDivider() {

        int titleDividerId = context.getResources().getIdentifier("titleDivider", "id", "android");
        View titleDivider = dialog.findViewById(titleDividerId);
        if (titleDivider != null)
            titleDivider.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_light));
    }
}
