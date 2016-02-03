package com.andela.omotoso.bukola.movementtracker.utilities;

import android.content.Context;
import android.view.animation.TranslateAnimation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bukola_omotoso on 03/02/16.
 */
public class TrailList {

    private static TrailList trailList;
    private List<Trail> trails;

    private TrailList(Context context) {

        trails =  new ArrayList<Trail>();
        for(Trail trail:trails) {

            trail.setActivity(extractBody(trail.toString()));
            trail.setLocation(extractHead(trail.toString()));
            trails.add(trail);
        }

    }

    private String extractHead(String trail) {
        return trail.split("\\r?\\n")[0];
    }

    private String extractBody(String trail) {
        return trail.split("\\r?\\n")[1];
    }

    public List<Trail>getTrails() {
        return trails;
    }


    public void setTrails(List<Trail> trails) {
        this.trails = trails;
    }

    public static TrailList getTrailList (Context context) {

        if(trailList == null) {

            trailList = new TrailList(context);
        }
        return trailList;
    }
    
}
