package com.supraja_y.popularmovies.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.supraja_y.popularmovies.R;
import com.supraja_y.popularmovies.POJOS.revModel;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class revAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<revModel> mov_data = new ArrayList<>();


    public revAdapter(Context context, ArrayList<revModel> mov_data) {
        this.context = context;
        this.mov_data = mov_data;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.rev_list_item, parent, false);
        viewHolder = new MyItemHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        ((MyItemHolder) holder).authorTextView.setText(mov_data.get(position).getAuthor());
        ((MyItemHolder) holder).reviewTextView.setText(mov_data.get(position).getcontent());

    }

    @Override
    public int getItemCount() {
        return mov_data.size();
    }

    public static class MyItemHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.authorTextView)
        TextView authorTextView;
        @Bind(R.id.reviewTextView)
        TextView reviewTextView;


        public MyItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

        }

    }


}