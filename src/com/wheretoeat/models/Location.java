package com.wheretoeat.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Location {

	private String address;
	private String city;
	private String zipCode;
	private String state;
	private String displayAddress;

	public Location() {
	}

	public String getAddress() {
		return address;
	}

	public String getCity() {
		return city;
	}

	public String getZipCode() {
		return zipCode;
	}

	public String getState() {
		return state;
	}

	public String getDisplayAddress() {
		return displayAddress;
	}
	
	public String toString(){
		return displayAddress;
	}
	
	public static String parseDisplayAddress(JSONArray displayJson) throws JSONException{
		StringBuilder displayAddress = new StringBuilder();
		for (int i = 0; i < displayJson.length(); i++) {
			displayAddress.append(displayJson.get(i));
			displayAddress.append("\n");
		}
		return displayAddress.toString();
	}

	public static Location fromJson(JSONObject json) {
		Location location = new Location();
		try {
			location.address = json.getString("address");
			location.city = json.getString("city");
			location.zipCode = json.getString("postal_code");
			location.state = json.getString("state_code");
			location.displayAddress =  parseDisplayAddress(json.getJSONArray("display_address"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return location;

	}

}
