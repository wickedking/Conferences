package pr3k.model;


@SuppressWarnings("serial")
/**
 * Exception class for when a user is assigned to his or her own paper.
 * @author Brandon Martin
 * @version October 2013
 */
public final class InvalidAssignmentException extends Exception{

	/**
	 * The current user.
	 */
	final private User my_user;

	/**
	 * The current paper.
	 */
	private Paper my_paper;

	/**
	 * Constructor for exception. Takes in the offending pair of author and paper, for reference.
	 * @param the_user The current user.
	 * @param the_paper The current paper.
	 */
	public InvalidAssignmentException(final User the_user, final Paper the_paper){
		my_user = the_user;
		my_paper = the_paper;
	}

	/**
	 * Returns a string that is easily displayable.
	 */
	public String getMessage(){
		final StringBuilder builder = new StringBuilder(my_user.getUserName());
		builder.append(" cannot be a reviewer of ");
		builder.append(my_paper.getTitle());
		return builder.toString();
	}
}