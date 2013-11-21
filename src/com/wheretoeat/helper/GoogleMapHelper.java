package com.wheretoeat.helper;

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class GoogleMapHelper {

	private static final String TAG = "GoogleMapHelper";

	public static double[] getCurrentlocation(Context context) {
		LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		List<String> providers = lm.getProviders(true);
		Location l = null;
		for (int i = 0; i < providers.size(); i++) {
			l = lm.getLastKnownLocation(providers.get(i));
			if (l != null)
				break;
		}
		double[] gps = new double[2];

		if (l != null) {
			gps[0] = l.getLatitude();
			gps[1] = l.getLongitude();
		}
		return gps;
	}

	public static void markLocationOnMap(double[] coordinates, GoogleMap googleMap, String title, Context context, int counter) {

		LatLng currentLocation = new LatLng(coordinates[0], coordinates[1]);
		Bitmap bmp = null;
		if (counter > 0) {
			bmp = drawTextToBitmap(context, counter + "");
		} else {
			bmp = drawTextToBitmap(context, "0");
		}

		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.position(currentLocation);
		markerOptions.title(title);
		if (title.equalsIgnoreCase("This is You")) {
			markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
		} else {
			markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bmp));
		}

		googleMap.addMarker(markerOptions);

		googleMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) {
				marker.getId();
				return false;
			}
		});
	}

	public static void clearAllMarkers(GoogleMap googleMap) {
		googleMap.clear();
	}

	public static Bitmap drawTextToBitmap(Context mContext, String mText) {
		try {
			Resources resources = mContext.getResources();
			Bitmap bitmap = BitmapFactory.decodeResource(resources, com.wheretoeat.activities.R.drawable.ic_map_marker);

			android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();
			// set default bitmap config if none
			if (bitmapConfig == null) {
				bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
			}
			// resource bitmaps are imutable,
			// so we need to convert it to mutable one
			bitmap = bitmap.copy(bitmapConfig, true);

			Canvas canvas = new Canvas(bitmap);
			// new antialised Paint
			Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			// text color - #3D3D3D
			paint.setColor(Color.rgb(0, 0, 0));
			// setTypeface(null, Typeface.BOLD);
			paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
			// text size in pixels
			paint.setTextSize((int) (30));

			// draw text to the Canvas center
			Rect bounds = new Rect();
			paint.getTextBounds(mText, 0, mText.length(), bounds);
			int x = (bitmap.getWidth() - bounds.width()) / 2;
			int y = (bitmap.getHeight() + bounds.height()) / 2;
			canvas.drawText(mText, x - 5, y - 20, paint);

			return bitmap;
		} catch (Exception e) {
			Log.d(TAG, e + "");
			return null;
		}

	}
}
