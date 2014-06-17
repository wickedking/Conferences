package pr3k.model;

import java.io.Serializable;

/**
 * This is the user object. This holds a name of the user. This is an immutable class and
 * has been made final to prevent sub classing.
 * @author Edward Bassan
 * @version 11/04/2013
 */
public final class User implements Serializable{

	private static final long serialVersionUID = 7251430328118893598L;
	/**
	 * My username.
	 */
	private final String my_username;
	
	public User(final String the_username){
		my_username = the_username;
	}

	
	/**
	 * @return The user name.
	 */
	public String getUserName() {
		return my_username; 
	}
	
	
	/**
	 * String representation of the User
	 * 
	 * @param the_object The object
	 */
	public boolean equals(final Object the_other){
		boolean result = the_other == this;
		if (!result && the_other != null && getClass() == the_other.getClass()){
			final User other = (User) the_other;
			result = (getUserName().equals(other.getUserName()));
		}
		return result;
	}
	
	/**
	 * @return The hash code for this user.
	 */
	public int hashCode(){
		return toString().hashCode();
	}

	/**
	 * Return the name of the User
	 */
	public String toString(){
		return my_username;
	}
}
