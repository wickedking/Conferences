package pr3k.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import pr3k.guiv2.PR3KFrame;
import pr3k.guiv2.PR3KPanel;
import pr3k.model.User;
/**
 * An action to change out panels in the frame. Specify the old panel, and the new one, and this will switch 
 * it out in the frame.
 * @author Mike Westbrook
 * @version November 2013
 */
@SuppressWarnings("serial")
public class ChangePanelAction extends AbstractAction{
	
	/**
	 * A reference to the old panel to be switched out.
	 */
	private final PR3KPanel my_old;
	
	/**
	 * The new Panel to be switched in.
	 */
	private final PR3KPanel my_new;
	
	public ChangePanelAction(final PR3KPanel the_old, final PR3KPanel the_new){
		super();
		my_old = the_old;
		my_new = the_new;
	}

	/**
	 * Switches the panels out in the frame of the old panel.
	 * @param e
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		final PR3KFrame old_frame = (PR3KFrame) my_old.getTopLevelAncestor();
		final User username = old_frame.getUser();
		final PR3KFrame new_frame = new PR3KFrame();
		new_frame.setUser(username);
		new_frame.addMenu();
		new_frame.add(my_new);
		new_frame.start();
		old_frame.dispose();		
	}
}
