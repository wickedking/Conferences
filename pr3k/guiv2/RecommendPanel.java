package pr3k.guiv2;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import pr3k.actions.ChangePanelAction;
import pr3k.database.Database;
import pr3k.model.MaxPapersException;
import pr3k.model.Paper;
import pr3k.model.ReviewStatus;
import pr3k.model.User;


@SuppressWarnings("serial")
/**
 * This panel is to display the text of the paper and to allow a reviewer to make a decision.
 * @author Edward Bassan
 * @version November 2013
 */
public class RecommendPanel extends PR3KPanel{ 
	
	/**
	 * The paper that is currently being viewed.
	 */
	private final  Paper my_paper;

	/**
	 * Constructor, Creates the gui and displays.
	 * @param the_user
	 * @param the_conf
	 */
	public RecommendPanel(final User the_user, final Paper the_paper){
		super (the_user, the_paper.getConference());
		my_paper = the_paper;
		initPanel();
	}

	/**
	 * Creates the panel to be displayed.
	 */
	private void initPanel()
	{
		final JPanel main = new JPanel();
		final JPanel paperField = new JPanel();
		final JPanel buttons = new JPanel();
		final BoxLayout layout = new BoxLayout(main, BoxLayout.Y_AXIS);
		main.setLayout(layout);
		final JTextArea paper = new JTextArea();
		paper.setEditable(false);

		//accept, reject, cancel
		final JButton accept = new JButton("Accept");
		accept.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				Paper paper = RecommendPanel.this.my_paper;
				Paper new_paper = new Paper(paper.getTitle(), paper.getAuthor(), paper.getFile(), paper.getConference(), ReviewStatus.RECOMMEND);
				try {
					Database.getInstance().updatePaper(paper, new_paper);
				} catch (MaxPapersException e1) {
					//should never reach. just updating paper.
				}
				new ChangePanelAction(RecommendPanel.this, new ConferenceInfo(getUser(), getConf())).actionPerformed(null);;
			}
		});



		final JButton reject = new JButton("Reject");
		reject.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				Paper paper = RecommendPanel.this.my_paper;
				Paper new_paper = new Paper(paper.getTitle(), paper.getAuthor(), paper.getFile(), paper.getConference(), ReviewStatus.DONT_RECOMMEND);
				try {
					Database.getInstance().updatePaper(paper, new_paper);
				} catch (MaxPapersException e1) {
					//Should never reach, just updating paper.
				}
				new ChangePanelAction(RecommendPanel.this, new ConferenceInfo(getUser(), getConf())).actionPerformed(null);;
			}
		});


		final JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener(){
			public void actionPerformed(final ActionEvent e) {
				new ChangePanelAction(RecommendPanel.this, 
						new ConferenceInfo(getUser(), 
								RecommendPanel.this.getConf())).actionPerformed(null);

			}
		});
		buttons.add(accept);
		buttons.add(reject);
		buttons.add(cancel);





		writePaper(paper);
		paperField.add(paper);
		//		main.add(paperField);
		//		main.add(buttons);
		add(paperField, BorderLayout.NORTH);
		add(buttons, BorderLayout.SOUTH);
	}

	/**
	 * 
	 * @param paper
	 */
	private void writePaper(JTextArea paper)
	{
	    
	    JTextArea textArea = new JTextArea(10,80);
	    
	    Reader reader;
		try{
		    reader = new FileReader(my_paper.getFile());
		    try {
                textArea.read(reader, "paper");
            } catch (IOException e) {
            try {
                reader.close();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } //TODO dafuq? Whoever made this should really comment this.
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
		    
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		add(new JScrollPane(textArea));

	}

}
