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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import pr3k.actions.ChangePanelAction;
import pr3k.database.Database;
import pr3k.model.AcceptanceStatus;
import pr3k.model.Conference;
import pr3k.model.MaxPapersException;
import pr3k.model.Paper;
import pr3k.model.User;


@SuppressWarnings("serial")
/**
 * This gui is for showing and allowing a program chair to make a final acceptence on a paper.
 * @author Cody Shafer
 * @version November 2013
 */
public class FinalAcc extends PR3KPanel {
	
	/**
	 * These are the headers for the JTable to be displayed
	 */
	private static final String[] COL_NAMES = {"Title", "Conference", "Accept Status", "Review Status"};

	/**
	 * This is the 2d object array of papers to be insterted into the JTable
	 */
	private Object[][] my_data;

	/**
	 * This is the JTable that holds the paper info to display. Non Editable.
	 */
	private PR3KTable my_table;

	/**
	 * Reference to the paper chosen in the my_table.
	 */
	private Paper my_paper;

	/**
	 * Constructor, Creates the gui and displays.
	 * @param the_user
	 * @param the_conf
	 */
	public FinalAcc(final User the_user, final Conference the_conf){
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

		JPanel inner = new JPanel();

		final JButton accept = new JButton("Accept");
		accept.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (my_table.getSelectedRow() != -1){
					final int row = my_table.getSelectedRow();
					my_paper = (Paper) my_table.getValueAt(row, 0);
					Paper new_paper = new Paper(my_paper.getTitle(), my_paper.getAuthor(),
							my_paper.getFile(), my_paper.getConference(), 
							my_paper.getReviewStatus(), AcceptanceStatus.ACCEPT);

					if(my_paper.getAcceptanceStatus() == AcceptanceStatus.ACCEPT ||
							my_paper.getAcceptanceStatus() == AcceptanceStatus.REJECT){
							int check = JOptionPane.showConfirmDialog(null, "Already made Final Reccomend. Do you wish to Change Acceptence status?");
							if(check == JOptionPane.YES_OPTION){
								try {
									Database.getInstance().updatePaper(my_paper, new_paper);
								} catch (MaxPapersException e1) {
									//Should never reach just updating paper
								}
								new ChangePanelAction(FinalAcc.this, new ConferenceInfo(getUser(), 
										getConf())).actionPerformed(null);
							}
					} else {
						try {
							Database.getInstance().updatePaper(my_paper, new_paper);
						} catch (MaxPapersException e1) {
							//should never reach. just updating paper.
						}
						new ChangePanelAction(FinalAcc.this, new ConferenceInfo(getUser(), 
								getConf())).actionPerformed(null);
					}
	


		
				}
			}
		});

		final JButton reject = new JButton("Reject");
		reject.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (my_table.getSelectedRow() != -1){
					final int row = my_table.getSelectedRow();
					my_paper = (Paper) my_table.getValueAt(row, 0);
					Paper new_paper = new Paper(my_paper.getTitle(), my_paper.getAuthor(),
							my_paper.getFile(), my_paper.getConference(), my_paper.getReviewStatus(), AcceptanceStatus.REJECT);
					
					if(my_paper.getAcceptanceStatus() == AcceptanceStatus.ACCEPT ||
							my_paper.getAcceptanceStatus() == AcceptanceStatus.REJECT){
							int check = JOptionPane.showConfirmDialog(null, "Already made Final Reccomend. Do you wish to Change Acceptence status?");
							if(check == JOptionPane.YES_OPTION){
								try {
									Database.getInstance().updatePaper(my_paper, new_paper);
								} catch (MaxPapersException e1) {
									//Should never reach, just updating paper.
								}
								new ChangePanelAction(FinalAcc.this, new ConferenceInfo(getUser(), 
										getConf())).actionPerformed(null);
							}
					} else {
						try {
							Database.getInstance().updatePaper(my_paper, new_paper);
						} catch (MaxPapersException e1) {
							//should never reach, just updating paper.
						}
						new ChangePanelAction(FinalAcc.this, new ConferenceInfo(getUser(), 
								getConf())).actionPerformed(null);
					}
					
				}
			}
		});

		inner.add(accept);
		panel.add(inner);
		inner = new JPanel();

		inner.add(reject);
		panel.add(inner);
		inner = new JPanel();

		final JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				new ChangePanelAction(FinalAcc.this, 
						new ConferenceInfo(getUser(), 
								FinalAcc.this.getConf())).actionPerformed(null);
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
			my_data[row][2] = papers.get(row).getAcceptanceStatus();
			my_data[row][3] = papers.get(row).getReviewStatus();
		}
		my_table = new PR3KTable(my_data, COL_NAMES);
	}

	/**
	 * Adds the table to the panel.
	 */
	private void initPanel(){
		final JScrollPane pane = new JScrollPane(my_table);
		add(pane, BorderLayout.CENTER);
	}
}