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
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class TweetsListActivity extends ListActivity {

	final Uri TWITTER_SEARCH = Uri
			.parse("http://search.twitter.com/search.json");

	private String mSearchTerm = "#oscon";
	private List<JSONObject> mTweetsList;
	private TweetsAdapter mTweetsAdapter;
	private String mSinceId;

	private Button mRefreshTweets;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mTweetsList = new ArrayList<JSONObject>();
		mTweetsAdapter = new TweetsAdapter(this, 0, mTweetsList);
		mSinceId = "0"; // Want to start off by getting as many tweets as
						// possible.

		mSearchTerm = this.getIntent().getStringExtra("search_term");
		
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

	/** Adapter that displays twitter search result JSONObjects inside single_tweet layouts. 
	 * 
	 * @author chrisjrn
	 *
	 */
	class TweetsAdapter extends ArrayAdapter<JSONObject> {

		/** Just picked the most relevant constructor from the help provided by eclipse!
		 * 
		 */
		public TweetsAdapter(Context context, int textViewResourceId,
				List<JSONObject> objects) {
			super(context, textViewResourceId, objects);
			// TODO Auto-generated constructor stub
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View tweetView;
			LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			tweetView = vi.inflate(R.layout.single_tweet, parent, false);
			
			TextView fromUser = (TextView) tweetView.findViewById(R.id.tweet_from_user);
			TextView text = (TextView) tweetView.findViewById(R.id.tweet_text);
			JSONObject tweet = this.getItem(position);
						
			fromUser.setText(tweet.optString("from_user"));
			text.setText(tweet.optString("text"));
						
			return tweetView;
		}

	}

	/**
	 * AsyncTask which handles fetching the most recent tweets and adds them to
	 * the UI We use an AsyncTask to do things off the UI thread -- if a
	 * blocking task, e.g. net fetching is done on the UI thread, our app can
	 * hang. We don't want that.
	 * 
	 * @author chrisjrn
	 * 
	 */
	class TwitterFetcher extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			// Build up the URL -- See http://dev.twitter.com/doc/get/search
			Uri.Builder b = TWITTER_SEARCH.buildUpon();
			b.appendQueryParameter("q", mSearchTerm);
			b.appendQueryParameter("since_id", mSinceId);

			String twitterSearchUri = b.build().toString();

			HttpClient httpClient = new DefaultHttpClient();
			HttpGet request = new HttpGet(twitterSearchUri);

			String jsonString = null;

			try {
				// BasicResponseHandler magically turns out response into a
				// string body. Saves a lot of code, but isn't immediately
				// obvious, sadly.
				return httpClient.execute(request, new BasicResponseHandler());
			} catch (Exception e) {
				// This is bad Java practice, but we really don't have time
				// to cover doing this properly.
				Log.e("TwosconActivity", "Error: " + e);
				return null;
			}
		}

		@Override
		protected void onPostExecute(String result) {
			JSONArray js;
			if (result != null) {
				try {
					js = new JSONObject(result).optJSONArray("results");
				} catch (JSONException e) {
					return;
				}

				// Create list to store tweets in: JSONArray doesn't work as a Java collection (hrrrngh)
				ArrayList<JSONObject> newTweets = new ArrayList<JSONObject>();

				// Fill up our cursor!
				for (int i = 0; i < js.length(); i++) {
					JSONObject tweet = js.optJSONObject(i);
					if (i==0) mSinceId = tweet.optString("id");
					newTweets.add(tweet);
				}
				
				// Add to the underlying list -- can mass copy objects.
				mTweetsList.addAll(0, newTweets);				
				mTweetsAdapter.notifyDataSetChanged();

			} else {

				// Show that there's an error using a toast
				Toast.makeText(TweetsListActivity.this, R.string.twitter_failed,
						Toast.LENGTH_LONG).show();
			}
			mRefreshTweets.setEnabled(true);
		}
	}
}