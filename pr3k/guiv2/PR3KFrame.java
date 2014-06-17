package pr3k.guiv2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import pr3k.model.User;


@SuppressWarnings("serial")
/**
 * This is a frame that holds all of the different panels associated with different screens.
 * It has a reference to the user, so that every window has the ability to get a reference to the user,
 * Throughout the session.
 * @author Mike Westbrook
 * @version November 2013
 */
public class PR3KFrame extends JFrame {
    
	/**
	 * The user that is signed in.
	 */
	private User my_user;
	
	/**
	 * Constructor that Creates the Frame to hold all the gui.
	 */
	public PR3KFrame(){
		super();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setCenter();
	}
	
	/**
	 * Runs the gui and displays it on the screen.
	 */
	public void start() {
		setTitle("PR3K v2");
	    pack();
		setCenter();
		setVisible(true);
		setResizable(false);
	}
	
	//Can only be set once.
	/**
	 * Setter for the my_user field.
	 * @param the_user The user that is logged in.
	 */
	public void setUser(final User the_user){
		if (my_user == null){
			my_user = the_user;
		}
	}
	
	/**
	 * Adds the menu bar
	 */
	public void addMenu(){
	      
        final JMenuBar menu_bar = new JMenuBar();
      
        final JMenuItem my_exit_menu_item = new JMenuItem("Exit", new Integer('E'));

        /**
         * Will take user to login screen
         */
        final JMenuItem my_logout_menu_item = new JMenuItem("Logout", new Integer('L'));
        
        
        /**
         * Will tell user what their current role is for current selected conference
         */
        
        final JMenuItem about_menu_item = new JMenuItem("About", new Integer('A'));
        
        /**
         * Brings user to home page
         */
        final JMenuItem home_menu_item = new JMenuItem("Home Page", new Integer('H'));
        
        //set up file menu
        final JMenu file_menu = new JMenu("File");
        file_menu.setMnemonic('F');
        file_menu.add(my_logout_menu_item );
        my_logout_menu_item.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                final PR3KFrame frame = new PR3KFrame();
                final Login login = new Login();
                frame.add(login);
                frame.start();  
                dispose();
            }
        });
        
        file_menu.add(my_exit_menu_item);
        my_exit_menu_item.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                PR3KFrame.this.dispose();
            }
        });
        
        //setup option menu
        final JMenu option_menu = new JMenu("Options");
        option_menu.setMnemonic('O');
        option_menu.add(home_menu_item);
        home_menu_item.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                PR3KFrame.this.dispose();
                final PR3KFrame frame = new PR3KFrame();
                frame.addMenu();
                frame.setUser(my_user);
                final ConferenceSelect conference = new ConferenceSelect(my_user);
                frame.add(conference);
                frame.start();
            }
        });

        //set up help menu
        final JMenu help_menu = new JMenu("Help");
        help_menu.setMnemonic('H');
        help_menu.add(about_menu_item);
        about_menu_item.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e){
                JOptionPane.showMessageDialog(about_menu_item, "PR3K (Peer Review 3,000) \nVersion: 3.0 \nAuthors: \n" +
                		"Mike Westbrook \n\tCody Shafer \n\tJesse Kitterman \n\tBrandon Martin \n\t" +
                		"Edward Bassan ", null, EXIT_ON_CLOSE);
                
            }
        });
              
        menu_bar.add(file_menu);
        menu_bar.add(option_menu);
        menu_bar.add(help_menu);
        
        setJMenuBar(menu_bar);
        
	}
	
	/**
	 * Getter for the user field.
	 * @return The current user.
	 */
	public User getUser(){
		return my_user;
	}
	
	/**
	 * Sets location on the screen.
	 */
	private void setCenter(){
		int x = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width / 2;
		int y = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height / 2;
		this.setLocation(x - getWidth() / 2, y - getHeight() / 2);
	}
}
