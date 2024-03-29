package com.codepath.apps.restclienttemplate;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/scribejava/scribejava/tree/master/scribejava-apis/src/main/java/com/github/scribejava/apis
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final BaseApi REST_API_INSTANCE = TwitterApi.instance(); // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "TEkkex4tqDbKHf0vWafhdTNdG";       // Change this
	public static final String REST_CONSUMER_SECRET = "zcDJIfZPTT5Ivo5NqUuQ735ld3zuQVsugnP9Nsc38jx2NKYRVU"; // Change this

	// Landing page to indicate the OAuth flow worked in case Chrome for Android 25+ blocks navigation back to the app.
	public static final String FALLBACK_URL = "https://codepath.github.io/android-rest-client-template/success.html";

	// See https://developer.chrome.com/multidevice/android/intents
	public static final String REST_CALLBACK_URL_TEMPLATE = "intent://%s#Intent;action=android.intent.action.VIEW;scheme=%s;package=%s;S.browser_fallback_url=%s;end";

	public TwitterClient(Context context) {
		super(context, REST_API_INSTANCE,
				REST_URL,
				REST_CONSUMER_KEY,
				REST_CONSUMER_SECRET,
				String.format(REST_CALLBACK_URL_TEMPLATE, context.getString(R.string.intent_host),
						context.getString(R.string.intent_scheme), context.getPackageName(), FALLBACK_URL));
	}

	public void getHomeTimeline(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("tweet_mode", "extended");
		client.get(apiUrl, params, handler);
	}

	public void loadNextDataFromApi(long oldest_id, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", 25);
		// setting max_id to id before the oldest post (as max_id is inclusive)
		params.put("max_id", oldest_id - 1);
		params.put("tweet_mode", "extended");
		client.get(apiUrl, params, handler);
	}


	public void sendTweet(String message, AsyncHttpResponseHandler handler) {
		// define the endpoint URL with getApiUrl and pass a relative path to the endpoint
		String apiUrl = getApiUrl("statuses/update.json");
		// define the parameters to pass to the request
		RequestParams params = new RequestParams();
		params.put("status", message);
		params.put("tweet_mode", "extended");
		// define request method and make a call to the client
		client.post(apiUrl, params, handler);
	}

	public void replyTweet(String message, long twitterId, AsyncHttpResponseHandler handler) {
		// define the endpoint URL with getApiUrl and pass a relative path to the endpoint
		String apiUrl = getApiUrl("statuses/update.json");
		// define the parameters to pass to the request
		RequestParams params = new RequestParams();
		params.put("status", message);
		params.put("in_reply_to_status_id", twitterId);
		params.put("tweet_mode", "extended");
		// define request method and make a call to the client
		client.post(apiUrl, params, handler);
	}

	public void likeTweet(boolean like, long twitterId, AsyncHttpResponseHandler handler) {
		// define the endpoint URL with getApiUrl and pass a relative path to the endpoint
		String apiUrl = getApiUrl("favorites/create.json");
		// the message has already been liked
		if (like) {
			apiUrl = getApiUrl("favorites/destroy.json");
		}
		// define the parameters to pass to the request
		RequestParams params = new RequestParams();
		params.put("id", twitterId);
		// define request method and make a call to the client
		client.post(apiUrl, params, handler);
	}

	public void rtTweet(boolean retweet, long twitterId, AsyncHttpResponseHandler handler) {
		// define the endpoint URL with getApiUrl and pass a relative path to the endpoint
		String apiUrl = getApiUrl("statuses/retweet/" + Long.toString(twitterId) + ".json");
		// the message has already been liked
		if (retweet) {
			apiUrl = getApiUrl("statuses/unretweet/" + Long.toString(twitterId) + ".json");
		}
		// define the parameters to pass to the request
		RequestParams params = new RequestParams();
		params.put("id", twitterId);
		// define request method and make a call to the client
		client.post(apiUrl, params, handler);
	}

}
