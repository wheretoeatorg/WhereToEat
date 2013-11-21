package com.wheretoeat.restclients;

/*
 Example code based on code from Nicholas Smith at http://imnes.blogspot.com/2011/01/how-to-use-yelp-v2-from-java-including.html
 For a more complete example (how to integrate with GSON, etc) see the blog post above.
 */

import org.scribe.builder.api.Api;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Example for accessing the Yelp API.
 */
public class YelpClient extends OAuthBaseClient {

	public static Class<? extends Api> REST_API_CLASS = YelpApi2.class;
	public static final String REST_URL = "http://api.yelp.com/v2";
	protected static final String TAG = "YELP";
	private static final String CONSUMER_KEY = "38ms0UZ6soEdBZuDpM2JCA";
	private static final String CONSUMER_SECRET = "T-1bqbexkCPt9X3VNLXTarBGor8";
	private static final String TOKEN = "eWnMPdw_aR6e78JwtW22lZHwJbendqbw";
	private static final String TOKEN_SECRET = "Op_0wniU1YU5dvF8CP5sqVfx14s";
	private static final String REST_CALLBACK_URL = "oauth://wheretoeat";
	OAuthService service;
	Token accessToken;

	public YelpClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, CONSUMER_KEY, CONSUMER_SECRET, REST_CALLBACK_URL);
		client.setAccessToken(new Token(TOKEN, TOKEN_SECRET));
	}

	public void searchRestaurants(String term, String sort, double latitude, double longitude, AsyncHttpResponseHandler handler) {

		/*
		 * YelpAsyncTask asyncTask = new YelpAsyncTask();
		 * asyncTask.execute(service,accessToken);
		 */
		String apiUrl = getApiUrl("search");
		RequestParams param = new RequestParams();
		param.put("term", term);
		param.put("ll", latitude + "," + longitude);
		param.put("sort", sort);
		client.get(apiUrl, param, handler);
	}

}