package au.com.secretlab.twoscon;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class TwosconActivity extends ListActivity {

	final Uri TWITTER_SEARCH = Uri
			.parse("http://search.twitter.com/search.json");

	final String[] TWEET_FIELDS = new String[] { "from_user", "text", "_id" };

	final int[] TWEET_VIEWS = new int[] { R.id.tweet_from_user,
			R.id.tweet_text, };

	private String mSearchTerm = "#oscon";
	private MatrixCursor mTweetsCursor;
	private SimpleCursorAdapter mTweetsAdapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mTweetsCursor = new MatrixCursor(TWEET_FIELDS);
		mTweetsAdapter = new SimpleCursorAdapter(this, R.layout.single_tweet,
				mTweetsCursor, TWEET_FIELDS, TWEET_VIEWS);
		
		setListAdapter(mTweetsAdapter);

		// Fetch The OSCON stuff and dump the raw json to screen
		// We're using an AsyncTask because we don't want the app to hang whilst
		// it polls the Twitter Web Service
		// AsyncTask <Params, Progress, Result>
		AsyncTask<Void, Void, String> fetchTwitterStuffs = new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... arg0) {
				// Build up the URL
				Uri.Builder b = TWITTER_SEARCH.buildUpon();
				b.appendQueryParameter("q", mSearchTerm); // Does what it says
															// on the can.
				String twitterSearchUri = b.build().toString();

				HttpClient httpClient = new DefaultHttpClient();
				HttpGet request = new HttpGet(twitterSearchUri);

				String jsonString = null;

				try {
					// BasicResponseHandler magically turns out response into a
					// string body. Saves a lot of code, but isn't immediately
					// obvious, sadly.
					jsonString = httpClient.execute(request,
							new BasicResponseHandler());
				} catch (Exception e) {
					// This is bad Java practice, but we really don't have time
					// to cover doing this properly.
					Log.e("TwosconActivity", "Error: " + e);
					return null;
				}

				return jsonString;

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

					// Fill up our cursor!
					for (int i = 0; i < js.length(); i++) {
						JSONObject tweet = js.optJSONObject(i);
						mTweetsCursor.addRow(new String[] { 
							tweet.optString("from_user"),
							tweet.optString("text"),
							tweet.optString("id"),
						} );
					}
					mTweetsAdapter.notifyDataSetChanged();

				} else {

					// Show that there's an error using a toast
					Toast.makeText(TwosconActivity.this,
							R.string.twitter_failed, Toast.LENGTH_LONG).show();
				}
			}

		};

		fetchTwitterStuffs.execute();

	}

}