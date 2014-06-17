/**
 * Panel displays the papers that are still pending a review.
 */
package pr3k.guiv2;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
 * This gui allows a subprogram chair to see what papers have already been reviewed,
 * and then review them themselves.
 * @author Jesse Kitterman
 * @version November 2013
 */
@SuppressWarnings("serial")
public class ViewReviewed extends PR3KPanel {
	
	/**
	 * These are the headers for the JTable to be displayed
	 */
	private static final String[] COL_NAMES = {"Title", "Conference", "Author", "Review Status"};

	/**
	 * This is the 2d object array to be insterted into the JTable
	 */
	private Object[][] my_data;

	/**
	 * This is the JTable that holds the paper info to display. Non Editable.
	 */
	private PR3KTable my_table;	

	/**
	 * Constructor
	 * @param the_user The current user.
	 * @param the_conf The current conference.
	 */
	public ViewReviewed(final User the_user, final Conference the_conf){
		super(the_user, the_conf);
		initData();
		initPanel();
		initButtons();
	}

	/**
	 * Creates all of the buttons for the panel.
	 */
	private void initButtons()
	{
		final JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		
		if(Database.getInstance().getRole(getUser(), getConf()) == UserType.PROGCHAIR) {
		    
	    } else {
		    final JButton review = new JButton("Review");
		    review.addActionListener(new ActionListener(){
		        public void actionPerformed(ActionEvent e){
		            new ChangePanelAction(ViewReviewed.this, new RecommendPanel(
						ViewReviewed.this.getUser(),(Paper) my_table.getValueAt(my_table.getSelectedRow(), 0))).actionPerformed(null);;

		        }
		        
		        
		    });
            JPanel inner = new JPanel();
            
            inner.add(review);
            panel.add(inner);
		}
		

		JPanel inner = new JPanel();
		final JButton cancel = new JButton("Back");

		cancel.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				new ChangePanelAction(ViewReviewed.this, 
						new ConferenceInfo(getUser(), 
								ViewReviewed.this.getConf())).actionPerformed(null);
			}
		});


		inner.add(cancel);
		panel.add(inner);

		add(panel, BorderLayout.EAST);
	}

	/**
	 * Populates the table with the paper info.
	 */
	private void initData()
	{
		final List<Paper> papers = Database.getInstance().getReviewed(getConf());
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
