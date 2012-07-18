package org.exobel.routerkeygen.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.RelativeLayout;

public class CheckableRelativeLayout extends RelativeLayout implements Checkable {
	private boolean checked = false;

	final private List<Checkable> checkableViews;
	public CheckableRelativeLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		checkableViews = new ArrayList<Checkable>();
	}

	public CheckableRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		checkableViews = new ArrayList<Checkable>();
	}
	public CheckableRelativeLayout(Context context) {
		super(context);
		checkableViews = new ArrayList<Checkable>();
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
		for (Checkable c : checkableViews) {
			// Pass the information to all the child Checkable widgets
			c.setChecked(checked);
		}
	}

	public void toggle() {
		checked = !checked;
		for (Checkable c : checkableViews) {
			// Pass the information to all the child Checkable widgets
			c.toggle();
		}
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		final int childCount = this.getChildCount();
		for (int i = 0; i < childCount; ++i) {
			findCheckableChildren(this.getChildAt(i));
		}
	}

	/**
	 * Add to our checkable list all the children of the view that implement the
	 * interface Checkable
	 */
	private void findCheckableChildren(View v) {
		if (v instanceof Checkable) {
			this.checkableViews.add((Checkable) v);
		}

		if (v instanceof ViewGroup) {
			final ViewGroup vg = (ViewGroup) v;
			final int childCount = vg.getChildCount();
			for (int i = 0; i < childCount; ++i) {
				findCheckableChildren(vg.getChildAt(i));
			}
		}
	}
}
