
package com.wheretoeat.adapters;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnTouchModeChangeListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wheretoeat.activities.R;
import com.wheretoeat.helper.SharedPrefHelper;
import com.wheretoeat.helper.Utility;
import com.wheretoeat.models.Restaurant;

public class RestaurantsAdpater extends ArrayAdapter<Restaurant> implements
        OnTouchListener {
    private static final String TAG = "RestaurantsAdpater";
    private Context context;
    List<Restaurant> restaurants;
    ImageView imgRestaurant;
    TextView tvNumbers;
    TextView tvName;
    TextView tvInfo;
    TextView tvMiles;
    TextView tvRatings;

    public RestaurantsAdpater(Context context, List<Restaurant> restaurants) {
        super(context, 0, restaurants);
        this.context = context;
        this.restaurants = restaurants;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_restaurant_item, null);
        }
        initViews(view);
        tvNumbers.setText(position + 1 + "");
        tvName.setText(restaurants.get(position).getName());
        tvInfo.setText(restaurants.get(position).getCategories());
        tvRatings.setText(restaurants.get(position).getRating() + "/5");
        Log.d(TAG, restaurants.get(position).getResId() + "");
        view.setTag(R.string.RES_REF_KEY, restaurants.get(position).getResRef());
        view.setTag(R.string.RES_ID_KEY, restaurants.get(position).getResId());
        view.setTag(R.string.RES_LOCATION_KEY, restaurants.get(position).getLocation());

        double[] location2 = restaurants.get(position).getLocation();
        double[] currentLocation = SharedPrefHelper.getLastKnownLocationPrefs(context);
        if (location2 != null && currentLocation != null && currentLocation.length > 0
                && location2.length > 0) {
            double distance = Utility.distance(currentLocation[0], currentLocation[1],
                    location2[0], location2[1]);
            tvMiles.setText(distance + " mi");
        }

        return view;
    }

    private void initViews(View view) {
        tvNumbers = (TextView) view.findViewById(R.id.tv_numbers);
        tvName = (TextView) view.findViewById(R.id.tv_name);
        tvMiles = (TextView) view.findViewById(R.id.tv_miles);
        tvInfo = (TextView) view.findViewById(R.id.tv_body);
        tvRatings = (TextView) view.findViewById(R.id.tv_ratings);
    }

    @Override
    public int getCount() {
        return restaurants.size();
    }

    @Override
    public Restaurant getItem(int position) {
        return restaurants.get(position);
    }

    @Override
    public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
        return false;
    }


}
