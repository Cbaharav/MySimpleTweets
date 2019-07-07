package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import java.util.List;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    private List<Tweet> mTweets;
    Context context;

    // pass in the Tweets array into the constructor
    public TweetAdapter(List<Tweet> tweets) {
        mTweets = tweets;
    }

    // for each row, inflate the layout and cache references into ViewHolder
    @NonNull
    @Override
    public TweetAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    //bind the values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        // get the data according to position
        final Tweet tweet = mTweets.get(position);

        // populate the views according to position
        viewHolder.tvUsername.setText(tweet.user.name);
        viewHolder.tvBody.setText(tweet.body);
        viewHolder.tvTime.setText(tweet.getRelativeTimeAgo(tweet.createdAt));
        viewHolder.tvHandle.setText("@" + tweet.user.screenName);

        // load user images
        Glide.with(context)
                .load(tweet.user.profileImageUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(viewHolder.ivProfileImage);

        // load embedded media
        Glide.with(context)
                .load(tweet.media)
                .into(viewHolder.ivMedia);

        viewHolder.ivReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Reply button clicked", Toast.LENGTH_LONG);
                // create intent for the new activity
                Intent i = new Intent(context, ReplyActivity.class);
                // serialize the movie using parceler, use its short name as a key
                i.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                // start activity
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    // create ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView ivProfileImage;
        public TextView tvUsername;
        public TextView tvBody;
        public TextView tvTime;
        public TextView tvHandle;
        public ImageButton ivReply;
        public ImageView ivMedia;

        public ViewHolder(View itemView) {
            super(itemView);

            // perform findViewById lookups
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUsername = (TextView) itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            tvHandle = (TextView) itemView.findViewById(R.id.tvHandle);
            ivReply = (ImageButton) itemView.findViewById(R.id.ivReply);
            ivMedia = (ImageView) itemView.findViewById(R.id.ivMedia);
            itemView.setOnClickListener(this);
        }

        //when user clicks on a row, show MovieDetailsActivity for the selected movie
        @Override
        public void onClick(View v) {
            //gets item position
            int position = getAdapterPosition();
            //make sure position is valid, actually exists in the view
            if(position != RecyclerView.NO_POSITION) {
                //get the movie at the position
                Tweet tweet = mTweets.get(position);
                //create intent for the new activity
                Intent intent = new Intent(context, TweetDetailsActivity.class);
                //serialize the movie using parceler, use its short name as a key
                intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                //show activity
                context.startActivity(intent);
            }
        }

    }

    // helper methods for adapter to clear items from dataset or add items to it
    // clean all elements of the recycler
    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }

    // add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        mTweets.addAll(list);
        notifyDataSetChanged();
    }
}
