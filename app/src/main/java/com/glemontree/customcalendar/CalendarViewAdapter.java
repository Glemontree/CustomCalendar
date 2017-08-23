package com.glemontree.customcalendar;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2017/8/22.
 */

public class CalendarViewAdapter<V extends View> extends PagerAdapter {

    private static final String TAG = "CalendarViewAdapter";
    private V[] views;

    public CalendarViewAdapter(V[] views) {
        this.views = views;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (View)object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (((ViewPager) container).getChildCount() == views.length) {
            ((ViewPager) container).removeView(views[position % views.length]);
        }
        ((ViewPager)container).addView(views[position % views.length]);
        return views[position % views.length];
    }

    public V[] getAllItems() {
        return views;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
    }
}
