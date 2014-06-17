package pr3k.guiv2;

/**
 * Are comments really needed? Main method. Starts the program.
 * @author Cody Shafer
 * @version October 2013
 */
public class Main {
	
	/**
	 * Classic main method to start the program. the_args not used.
	 * @param the_args Not used.
	 */
	public static void main(final String[] the_args) {
		final PR3KFrame frame = new PR3KFrame();
		final Login login = new Login();
		frame.add(login);
		frame.pack();
		frame.start();
	}
}
