package com.wheretoeat.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.wheretoeat.activities.R;
import com.wheretoeat.adapters.RestaurantsAdpater;
import com.wheretoeat.models.FavoriteRestaurant;
import com.wheretoeat.models.Restaurant;

public class FavoritesFragment extends Fragment {

	private static final String TAG = "FavoritesFragment";
	ListView lvFavRests;
	List<FavoriteRestaurant> listFav;
	RestaurantsAdpater adapter;
	View dialogView;
	List<Restaurant> resList = new ArrayList<Restaurant>();
	EditText etNotes;
	TextView tvResName;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView()");
		View view = inflater.inflate(R.layout.fragment_favorite, container, false);
		lvFavRests = (ListView) view.findViewById(R.id.lv_fav_res);
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.d(TAG, "onStart()");
		resList.clear();
		resList = getFavRestaurants();

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d(TAG, "onActivityCreated()");
		adapter = new RestaurantsAdpater(getActivity(), getFavRestaurants());
		lvFavRests.setAdapter(adapter);
		lvFavRests.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String resId = view.getTag(R.string.RES_ID_KEY).toString();
				Restaurant res = resList.get(position);
				showFilterDialog();
				tvResName.setText(res.getName());
			}
		});

		lvFavRests.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				Restaurant rest = resList.get(position);
				removeFavRestaurant(rest);
				resList.remove(position);
				adapter.notifyDataSetChanged();
				return false;
			}

		});
	}

	public List<Restaurant> getFavRestaurants() {
		Log.d(TAG, "getFavRestaurants()");
		List<FavoriteRestaurant> favList = new Select().from(FavoriteRestaurant.class).execute();
		resList.clear();
		for (FavoriteRestaurant fav : favList) {
			Restaurant res = new Restaurant();
			res.setName(fav.getName());
			res.setDistanceMiles(1.0 + "");
			res.setResId(fav.getResId());
			res.setResRef(fav.getResRef());
			res.setCategories(fav.getCategories());
			double[] coords = new double[2];
			coords[0] = fav.getLattitude();
			coords[1] = fav.getLongitude();
			res.setLocation(coords);
			res.setRating(String.valueOf(fav.getRatings()));
			resList.add(res);
		}
		return resList;
	}

	public void removeFavRestaurant(Restaurant rest) {
		Log.d(TAG, "removeFavRestaurant()");
		if (rest != null && !rest.getResId().isEmpty()) {
			new Delete().from(FavoriteRestaurant.class).where("resId=?", rest.getResId()).execute();
		}
	}

	public List<Restaurant> getResList() {
		return resList;
	}

	// display dialog box
	private void showFilterDialog() {
		Log.d(TAG, "showFilterDialog()");
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		dialogView = inflater.inflate(R.layout.dialog_restaurants_notes, null, true);
		etNotes = (EditText) dialogView.findViewById(R.id.et_notes);
		tvResName = (TextView) dialogView.findViewById(R.id.lbl_res_name);
		dialogBuilder.setView(dialogView);
		dialogBuilder.setPositiveButton(R.string.save, dialogOnClickListener);
		dialogBuilder.setNegativeButton(R.string.cancle, dialogOnClickListener);
		dialogBuilder.create().show();
	}

	DialogInterface.OnClickListener dialogOnClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {

		}
	};
}
