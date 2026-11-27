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

import javax.swing.JPanel;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.RowSet;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.google.common.reflect.TypeToken;
import com.nqadmin.swingset.SSDBComboBox;
import com.nqadmin.swingset.SSDataNavigator;
import com.nqadmin.swingset.SSTextField;
import com.nqadmin.swingset.utils.SSSyncManager;

import com.nqadmin.swingset.SSComboBox2;
import com.nqadmin.swingset.SSDBComboBox2;
import dev.visdb.seesaw.datasources.DbOps;
import dev.visdb.seesaw.datasources.products.DbOpsBase;
import dev.visdb.seesaw.navigate.RowsModel;
import dev.visdb.seesaw.utils.JStuff;

import com.nqadmin.swingset.utils.SSUtils;

/**
 * This example displays data from the part_data table.
 * SSTextFields are used to display part id, name, weight,
 * and city. SSComboBox is used to display color.
 * <p>
 * Record navigation can be handled with a SSDataNavigator or
 * with a SSDBComboBox.
 * <p>
 * Since the navigation can take place by multiple methods, the navigation
 * controls have to be synchronized. This is accomplished with the
 * SSSyncManager.
 * <p>
 * IMPORTANT: If {@code DbSupport.createRownumQuery()} is not available,
 * the SSDBComboBox and the SSRowSet queries should select the same
 * records and in the same order. Otherwise the SSSyncManager will spend a lot of
 * time looping through records to match.
 */
@SuppressWarnings("serial")
public class Example4 extends JFrame {
  /** Logger */
  static final Logger logger = JStuff.getLogger();

  static SSComboBox2.ModelType comboModelType = SSComboBox2.ModelType.GLAZED;

  /**
   * screen label declarations
   */
  JLabel lblSelectPart = new JLabel("Part");
  JLabel lblPartID = new JLabel("Part ID");
  JLabel lblPartName = new JLabel("Part Name");
  JLabel lblPartColor = new JLabel("Color");
  JLabel lblPartWeight = new JLabel("Weight");
  JLabel lblPartCity = new JLabel("City");

  static class MyComboBox2<D2> extends SSComboBox2<Integer, String, D2> {
    public static class Builder<D2>
        extends SSComboBox2.AbstractBuilder<Integer, String, D2, Builder<D2>> {
      @Override
      protected Builder<D2> self() {
        return this;
      }
      @Override
      public MyComboBox2<D2> build() {
        return new MyComboBox2<>(this);
      }
    }
    private MyComboBox2(Builder<D2> builder) {
      super(builder);
    }
  }

  static class MyDbComboBox extends SSDBComboBox2<Integer, String, Byte> {
    public static class Builder
        extends SSDBComboBox2.AbstractBuilder<Integer, String, Byte, Builder> {
      @Override
      protected Builder self() {
        return this;
      }
      @Override
      public MyDbComboBox build() {
        return new MyDbComboBox(this);
      }
    }
    private MyDbComboBox(Builder builder) {
      super(builder);
    }
  }

  // Concrete class, additional generric type, with type capture, extendable

  static class DbComboBox2Extra<D2, D3> extends SSDBComboBox2<Integer, String, D2> {
    private final D3 d3Value; // Note: not part of combo list item.
    private final TypeToken<D3> d3TypeToken;

    public abstract static class AbstractBuilder<D2, D3, T extends AbstractBuilder<D2, D3, T>>
        extends SSDBComboBox2.AbstractBuilder<Integer, String, D2, T> {
      private D3 d3Value;
      // captures D3 for whatever runtime class extends this Abstractbuilder
      private final TypeToken<D3> d3TypeToken = new TypeToken<D3>(getClass()) {};

      public AbstractBuilder() {
        // check if TypeToken is generic; verifyTypeClass throws useful msg
        verifyTypeClass(d3TypeToken, getClass());
      }

      public T d3Data(D3 val) {
        d3Value = val;
        return self();
      }
    }

    // Regular Builder for direct instantiation
    public static class Builder<D2, D3> extends AbstractBuilder<D2, D3, Builder<D2, D3>> {
      // self type idiom
      @Override
      protected Builder<D2, D3> self() {
        return this;
      }

      @Override
      public DbComboBox2Extra<D2, D3> build() {
        return new DbComboBox2Extra<>(this);
      }
    }

    protected DbComboBox2Extra(AbstractBuilder<D2, D3, ?> builder) {
      super(builder);
      d3Value = builder.d3Value;
      d3TypeToken = builder.d3TypeToken;
    }

    public D3 getD3() {
      return d3Value;
    }
    public TypeToken<D3> getD3TypeToken() {
      return d3TypeToken;
    }
  }

  private void comboPlay() {
    if (Boolean.TRUE)
      return;
    new DbComboBox2Extra.Builder<Double, List<Double>>() {};

    @SuppressWarnings("unused")
    SSDBComboBox2.Builder<Integer, String, Byte> b
        = new SSDBComboBox2.Builder<Integer, String, Byte>() {};

    @SuppressWarnings("unused")
    SSDBComboBox2<Integer, String, Byte> x1 = new SSDBComboBox2.Builder<Integer, String, Byte>() {
    }.primaryKeyColumnName("aaa").displayColumnName("bbb").build();

    MyDbComboBox dbCombo = new MyDbComboBox
                               .Builder() // don't need {}
                               .primaryKeyColumnName("x")
                               .displayColumnName("y")
                               .build();
    System.out.println("" + dbCombo.getD2Type());

    DbComboBox2Extra<Double, List<Double>> cbExtra = new DbComboBox2Extra
                                                         .Builder<Double, List<Double>>() {}
                                                         // <- "{ }" NEEDED
                                                         .primaryKeyColumnName("aaa")
                                                         .displayColumnName("bbb")
                                                         .d3Data(new ArrayList<>())
                                                         .build();
    cbExtra.getD3().addAll(List.of(7D, 6D, 5D));
    System.out.println("" + cbExtra.getKeyType());
    System.out.println("" + cbExtra.getDisplayValueType());
    System.out.println("" + cbExtra.getD2Type());
    System.out.println("" + cbExtra.getD3TypeToken());
    System.out.println("" + cbExtra.getD3());
  }

  /**
   * bound component declarations
   */
  SSTextField txtPartID = new SSTextField();
  SSTextField txtPartName = new SSTextField();
  MyComboBox2<Byte> cmbPartColor
      = new MyComboBox2.Builder<Byte>() {}.modelType(comboModelType).build();
  SSTextField txtPartWeight = new SSTextField();
  SSTextField txtPartCity = new SSTextField();

  /**
   * database component declarations
   */
  Connection connection = null;
  SSDataNavigator navigator = null;
  RowsModel rowsModel;

  /** combo navigator and sync manger */
  SSDBComboBox cmbSelectPart = null;
  SSSyncManager syncManager;

  /**
   * Constructor for Example4
   * <p>
   * @param _dbConn - database connection
   */
  @SuppressWarnings({"LeakingThisInConstructor", "OverridableMethodCallInConstructor"})
  public Example4(Connection _dbConn) {
    // SET SCREEN TITLE
    super("Example4");
    DemoUtil.initExampleFrame(this, null);

    comboPlay();

    // SET SCREEN DIMENSIONS
    setSize(MainClass.childScreenWidth, MainClass.childScreenHeight);

    // SET SCREEN POSITION
    setLocation(DemoUtil.getChildScreenLocation(this.getName()));

    // SET CONNECTION
    connection = _dbConn;

    // INITIALIZE DATABASE CONNECTION AND COMPONENTS
    try {
      RowSet rowset = DemoUtil.getNewRowSet(connection);
      rowset.setCommand("SELECT * FROM part_data;");
      rowset.execute();
      rowsModel = RowsModel.create(rowset, createDbNav());
      navigator = new SSDataNavigator(rowsModel);
    } catch (final SQLException se) {
      logger.log(Level.ERROR, "SQL Exception.", se);
    }

    // DEBUG
    //rowsModel.setAllowWrite(false);

    // Setup navigator query.
    // Use the "order by" to exercise SSSyncManager's "perform a manual loop"
    @
    SuppressWarnings("unused") String simpleQuery = "SELECT * FROM part_data;";
    String orderedQuery = "SELECT * FROM part_data order by part_name;";
    String query
        = SSUtils.dbSupport().createRownumQuery("*", "rown", "part_data", "ORDER BY part_name");

    boolean comboHasRowNum = false;

    if (query != null) {
      comboHasRowNum = true;
    } else {
      query = orderedQuery;
    }

    SSDBComboBox.Builder builder = new SSDBComboBox.Builder();
    builder.connection(connection)
        .query(query)
        .primaryKeyColumnName("part_id")
        .displayColumnName("part_name");
    if (!comboModelType.equals(DbComboBox2Extra.DEFAULT_MODEL_DB_COMBO2))
      builder.modelType(comboModelType);
    if (comboHasRowNum)
      builder.d2ColumnName("rown");

    // builder.d2DisplayEnabled(true);

    cmbSelectPart = builder.build();

    try {
      cmbSelectPart.execute();
    } catch (final SQLException se) {
      logger.log(Level.ERROR, "SQL Exception.", se);
    } catch (final Exception e) {
      logger.log(Level.ERROR, "Exception.", e);
    }

    // Setup the part color combo box options to be displayed and their
    // corresponding values.

    if (Boolean.TRUE) {
      // This is the normal case, specify an option for each mapping
      cmbPartColor.setDisplayValues(List.of("Red", "Green", "Blue"), null);
    } else {
      // For testing, indlude <D2> values and display them.
      cmbPartColor.setD2DisplayEnabled(true);
      cmbPartColor.setDisplayValues(List.of("Red", "Green", "Blue"), null,
                                    List.of((byte) 3, (byte) 5, (byte) 7));
    }

    // This is used to initialize some stuff for Example4Advanced
    cmbPartColorChangeOptions();

    // BIND THE COMPONENTS TO THE DATABASE COLUMNS
    rowsModel.bind(txtPartID, "part_id");
    rowsModel.bind(txtPartName, "part_name");
    rowsModel.bind(cmbPartColor, "color_code");
    rowsModel.bind(txtPartWeight, "weight");
    rowsModel.bind(txtPartCity, "city");

    // SETUP SYNCMANAGER, WHICH WILL TAKE CARE OF KEEPING THE COMBO NAVIGATOR AND
    // DATA NAVIGATOR IN SYNC.
    //
    // BEFORE CHANGING THE QUERY OR RE-EXECUTING THE QUERY FOR THE COMBO BOX,
    // YOU HAVE TO CALL THE .async() METHOD
    //
    // AFTER CALLING .execute() ON THE COMBO NAVIGATOR, CALL THE .sync() METHOD
    syncManager = new SSSyncManager(cmbSelectPart, rowsModel);
    syncManager.setSyncColumnName("part_id");
    syncManager.setComboHasRowNum(comboHasRowNum);
    syncManager.sync();

    // SET LABEL DIMENSIONS
    lblSelectPart.setPreferredSize(MainClass.labelDim);
    lblPartID.setPreferredSize(MainClass.labelDim);
    lblPartName.setPreferredSize(MainClass.labelDim);
    lblPartColor.setPreferredSize(MainClass.labelDim);
    lblPartWeight.setPreferredSize(MainClass.labelDim);
    lblPartCity.setPreferredSize(MainClass.labelDim);

    // SET BOUND COMPONENT DIMENSIONS
    cmbSelectPart.setPreferredSize(MainClass.ssDim);
    txtPartID.setPreferredSize(MainClass.ssDim);
    txtPartName.setPreferredSize(MainClass.ssDim);
    cmbPartColor.setPreferredSize(MainClass.ssDim);
    txtPartWeight.setPreferredSize(MainClass.ssDim);
    txtPartCity.setPreferredSize(MainClass.ssDim);

    // SETUP THE CONTAINER AND LAYOUT THE COMPONENTS
    final Container contentPane = new JPanel();
    final GridBagConstraints constraints = new GridBagConstraints();
    contentPane.setLayout(new GridBagLayout());

    constraints.gridx = 0;
    constraints.gridy = 0;
    contentPane.add(lblSelectPart, constraints);
    constraints.gridy = 1;
    contentPane.add(lblPartID, constraints);
    constraints.gridy = 2;
    contentPane.add(lblPartName, constraints);
    constraints.gridy = 3;
    contentPane.add(lblPartColor, constraints);
    constraints.gridy = 4;
    contentPane.add(lblPartWeight, constraints);
    constraints.gridy = 5;
    contentPane.add(lblPartCity, constraints);

    constraints.gridx = 1;
    constraints.gridy = 0;
    contentPane.add(cmbSelectPart, constraints);
    constraints.gridy = 1;
    contentPane.add(txtPartID, constraints);
    constraints.gridy = 2;
    contentPane.add(txtPartName, constraints);
    constraints.gridy = 3;
    contentPane.add(cmbPartColor, constraints);
    constraints.gridy = 4;
    contentPane.add(txtPartWeight, constraints);
    constraints.gridy = 5;
    contentPane.add(txtPartCity, constraints);

    constraints.gridx = 0;
    constraints.gridy = 6;
    constraints.gridwidth = 2;
    contentPane.add(navigator, constraints);

    constraints.gridy = 7;
    constraints.gridwidth = 1;

    // DISABLE THE PRIMARY KEY
    txtPartID.setEnabled(false);

    // MAKE THE JFRAME VISIBLE
    setContentPane(SSUtils.createDecoratorPanel(contentPane));
    pack();
    setVisible(true);
  }

  private DbOps createDbNav() {
    return new DbOpsBase(this) {
      /**
       * Re-enable DB Navigator following insertion Cancel
       */
      @Override
      public void performCancelOps() {
        super.performCancelOps();
        cmbSelectPart.setEnabled(true);
      }

      /**
       * Requery the rowset following a deletion. This is needed for H2.
       */
      @Override
      public void performPostDeletionOps(RowsModel rm) throws SQLException {
        super.performPostDeletionOps(rm);
        performRefreshOps();
      }

      /**
       * Re-query the rowset following an insertion. This is needed for H2.
       */
      @Override
      public void performPostInsertOps(RowsModel rm) throws SQLException {
        super.performPostInsertOps(rm);
        cmbSelectPart.setEnabled(true);
        performRefreshOps();
      }

      /**
       * Obtain and set the PK value for the new record & perform any
       * other actions needed before an insert.
       */
      @Override
      public void performPreInsertOps() {
        // SSDBNavImpl will clear the component values
        super.performPreInsertOps();

        try (ResultSet rs = connection.createStatement().executeQuery(
                 "SELECT nextval('part_data_seq') as nextVal;");) {
          // GET THE NEW RECORD ID.
          rs.next();
          final int partID = rs.getInt("nextVal");
          txtPartID.setText(String.valueOf(partID));
        } catch (final SQLException se) {
          logger.log(Level.ERROR, "SQL Exception occured initializing new record.", se);
        } catch (final Exception e) {
          logger.log(Level.ERROR, "Exception occured initializing new record.", e);
        }
        // DISABLE PART SELECTOR
        cmbSelectPart.setEnabled(false);

        // SET OTHER DEFAULTS
        //						txtPartName.setText(null);
        //						cmbPartColor.setSelectedValue(0);
        //						txtPartWeight.setText("0");
        //						txtPartCity.setText(null);
      }

      /**
       * Manage sync manager during a Refresh
       */
      @Override
      public void performRefreshOps() {
        super.performRefreshOps();
        syncManager.async();
        try {
          cmbSelectPart.execute();
        } catch (final SQLException se) {
          logger.log(Level.ERROR, "SQL Exception.", se);
        } catch (final Exception e) {
          logger.log(Level.ERROR, "Exception.", e);
        }
        syncManager.sync();
      }
    };
  }

  void cmbPartColorChangeOptions() {}
}
