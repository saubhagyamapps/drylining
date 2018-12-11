package com.app.drylining.custom;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.app.drylining.data.AppConstant;
import com.app.drylining.R;

@SuppressLint({ "NewApi", "SetJavaScriptEnabled" })
public class CustomWebView extends AppCompatActivity {
	private Toolbar toolbar;
	private WebView webview;
	String url = "";
	static ProgressDialog progress;

	private TextView titleText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_customwebview);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		initialze();
	}

	private void initialze() {
		setToolbar();

		progress = ProgressDialog.show(this, getString(R.string.alert_title_loading),
				getString(R.string.alert_body_wait));

		url = getIntent().getExtras().get(AppConstant.KEY_URL).toString();
		webview = (WebView) findViewById(R.id.webview);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webview.getSettings().setLoadsImagesAutomatically(true);
		webview.getSettings().setSaveFormData(true);
		webview.getSettings().setUseWideViewPort(false);
		webview.getSettings().setLoadWithOverviewMode(true);

		webview.setBackgroundColor(Color.TRANSPARENT);
		if (Build.VERSION.SDK_INT >= 11) {
			webview.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
		}

		webview.setWebViewClient(new HelloWebViewClient());
		webview.loadUrl(url);
	}


	private void setToolbar() {
		String title = "";
		if (getIntent().hasExtra(AppConstant.KEY_TITLE)) {
			title = getIntent().getStringExtra(AppConstant.KEY_TITLE);
		}

		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle(title);
		toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.toolbar_title_txt_color));

		Drawable btnBack = ContextCompat.getDrawable(this, R.drawable.back_btn_white);
		btnBack.setColorFilter(ContextCompat.getColor(this, R.color.toolbar_menu_btn_color), PorterDuff.Mode.SRC_ATOP);
		toolbar.setNavigationIcon(btnBack);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CustomWebView.this.finish();
			}
		});
	}

	private class HelloWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			if (progress.isShowing() && progress != null) {
				progress.dismiss();
			}
		}

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
			super.onReceivedSslError(view, handler, error);
			handler.cancel();

		}
	}

}
