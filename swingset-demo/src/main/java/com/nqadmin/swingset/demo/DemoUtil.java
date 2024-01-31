/**
 * This class can be used to read sql files into an array of Strings, each 
 * representing a single query terminated by ";" 
 * Comments are filtered out. 
 */
package com.nqadmin.swingset.demo;
 
import com.nqadmin.rowset.JdbcRowSetImpl;
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
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.sql.RowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;

 
/**
 * Utilities for setting up a database to use with the demos.
 * <p>
 * The Demo environment supports 3 ways to get a Rowset using
 * {@link #getNewRowSet(java.sql.Connection, com.nqadmin.swingset.demo.DemoUtil.RowSetSource) }. 
 * Two of the ways allow the assignment of a connection to a JdbcRowSet,
 * several JdbcRowSet can share a single connection;
 * any threading and/or concurancy issues are handled by the connection.
 * The third way hooks into an actual connection pool, which is provided
 * by the database, and uses a {@link CachedRowSet}; in the case of the demo
 * the pool is never bigger than one.
 * <p>
 * One way uses {@link com.nqadmin.rowset.JdbcRowSetImpl}, which is a custom
 * JdbcRowSet implementation, which allows the direct assignment of a connection
 * to the RowSet for its use. This is useful in environments where a
 * {@link RowSetProvider} can not be used. For example
 * <pre>
 * {@code 
 *    rowSet = new JdbcRowSetImpl(connection);
 * }
 * </pre>
 * <p>
 * The demo contains two different DataSource implementations. One is trivial
 * and allows a given connection to be associated with a factory provided
 * RowSet. This scheme is semantically equivelent to the previously mentioned
 * mechanism, but without requiring a custom RowSet implementation.
 * It is used like
 * <pre>
 * {@code 
 *    rs = RowSetProvider.newFactory().createJdbcRowSet();
 *    rs.setDataSourceName(getDsName(connection));
 * }
 * </pre>
 * <p>
 * Here is an example for the third way using connection pool,
 * based on a connection pool provided by the database.
 * <pre>
 * {@code 
 *    rs = RowSetProvider.newFactory().createCachedRowSet();
 *    rs.setDataSourceName(DataSourcePool.DATA_SOURCE_NAME);
 * }
 * </pre>
 */
public class DemoUtil { 
	private DemoUtil() { }
	private static final Logger logger = LogManager.getLogger(MainClass.class);

	public static void logConnectionUsage() {
		logger.info(() -> "RowSetSourceDefault: " + whichRowSetDefault);
		logger.info(() -> String.format(
				"Connection pool: max active: %d, nOpen %d, nClose %d",
				DataSourcePool.cMax(), DataSourcePool.nOpen(), DataSourcePool.nClose()));
		for (String dsName : connMap.values()) {
			logger.info(() -> "Connection dsName: " + dsName);
		}
	}

	/**
	 * Used to specify how to create a new {@linkplain RowSet}.
	 */
	public enum RowSetSource {
		SHARE_JDBC,		// Use specified connection and JdbcRowSet
		POOL_CACHED,	// Use normal connection pool and CachedRowSet
		NQADMIN,
	}
	/** Track every connection that's seen. If a connection does not have an
	 * associated DataSourceName then the value is null.
	 */
	private static final Map<Connection, String> connMap = new IdentityHashMap<>();
	private static InitialContext initialContext = null;
	private static RowSetFactory rsFactory = null;

	private static RowSetSource whichRowSetDefault = RowSetSource.SHARE_JDBC;

	/**
	 * The property {@code WhichRowSetDefault} controls how a {@linkplain RowSet}
	 * is created by {@linkplain #getNewRowSet(java.sql.Connection) }.
	 * @return default type of RowSet
	 */
	public static RowSetSource getWhichRowSetDefault() {
		return whichRowSetDefault;
	}

	/**
	 * Establish the default for how a RowSet is created.
	 * @param _whichRowSetDefault default
	 */
	public static void setWhichRowSetDefault(RowSetSource _whichRowSetDefault) {
		if (_whichRowSetDefault != null) {
			whichRowSetDefault = _whichRowSetDefault;
		}
	}

	private static void rowSetSourceInit(RowSetSource whichRowSet) throws SQLException {
		if (whichRowSet == RowSetSource.NQADMIN || rsFactory != null) {
			return;
		}

		String factory = TrivialCtxFactory.class.getName();
		logger.info(() -> "Initializing naming factory: " + factory);
		System.setProperty("java.naming.factory.initial", factory);
		try {
			InitialContext ctx = new InitialContext();
			// Create and bind the Pool
			ctx.bind(DataSourcePool.DATA_SOURCE_NAME,
					DataSourcePool.getDataSource());
			initialContext = ctx;
		} catch (NamingException ex) {
			throw new RuntimeException(ex);
		}

		rsFactory = RowSetProvider.newFactory();

		if (Boolean.FALSE) {	// For debug to see variety of stats
			debugRowSetSourceConnections(initialContext);
		}
	}

	/**
	 * Build a RowSet with a connection, or how to get one, for use in the Demo.
	 * There a flag for whether or not to use the jdk standard mechanisms or
	 * Pangburn group's JdbcRowSetImpl.
	 * @param connection only used to build JdbcRowSetImpl.
	 * @return The RowSet with either a Connection or DataSource
	 * @throws SQLException 
	 */
	public static RowSet getNewRowSet(Connection connection) throws SQLException {
		return getNewRowSet(connection, whichRowSetDefault);
	}

	/**
	 * Like {@linkplain getNewRowSet(Connection)}, but specify RowSetSource.
	 * Note: connection can be null, but only if whichRowSet is POOL_CACHED.
	 * @param connection only used to build JdbcRowSetImpl.
	 * @param whichRowSet how to construct the RowSet and/or get it's connection
	 * @return The RowSet with either a Connection or DataSource
	 * @throws SQLException 
	 */
	public static RowSet getNewRowSet(Connection connection, RowSetSource whichRowSet) throws SQLException {
		rowSetSourceInit(whichRowSet);

		if (whichRowSet != RowSetSource.POOL_CACHED) {
			Objects.requireNonNull(connection);
		}

		if (connection != null) {
			connMap.putIfAbsent(connection, null);
		}

		RowSet rs;
		switch (whichRowSet) {
			case SHARE_JDBC:
				rs = RowSetProvider.newFactory().createJdbcRowSet();
				rs.setDataSourceName(getDsName(connection));
				logger.debug(() -> "DataSource: " + getDsName(connection));
				break;
			case POOL_CACHED:
				rs = RowSetProvider.newFactory().createCachedRowSet();
				rs.setDataSourceName(DataSourcePool.DATA_SOURCE_NAME);
				logger.debug(() -> "DataSource: " + DataSourcePool.DATA_SOURCE_NAME);
				break;
			case NQADMIN:
				rs = new JdbcRowSetImpl(connection);
				logger.debug(() -> "DataSource: " + RowSetSource.NQADMIN);
				break;
			default:
				throw new RuntimeException("Unknown data source");
		}
		return rs;
	}

	private static String getDsName(Connection conn) {
		return connMap.computeIfAbsent(conn, k -> {
			// create a ds name and binding for this specific connection
			int id = System.identityHashCode(k);
			String dsName = null;
			// For production, must guarentee that same ds not in use.
			int t = 0;
			while (dsName == null || connMap.values().contains(dsName)) {
				++t;
				dsName = "ds-" + t + "-" + id;
			}
			String finalDsName = dsName;
			logger.info(() -> "Creating new DataSourceShareConnection: " + finalDsName );
			try {
				initialContext.bind(dsName, DataSourceShareConnection.getDataSource(conn));
			} catch (NamingException ex) {
				throw new RuntimeException(ex);
			}
			return dsName;
		});
	}

	/** open/close stuff to show up in statistics */
	@SuppressWarnings("CallToPrintStackTrace")
	private static void debugRowSetSourceConnections(InitialContext ctx) {
		try {
			DataSource ds = (DataSource) ctx.lookup(DataSourcePool.DATA_SOURCE_NAME);
			Connection c = ds.getConnection();
			RowSet rs1 = getNewRowSet(c, RowSetSource.POOL_CACHED);
			RowSet rs2 = getNewRowSet(c, RowSetSource.SHARE_JDBC);
			c.close();
			c = ds.getConnection();
			RowSet rs3 = getNewRowSet(c, RowSetSource.SHARE_JDBC);
			c.close();
			rs1.close();
			rs2.close();
			rs3.close();
		} catch (SQLException | NamingException ex) {
			ex.printStackTrace();
		}
	}

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
	@SuppressWarnings("UseOfSystemOutOrSystemErr")
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
	
	@SuppressWarnings("UseOfSystemOutOrSystemErr")
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
	@SuppressWarnings({"BroadCatchBlock", "TooBroadCatch", "UseSpecificCatch"})
    public static ArrayList<String> extractStatements(BufferedReader _br)
    { 
		ArrayList<String> listOfQueries = new ArrayList<>();
		ArrayList<String> listOfLines = new ArrayList<>();
		LineReader lr = null;
        String line;
        int indexOfCommentSign;
        StringBuilder sBuffer =  new StringBuilder();
         
        try {  
			lr = new LineReader(_br);
       
            //read the file line by line
            while((line = lr.getNext()) != null)  {  
                // first stip out comments surrounded by /* */
                if(line.contains("/*")) {
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
                if(!query.isEmpty() && !query.equals("\t"))  
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

                // inComment = false; warning: Assigned value never used
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
	@SuppressWarnings("UseOfSystemOutOrSystemErr")
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
