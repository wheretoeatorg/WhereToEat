package com.wheretoeat.restclients;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import android.os.AsyncTask;
import android.util.Log;

public class YelpAsyncTask extends AsyncTask<Object, Void, String> {

	private static final String TAG = "YelpAsyncTask";

	@Override
	protected String doInBackground(Object... params) {
		OAuthService service = (OAuthService) params[0];
		Token accessToken = (Token) params[1];

		OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.yelp.com/v2/search");
		request.addQuerystringParameter("term", "burritos");
		request.addQuerystringParameter("ll", 30.361471 + "," + -87.164326);
		service.signRequest(accessToken, request);
		Response response = request.send();
		Log.d(TAG, response.getBody());
		return response.getBody();
	}

}
