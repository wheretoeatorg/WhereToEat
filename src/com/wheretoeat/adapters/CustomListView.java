
package com.wheretoeat.adapters;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

public class CustomListView extends ListView implements OnScrollListener {

    private static final String TAG = "CustomListView";
    private int touchSlop;
    private boolean isScrolling = false;
    private int firstItemTop = -1;
    private float diffY;

    public CustomListView(Context context) {
        super(context);
        ViewConfiguration vc = ViewConfiguration.get(context);
        touchSlop = vc.getScaledTouchSlop();
    }

    public CustomListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CustomListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.d(TAG, "Intercepting touch event");
        return false;
    }

    @Override
    public void onScrollStateChanged(AbsListView paramAbsListView, int scrollState) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onScroll(AbsListView paramAbsListView, int firstItemVisible, int visibleItemCount,
            int totalItemCount) {
        View item = (View) paramAbsListView.getItemAtPosition(firstItemVisible);
        int top = item.getTop();
        firstItemTop = top;
        Log.d(TAG, "firstVisible Item Top = " + top);
    }

}
