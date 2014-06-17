package pr3k.guiv2;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import pr3k.model.Conference;
import pr3k.model.User;


@SuppressWarnings("serial")
/**
 * This panel is subclassed by all of other guis. It allows for some common functionallity and some references to some data
 * need in most other panels.
 * @author Jesse Kitterman
 * @version November 2013
 */
public class PR3KPanel extends JPanel {
	
	/**
	 * The conference that is currently being viewed or used.
	 */
	private final Conference my_conf;
	
	/**
	 * The current user that is logged in.
	 */
	private final User my_user;
	
	/**
	 * Constructor
	 * @param the_user The current user.
	 * @param the_conf The current conference.
	 */
	public PR3KPanel(final User the_user, final Conference the_conf) {
		super();
		my_conf = the_conf;
		my_user = the_user;
		setLayout(new BorderLayout());

	}
	
	/**
	 * Getter for the current conference.
	 * @return The current conference.
	 */
	public Conference getConf(){
		return my_conf;
	}
	
	/**
	 * Getter for the current user.
	 * @return The current user.
	 */
	public User getUser(){
		return my_user;
	}
}
