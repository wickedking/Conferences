package pr3k.model;

import java.io.File;
import java.io.Serializable;


/**
 * This class holds the info needed for a paper object. This class is immutable and 
 * has been made final to prevent sub classing.
 * @author Edward Bassan
 * @version October 2013
 */
public final class Paper implements Serializable{

	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 6453713173884471063L;

	/**
	 * The title of the paper.
	 */
	private final String my_title;

	/**
	 * The author of the paper.
	 */
	private final User my_author;

	/**
	 * The file of the file submitted.
	 */
	private final File my_file;

	/**
	 * The acceptance status of the paper.
	 */
	private final AcceptanceStatus my_accept_status;

	/**
	 * The review status of the paper, and recommendation.
	 */
	private final ReviewStatus my_review_status;
	
	/**
	 * The Conference that the paper is submitted to.
	 */
	private final Conference my_conf;

	/**
	 * Constructor used for first creating paper, or paper with no accept/review status.
	 * @param the_title The title of the paper.
	 * @param the_author The author of the paper.
	 * @param the_file The file of the paper submitted.
	 * @param the_conf The conference the paper is submitted to.
	 */
	public Paper(final String the_title, final User the_author, final File the_file, final Conference the_conf){
		this(the_title, the_author, the_file, the_conf, ReviewStatus.UNDER_REVIEW, AcceptanceStatus.UNDER_REVIEW);
	}
	
	/**
	 * Constructor used for when you dont have accept status field.
	 * @param the_title The title of the paper.
	 * @param the_author The author of the paper.
	 * @param the_file The file of the paper submitted.
	 * @param the_conf The conference the paper is submitted to.
	 * @param the_review The review status of the paper.
	 */
	public Paper(final String the_title, final User the_author, final File the_file, final Conference the_conf, final ReviewStatus the_review){
		this(the_title, the_author, the_file, the_conf, the_review, AcceptanceStatus.UNDER_REVIEW);
	}
	
	/**
	 * Constructor used for all fields. Overloaded.
	 * @param the_title The title of the paper.
	 * @param the_author The author of the paper.
	 * @param the_file The file of the paper submitted.
	 * @param the_conf The conference the paper is submitted to.
	 * @param the_review The review status of the paper.
	 * @param the_status The acceptence status of the paper.
	 */
	public Paper(final String the_title, final User the_author, final File the_file, final Conference the_conf,
			final ReviewStatus the_review, final AcceptanceStatus the_status){
		this.my_title = the_title;
		this.my_author = the_author;
		this.my_file = the_file;
		my_conf = the_conf;
		this.my_review_status =  the_review;
		this.my_accept_status = the_status;
	}
	
	/**
	 * Getter for acceptance status.
	 * @return The acceptance status.
	 */
	public AcceptanceStatus getAcceptanceStatus(){
		return my_accept_status;
	}

	/**
	 * Getter for review status.
	 * @return The review status.
	 */
	public ReviewStatus getReviewStatus(){
		return my_review_status;
	}
	
	/**
	 * Getter for the author.
	 * @return The author of the paper.
	 */
	public User getAuthor(){
		return my_author;
	}
	
	/**
	 * Getter for the name of the paper.
	 * @return The title of the paper.
	 */
	public String getTitle(){
		return my_title;
	}

	/**
	 * Getter for the file.
	 * @return The file associated with the paper.
	 */
	public File getFile(){
		return my_file;
	}
	
	/**
	 * Getter for the conference that paper is submitted to.
	 * @return The Conference the paper is in.
	 */
	public Conference getConference(){
		return my_conf;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(my_title);
		//sb.append("\nFILE:" + my_file);
		//sb.append("\nSTATUS:" + my_accept_status);
		//sb.append("\nAUTHOR:" + my_author);
		return sb.toString();
	}

	@Override
	/**
	 * Overwritten method that checks equality on authors, titles and conference.
	 */
	public boolean equals(final Object the_other){
		boolean result = the_other == this;
		if (!result && the_other != null && the_other.getClass() == getClass()){
			final Paper other = (Paper) the_other;
			result = other.getAuthor().equals(getAuthor()) && other.getTitle().equals(getTitle()) && 
					getConference().equals(other.getConference());
		}
		return result;
	}

	@Override
	public int hashCode(){ 
		return getAuthor().hashCode() + getTitle().hashCode(); 
	}
}
