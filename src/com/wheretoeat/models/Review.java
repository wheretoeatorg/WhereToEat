package com.wheretoeat.models;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.Html;

public class Review {

	private String author;
	private String ratingText;
	private int rating;
	private String time;
	
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
	
	public static List<Review> fromJSON(JSONArray jsonArr){
		List<Review> ratings = new ArrayList<Review>();
		
		try{
			for (int i = 0; i < jsonArr.length(); i++) {
				Review r = new Review();
				JSONObject rObj = jsonArr.getJSONObject(i);
				r.author = rObj.getString("author_name");
				r.ratingText = Html.fromHtml(rObj.getString("text")).toString();
				if(r.ratingText.length() > 100){
					r.ratingText = r.ratingText.substring(0, 100) + "...";
				}
				r.time = rObj.getString("time");
				JSONArray aspects = rObj.getJSONArray("aspects");
				r.rating = aspects.getJSONObject(0).getInt("rating");
				ratings.add(r);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return ratings;
	}

}
