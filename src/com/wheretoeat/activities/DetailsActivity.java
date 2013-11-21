package com.wheretoeat.activities;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.wheretoeat.adapters.ReviewsAdapter;
import com.wheretoeat.helper.Constants;
import com.wheretoeat.helper.GoogleMapHelper;
import com.wheretoeat.helper.Utility;
import com.wheretoeat.models.FavoriteRestaurant;
import com.wheretoeat.models.FilterOptions;
import com.wheretoeat.models.Restaurant;
import com.wheretoeat.models.Review;
import com.wheretoeat.restclients.PlacesClient;
import com.wheretoeat.restclients.RestClientApplication;

public class DetailsActivity extends Activity {

	private final String TAG = "DETAILS";
	private List<Review> reviews;
	private ReviewsAdapter adapter;
	private String phoneNumber;
	private String website;
	private String direction;
	private RatingBar reviewRatingBar;
	private TextView tvDetailviewRatings;
	private TextView tvReviewCount;
	private TextView tvCategories;
	private TextView tvRestaurantName;
	private TextView tvAddress;
	private ToggleButton tglBtnFav;
	String resRef;
	String resId;
	double[] coords;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		resRef = getIntent().getStringExtra(Constants.RES_REF);
		resId = getIntent().getStringExtra(Constants.RES_ID);
		coords = getIntent().getDoubleArrayExtra(Constants.RES_LOCATION);
		fetchDetails(resRef);
		reviews = new ArrayList<Review>();
		adapter = new ReviewsAdapter(this, reviews);
		ListView lvReviewsList = (ListView) findViewById(R.id.lvReviewsList);
		lvReviewsList.setAdapter(adapter);
		initViews();
		refreshFavSelected();
	}

	private void refreshFavSelected() {
		FavoriteRestaurant fav = new Select().from(FavoriteRestaurant.class).where("resId = ?", resId).executeSingle();
		if (fav != null) {
			tglBtnFav.setChecked(true);
		}
	}

	private void initViews() {
		tglBtnFav = (ToggleButton) findViewById(R.id.tglBtnFav);
		tvReviewCount = (TextView) findViewById(R.id.tvReviewCount);
		tvCategories = (TextView) findViewById(R.id.tvCategories);
		tvRestaurantName = (TextView) findViewById(R.id.tvRestaurantName);
		tvAddress = (TextView) findViewById(R.id.tvAddress);
		reviewRatingBar = (RatingBar) findViewById(R.id.ivRating);
		tvDetailviewRatings = (TextView) findViewById(R.id.tvDetailviewRatings);
	}

	private void setupRestaurant(Restaurant rest) {
		if (rest == null) {
			return;
		}
		adapter.clear();
		if (rest.getReviews() != null) {
			adapter.addAll(rest.getReviews());
		}

		tvRestaurantName.setText(rest.getName());
		tvCategories.setText(rest.getCategories());
		phoneNumber = rest.getPhoneNumber();
		tvAddress.setText(rest.getAddress());
		direction = rest.getAddress();
		website = rest.getResUrl();
		float rat = 0.0f;
		try {
			rat = Float.parseFloat(rest.getRating());
		} catch (Exception e) {
			// do nothing
		}

		reviewRatingBar.setRating(rat);
		tvDetailviewRatings.setText(rest.getRating());
		int reviewCount = 0;
		if (rest.getReviews() != null) {
			reviewCount = rest.getReviews().size();
		}

		tvReviewCount.setText(reviewCount + " Reviews");
	}

	public void onClickFavorite(View v) {
		boolean isChecked = tglBtnFav.isChecked();
		if (isChecked) {
			FavoriteRestaurant fav = new FavoriteRestaurant();
			fav.setName(tvRestaurantName.getText().toString());
			fav.setResRef(resRef);
			fav.setResId(resId);
			fav.setRatings(reviewRatingBar.getRating());
			fav.setDistanceMiles(1.0);
			fav.setLattitude(coords[0]);
			fav.setLongitude(coords[1]);
			fav.setCategories(tvCategories.getText().toString());
			fav.save();
		} else {
			new Delete().from(FavoriteRestaurant.class).where("resId=?", resId).execute();
			List<FavoriteRestaurant> list = new Select().from(FavoriteRestaurant.class).execute();
			Log.d(TAG, list.toString());
			Toast.makeText(DetailsActivity.this, "Not Checked !", Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.details, menu);
		return true;
	}

	private void fetchDetails(String ref) {
		FilterOptions filOpt = new FilterOptions();
		filOpt.setReference(ref);

		PlacesClient client = RestClientApplication.getPlacesClient();
		client.getDetails(filOpt, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject response) {
				// Log.d(TAG, "Places response = " + response);
				Restaurant r = null;
				try {
					r = Restaurant.fromJson(response);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				setupRestaurant(r);
			}

			@Override
			public void onFailure(Throwable t) {
				Log.d(TAG, "Places Failure = " + t.getMessage());
			}
		});
	}

	// phone call intent, initiate the call.
	public void callNumber(View v) {
		Intent callIntent = new Intent(Intent.ACTION_CALL);
		callIntent.setData(Uri.parse("tel:" + phoneNumber));
		startActivity(callIntent);
	}

	// website intent
	public void openSite(View v) {
		if (!Utility.isStringBlank(website)) {
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
			startActivity(browserIntent);
		} else {
			Toast.makeText(DetailsActivity.this, "website not available", Toast.LENGTH_SHORT).show();
		}

	}

	// direction intent
	public void getMap(View v) {
		double[] coordinates = GoogleMapHelper.getCurrentlocation(getBaseContext());
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		String data = String.format("geo:%s,%s", 37.7764, -122.417);
		int zoomLevel = 15;

		data = String.format("%s?z=%s", data, zoomLevel);

		intent.setData(Uri.parse(data));
		startActivity(intent);
	}

	public void launchGoogleMaps(View v) {
		// "geo:0,0?q="+direction;
		String format = String.format("geo:0,0?q=%s", direction);
		Uri uri = Uri.parse(format);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

}
