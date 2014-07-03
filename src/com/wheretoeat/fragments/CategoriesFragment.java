package com.wheretoeat.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wheretoeat.activities.R;
import com.wheretoeat.fragments.NearbyFragment.OnMapUpdateListener;
import com.wheretoeat.helper.SharedPrefHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoriesFragment extends Fragment {

    protected static final String TAG = "CategoriesFragment";
    private ListView listView;
    private List<String> categories;

    private final int TOTAL_VIEW_TYPE = 2;
    private final int REG_LIST_ITEM = 0;
    private final int ADD_NEW_LIST_ITEM = 1;
    private LayoutInflater mInflater;
    ArrayAdapter<String> adapter;

    private OnMapUpdateListener callBackHandler;
    private EditText etNewCategory;

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
        mInflater = inflater;
        Log.d(TAG, "onCreateView()");
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        listView = (ListView) view.findViewById(R.id.lv_categories);
        categories = SharedPrefHelper.getCategoriesPrefs(getActivity());
        adapter = new CategoriesAdapter(getActivity(), categories);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                callBackHandler.onCategorySelected(item);
            }
        });

        return view;
    }

    public class CategoriesAdapter extends ArrayAdapter<String> {
        private Map<Integer, View> viewCache;
        private Integer TV_CATEGORY_VIEW = 101;
        private Integer BTN_CATEGORY_VIEW_ADD = 102;
        private List<String> adaptorCategories;

        public CategoriesAdapter(Context context, List<String> categoriesList) {
            super(context, 0, categoriesList);
            this.adaptorCategories=categoriesList;
            viewCache = new HashMap<Integer, View>();
        }

        @Override
        public int getViewTypeCount() {
            Log.d(TAG, "getViewTypeCount() ");
            return TOTAL_VIEW_TYPE;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == (adaptorCategories.size() - 1)) {
                return ADD_NEW_LIST_ITEM;
            } else {
                return REG_LIST_ITEM;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            int type = getItemViewType(position);
                switch (type) {
                    case ADD_NEW_LIST_ITEM:
                        convertView = mInflater.inflate(R.layout.categories_list_item_btn, parent, false);
                        Button btnNewCategory = (Button) convertView.findViewById(R.id.btn_new_category);
                        btnNewCategory.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showCategoryPopup(ResponseType.VALID);
                            }
                        });

                        break;
                    case REG_LIST_ITEM:
                        convertView = mInflater.inflate(R.layout.categories_list_item, parent, false);
                        TextView tvCat = (TextView) convertView.findViewById(R.id.tv_categories);
                        tvCat.setText(adaptorCategories.get(position));
                        break;
                }
            return convertView;
        }
    }

    private void showCategoryPopup(ResponseType responseType) {
        Log.d(TAG, "showFilterDialog()");
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_new_category, null, true);
        etNewCategory = (EditText) dialogView.findViewById(R.id.et_category);
        TextView lblErrorView = (TextView) dialogView.findViewById(R.id.lbl_error_type);
        switch (responseType){
            case ALREADY_EXIST:
                lblErrorView.setVisibility(View.VISIBLE);
                lblErrorView.setText("Restaurant already exist,Try another?");
                break;
            case EMPTY:
                lblErrorView.setVisibility(View.VISIBLE);
                lblErrorView.setText("Restaurant type can't be empty.");
                break;
            case VALID:
                lblErrorView.setVisibility(View.GONE);
                break;
        }

        dialogBuilder.setView(dialogView);
        dialogBuilder.setPositiveButton(R.string.save, dialogOnClickListener);
        dialogBuilder.setNegativeButton(R.string.cancle, dialogOnClickListener);
        dialogBuilder.create().show();
    }

    public DialogInterface.OnClickListener dialogOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                // Save Button
                case -1:
                    String newCategory = etNewCategory.getText().toString();
                    ResponseType responseType = validateCategory(newCategory);
                    if (responseType!=ResponseType.VALID) {
                        showCategoryPopup(responseType);
                    } else {
                        //Save in SharedPref here
                        SharedPrefHelper.saveCategoryPrefSet(getActivity(), newCategory);
                        adapter.add(newCategory);
                    }
                    break;

                // Cancel Button
                case -2:
                    Toast.makeText(getActivity(), "Cancel Clicked", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }

        private ResponseType validateCategory(String newCategory) {

            if (TextUtils.isEmpty(newCategory)) {
                return ResponseType.EMPTY;
            }
            List<String> categories = SharedPrefHelper.getCategoriesPrefs(getActivity());
            boolean contains = false;
            for(String category:categories){
                if(category.equalsIgnoreCase(newCategory)){
                    contains=true;
                    break;
                }
            }

            if (contains) {
                return ResponseType.ALREADY_EXIST;
            }

            return ResponseType.VALID;
        }


    };


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
