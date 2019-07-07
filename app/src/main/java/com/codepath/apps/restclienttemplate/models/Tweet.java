package com.codepath.apps.restclienttemplate.models;

import android.text.format.DateUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

@Parcel
public class Tweet {

    //list out the attributes
    public String body;
    public long uid; // database ID for the tweet
    public User user;
    public String createdAt;
    public boolean liked;
    public int like_count;
    public int retweet_count;

    public Tweet() {}

    //deserialize the JSON
    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();

        //extract the values from JSON
        tweet.body = jsonObject.getString("full_text");
        tweet.uid = jsonObject.getLong("id");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        tweet.liked = jsonObject.getBoolean("favorited");
        tweet.like_count = jsonObject.getInt("favorite_count");
        tweet.retweet_count = jsonObject.getInt("retweet_count");
        return tweet;
    }

    // getting the time since posted based on created_at JSON value
    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }
}
