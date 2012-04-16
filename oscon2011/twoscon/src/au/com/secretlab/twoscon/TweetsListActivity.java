package au.com.secretlab.twoscon;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TweetsListActivity extends ListActivity {

	final Uri TWITTER_SEARCH = Uri
			.parse("http://search.twitter.com/search.json");

	private String mSearchTerm = "#oscon";
	private List<Tweet> mTweetsList;
	private TweetsAdapter mTweetsAdapter;
	private TwitterSearcher mTwitterSearcher;

	private Button mRefreshTweets;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mTweetsList = new ArrayList<Tweet>();
		mTweetsAdapter = new TweetsAdapter(this, 0, mTweetsList);

		mSearchTerm = this.getIntent().getStringExtra("search_term");
		mTwitterSearcher = new TwitterSearcher(mSearchTerm);

		this.setContentView(R.layout.twoscon);
		setListAdapter(mTweetsAdapter);

		mRefreshTweets = (Button) findViewById(R.id.refresh_tweets);
		mRefreshTweets.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				new TwitterFetcher().execute();
				mRefreshTweets.setEnabled(false);
			}
		});

		new TwitterFetcher().execute();

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

			// Add our tweets to the underlying UI storage -- we don't update
			// the UI yet.
			mTweetsList.addAll(0, tweets);

			return null;
		}

		@Override
		protected void onPostExecute(String error) {
			if (error == null) {
				// Update the UI
				mTweetsAdapter.notifyDataSetChanged();
			} else {
				// Show that there's an error using a toast
				// This error is probably not very user-friendly. We can improve
				// this later.
				Toast.makeText(TweetsListActivity.this, error,
						Toast.LENGTH_LONG).show();
			}

			// Set our button to be usable again!
			mRefreshTweets.setEnabled(true);
		}
	}
}