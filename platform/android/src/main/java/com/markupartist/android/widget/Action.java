package com.markupartist.android.widget;

import android.view.View;

public interface Action {
	public int getDrawable();
    public void performAction(View view);
    public String getDescription();
}
