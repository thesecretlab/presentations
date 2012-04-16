package org.s31.twoscon;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import au.com.secretlab.twoscon.Tweet;
import au.com.secretlab.twoscon.TwitterSearcher;
import au.com.secretlab.twoscon.TwosconException;

public class TweetsListActivity extends ListActivity {
	
	String mSearchTerm = "#OSCON";
	TwitterSearcher mTwitterSearcher;
	
	Button mRefreshTweets;
	
	ArrayList<Tweet> mTweetsList;
	TweetsAdapter mTweetsAdapter;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mRefreshTweets = (Button) findViewById(R.id.refresh_tweets);
        mRefreshTweets.setOnClickListener(new RefreshTweetsListener());
        mRefreshTweets.setEnabled(false);
        
        mTweetsList = new ArrayList<Tweet>();
        mTweetsAdapter = new TweetsAdapter(this, 0, 0, mTweetsList);
        setListAdapter(mTweetsAdapter);
        
        mTwitterSearcher = new TwitterSearcher(mSearchTerm);
        new TwitterFetcher().execute(null);
    }
    
    
    class TwitterFetcher extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {

			try {
				mTweetsList.addAll(0, mTwitterSearcher.getNewestTweets());
				return null;
			} catch (TwosconException e) {
				return e.toString();
			}
		}

		@Override
		protected void onPostExecute(String result) {
			
			mRefreshTweets.setEnabled(true);
			mTweetsAdapter.notifyDataSetChanged();
			if (result != null) {
				Toast.makeText(TweetsListActivity.this, result, Toast.LENGTH_LONG).show();
			}
			
		}
    	
    }
    
    class RefreshTweetsListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			arg0.setEnabled(false);
			new TwitterFetcher().execute();
		}
    	
    }
    
    class TweetsAdapter extends ArrayAdapter<Tweet> {

		public TweetsAdapter(Context context, int resource,
				int textViewResourceId, List<Tweet> objects) {
			super(context, resource, textViewResourceId, objects);
			// TODO Auto-generated constructor stub
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			Tweet tweet = getItem(position);
			View v = getLayoutInflater().inflate(R.layout.single_tweet, parent, false);
			
			TextView t = (TextView) v.findViewById(R.id.tweet_from_user);
			t.setText(tweet.fromUser);
			t = (TextView) v.findViewById(R.id.tweet_text);
			t.setText(tweet.text);
			
			return v;
			
		}
    	
		
		
    }
    
    
}