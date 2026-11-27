/* *****************************************************************************
 * Copyright (C) 2025-2026, Ernie R Rael. All rights reserved.
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
 * ****************************************************************************/
package com.nqadmin.swingset.demo;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;

import javax.sql.RowSet;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.nqadmin.swingset.demo.Example1.DbOpsAllows;

import dev.visdb.seesaw.navigate.RowSetState;
import dev.visdb.seesaw.navigate.RowsModel;


/**
 * x
 */
@SuppressWarnings("serial")
public abstract class RowSetButtons extends JPanel {
  private static final Logger logger = System.getLogger(RowSetButtons.class.getName());

  record AppInfo(Logger logger, RowsModel rowsModel, DbOpsAllows dbOps) {}
  abstract AppInfo getAppInfo();

  /** Override for notification of "next" button press. */
  void nextRowSetButtonPush() {
    AppInfo ai = getAppInfo();
    setNextDebugRowSet(ai.logger, ai.rowsModel);
  }

  /** Override for notification of "null" button press. */
  void nullRowSet() {
    AppInfo ai = getAppInfo();
    ai.logger.log(Level.INFO, "nullRowSet");
    ai.rowsModel.setRowSet(null);
  }

  /**
   * Assign the next RowSet to the rowsModel.
   * Set the cursor to the table name index for newly created RowSets.
   * Typically used from nextRowSetButtonPush().
   */
  void setNextDebugRowSet(Logger l, RowsModel rowsModel) {
    l.log(Level.INFO, "nextRowSetButtonPush");
    try {
      RowSet rs = getTableLoopRowSet();
      boolean newRowSet = !DemoExtraDB.isExecuted(rs);
      if (newRowSet) {
        rs.execute();
        rs.absolute(DemoExtraDB.findIdxTbl(rs));
      }
      rowsModel.setRowSet(rs); // SetRowSet should leave the row

      // DON'T LIKE INVOKELATER NEEDED.
      // if (newRowSet)
      // 	rowsModel.setRow(DemoExtraDB.findIdxTbl(rs)); //invokeLater...
      // else
      // 	DemoExtraDB.check();
      DemoExtraDB.check();
    } catch (SQLException | ClassNotFoundException ex) {
      l.log(Level.ERROR, (String) null, ex);
    }
  }

  // Default table cycle: 2, 3, 4, 2, 3, 4, ...
  private int tableLoopBase = 2;
  private int tableLoopCount = 3;
  private int tableLoopRowCountBase = 5;
  /** tableLoopIndex is added to base to form table ID. */
  private int tableLoopIndex = tableLoopCount - 1;

  /**
   * x
   */
  RowSetButtons() {
    init();
    create();
  }

  private void outStuff() {
    logger.log(Level.INFO,
               ()
                   -> String.format("Active: RSState %d, NavState %d, RModel %d", RowSetState.count(),
                         RowsModel.navCount(), RowsModel.count()));
  }

  private void init() {
    JButton button;
    String text;

    allowText = text = "<html><center>allow<br>{what}</center></html>";
    allowButton = button = new JButton(text);
    add(button);
    changeAllow();
    button.addActionListener((e) -> {
      changeAllow();
      outStuff();
    });

    text = "<html><center>next<br>RS</center></html>";
    button = new JButton(text);
    add(button);
    button.addActionListener((e) -> {
      tableLoopIncr();
      nextRowSetButtonPush();
      outStuff();
    });

    text = "<html><center>null<br>RS</center></html>";
    button = new JButton(text);
    add(button);
    button.addActionListener((e) -> {
      nullRowSet();
      outStuff();
    });

    // TODO: could toggle weak/strong
    text = "<html><center>dref<br>RS</center></html>";
    button = new JButton(text);
    add(button);
    button.addActionListener((e) -> {
      DemoExtraDB.derefSupplierData(null);
      outStuff();
    });

    text = "<html><center>clean<br>DB</center></html>";
    button = new JButton(text);
    add(button);
    button.addActionListener((e) -> {
      create();
      outStuff();
    });

    text = "<html><center>garb<br>coll</center></html>";
    button = new JButton(text);
    add(button);
    button.addActionListener((e) -> {
      System.gc();
      outStuff();
    });
  }

  private void create() {
    try {
      H2Demo.clean();
      for (int i = 0; i < tableLoopCount; i++) {
        tableLoopIncr();
        createTableLoopRowSet();
      }
      this.tableLoopIndex = tableLoopCount - 1; // next will be first of sequence
    } catch (SQLException | ClassNotFoundException ex) {
      logger.log(Level.ERROR, (String) null, ex);
    }
  }

  void tableLoopIncr() {
    tableLoopIndex = ++tableLoopIndex % tableLoopCount;
  }

  /**
   * Create the RowSet for the current table loop iteration.
   */
  private void createTableLoopRowSet() throws SQLException, ClassNotFoundException {
    DemoExtraDB.createSimpleSupplierData(tableLoopBase + tableLoopIndex,
                                         tableLoopRowCountBase + tableLoopIndex);
    logger.log(Level.INFO,
               ()
                   -> String.format("Creating tbl%d, nRows %d", tableLoopBase + tableLoopIndex,
                         tableLoopRowCountBase + tableLoopIndex));
  }

  /**
   * Return the RowSet for the current table loop iteration.
   */
  RowSet getTableLoopRowSet() throws SQLException, ClassNotFoundException {
    RowSet rs = DemoExtraDB.findSimpleSupplierData(tableLoopBase + tableLoopIndex,
                                                   tableLoopRowCountBase + tableLoopIndex);
    logger.log(Level.INFO,
               ()
                   -> String.format("Using tbl%d, nRows %d", tableLoopBase + tableLoopIndex,
                         tableLoopRowCountBase + tableLoopIndex));
    return rs;
  }

  @SuppressWarnings("unused")
  void setTableLoopParams(int tableLoopBase, int tableLoopCount, int tableLoopRowCount) {
    this.tableLoopBase = tableLoopBase;
    this.tableLoopCount = tableLoopCount;
    this.tableLoopRowCountBase = tableLoopRowCount;
    this.tableLoopIndex = tableLoopCount - 1; // next will be first of sequence
  }

  record Allow(String name, Consumer<AppInfo> doOp) {}
  ;
  private int allowIdx;
  private JButton allowButton;
  private String allowText;
  private final List<Allow> allow1 = List.of(
      new Allow("all", null), new Allow("n-upd", ai -> ai.rowsModel.setAllowUpdate(false)),
      new Allow("n+upd", ai -> ai.rowsModel.setAllowUpdate(true)),
      new Allow("n-ins", ai -> ai.rowsModel.setAllowInsert(false)),
      new Allow("n+ins", ai -> ai.rowsModel.setAllowInsert(true)),
      new Allow("n-del", ai -> ai.rowsModel.setAllowDelete(false)),
      new Allow("n+del", ai -> ai.rowsModel.setAllowDelete(true)),
      new Allow("n-wrt", ai -> ai.rowsModel.setAllowWrite(false)),
      new Allow("n+wrt", ai -> ai.rowsModel.setAllowWrite(true)),
      new Allow("d-upd", ai -> ai.dbOps.allowUpdate(false)),
      new Allow("d+upd", ai -> ai.dbOps.allowUpdate(true)),
      new Allow("d-ins", ai -> ai.dbOps.allowInsert(false)),
      new Allow("d+ins", ai -> ai.dbOps.allowInsert(true)),
      new Allow("d-del", ai -> ai.dbOps.allowDelete(false)),
      new Allow("d+del", ai -> ai.dbOps.allowDelete(true)));
  @SuppressWarnings("unused")
  private final List<Allow> allow2 = List.of(
      new Allow("all", null), new Allow("n-upd", ai -> ai.rowsModel.setAllowUpdate(false)),
      new Allow("n-ins", ai -> ai.rowsModel.setAllowInsert(false)),
      new Allow("n-del", ai -> ai.rowsModel.setAllowDelete(false)),
      new Allow("n+del", ai -> ai.rowsModel.setAllowDelete(true)),
      new Allow("n+upd", ai -> ai.rowsModel.setAllowUpdate(true)),
      new Allow("n+ins", ai -> ai.rowsModel.setAllowInsert(true)),

      new Allow("d-del", ai -> ai.dbOps.allowDelete(false)),
      new Allow("d-ins", ai -> ai.dbOps.allowInsert(false)),
      new Allow("d-upd", ai -> ai.dbOps.allowUpdate(false)),
      new Allow("d+del", ai -> ai.dbOps.allowDelete(true)),
      new Allow("d+upd", ai -> ai.dbOps.allowUpdate(true)),
      new Allow("d+ins", ai -> ai.dbOps.allowInsert(true)));
  private final List<Allow> allows = allow1;
  void changeAllow() {
    Allow allow = allows.get(allowIdx++);
    allowIdx = allowIdx % allows.size();
    allowButton.setText(allowText.replace("{what}", allow.name));
    AppInfo ai = getAppInfo();
    if (allow.doOp != null) {
      allow.doOp.accept(ai);
      return;
    }
    // everything should be enabled
    RowsModel rm = ai.rowsModel;
    DbOpsAllows ops = ai.dbOps;
    if (rm == null || ops == null)
      return;
    // verify everything enabled
    if (rm.getAllowUpdate() != true)
      throw new IllegalStateException("n-upd");
    if (rm.getAllowInsert() != true)
      throw new IllegalStateException("n-ins");
    if (rm.getAllowDelete() != true)
      throw new IllegalStateException("n-del");
    if (rm.getAllowWrite() != true)
      throw new IllegalStateException("n-wrt");
    if (ops.allowUpdate() != true)
      throw new IllegalStateException("d-upd");
    if (ops.allowInsert() != true)
      throw new IllegalStateException("d-ins");
    if (ops.allowDelete() != true)
      throw new IllegalStateException("d-del");
  }
}
