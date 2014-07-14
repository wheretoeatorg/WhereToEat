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

import java.util.List;

public class CategoriesFragment extends Fragment {

    protected static final String TAG = "CategoriesFragment";
    private ListView listView;
    private List<String> categories;
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
        View footerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.categories_list_item_btn, null, false);
        Button btnNewCategory = (Button) footerView.findViewById(R.id.btn_new_category);
        btnNewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategoryPopup(ResponseType.VALID);
            }
        });
        categories = SharedPrefHelper.getCategoriesPrefs(getActivity());
        adapter = new CategoriesAdapter(getActivity(), categories);
        listView.addFooterView(footerView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                callBackHandler.onCategorySelected(item);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                dialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String category = categories.get(position);
                        SharedPrefHelper.removeCategoriesPrefsSet(getActivity(), category);
                        adapter.remove(category);
                    }
                });
                dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });
                dialogBuilder.show();
                return false;
            }
        });

        return view;
    }

    public class CategoriesAdapter extends ArrayAdapter<String> {

        private List<String> adaptorCategories;

        public CategoriesAdapter(Context context, List<String> categoriesList) {
            super(context, 0, categoriesList);
            this.adaptorCategories = categoriesList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = mInflater.inflate(R.layout.categories_list_item, parent, false);
            TextView tvCat = (TextView) convertView.findViewById(R.id.tv_categories);
            tvCat.setText(adaptorCategories.get(position));
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
        switch (responseType) {
            case ALREADY_EXIST:
                lblErrorView.setVisibility(View.VISIBLE);
                lblErrorView.setText("Category already exist,Try another?");
                break;
            case EMPTY:
                lblErrorView.setVisibility(View.VISIBLE);
                lblErrorView.setText("Category type can't be empty.");
                break;
            case VALID:
                lblErrorView.setVisibility(View.GONE);
                break;
        }

        dialogBuilder.setView(dialogView);
        dialogBuilder.setPositiveButton(R.string.save, newCategoryDialog);
        dialogBuilder.setNegativeButton(R.string.cancle, newCategoryDialog);
        dialogBuilder.create().show();
    }

    public DialogInterface.OnClickListener newCategoryDialog = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                // Save Button
                case -1:
                    String newCategory = etNewCategory.getText().toString();
                    ResponseType responseType = validateCategory(newCategory);
                    if (responseType != ResponseType.VALID) {
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
            for (String category : categories) {
                if (category.equalsIgnoreCase(newCategory)) {
                    contains = true;
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
