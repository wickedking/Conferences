package pr3k.guiv2;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;

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


/**
 * This gui is to allow a user to submit a paper to a certain conference.
 * @author Brandon Martin
 * @version November 2013
 */
@SuppressWarnings("serial")
public class SubmitPaper extends PR3KPanel{
	
	/**
	 * The File chooser where the user chooses the paper to submit.
	 */
	private final JFileChooser my_chooser;

	/**
	 * The text area to input the title.
	 */
	private JTextField my_field;
	
	/**
	 * The file that the user chooses.
	 */
	private File my_file;

	
	//TODO should switch the parameters.
	/**
	 * Constructor
	 * @param the_conf The current conference.
	 * @param the_user The current user.
	 */
	public SubmitPaper(final Conference the_conf, final User the_user){
		super(the_user, the_conf);
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
			    if(Database.getInstance().getPapers(getUser(), getConf()).size() > 3) {
			        JOptionPane.showMessageDialog(SubmitPaper.this, 
                            "You have reached your paper submission limit");
                    return;
			    }
			    
				if (my_field.getText().equals("")){
					JOptionPane.showMessageDialog(SubmitPaper.this, 
							"You must enter a title for the paper.");
					return;
				}
				if (my_file == null){
					JOptionPane.showMessageDialog(SubmitPaper.this, 
							"You must select a file to upload.");
					return;
				}
				final Paper paper = 
						new Paper(my_field.getText(), getUser(), my_file, getConf());
				
				final boolean ontime = beforeDeadLIne();
				
				
				
				if (ontime){
					try
					{
						Long check = Database.getInstance().addPaper(paper);
						if(check > 0){
							JOptionPane.showMessageDialog(SubmitPaper.this, "Paper added successfully");
							new ChangePanelAction(SubmitPaper.this, 
									new ConferenceInfo(getUser(), getConf())).actionPerformed(null);
						} else {
							JOptionPane.showMessageDialog(SubmitPaper.this, "Paper already Submitted. Please choose another paper or conference.");
						}
					} catch (MaxPapersException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				else {
					JOptionPane.showMessageDialog(SubmitPaper.this, "It is past the deadline.");
				}	
			}
		});
		
		final JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener(){
			public void actionPerformed(final ActionEvent the_e){
					new ChangePanelAction(SubmitPaper.this, 
							new ConferenceSelect(getUser())).actionPerformed(null);
			}
		});
		bottom.add(ok);
		bottom.add(cancel);
		
		main_panel.add(top);
		main_panel.add(bottom);
		
		add(main_panel);
	}
	
	/**
	 * Gets the file that was chosen to be uploaded.
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

	private boolean beforeDeadLIne()
	{
		java.util.Calendar cal = java.util.Calendar.getInstance();
		java.util.Date utilDate = cal.getTime();
		java.sql.Date sqlDate = new Date(utilDate.getTime());
		
		java.sql.Date conf_date = getConf().getDeadline();
		return conf_date.after(sqlDate);
	}
}
