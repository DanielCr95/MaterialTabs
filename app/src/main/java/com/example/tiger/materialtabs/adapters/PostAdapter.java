package com.example.tiger.materialtabs.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.tiger.materialtabs.Loader.PicassoClient;
import com.example.tiger.materialtabs.R;
import com.example.tiger.materialtabs.models.Post;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by am on 1/24/2017.
 */

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> implements Filterable{

    public Context mContext;

    public ArrayList<Post> original_items = new ArrayList<>();
    public ArrayList<Post> filtered_items = new ArrayList<>();
    ItemFilter mFilters = new ItemFilter();

    public PostAdapter(Context mContext, ArrayList<Post> postList) {
        this.mContext = mContext;
        this.original_items = postList;
        this.filtered_items = postList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_post_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
            Post post = filtered_items.get(position);
            holder.txt_title.setText(post.getId());
            holder.txt_body.setText(post.getUrl());
                PicassoClient.downloadImage(mContext, post.getImage(), holder.image);

    }

    @Override
    public int getItemCount() {
        return filtered_items.size();
    }

    @Override
    public Filter getFilter() {
        return mFilters;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView txt_title, txt_body;
        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            txt_title = (TextView) itemView.findViewById(R.id.txt_title);
            txt_body = (TextView) itemView.findViewById(R.id.txt_body);
            image=(ImageView)itemView.findViewById(R.id.image_url);
        }

    }

    private class ItemFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            String query = charSequence.toString().toLowerCase();
            FilterResults results = new FilterResults();
            final ArrayList<Post> list = original_items;
            final ArrayList<Post> result_list = new ArrayList<>(list.size());
            for (int i = 0; i < list.size(); i++){
                String str_title = list.get(i).getId();
                if (str_title.toLowerCase().contains(query)){
                    result_list.add(list.get(i));
                }
            }
            results.values = result_list;
            results.count = result_list.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            filtered_items = (ArrayList<Post>) filterResults.values;
            notifyDataSetChanged();

        }
    }

}
