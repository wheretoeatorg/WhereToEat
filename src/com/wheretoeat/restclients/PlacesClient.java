
package com.wheretoeat.restclients;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.wheretoeat.helper.SharedPrefHelper;
import com.wheretoeat.helper.Utility;
import com.wheretoeat.models.FilterOptions;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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
        if (!Utility.isStringBlank(filOpt.getName()) && !filOpt.getName().equalsIgnoreCase("All")) {
            try {
                String name=URLEncoder.encode(filOpt.getName(),"UTF-8");
                urlString.append("&name=" + name);
            } catch (UnsupportedEncodingException e) {
            }

        }
//        if (!Utility.isStringBlank(filOpt.getType())) {
//            urlString.append("&types=" + filOpt.getType());
//        }

        try {
            String types=URLEncoder.encode("restaurant|cafe|bar","UTF-8");
            urlString.append("&types=" + types);
        } catch (UnsupportedEncodingException e) {
            // error encoding value use default
            urlString.append("&types=" + "restaurant");
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
        if (SharedPrefHelper.getOpenNowPref(context)) {
            urlString.append("&opennow=true");
        }
        int minPrice = SharedPrefHelper.getMinPricePref(context);
        int maxPrice = SharedPrefHelper.getMaxPricePref(context);
        if (SharedPrefHelper.getMinPricePref(context) != 0) {
            urlString.append("&minprice=" + minPrice);
        }
        if (SharedPrefHelper.getMaxPricePref(context) != 0) {
            urlString.append("&maxprice=" + maxPrice);
        }

        urlString.append("&sensor=false");
        urlString.append("&key=" + BROWSER_API_KEY);
        return urlString.toString();
    }
}
