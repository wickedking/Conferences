/**
 * Displays all of the papers submitted by an author.
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
import pr3k.model.Conference;
import pr3k.model.Paper;
import pr3k.model.User;


/**
 * This gui shows all of the papers that a user has submitted and some info about them.
 * @author Edward Bassan
 * @version November 2013
 */
@SuppressWarnings("serial")
public class ViewPapers extends PR3KPanel{
	
	/**
	 * These are the headers for the JTable to be displayed
	 */
	private static final String[] HEADERS = {"Title", "Conference", "Deadline", "Conf. Date", "Status"};
	
	/**
	 * Button to bring up the revise screen.
	 */
	private final JButton my_revise = new JButton("Revise");
	
	/**
	 * Button to delete the currently selected paper.
	 */
	private final JButton my_delete = new JButton("Delete");
	
	/**
	 * Button to go back to the previous screen.
	 */
	private final JButton my_cancel = new JButton("Cancel");
	
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
	 */
	public ViewPapers(final User the_user){
		this(the_user, null);
	}
	
	/**
	 * Overloaded constructor.
	 * @param the_user The current user.
	 * @param the_conf The current conference.
	 */
	public ViewPapers(final User the_user, final Conference the_conf){
		super(the_user, the_conf);
		initData();
		initButtons();
		initPanel();
	}
	
	/**
	 * Creates the Scroll panel.
	 */
	private void initPanel()
	{
		final JScrollPane pane = new JScrollPane(my_table);
		add(pane, BorderLayout.CENTER);
	}
	
	/**
	 * Creates the button for the gui.
	 */
	private void initButtons(){
		final JPanel panel = new JPanel();
		final BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
		panel.setLayout(layout);

		JPanel inner = new JPanel();
		
		my_revise.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					final Paper p = (Paper) my_table.getValueAt(my_table.getSelectedRow(), 0);
					new ChangePanelAction(ViewPapers.this, 
							new RevisePaper(getUser(), p.getConference(), p)).
								actionPerformed(null);
				}
		});
		
		my_delete.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				Paper paper = (Paper) my_table.getValueAt(my_table.getSelectedRow(), 0);
                int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete your paper?");
                if(response == JOptionPane.YES_OPTION) {
                    Database.getInstance().removePaper(paper);
                    new ChangePanelAction(ViewPapers.this, new ViewPapers(ViewPapers.this.getUser())).actionPerformed(null);
                }
			}
		});
		
		my_cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new ChangePanelAction(ViewPapers.this, 
						new ConferenceSelect(getUser())).actionPerformed(null);
			}
		});
		
		inner.add(my_revise);
		panel.add(inner);

		inner = new JPanel();
		
		inner.add(my_delete);
		panel.add(inner);
		
		inner = new JPanel();

		inner.add(my_cancel);
		panel.add(inner);
		
		add(panel, BorderLayout.EAST);
		
	}

	/**
	 * Populates the table with the paper info.
	 */
	private void initData(){
		final List<Paper> papers = getConf() == null ? 
				Database.getInstance().getPapers(getUser()) : 
					Database.getInstance().getPapers(getUser(), getConf());
		my_data = new Object[papers.size()][HEADERS.length];
		for (int row = 0; row < papers.size(); row++){
			final Paper p = papers.get(row);
			my_data[row][0] = p;
			my_data[row][1] = p.getConference().getName();
			my_data[row][2] = p.getConference().getDeadline();
			my_data[row][3] = p.getConference().getDate();
			my_data[row][4] = p.getAcceptanceStatus().getMessage();
		}
		if (papers.size() > 0){
			my_table = new PR3KTable(my_data, HEADERS);
		}
		if (papers.size() <= 0) {
			my_revise.setEnabled(false);
			my_delete.setEnabled(false);
		} else {
			my_revise.setEnabled(true);
			my_delete.setEnabled(true);
		}
	}
}
