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
 ******************************************************************************/

package com.nqadmin.swingset.demo;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.h2.tools.RunScript;

/**
 * A JFrame with buttons to launch each of the SwingSet example/demo screens.
 */
public class MainClass extends JFrame {

	/**
     * ActionListener implementation to call code for each button.
     */
    private class MyButtonListener implements ActionListener{

		public MyButtonListener() {
			super();
		}

		@Override
		public void actionPerformed( final ActionEvent ae){
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
            	new TestBaseComponents(dbConnection);
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
    		dbConnection = getDatabase();
    		if (dbConnection == null) {
				logger.fatal("Error initializing database. Exiting.");
				System.exit(0);
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

		            if (USE_IN_MEMORY_DATABASE) {
		            	RunScript.execute(result, new InputStreamReader(inStreamTestImages));
		            	inStreamTestImages.close();
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
    
    /**
     * Main method for SwingSet samples/demo
     * <p>
     * @param _args - optional command line arguments, which are ignored by this program
     */
	public static void main(final String[] _args){

		// create application screen
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					new MainClass();
				}
			});
    }
} // end public class MainClass extends JFrame {