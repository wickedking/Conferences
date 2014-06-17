package pr3k.guiv2;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import pr3k.actions.ChangePanelAction;
import pr3k.database.Database;
import pr3k.model.Conference;
import pr3k.model.MaxPapersException;
import pr3k.model.Paper;
import pr3k.model.User;


@SuppressWarnings("serial")
/**
 * This gui is to allow a user to resubmit a paper and remove the previous version of the paper.
 * @author Jesse Kitterman
 * @version November 2013
 */
public class RevisePaper extends PR3KPanel{
	
	/**
	 * File Chooser so the user can upload their paper.
	 */
	private final JFileChooser my_chooser;

	/**
	 * Text Field where the user inputs the title of the paper.
	 */
	private JTextField my_field;
	
	/**
	 * The new file that is uploaded by the user.
	 */
	private File my_file;
	
	/**
	 * The paper that the user is trying to replace.
	 */
	private Paper old_paper;
	
	/**
	 * Constructor for the gui.
	 * @param the_user The current user.
	 * @param the_conf The current conference.
	 * @param the_paper The current paper.
	 */
	public RevisePaper(final User the_user, final Conference the_conf, final Paper the_paper){
		super(the_user, the_conf);
		old_paper = the_paper;
		final Path curr = Paths.get("");
		my_chooser = new JFileChooser(curr.toAbsolutePath().toString());
		initPanel();
	}
	
	/**
	 * Creates the panel to be displayed.
	 */
	private void initPanel(){
		final JPanel main_panel = new JPanel();
		main_panel.setLayout(new BoxLayout(main_panel, BoxLayout.Y_AXIS));
		
		final JPanel top = new JPanel();
		top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
		final JLabel title = new JLabel("Title");
		my_field = new JTextField(50);
		final JButton select = new JButton("Select File");
		select.addActionListener(new ActionListener(){
			public void actionPerformed(final ActionEvent the_e){
				getFile();
			}
		});
		top.add(title);
		top.add(my_field);
		top.add(select);
		
		
		
		final JPanel bottom = new JPanel();
		final JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener(){
			public void actionPerformed(final ActionEvent the_e){
				if (my_field.getText().equals("")){
					JOptionPane.showMessageDialog(RevisePaper.this, 
							"You must enter a title for the paper.");
					return;
				}
				if (my_file == null){
					JOptionPane.showMessageDialog(RevisePaper.this, 
							"You must select a file to upload.");
					return;
				}
				final Paper paper = 
						new Paper(my_field.getText(), getUser(), my_file, getConf());
				//TODO check return to display proper message.

				try {
					Database.getInstance().updatePaper(old_paper, paper);
				} catch (MaxPapersException e) {
					//Should never reach, just updating paper.
				}
				//TODO 
				new ChangePanelAction(RevisePaper.this, 
						new ConferenceInfo(getUser(), getConf())).actionPerformed(null);
			}
		});
		
		final JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener(){
			public void actionPerformed(final ActionEvent the_e){
				//TODO should return to conference info panel.
				new ChangePanelAction(RevisePaper.this, 
						new ViewPapers(getUser())).actionPerformed(null);
			}
		});
		bottom.add(ok);
		bottom.add(cancel);
		
		main_panel.add(top);
		main_panel.add(bottom);
		
		add(main_panel);
	}
	
	/**
	 * Loads the file that was chosen.
	 */
	private void getFile(){
		final int choice = my_chooser.showOpenDialog(this);
		if (choice == JFileChooser.APPROVE_OPTION){
			my_file = my_chooser.getSelectedFile();
			final JLabel label = new JLabel(my_file.getAbsolutePath());
			add(label, BorderLayout.SOUTH);
			((JFrame) getTopLevelAncestor()).pack();
		}
	}
}
