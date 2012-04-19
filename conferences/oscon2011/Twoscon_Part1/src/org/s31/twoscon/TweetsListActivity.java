package org.s31.twoscon;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;
import au.com.secretlab.twoscon.TwitterSearcher;
import au.com.secretlab.twoscon.TwosconException;

public class TweetsListActivity extends Activity {
	
	String mSearchTerm = "#OSCON";
	TwitterSearcher mTwitterSearcher;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mTwitterSearcher = new TwitterSearcher(mSearchTerm);
        new TwitterFetcher().execute(null);
    }
    
    
    class TwitterFetcher extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {

			try {
				mTwitterSearcher.getNewestTweets();
			} catch (TwosconException e) {
				return e.toString();
			}
			
			return "SUCCESS";
		}

		@Override
		protected void onPostExecute(String result) {
			
			Toast.makeText(TweetsListActivity.this, result, Toast.LENGTH_LONG).show();
			
		}
    	
    	
    	
    }
    
    
}