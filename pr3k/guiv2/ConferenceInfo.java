package pr3k.guiv2;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import pr3k.actions.ChangePanelAction;
import pr3k.database.Database;
import pr3k.model.Conference;
import pr3k.model.User;


@SuppressWarnings("serial")
/**
 * This gui is for showing all of the information about a selected conference.
 * @author Brandon Martin
 * @version November 2013
 */
public class ConferenceInfo extends PR3KPanel {

	/**
	 * Constructor, Creates the gui and displays.
	 * @param the_user The current User.
	 * @param the_conference The current Conference.
	 */
	public ConferenceInfo(final User the_user, final Conference the_conference)
	{
		super(the_user, the_conference);
		initInfo();
		initButtons();	
		this.getTopLevelAncestor();

		
	}

	/**
	 * Creates the Gui and all the fixins.
	 */
	private void initInfo()
	{
		final JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(2, 2));
		
		final JLabel name = new JLabel("Conference: " + getConf().getName() + " ");
		name.setAlignmentX(CENTER_ALIGNMENT);
		name.setAlignmentY(CENTER_ALIGNMENT);
		
		final JLabel date = new JLabel("Start Date: " + getConf().getDate().toString() + " ");
		date.setAlignmentX(CENTER_ALIGNMENT);
		date.setAlignmentY(CENTER_ALIGNMENT);
		
		final JLabel deadline = new JLabel("Submisson Deadline: " + getConf().getDeadline().toString() + " ");
		deadline.setAlignmentX(CENTER_ALIGNMENT);
		deadline.setAlignmentY(CENTER_ALIGNMENT);
		
		final JLabel prog = new JLabel("Program Chair: " + getConf().getProgramChair().getUserName() + " ");
		prog.setAlignmentX(CENTER_ALIGNMENT);
		prog.setAlignmentY(CENTER_ALIGNMENT);
		
		panel.add(name);
		panel.add(prog);
		panel.add(date);
		panel.add(deadline);
		
		add(panel, BorderLayout.NORTH);
	}

	//TODO long method.
	/**
	 * Creates all the buttons according to permisson level.
	 */
	private void initButtons()
	{
		
		final JPanel panel = new JPanel(new GridLayout(3, 4));
		
		//TODO put into for loop.
		final JButton viewPending = new JButton("View Pending Papers");
		viewPending.addActionListener(new ChangePanelAction(this, 
				new ViewPending(getUser(), getConf())));
		
		final JButton viewReviewed = new JButton("View Reviewed Papers");
		viewReviewed.addActionListener(new ChangePanelAction(this, 
				new ViewReviewed(getUser(), getConf())));
		
		final JButton assignReviewer = new JButton("Promote User to Reviewer");
		assignReviewer.addActionListener(new ChangePanelAction(this, 
				new AssignReviewer(getUser(), getConf())));
		
		//final JButton makeRec = new JButton("Make Recommendation on Paper");		
		
		final JButton assignChair = new JButton("Assign a Sub-Chair");
		assignChair.addActionListener(new ChangePanelAction(this,
				new AssignChair(getUser(), getConf())));
		
		final JButton assignPaperToChair = new JButton("Assign Paper to Reviewer");
		assignPaperToChair.addActionListener(new ChangePanelAction(this,
				new AssignPaper(getUser(), getConf())));
		
		final JButton makeFinal = new JButton("Final Acceptance on Paper");
		makeFinal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new ChangePanelAction(ConferenceInfo.this, new FinalAcc(getUser(),
						getConf())).actionPerformed(null);
			}
		});
		
		
		final JButton back = new JButton("Back");
		back.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				new ChangePanelAction(ConferenceInfo.this,
						new ConferenceSelect(getUser())).actionPerformed(null);
			}
		});
		
		final JButton[] buttons = 
			{viewPending, viewReviewed, assignReviewer, assignChair, 
				assignPaperToChair, makeFinal, back};
		
		final JButton submit = new JButton("Submit a Paper");
		submit.addActionListener(new ChangePanelAction(this, 
				new SubmitPaper(getConf(), getUser())));
		
		final JButton viewPapers = new JButton("View Papers");
		viewPapers.addActionListener(new ChangePanelAction(this, 
				new ViewPapers(getUser(), getConf())));	
		
		//TODO put this in its own method.
		JPanel inner = new JPanel();
		

		
		switch (Database.getInstance().getRole(getUser(), getConf())){
		case USER:
			inner.add(submit);
			panel.add(inner);

			break;
		case AUTHOR:
			inner.add(submit);
			panel.add(inner);
			inner = new JPanel();
			inner.add(viewPapers);
			panel.add(inner);
			inner = new JPanel();

			break;
		case REVIEWER:	
			inner.add(submit);
			panel.add(inner);
			inner.add(buttons[0]);
			panel.add(inner);
			break;
		case SUBCHAIR:
			panel.setLayout(new GridLayout(2, 4));
			inner.add(submit);
			panel.add(inner);

			for (int i = 0; i < 3; i++){
				inner = new JPanel();
				inner.add(buttons[i]);
				panel.add(inner);
			}
			inner = new JPanel();
			inner.add(buttons[4]);
			panel.add(inner);
			inner = new JPanel();
			inner.add(back);
			panel.add(inner);
			break;
		case PROGCHAIR:
			panel.setLayout(new GridLayout(2, 4));
			for (final JButton b : buttons){
				inner = new JPanel();
				inner.add(b);
				panel.add(inner);
			}
		    inner.add(back);
			break;
		default:
		    inner.add(back);
			break;
		}
		add(panel, BorderLayout.SOUTH);

		}
}
