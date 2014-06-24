
package com.wheretoeat.activities;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
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
    private ImageButton imgBtnGetDirection;
    private ImageButton imgBtnSeeWebSite;
    private ImageButton imgBtnCall;
    private String resRef;
    private String resId;
    private double[] coords;
    private View headerView = null;
    private PopupWindow popWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        // Must be call before calling setContentView().
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_details);

        resRef = getIntent().getStringExtra(Constants.RES_REF);
        resId = getIntent().getStringExtra(Constants.RES_ID);
        coords = getIntent().getDoubleArrayExtra(Constants.RES_LOCATION);

        fetchDetails(resRef);

        initViews();

        initHeaderView();

        reviews = new ArrayList<Review>();
        adapter = new ReviewsAdapter(this, reviews);
        ListView lvReviewsList = (ListView) findViewById(R.id.lvReviewsList);
        lvReviewsList.addHeaderView(headerView);
        lvReviewsList.setAdapter(adapter);
        refreshFavSelected();
        lvReviewsList.setOnItemClickListener(itemClickListener);
    }

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(TAG, "itemClicked = " + position);
            Log.d(TAG, "itemClicked = " + position);
            if (reviews.size() >= position) {
                Log.d(TAG, "Review  = " + reviews.get(position - 1).getRatingText());
                onShowPopup(parent, position - 1);
            }

        }
    };

    private void refreshFavSelected() {
        Log.d(TAG, "refreshFavSelected()");
        FavoriteRestaurant fav = new Select().from(FavoriteRestaurant.class)
                .where("resId = ?", resId).executeSingle();
        if (fav != null) {
            tglBtnFav.setChecked(true);
        }
    }

    private void initViews() {
        Log.d(TAG, "initViews()");
        tvCategories = (TextView) findViewById(R.id.tvCategories);
        tvRestaurantName = (TextView) findViewById(R.id.tvRestaurantName);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
    }

    private void initHeaderView() {
        Log.d(TAG, "initHeaderViews()");
        headerView = getLayoutInflater().inflate(R.layout.details_list_header_view, null);
        tglBtnFav = (ToggleButton) headerView.findViewById(R.id.tglBtnFav);
        tvReviewCount = (TextView) headerView.findViewById(R.id.tvReviewCount);
        reviewRatingBar = (RatingBar) headerView.findViewById(R.id.ivRating);
        tvDetailviewRatings = (TextView) headerView.findViewById(R.id.tvDetailviewRatings);
        imgBtnSeeWebSite = (ImageButton) headerView.findViewById(R.id.imgBtnSeeWebSite);
        imgBtnCall = (ImageButton) headerView.findViewById(R.id.imgBtnCall);
        imgBtnGetDirection = (ImageButton) headerView.findViewById(R.id.imgBtnGetDirection);
    }

    private void setupRestaurant(Restaurant rest) {
        Log.d(TAG, "setupRestaurant()");
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

    // call this method when required to show popup
    public void onShowPopup(View v, int listItemIndex) {

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(DetailsActivity.LAYOUT_INFLATER_SERVICE);

        // inflate the custom popup layout
        final View inflatedView = layoutInflater.inflate(R.layout.custom_dialog, null, false);
        // find the ListView in the popup layout
        //ListView listView = (ListView) inflatedView.findViewById(R.id.commentsListView);
        TextView tvUserRatings = (TextView) inflatedView.findViewById(R.id.tv_user_ratings);
        TextView tvUserName = (TextView) inflatedView.findViewById(R.id.tv_user_name);
        tvUserName.setText(reviews.get(listItemIndex).getAuthor());
        tvUserRatings.setText(reviews.get(listItemIndex).getRatingText());

        // get device size
        Display display = getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);
        //mDeviceHeight = size.y;


        // fill the data to the list items
        //setSimpleList(listView);


        // set height depends on the device size
        popWindow = new PopupWindow(inflatedView, (int) (size.x * 0.90), (int) (size.y * 0.50), true);
        // set a background drawable with rounders corners
        popWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.white_background));
        // make it focusable to show the keyboard to enter in `EditText`
        //popWindow.setFocusable(false);
        // make it outside touchable to dismiss the popup window
        popWindow.setOutsideTouchable(true);

        // show the popup at bottom of the screen and set some margin at bottom ie,
        popWindow.showAtLocation(v, Gravity.CENTER, 0, 200);
    }

    public void onClickFavorite(View v) {
        Log.d(TAG, "onClickFavorite()");
        boolean isChecked = tglBtnFav.isChecked();
        if (isChecked) {
            if (TextUtils.isEmpty(tvRestaurantName.getText().toString())) {
                tglBtnFav.setChecked(false);
                Toast.makeText(DetailsActivity.this,
                        "Please Wait until data is available and then try",
                        Toast.LENGTH_SHORT).show();
                return;
            }
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
            // List<FavoriteRestaurant> list = new
            // Select().from(FavoriteRestaurant.class).execute();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu()");
        // getMenuInflater().inflate(R.menu.details, menu);
        return true;
    }

    private void fetchDetails(String ref) {
        Log.d(TAG, "fetchDetails()");
        FilterOptions filOpt = new FilterOptions();
        filOpt.setReference(ref);

        PlacesClient client = RestClientApplication.getPlacesClient();
        setProgressBarIndeterminateVisibility(true);
        client.getDetails(filOpt, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                Restaurant r = null;
                try {
                    r = Restaurant.fromJson(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setupRestaurant(r);
                setProgressBarIndeterminateVisibility(false);
                enableButtons();
            }

            private void enableButtons() {
                tglBtnFav.setClickable(true);

            }

            @Override
            public void onFailure(Throwable t) {
                setProgressBarIndeterminateVisibility(false);
                Toast.makeText(DetailsActivity.this, "Restaurants details Not available",
                        Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure() : " + t);
            }
        });
    }

    // phone call intent, initiate the call.
    public void callNumber(View v) {
        Log.d(TAG, "callNumber()");
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(callIntent);
    }

    // website intent
    public void openSite(View v) {
        Log.d(TAG, "openSite()");
        if (!Utility.isStringBlank(website)) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
            startActivity(browserIntent);
        } else {
            Toast.makeText(DetailsActivity.this, R.string.website_information_is_not_available_,
                    Toast.LENGTH_SHORT).show();
        }

    }

    // direction intent
    public void getMap(View v) {
        Log.d(TAG, "getMap()");
        double[] coordinates = GoogleMapHelper.getCurrentlocation(getBaseContext());
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        String data = String.format("geo:%s,%s", coordinates[0], coordinates[1]);
        int zoomLevel = 15;
        data = String.format("%s?z=%s", data, zoomLevel);
        intent.setData(Uri.parse(data));
        startActivity(intent);
    }

    public void launchGoogleMaps(View v) {
        Log.d(TAG, "launchGoogleMaps()");
        String format = String.format("geo:0,0?q=%s", direction);
        Uri uri = Uri.parse(format);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
