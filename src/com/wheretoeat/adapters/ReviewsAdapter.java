package com.wheretoeat.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wheretoeat.activities.R;
import com.wheretoeat.models.Review;

public class ReviewsAdapter extends ArrayAdapter<Review> {
	private static final String TAG = "reviewsAdpater";
	private Context context;
	
	List<Review> reviews;
	TextView tvName;
	TextView tvDate;
	TextView tvRating;
	TextView tvText;

	public ReviewsAdapter(Context context, List<Review> reviews) {
		super(context, 0, reviews);
		this.context = context;
		this.reviews = reviews;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.review_item, null);
		}
		initViews(view);

		tvName.setText(reviews.get(position).getAuthor());
		tvDate.setText(reviews.get(position).getTime());
		tvRating.setText(reviews.get(position).getRating() + "/5");
		tvText.setText(reviews.get(position).getRatingText());
		return view;
	}

	private void initViews(View view) {
		tvName = (TextView) view.findViewById(R.id.tvName);
		tvDate = (TextView) view.findViewById(R.id.tvDate);
		tvRating = (TextView) view.findViewById(R.id.tvRating);
		tvText = (TextView) view.findViewById(R.id.tvText);
	}


	@Override
	public int getCount() {
		return reviews.size();
	}

	@Override
	public Review getItem(int position) {
		return reviews.get(position);
	}

}
