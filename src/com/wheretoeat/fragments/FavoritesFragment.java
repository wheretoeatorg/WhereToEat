
package com.wheretoeat.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.wheretoeat.activities.R;
import com.wheretoeat.adapters.RestaurantsAdpater;
import com.wheretoeat.fragments.NearbyFragment.OnMapUpdateListener;
import com.wheretoeat.models.FavoriteRestaurant;
import com.wheretoeat.models.Restaurant;

public class FavoritesFragment extends Fragment {

    private static final String TAG = "FavoritesFragment";
    private List<FavoriteRestaurant> listFav;
    private RestaurantsAdpater adapter;
    private View dialogView;
    private List<Restaurant> resList = new ArrayList<Restaurant>();
    private EditText etNotes;
    private TextView tvResName;

    private ListView listView;

    private OnMapUpdateListener callBackHandler;

    public ListView getListView() {
        return listView;
    }

    public void setListView(ListView listView) {
        this.listView = listView;
    }

    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "onAttach()");
        super.onAttach(activity);
        if (activity instanceof OnMapUpdateListener) {
            callBackHandler = (OnMapUpdateListener) activity;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        listView = (ListView) view.findViewById(R.id.lv_fav_res);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
        resList.clear();
        resList = getFavRestaurants();
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated()");
        adapter = new RestaurantsAdpater(getActivity(), getFavRestaurants());
        listView.setAdapter(adapter);
        registerForContextMenu(listView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.res_fav_menu_items, menu);
    }

    private int position;

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        position = (int) info.id;
        Restaurant rest = resList.get(position);

        switch (item.getItemId()) {
            case R.id.delete_item:
                removeFavRestaurant(rest);
                resList.remove(position);
                adapter.notifyDataSetChanged();
                return true;

            case R.id.details_item:
                return true;
            case R.id.take_notes_item:
                View listItem = listView.getChildAt(position);
                String resId = listItem.getTag(R.string.RES_ID_KEY).toString();
                Restaurant res = resList.get(position);
                showFilterDialog();
                tvResName.setText(res.getName());

                FavoriteRestaurant favRes = getFavRestaurant(resId);
                if (favRes != null) {
                    String note = favRes.getNote();
                    String restId = favRes.getResId();
                    etNotes.setTag(restId);
                    if (!TextUtils.isEmpty(note)) {
                        etNotes.setText(note);
                    }
                }
                return true;
            case R.id.share_item:
                return true;
        }
        return super.onContextItemSelected(item);
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
        if (rest != null && !TextUtils.isEmpty(rest.getResId())) {
            new Delete().from(FavoriteRestaurant.class).where("resId=?", rest.getResId()).execute();
        }
    }

    public FavoriteRestaurant getFavRestaurant(String restId) {
        Log.d(TAG, "getFavRestaurantNote()");
        if (!TextUtils.isEmpty(restId)) {
            FavoriteRestaurant favRest = new Select().from(FavoriteRestaurant.class)
                    .where("resId = ?", restId).executeSingle();
            return favRest;
        }
        return null;
    }

    public List<Restaurant> getResList() {
        return resList;
    }

    // display dialog box
    private void showFilterDialog() {
        Log.d(TAG, "showFilterDialog()");
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
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
            switch (which) {
                // Save Button
                case -1:
                    updateRestaurant(etNotes.getText(), etNotes.getTag());
                    break;
                // Cancel Button
                case -2:
                    break;
                default:
                    break;
            }
        }

        private void updateRestaurant(Editable text, Object favResId) {
            if (favResId != null && !TextUtils.isEmpty(text)) {
                FavoriteRestaurant favRes = getFavRestaurant(favResId.toString());
                favRes.setNote(text.toString());
                favRes.save();
            }
        }
    };

    public int getScrollY() {
        if (listView != null && listView.getChildAt(0) != null) {
            return listView.getChildAt(0).getTop();
        }
        return -1;
    }
}
