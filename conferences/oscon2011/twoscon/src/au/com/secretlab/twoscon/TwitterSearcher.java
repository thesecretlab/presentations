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

import android.net.Uri;
import android.util.Log;

/**
 * A class for searching Twitter for the latest tweets matching a particular
 * search term. This consitutes part of the Twoscon library that's being used as
 * part of "Android for People Who Hate Phones" at OSCON 2011.
 * 
 * @author Christopher Neugebauer <chris@secretlab.com.au>
 * 
 */

public class TwitterSearcher {

	String mSearchTerm;
	String mSinceId;

	final Uri TWITTER_SEARCH = Uri
			.parse("http://search.twitter.com/search.json");

	/**
	 * Construct a new TwitterSearcher to use for searching for new tweets
	 * matching the given search term.
	 * 
	 * @param searchTerm
	 *            The term you wish to search for. Include a '#' if you're
	 *            looking specifically for a hashtag. Twitter searches for terms
	 *            separated by spaces.
	 * 
	 */

	public TwitterSearcher(String searchTerm) {
		this.mSearchTerm = searchTerm;
		this.mSinceId = "0";
	}

	/** Gets all tweets matching the search term given at construction, since the last time the method was called.
	 * 
	 * @return A List containing the latest Tweets matching the search term.
	 * @throws TwosconException
	 */
	
	public List<Tweet> getNewestTweets() throws TwosconException {
		ArrayList<Tweet> tweets = new ArrayList<Tweet>();
		String tempSinceId = mSinceId;

		// Build up the URL -- See http://dev.twitter.com/doc/get/search
		Uri.Builder b = TWITTER_SEARCH.buildUpon();
		b.appendQueryParameter("q", mSearchTerm);
		b.appendQueryParameter("since_id", mSinceId);

		String twitterSearchUri = b.build().toString();

		HttpClient httpClient = new DefaultHttpClient();
		HttpGet request = new HttpGet(twitterSearchUri);

		String result = null;

		try {
			// BasicResponseHandler magically turns out response into a
			// string body. Saves a lot of code, but isn't immediately
			// obvious, sadly.
			result = httpClient.execute(request, new BasicResponseHandler());
		} catch (Exception e) {
			// This is bad Java practice, but we really don't have time
			// to cover doing this properly.
			throw new TwosconException(e.toString());
		}

		JSONArray js;
		try {
			js = new JSONObject(result).getJSONArray("results");
		} catch (JSONException e) {
			throw new TwosconException(e.toString());
		}

		assert (js != null);

		// Build up 'tweet' objects from our JSON array.
		for (int i = 0; i < js.length(); i++) {
			try {

				JSONObject jsTweet = js.getJSONObject(i);
				Tweet tweet = new Tweet();
				tweet.fromUser = jsTweet.getString("from_user");
				tweet.text = jsTweet.getString("text");
				tweet.id = jsTweet.getString("id");

				if (i == 0) {
					// We're only going to update the sinceId if the process was
					// successful.
					tempSinceId = tweet.id;
				}
				tweets.add(tweet);
			} catch (JSONException e) {
				// There was an issue processing the JSON object at this point;
				// tell our consumer about it!
				throw new TwosconException(e.toString());
			}
		}

		mSinceId = tempSinceId;

		return tweets;
	}
}
