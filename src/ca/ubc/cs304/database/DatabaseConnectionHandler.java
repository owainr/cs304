package ca.ubc.cs304.database;

import java.sql.*;
import java.util.ArrayList;

import ca.ubc.cs304.model.PictureModel;
import ca.ubc.cs304.model.UserModel;

import javax.jws.soap.SOAPBinding;
//test!!!!
/**
 * This class handles all database related transactions
 * TODO: join
 *       aggregation with group by
 *       aggregation with having
 *       nested aggregation with group by
 *       division
 *       insert more tuples
 */
public class DatabaseConnectionHandler {
	// Use this version of the ORACLE_URL if you are running the code off of the server
//	private static final String ORACLE_URL = "jdbc:oracle:thin:@dbhost.students.cs.ubc.ca:1522:stu";
	// Use this version of the ORACLE_URL if you are tunneling into the undergrad servers
	private static final String ORACLE_URL = "jdbc:oracle:thin:@localhost:1522:stu";
	private static final String EXCEPTION_TAG = "[EXCEPTION]";
	private static final String WARNING_TAG = "[WARNING]";
	
	private Connection connection = null;
	
	public DatabaseConnectionHandler() {
		try {
			// Load the Oracle JDBC driver
			// Note that the path could change for new drivers
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}
	}
	
	public void close() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}
	}

	public void deletePicture(String title, Date releaseDate) {
		try {
			PreparedStatement ps = connection.prepareStatement("DELETE FROM picture WHERE pictureTitle = ? AND releaseDate = ?");
			ps.setString(1, title);
			ps.setDate(2, releaseDate);
			
			int rowCount = ps.executeUpdate();
			if (rowCount == 0) {
				System.out.println(WARNING_TAG + " Picture " + title + " does not exist!");
			}
			
			connection.commit();
	
			ps.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
		}
	}
	
	public void insertPicture(PictureModel model) {
		try {
			PreparedStatement ps = connection.prepareStatement("INSERT INTO branch VALUES (?,?,?,?,?)");
			ps.setInt(1, model.getLength());
			ps.setString(2, model.getDirector());
			ps.setString(3, model.getTitle());
			ps.setDate(4, model.getReleaseDate());
			if (model.getSeriesID() == 0) {
				ps.setNull(5, java.sql.Types.INTEGER);
			} else {
				ps.setInt(5, model.getSeriesID());
			}

			ps.executeUpdate();
			connection.commit();

			ps.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
		}
	}
	
	public PictureModel[] getPictureInfo() {
		ArrayList<PictureModel> result = new ArrayList<PictureModel>();
		
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM picture");
		
//    		// get info on ResultSet
//    		ResultSetMetaData rsmd = rs.getMetaData();
//
//    		System.out.println(" ");
//
//    		// display column names;
//    		for (int i = 0; i < rsmd.getColumnCount(); i++) {
//    			// get column name and print it
//    			System.out.printf("%-15s", rsmd.getColumnName(i + 1));
//    		}
			
			while(rs.next()) {
				PictureModel model = new PictureModel(rs.getString("pictureTitle"),
													rs.getDate("releaseDate"),
													rs.getInt("lenght"),
													rs.getString("director"),
													rs.getInt("seriesID"));
				result.add(model);
			}

			rs.close();
			stmt.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}	
		
		return result.toArray(new PictureModel[result.size()]);
	}
	
	public void updatePictureDirector(String title, Date releaseDate, String director) {
		try {
		  PreparedStatement ps = connection.prepareStatement("UPDATE picture SET director = ? WHERE title = ? AND releaseDate = ?");
		  ps.setString(1, director);
		  ps.setString(2, title);
		  ps.setDate(3, releaseDate);
		
		  int rowCount = ps.executeUpdate();
		  if (rowCount == 0) {
		      System.out.println(WARNING_TAG + " Picture " + title + " does not exist!");
		  }
	
		  connection.commit();
		  
		  ps.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
		}	
	}

	public UserModel[] selectUsersFavGenreHorror() {
        ArrayList<UserModel> result = new ArrayList<UserModel>();

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM user WHERE favGenreCategory = Horror");

//    		// get info on ResultSet
//    		ResultSetMetaData rsmd = rs.getMetaData();
//
//    		System.out.println(" ");
//
//    		// display column names;
//    		for (int i = 0; i < rsmd.getColumnCount(); i++) {
//    			// get column name and print it
//    			System.out.printf("%-15s", rsmd.getColumnName(i + 1));
//    		}

            while(rs.next()) {
                UserModel model = new UserModel(rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("favGenreCategory"),
                        rs.getInt("watchlistID"),
                        rs.getInt("historyID"));
                result.add(model);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }

        return result.toArray(new UserModel[result.size()]);
    }
	
	public boolean login(String username, String password) {
		try {
			if (connection != null) {
				connection.close();
			}
	
			connection = DriverManager.getConnection(ORACLE_URL, username, password);
			connection.setAutoCommit(false);
	
			System.out.println("\nConnected to Oracle!");
			return true;
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			return false;
		}
	}



	private void rollbackConnection() {
		try  {
			connection.rollback();	
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}
	}
	
	public void databaseSetup() {
		dropBranchTableIfExists();
		
		try {
			Statement stmt = connection.createStatement();
			stmt.executeUpdate("CREATE TABLE picture (pictureTitle char(50) PRIMARY KEY," +
                    " releaseDate date PRIMARY KEY," +
                    " length real NOT NULL," +
                    " director: char(40)," +
                    " seriesID int)");
			stmt.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}

        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("CREATE TABLE user (" +
                    "username char(20) not null PRIMARY KEY," +
                    "email char(40) not null PRIMARY KEY," +
                    "favGenreCategory char(20) not null," +
                    "watchListID int UNIQUE not null," +
                    "historyID int UNIQUE not null," +
                    ")");
            stmt.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
		
		PictureModel branch1 = new PictureModel("Twilight", new Date(2008, 10, 21), 126, "Catherine Hardwicke", 6);
		insertPicture(branch1);
		
		PictureModel branch2 = new PictureModel("Gone Girl", new Date(2014, 10, 23), 149, "David Fincher", 0);
		insertPicture(branch2);

		UserModel jack = new UserModel("Jack", "a@b.ca", "Documentary", 1, 1);
		insertUser(jack);

        UserModel dad = new UserModel("Dad", "b@c.ca", "Thriller", 2, 2);
        insertUser(dad);
	}

    private void insertUser(UserModel model) {
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO user VALUES (?,?,?,?,?)");
            ps.setString(1, model.getUsername() );
            ps.setString(2, model.getEmail());
            ps.setString(3, model.getFavGenreCategory());
            ps.setInt(4, model.getWatchlistID());
            ps.setInt(5, model.getHistoryID());

            ps.executeUpdate();
            connection.commit();

            ps.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
        }
    }

    private void dropBranchTableIfExists() {
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("select table_name from user_tables");
			
			while(rs.next()) {
				if(rs.getString(1).toLowerCase().equals("branch")) {
					stmt.execute("DROP TABLE branch");
					break;
				}
			}
			
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}
	}

    public UserModel[] projectPictureSeriesID() {
        ArrayList<UserModel> result = new ArrayList<UserModel>();

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT seriesID FROM user");

//    		// get info on ResultSet
//    		ResultSetMetaData rsmd = rs.getMetaData();
//
//    		System.out.println(" ");
//
//    		// display column names;
//    		for (int i = 0; i < rsmd.getColumnCount(); i++) {
//    			// get column name and print it
//    			System.out.printf("%-15s", rsmd.getColumnName(i + 1));
//    		}

            while(rs.next()) {
                UserModel model = new UserModel(rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("favGenreCategory"),
                        rs.getInt("watchlistID"),
                        rs.getInt("historyID"));
                result.add(model);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }

        return result.toArray(new UserModel[result.size()]);

    }

   // public void
}

