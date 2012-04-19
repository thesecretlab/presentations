package org.s31.twoscon;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import au.com.secretlab.twoscon.TwitterSearcher;
import au.com.secretlab.twoscon.TwosconException;

public class TweetsListActivity extends Activity {
	
	String mSearchTerm = "#OSCON";
	TwitterSearcher mTwitterSearcher;
	
	Button mRefreshTweets;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mRefreshTweets = (Button) findViewById(R.id.refresh_tweets);
        mRefreshTweets.setOnClickListener(new RefreshTweetsListener());
        mRefreshTweets.setEnabled(false);
        
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
			
			mRefreshTweets.setEnabled(true);
			Toast.makeText(TweetsListActivity.this, result, Toast.LENGTH_LONG).show();
			
		}
    	
    }
    
    class RefreshTweetsListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			arg0.setEnabled(false);
			new TwitterFetcher().execute();
		}
    	
    }
    
    
}