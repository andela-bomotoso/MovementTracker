package com.andela.omotoso.bukola.movementtracker.fragments;

import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andela.omotoso.bukola.movementtracker.R;
import com.andela.omotoso.bukola.movementtracker.utilities.Trail;
import com.andela.omotoso.bukola.movementtracker.utilities.TrailList;

import java.util.List;

/**
 * Created by bukola_omotoso on 03/02/16.
 */
public class TrackerListFragment extends Fragment {

    RecyclerView trackerRecyclerView;
    TrackerAdapter trackerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.tracker_list_fragment,container,false);
        trackerRecyclerView = (RecyclerView)view.findViewById(R.id.tracker_recycler_view);
        trackerRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();
        return view;
    }

    private void updateUI() {

        TrailList trailList = TrailList.getTrailList(getActivity());
        List<Trail>trails = trailList.getTrails();
        trackerAdapter = new TrackerAdapter(trails);
        trackerRecyclerView.setAdapter(trackerAdapter);
    }

    private class TrackerHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private Trail trail;
        private TextView locationText;
        private TextView activityText;

        public TrackerHolder(View itemView) {

            super(itemView);

            itemView.setOnClickListener(this);
            locationText = (TextView)itemView.findViewById(R.id.location);
            activityText = (TextView)itemView.findViewById(R.id.duration);
        }

        @Override
        public void onClick(View view) {

        }

        private void bindTracker(Trail trail) {

            this.trail = trail;
            locationText.setText(trail.getLocation());
            activityText.setText(trail.getActivity());
        }

    }

    private class TrackerAdapter extends RecyclerView.Adapter<TrackerHolder> {

        private List<Trail> trails;

        public TrackerAdapter(List<Trail>trails) {

            this.trails = trails;
        }
        public int getItemCount() {

            return trails.size();
        }

        public void onBindViewHolder(TrackerHolder holder, int position) {
            Trail trail = trails.get(position);
            holder.bindTracker(trail);
        }

        public TrackerHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater layoutInflater =   LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_tracker, parent, false);
            return new TrackerHolder(view);
        }
    }



}
