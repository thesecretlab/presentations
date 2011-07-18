package org.s31.twoscon;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class TwosconTabsActivity extends TabActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.tabs_view);

		TabHost tabHost = getTabHost();
		TabSpec tabSpec;
		Intent intent;

		intent = new Intent(this, TweetsListActivity.class);
		intent.putExtra("search_term", "#oscon");
		tabSpec = tabHost.newTabSpec("#oscon").setIndicator("#oscon")
				.setContent(intent);
		tabHost.addTab(tabSpec);

		intent = new Intent(this, TweetsListActivity.class);
		intent.putExtra("search_term", "#coffee");
		tabSpec = tabHost.newTabSpec("#coffee").setIndicator("#coffee")
				.setContent(intent);
		tabHost.addTab(tabSpec);

		intent = new Intent(this, TweetsListActivity.class);
		intent.putExtra("search_term", "#pdx");
		tabSpec = tabHost.newTabSpec("#pdx").setIndicator("#pdx")
				.setContent(intent);
		tabHost.addTab(tabSpec);

	}

}
