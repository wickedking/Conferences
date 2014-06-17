/**
 * Interface between client side and data side. 
 */
package pr3k.database;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import pr3k.model.Conference;
import pr3k.model.InvalidAssignmentException;
import pr3k.model.MaxPapersException;
import pr3k.model.Paper;
import pr3k.model.ReviewStatus;
import pr3k.model.User;
import pr3k.model.UserType;

/**
 * Provides a set of methods for the client side to retrieve information out of the database.
 * 
 * @author Mike Westbrook
 * @version 2.1
 */
public class Database{

	/**
	 * Maximum number of papers that can be assigned to a reviewer or subprogram chair.
	 */
	public static final int MAXPAPERS = 4;

	/**
	 * The only instance of this database.
	 */
	private static Database me;

	/**
	 * Actual connection to the database.
	 */
	private final Connection my_conn;

	/**
	 * Cannot be directly instantiated.
	 */
	protected Database(){
		my_conn = getConnection();
	}

	/**
	 * @return The only instance of this class.
	 */
	public static Database getInstance(){
		if (me == null){
			me = new Database();
		}
		return me;
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
								":" + DatabaseConstants.PORT + "/" + DatabaseConstants.DATABASE,
								connectionProps);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} 
		return conn;
	}

	/**
	 * Convenience method for deserialization.
	 * 
	 * @param the_table The table to deserialize from
	 * @return The sql command.
	 */
	private static String getdesiralizeCommand(String the_table){
		return "SELECT serialized_object FROM " + the_table + " WHERE serialized_id = ?";
	}

	/**
	 * Convenience method for serialization.
	 * 
	 * @param the_table The table to serialize from
	 * @return The sql command.
	 */
	private static String getSerializeCommand(final String the_table) {
		return "INSERT INTO " + the_table + " (object_name, serialized_object) VALUES (?, ?)";
	} 

	/**
	 * Adds a conference if it does not already exist.
	 * 
	 * @param the_conf The conference to add.
	 * @return The serial id of the conference.
	 */
	public long addConference(final Conference the_conf){
		if (!getConferences().contains(the_conf)){
			return serializeObjectToDB(DatabaseConstants.CONFERENCES, the_conf);
		}
		return -1l;
	}

	/**
	 * Adds a paper to a conference if it does not already exist or if they are not at their max.
	 * @param the_paper The paper to add.
	 * @return The serial number of the paper.
	 */
	public long addPaper(final Paper the_paper) throws MaxPapersException {
		if (!getPapers(the_paper.getConference()).contains(the_paper) &&
				getPapers(the_paper.getAuthor(), the_paper.getConference()).size() <= MAXPAPERS){
			serializeObjectToDB(DatabaseConstants.PAPERS, the_paper);
			return 1l;
		}
		else if (getPapers(the_paper.getAuthor(), the_paper.getConference()).size() <= MAXPAPERS){
			throw new MaxPapersException(the_paper.getAuthor());
		}
		return -1l;
	}

	/**
	 * Adds a reviewer to a conference if they are not already a reviewer.
	 * 
	 * @param the_user The user to add as a reviewer.
	 * @param the_conf The conference they are a reviewer of.
	 * @return The serial number of the user.
	 */
	public long addReviewer(final User the_user, final Conference the_conf){
		if (!getReviewers(the_conf).contains(the_user)){
			final Reviewer r = new Reviewer(the_user, the_conf, new ArrayList<Paper>());
			return serializeObjectToDB(DatabaseConstants.REVIEWERS, r);
		}
		return -1l;
	}

	/**
	 * Adds a subprogram chair to a conference if they are not already one.
	 * 
	 * @param the_user The user to add as a sub chair.
	 * @param the_conf The conference they are now a subprogram chair of.
	 * @return The serial number of the user.
	 */
	public long addSubChair(final User the_user, final Conference the_conf){
		if (!getSubChairs(the_conf).contains(the_user)){
			return serializeObjectToDB(DatabaseConstants.SUBCHAIRS, 
					new Reviewer(the_user, the_conf, new ArrayList<Paper>()));
		}
		return -1l;
	}

	/**
	 * @param the_user The user to add to the database.
	 * @return The users serial number.
	 */
	public long addUser(final User the_user){
		if (!getUsers().contains(the_user))
		{
			return serializeObjectToDB(DatabaseConstants.USERS, the_user);
		}
		return -1l;
	}

	/**
	 * Assigns a paper to a Reviewer for review. 
	 * @param the_user The reviewer to assign the paper to.
	 * @param the_paper The paper to be reviewed.
	 * @return The serial number of the reviewer.
	 * @throws InvalidAssignmentException
	 * @throws MaxPapersException
	 */
	public long  assignPaperToReviewer(final User the_user, final Paper the_paper)
			throws InvalidAssignmentException, MaxPapersException{
		if (the_user.equals(the_paper.getAuthor())){
			throw new InvalidAssignmentException(the_user, the_paper);
		}
		if (getReviewers(the_paper.getConference()).contains(the_user)){
			for (final Reviewer r : getReviewersFromDB()){
				if (r.getUser().equals(the_user) && 
						r.getConf().equals(the_paper.getConference())){
					if (r.getPapers().size() < MAXPAPERS){
						remove(DatabaseConstants.REVIEWERS, r);
						r.getPapers().add(the_paper);
						return serializeObjectToDB(DatabaseConstants.REVIEWERS, r);
					}
					else{
						throw new MaxPapersException(r.getUser());
					}
				}
			}
		}
		return -1l;
	}

	/**
	 * Assigns a paper to a subprogram chair for review. 
	 * @param the_user The user to assign the paper to.
	 * @param the_paper The paper to be reviewed.
	 * @return The serial number of the subprogram chair.
	 * @throws InvalidAssignmentException
	 * @throws MaxPapersException
	 */
	public long  assignPaperToSubChair(final User the_user, final Paper the_paper)
			throws InvalidAssignmentException, MaxPapersException{
		if (the_user.equals(the_paper.getAuthor())){
			throw new InvalidAssignmentException(the_user, the_paper);
		}
		if (getSubChairs(the_paper.getConference()).contains(the_user)){
			for (final Reviewer r : getSubChairsFromDB()){
				if (r.getUser().equals(the_user) && 
						r.getConf().equals(the_paper.getConference())){
					if (r.getPapers().size() < MAXPAPERS){
						remove(DatabaseConstants.SUBCHAIRS, r);
						r.getPapers().add(the_paper);
						return serializeObjectToDB(DatabaseConstants.SUBCHAIRS, r);
					}
					else{
						throw new MaxPapersException(r.getUser());
					}
				}
			}
		}
		return -1l;
	}

	/**
	 * @return A list of every author.
	 */
	public List<User> getAuthors(){
		final List<User> list = new ArrayList<User>();
		for (final Paper p : getPapers()){
			if (!list.contains(p.getAuthor())){
				list.add(p.getAuthor());
			}
		}
		return list;
	}

	/**
	 * @param the_conf The conference to get authors for.
	 * @return A list of papers in the specified conference.
	 */
	public List<User> getAuthors(final Conference the_conf){
		final List<User> list = new ArrayList<User>();
		for (final Paper p : getPapers()){
			if (!list.contains(p.getAuthor()) && p.getConference().equals(the_conf)){
				list.add(p.getAuthor());
			}
		}
		return list;
	}

	/**
	 * @return A list of all conferences.
	 */
	public List<Conference> getConferences(){
		final ResultSet set = getResultSet(DatabaseConstants.CONFERENCES);
		final List<Conference> list = new ArrayList<Conference>();
		try
		{
			while (set.next()){
				final Conference c = (Conference) deserializeObjectFromDB(
						DatabaseConstants.CONFERENCES, set.getInt(1));
				list.add(c);
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * @return A list of all papers.
	 */
	public List<Paper> getPapers(){
		final List<Paper> list = new ArrayList<Paper>();
		final ResultSet set = getResultSet(DatabaseConstants.PAPERS);
		try
		{
			while (set.next()){
				list.add((Paper) deserializeObjectFromDB(DatabaseConstants.PAPERS, set.getInt(1)));
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * @param The author to get papers for.
	 * 
	 * @return A list of papers submitted by the user. 
	 */
	public List<Paper> getPapers(final User the_user){
		final List<Paper> list = new ArrayList<Paper>();
		for (final Paper p : getPapers()){
			if (p.getAuthor().equals(the_user)){
				list.add(p);
			}
		}
		return list;
	}

	/**
	 * @param the_conf The conference to get papers for.
	 * @return A list of papers submitted to the specified conference.
	 */
	public List<Paper> getPapers(final Conference the_conf){
		final List<Paper> list = new ArrayList<Paper>();
		for (final Paper p : getPapers()){
			if (p.getConference().equals(the_conf)){
				list.add(p);
			}
		}
		return list;
	}

	/**
	 * @param the_user The user to get papers for.
	 * @param the_conf The conference to get papers for.
	 * @return A list of papers submitted by the specified user in the specified conference.
	 */
	public List<Paper> getPapers(final User the_user, final Conference the_conf){
		final List<Paper> list = new ArrayList<Paper>();
		for (final Paper p : getPapers(the_user)){
			if (p.getConference().equals(the_conf)){
				list.add(p);
			}
		}
		return list;
	}

	/**
	 * @param the_conf The conference to get reviewed papers from.
	 * @return A list of reviewed papers in the specified conference.
	 */
	public List<Paper> getReviewed(final Conference the_conf){
		final List<Paper> list = new ArrayList<Paper>();
		for (final Paper p : getPapers(the_conf)){
			if (p.getReviewStatus() != ReviewStatus.UNDER_REVIEW){
				list.add(p);
			}
		}
		return list;
	}

	/**
	 * @param the_user The reviewer to get papers for.
	 * @param the_conf The conference they are a reviewer in.
	 * @return A list of papers the specified reviewer has been assigned.
	 */
	public List<Paper> getReviewerPapers(final User the_user, final Conference the_conf){
		final List<Paper> list = new ArrayList<Paper>();
		for (final Reviewer r : getReviewersFromDB()){
			if (r.getUser().equals(the_user) && r.getConf().equals(the_conf)){
				list.addAll(r.getPapers());
			}
		}
		return list;
	}

	/**
	 * @return A list of all reviewers.
	 */
	public List<User> getReviewers(){
		final List<User> list = new ArrayList<User>();
		for (final Reviewer r : getReviewersFromDB()){
			list.add(r.getUser());
		}
		return list;
	}

	/**
	 * @param the_conf The conference to get reviewers for.
	 * @return A list of reviewers in the specified conference.
	 */
	public List<User> getReviewers(final Conference the_conf){
		final List<User> list = new ArrayList<User>();
		for (final Reviewer r : getReviewersFromDB()){
			if (r.getConf().equals(the_conf)){
				list.add(r.getUser());
			}
		}
		return list;
	}

	/**
	 * @param the_user The user to find role.
	 * @param the_conf The conference to check.
	 * @return The type of role the specified user has in the specified conference.
	 */
	public UserType getRole(final User the_user, final Conference the_conf) {
		UserType type = UserType.USER;
		if (the_conf.getProgramChair().equals(the_user)) {
			type = UserType.PROGCHAIR;
		}
		else if (getSubChairs(the_conf).contains(the_user))
		{
			type = UserType.SUBCHAIR;
		}
		else if (getReviewers(the_conf).contains(the_user)) {
			type = UserType.REVIEWER;
		}
		else if (getAuthors(the_conf).contains(the_user)) {
			type = UserType.AUTHOR;
		}
		return type;
	}

	/**
	 * @param the_conf The conference to get subchairs for.
	 * @return A list of subprogram chairs in the specified conference.
	 */
	public List<User> getSubChairs(final Conference the_conf){
		final List<User> list = new ArrayList<User>();
		for (final Reviewer r : getSubChairsFromDB()){
			if (r.getConf().equals(the_conf)){
				list.add(r.getUser());
			}
		}
		return list;
	}
	
	/**
	 * @param the_user The subchair to get papers for.
	 * @param the_conf The conference to get papers for.
	 * @return A list of the specified subprogram chair's papers in the specified conference.
	 */
	public List<Paper> getSubChairPapers(final User the_user, final Conference the_conf){
		final List<Paper> list = new ArrayList<Paper>();
		for (final Reviewer r : getSubChairsFromDB()){
			if (r.getUser().equals(the_user) && r.getConf().equals(the_conf)){
				list.addAll(r.getPapers());
			}
		}
		return list;
	}

	/**
	 * @param the_conf The conference to get unreviewed papers for.
	 * @return A list of unreviewed papers in the specified conference.
	 */
	public List<Paper> getUnreviewed(final Conference the_conf){
		final List<Paper> list = getPapers(the_conf);
		list.removeAll(getReviewed(the_conf));
		return list;
	}

	/**
	 * @return A list of all users.
	 */
	public List<User> getUsers(){
		final List<User> list = new ArrayList<User>();
		try
		{
			final ResultSet set = getResultSet(DatabaseConstants.USERS);
			while (set.next()){
				list.add((User) deserializeObjectFromDB(DatabaseConstants.USERS, 
						set.getInt(1)));
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * @param the_paper The paper to remove from the database.
	 */
	public void removePaper(final Paper the_paper){
		remove(DatabaseConstants.PAPERS, the_paper);
		for (final Reviewer r : getReviewersFromDB()){
			if (r.getPapers().contains(the_paper)){
				r.getPapers().remove(the_paper);
			}
		}
		for (final Reviewer r : getSubChairsFromDB()){
			if (r.getPapers().contains(the_paper)){
				r.getPapers().remove(the_paper);
			}
		}
	}

	/**
	 * Updates a users paper.
	 * 
	 * @param the_old The paper to update.
	 * @param the_new The updated paper.
	 * @return new serial number for the paper.
	 * @throws MaxPapersException 
	 */
	public long updatePaper(final Paper the_old, final Paper the_new) 
			throws MaxPapersException{
		if (getPapers(the_new.getConference()).contains(the_old)){
			remove(DatabaseConstants.PAPERS, the_old);
			return addPaper(the_new);
		}
		return -1l;
	}
	
	/**
	 * @param the_table The table to retrieve an object from
	 * @param serialized_id The serial number of the object.
	 * @return The object from the database.
	 */
	private Object deserializeObjectFromDB(String the_table, long serialized_id) {
		PreparedStatement pstmt;
		try
		{
			pstmt = my_conn
					.prepareStatement(getdesiralizeCommand(the_table));
			pstmt.setLong(1, serialized_id);
			ResultSet rs = pstmt.executeQuery();
			rs.next();

			byte[] buf = rs.getBytes(1);
			ObjectInputStream objectIn = null;
			if (buf != null)
				objectIn = new ObjectInputStream(new ByteArrayInputStream(buf));

			Object deSerializedObject = objectIn.readObject();

			rs.close();
			pstmt.close();

			return deSerializedObject;
		} catch (SQLException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param the_table The table to get a result set for.
	 * @return A result set for the specified table.
	 */
	private ResultSet getResultSet(final String the_table){
		Statement stmt;
		try {
			stmt = my_conn.createStatement();
			final String sql = "SELECT * FROM "+ the_table + ";";
			return stmt.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @return All of the reviewers.
	 */
	private List<Reviewer> getReviewersFromDB(){
		final List<Reviewer> list = new ArrayList<Reviewer>();
		final ResultSet set = getResultSet(DatabaseConstants.REVIEWERS);
		try
		{
			while (set.next()){
				list.add((Reviewer) deserializeObjectFromDB(DatabaseConstants.REVIEWERS, 
						set.getInt(1)));
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * @return All of the subprogram chairs.
	 */
	private List<Reviewer> getSubChairsFromDB(){
		final List<Reviewer> list = new ArrayList<Reviewer>();
		final ResultSet set = getResultSet(DatabaseConstants.SUBCHAIRS);
		try
		{
			while (set.next()){
				list.add((Reviewer) deserializeObjectFromDB(DatabaseConstants.SUBCHAIRS, 
						set.getInt(1)));
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * @param the_table The table to remove the object from.
	 * @param the_obj The object to remove.
	 */
	private void remove(final String the_table, final Object the_obj){
		final ResultSet set = getResultSet(the_table);
		try
		{
			while (set.next()){
				final Object o = deserializeObjectFromDB(the_table, set.getInt(1));
				if (o.equals(the_obj)){
					final Statement stmt = my_conn.createStatement();
					final String sql = "DELETE FROM " + the_table +
							" WHERE object_name = '" + o.toString() + "';";
					stmt.execute(sql);
				}
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Serializes object to database.
	 * 
	 * @param the_table The table to add the object to.
	 * @param objectToSerialize The object to add.
	 * @return Serial number of the object added.
	 */
	private long serializeObjectToDB(String the_table, Object objectToSerialize) {
		PreparedStatement pstmt;
		try
		{
			pstmt = my_conn
					.prepareStatement(getSerializeCommand(the_table), Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, objectToSerialize.toString());
			pstmt.setObject(2, objectToSerialize);
			pstmt.executeUpdate();
			ResultSet rs = pstmt.getGeneratedKeys();
			int serialized_id = -1;
			if (rs.next()) {
				serialized_id = rs.getInt(1);
			}
			rs.close();
			pstmt.close();
			return serialized_id;
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return -1l;
	}
}