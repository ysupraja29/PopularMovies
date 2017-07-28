package com.supraja_y.popularmovies.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.supraja_y.popularmovies.BuildConfig;
import com.supraja_y.popularmovies.POJOS.movModel;
import com.supraja_y.popularmovies.Screens.movListActivity;
import com.supraja_y.popularmovies.R;

import java.util.ArrayList;

public class movAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<movModel> mov_data = new ArrayList<>();
    public movAdapter(Context context, ArrayList<movModel> mov_data) {
        this.context = context;
        this.mov_data = mov_data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.grid_image, parent, false);
        viewHolder = new MyItemHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        movListActivity.progressBar.setVisibility(View.VISIBLE);

        String imageURL = BuildConfig.IMAGE_URL+"/w342" + mov_data.get(position).getposter_path() + "?api_key?=" + BuildConfig.API_KEY;
        Picasso.with(context).load(imageURL).into(((MyItemHolder) holder).imageView, new Callback() {
            @Override
            public void onSuccess() {
                movListActivity.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError() {

            }
        });

    }

    @Override
    public int getItemCount() {
        return mov_data.size();
    }

    public static class MyItemHolder extends RecyclerView.ViewHolder {
        ImageView imageView;


        public MyItemHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.listImage);
        }

    }

    public void addAll(ArrayList<movModel> movlist){
        for (int i = 0; i < movlist.size(); i++)
            mov_data.add(movlist.get(i));
    }

}