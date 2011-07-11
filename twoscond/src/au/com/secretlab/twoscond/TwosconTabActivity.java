package au.com.secretlab.twoscond;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class TwosconTabActivity extends TabActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.tabs);

	    Resources res = getResources(); // Resource object to get Drawables
	    TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab
	    	    
	    // Create an Intent to launch an Activity for the tab (to be reused)
	    intent = new Intent().setClass(this, TweetsListActivity.class);

	    // Give an extra so that we can load a specific search term per tab
	    intent.putExtra("search_term", "#oscon");
	    
	    // Initialize a TabSpec for each tab and add it to the TabHost
	    spec = tabHost.newTabSpec("oscon").setIndicator("#oscon",
	                      res.getDrawable(android.R.drawable.ic_menu_add))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    // Do the same for the other tabs
	    intent = new Intent().setClass(this, TweetsListActivity.class);
	    intent.putExtra("search_term", "#coffee");
	    spec = tabHost.newTabSpec("coffee").setIndicator("#coffee",
	                      res.getDrawable(android.R.drawable.ic_menu_add))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    intent = new Intent().setClass(this, TweetsListActivity.class);
	    intent.putExtra("search_term", "#ohgod");
	    spec = tabHost.newTabSpec("ohgod").setIndicator("#ohgod",
	                      res.getDrawable(android.R.drawable.ic_menu_add))
	                  .setContent(intent);
	    tabHost.addTab(spec);	
		
	}
	
}
