package pr3k.guiv2;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import pr3k.actions.ChangePanelAction;
import pr3k.model.Paper;
import pr3k.model.User;


@SuppressWarnings("serial")
/**
 * This gui is to allow a reviewer to make a recommendation on a paper.
 * @author Jesse Kitterman
 * @version November 2013
 */
public class MakeRecommendation extends PR3KPanel{ 
	
	/**
	 * The paper that is being reviewed.
	 */
	private final  Paper my_paper;

	/**
	 * Constructor for the gui.
	 * @param the_user The current User.
	 * @param the_paper The current paper to recommend.
	 */
	public MakeRecommendation(final User the_user, final Paper the_paper){
		super (the_user, the_paper.getConference());
		my_paper = the_paper;
		initPanel();
	}

	/**
	 * Creates the panel to display.
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
			public void actionPerformed(ActionEvent e){
//				final Paper paper = new Paper(my_paper.getTitle(), my_paper.getAuthor(), my_paper)
			}
		});
		
		
		final JButton reject = new JButton("Reject");
		
		
		final JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener(){
			public void actionPerformed(final ActionEvent e) {
				new ChangePanelAction(MakeRecommendation.this, 
						new ConferenceInfo(getUser(), 
								MakeRecommendation.this.getConf())).actionPerformed(null);
			
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
	 * Loads the text file and displays the contents.
	 * @param the_paper_viewer The Text area to display the paper contents.
	 */
	private void writePaper(JTextArea the_paper_viewer)
	{
		try
		{
			final Scanner scanner = new Scanner(my_paper.getFile());
			while (scanner.hasNext()){
				the_paper_viewer.setText(the_paper_viewer.getText() + scanner.nextLine() + "\n");
			}
			scanner.close();
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
}
