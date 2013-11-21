package com.wheretoeat.models;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wheretoeat.helper.Utility;

public class Restaurant {

	private String resId;
	private List<Review> reviews;
	private String address;
	private String phoneNumber;
	private String rating;
	private Category category;
	private List<String> photoUrl;
	private String resUrl;
	private String name;
	private String categories;
	private String distanceMiles;
	// double[0] = lat
	// double[1] = lng
	private double[] location;
	private String resRef;

	public static List<Restaurant> fromJSON(JSONObject jsonObject) {
		List<Restaurant> resList = new ArrayList<Restaurant>();
		JSONArray array = null;
		try {
			array = jsonObject.getJSONArray("businesses");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (array != null && array.length() > 0) {
			for (int i = 0; i < array.length(); i++) {
				Restaurant res = new Restaurant();
				JSONObject busiJsonObj;
				try {
					busiJsonObj = (JSONObject) array.getJSONObject(i);
					JSONArray catArray = busiJsonObj.getJSONArray("categories");
					res.setResUrl(busiJsonObj.getString("image_url"));
					res.setName(busiJsonObj.getString("name"));
					res.setRating(busiJsonObj.getString("rating"));
					// categoriesStringConversion(catArray)
					res.setCategories(catArray.toString());
					double miles = Utility.convertMeterstoMiles(Double.parseDouble(busiJsonObj.getString("distance")));
					miles = Math.round(miles * 100);
					miles = miles / 100;
					res.setDistanceMiles(miles + "");
					resList.add(res);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		return resList;
	}

	public static List<Restaurant> fromPlacesJSON(JSONObject jsonObject) {
		List<Restaurant> resList = new ArrayList<Restaurant>();
		JSONArray array = null;
		try {
			array = jsonObject.getJSONArray("results");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (array != null && array.length() > 0) {
			for (int i = 0; i < array.length(); i++) {
				try {
					resList.add(fromJson(array.getJSONObject(i)));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		return resList;
	}

	public static Restaurant fromJson(JSONObject resultsObj) throws JSONException {
		boolean isDetail = false;
		if (resultsObj.has("result")) {
			resultsObj = resultsObj.getJSONObject("result");
			isDetail = true;
		}
		Restaurant res = new Restaurant();

		if (resultsObj.has("geometry")) {
			JSONObject geometryObj = resultsObj.getJSONObject("geometry");
			res.setLocation(getLocation(geometryObj));
		}

		if (resultsObj.has("name")) {
			res.setName(resultsObj.getString("name"));
		}

		if (resultsObj.has("website")) {
			res.setResUrl(resultsObj.optString("website"));
		}

		if (resultsObj.has("rating")) {
			res.setRating(resultsObj.getString("rating"));
		} else {
			res.setRating("NA");
		}

		if (resultsObj.has("reference")) {
			res.setResRef(resultsObj.getString("reference"));
		}

		if (resultsObj.has("id")) {
			res.setResId(resultsObj.getString("id"));
		}

		if (isDetail) {
			if (resultsObj.has("formatted_phone_number")) {
				res.setPhoneNumber(resultsObj.getString("formatted_phone_number"));
			}

			if (resultsObj.has("formatted_address")) {
				res.setAddress(resultsObj.getString("formatted_address"));
			}

			if (resultsObj.has("reviews")) {
				res.setReviews(Review.fromJSON(resultsObj.getJSONArray("reviews")));
			}

		}
		if (resultsObj.has("types")) {
			JSONArray catArray = resultsObj.getJSONArray("types");
			String categories = categoriesStringConversion(catArray);
			res.setCategories(categories);
		}

		return res;
	}

	private static double[] getLocation(JSONObject geometryJsonObj) {
		double[] location = { 0, 0 };
		try {
			JSONObject locationObj = geometryJsonObj.getJSONObject("location");
			location[0] = locationObj.getDouble("lat");
			location[1] = locationObj.getDouble("lng");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return location;
	}

	private static String categoriesStringConversion(JSONArray catArray) {
		StringBuilder categories = new StringBuilder();
		for (int i = 0; i < catArray.length(); i++) {
			try {
				String type = catArray.get(i).toString();
				String prettyType = type.substring(0, 1).toUpperCase() + type.substring(1);
				prettyType = prettyType.replace('_', ' ');
				categories.append(prettyType);
				if (i < catArray.length() - 1) {
					categories.append(", ");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return categories.toString();
	}

	public List<Review> getReviews() {
		return reviews;
	}

	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public List<String> getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(List<String> photoUrl) {
		this.photoUrl = photoUrl;
	}

	public String getResUrl() {
		return resUrl;
	}

	public void setResUrl(String resUrl) {
		this.resUrl = resUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDistanceMiles() {
		return distanceMiles;
	}

	public void setDistanceMiles(String distanceMiles) {
		this.distanceMiles = distanceMiles;
	}

	public String getCategories() {
		return categories;
	}

	public void setCategories(String categories) {
		this.categories = categories;
	}

	public double[] getLocation() {
		return location;
	}

	public void setLocation(double[] location) {
		this.location = location;
	}

	public String getResId() {
		return resId;
	}

	public void setResId(String resId) {
		this.resId = resId;
	}

	public String getResRef() {
		return resRef;
	}

	public void setResRef(String resRef) {
		this.resRef = resRef;
	}

}
