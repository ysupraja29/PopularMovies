package com.supraja_y.popularmovies.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.squareup.picasso.Picasso;
import com.supraja_y.popularmovies.POJOS.traModel;
import com.supraja_y.popularmovies.R;

import java.util.ArrayList;

public class traAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<traModel> mov_data = new ArrayList<>();


    public traAdapter(Context context, ArrayList<traModel> mov_data) {
        this.context = context;
        this.mov_data = mov_data;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.tra_list_item, parent, false);
        viewHolder = new MyItemHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        String id = mov_data.get(position).getKey();
        String thumbnailURL = "http://img.youtube.com/vi/".concat(id).concat("/hqdefault.jpg");
        Picasso.with(context).load(thumbnailURL).placeholder(new IconicsDrawable(context)
                .color(Color.BLACK)
                .icon(MaterialDesignIconic.Icon.gmi_youtube)
                .sizeDp(24)
        ).into(((MyItemHolder) holder).imageView);


    }

    @Override
    public int getItemCount() {
        return mov_data.size();
    }

    public static class MyItemHolder extends RecyclerView.ViewHolder {
        ImageView imageView;


        public MyItemHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.trailerImage);
        }

    }


}