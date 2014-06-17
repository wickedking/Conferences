package pr3k.guiv2;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import pr3k.actions.ChangePanelAction;
import pr3k.database.Database;
import pr3k.model.Conference;
import pr3k.model.User;

@SuppressWarnings("serial")
/**
 * This is the gui class for creating a subprogram chair from a reviewer.
 * @author Jesse Kitterman
 * @version November 2013
 */
public class AssignChair extends PR3KPanel{

	/**
	 * These are the headers for the JTable to be displayed
	 */
//	private static final String[] P_HEADERS = 
//		{"Title", "Deadline", "Conf. Date", "Status"};

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
	private PR3KTable my_users;

	/**
	 * This is the JTable that holds the paper info to display. Non Editable.
	 */
//	private PR3KTable my_papers;

	/**
	 * This is the 2d object array of users to be insterted into the JTable
	 */
	private Object[][] my_user_data;

	/**
	 * This is the 2d object array of papers to be insterted into the JTable
	 */
//	private Object[][] my_paper_data;

	/**
	 * Constructor Sets up the gui and displays.
	 * @param the_user The Current user.
	 * @param the_conf The Current Conference.
	 */
	public AssignChair(final User the_user, final Conference the_conf){
		super(the_user, the_conf);
		initPanel();

	}

	/**
	 * Creates the panels and buttons for display. 
	 */
	private void initPanel()	{
		final JPanel center = new JPanel();
		//initPaper(center);
		initUser(center);
        /**
         * The south panel
         */
		final JPanel south = new JPanel();
        /**
         * The assign button
         */
		JButton assign = new JButton("Assign");
		/**
		 * The cancel button
		 */
		JButton cancel = new JButton("Cancel");
        /**
         * Anonymous action listener for assign button
         * which get the users from the dataBase and promote the
         * user to either a subprogram chair 
         */
		assign.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int row = my_users.getSelectedRow();
				User user = (User) my_users.getValueAt(row, 0);
				Database.getInstance().addSubChair(user, AssignChair.this.getConf());
				JOptionPane.showMessageDialog(null, "Promoted " + user + " to Sub-Chair");
				new ChangePanelAction(AssignChair.this, 
						new ConferenceInfo(getUser(), 
								AssignChair.this.getConf())).actionPerformed(null);
			}
		});
        /**
         * Anonymous action listener for cancel window
         */
		cancel.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				new ChangePanelAction(AssignChair.this, 
						new ConferenceInfo(getUser(), 
								AssignChair.this.getConf())).actionPerformed(null);
			}


		});
        /**
         * Adding the assign and cancel button to south panel
         */
		south.add(assign);
		south.add(cancel);
         /**
          * Adding the south and center panel to this panel
          */
		add(center, BorderLayout.CENTER);
		add(south, BorderLayout.SOUTH);
	}

	/**
	 * Creates the Table for users for display.
	 * @param the_center The panel to put the table in.
	 */
	private void initUser(final JPanel the_center){
		final List<User> users = DB.getReviewers(getConf());
		my_user_data = new Object[users.size()][U_HEADERS.length];
		for (int row = 0; row < users.size(); row++){
			my_user_data[row][0] = users.get(row);
			//TODO Try to disable the users that are the author of the selected paper
			//at runtime.
			my_user_data[row][1] = DB.getRole(users.get(row), getConf());
		}
		my_users = new PR3KTable(my_user_data, U_HEADERS);
		final JScrollPane pane = new JScrollPane(my_users);
		the_center.add(pane);
	}

//	private void initPaper(final JPanel the_center){
//		final List<Paper>papers = DB.getPapers(getConf());
//		my_paper_data = new Object[papers.size()][P_HEADERS.length];
//		for (int row = 0; row < papers.size(); row++){
//			final Paper p = papers.get(row);
//			my_paper_data[row][0] = p;
//			my_paper_data[row][1] = p.getConference().getDeadline();
//			my_paper_data[row][2] = p.getConference().getDate();
//			my_paper_data[row][3] = p.getReviewStatus();
//		}
//		my_papers = new PR3KTable(my_paper_data, P_HEADERS);
//		the_center.add(new JScrollPane(my_papers));
//	}
}
