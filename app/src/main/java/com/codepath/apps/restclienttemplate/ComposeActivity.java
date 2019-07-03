package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    }


    // post button is clicked on new tweet
    public void onPost(View v) {
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
                    finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
