/*******************************************************************************
 * Copyright (C) 2003-2020, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * Contributors:
 *   Prasanth R. Pasala
 *   Brian E. Pangburn
 *   Diego Gil
 *   Man "Bee" Vo
 *   Ernie R. Rael
 ******************************************************************************/
package com.nqadmin.swingset.demo;

import com.nqadmin.swingset.models.SSCollectionModel;
import com.nqadmin.swingset.models.SSMysqlSetModel;
import com.nqadmin.swingset.models.SSStringArrayModel;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.h2.tools.RunScript;

import gnu.getopt.Getopt;
import java.sql.JDBCType;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * A JFrame with buttons to launch each of the SwingSet example/demo screens.
 */
public class MainClass extends JFrame {
	private static final Map<String, Object> globalHints = new HashMap<>();

	/**
     * ActionListener implementation to call code for each button.
     */
    private class MyButtonListener implements ActionListener{

		public MyButtonListener() {
			super();
		}

		@Override
		public void actionPerformed( final ActionEvent ae){
			final Map<String, Object> hints = new HashMap<>(globalHints);

            if(ae.getSource().equals(btnExample1)){
                new Example1(dbConnection);
            }
            else if(ae.getSource().equals(btnExample2)){
            	new Example2(dbConnection);
            }
            else if(ae.getSource().equals(btnExample3)){
            	new Example3(dbConnection);
            }
            else if(ae.getSource().equals(btnExample4)){
            	new Example4(dbConnection);
            }
            else if(ae.getSource().equals(btnExample5)){
            	new Example5(dbConnection);
            }
            else if(ae.getSource().equals(btnExample6)){
            	new Example6(dbConnection);
            }
            else if(ae.getSource().equals(btnExample7)){
            	new Example7(dbConnection);
            }
            else if(ae.getSource().equals(btnTestBase)){
            	new TestBaseComponents(dbConnection, hints);
            }
            else if(ae.getSource().equals(btnTestGrid)){
            	// TODO
            	//new TestGridComponents(dbConnection);
            }
            else if(ae.getSource().equals(btnTestFormatted)){
            	new TestFormattedComponents(dbConnection);
            }
        }
    }


	/**
	 * component dimensions
	 */
	private static final int buttonHeight = 25;
	private static final int buttonWidth = 150;
	public static final Dimension buttonDim = new Dimension(buttonWidth, buttonHeight);
	
	public static final int gridColumnWidth = 60;
	
	private static final int labelHeight = 20;
	private static final int labelHeightTall = 100; // used for lists, textareas
	private static final int labelHeightVeryTall = 100;  // used for images
	private static final int labelWidth = 200;
	
	public static final Dimension labelDim = new Dimension(labelWidth, labelHeight);
	public static final Dimension labelDimTall = new Dimension(labelWidth, labelHeightTall); // used for lists, textareas
	public static final Dimension labelDimVeryTall = new Dimension(labelWidth, labelHeightVeryTall); // used for images
	
	private static final int ssHeight = 20;
	private static final int ssHeightTall = 100; // used for lists, textareas
	private static final int ssHeightVeryTall = 200; // used for images
	private static final int ssWidth = 200;
	
	public static final Dimension ssDim = new Dimension(ssWidth, ssHeight);
	public static final Dimension ssDimTall = new Dimension(ssWidth, ssHeightTall); // used for lists, textareas
	public static final Dimension ssDimVeryTall = new Dimension(ssWidth, ssHeightVeryTall); // used for images

	public static final int childScreenHeight = 400;
	public static final int childScreenHeightTall = 800;
	public static final int childScreenWidth = 600;

	/**
	 * database
	 */
	private static final String DATABASE_NAME = "suppliers_and_parts";
	private static final String DATABASE_PATH = "//localhost/~/h2/databases/";

	private static final boolean USE_IN_MEMORY_DATABASE = true;
	 
	private static final String DATABASE_SCRIPT_DEMO = "suppliers_and_parts.sql";
	private static final String DATABASE_SCRIPT_TEST = "swingset_tests.sql";
	private static final String DATABASE_SCRIPT_TEST_IMAGES = "swingset_tests_load_blobs.sql";
	
    private Connection dbConnection = null;
    
    /**
	 * buttons to launch code examples
	 */
	private JButton btnExample1 = new JButton("Example1");
	private JButton btnExample2 = new JButton("Example2");
	private JButton btnExample3 = new JButton("Example3");
	private JButton btnExample4 = new JButton("Example4");
	private JButton btnExample5 = new JButton("Example5");
	private JButton btnExample6 = new JButton("Example6");
	private JButton btnExample7 = new JButton("Example7");
	private JButton btnTestBase = new JButton("Test Base Components");
	private JButton btnTestFormatted = new JButton("Test Formatted Components");
	private JButton btnTestGrid = new JButton("Test Grid Components");

	/**
	 * Log4j2 Logger
	 */
    private static final Logger logger = LogManager.getLogger(MainClass.class);
	private static final boolean RUN_SQL_SCRIPTS = true;
	
	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = -6316984401822746124L;

    /**
     * Constructor for MainClass
     */
    public MainClass(){

        // SETUP WINDOW
	    	super("SwingSet Demo");
	        setSize(300,300);
	        setDefaultCloseOperation(EXIT_ON_CLOSE);
		
	    // ECHO WORKING DIRECTORY
	        logger.info("Working Directory = " +
	                System.getProperty("user.dir"));

	    // INITIALIZE DATABASE
			if ("h2".equals(dbname)) {
				dbConnection = getDatabase();
			} else {
				databaseSetup.run();
				dbConnection = databaseSetup.getConnection();
			}
    		if (dbConnection == null) {
				logger.fatal("Error initializing database. Exiting.");
				System.exit(1);
    		}

	    // ADD ACTION LISTENERS FOR BUTTONS
	        btnExample1.addActionListener( new MyButtonListener());
	        btnExample2.addActionListener( new MyButtonListener());
	        btnExample3.addActionListener( new MyButtonListener());
	        btnExample4.addActionListener( new MyButtonListener());
	        btnExample5.addActionListener( new MyButtonListener());
	        btnExample6.addActionListener( new MyButtonListener());
	        btnExample7.addActionListener( new MyButtonListener());
	        btnTestBase.addActionListener( new MyButtonListener());
	        btnTestGrid.addActionListener( new MyButtonListener());
	        btnTestFormatted.addActionListener( new MyButtonListener());

        // SET BUTTON DIMENSIONS
	        btnExample1.setPreferredSize(buttonDim);
	        btnExample2.setPreferredSize(buttonDim);
	        btnExample3.setPreferredSize(buttonDim);
	        btnExample4.setPreferredSize(buttonDim);
	        btnExample5.setPreferredSize(buttonDim);
	        btnExample6.setPreferredSize(buttonDim);
	        btnExample7.setPreferredSize(buttonDim);
	        btnTestBase.setPreferredSize(buttonDim);
	        btnTestGrid.setPreferredSize(buttonDim);
	        btnTestFormatted.setPreferredSize(buttonDim);

	    // LAYOUT BUTTONS
	        getContentPane().setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));
	        getContentPane().add(btnExample1);
	        getContentPane().add(btnExample2);
	        getContentPane().add(btnExample3);
	        getContentPane().add(btnExample4);
	        getContentPane().add(btnExample5);
	        getContentPane().add(btnExample6);
	        getContentPane().add(btnExample7);
	        getContentPane().add(btnTestBase);
	        //getContentPane().add(this.btnTestGrid);
	        getContentPane().add(btnTestFormatted);

        // DISPLAY SCREEN
	        setVisible(true);
	        //pack();
    }

    /**
     * Class to initialize the database connection and load the database content from a script
     */
    protected Connection getDatabase() {

    	Connection result = null;

		// INITIALIZE DATABASE CONNECTION AND COMPONENTS
		try {

			Class.forName("org.h2.Driver");

			logger.debug("Resource path: " + getClass().getPackage().getName());
			logger.debug("Resource path: " + getClass().getClassLoader().getResource(DATABASE_SCRIPT_DEMO));

	        final InputStream inStreamDemo = getClass().getClassLoader().getResourceAsStream(DATABASE_SCRIPT_DEMO);
        	final InputStream inStreamTest = getClass().getClassLoader().getResourceAsStream(DATABASE_SCRIPT_TEST);
        	InputStream inStreamTestImages = null;

	        if (USE_IN_MEMORY_DATABASE) {
	        	inStreamTestImages = getClass().getClassLoader().getResourceAsStream(DATABASE_SCRIPT_TEST_IMAGES);
	        } else {
	        	logger.info("Running H2 as a database server (versus an in-memory database) so binary files (e.g., images) cannot be pre-populated to any BLOB column(s).");
	        }
	        if ((inStreamDemo == null) || (inStreamTest == null)) {
	            logger.fatal("Please add the file "
	            		+ DATABASE_SCRIPT_DEMO
	            		+ " and "
	            		+ DATABASE_SCRIPT_TEST
	            		+ " and "
	            		+ DATABASE_SCRIPT_TEST_IMAGES
	            		+ " to the classpath, package "
	                    + getClass().getPackage().getName());
	        } else {
	        	if (USE_IN_MEMORY_DATABASE) {
	        		result = DriverManager.getConnection("jdbc:h2:mem:" + DATABASE_NAME);
	        		logger.info("Established connection to in-memory database.");
	        	} else {
	        	// ASSUMING DATABASE IS IN LOCAL ./h2/databases/ FOLDER WITH DEFAULT USERNAME OF sa AND BLANK PASSWORD
	        	// USEFUL FOR WORKING WITH DATASET FOR SWINGSET TESTS
	        		result = DriverManager.getConnection("jdbc:h2:tcp:" + DATABASE_PATH + DATABASE_NAME,"sa","");
	        		logger.info("Established connection to database server.");
	        	}

	        	// RUN SCRIPTS AND CLOSE STREAMS
	        	if (RUN_SQL_SCRIPTS) {
		            RunScript.execute(result, new InputStreamReader(inStreamDemo));
		            inStreamDemo.close();

		            RunScript.execute(result, new InputStreamReader(inStreamTest));
		            inStreamTest.close();

					if (!no_load_images) {
						String sql = "UPDATE swingset_base_test_data"
								+ " SET ss_image = ? WHERE swingset_base_test_pk = ?";
						DemoUtil.loadBinaries(result, "/swingset-demo-images.txt", sql, verbose);
					}
	        	}
	        }

		} catch (final IOException ioe) {
			logger.error("IO Exception.", ioe);
		} catch (final SQLException se) {
			logger.error("SQL Exception.", se);
		} catch (final ClassNotFoundException cnfe) {
			logger.error("Class Not Found Exception.", cnfe);
		}

		return result;
    }

	static abstract class DatabaseSetup implements Runnable {
		private Connection conn;

		abstract Properties getDatabaseProperties();
		abstract List<String> getScripts();

		Connection getConnection() {
			return conn;
		}

		/**
		 * Find resource;
		 * an alternate implementation could take the resource as a file.
		 * @param resourceName name of resource
		 * @return 
		 */
		BufferedReader getBufferedReader(String resourceName) {
			InputStream stream = MainClass.class.getResourceAsStream(resourceName);
			if (stream == null) {
				System.err.println("Script '" + resourceName +"' not found. Exiting.");
				return null;
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));  
			return br;
		}

		public void run() {
			Properties info = getDatabaseProperties();
			if(info == null) {
				return;
			}
			String clazz = info.getProperty("DB_DRIVER_CLASS", "");

			if (clazz.isEmpty()) {
				System.err.println("'DB_DRIVER_CLASS' not found in property file");
				conn = null;
				return;
			}
			
			try {
				Class.forName(clazz);
			} catch (ClassNotFoundException ex) {
				System.err.println("Class '" + clazz + "' not found");
				conn = null;
				return;
			}
			
			conn = DemoUtil.getConnection(info.getProperty("DB_URL"), info);
			if (conn == null) {
				return;
			}

			if (no_initialize_db) {
				return;
			}
			
			boolean ok = false;
			for (String script : getScripts()) {
				ok = false;
				BufferedReader br = getBufferedReader(script);
				if (br != null) {
					ok = DemoUtil.runSqlStatements(conn, br, verbose);
				}
				if (!ok) {
					break;
				}
			}
			
			if (ok && !no_load_images) {
				// Load up the images
				String sql = "UPDATE swingset_base_test_data"
						+ " SET ss_image = ? WHERE swingset_base_test_pk = ?";
				ok = DemoUtil.loadBinaries(conn, "/swingset-demo-images.txt", sql, verbose);
			}
			
			if (!ok) {
				conn = null;
			}
		}

		Properties getFileProperties(String fname) {
			try {
				Properties props = new Properties();
				FileReader reader = new FileReader(fname);
				props.load(reader);
				return props;
			} catch (FileNotFoundException ex) {
				System.err.println("Property file '" + fname + "' not found");
			} catch (IOException ex) {
				logger.error("IO exception.", ex);
			}
			return null;
		}

		void dump() {
			for (String script : getScripts()) {
				boolean ok = false;
				try (BufferedReader br = getBufferedReader(script)){
					if (br != null) {
							String name = "dump." + new File(script).getName();
							try (FileWriter fout = new FileWriter(name)) {
								char buf[] = new char[4 * 1024];
								int n;
								while((n = br.read(buf)) > 0) {
									fout.write(buf, 0, n);
								}
							}
					}
					ok = true;
				} catch (IOException ex) {
					logger.error("IO exception.", ex);
				}
				if (!ok) {
					break;
				}
			}
		}
	}

	static class ExternalSetup extends DatabaseSetup {
		List<String> sqlFiles;

		public ExternalSetup(List<String> sqlFiles) {
			this.sqlFiles = sqlFiles;
		}

		@Override
		BufferedReader getBufferedReader(String fileName) {
			try {
				return new BufferedReader(new FileReader(fileName));
			} catch (FileNotFoundException ex) {
				System.err.println("Script '" + fileName +"' not found. Exiting.");
				return null;
			}
		}

		@Override
		Properties getDatabaseProperties() {
			Properties info = null;
			info = getFileProperties(propertyFile);
			return info;
		}

		@Override
		List<String> getScripts() {
			return sqlFiles;
		}
	}

	/**
	 * This is only used to dump H2 sql files
	 */
	static class H2Setup extends DatabaseSetup {

		@Override
		Properties getDatabaseProperties() {
			return null;
		}

		public void run() {
			return;
		}

		@Override
		List<String> getScripts() {
			return Arrays.asList(new String[] {
				"/" + DATABASE_SCRIPT_DEMO ,
				"/" + DATABASE_SCRIPT_TEST ,
				"/" + DATABASE_SCRIPT_TEST_IMAGES
			});
		}
	}

	static class MysqlSetup extends DatabaseSetup {

		@Override
		public void run() {
			super.run();
			globalHints.put("collectionModel", (Supplier<SSCollectionModel>)
					() -> new SSMysqlSetModel(JDBCType.INTEGER));
			// 		() -> new SSStringArrayModel(JDBCType.INTEGER));
		}

		@Override
		Properties getDatabaseProperties() {
			//      <dependency>
			//          <groupId>mysql</groupId>
			//          <artifactId>mysql-connector-java</artifactId>
			//          <version>8.0.21</version>
			//      </dependency>
			Properties info;
			if (propertyFile != null) {
				info = getFileProperties(propertyFile);
			} else {
				info = new Properties();
				info.put("DB_DRIVER_CLASS", "com.mysql.cj.jdbc.Driver");
				info.put("DB_URL", "jdbc:mysql://localhost/swingset_demo_suppliers_and_parts");
				// DB_NAME NOT USED
				info.put("DB_NAME", "swingset_demo_suppliers_and_parts");
				info.put("user", "root");
				info.put("serverTimezone", "UTC");
			}
			return info;
		}

		@Override
		List<String> getScripts() {
			return Arrays.asList(new String[] {
				"/mysql.swingset-demo-app.sql",
				"/mysql.swingset-demo-components.sql"
			});
		}
	}

	private static final String DBMS_MYSQL = "mysql";
	private static final String DBMS_H2 = "h2";
	private static final String DBMS_EXTERNAL = "external";

	/*
	 * defaults for optional command line arguments
	 */
	private static boolean verbose = false;
	private static boolean dump = false;
	private static boolean no_load_images = false;
	private static boolean no_initialize_db = false;
	private static boolean readme = false;
	private static String dbname = DBMS_H2;
	private static String propertyFile = null;
	private static List<String> userSqlFiles = new ArrayList<>();
	private static DatabaseSetup databaseSetup = null;

	private static String cmdName = "SwingSetDemo";

	private static void usage() {
		// TODO: specify don't load images
		String usage =
				"\n"
				+ "Run the SwingSet demo. With no options/args use the self contained\n"
				+ "in memory database.\n"
				+ "\n"
				+ cmdName + " [-h] [-v] [-d] [-n] [-i] [-r] [-p fname] [-s sql]* [dbms-server]\n"
				+ "\n"
				+ "    -h             help\n"
				+ "    -v             verbose; output initialization sql as executed\n"
				+ "    -d             dump/create sql scripts in local directory, exit\n"
				+ "    -n             do NOT initialize database, just run demo\n"
				+ "    -i             do NOT load images\n"
			//	+ "    -r             print a readme to stdout\n"
				+ "    -p fname       properties file for jdbc database connection\n"
				+ "                   'DB_URL', 'DB_DRIVER_CLASS' keys required\n"
				+ "    -s sqlScript   sql file to initialize database, multiple OK\n"
				+ "\n"
				+ "If specified, dbms-server in {mysql}\n"
				+ "Internal mysql properties use database swingset_demo_suppliers_and_parts.\n"
				+ "After the sql files are run, the images are loaded, unless '-i'.\n"
				+ "Use '-n -p props' to run demo with a previously initialized database.\n"
				+ "Use '-d' or '-d mysql' to create local files with sql initialization.\n"
				+ "See swingset-demo/README.txt for more information.\n"
				+ "\n"
				+ "Examples: (ss.jar like swingset-demo-vers-jar-with-dependencies.jar)\n"
				+ "    java -jar ss.jar -d   # dump sql that creates in memory database\n"
				+ "    java -jar ss.jar -d mysql   # dump sql to create mysql database\n"
				+ "    java -cp jdbc_driver:ss.jar com.nqadmin.swingset.demo.MainClass \\\n"
				+ "        -p db_props -s initializer.sql\n"
				+ "\n"
				;
		System.err.println(usage);
		System.exit(1);
	}
	
	/**
	 * Main method for SwingSet samples/demo
	 * <p>
	 * @param _args - optional command line arguments, which are ignored by this program
	 */
	public static void main(final String[] _args) {
		boolean some_error = false;

		Getopt g = new Getopt(cmdName, _args, "hvdinrp:s:");

		int c;
		while ((c = g.getopt()) != -1)
		{
			switch(c)
			{
				case 'v': verbose = true;                   break;
				case 'd': dump = true;                      break;
				case 'n': no_initialize_db = true;          break;
				case 'i': no_load_images = true;            break;
				case 'r': readme = true;                    break;
				case 'p': propertyFile = g.getOptarg();     break;
				case 's': userSqlFiles.add(g.getOptarg());  break;
				case '?':
				case 'h':
					some_error = true;
					break; // getopt() already printed an error
			}
		}

		if (verbose) {
			StringBuffer sb = new StringBuffer();
			sb.append('\n').append(cmdName);
			for (int i = 0; i < _args.length; i++) {
				String arg = _args[i];
				sb.append(' ').append(arg);
			}
			System.err.println(sb.toString());
		}

		if (some_error) {
			usage();
		}

		// if (readme) {
		// 	return;
		// }

		boolean databaseServerSpecified = false;

		int i = g.getOptind();
		if (i != _args.length) {
			// There are positional arguemnts. Can only be one.
			if (i + 1 != _args.length) {
				usage();
			}
			dbname = _args[i];
			databaseServerSpecified = true;
		}

		boolean useInternalSql = userSqlFiles.isEmpty();

		if (!useInternalSql) {
			if (propertyFile == null) {
				System.err.println("-p required if -s is used. Exiting.");
				some_error = true;
			} else {
				// there's both a property file and sql files
				if (databaseServerSpecified) {
					System.err.println("-s not allowed if database specified. Exiting.");
					some_error = true;
				} else {
					dbname = DBMS_EXTERNAL;
				}
			}
			if (dump) {
				System.err.println("-d not allowed when -s is used. Exiting.");
				some_error = true;
			}
		}

		switch(dbname) {
			case DBMS_MYSQL:
				databaseSetup = new MysqlSetup();
				break;
			case DBMS_EXTERNAL:
				databaseSetup = new ExternalSetup(userSqlFiles);
				break;
			case DBMS_H2:
				databaseSetup = new H2Setup();
				break;
			default:
				System.err.println("Unknown database server '" + dbname + "' . Exiting.");
				some_error = true;
				break;
		}

		if (some_error) {
			System.exit(1);
		}

		if (dump) {
			databaseSetup.dump();
			return;
		}
		
		// create application screen
		SwingUtilities.invokeLater(() -> new MainClass());
	}
} // end public class MainClass extends JFrame {
