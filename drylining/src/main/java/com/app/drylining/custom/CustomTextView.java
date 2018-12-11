package com.app.drylining.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.app.drylining.R;

public class CustomTextView extends TextView {
	private static final String TAG = "CustomTextView";

	public CustomTextView(Context context) {
		super(context);
	}

	public CustomTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setCustomFont(context, attrs);
	}

	public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setCustomFont(context, attrs);
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

	}

	private void setCustomFont(Context ctx, AttributeSet attrs) {
		TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.CustomTextView);
		String customFont = a.getString(R.styleable.CustomTextView_customFont);

		String fontName = customFont;

		setCustomFont(ctx, fontName);
		a.recycle();
	}

	public boolean setCustomFont(Context ctx, String fontName) {
		Typeface tf = null;
		try {
			// AppDebugLog.println(TAG + "Font : " + fontName);
			tf = Typeface.createFromAsset(ctx.getAssets(), fontName);

		} catch (Exception e) {
			AppDebugLog.println(TAG + ": Could not get typeface: " + e.getMessage());
			return false;
		}

		setTypeface(tf);
		return true;
	}

}
