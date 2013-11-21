package com.wheretoeat.adapters;

import java.util.HashMap;
import java.util.Map;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.wheretoeat.fragments.CategoriesFragment;
import com.wheretoeat.fragments.FavoritesFragment;
import com.wheretoeat.fragments.NearbyFragment;
import com.wheretoeat.fragments.TopRatedFragment;

public class SectionPagerAdapter extends FragmentPagerAdapter {
	Map<Integer, Fragment> fragCached;

	public SectionPagerAdapter(FragmentManager fm) {
		super(fm);
		fragCached = new HashMap<Integer, Fragment>();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
		case 0:
			return "Categories";
		case 1:
			return "Nearby";
		case 2:
			return "Top Rated";
		case 3:
			return "My Restaurants";
		default:
			break;
		}
		return null;
	}

	@Override
	public Fragment getItem(int position) {
		Fragment fragment = null;
		switch (position) {
		case 0:
			fragment = fragCached.get(0);
			if (fragment == null) {
				fragment = new CategoriesFragment();
				fragCached.put(0, fragment);
			}
			break;
		case 1:
			fragment = fragCached.get(1);
			if (fragment == null) {
				fragment = new NearbyFragment();
				fragCached.put(1, fragment);
			}

			break;
		case 2:
			fragment = fragCached.get(2);
			if (fragment == null) {
				fragment = new TopRatedFragment();
				fragCached.put(2, fragment);
			}

			break;
		case 3:
			fragment = fragCached.get(3);
			if (fragment == null) {
				fragment = new FavoritesFragment();
				fragCached.put(3, fragment);
			}
			break;
		}
		return fragment;
	}

	@Override
	public int getCount() {
		return 4;
	}

}
