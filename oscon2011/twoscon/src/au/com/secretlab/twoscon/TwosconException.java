package au.com.secretlab.twoscon;

/** Used to indicate runtime errors in the Twoscon code
 * 
 * @author Christopher Neugebauer <chris@secretlab.com.au>
 *
 */

public class TwosconException extends Exception {

	/** Magic from Eclipse
	 * 
	 */
	private static final long serialVersionUID = -8575330343560375691L;
	
	/** Construct a TwosconException with the given error message 
	 * 
	 * @param s the error message.
	 */
	public TwosconException(String s) {
		super(s);
	}

}
