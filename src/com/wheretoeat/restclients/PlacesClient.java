package com.wheretoeat.restclients;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.wheretoeat.helper.Utility;
import com.wheretoeat.models.FilterOptions;

public class PlacesClient {
	private static final String TAG = "PlacesClient";
	private static PlacesClient instance;
	Context context;
	String apiKey;
	private static final String BROWSER_API_KEY = "AIzaSyBQy7xjiAd8k4RIlKkeBiQl764UC2C_Mks";
	private static final String NEARBY_BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
	public static final String DETAIL_BASE_URL = "https://maps.googleapis.com/maps/api/place/details/json?";
	public static final String TEXT_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json?";

	private PlacesClient(Context context) {
		this.context = context;
	}

	public static PlacesClient getInstance(Context context) {
		if (instance == null) {
			try {
				instance = new PlacesClient(context);
			} catch (Exception e) {
				Log.e(TAG, "Exception in getInstance placeclient " + e);
			}
		}
		return instance;
	}

	public void searchRestaurants(FilterOptions filterOptions, AsyncHttpResponseHandler handler) {
		if (filterOptions != null) {
			String url = makeUrl(filterOptions, NEARBY_BASE_URL);
			Log.d(TAG, url);
			AsyncHttpClient client = new AsyncHttpClient();
			client.get(url, handler);
		}

	}

	public void getDetails(FilterOptions filOpt, AsyncHttpResponseHandler handler) {
		String url = makeUrl(filOpt, DETAIL_BASE_URL);
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(url, handler);
	}

	public void textSearchRestaurants(FilterOptions filterOptions, AsyncHttpResponseHandler handler) {
		String url = makeUrl(filterOptions, TEXT_SEARCH_URL);
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(url, handler);
	}

	private String makeUrl(FilterOptions filOpt, String baseurl) {
		StringBuilder urlString = new StringBuilder(baseurl);

		if (filOpt.getLatitude() != 0 || filOpt.getLongitude() != 0) {
			urlString.append("&location=");
			urlString.append(Double.toString(filOpt.getLatitude()));
			urlString.append(",");
			urlString.append(Double.toString(filOpt.getLongitude()));
		}
		if (!Utility.isStringBlank(filOpt.getName())) {
			urlString.append("&name=" + filOpt.getName());
		}
		if (!Utility.isStringBlank(filOpt.getType())) {
			urlString.append("&types=" + filOpt.getType());
		}
		if (filOpt.getRadius() != 0) {
			urlString.append("&radius=" + filOpt.getRadius());
		}
		if (filOpt.getRankby() != null) {
			urlString.append("&rankby=" + filOpt.getRankby());
		}
		if (!Utility.isStringBlank(filOpt.getReference())) {
			urlString.append("&reference=" + filOpt.getReference());
		}
		if (!Utility.isStringBlank(filOpt.getQuery())) {
			urlString.append("&query=" + filOpt.getQuery());
		}
		urlString.append("&sensor=false");
		urlString.append("&key=" + BROWSER_API_KEY);
		return urlString.toString();
	}
}
