package pr3k.model;


@SuppressWarnings("serial")
/**
 * Exception class for when a user cannot add to a conference.
 * @author Brandon Martin
 * @version October 2013
 *
 */
public final class InvalidSubmissionException extends Exception{
	
	/**
	 * The current user.
	 */
	private final User my_user;

	/**
	 * The current conference. 
	 */
	private final Conference my_conference;

	public InvalidSubmissionException(final User the_user,
			final Conference the_conference){
		my_user = the_user;
		my_conference = the_conference;
	}

	public String getMessage()
	{
		final StringBuilder builder = 
				new StringBuilder(super.getMessage());
		builder.append("\n");
		builder.append(my_user.getUserName());
		builder.append(" cannot submit papers to ");
		builder.append(my_conference.getName());
		builder.append(" because user is alreader a Reviewer. ");
		return builder.toString();
	}
}
