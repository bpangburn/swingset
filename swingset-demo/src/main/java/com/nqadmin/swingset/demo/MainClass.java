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
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.sql.rowset.spi.SyncFactory;
import javax.sql.rowset.spi.SyncFactoryException;
import javax.sql.rowset.spi.SyncProvider;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;


import com.raelity.lib.ui.Screens;

import dev.visdb.seesaw.datasources.DbSupport;
import dev.visdb.seesaw.datasources.RowSetOps.ForceConflict;
import dev.visdb.seesaw.datasources.products.DbSupportBase;
import dev.visdb.seesaw.datasources.products.DbSupportFactory;
import dev.visdb.seesaw.navigate.Utils;

import com.nqadmin.swingset.utils.SSUtils;

import dev.visdb.seesaw.utils.Version;

import static com.nqadmin.swingset.demo.DemoUtil.configureJavaUtilLogger;
import static java.lang.System.Logger.Level.*;

/**
 * A JFrame with buttons to launch each of the SwingSet example/demo screens.
 */
@SuppressWarnings("serial")
public class MainClass extends JFrame {
  /**
   * database
   */
  public static final String DATABASE_NAME = "suppliers_and_parts";
  private static final Map<String, Object> globalHints = new HashMap<>();

  static {
    if (Boolean.FALSE) {
      // Get rid of unused warnings
      Objects.nonNull(new SSUtils.DebugRowSetListenerFlag());
      Objects.nonNull(new ForceConflict(0));
      Objects.nonNull(new H2Trace(""));
      Objects.nonNull(new MainClass().new LogManListener());
    }
  }

  static class LoadDemoImages {}
  // https://h2database.com/html/features.html#trace_options
  // SSUtils.setOption(H2Trace.class, new H2Trace(";TRACE_LEVEL_SYSTEM_OUT=3"));
  static class H2Trace {
    private final String flags;
    H2Trace() {
      this.flags = "";
    }
    H2Trace(String flags) {
      this.flags = flags;
    }
    String getTraceUrlFlags() {
      return flags;
    }
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
    public void actionPerformed(ActionEvent ae) {
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
        Example4UsingHelper example4 = new Example4UsingHelper(dbConnection, e4JFrame);
        e4JFrame.setContentPane(SSUtils.createDecoratorPanel(example4));
        example4.showUp(e4JFrame.getContentPane());
        e4JFrame.pack();
        // screen dimensions handled by SSScreenHelperCommon.setScreenSize()
        DemoUtil.initExampleFrame(e4JFrame, null);
        DemoUtil.setChildScreenLocation(e4JFrame);
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
        e7JFrame.setContentPane(SSUtils.createDecoratorPanel(example7));
        example7.showUp(e7JFrame.getContentPane());
        //e7JFrame.pack(); // NOT USING .pack() FOR DATA GRID SCREENS
        e7JFrame.setSize(MainClass.childScreenWidth, MainClass.childScreenHeight);
        DemoUtil.initExampleFrame(e7JFrame, null);
        DemoUtil.setChildScreenLocation(e7JFrame);
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

  // Use a plain combobox to avoid using SS in main demo menu window
  //private class ComboRowSetSource extends SSComboBox {
  @SuppressWarnings("serial")
  private final class ComboRowSetSource extends JComboBox<DemoUtil.RowSetSource> {
    Popup popup;

    public ComboRowSetSource() {
      //super(ModelType.SWING);
      super(DemoUtil.RowSetSource.values());
      setMaximumSize(new Dimension(210, 25));
      //setAllowNull(false);
      DemoUtil.RowSetSource rsSource = DemoUtil.getWhichRowSetDefault();
      //setDisplayValues(DemoUtil.RowSetSource.class);
      //setChosenEnum(rsSource);
      setSelectedItem(rsSource);
      btnRowSetSource.setText("RowSet: " + rsSource.toString());
    }

    //@Override
    public DemoUtil.RowSetSource getChosenEnum() {
      // return (DemoUtil.RowSetSource) super.getChosenEnum();
      return (DemoUtil.RowSetSource) getSelectedItem();
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
      Popup popup = PopupFactory.getSharedInstance().getPopup(null, comboRowSetSource, p.x, p.y);
      comboRowSetSource.popup = popup;
      popup.show();
      SwingUtilities.invokeLater(() -> comboRowSetSource.showPopup());
    }
  }

  private class LogManListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {}
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
  protected static final Dimension labelDimVeryTall
      = new Dimension(labelWidth, labelHeightVeryTall);

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
  private static final Logger logger = System.getLogger(MainClass.class.getName());

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
    logger.log(INFO, () -> String.format("Working Directory = %s", System.getProperty("user.dir")));

    // INITIALIZE DATABASE
    dbConnection = getDatabase();
    if (dbConnection == null) {
      logger.log(Level.ERROR, "Error initializing database. Exiting.");
      System.exit(1);
    }

    String databaseID = "";
    try {
      dbMeta = dbConnection.getMetaData();
      databaseID = dbMeta.getDatabaseProductName() + " " + dbMeta.getDatabaseProductVersion();
      System.err.printf(
          "Database product '%s', version '%s', MajMin '%s'\n", dbMeta.getDatabaseProductName(),
          dbMeta.getDatabaseProductVersion(),
          "" + dbMeta.getDatabaseMajorVersion() + ":" + dbMeta.getDatabaseMinorVersion());
    } catch (SQLException ex) {
    }

    DbSupport supp = DbSupportFactory.setDbSupportGlobalOption(dbConnection);
    if (supp == null) {
      logger.log(Level.ERROR, String.format("No SSDBSupport found for '%s'", databaseID));
      SSUtils.setOption(DbSupport.class, new DbSupportBase(dbConnection) {});
    }

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

  /** Initialize the selected database and return its connection. */
  protected Connection getDatabase() {
    if (databaseSetup == null) {
      databaseSetup = new DemoDatabase.H2Setup(
          DemoOptions.defaults());
    }
    databaseSetup.run();
    return databaseSetup.getConnection();
  }

  private static DemoDatabase.DatabaseSetup databaseSetup;

  /**
   * Main method for SwingSet samples/demo
   * <p>
   *
   * @param _args optional command-line arguments
   */
  @SuppressWarnings("UseOfSystemOutOrSystemErr")
  public static void main(String[] _args) {
    configureJavaUtilLogger();
    Screens.setPrefScreen("SWINGSET_PREFERRED_SCREEN");

    SSUtils.setOption(LoadDemoImages.class, new LoadDemoImages());
    SSUtils.setOption(H2Trace.class, new H2Trace());

    // To allow force baseValidate() and componentValidate() erros.
    // SSUtils.enableDebugBaseComponentValidation();

    // SSUtils.setSimpleValidationBorderEnabled(false);
    // SSUtils.setDecoratorStyle(SSUtils.DecoratorStyle.SIMPLE_VALIDATION);
    // SSUtils.setDecoratorStyle(SSUtils.DecoratorStyle.BACKGROUND);

    //SSUtils.setOption(H2Trace.class, new H2Trace(";TRACE_LEVEL_SYSTEM_OUT=3"));
    //SELECT VALUE FROM INFORMATION_SCHEMA.SETTINGS WHERE NAME = 'info.VERSION';
    //SSUtils.setOption(new H2Workaround()); // fixed in H2 Version 2.3.230 (2024-07-15
    //SSUtils.setOption(new SsUtils.DebugRowSetListenerFlag());

    // For debug, enable this to force CachedRowSet conflict
    //SSUtils.setOption(new ForceConflict(1));

    SSUtils.init();

    System.err.printf("java:%s vm:%s date:%s os:%s\n", System.getProperty("java.version"),
                      System.getProperty("java.vm.version"),
                      System.getProperty("java.version.date"), System.getProperty("os.name"));
    System.err.printf("SwingSet: %s\n", Version.get().toString());

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

    DemoOptions options;
    try {
      options = DemoOptions.parse(_args);
    } catch (IllegalArgumentException ex) {
      System.err.println(ex.getMessage());
      DemoOptions.usage();
      System.exit(1);
      return;
    }
    if (options == null) {
      DemoOptions.usage();
      return;
    }

    databaseSetup = switch (options.database()) {
      case "mysql" -> new DemoDatabase.MysqlSetup(options, globalHints);
      case "external" -> new DemoDatabase.ExternalSetup(options, options.userSqlFiles());
      default -> new DemoDatabase.H2Setup(options);
    };
    if (options.dump()) {
      databaseSetup.dump();
      return;
    }

    // create application screen
    SwingUtilities.invokeLater(() -> new MainClass());
  }
} // end public class MainClass extends JFrame {
