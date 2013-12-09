
package com.wheretoeat.models;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

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
                if (review.ratingText.length() > 100) {
                    review.ratingText = review.ratingText.substring(0, 100) + "...";
                }
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

    private static String formatedDate(long timeStamp) {
        Date date = new Date(timeStamp);
        SimpleDateFormat formatter = new SimpleDateFormat("MM/yy");
        String formattedDate = formatter.format(date);
        return formattedDate;
    }
}
