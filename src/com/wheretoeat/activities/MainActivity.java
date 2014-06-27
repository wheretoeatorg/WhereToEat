
package com.wheretoeat.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.avgtechie.slidinguppanel.SlidingBottomUpLayout;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.wheretoeat.adapters.SectionPagerAdapter;
import com.wheretoeat.fragments.CategoriesFragment;
import com.wheretoeat.fragments.FavoritesFragment;
import com.wheretoeat.fragments.NearbyFragment;
import com.wheretoeat.fragments.NearbyFragment.OnMapUpdateListener;
import com.wheretoeat.fragments.TopRatedFragment;
import com.wheretoeat.helper.Constants;
import com.wheretoeat.helper.GoogleMapHelper;
import com.wheretoeat.helper.SharedPrefHelper;
import com.wheretoeat.helper.Utility;
import com.wheretoeat.models.Filters;
import com.wheretoeat.models.Restaurant;

public class MainActivity extends FragmentActivity implements OnMapUpdateListener {

    private static final String TAG = "MainFragmentActivity";
    private static int DETAILS_REQUEST_CODE = 1;
    final static int ZOOM_LEVEL = 12;
    private SectionPagerAdapter sectionPagerAdapter;
    private ViewPager viewPager;
    private GoogleMap googleMap;
    private SupportMapFragment supportMapFragment;
    private PagerTabStrip page;
    private View dialogView;
    // Dialog Views
    private ToggleButton price1;
    private ToggleButton price2;
    private ToggleButton price3;
    private ToggleButton price4;
    // Switch swtchShowVisited;
    private Switch swtchOpenNow;
    // Layouts
    private FrameLayout pagerFrameLayout;
    private FrameLayout mapFrameLayout;

    private ListView listView;
    private SlidingBottomUpLayout layout;
    private List<Restaurant> globalResList = new ArrayList<Restaurant>();
    private List<Marker> markerList;
    MenuItem refreshItem;
    MenuItem filterItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        markerList = new ArrayList<Marker>();

        // create Pager Adapter.
        sectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        // get ViewPager.
        viewPager = (ViewPager) findViewById(R.id.viewPagerCategory);
        page = (PagerTabStrip) findViewById(R.id.pager_title_strip);
        page.setTabIndicatorColor(getResources().getColor(R.color.holo_blue_light));
        // Set Listener for ViewPager
        viewPager.setOnPageChangeListener(pageChangeListener);
        // Set Adapter on ViewPager
        viewPager.setAdapter(sectionPagerAdapter);
        viewPager.setCurrentItem(1);

        // Initialize layouts
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        googleMap = supportMapFragment.getMap();

        double[] currentCoordinates = GoogleMapHelper.getCurrentlocation(MainActivity.this);
        if (currentCoordinates.length == 0) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(R.string.couldnot_find_your_current_location_);
            dialog.setPositiveButton("OK", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Positive button clicked.
                }
            });
            currentCoordinates = SharedPrefHelper.getLastKnownLocationPrefs(MainActivity.this);
        } else {
            SharedPrefHelper.setLastKnownLocation(currentCoordinates, MainActivity.this);
        }

        SharedPrefHelper.addSearchName(MainActivity.this, SharedPrefHelper.NONE);
        LatLng currentLocation = new LatLng(currentCoordinates[0], currentCoordinates[1]);
        if (googleMap != null) {
            googleMap.setMyLocationEnabled(true);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, ZOOM_LEVEL));
            googleMap.getUiSettings().setZoomControlsEnabled(false);
            googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    LatLng latLng = marker.getPosition();
                    String id = marker.getId();
                    Log.d(TAG, "id = " + id);
                    int index = -1;
                    for (int i = 0; i < markerList.size(); i++) {
                        if (markerList.get(i).getId().equalsIgnoreCase(marker.getId())) {
                            index = i;
                            break;
                        }
                    }
                    if (globalResList.size() > index) {
                        double[] coord = new double[]{latLng.latitude, latLng.longitude};
                        onDetailSelected(globalResList.get(index).getResRef(), globalResList.get(index).getResId(), coord);
                    }
                }
            });
        }
        layout = (SlidingBottomUpLayout) findViewById(R.id.sliding_layout);
        layout.setShadowDrawable(getResources().getDrawable(R.drawable.above_shadow));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu()");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        filterItem = menu.findItem(R.id.action_filter);
        refreshItem = menu.findItem(R.id.action_refresh);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected()");
        if (item.getItemId() == R.id.action_filter) {
            showFilterDialog();
        } else if (item.getItemId() == R.id.action_refresh) {
            onClickedPageRefresh();
        }
        return true;
    }

    // display dialog box
    private void showFilterDialog() {
        Log.d(TAG, "showFilterDialog()");
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dialogView = inflater.inflate(R.layout.dialog_filter, null, true);
        initDialogView();
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Set the Filter");
        dialogBuilder.setPositiveButton(R.string.save, dialogOnClickListener);
        dialogBuilder.setNegativeButton(R.string.cancle, dialogOnClickListener);
        dialogBuilder.create().show();
    }

    // Initialize the views of dialog
    private void initDialogView() {
        Log.d(TAG, "initDialogView()");
        price1 = (ToggleButton) dialogView.findViewById(R.id.price1);
        price2 = (ToggleButton) dialogView.findViewById(R.id.price2);
        price3 = (ToggleButton) dialogView.findViewById(R.id.price3);
        price4 = (ToggleButton) dialogView.findViewById(R.id.price4);
        swtchOpenNow = (Switch) dialogView.findViewById(R.id.swtch_open_now);
        populateDialotValues();
    }

    // Populate the values in dialogbox stored from SharedPrefs
    private void populateDialotValues() {
        Log.d(TAG, "populateDialotValues()");
        price1.setChecked(SharedPrefHelper.getPrice1Pref(MainActivity.this));
        price2.setChecked(SharedPrefHelper.getPrice2Pref(MainActivity.this));
        price3.setChecked(SharedPrefHelper.getPrice3Pref(MainActivity.this));
        price4.setChecked(SharedPrefHelper.getPrice4Pref(MainActivity.this));
        swtchOpenNow.setChecked(SharedPrefHelper.getOpenNowPref(MainActivity.this));

    }

    // Dialog button click listener
    OnClickListener dialogOnClickListener = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Log.d(TAG, "onClick()");

            switch (which) {
                // Save button
                case -1:
                    Filters filters = new Filters();

                    filters.setPrice1(price1.isChecked());
                    filters.setPrice2(price2.isChecked());
                    filters.setPrice3(price3.isChecked());
                    filters.setPrice4(price4.isChecked());
                    filters.setOpenNow(swtchOpenNow.isChecked());
                    SharedPrefHelper.AddFiltersSharedPrefs(filters, MainActivity.this);

                    break;
                // Cancel Button
                case -2:
                    break;
                default:
                    break;
            }
        }
    };

    private String getBtnTag(ImageButton btn) {
        if (btn != null && btn.getTag() != null) {
            return btn.getTag().toString();
        }
        return "out";
    }

    OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            Log.d(TAG, "onPageSelected()");

            if (!Utility.isNetworkAvailable(MainActivity.this)) {
                Toast.makeText(MainActivity.this, R.string.network_is_not_available_,
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (listView != null) {
                listView.setOnScrollListener(null);
            }
            Fragment frag = sectionPagerAdapter.getItem(position);
            List<Restaurant> resList = null;
            if (frag instanceof NearbyFragment) {
                showMenuItems();
                resList = ((NearbyFragment) frag).getResList();
                Log.d(TAG, "NearbyFragment onPageSelected()");
                listView = ((NearbyFragment) frag).getListView();
                onMapUpdate(resList);
            } else if (frag instanceof TopRatedFragment) {
                showMenuItems();
                Log.d(TAG, "TopRatedFragment onPageSelected()");
                resList = ((TopRatedFragment) frag).getResList();
                listView = ((TopRatedFragment) frag).getListView();
                onMapUpdate(resList);
            } else if (frag instanceof FavoritesFragment) {
                hideMenuItems();
                Log.d(TAG, "FavoritesFragment onPageSelected()");
                resList = ((FavoritesFragment) frag).getResList();
                listView = ((FavoritesFragment) frag).getListView();
                onMapUpdate(resList);
            } else if (frag instanceof CategoriesFragment) {
                hideMenuItems();
            }


            if (listView != null) {
                Log.d(TAG, "setupListViewScrollListener call onPageSelected()");
                setupListViewScrollListener(listView);
            }

        }

        private void showMenuItems() {
            if (refreshItem != null) {
                refreshItem.setVisible(true);
            }
            if (filterItem != null) {
                filterItem.setVisible(true);
            }
            //invalidateOptionsMenu();
        }

        private void hideMenuItems() {
            if (refreshItem != null) {
                refreshItem.setVisible(false);
            }
            if (filterItem != null) {
                filterItem.setVisible(false);
            }
            //invalidateOptionsMenu();
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private int getScrollY(ListView lv) {
        if (lv != null && lv.getChildAt(0) != null) {
            return lv.getChildAt(0).getTop();
        }
        return -1;
    }

    @Override
    public void onMapUpdate(List<Restaurant> resList) {
        globalResList = resList;

        Log.d(TAG, "onMapUpdate()");
        if (googleMap != null) {
            GoogleMapHelper.clearAllMarkers(googleMap);
            markerList.clear();
            double[] currentCoordinates = GoogleMapHelper.getCurrentlocation(MainActivity.this);
            GoogleMapHelper.markLocationOnMap(currentCoordinates, googleMap, "This is You", this, 0);
        }

        if (resList != null && resList.size() > 0) {
            int counter = 1;
            for (Restaurant restaurant : resList) {
                double[] coordinates = restaurant.getLocation();
                if (!TextUtils.isEmpty(restaurant.getName()) && coordinates != null && coordinates.length > 0) {
                    Marker marker = GoogleMapHelper.markLocationOnMap(coordinates, googleMap, restaurant.getName(), this, counter);
                    markerList.add(marker);
                }
                counter++;
            }
        }
    }

    @Override
    public void onDetailSelected(String resRef, String resId, double[] coords) {
        Log.d(TAG, "onDetailSelected()");
        Intent i = new Intent(this, DetailsActivity.class);
        i.putExtra(Constants.RES_REF, resRef);
        i.putExtra(Constants.RES_ID, resId);
        i.putExtra(Constants.RES_LOCATION, coords);
        startActivityForResult(i, DETAILS_REQUEST_CODE);
    }

    @Override
    public void onCategorySelected(String category) {
        Log.d(TAG, "onCategorySelected()");
        SharedPrefHelper.addSearchName(MainActivity.this, category);
        getActionBar().setTitle(category);
        viewPager.setCurrentItem(1);
        Fragment frag = sectionPagerAdapter.getItem(1);
        if (frag instanceof NearbyFragment) {
            ((NearbyFragment) frag).searchPlacesApi();
        }
    }

    public void onClickedPageRefresh() {
        Log.d(TAG, "onClickedPageRefresh()");
        int position = viewPager.getCurrentItem();
        Fragment frag = sectionPagerAdapter.getItem(position);
        String category = getActionBar().getTitle().toString();
        if (!category.equalsIgnoreCase("WhereToEat") && !category.equalsIgnoreCase("ALL")) {
            SharedPrefHelper.addSearchName(MainActivity.this, category);
        } else {
            SharedPrefHelper.addSearchName(MainActivity.this, "");
        }
        if (frag instanceof NearbyFragment) {
            ((NearbyFragment) frag).searchPlacesApi();
        } else if (frag instanceof TopRatedFragment) {
            ((TopRatedFragment) frag).searchPlacesApi();
        }
    }

    public FrameLayout getPagerFrameLayout() {
        return pagerFrameLayout;
    }

    public FrameLayout getMapFrameLayout() {
        return mapFrameLayout;
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    @Override
    public void setupListViewScrollListener(final ListView lv) {
        Log.d(TAG, "setupListViewScrollListener()");

        lv.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int
                    visibleItemCount,
                                 int totalItemCount) {
                int topY = getScrollY(lv);
                if (topY == 0) {
                    Log.d(TAG, "onScroll if" + topY);
                    layout.setListViewFirstRowVisible(true);
                } else {
                    Log.d(TAG, "onScroll else" + topY);
                    layout.setListViewFirstRowVisible(false);
                }

            }
        });
    }

    public int getScrollY() {
        if (listView != null && listView.getChildAt(0) != null) {
            return listView.getChildAt(0).getTop();
        }
        return -1;
    }

}
