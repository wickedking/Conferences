package pr3k.model;


@SuppressWarnings("serial")
/**
 * Exception for when a user has reached the max submissions allowed.
 * @author Mike Westbrook
 * @version  October 2013
 */
public class MaxPapersException extends Exception{
	
	/**
	 * The current user.
	 */
	private final User my_user;
	
	public MaxPapersException(final User the_user){
		super();
		my_user = the_user;
	}
	
	public String getMessage(){
		return super.getMessage() + "/n" + my_user.getUserName() + " has been assigned the max" + 
				" number of papers";
	}
}
