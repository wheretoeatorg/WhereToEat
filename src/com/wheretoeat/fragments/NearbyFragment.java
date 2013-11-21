package com.wheretoeat.fragments;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.wheretoeat.activities.R;
import com.wheretoeat.adapters.RestaurantsAdpater;
import com.wheretoeat.helper.GoogleMapHelper;
import com.wheretoeat.helper.SharedPrefHelper;
import com.wheretoeat.helper.Utility;
import com.wheretoeat.models.FilterOptions;
import com.wheretoeat.models.FilterOptions.Rankby;
import com.wheretoeat.models.Restaurant;
import com.wheretoeat.restclients.PlacesClient;
import com.wheretoeat.restclients.RestClientApplication;
import com.wheretoeat.restclients.YelpClient;

public class NearbyFragment extends Fragment {

	private static final String TAG = "NearbyFragment";

	private List<Restaurant> resList;
	private ListView listView;
	private RestaurantsAdpater adapter;
	private OnMapUpdateListener callBackHandler;
	private View nearbyFragView;

	public interface OnMapUpdateListener {
		public void onMapUpdate(List<Restaurant> resList);

		public void onDetailSelected(String resRef, String resId, double[] coords);

		public void onCategorySelected(String category);
	}

	@Override
	public void onAttach(Activity activity) {
		Log.d(TAG, "OnAttach");
		super.onAttach(activity);
		if (activity instanceof OnMapUpdateListener) {
			callBackHandler = (OnMapUpdateListener) activity;
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.d(TAG, "onActivityCreated");
		super.onActivityCreated(savedInstanceState);
		resList = new ArrayList<Restaurant>();
		adapter = new RestaurantsAdpater(getActivity(), resList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				if (view.getTag(R.string.RES_REF_KEY) != null && view.getTag(R.string.RES_ID_KEY) != null) {
					String resRef = view.getTag(R.string.RES_REF_KEY).toString();
					String resId = view.getTag(R.string.RES_ID_KEY).toString();
					double[] coords = (double[]) view.getTag(R.string.RES_LOCATION_KEY);
					callBackHandler.onDetailSelected(resRef, resId, coords);
				}

			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		nearbyFragView = inflater.inflate(R.layout.fragment_nearby, container, false);
		nearbyFragView.setTag("nearby");
		initViews(nearbyFragView);
		return nearbyFragView;
	}

	private void initViews(View nearbyFragView) {
		listView = (ListView) nearbyFragView.findViewById(R.id.lv_nearby_res);
	}

	@Override
	public void onStart() {
		Log.d(TAG, "onStart");
		super.onStart();
		searchPlacesApi();
	}

	private void searchYelpApi() {
		double[] coords = GoogleMapHelper.getCurrentlocation(getActivity());
		YelpClient client = RestClientApplication.getYelpClient();
		client.searchRestaurants("restaurants", "1", coords[0], coords[1], new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(JSONObject res) {
				Log.d(TAG, "JSONObject: " + res.toString());
				List<Restaurant> restauList = Restaurant.fromJSON(res);
				Log.d(TAG, "resList size: " + resList.size());
				adapter.addAll(restauList);
			}

			@Override
			public void onFailure(Throwable t) {
				Log.e(TAG, "Erros - " + t.getMessage());
			}
		});
	}

	public void searchPlacesApi() {
		if (!Utility.isNetworkAvailable(getActivity())) {
			return;
		}
		Log.d(TAG, "NearbyFragmentSearchApi");
		String searchName = SharedPrefHelper.getSearchName(getActivity());
		double[] coords = GoogleMapHelper.getCurrentlocation(getActivity());
		PlacesClient client = RestClientApplication.getPlacesClient();
		FilterOptions filterOptions = new FilterOptions();
		filterOptions.setType("restaurant");
		filterOptions.setRankby(Rankby.distance);
		filterOptions.setLatitude(coords[0]);
		filterOptions.setLongitude(coords[1]);
		filterOptions.setSensor(false);
		if (!searchName.equalsIgnoreCase(SharedPrefHelper.NONE)) {
			filterOptions.setName(searchName);
		}
		
		client.searchRestaurants(filterOptions, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject response) {
				Log.d(TAG, "Places response = " + response);
				adapter.clear();
				resList.clear();
				resList = Restaurant.fromPlacesJSON(response);
				adapter.addAll(resList);
				// callBackHandler.onMapUpdate(resList);
			}

			@Override
			public void onFailure(Throwable t) {
				Log.d(TAG, "Places Failure = " + t.getMessage());
			}
		});
	}

	public List<Restaurant> getResList() {
		return resList;
	}

}
