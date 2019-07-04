package com.codepath.apps.restclienttemplate;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ReplyActivity extends AppCompatActivity {

    Tweet tweet;
    public ImageView ivProfileImage;
    public TextView tvUsername;
    public TextView tvBody;
    public TextView tvTime;
    public TextView tvHandle;
    public EditText etReply;
    public Button btnReply;
    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        // perform findViewById lookups
        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        tvUsername = (TextView) findViewById(R.id.tvUserName);
        tvBody = (TextView) findViewById(R.id.tvBody);
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvHandle = (TextView) findViewById(R.id.tvHandle);
        etReply = (EditText) findViewById(R.id.etReply);
        btnReply = (Button) findViewById(R.id.btnReply);

        // populate the views according to position
        tvUsername.setText(tweet.user.name);
        tvBody.setText(tweet.body);
        tvTime.setText(tweet.getRelativeTimeAgo(tweet.createdAt));
        tvHandle.setText("@" + tweet.user.screenName);
        etReply.setText(tvHandle.getText());

        // load user images
        Glide.with(this)
                .load(tweet.user.profileImageUrl)
                .into(ivProfileImage);

        // modifying the action bar, blue background and different title
        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff1da1f2")));
        bar.setTitle("Reply to @" + tweet.user.screenName);
    }

    public void onReplyBtn(View v) {
        // make api call when etReply button is pressed
        // instantiating client with this context
        client = TwitterApp.getRestClient(this);
        // call sendTweet from TwitterClient
        client.replyTweet(etReply.getText().toString(), tweet.uid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }
}
