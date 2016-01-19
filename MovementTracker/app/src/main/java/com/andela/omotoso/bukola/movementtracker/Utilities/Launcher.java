package com.andela.omotoso.bukola.movementtracker.utilities;

import android.content.Context;
import android.content.Intent;

/**
 * Created by GRACE on 1/16/2016.
 */

    public class Launcher {
        public static void launchActivity(Context context, Class<?> activity) {
            Intent intent = new Intent(context, activity);
            context.startActivity(intent);
        }
    }

