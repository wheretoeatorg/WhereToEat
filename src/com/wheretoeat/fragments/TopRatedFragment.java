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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.wheretoeat.activities.R;
import com.wheretoeat.adapters.RestaurantsAdpater;
import com.wheretoeat.fragments.NearbyFragment.OnMapUpdateListener;
import com.wheretoeat.helper.GoogleMapHelper;
import com.wheretoeat.helper.SharedPrefHelper;
import com.wheretoeat.helper.Utility;
import com.wheretoeat.models.FilterOptions;
import com.wheretoeat.models.Restaurant;
import com.wheretoeat.restclients.PlacesClient;
import com.wheretoeat.restclients.RestClientApplication;
import com.wheretoeat.restclients.YelpClient;

public class TopRatedFragment extends Fragment {

	private static final String TAG = "TopRatedFragment";
	private List<Restaurant> resList;
	private ListView listView;
	private RestaurantsAdpater adapter;
	private OnMapUpdateListener callBackHandler;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof OnMapUpdateListener) {
			callBackHandler = (OnMapUpdateListener) activity;
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		listView = (ListView) getActivity().findViewById(R.id.lv_toprated_res);
		resList = new ArrayList<Restaurant>();
		adapter = new RestaurantsAdpater(getActivity(), resList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				String resRef = view.getTag(R.string.RES_REF_KEY).toString();
				String resId = view.getTag(R.string.RES_ID_KEY).toString();
				double[] coords = (double[]) view.getTag(R.string.RES_LOCATION_KEY);
				callBackHandler.onDetailSelected(resRef, resId, coords);
			}
		});
	}

	@Override
	public void onStart() {
		super.onStart();
		searchPlacesApi();
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "TopRatedOnResume");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_toprated, container, false);
	}

	private void searchYelpApi() {
		double[] coords = GoogleMapHelper.getCurrentlocation(getActivity());
		YelpClient client = RestClientApplication.getYelpClient();
		client.searchRestaurants("restaurants", "2", coords[0], coords[1], new JsonHttpResponseHandler() {

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
		Log.d(TAG, "TopRatedFragmentSearchApi");
		String searchName = SharedPrefHelper.getSearchName(getActivity());
		double[] coords = GoogleMapHelper.getCurrentlocation(getActivity());
		PlacesClient client = RestClientApplication.getPlacesClient();
		FilterOptions filOpt = new FilterOptions();
		filOpt.setType("restaurant");
		filOpt.setLatitude(coords[0]);
		filOpt.setLongitude(coords[1]);
		filOpt.setRadius(5000);
		if (!searchName.equalsIgnoreCase(SharedPrefHelper.NONE)) {
			filOpt.setName(searchName);
		}

		client.searchRestaurants(filOpt, new JsonHttpResponseHandler() {
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
