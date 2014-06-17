package pr3k.guiv2;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import pr3k.actions.ChangePanelAction;
import pr3k.database.Database;
import pr3k.model.Conference;
import pr3k.model.User;


@SuppressWarnings("serial")
/**
 * This gui is for making a user into a reviewer for a conference.
 * @author Cody Shafer
 * @version November 2013
 */
public class AssignReviewer extends PR3KPanel{
	
	/**
	 * These are the headers for the JTable to be displayed
	 */
	private static final String[] U_HEADERS = 
		{"Username", "Role"};

	/**
	 * A instance of the database for info storage/retrieval.
	 */
	private static final Database DB = Database.getInstance();

	/**
	 * This is the JTable that holds the user info to display. Non Editable.
	 */
	private JTable my_users;

	/**
	 * This is the 2d object array of users to be insterted into the JTable
	 */
	private Object[][] my_user_data;

	/**
	 * Constructor, creates the gui and displays.
	 * @param the_user The current User.
	 * @param the_conf The current Conference.
	 */
	public AssignReviewer(final User the_user, final Conference the_conf){
		super(the_user, the_conf);
		initPanel();
	}
	
	//TODO Can add user with no paper selected. If paper selected ensure they are not author.
	/**
	 * Creates the gui.
	 */
	private void initPanel()	{
		final JPanel center = new JPanel();
		initUser(center);

		final JPanel south = new JPanel();

		JButton assign = new JButton("Assign");
		JButton cancel = new JButton("Cancel");

		assign.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int row = my_users.getSelectedRow();
				User user = (User) my_users.getValueAt(row, 0);			
				Database.getInstance().addReviewer(user, AssignReviewer.this.getConf());
					
				JOptionPane.showMessageDialog(null, "Reviewer Added");
				new ChangePanelAction(AssignReviewer.this, 
						new ConferenceInfo(getUser(), 
								AssignReviewer.this.getConf())).actionPerformed(null);
				
			}

		});

		cancel.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				new ChangePanelAction(AssignReviewer.this, 
						new ConferenceInfo(getUser(), 
								AssignReviewer.this.getConf())).actionPerformed(null);
			}
		});

		south.add(assign);
		south.add(cancel);

		add(center, BorderLayout.CENTER);
		add(south, BorderLayout.SOUTH);
	}

	/**
	 * Populates the table with user info.
	 * @param the_center The panel to put the table into.
	 */
	private void initUser(final JPanel the_center){
		final List<User> users = DB.getUsers();
		my_user_data = new Object[users.size()][U_HEADERS.length];
		for (int row = 0; row < users.size(); row++){
			
			my_user_data[row][0] = users.get(row);
			my_user_data[row][1] = DB.getRole(users.get(row), getConf());
		}
		my_users = new PR3KTable(my_user_data, U_HEADERS);
		final JScrollPane pane = new JScrollPane(my_users);
		the_center.add(pane);
	}

}
