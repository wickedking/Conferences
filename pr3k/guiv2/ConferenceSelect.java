package pr3k.guiv2;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import pr3k.actions.ChangePanelAction;
import pr3k.database.Database;
import pr3k.model.Conference;
import pr3k.model.User;


@SuppressWarnings("serial")
/**
 * This gui is for showing all available conferences and their basic info for selection.
 * @author Cody Shafer
 * @version November 2013
 */
public class ConferenceSelect extends PR3KPanel {
	
	/**
	 * These are the headers for the JTable to be displayed
	 */
	private static final String[] COL_NAMES = {"Title", "Date", "Deadline", "Program Chair"};

	/**
	 * This is the 2d object array of conferences to be inserted into the JTable
	 */
	private final Object[][] data;
	
	/**
	 * Button to select a specific conference from the table.
	 */
	private final JButton my_select = new JButton("Select");
	
	/**
	 * Button for a user to create a new conference.
	 */
	private final JButton my_create = new JButton("Create a Conference");

	/**
	 * This is the JTable that holds the conference info to display. Non Editable.
	 */
	private PR3KTable my_table;

	/**
	 * Constructor, creates the gui and displays.
	 * @param the_user The current user.
	 */
	public ConferenceSelect(final User the_user) {
		super(the_user, null);
		data = new Object[Database.getInstance().getConferences().size()][COL_NAMES.length];
		initData();
		initTablePanel();
		initButtonPanel();
	}

	/**
	 * Creates all of the buttons for the gui.
	 */
	private void initButtonPanel()
	{
		final JPanel panel = new JPanel();

		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		my_select.setAlignmentX(Component.CENTER_ALIGNMENT);
		my_select.setAlignmentY(Component.CENTER_ALIGNMENT);
		my_select.addActionListener(new ActionListener(){
			public void actionPerformed(final ActionEvent the_e){
				if (my_table.getSelectedRow() >= 0){
					new ChangePanelAction(ConferenceSelect.this, 
							new ConferenceInfo(getUser(), 
									selectConference())).actionPerformed(null);
				}
			}
		});

		JPanel inner = new JPanel();
        final JLabel user_name = new JLabel("Hello, " + getUser().getUserName() + "!");
        inner.add(user_name);
        panel.add(inner);
	    

		inner = new JPanel();
        inner.add(my_select);
        panel.add(inner);
		
		inner = new JPanel();
		my_create.addActionListener(new ChangePanelAction(this, 
				new CreateConference(getUser())));
		inner.add(my_create);
		panel.add(inner);

		inner = new JPanel();
		final JButton viewAll = new JButton("View Your Papers");
		viewAll.addActionListener(new ChangePanelAction(this, new ViewPapers(getUser())));
		inner.add(viewAll);
		panel.add(inner);

		inner = new JPanel();
		final JButton exit = new JButton("Exit");
		exit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				((JFrame) getTopLevelAncestor()).dispose();
			}
		});
		inner.add(exit);
		panel.add(inner);
		
		
		add(panel, BorderLayout.EAST);
	}

	/**
	 * populates the table with the user data.
	 */
	private void initData(){
		final List<Conference> confs = Database.getInstance().getConferences();
		for (int row = 0; row < data.length; row++){
			final Conference c = confs.get(row);
			data[row][0] = c.getName();
			data[row][1] = c.getDate();
			data[row][2] = c.getDeadline();
			data[row][3] = c.getProgramChair();
		}
	}

	/**
	 * Creates the table of conferences and adds it to the gui.
	 */
	private void initTablePanel(){
		my_table = new PR3KTable(data, COL_NAMES);
		my_table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					my_select.doClick();
				}
			}
		});
		JScrollPane scroll_pane = new JScrollPane(my_table);
		my_table.setFillsViewportHeight(true);
		add(scroll_pane, BorderLayout.CENTER);
	}

	/**
	 * Gets the currently selected conference from the table.
	 * @return The conference that is selected.
	 */
	private Conference selectConference(){
		int row = my_table.getSelectedRow();

		final String title = (String) my_table.getValueAt(row, 0);
		final Date date = (Date) my_table.getValueAt(row, 1);
		final Date deadline = (Date) my_table.getValueAt(row, 2);
		final User prog = (User) my_table.getValueAt(row, 3);
		return new Conference(title, date, deadline, prog);
	}
}
