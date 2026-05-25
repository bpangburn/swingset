/*******************************************************************************
 * Copyright (C) 2003-2021, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Supplier;

import javax.sql.RowSet;
import javax.sql.rowset.spi.SyncFactory;
import javax.sql.rowset.spi.SyncFactoryException;
import javax.sql.rowset.spi.SyncProvider;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;

import org.h2.tools.RunScript;

import com.nqadmin.swingset.SSComboBox;
import com.nqadmin.swingset.datasources.DefaultSSDBSupport;
import com.nqadmin.swingset.datasources.RowSetOps.ForceConflict;
import com.nqadmin.swingset.decorators.BackgroundDecorator;
import com.nqadmin.swingset.decorators.BorderDecorator;
import com.nqadmin.swingset.decorators.Decorator;
import com.nqadmin.swingset.decorators.DecoratorSupplier;
import com.nqadmin.swingset.models.SSCollectionModel;
import com.nqadmin.swingset.models.SSMysqlSetModel;
import com.nqadmin.swingset.navigate.Utils;
import com.nqadmin.swingset.utils.CentralLookup;
import com.nqadmin.swingset.utils.SSUtils;
import com.nqadmin.swingset.utils.SSVersion;
import com.raelity.lib.ui.Screens;

import gnu.getopt.Getopt;

import static com.nqadmin.swingset.demo.DemoUtil.configureJavaUtilLogger;
import static com.nqadmin.swingset.utils.CentralLookup.defLookup;
import static com.nqadmin.swingset.utils.SSUtils.sf;
import static java.lang.System.Logger.Level.*;

/**
 * A JFrame with buttons to launch each of the SwingSet example/demo screens.
 */
@SuppressWarnings("serial")
public class MainClass extends JFrame
{

	/**
	 * database
	 */
	public static final String DATABASE_NAME = "suppliers_and_parts";
	private static final String DATABASE_PATH = "//localhost/~/h2/databases/";

	private static final boolean USE_IN_MEMORY_DATABASE = true;

	private static final String DATABASE_SCRIPT_DEMO = "suppliers_and_parts.sql";
	private static final String DATABASE_SCRIPT_TEST = "swingset_tests.sql";
	private static final String DATABASE_SCRIPT_TEST_IMAGES = "swingset_tests_load_blobs.sql";
	private static final String DATABASE_SCRIPT_DEBUG = "swingset_debug.sql";

	private static final boolean RUN_DEMO_SQL_SCRIPTS = true;
	private static final boolean RUN_TEST_SQL_SCRIPTS = true;
	private static final boolean RUN_DEBUG_SQL_SCRIPTS = false;


	private static final Map<String, Object> globalHints = new HashMap<>();

	static {
		CentralLookup lkup = CentralLookup.getDefault();
		lkup.replace(Decorator.DecoratorStyle.class, Decorator.DecoratorStyle.BORDER);
		lkup.add(new DecoratorSupplier(() -> {return new BorderDecorator();}));
		lkup.add(new DecoratorSupplier(() -> {return new BackgroundDecorator();}));

		if(Boolean.FALSE) {
			// Get rid of unused warnings
			Objects.nonNull(new SSUtils.DebugRowSetListenerFlag());
			Objects.nonNull(new ForceConflict(0));
			Objects.nonNull(new H2Trace(""));
			Objects.nonNull(new MainClass().new LogManListener());
		}
	}

	static class LoadDemoImages {}
	// https://h2database.com/html/features.html#trace_options
	// lkup.replace(H2Trace.class, new H2Trace(";TRACE_LEVEL_SYSTEM_OUT=3"));
	static class H2Trace {
		private final String flags;
		H2Trace() { this.flags = ""; }
		H2Trace(String flags) { this.flags = flags; }
		String getTraceUrlFlags() { return flags; }
	}
	static class H2Workaround {}

	/**
	 * ActionListener implementation to call code for each button.
	 */
	private class MyButtonListener implements ActionListener {

		public MyButtonListener() {
			super();
		}

		@SuppressWarnings({"unused", "ResultOfObjectAllocationIgnored"})
		@Override
		public void actionPerformed(final ActionEvent ae) {
			final Map<String, Object> hints = new HashMap<>(globalHints);
			Utils.dumpLatestEvents("New Window:");

			Object source = ae.getSource();

			if (source == btnExample1) {
				logger.log(DEBUG, "**** Opening Example1 ****");
				new Example1(dbConnection);
			} else if (source == btnExample2) {
				logger.log(DEBUG, "**** Opening Example2 ****");
				new Example2(dbConnection);
			} else if (source == btnExample3) {
				logger.log(DEBUG, "**** Opening Example3 ****");
				new Example3(dbConnection);
			} else if (source == btnExample4) {
				logger.log(DEBUG, "**** Opening Example4 ****");
				new Example4(dbConnection);
			} else if (source == btnExample4Advanced) {
				logger.log(DEBUG, "**** Opening Example4Advanced ****");
				new Example4Advanced(dbConnection);
			} else if (source == btnExample4UsingHelper) {
				logger.log(DEBUG, "**** Opening Example4UsingHelper ****");
				JFrame e4JFrame = new JFrame("Example4 Using Helper");
				e4JFrame.setLocation(DemoUtil.getChildScreenLocation("Example4UsingHelper"));
				Example4UsingHelper example4 = new Example4UsingHelper(dbConnection, e4JFrame);
				e4JFrame.add(example4);
				example4.showUp(e4JFrame);
				e4JFrame.pack();
				// screen dimensions handled by SSScreenHelperCommon.setScreenSize()
				e4JFrame.setVisible(true);
			} else if (source == btnExample5) {
				logger.log(DEBUG, "**** Opening Example5 ****");
				new Example5(dbConnection);
			} else if (source == btnExample6) {
				logger.log(DEBUG, "**** Opening Example6 ****");
				new Example6(dbConnection);
			} else if (source == btnExample7) {
				logger.log(DEBUG, "**** Opening Example7 ****");
				new Example7(dbConnection);
			} else if (source == btnExample7UsingHelper) {
				logger.log(DEBUG, "**** Opening Example7UsingHelper ****");
				JFrame e7JFrame = new JFrame("Example7 Using Helper");
				Example7UsingHelper example7 = new Example7UsingHelper(dbConnection, null);
				e7JFrame.add(example7);
				e7JFrame.setLocation(DemoUtil.getChildScreenLocation("Example7UsingHelper"));
				example7.showUp(e7JFrame);
				//e7JFrame.pack(); // NOT USING .pack() FOR DATA GRID SCREENS
				e7JFrame.setSize(MainClass.childScreenWidth, MainClass.childScreenHeight);
				e7JFrame.setVisible(true);
			} else if (source == btnTestBase) {
				logger.log(DEBUG, "**** Opening TestBaseComponents ****");
				new TestBaseComponents(dbConnection, hints);
			} else if (source == btnTestGrid) {
				logger.log(DEBUG, "**** TestGridComponents not implemented ****");
				// TODO
				// new TestGridComponents(dbConnection);
			} else if (source == btnTestFormatted) {
				logger.log(DEBUG, "**** Opening TestFormattedComponents ****");
				new TestFormattedComponents(dbConnection);
			}
		}
	}

	@SuppressWarnings("serial")
	private class ComboRowSetSource extends SSComboBox {
		Popup popup;

		public ComboRowSetSource() {
			super(ModelType.SWING);
			setMaximumSize(new Dimension(210, 25));
			setAllowNull(false);
			DemoUtil.RowSetSource rsSource = DemoUtil.getWhichRowSetDefault();
			setDisplayValues(DemoUtil.RowSetSource.class);
			setChosenEnum(rsSource);
			btnRowSetSource.setText("RowSet: " + rsSource.toString());
		}

		@Override
		public DemoUtil.RowSetSource getChosenEnum() {
			return (DemoUtil.RowSetSource) super.getChosenEnum();
		}

		@Override
		public void setSelectedItem(Object anObject) {
			super.setSelectedItem(anObject);
			DemoUtil.RowSetSource rsSource = getChosenEnum();
			DemoUtil.setWhichRowSetDefault(rsSource);
			btnRowSetSource.setText("RowSet: " + rsSource.toString());
			if (popup == null) {
				return;
			}
			popup.hide();
			popup = null;
		}
	}

	private class RowSetSourceMouseListener extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (comboRowSetSource.popup != null) {
				comboRowSetSource.popup.hide();
				comboRowSetSource.popup = null;
				return;
			}
			Point p = e.getLocationOnScreen();
			Popup popup = PopupFactory.getSharedInstance().getPopup(
					null, comboRowSetSource, p.x, p.y);
			comboRowSetSource.popup = popup;
			popup.show();
			SwingUtilities.invokeLater(() -> comboRowSetSource.showPopup());
		}
	}

	private class LogManListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
		}

	}

	/** component dimension */
	protected static final int buttonHeight = 25;
	/** component dimension */
	protected static final int buttonWidth = 250;
	/** component dimension */
	protected static final Dimension buttonDim = new Dimension(buttonWidth, buttonHeight);

	/** component dimension */
	protected static final int gridColumnWidth = 60;

	private static final int labelHeight = 20;
	private static final int labelHeightTall = 100; // used for lists, textareas
	private static final int labelHeightVeryTall = 100; // used for images
	private static final int labelWidth = 200;

	/** component dimension default*/
	protected static final Dimension labelDim = new Dimension(labelWidth, labelHeight);
	/** component dimension for lists, text areas */
	protected static final Dimension labelDimTall = new Dimension(labelWidth, labelHeightTall);
	/** component dimension for images */
	protected static final Dimension labelDimVeryTall = new Dimension(labelWidth, labelHeightVeryTall);

	private static final int ssHeight = 20;
	private static final int ssHeightTall = 100; // used for lists, textareas
	private static final int ssHeightVeryTall = 200; // used for images
	private static final int ssWidth = 240;

	/** component dimension default*/
	protected static final Dimension ssDim = new Dimension(ssWidth, ssHeight);
	/** component dimension for lists, text areas */
	protected static final Dimension ssDimTall = new Dimension(ssWidth, ssHeightTall);
	/** component dimension for images */
	protected static final Dimension ssDimVeryTall = new Dimension(ssWidth, ssHeightVeryTall);

	/** component dimension */
	protected static final int childScreenHeight = 400;
	/** component dimension */
	protected static final int childScreenHeightTall = 800;
	/** component dimension */
	protected static final int childScreenWidth = 600;
	/** component dimension */
	protected static final int childScreenCount = 10;

	private Connection dbConnection = null;

	/**
	 * buttons to launch code examples
	 */
	private JButton btnExample1 = new JButton("Example1");
	private JButton btnExample2 = new JButton("Example2");
	private JButton btnExample3 = new JButton("Example3");
	private JButton btnExample4 = new JButton("Example4");
	private JButton btnExample4Advanced = new JButton("Example4 Advanced");
	private JButton btnExample4UsingHelper = new JButton("Example4 Using Helper");
	private JButton btnExample5 = new JButton("Example5");
	private JButton btnExample6 = new JButton("Example6");
	private JButton btnExample7 = new JButton("Example7");
	private JButton btnExample7UsingHelper = new JButton("Example7 Using Helper");
	private JButton btnTestBase = new JButton("Test Base Components");
	private JButton btnTestFormatted = new JButton("Test Formatted Components");
	private JButton btnTestGrid = new JButton("Test Grid Components");
	private JButton btnRowSetSource = new JButton("RowSet: XXXXXXXX");
	private ComboRowSetSource comboRowSetSource = new ComboRowSetSource();
	private JButton btnLogMan = new JButton("Manage Logging");

	/** Logger */
	private static final Logger logger = SSUtils.getLogger();

	static DatabaseMetaData dbMeta;

	/**
	 * Constructor for MainClass
	 */
	@SuppressWarnings({"LeakingThisInConstructor", "OverridableMethodCallInConstructor"})
	public MainClass() {

		// SETUP WINDOW
		super("SwingSet Demo");
		setSize(300, 300);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				DemoUtil.logConnectionUsage();
			}
		});

		// ECHO WORKING DIRECTORY
		logger.log(INFO, () -> sf("Working Directory = %s", System.getProperty("user.dir")));

		// INITIALIZE DATABASE
		if ("h2".equals(dbname)) {
			dbConnection = getDatabase();
		} else {
			databaseSetup.run();
			dbConnection = databaseSetup.getConnection();
		}
		if (dbConnection == null) {
			logger.log(Level.ERROR, "Error initializing database. Exiting.");
			System.exit(1);
		}

		try {
			dbMeta = dbConnection.getMetaData();
			Objects.isNull(dbMeta);
		} catch (SQLException ex) {
		}
		
		CentralLookup.getDefault().add(new DefaultSSDBSupport(dbConnection) {
			@Override
			public RowSet getJdbcRowSet(RowSet rs) throws SQLException
			{
				return DemoUtil.getNewRowSet(getSharedConnection(rs), DemoUtil.RowSetSource.POOL_JDBC);
			}
		});

		// ADD ACTION LISTENERS FOR BUTTONS
		// TODO: can share listener OR add arg that can be switched on.
		btnExample1.addActionListener(new MyButtonListener());
		btnExample2.addActionListener(new MyButtonListener());
		btnExample3.addActionListener(new MyButtonListener());
		btnExample4.addActionListener(new MyButtonListener());
		btnExample4Advanced.addActionListener(new MyButtonListener());
		btnExample4UsingHelper.addActionListener(new MyButtonListener());
		btnExample5.addActionListener(new MyButtonListener());
		btnExample6.addActionListener(new MyButtonListener());
		btnExample7.addActionListener(new MyButtonListener());
		btnExample7UsingHelper.addActionListener(new MyButtonListener());
		btnTestBase.addActionListener(new MyButtonListener());
		btnTestGrid.addActionListener(new MyButtonListener());
		btnTestFormatted.addActionListener(new MyButtonListener());
		btnRowSetSource.addMouseListener(new RowSetSourceMouseListener());
		//btnLogMan.addActionListener(new LogManListener());

		// SET BUTTON DIMENSIONS
		btnExample1.setPreferredSize(buttonDim);
		btnExample2.setPreferredSize(buttonDim);
		btnExample3.setPreferredSize(buttonDim);
		btnExample4.setPreferredSize(buttonDim);
		btnExample4Advanced.setPreferredSize(buttonDim);
		btnExample4UsingHelper.setPreferredSize(buttonDim);
		btnExample5.setPreferredSize(buttonDim);
		btnExample6.setPreferredSize(buttonDim);
		btnExample7.setPreferredSize(buttonDim);
		btnExample7UsingHelper.setPreferredSize(buttonDim);
		btnTestBase.setPreferredSize(buttonDim);
		btnTestGrid.setPreferredSize(buttonDim);
		btnTestFormatted.setPreferredSize(buttonDim);
		btnRowSetSource.setPreferredSize(buttonDim);
		btnLogMan.setPreferredSize(buttonDim);

		// LAYOUT BUTTONS
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(btnExample1);
		getContentPane().add(btnExample2);
		getContentPane().add(btnExample3);
		getContentPane().add(btnExample4);
		getContentPane().add(btnExample4Advanced);
		getContentPane().add(btnExample4UsingHelper);
		getContentPane().add(btnExample5);
		getContentPane().add(btnExample6);
		getContentPane().add(btnExample7);
		getContentPane().add(btnExample7UsingHelper);
		getContentPane().add(btnTestBase);
		// getContentPane().add(this.btnTestGrid);
		getContentPane().add(btnTestFormatted);
		getContentPane().add(btnRowSetSource);
		if (DemoUtil.isUtilLogging()) {
			btnLogMan.setAction(DemoUtil.getLogManAction());
			getContentPane().add(btnLogMan);
		}

		// DISPLAY SCREEN
		setVisible(true);
		pack();
		Screens.translateToPrefScreen(this);
	}
	
	/**
	 * Class to initialize the database connection and load the database content
	 * from a script
	 *
	 * @return database connection
	 */
	protected Connection getDatabase() {

		Connection result = null;

		// INITIALIZE DATABASE CONNECTION AND COMPONENTS
		try {

			Class.forName("org.h2.Driver");

			logger.log(DEBUG, ()->sf("Resource path: %s.", getClass().getPackage().getName()));
			logger.log(DEBUG, "Resource path: %s.", getClass().getClassLoader().getResource(DATABASE_SCRIPT_DEMO));

			InputStream inStreamDemo = null;
			InputStream inStreamTest = null;
			InputStream inStreamDebug = null;

			boolean has_startup_error = false;

			if (RUN_DEMO_SQL_SCRIPTS) {
				inStreamDemo = getClass().getClassLoader().getResourceAsStream(DATABASE_SCRIPT_DEMO);
				if (inStreamDemo == null) {
					logger.log(Level.ERROR, () -> "Please add the file " + DATABASE_SCRIPT_DEMO
							+ " to the classpath, package " + getClass().getPackage().getName());
					has_startup_error = true;
				}
			}

			if (RUN_TEST_SQL_SCRIPTS) {
				inStreamTest = getClass().getClassLoader().getResourceAsStream(DATABASE_SCRIPT_TEST);
				if (inStreamTest == null) {
					logger.log(Level.ERROR, () -> "Please add the file "
							+ DATABASE_SCRIPT_TEST + " and " + DATABASE_SCRIPT_TEST_IMAGES
							+ " to the classpath, package "
							+ getClass().getPackage().getName());
					has_startup_error = true;
				}
			}

			if (RUN_DEBUG_SQL_SCRIPTS) {
				inStreamDebug = getClass().getClassLoader().getResourceAsStream(DATABASE_SCRIPT_DEBUG);
				if (inStreamDebug == null) {
					logger.log(Level.ERROR, () -> "Please add the file " + DATABASE_SCRIPT_DEBUG
							+ " to the classpath, package " + getClass().getPackage().getName());
					has_startup_error = true;
				}
			}

			if (has_startup_error) {
				logger.log(Level.ERROR, "\n*******\n*******\n*******");
				System.exit(1);
			}

			if (USE_IN_MEMORY_DATABASE) {
				result = DriverManager.getConnection("jdbc:h2:mem:" + DATABASE_NAME
						+ defLookup(H2Trace.class).getTraceUrlFlags());
				logger.log(INFO, "Established connection to in-memory database.");
			} else {
				// ASSUMING DATABASE IS IN LOCAL ./h2/databases/ FOLDER WITH DEFAULT USERNAME OF
				// sa AND BLANK PASSWORD
				// USEFUL FOR WORKING WITH DATASET FOR SWINGSET TESTS
				result = DriverManager.getConnection("jdbc:h2:tcp:" + DATABASE_PATH + DATABASE_NAME, "sa", "");
				logger.log(INFO, "Established connection to database server.");
			}

			// RUN SCRIPTS AND CLOSE STREAMS
			if (RUN_DEMO_SQL_SCRIPTS) {
				assert inStreamDemo != null;
				RunScript.execute(result, new InputStreamReader(inStreamDemo));
				inStreamDemo.close();
			}

			if (RUN_TEST_SQL_SCRIPTS) {
				assert inStreamTest != null;
				RunScript.execute(result, new InputStreamReader(inStreamTest));
				inStreamTest.close();

				if (!no_load_images) {
					String sql = "UPDATE swingset_base_test_data"
							+ " SET ss_image = ? WHERE swingset_base_test_pk = ?";
					DemoUtil.loadBinaries(result, "/swingset-demo-images.txt", sql, verbose);
				}
			}

			if (RUN_DEBUG_SQL_SCRIPTS) {
				assert inStreamDebug != null;
				RunScript.execute(result, new InputStreamReader(inStreamDebug));
				inStreamDebug.close();
			}

		} catch (final IOException ioe) {
			logger.log(Level.ERROR, "IO Exception.", ioe);
		} catch (final SQLException se) {
			logger.log(Level.ERROR, "SQL Exception.", se);
		} catch (final ClassNotFoundException cnfe) {
			logger.log(Level.ERROR, "Class Not Found Exception.", cnfe);
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
		 * Find resource; an alternate implementation could take the resource as a file.
		 * 
		 * @param resourceName name of resource
		 * @return BufferReader for specified resource
		 */
		@SuppressWarnings("UseOfSystemOutOrSystemErr")
		BufferedReader getBufferedReader(String resourceName) {
			InputStream stream = MainClass.class.getResourceAsStream(resourceName);
			if (stream == null) {
				System.err.println("Script '" + resourceName + "' not found. Exiting.");
				return null;
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			return br;
		}

		@Override
		@SuppressWarnings("UseOfSystemOutOrSystemErr")
		public void run() {
			Properties info = getDatabaseProperties();
			if (info == null) {
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
				String sql = "UPDATE swingset_base_test_data" + " SET ss_image = ? WHERE swingset_base_test_pk = ?";
				ok = DemoUtil.loadBinaries(conn, "/swingset-demo-images.txt", sql, verbose);
			}

			if (!ok) {
				conn = null;
			}
		}

		@SuppressWarnings("UseOfSystemOutOrSystemErr")
		Properties getFileProperties(String fname) {
			try {
				Properties props = new Properties();
				FileReader reader = new FileReader(fname);
				props.load(reader);
				return props;
			} catch (FileNotFoundException ex) {
				System.err.println("Property file '" + fname + "' not found");
			} catch (IOException ex) {
				logger.log(Level.ERROR, "IO exception.", ex);
			}
			return null;
		}

		void dump() {
			for (String script : getScripts()) {
				boolean ok = false;
				try (BufferedReader br = getBufferedReader(script)) {
					if (br != null) {
						String name = "dump." + new File(script).getName();
						try (FileWriter fout = new FileWriter(name)) {
							char buf[] = new char[4 * 1024];
							int n;
							while ((n = br.read(buf)) > 0) {
								fout.write(buf, 0, n);
							}
						}
					}
					ok = true;
				} catch (IOException ex) {
					logger.log(Level.ERROR, "IO exception.", ex);
				}
				if (!ok) {
					break;
				}
			}
		}
	}

	static class ExternalSetup extends DatabaseSetup {
		List<String> sqlFiles;

		public ExternalSetup(List<String> _sqlFiles) {
			sqlFiles = _sqlFiles;
		}

		@Override
		@SuppressWarnings("UseOfSystemOutOrSystemErr")
		BufferedReader getBufferedReader(String fileName) {
			try {
				return new BufferedReader(new FileReader(fileName));
			} catch (FileNotFoundException ex) {
				System.err.println("Script '" + fileName + "' not found. Exiting.");
				return null;
			}
		}

		@Override
		Properties getDatabaseProperties() {
			Properties info = getFileProperties(propertyFile);
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

		@Override
		public void run() {
		}

		@Override
		List<String> getScripts() {
			return Arrays.asList(new String[] { "/" + DATABASE_SCRIPT_DEMO, "/" + DATABASE_SCRIPT_TEST,
					"/" + DATABASE_SCRIPT_TEST_IMAGES });
		}
	}

	static class MysqlSetup extends DatabaseSetup {

		@Override
		public void run() {
			super.run();
			globalHints.put("collectionModel",
					(Supplier<SSCollectionModel>) () -> new SSMysqlSetModel(JDBCType.INTEGER));
			// () -> new SSStringArrayModel(JDBCType.INTEGER));
		}

		@Override
		Properties getDatabaseProperties() {
			// <dependency>
			// <groupId>mysql</groupId>
			// <artifactId>mysql-connector-java</artifactId>
			// <version>8.0.21</version>
			// </dependency>
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
			return Arrays
					.asList(new String[] { "/mysql.swingset-demo-app.sql", "/mysql.swingset-demo-components.sql" });
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
	// private static boolean readme = false;
	private static String dbname = DBMS_H2;
	private static String propertyFile = null;
	private static List<String> userSqlFiles = new ArrayList<>();
	private static DatabaseSetup databaseSetup = null;

	private static String cmdName = "SwingSetDemo";

	@SuppressWarnings({"ResultOfMethodCallIgnored", "UseOfSystemOutOrSystemErr"})
	private static void usage() {
		// TODO: specify don't load images
		String usage = """
				
				Run the SwingSet demo. With no options/args use the self contained
				in memory database.
				
				CMD_NAME [-h] [-v] [-d] [-n] [-i] [-r] [-p fname] [-s sql]* [dbms-server]
				
				    -h             help
				    -v             verbose; output initialization sql as executed
				    -d             dump/create sql scripts in local directory, exit
				    -n             do NOT initialize database, just run demo
				    -i             do NOT load images
				    -p fname       properties file for jdbc database connection
				                   'DB_URL', 'DB_DRIVER_CLASS' keys required
				    -s sqlScript   sql file to initialize database, multiple OK
				
				If specified, dbms-server in {mysql}
				Internal mysql properties use database swingset_demo_suppliers_and_parts.
				After the sql files are run, the images are loaded, unless '-i'.
				Use '-n -p props' to run demo with a previously initialized database.
				Use '-d' or '-d mysql' to create local files with sql initialization.
				See swingset-demo/README.txt for more information.
				
				Examples: (ss.jar like swingset-demo-vers-jar-with-dependencies.jar)
				    java -jar ss.jar -d   # dump sql that creates in memory database
				    java -jar ss.jar -d mysql   # dump sql to create mysql database
				    java -cp jdbc_driver:ss.jar com.nqadmin.swingset.demo.MainClass \\
				        -p db_props -s initializer.sql
				
				""";
		usage = usage.replace("CMD_NAME", cmdName);
		System.err.println(usage);
		System.exit(1);
	}

	/**
	 * Main method for SwingSet samples/demo
	 * <p>
	 * 
	 * @param _args - optional command line arguments, which are ignored by this
	 *              program
	 */
	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void main(final String[] _args)
	{
		configureJavaUtilLogger();
		Screens.setPrefGraphicsDev("SWINGSET_PREFERRED_SCREEN");

		CentralLookup lkup = CentralLookup.getDefault();
		lkup.add(new LoadDemoImages());
		lkup.add(new H2Trace());
		//lkup.replace(H2Trace.class, new H2Trace(";TRACE_LEVEL_SYSTEM_OUT=3"));
		//SELECT VALUE FROM INFORMATION_SCHEMA.SETTINGS WHERE NAME = 'info.VERSION';
		//lkup.add(new H2Workaround()); // fixed in H2 Version 2.3.230 (2024-07-15
		//lkup.add(new ForceConflict(0));
		//lkup.add(new SSUtils.DebugRowSetListenerFlag());

		boolean some_error = false;
		System.err.printf("java:%s vm:%s date:%s os:%s\n",
				System.getProperty("java.version"),
				System.getProperty("java.vm.version"),
				System.getProperty("java.version.date"),
				System.getProperty("os.name"));
		System.err.printf("SwingSet: %s\n", SSVersion.get().toString());

		try {
			for (SyncProvider sp : Collections.list(SyncFactory.getRegisteredProviders())) {
				Class<?> c = sp.getClass();
				System.err.printf("SyncProvider: %s\n", c.getName());
				for (Class<?> iface : c.getInterfaces()) {
					System.err.println("    " + iface.getName());
				}
			}
		} catch (SyncFactoryException ex) {
			logger.log(DEBUG, "SyncFactory.getRegisteredProviders()", ex);
		}

		Getopt g = new Getopt(cmdName, _args, "hvdinrp:s:");

		int c;
		while ((c = g.getopt()) != -1) {
			switch (c) {
			case 'v':
				verbose = true;
				break;
			case 'd':
				dump = true;
				break;
			case 'n':
				no_initialize_db = true;
				break;
			case 'i':
				no_load_images = true;
				break;
			// case 'r': readme = true; break;
			case 'p':
				propertyFile = g.getOptarg();
				break;
			case 's':
				userSqlFiles.add(g.getOptarg());
				break;
			case '?':
			case 'h':
			default: // TODO: not sure if default is OK here meaning all other characters indicate an
						// error??
				some_error = true;
				break; // getopt() already printed an error
			}
		}

		if (verbose) {
			StringBuilder sb = new StringBuilder();
			sb.append('\n').append(cmdName);
			for (String arg : _args) {
				sb.append(' ').append(arg);
			}
			System.err.println(sb.toString());
		}

		if (some_error) {
			usage();
		}

		// if (readme) {
		// return;
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

		databaseSetup = switch (dbname) {
			case DBMS_MYSQL -> new MysqlSetup();
			case DBMS_EXTERNAL -> new ExternalSetup(userSqlFiles);
			case DBMS_H2 -> new H2Setup();
			default -> null;
		};
		if (databaseSetup == null) {
			System.err.println("Unknown database server '" + dbname + "' . Exiting.");
			some_error = true;
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
