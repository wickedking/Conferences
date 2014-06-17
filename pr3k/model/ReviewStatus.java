package pr3k.model;


/**
 * Enum for the review status of a paper.
 * @author Jesse Kitterman
 * @version October 2013
 */
public enum ReviewStatus {

	RECOMMEND("Paper has been recommended for approval. "),

	DONT_RECOMMEND("Paper has been recommended for disapproval. "),

	UNDER_REVIEW("Paper has not been reviewed yet. ");

	private final String my_message;
    /**
     * Set review status for the paper
     * 
     * @param the_message The message
     */
	ReviewStatus(final String the_message){
		my_message = the_message;
	}
	
    /**
     * Return the message base on the review status
     * 
     * @return my_message
     */
	public String getMessage(){
		return my_message;
	}
	
    /**
     * Return a message base on the paper status
     * 
     * @param the_status The status
     * @return
     */
	public static String getName(ReviewStatus the_status){
		StringBuilder sb = new StringBuilder();
		switch (the_status) {
		case RECOMMEND:
			sb.append("RECOMMEND");
			break;
		case DONT_RECOMMEND:
			sb.append("DONT_RECOMMEND");
			break;
		case UNDER_REVIEW:
			sb.append("UNDER_REVIEW");
		default:
			break;
		}
		return sb.toString();
	}
}
