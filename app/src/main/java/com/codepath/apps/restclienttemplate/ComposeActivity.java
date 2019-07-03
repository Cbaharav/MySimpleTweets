package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    EditText etNewTweet;
    Button btnPost;
    TextView tvChar;
    TwitterClient client;
    MenuItem miActionProgressItem;

    // TextWatcher counts the number of characters, sets tvChar to characters remaining
    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // sets tvChar to characters remaining while text is being edited
            tvChar.setText(String.valueOf(280 - s.length()) + " characters remaining");
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        // perform findViewById lookups
        etNewTweet = (EditText) findViewById(R.id.etNewTweet);
        btnPost = (Button) findViewById(R.id.btnPost);
        tvChar = (TextView) findViewById(R.id.tvChar);
        // setting textwatcher on etNewTweet
        etNewTweet.addTextChangedListener(mTextEditorWatcher);

        // modifying the action bar, blue background and different title
        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff1da1f2")));
        bar.setTitle("New Tweet");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.menu_compose, menu);

        // store instance of the menu item containing progress
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        // extract the action-view from the menu item
        ProgressBar v = (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);
        v.setBackgroundColor(getResources().getColor(android.R.color.white));

        return super.onCreateOptionsMenu(menu);
    }

    // toggle visibility of progress bar
    public void showProgressBar() {
        // show progress item
        miActionProgressItem.setVisible(true);
    }

    public void hideProgressBar() {
        // hide progress item
        miActionProgressItem.setVisible(false);
    }

    // post button is clicked on new tweet
    public void onPost(View v) {
        showProgressBar();
        // instantiating client with this context
        client = TwitterApp.getRestClient(this);
        // call sendTweet from TwitterClient
        client.sendTweet(etNewTweet.getText().toString(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    // get new tweet
                    Tweet tweet = Tweet.fromJSON(response);
                    // create new intent and add tweet as extra (parceled)
                    Intent i = new Intent();
                    i.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                    // set as result of the intent, returning to TimelineActivity
                    setResult(RESULT_OK, i);
                    // close ComposeActivity
                    hideProgressBar();
                    finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                hideProgressBar();
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });

    }
}
