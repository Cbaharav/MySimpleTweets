package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    TwitterClient client;
    TweetAdapter tweetAdapter;
    ArrayList<Tweet> tweets;
    RecyclerView rvTweets;

    public static final int COMP_REQUEST_CODE = 20; // arbitrary request code for intent
    private SwipeRefreshLayout swipeContainer; // for swipe to refresh
    MenuItem miActionProgressItem; // indeterminate progress bar

    // infinite pagination variables
    long oldest_id = 0;

    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        client = TwitterApp.getRestClient(this);

        // find the RecyclerView
        rvTweets = (RecyclerView) findViewById(R.id.rvTweet);
        // init the arraylist (data source)
        tweets = new ArrayList<>();
        // construct the adapter from this data source
        tweetAdapter = new TweetAdapter(tweets);

        // creating LinearLayout Manager object to reference later for scroll listener
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        // RecyclerView setup (layout manager, use adapter)
        rvTweets.setLayoutManager(linearLayoutManager);
        // set the adapter
        rvTweets.setAdapter(tweetAdapter);

        // lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimelineAsync(0);
            }
        });

        // modifying the action bar, blue background and different title
        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff1da1f2")));
        bar.setTitle("Twitter-ish");

        // creating scrollListener for infinite pagination
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                loadNextDataFromApi();
            }
        };

        // Adds the scroll listener to RecyclerView
        rvTweets.addOnScrollListener(scrollListener);

        populateTimeline();
    }

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi() {
        // Send an API request to retrieve appropriate paginated data
        client.loadNextDataFromApi(oldest_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // iterate through the JSON array
                // for each entry, deserialize the JSON object
                showProgressBar();
                for (int i = 0; i < response.length(); i++) {
                    // convert each object to a Tweet model
                    // add that Tweet model to our data source
                    // notify the adapter that we've added an item
                    try {
                        Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                        tweets.add(tweet);
                        tweetAdapter.notifyItemInserted(tweets.size() - 1);

                        if (tweet.uid < oldest_id) {
                            oldest_id = tweet.uid;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }
        });
    }

    public void fetchTimelineAsync(int page) {
        // send the network request to fetch the updated data
        // 'client' here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint
        showProgressBar();
        client.getHomeTimeline(new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // clear out old items before appending in the new ones
                tweetAdapter.clear();
                tweets.clear();
                // the data has come back, add new items to your adapter
                for(int i = 0; i < response.length(); i++) {
                    // convert each object to a Tweet model
                    // add that Tweet model to our data source
                    // notify the adapter that we've added an item

                    try {
                        Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                        tweets.add(tweet);
                        tweetAdapter.notifyItemInserted(tweets.size() - 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                hideProgressBar();
                // signal refresh has finished
                swipeContainer.setRefreshing(false);
            }

            public void onFailure(Throwable throwable) {
                Log.d("DEBUG", "Fetch timeline error: " + throwable.toString());
                hideProgressBar();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // store instance of the menu item containing progress
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        // extract the action-view from the menu item
        ProgressBar v = (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);
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

    public void onComposeAction(MenuItem mi) {
        // sending intent to compose activity class when compose action is clicked
        Intent i = new Intent(TimelineActivity.this, ComposeActivity.class);
        startActivityForResult(i, COMP_REQUEST_CODE);
    }

    // handle results from compose action
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if the edit activity completed ok
        if(resultCode == RESULT_OK && requestCode == COMP_REQUEST_CODE) {
            //extract updated item text from result intent extras
            Tweet newTweet = (Tweet) Parcels.unwrap(data.getParcelableExtra(Tweet.class.getSimpleName()));
            // add new tweet to top of array list
            tweets.add(0, newTweet);
            // notify adapter of new tweet
            tweetAdapter.notifyItemInserted(0);
            // move recycler view to top of page
            rvTweets.scrollToPosition(0);
        }
    }

    private void populateTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // iterate through the JSON array
                // for each entry, deserialize the JSON object
                showProgressBar();
                for (int i = 0; i < response.length(); i++) {
                    // convert each object to a Tweet model
                    // add that Tweet model to our data source
                    // notify the adapter that we've added an item

                    try {
                        Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                        tweets.add(tweet);
                        tweetAdapter.notifyItemInserted(tweets.size() - 1);

                        // comparing tweet id with the oldest_id, and updating if necessary
                        if (i == 0) {
                            oldest_id = tweet.uid;
                        } else if (tweet.uid < oldest_id) {
                            oldest_id = tweet.uid;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                hideProgressBar();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("TwitterClient", response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("TwitterClient", responseString);
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }
        });
    }
}
