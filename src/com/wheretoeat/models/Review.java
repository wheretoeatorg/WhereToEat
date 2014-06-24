
package com.wheretoeat.models;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.Html;
import android.text.format.DateFormat;

public class Review {

    private String author;
    private String ratingText;
    private int rating;
    private String time;
    private long timestamp;

    public String getAuthor() {
        return author;
    }

    public String getRatingText() {
        return ratingText;
    }

    public int getRating() {
        return rating;
    }

    public String getTime() {
        return time;
    }

    public static List<Review> fromJSON(JSONArray jsonArr) {
        List<Review> ratings = new ArrayList<Review>();

        try {
            for (int i = 0; i < jsonArr.length(); i++) {
                Review review = new Review();
                JSONObject rObj = jsonArr.getJSONObject(i);
                review.author = rObj.getString("author_name");
                review.ratingText = Html.fromHtml(rObj.getString("text")).toString();
                // review.time = rObj.getString("time");
                review.timestamp = rObj.getLong("time");
                review.time = formatedDate(review.timestamp);

                JSONArray aspects = rObj.getJSONArray("aspects");
                review.rating = aspects.getJSONObject(0).getInt("rating");
                ratings.add(review);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ratings;
    }

    private static String formatedDate(long seconds) {

        long millis = seconds * 1000;
        Date date = new Date(millis);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM yy", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String formattedDate = sdf.format(date);
        System.out.println(formattedDate);
        return formattedDate;
    }
}
