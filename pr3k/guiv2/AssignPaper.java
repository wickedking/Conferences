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
import pr3k.model.InvalidAssignmentException;
import pr3k.model.MaxPapersException;
import pr3k.model.Paper;
import pr3k.model.User;

@SuppressWarnings("serial")
/**
 * This gui is for assigning a paper to a reviewer to review.
 * @author Brandon Martin
 * @version November 2013
 *
 */
public class AssignPaper extends PR3KPanel {

	
	/**
	 * These are the headers for the JTable to be displayed
	 */
	private static final String[] P_HEADERS = 
		{"Title", "Deadline", "Conf. Date", "Author", "Status", "Acceptence"};

	/**
	 * These are the headers for the JTable to be displayed
	 */
	private static final String[] U_HEADERS = 
		{"Username", "Role", "# Reviewing"};

	/**
	 * A instance of the database for info storage/retrieval.
	 */
	private static final Database DB = Database.getInstance();

	/**
	 * This is the JTable that holds the user info to display. Non Editable.
	 */
	private JTable my_users;

	/**
	 * This is the JTable that holds the paper info to display. Non Editable.
	 */
	private JTable my_papers;

	/**
	 * This is the 2d object array of users to be insterted into the JTable
	 */
	private Object[][] my_user_data;

	/**
	 * This is the 2d object array of papers to be insterted into the JTable
	 */
	private Object[][] my_paper_data;

	/**
	 * Constructor to create and start the gui.
	 * @param the_user The current User.
	 * @param the_conf The current Conference.
	 */
	public AssignPaper(final User the_user, final Conference the_conf){
		super(the_user, the_conf);
		initPanel();
	}
	
	//TODO Can add user with no paper selected. If paper selected ensure they are not author.
	/**
	 * Creates the gui that is to be displayed.
	 */
	private void initPanel()    {
		final JPanel center = new JPanel();
		initPaper(center);
		initUser(center);

		final JPanel south = new JPanel();

		JButton assign = new JButton("Assign");
		JButton cancel = new JButton("Cancel");

		assign.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int paper_row = my_papers.getSelectedRow();
				if (paper_row < 0){
					return;
				}
				
				int row = my_users.getSelectedRow();
				final User user = (User) my_users.getValueAt(row, 0);
				
				final Paper paper = (Paper) my_papers.getValueAt(paper_row, 0);
				try {
					if (!Database.getInstance().getReviewerPapers(user, getConf()).contains(paper)){
						Database.getInstance().assignPaperToReviewer(user, paper);
						JOptionPane.showMessageDialog(AssignPaper.this, "Paper added to " + user + 
							" for review");
						final PR3KFrame frame = ((PR3KFrame) getTopLevelAncestor());
						frame.remove(AssignPaper.this);
						frame.add(new AssignPaper(getUser(), getConf()));
						frame.pack();
					}
					else {
						JOptionPane.showMessageDialog(AssignPaper.this, 
								"Paper already assigned to user.");
					}

				} catch (InvalidAssignmentException e) {
					JOptionPane.showMessageDialog(AssignPaper.this, e.getMessage()
							+ "\n Please select a different paper or user.");
				} catch (MaxPapersException e)
				{
					JOptionPane.showMessageDialog(AssignPaper.this, e.getMessage());
				}
			}

		});

		cancel.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				new ChangePanelAction(AssignPaper.this, 
						new ConferenceInfo(getUser(), 
								AssignPaper.this.getConf())).actionPerformed(null);
			}


		});

		south.add(assign);
		south.add(cancel);

		add(center, BorderLayout.CENTER);
		add(south, BorderLayout.SOUTH);
	}

	/**
	 * Populates the table of users.
	 * @param the_center The panel to put the table into.
	 */
	private void initUser(final JPanel the_center){
		final List<User> users = DB.getReviewers(getConf());
		my_user_data = new Object[users.size()][U_HEADERS.length];
		for (int row = 0; row < users.size(); row++){
			my_user_data[row][0] = users.get(row);
			//TODO Try to disable the users that are the author of the selected paper
			//at runtime.
			my_user_data[row][1] = DB.getRole(users.get(row), getConf());
			my_user_data[row][2] = DB.getReviewerPapers(users.get(row), getConf());
		}
		my_users = new PR3KTable(my_user_data, U_HEADERS);
		final JScrollPane pane = new JScrollPane(my_users);
		the_center.add(pane);
	}

	/**
	 * Populates the table with the paper info.
	 * @param the_center The panel to put the table into.
	 */
	private void initPaper(final JPanel the_center){
		final List<Paper>papers = DB.getPapers(getConf());
		my_paper_data = new Object[papers.size()][P_HEADERS.length];
		for (int row = 0; row < papers.size(); row++){
			final Paper p = papers.get(row);
			my_paper_data[row][0] = p;
			my_paper_data[row][1] = p.getConference().getDeadline();
			my_paper_data[row][2] = p.getConference().getDate();
			my_paper_data[row][3] = p .getAuthor();
			my_paper_data[row][4] = p.getReviewStatus();
			my_paper_data[row][4] = p.getAcceptanceStatus();
		}
		my_papers = new PR3KTable(my_paper_data, P_HEADERS);
		the_center.add(new JScrollPane(my_papers));
	}
}
