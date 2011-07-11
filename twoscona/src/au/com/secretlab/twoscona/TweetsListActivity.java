package au.com.secretlab.twoscona;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;
import au.com.secretlab.twoscon.TwitterSearcher;
import au.com.secretlab.twoscon.TwosconException;
import au.com.secretlab.twoscona.R;

public class TweetsListActivity extends Activity {

	TwitterSearcher mTwitterSearcher;
	String mSearchTerm = "#OSCON";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Press (Ctrl|Cmd) + o to import TwitterSearcher from twoscon.jar
		mTwitterSearcher = new TwitterSearcher(mSearchTerm);

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

			try {
				mTwitterSearcher.getNewestTweets();
			} catch (TwosconException e) {
				// If searching for tweets causes problems, then return the
				// error message, which will be displayed as an error.
				return e.toString();
			}

			return "SUCCESS";
		}

		@Override
		protected void onPostExecute(String error) {
			// Toast popups display a small amount of text
			Toast.makeText(TweetsListActivity.this, error, Toast.LENGTH_LONG)
					.show();
		}

	}
}
