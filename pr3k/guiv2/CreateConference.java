package pr3k.guiv2;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Date;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import pr3k.actions.ChangePanelAction;
import pr3k.database.Database;
import pr3k.model.Conference;
import pr3k.model.User;

import com.toedter.calendar.JDateChooser;


@SuppressWarnings("serial")
/**
 * This gui is for creating a new conference based on user input for data.
 * @author Brandon Martin
 * @version November 2013
 */
public class CreateConference extends PR3KPanel {

	/**
	 * Text Field to accept the title of the conference from the user.
	 */
	private JTextField my_title = new JTextField(15);
	
	/**
	 * Mini Popup to choose the date of the conference
	 */
	private final JDateChooser my_date_chooser = new JDateChooser();
	
	/**
	 * Mini Popup to choose the deadline for sumbission of papers to the conference.
	 */
	private final JDateChooser my_deadline_chooser = new JDateChooser();
	
	/**
	 * Construtor, creates the gui and displays.
	 * @param the_user
	 * @precondition my_title has a string
	 * @precondition my_date_chooser has a date choosen
	 * @precondition my_deadline_chooser has a date choosen and before my_date_chooser
	 * @postCondition A new Conference is created with my_title as the title, and the 2 dates as the dates.
	 */
	public CreateConference(final User the_user){
		super(the_user, null);
		my_date_chooser.setDateFormatString("yyyy-MM-dd");
		my_deadline_chooser.setDateFormatString("yyyy-MM-dd");
		my_date_chooser.setMinSelectableDate(new java.util.Date());
		//TODO this should be dynamic for when the user selects a date.
		my_date_chooser.getDateEditor().addPropertyChangeListener(
				new PropertyChangeListener(){
					public void propertyChange(final PropertyChangeEvent e){
						if ("date".equals(e.getPropertyName())){
							my_deadline_chooser.setEnabled(true);
							my_deadline_chooser.setMaxSelectableDate(my_date_chooser.getDate());
							my_deadline_chooser.setMinSelectableDate(new java.util.Date());
						}
					}
				});
		my_deadline_chooser.setEnabled(false);
		initPanel();
		initButtons();
	}

	/**
	 * Creates the buttons for the gui.
	 */
	private void initButtons()
	{
		final JPanel panel = new JPanel();
		final JButton create = new JButton("Create");
		create.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				create();
			}
		});
		final JButton cancel = new JButton("Cancel");
		
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new ChangePanelAction(CreateConference.this, 
						new ConferenceSelect(getUser())).actionPerformed(null);
			}
		});
		
		panel.add(create);
		panel.add(cancel);
		
		add(panel, BorderLayout.SOUTH);
	}

	/**
	 * Creates all the panels for the gui.
	 */
	private void initPanel()
	{
		final JPanel panel = new JPanel(new GridLayout(2,3));

		final String[] hints = 
			{"Title", "Date Held 'yyyy-mm-dd", "Paper Deadline 'yyyy-mm-dd'"};
		for (final String hint : hints){
			JPanel inner = new JPanel();
			inner.add(new JLabel(hint));
			panel.add(inner);
		}
		JPanel inner = new JPanel();
		inner.add(my_title);
		panel.add(inner);
		
		inner = new JPanel();
		inner.add(my_date_chooser);
		panel.add(inner);
		
		inner = new JPanel();
		inner.add(my_deadline_chooser);
		panel.add(inner);
		
		add(panel, BorderLayout.CENTER);
	}
	
	/**
	 * This takes the inputed info and attempts to create a new conference.
	 */
	private void create(){
		if (my_title.getText().equals("")){
			JOptionPane.showMessageDialog(this, "Please enter a title.");
			return;
		}
		try
		{
			final Conference c = new Conference(my_title.getText(), 
					new Date(my_date_chooser.getDate().getTime()), 
					new Date(my_deadline_chooser.getDate().getTime()), getUser());
			Database.getInstance().addConference(c);

			new ChangePanelAction(CreateConference.this, 
					new ConferenceSelect(getUser())).actionPerformed(null);
			//TODO print out result.
		} catch (final NullPointerException the_e){
			JOptionPane.showMessageDialog(this, 
					"One or more of the dates you entered was incorrect. "
					+ "Please try again or use the calendar selector");
		}
	}
}
