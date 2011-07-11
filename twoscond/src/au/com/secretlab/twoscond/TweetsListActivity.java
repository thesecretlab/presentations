package au.com.secretlab.twoscond;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
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

	TwitterSearcher mTwitterSearcher;
	String mSearchTerm = "#OSCON";

	// Added in version B
	List<Tweet> mTweetsList;
	TweetsAdapter mTweetsAdapter;

	// Added in version C
	Button mRefreshTweets;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.tweets_list);

		mTweetsList = new ArrayList<Tweet>();
		mTweetsAdapter = new TweetsAdapter(this, 0, mTweetsList);
		setListAdapter(mTweetsAdapter);

		mRefreshTweets = (Button) findViewById(R.id.refresh_tweets);
		mRefreshTweets.setEnabled(false); // Only want it to be clickable when
											// there's things to refresh.

		// Need to get the search term from the intent...
		Bundle extras = this.getIntent().getExtras();
		mSearchTerm = extras.getString("search_term");
		
		// Press (Ctrl|Cmd) + o to import TwitterSearcher from twoscon.jar
		mTwitterSearcher = new TwitterSearcher(mSearchTerm);
		
		mRefreshTweets.setOnClickListener( new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mRefreshTweets.setEnabled(false);
				new TwitterFetcher().execute(null);				
			}
		});
		

		new TwitterFetcher().execute(null);
	}

	/**
	 * AsyncTask which handles fetching the most recent tweets and adds them to
	 * the UI We use an AsyncTask to do things off the UI thread -- if a
	 * blocking task, e.g. net fetching is done on the UI thread, our app can
	 * hang. We don't want that.
	 * 
	 * @author Christopher Neugebauer <chris@secretlab.com.au>
	 * 
	 */
	class TwitterFetcher extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {

			List<Tweet> tweets = null;

			try {
				tweets = mTwitterSearcher.getNewestTweets();
			} catch (TwosconException e) {
				// If searching for tweets causes problems, then return the
				// error message, which will be displayed as an error.
				return e.toString();
			}

			// Insert at the start of the list.
			mTweetsList.addAll(0, tweets);

			return null;
		}

		@Override
		protected void onPostExecute(String error) {
			if (error == null) {
				// We're using null to indicate no error. i.e. success

				// Tell our list adapter that there's new tweets
				mTweetsAdapter.notifyDataSetChanged();
			} else {
				// Toast popups display a small amount of text
				Toast.makeText(TweetsListActivity.this, error,
						Toast.LENGTH_LONG).show();
			}
			
			// Added in version C
			mRefreshTweets.setEnabled(true);
			
		}
	}

	/**
	 * Adapter that displays twitter search result JSONObjects inside
	 * single_tweet layouts.
	 * 
	 * @author chrisjrn
	 * 
	 */
	class TweetsAdapter extends ArrayAdapter<Tweet> {

		/**
		 * Just picked the most relevant constructor from the help provided by
		 * eclipse!
		 */
		public TweetsAdapter(Context context, int textViewResourceId,
				List<Tweet> objects) {
			super(context, textViewResourceId, objects);
			// TODO Auto-generated constructor stub
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View tweetView;
			LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			tweetView = vi.inflate(R.layout.single_tweet, parent, false);

			TextView fromUser = (TextView) tweetView
					.findViewById(R.id.tweet_from_user);
			TextView text = (TextView) tweetView.findViewById(R.id.tweet_text);
			Tweet tweet = this.getItem(position);

			fromUser.setText(tweet.fromUser);
			text.setText(tweet.text);

			return tweetView;
		}

	}

}
