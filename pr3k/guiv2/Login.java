package pr3k.guiv2;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import pr3k.actions.ChangePanelAction;
import pr3k.database.Database;
import pr3k.model.User;


@SuppressWarnings("serial")
/**
 * This gui is to allow a user to login or create a new account to access the system.
 * @author Brandon Martin
 * @version November 2013
 */
public class Login extends PR3KPanel {
	
	/**
	 * Constant for the size of the window.
	 */
	private static final Dimension SIZE = new Dimension(300, 400);
	
	/**
	 * Constant for the size of the input field.
	 */
	private static final Dimension FIELDSZ = new Dimension(96, 20);
	
	/**
	 * Constructor for creating the gui.
	 */
	public Login() {
		super(null, null);
		this.setSize(SIZE);
		initPanel();
	}
	
	/**
	 * Creates the gui for the login.
	 */
	private void initPanel() {
		final JButton login = new JButton("Login");
		setLayout(new FlowLayout());
		final JTextField field = new JTextField();
		field.setPreferredSize(FIELDSZ);
		field.addActionListener(new ActionListener(){
			public void actionPerformed(final ActionEvent the_e){
				login.doClick();
			}
		});
		
		login.addActionListener(new ActionListener(){
			public void actionPerformed(final ActionEvent the_e){
				if (!field.getText().equals("")){
					loginAction(field.getText());
				} else {
					JOptionPane.showMessageDialog(Login.this, "Please enter a name.");
				}
			}
		});
		add(field);
		add(login);
	}
	
	/**
	 * Attemps to login in the user based on the string passed in.
	 * @param the_name The name of the user that was typed in.
	 */
	private void loginAction(final String the_name){
		final Database db = Database.getInstance();
		final ChangePanelAction action = 
				new ChangePanelAction(this, new ConferenceSelect(new User(the_name)));
		if (db.getUsers().contains(new User(the_name))){
			JOptionPane.showMessageDialog(this,"You have succesfully logged in.");
			((PR3KFrame) getTopLevelAncestor()).setUser(new User(the_name));
			action.actionPerformed(null);
		}
		else {	
			int response = JOptionPane.showConfirmDialog(this, "Would you like to register?");
			if (response == JOptionPane.YES_OPTION){
				db.addUser(new User(the_name));
				((PR3KFrame) getTopLevelAncestor()).setUser(new User(the_name));
				action.actionPerformed(null);
			}
		}
	}
}
