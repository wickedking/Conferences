/**
 * Panel displays the papers that are still pending a review.
 */
package pr3k.guiv2;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import pr3k.actions.ChangePanelAction;
import pr3k.database.Database;
import pr3k.model.Conference;
import pr3k.model.Paper;
import pr3k.model.User;
import pr3k.model.UserType;

/**
 * This gui is to allow a reviewer to see what papers s/he has left to review.
 * @author Cody Shafer
 * @version November 2013
 */
@SuppressWarnings("serial")
public class ViewPending extends PR3KPanel {
	
	/**
	 * These are the headers for the JTable to be displayed
	 */
	private static final String[] COL_NAMES = {"Title", "Conference", "Author", "Review Status"};

	/**
	 * This is the 2d object array of papers to be insterted into the JTable
	 */
	private Object[][] my_data;

	/**
	 * This is the JTable that holds the paper info to display. Non Editable.
	 */
	private PR3KTable my_table;

	/**
	 * Constructor.
	 * @param the_user The current user.
	 * @param the_conf The current Conference.
	 */
	public ViewPending(final User the_user, final Conference the_conf){
		super(the_user, the_conf);
		initData();
		initPanel();
		initButtons();
	}

	/**
	 * Creates all of the buttons for the gui.
	 */
	private void initButtons()
	{
		final JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JPanel inner = new JPanel();
	
		final JButton review = new JButton("Review");

		review.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (my_table.getSelectedRow() != -1){
					final int row = my_table.getSelectedRow();
					new ChangePanelAction(ViewPending.this, new RecommendPanel(getUser(), 
							(Paper) my_table.getValueAt(row, 0))).actionPerformed(null);
				}
			}
		});    
		inner.add(review);
		panel.add(inner);
		

		inner = new JPanel();

		final JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				new ChangePanelAction(ViewPending.this, 
						new ConferenceInfo(getUser(), 
								ViewPending.this.getConf())).actionPerformed(null);
			}
		});
		inner.add(cancel);
		panel.add(inner);

		add(panel, BorderLayout.EAST);

	}

	/**
	 * Populates the table with the correct level of paper info.
	 */
	private void initData()
	{
		final UserType type = Database.getInstance().getRole(getUser(), getConf());
		List<Paper> papers = new ArrayList<Paper>();
		if (type == UserType.PROGCHAIR){
			papers = Database.getInstance().getUnreviewed(getConf());	
		}
		else if (type == UserType.SUBCHAIR){
			papers = Database.getInstance().getSubChairPapers(getUser(), getConf());
		}
		else if (type == UserType.REVIEWER){
			papers = Database.getInstance().getReviewerPapers(getUser(), getConf());
		}
		my_data = new Object[papers.size()][COL_NAMES.length];
		for (int row = 0; row < papers.size(); row++){
			my_data[row][0] = papers.get(row);
			my_data[row][1] = papers.get(row).getConference();
			my_data[row][2] = papers.get(row).getAuthor();
			my_data[row][3] = papers.get(row).getReviewStatus();

		}
		my_table = new PR3KTable(my_data, COL_NAMES);
	}

	/**
	 * Creates the panel to be displayed.
	 */
	private void initPanel(){
		final JScrollPane pane = new JScrollPane(my_table);
		add(pane, BorderLayout.CENTER);
	}
}
