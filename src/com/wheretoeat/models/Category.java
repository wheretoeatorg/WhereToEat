package com.wheretoeat.models;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

public class Category {

	private String key;
	private String displayText;

	public Category(String displayText, String key) {
		this.displayText = displayText;
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public String getDisplayText() {
		return displayText;
	}
	
	public String toString(){
		return displayText;
	}

	public static List<Category> parseJSONCategoryList(JSONArray json) {
		ArrayList<Category> categories = new ArrayList<Category>();
		try {
			for (int i = 0; i < json.length(); i++) {
				JSONArray jsonCat = (JSONArray) json.get(i);
				categories.add(new Category(jsonCat.getString(0), jsonCat.getString(1)));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return categories;
	}

}
