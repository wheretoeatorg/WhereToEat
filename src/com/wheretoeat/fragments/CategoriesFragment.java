
package com.wheretoeat.fragments;

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

import com.wheretoeat.activities.R;
import com.wheretoeat.fragments.NearbyFragment.OnMapUpdateListener;

public class CategoriesFragment extends Fragment {

    protected static final String TAG = "CategoriesFragment";
    private ListView listView;

    private OnMapUpdateListener callBackHandler;

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
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        listView = (ListView) view.findViewById(R.id.lv_categories);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                callBackHandler.onCategorySelected(item);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated()");
        super.onActivityCreated(savedInstanceState);

    }

    public int getScrollY() {
        if (listView != null && listView.getChildAt(0) != null) {
            return listView.getChildAt(0).getTop();
        }
        return -1;
    }

}
