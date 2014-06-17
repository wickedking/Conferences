package pr3k.database;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.junit.Test;

import pr3k.model.Conference;
import pr3k.model.InvalidAssignmentException;
import pr3k.model.MaxPapersException;
import pr3k.model.Paper;
import pr3k.model.User;

public class DatabaseTest
{
	private static final Database DB = Database.getInstance();

	private static final String[] USERNAMES = {"mike", "cody", "edward", "jesse"};

	private static final String[] CONF_TITLES = 
		{"Peanut Butter Hotdogs and Society.", "Scientific Murkah!", "Conference 3.14", 
		"World Domination"};

	private static final String[] DATES = {"2014-1-15", "2014-6-15", "2015-1-15", "2014-6-15"};

	private static final String[] DEADLINES = {"2014-1-1", "2014-1-1", "2014-1-1", "2014-1-1"};

	private static final String[] PAPER = {"Peanut Butter Brats and the Future!", 
		"Tuk r Jerbs!", "Raspberry", "U.N."};

	private static final User[] USERS;

	private static final Conference[] CONFS;

	private static Paper[] PAPERS;

	private static Connection MY_CONN = getConnection();

	static {
		USERS = new User[USERNAMES.length];
		CONFS = new Conference[CONF_TITLES.length];
		PAPERS = new Paper[PAPER.length];
		for (int i = 0; i < USERNAMES.length; i++){
			USERS[i] = new User(USERNAMES[i]);
			CONFS[CONFS.length - 1 - i] = 
					new Conference(CONF_TITLES[i], Date.valueOf(DATES[i]), 
							Date.valueOf(DEADLINES[i]), USERS[i]);
			PAPERS[i] = 
					new Paper(PAPER[i], USERS[i], 
							new File(Integer.toString(i)), CONFS[CONFS.length - i - 1]);
		}
	}

	/**
	 * Tests that only unique users are entered.
	 */
	@Test
	public void testAddUser()
	{
		clearAll();
		for (final User u : USERS)
		{
			DB.addUser(u);
		}
		for (final User u : USERS)
		{
			final long serial = DB.addUser(u);
			assertTrue("User was incorrectly added.", serial == -1l);
		}
		clearAll();
	}

	/**
	 * Test that conferences are added.
	 */
	@Test
	public void testAddConference()
	{
		clearAll();
		for (int i = 0; i < USERS.length; i++)
		{
			DB.addUser(USERS[i]);
			DB.addConference(CONFS[i]);
		}
		//Test conferences were added.
		assertTrue(DB.getConferences().size() == CONFS.length);
		//Test uniqueness
		for (final Conference c : CONFS)
		{
			assertTrue(DB.addConference(c) == -1l);
		}
		clearAll();
	}
	
	/**
	 * Test that paers are actually added to the database.
	 */
	@Test
	public void testAddPaper()
	{
		clearAll();
		for (int i = 0; i < USERS.length; i++)
		{
			DB.addUser(USERS[i]);
			DB.addConference(CONFS[i]);
			try
			{
				DB.addPaper(PAPERS[i]);
			} catch (MaxPapersException e)
			{
				fail();
			}
		}
		if (DB.getPapers().size() != PAPERS.length)
		{
			fail();
		}
		clearAll();
	}

	/**
	 * Test that a reviewer cannot review their own paper.
	 * @param the_table
	 */
	@Test
	public void testSelfReview()
	{
		clearAll();
		for (int i = 0; i < USERS.length; i++)
		{
			try 
			{
				//Submit papers and add users as reviewers to conferences they are authors in.
				DB.addConference(CONFS[i]);
				DB.addPaper(PAPERS[i]);
				DB.addReviewer(USERS[i], CONFS[CONFS.length - i - 1]);
				//Try to assign that paper to the author.
				@SuppressWarnings("unused")
				final long serial = DB.assignPaperToReviewer(USERS[i], PAPERS[i]);
				fail();
			}
			catch (InvalidAssignmentException e){}
			catch (MaxPapersException e){}
		}
		clearAll();
	}

	/**
	 * Test a subprogram chair cannot be assigned his own paper.
	 */
	@Test
	public void testSubchairAssignment()
	{
		clearAll();
		for (int i = 0; i < USERS.length; i++)
		{
			try 
			{
				//Submit papers and add users as reviewers to conferences they are authors in.
				DB.addConference(CONFS[i]);
				DB.addPaper(PAPERS[i]);
				DB.addReviewer(USERS[i], CONFS[CONFS.length - i - 1]);
				DB.addSubChair(USERS[i], CONFS[CONFS.length - i - 1]);
				//Try to assign that paper to the author.
				@SuppressWarnings("unused")
				final long serial = DB.assignPaperToSubChair(USERS[i], PAPERS[i]);
				fail();
			}
			catch (InvalidAssignmentException e){}
			catch (MaxPapersException e){}
		}
		clearAll();
	}

	/**
	 * Tests that a user can only submit 4 papers.
	 */
	@Test
	public void testSubmissionLimit()
	{
		clearAll();
		DB.addUser(USERS[0]);
		DB.addConference(CONFS[0]);
		for (int i = 0; i < Database.MAXPAPERS; i++)
		{
			final Paper p = new Paper(Integer.toString(i), USERS[0],
					new File(Integer.toString(i)), CONFS[0]);
			try
			{
				DB.addPaper(p);
			} catch (MaxPapersException e)
			{
				fail();
			}
		}	
		clearAll();
	}

	/**
	 * Tests that a reviewer can only be assigned four papers.
	 * @param the_table
	 */
	@Test
	public void testReviewLimit()
	{
		clearAll();
		DB.addUser(USERS[0]);
		DB.addConference(CONFS[0]);
		DB.addReviewer(USERS[1], CONFS[0]);
		for (int i = 0; i < Database.MAXPAPERS; i++)
		{
			final Paper p = new Paper(Integer.toString(i), USERS[0],
					new File(Integer.toString(i)), CONFS[0]);
			try
			{
				DB.addPaper(p);
				DB.assignPaperToReviewer(USERS[1], p);
			} catch (MaxPapersException e)
			{
				fail();
			} catch (InvalidAssignmentException e)
			{
				fail();
			}
		}
		try
		{
			DB.assignPaperToReviewer(USERS[1], PAPERS[3]);
			fail();
		} catch (InvalidAssignmentException | MaxPapersException e){}
		clearAll();
	}
	
	/**
	 * Tests that a subchair can only be assigned four papers.
	 * @param the_table
	 */
	@Test
	public void testSubChairLimit()
	{
		clearAll();
		DB.addUser(USERS[0]);
		DB.addConference(CONFS[0]);
		DB.addReviewer(USERS[1], CONFS[0]);
		DB.addSubChair(USERS[1], CONFS[0]);
		for (int i = 0; i < Database.MAXPAPERS; i++)
		{
			final Paper p = new Paper(Integer.toString(i), USERS[0],
					new File(Integer.toString(i)), CONFS[0]);
			try
			{
				DB.addPaper(p);
				DB.assignPaperToReviewer(USERS[1], p);
			} catch (MaxPapersException e)
			{
				fail();
			} catch (InvalidAssignmentException e)
			{
				fail();
			}
		}
		try
		{
			DB.assignPaperToReviewer(USERS[1], PAPERS[3]);
			fail();
		} catch (InvalidAssignmentException | MaxPapersException e){}
		clearAll();
	}



	private void clear(final String the_table){
		Statement stmt;
		try
		{
			stmt = MY_CONN.createStatement();
			final String sql = "DELETE FROM " + the_table +" WHERE 1=1;";
			stmt.execute(sql);
		} catch (SQLException e)
		{
			e.printStackTrace();
		}

	} 
	
	private void clearAll()
	{
		clear("users");
		clear("reviewers");
		clear("papers");
		clear("subprogramchairs");
		clear("conferences");
	}

	/**
	 * Creates a connection to the database.
	 * @return
	 */
	private static Connection getConnection() {
		Connection conn = null;
		Properties connectionProps = new Properties();
		connectionProps.put("user", DatabaseConstants.USERNAME);

		if (DatabaseConstants.DBMS.equals("mysql")) {
			try {
				conn = DriverManager.getConnection(
						"jdbc:" + DatabaseConstants.DBMS + "://" +
								DatabaseConstants.SERVER_NAME +
								":" + DatabaseConstants.PORT + "/" + 
								DatabaseConstants.DATABASE,
								connectionProps);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} 
		return conn;
	}
}