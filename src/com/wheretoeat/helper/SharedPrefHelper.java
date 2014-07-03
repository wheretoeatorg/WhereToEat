package com.wheretoeat.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.wheretoeat.activities.R;
import com.wheretoeat.models.Filters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class SharedPrefHelper {

    private static final String DEFAULT_SHARED_PREFS = "default-shared-pref";
    private static final String PRICE1_KEY = "price1_key";
    private static final String PRICE2_KEY = "price2_key";
    private static final String PRICE3_KEY = "price3_key";
    private static final String PRICE4_KEY = "price4_key";
    private static final String OPEN_NOW_KEY = "open_now_key";
    private static final String SHOW_VISITED_KEY = "show_visited_key";
    private static final String LAST_LATITUDE_KEY = "latitude";
    private static final String LAST_LONGITUDE_KEY = "logitude";
    public static final String NONE = "none";
    private static final String SEARCH_NAME_KEY = "search_name_key";
    private static final String CATEGORIES_KEY = "categories";

    public static void AddFiltersSharedPrefs(Filters filters, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(DEFAULT_SHARED_PREFS, 0);
        Editor edit = prefs.edit();
        edit.putBoolean(PRICE1_KEY, filters.isPrice1());
        edit.putBoolean(PRICE2_KEY, filters.isPrice2());
        edit.putBoolean(PRICE3_KEY, filters.isPrice3());
        edit.putBoolean(PRICE4_KEY, filters.isPrice4());
        edit.putBoolean(OPEN_NOW_KEY, filters.isOpenNow());
        edit.putBoolean(SHOW_VISITED_KEY, filters.isShowVisited());
        edit.commit();
    }

    public static void addSearchName(Context context, String searchName) {
        SharedPreferences prefs = context.getSharedPreferences(DEFAULT_SHARED_PREFS, 0);
        Editor edit = prefs.edit();
        edit.putString(SEARCH_NAME_KEY, searchName);
        edit.commit();
    }

    public static String getSearchName(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(DEFAULT_SHARED_PREFS, 0);
        return prefs.getString(SEARCH_NAME_KEY, NONE);
    }

    public static void setLastKnownLocation(double[] coordinates, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(DEFAULT_SHARED_PREFS, 0);
        Editor edit = prefs.edit();
        edit.putString(LAST_LATITUDE_KEY, Double.toString(coordinates[0]));
        edit.putString(LAST_LONGITUDE_KEY, Double.toString(coordinates[1]));
        edit.commit();
    }

    public static double[] getLastKnownLocationPrefs(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(DEFAULT_SHARED_PREFS, 0);
        double[] coordinates = new double[2];
        if (!Double.valueOf(prefs.getString(LAST_LATITUDE_KEY, NONE)).equals(NONE)) {
            coordinates[0] = Double.valueOf(prefs.getString(LAST_LATITUDE_KEY, NONE));
        }

        if (!Double.valueOf(prefs.getString(LAST_LONGITUDE_KEY, NONE)).equals(NONE)) {
            coordinates[1] = Double.valueOf(prefs.getString(LAST_LONGITUDE_KEY, NONE));
        }

        return coordinates;
    }

    public static boolean getPrice1Pref(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(DEFAULT_SHARED_PREFS, 0);
        return prefs.getBoolean(PRICE1_KEY, true);
    }

    public static boolean getPrice2Pref(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(DEFAULT_SHARED_PREFS, 0);
        return prefs.getBoolean(PRICE2_KEY, true);
    }

    public static boolean getPrice3Pref(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(DEFAULT_SHARED_PREFS, 0);
        return prefs.getBoolean(PRICE3_KEY, true);
    }

    public static boolean getPrice4Pref(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(DEFAULT_SHARED_PREFS, 0);
        return prefs.getBoolean(PRICE4_KEY, true);
    }

    public static int getMinPricePref(Context context) {
        int returnVal = 0;
        if (getPrice1Pref(context)) {
            returnVal = 1;
        } else if (getPrice2Pref(context)) {
            returnVal = 2;
        } else if (getPrice3Pref(context)) {
            returnVal = 3;
        } else if (getPrice4Pref(context)) {
            returnVal = 4;
        }
        return returnVal;
    }

    public static int getMaxPricePref(Context context) {
        int returnVal = 0;
        if (getPrice4Pref(context)) {
            returnVal = 4;
        } else if (getPrice3Pref(context)) {
            returnVal = 3;
        } else if (getPrice2Pref(context)) {
            returnVal = 2;
        } else if (getPrice1Pref(context)) {
            returnVal = 1;
        }
        return returnVal;
    }

    public static boolean isShowVisitedPrefs(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(DEFAULT_SHARED_PREFS, 0);
        return prefs.getBoolean(SHOW_VISITED_KEY, true);
    }

    public static boolean getOpenNowPref(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(DEFAULT_SHARED_PREFS, 0);
        return prefs.getBoolean(OPEN_NOW_KEY, true);
    }

    public static void saveCategoriesPrefsSet(Context context, Set<String> categoriesSet) {
        if (categoriesSet != null && categoriesSet.size() > 0) {
            SharedPreferences prefs = context.getSharedPreferences(DEFAULT_SHARED_PREFS, 0);
            Editor editor = prefs.edit();
            editor.putStringSet(CATEGORIES_KEY, categoriesSet);
            editor.commit();
        }

    }

    public static void saveCategoryPrefSet(Context context,String category){
        if(!TextUtils.isEmpty(category)){
            SharedPreferences prefs = context.getSharedPreferences(DEFAULT_SHARED_PREFS, 0);
            String[] defValues = context.getResources().getStringArray(R.array.categories);
            Set<String> defSet = new TreeSet<String>(Arrays.asList(defValues));
            defSet.addAll(prefs.getStringSet(CATEGORIES_KEY,defSet));
            defSet.add(category);
            saveCategoriesPrefsSet(context,defSet);
        }
    }

    public static List<String> getCategoriesPrefs(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(DEFAULT_SHARED_PREFS, 0);
        String[] defValues = context.getResources().getStringArray(R.array.categories);
        Set<String> defSet = new TreeSet<String>(Arrays.asList(defValues));
        defSet.addAll(prefs.getStringSet(CATEGORIES_KEY,defSet));
        ArrayList<String> list = new ArrayList<String>();
        list.addAll(defSet);
        return list;
    }

}
