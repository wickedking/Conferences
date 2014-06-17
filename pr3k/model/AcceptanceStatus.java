package pr3k.model;


/**
 * Enum for current status of acceptance.
 * @author Cody Shafer
 * @version October 2013
 */
public enum AcceptanceStatus {
	
	ACCEPT("Accepted"),

	REJECT("Rejected"),

	UNDER_REVIEW("Under Review");

	private final String my_message;

	AcceptanceStatus(final String the_status){
		my_message = the_status;
	}

	public String getMessage(){
		return my_message;
	}

	/**
	 * Returns a string representation of the enum.
	 * @param the_status The status to get a string of.
	 * @return The string of the acceptance status.
	 */
	public static String getName(AcceptanceStatus the_status){
		StringBuilder sb = new StringBuilder();
		switch (the_status) {
		case ACCEPT:
			sb.append("Accept");
			break;
		case REJECT:
			sb.append("REJECT");
			break;
		case UNDER_REVIEW:
			sb.append("UNDER_REVIEW");
			break;
		default:
			break;
		}
		return sb.toString();
	}
}
