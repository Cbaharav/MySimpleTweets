package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class TweetDetailsActivity extends AppCompatActivity {

    Tweet tweet;
    public ImageView ivProfileImage;
    public TextView tvUsername;
    public TextView tvBody;
    public TextView tvTime;
    public TextView tvHandle;
    TwitterClient client;
    public ImageButton btnLike;
    public ImageButton btnRetweet;
    public ImageButton btnReply;
    public TextView tvLikeCount;
    public TextView tvRTCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        // perform findViewById lookups
        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        tvUsername = (TextView) findViewById(R.id.tvUserName);
        tvBody = (TextView) findViewById(R.id.tvBody);
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvHandle = (TextView) findViewById(R.id.tvHandle);
        btnLike = (ImageButton) findViewById(R.id.btnLike);
        btnReply = (ImageButton) findViewById(R.id.btnReply);
        btnRetweet = (ImageButton) findViewById(R.id.btnRetweet);
        tvLikeCount = (TextView) findViewById(R.id.tvLikeCount);
        tvRTCount = (TextView) findViewById(R.id.tvRTCount);

        // populate the views according to position
        tvUsername.setText(tweet.user.name);
        tvBody.setText(tweet.body);
        tvTime.setText(tweet.getRelativeTimeAgo(tweet.createdAt));
        tvHandle.setText("@" + tweet.user.screenName);
        tvLikeCount.setText(Integer.toString(tweet.like_count));
        tvRTCount.setText(Integer.toString(tweet.retweet_count));

        // load user images
        Glide.with(this)
                .load(tweet.user.profileImageUrl)
                .into(ivProfileImage);

        if(tweet.liked) {
            btnLike.setImageResource(R.drawable.ic_vector_heart);
            btnLike.setColorFilter(Color.RED);
        } else {
            btnLike.setImageResource(R.drawable.ic_vector_heart_stroke);
        }
    }

    public void onClick(View v) {
        finish();
    }

    public void onLike(View v) {
        // make api call when etReply button is pressed
        // instantiating client with this context
        client = TwitterApp.getRestClient(this);
        // call sendTweet from TwitterClient
        client.likeTweet(tweet.liked, tweet.uid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (!tweet.liked) {
                    btnLike.setImageResource(R.drawable.ic_vector_heart);
                    tweet.liked = true;
                    btnLike.setColorFilter(Color.RED);
                    // manually incremeneting like count
                    tvLikeCount.setText(Integer.toString(Integer.parseInt(tvLikeCount.getText().toString()) + 1));
                } else {
                    btnLike.setImageResource(R.drawable.ic_vector_heart_stroke);
                    btnLike.setColorFilter(Color.BLACK);
                    tweet.liked = false;
                    // manually decrementing like count
                    tvLikeCount.setText(Integer.toString(Integer.parseInt(tvLikeCount.getText().toString()) - 1));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    public void onReply(View v) {
        // create intent for the new activity
        Intent i = new Intent(this, ReplyActivity.class);
        // serialize the movie using parceler, use its short name as a key
        i.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
        // start activity
        this.startActivity(i);
    }
}
