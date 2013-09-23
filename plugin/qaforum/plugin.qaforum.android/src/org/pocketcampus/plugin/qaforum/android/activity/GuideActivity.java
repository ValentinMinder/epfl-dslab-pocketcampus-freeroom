package org.pocketcampus.plugin.qaforum.android.activity;

import org.pocketcampus.plugin.qaforum.R;

import org.pocketcampus.plugin.qaforum.android.ViewPagerAdapter;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * GuideActivity - show guide pages in the first launch.
 * 
 * @author Susheng <susheng.shi@epfl.ch>
 * 
 */

public class GuideActivity extends Activity implements OnPageChangeListener {

    private ViewPager vp;
    private ViewPagerAdapter vpAdapter;
    private List<View> views;

    private ImageView[] dots;

    private int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qaforum_guide);
        
        initViews();

        initDots();
    }

    private void initViews() {
        LayoutInflater inflater = LayoutInflater.from(this);

        views = new ArrayList<View>();
        
        views.add(inflater.inflate(R.layout.qaforum_guide_one, null));
        views.add(inflater.inflate(R.layout.qaforum_guide_two, null));
        views.add(inflater.inflate(R.layout.qaforum_guide_three, null));
        views.add(inflater.inflate(R.layout.qaforum_guide_four, null));
        views.add(inflater.inflate(R.layout.qaforum_guide_five, null));

        vpAdapter = new ViewPagerAdapter(views, this);

        vp = (ViewPager) findViewById(R.id.viewpager);
        vp.setAdapter(vpAdapter);

        vp.setOnPageChangeListener(this);
    }

    private void initDots() {
        LinearLayout ll = (LinearLayout) findViewById(R.id.ll);

        dots = new ImageView[views.size()];

        for (int i = 0; i < views.size(); i++) {
            dots[i] = (ImageView) ll.getChildAt(i);
            dots[i].setEnabled(true);
        }

        currentIndex = 0;
        dots[currentIndex].setEnabled(false);
    }

    private void setCurrentDot(int position) {
        if (position < 0 || position > views.size() - 1
                || currentIndex == position) {
            return;
        }

        dots[position].setEnabled(false);
        dots[currentIndex].setEnabled(true);

        currentIndex = position;
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int arg0) {
        setCurrentDot(arg0);
    }

}