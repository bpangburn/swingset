/**
 * This class can be used to read sql files into an array of Strings, each 
 * representing a single query terminated by ";" 
 * Comments are filtered out. 
 */
package com.nqadmin.swingset.demo;
 
import java.awt.Point;
import java.io.BufferedReader;  
import java.io.IOException;
 
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.raelity.lib.ui.Screens;

 
/**
 * Utilities for setting up a database to use with the demos.
 */
public class DemoUtil
{ 
    private static final Logger logger = LogManager.getLogger(MainClass.class);

	/**
	 * Create connection using properties; the properties are passed to
	 * the DriverManager. The properties usually
	 * have at least "user" and "password".
	 * 
	 * @param url database url
	 * @param props properties for connection
	 * @return database connection
	 */
	public static Connection getConnection(String url, Properties props) {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url, props);
		} catch (SQLException ex) {
			logger.error("SQL Exception. " + ex.getMessage());
		}
		return conn;
	}
	
	/**
	 * Provide a point with the child screen location based on the class name.
	 * 
	 * @param _className name of screen to position
	 * @return top-left point to locate child screen in relation to parent screen
	 */
	public static Point getChildScreenLocation(String _className) {
		// TODO Consider cascading based on name.
		
		Point result = new Point(MainClass.buttonWidth + 50, (MainClass.buttonHeight * MainClass.childScreenCount) + 50);
//		switch (_className) {
//		case "x":
//			result.setLocation(0,0);
//			break;		
//		default:
//			result.setLocation(0,0);
//			break;
//		}
		
		Screens.translateToPrefScreen(result);
		return result;
	}

	/**
	 * Run SQL statements from the sql file against the database.
	 * If an error is encountered a message is printed to stderr.
	 * 
	 * @param _conn database connection
	 * @param _resource sql resource name to run against the database
	 * @param _verbose indicates verbose mode
	 * @return false if an error was encountered
	 */
	public static boolean runSqlStatements(Connection _conn, String _resource, boolean _verbose) {
		boolean ok;
		if (_verbose) {
			System.err.println("===== Executing sql script: " + _resource);
		}
		try (InputStream stream = MainClass.class.getResourceAsStream(_resource)) {
			if (stream == null) {
				System.err.println("Script '" + _resource +"' not found. Exiting.");
				ok = false;
			} else {
				BufferedReader br = new BufferedReader(new InputStreamReader(stream));  
				ok = DemoUtil.runSqlStatements(_conn, br, _verbose);
			}
		} catch (IOException ex) {
			ok = false;
		}
		return ok;
	}
	
	/**
	 * Run SQL statements from the sql file against the database.
	 * 
	 * @param _conn database connection
	 * @param _br sql file to run against the database
	 * @param _verbose indicates verbose mode
	 * @return connection to the database
	 */
	public static boolean runSqlStatements(Connection _conn, BufferedReader _br, boolean _verbose) {

		boolean ok = false;
		
		try {
			Statement statement = _conn.createStatement();
			
			// Parse the sql
			List<String> queries = extractStatements(_br);
			
			for(String sql: queries) {
				runS(statement, sql, _verbose);
			}
			ok = true;
		} catch(SQLException ex) {
			logger.error("SQL Exception.", ex);
		}
		return ok;
	}
	
	private static void runS(Statement statement, String sql, boolean verbose)
	throws SQLException {
		if (verbose) {
			System.err.println("===== Executing sql statement:\n" + sql);
		}
		statement.execute(sql);
	}

	/**
	 * Use this method to parse a file for statements, ';' is the delimiter;
	 * column names can not include comment characters. 
	 * Comments starting with '#' or '--' terminate at end of line.
	 * SlashStar-StarSlash comments may span multiple lines,
	 * or there may be multiple of these on a single line, or both.
	 * <p>
	 * NOTE: for SQL file, column names must not have 
	 * '#', '--' or SlashStar since those are treated as comments.
	 *
	 * @param _br the file resource.
	 * @return List of query strings 
	 */
    public static ArrayList<String> extractStatements(BufferedReader _br)
    { 
		ArrayList<String> listOfQueries = new ArrayList<String>();
		ArrayList<String> listOfLines = new ArrayList<String>();
		LineReader lr = null;
        String line;
        int indexOfCommentSign;
        StringBuilder sBuffer =  new StringBuilder();
         
        try {  
			lr = new LineReader(_br);
       
            //read the file line by line
            while((line = lr.getNext()) != null)  {  
                // first stip out comments surrounded by /* */
                if(line.indexOf("/*") != -1) {
                    line = removeSlashStarComments(line, lr);
                }

                // ignore comments beginning with #
                if((indexOfCommentSign = line.indexOf('#')) != -1) {
                    line = line.substring(0, indexOfCommentSign);
                }

                // ignore comments beginning with --
                if((indexOfCommentSign = line.indexOf("--")) != -1) {
                    line = line.substring(0, indexOfCommentSign);
                }

                // like e.g. a.xyz FROM becomes a.xyzFROM otherwise and can not be executed 
                if(line != null)
                    // remove blank lines
                    if(!line.isEmpty()) {
                        listOfLines.add(line);
                        //System.err.println("OUT"+lino+": '" + line + "'");
                    }
                    //sBuffer.append(line + " ");  
            }  
            _br.close();

            // make one big string, preserve new lines
            for(String l : listOfLines) {
                sBuffer.append(l).append('\n');
            }
             
            // Use ";" as a delimiter for each statement.
            String[] splitQueries = sBuffer.toString().split(";");
             
            // filter out empty statements
            for(String query : splitQueries) {
                query = query.trim();
                if(!query.equals("") && !query.equals("\t"))  
                    listOfQueries.add(query);

            }
        }  
        catch(Exception e)  
        {  
			if(lr == null)
				logger.error("initialization error");
			else
				logger.error("*** Line "+lr.getLineNumber(), e);  
        }
        return listOfQueries;
    } 

	private static class LineReader {
		private BufferedReader br;
		private int lino;

		private LineReader(BufferedReader _br) {
			br = _br;
		}

		String getNext() throws IOException {
			String s = br.readLine();
			lino++;
			return s;
		}

		int getLineNumber() {
			return lino;
		}
	}

    private static String removeSlashStarComments(String firstLine, LineReader lr)
	throws IOException {
        boolean[] endComment = new boolean[1];
        String line = firstLine;
        StringBuilder sb = new StringBuilder();
        boolean inCom = false;
        while(true) {
            String t = removeSlashStarCommentsOnLine(line, inCom, endComment);
            sb.append(t);
            if(endComment[0])
                break;
            inCom = true;
            line = lr.getNext();
            if(line == null)
                break;
        }
        line = sb.toString();
        return line;
    }

    /** 
     * Called with a string that starts or is in a comment.
     * Find the chars within that line that are not part of a comment
     * @param _s string to analyze
     * @param _inComment indicates that string to analyze is within a comment
     * @param _endComment is true if the comment has finished
     * @return not comment chars
     */
    private static String removeSlashStarCommentsOnLine(String _s, boolean _inComment, boolean[] _endComment) {
        // Handle weird stuff like: xxx */f/* sdf */o/*dfdf*/o bar
        // which returns: foo bar
    	
    	// local copy of string to use
    	// Strings are immutable in Java so unnecessary, but eliminates a warning
    	String localS = _s;
    	
        boolean inComment = _inComment;
        StringBuilder sb = new StringBuilder();
        _endComment[0] = true;
        int idx;
        while(true) {
            if(inComment) {
                if((idx = localS.indexOf("*/")) == -1) {
                    // done with this line
                	_endComment[0] = false;
                    break;
                }
                // found an end of comment
                localS = localS.substring(idx+2);

                inComment = false;
            }
            // not in a comment
            if((idx = localS.indexOf("/*")) >= 0) {
                // another comment on this line, stash what we've got
                sb.append(localS.substring(0, idx));
                inComment = true;
            } else {
                // done with this line
                sb.append(localS);
                _endComment[0] = true;
                break;
            }
        }
        return sb.toString();
    }

	/**
	 * Load binary data into a database table.
	 * resourceName is read for a list of binary resource name and key.
	 * The sql is compiled into
	 * a prepared statement. Parameter 1 is set with a binary stream;
	 * parameter 2 is expected to be a key.
	 * 
	 * @param conn database connection
	 * @param resourceName list of files/keys
	 * @param sql use to put the binary data into the file
	 * @param verbose output progress information
	 * @return true if successful
	 */
	public static boolean loadBinaries(Connection conn, String resourceName, String sql, boolean verbose) {
		boolean ok = false;

		try (InputStream stream = MainClass.class.getResourceAsStream(resourceName)) {
			if(stream == null) {
				System.err.println("Images list '" + resourceName +"' not found. Exiting.");
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));  
			List<String> lines = DemoUtil.extractStatements(br);
			
			try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
				for(String l : lines) {
					String[] w = l.split("\\s+");
					if (verbose) {
						System.err.println("===== Loading image: '" + w[0] + "'");
					}
					try (InputStream img = MainClass.class.getResourceAsStream(w[0])) {
						pstmt.setBinaryStream(1, img);
						pstmt.setObject(2, w[1]);
						pstmt.executeUpdate();
					}
				}
				ok = true;
			} catch (SQLException ex) {
				logger.error("SQL exception", ex);
			}
		} catch (IOException ex) {
			ok = false;
		}

		return ok;
	}
}
