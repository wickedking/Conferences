package pr3k.model;


/**
 * This class represents a conference. This class is immutable and has been 
 * made final to prevent sub classing.
 * @author Cody Shafer
 * @version October 2013
 */
import java.io.Serializable;
import java.sql.Date;


public final class Conference implements Serializable{
	
	private static final long serialVersionUID = 4771048492489131654L;

	/**
	 * Title of the conference.
	 */
	private final String my_name;

	/**
	 * Date the conference is to be held.
	 */
	private final Date my_date;

	/**
	 * Due date for the papers.
	 */
	private final Date my_deadline;
	
	/**
	 * Program Chair of the conference.
	 */
	private final User my_program_chair;
	
	public Conference(final String the_name, final Date the_date, final Date the_deadline,
			final User the_chair){
		my_name = the_name;
		my_date = the_date;
		my_deadline = the_deadline;
		my_program_chair = the_chair;		
	}
	

	/**
	 * @return The title of the conference.
	 */
	public String getName() {
		return my_name;
	}

	/**
	 * @return The date of the conference.
	 */
	public Date getDate(){
		return my_date;
	}

	/**
	 * @return The Program Chair of the conference.
	 */
	public User getProgramChair(){
		return my_program_chair;
	}

	public Date getDeadline(){
		return my_deadline;
	}

	@Override
	/**
	 * Override of basic equals method. Equality is same name, same date, same deadline and same program chair.
	 */
	public boolean equals(Object the_object){
		if(the_object != null && the_object.getClass() == this.getClass()){
			Conference test = (Conference) the_object;
			if(getName().equals(test.getName()) && 
					getDate().equals(test.getDate()) && 
					getDeadline().equals(test.getDeadline()) && 
					getProgramChair().equals(test.getProgramChair())){
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode(){
		return getName().hashCode();
	}

	@Override
	public String toString(){
		final StringBuilder builder = new StringBuilder();
		//final DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		builder.append(getName());
		//builder.append(" Date: ");
		//builder.append(df.format(getDate()));
		//builder.append(" Deadline: ");
		//builder.append(df.format(getDeadline()));
		//builder.append(" ProgramChair: ");
		//builder.append(getProgramChair().getUserName());
		return builder.toString();
	}
}
