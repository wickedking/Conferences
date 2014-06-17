package pr3k.database;

import java.io.Serializable;
import java.util.List;

import pr3k.model.Conference;
import pr3k.model.Paper;
import pr3k.model.User;

/**
 * Originally an inner class that stopped serializing for some reason.
 * 
 * @author User Mike Westbrook
 * @version 1.0
 */
public final  class Reviewer implements Serializable
{
	private static final long serialVersionUID = 3332751031709961549L;

	/**
	 * User assigned papers.
	 */
	//Could be a reviewer or subprogram chair.
	private final User my_user;

	/**
	 * Conference they are assigned papers in.
	 */
	private final Conference my_conf;

	/**
	 * List of papers assigned.
	 */
	private final List<Paper> my_list;
	
	/**
	 * Creates the reviewer with the specified papers.
	 * 
	 * @param the_user The user that is assigned papers.
	 * @param the_conf The conference they are assigned papers in.
	 * @param the_list The list of papers assigned.
	 */
	protected Reviewer(final User the_user, final Conference the_conf, 
			final List<Paper> the_list){
		my_user = the_user;
		my_conf = the_conf;
		my_list = the_list;
	}
	
	/**
	 * @return Serial id.
	 */
	public static long getSerialversionuid()
	{
		return serialVersionUID;
	}

	/**
	 * @return User assigned papers.
	 */
	public User getUser()
	{
		return my_user;
	}

	/**
	 * @return The conference this user is assigned papers.
	 */
	public Conference getConf()
	{
		return my_conf;
	}

	/**
	 * Papers that have been assigned to the user.
	 * @return
	 */
	public List<Paper> getPapers()
	{
		return my_list;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Reviewer other = (Reviewer) obj;
		if (my_conf == null)
		{
			if (other.my_conf != null)
				return false;
		} else if (!my_conf.equals(other.my_conf))
			return false;
		if (my_list == null)
		{
			if (other.my_list != null)
				return false;
		} else if (!my_list.equals(other.my_list))
			return false;
		if (my_user == null)
		{
			if (other.my_user != null)
				return false;
		} else if (!my_user.equals(other.my_user))
			return false;
		return true;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((my_conf == null) ? 0 : my_conf.hashCode());
		result = prime * result + ((my_list == null) ? 0 : my_list.hashCode());
		result = prime * result + ((my_user == null) ? 0 : my_user.hashCode());
		return result;
	}

	@Override
	public String toString()
	{
		return "Reviewer [my_user=" + my_user + ", my_conf=" + my_conf
				+ ", my_list=" + my_list + "]";
	}
}
