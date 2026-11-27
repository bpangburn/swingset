/* *****************************************************************************
 * Copyright (C) 2021, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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
 * ****************************************************************************/
/* *****************************************************************************
 * The conditions in the above copyright notice apply to this copyright notice.
 * Additions and modifications made by Ernie R. Rael are
 * copyright (C) 2024-2026, Ernie R. Rael. All rights reserved.
 * ****************************************************************************/
package com.nqadmin.swingset.utils;

import java.awt.Container;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.sql.RowSet;
import javax.sql.rowset.CachedRowSet;

import dev.visdb.seesaw.datasources.DbSupport;
import dev.visdb.seesaw.navigate.RowsModel;
import dev.visdb.seesaw.utils.SsComponent;
import dev.visdb.seesaw.utils.SsUtils;
import dev.visdb.seesaw.utils.SsUtils.KeyInfo;


/**
 * General.
 */
public class SSUtils {
  private SSUtils() {}

  /** Initialize the SwingSet runtime once, before creating Swing components. */
  public static void init() {
    dev.visdb.seesaw.utils.LookupDefaults.init();
  }

  /** Wrap a screen in the configured component decorator panel. */
  public static javax.swing.JPanel createDecoratorPanel(java.awt.Component uiPanel) {
    return SsUtils.createDecoratorPanel(uiPanel);
  }

  /**
   * Temporary for hiding SsCommon; used from SsDBComboBox.
   * @param comp component to update
   */
  public static void updateSsComponent_HACK(SsComponent comp) {
    SsUtils.updateSsComponent_HACK(comp);
  }

  /**
   * Use this if you want a 1-1 correspondence between {@code RowSet} and {@code RowsModel};
   * for 1-1 only this method should be used.
   * If an existing RowsModel for the RowSet is
   * not found, a new RowsModel is created.
   * If {@link RowsModel#create(javax.sql.RowSet) }
   * is used multiple RowsModel can be created for the same RowSet;
   * and see {@link RowsModel#getActiveRowModels(javax.sql.RowSet) }
   * <p>
   * Can also use as a transition aid to RowsModel.
   * @param rs
   * @return
   */
  // TODO: could throw exception if more than one model for specified RowSet.
  public static RowsModel findRowsModel(RowSet rs) {
    return SsUtils.findRowsModel(rs);
  }

  /**
   * Check if the SsComponent's listener is added for debug/logging.
   * @param comp
   * @return true if the listener is added
   */
  public static boolean isSsComponentListenerAddedDebug(SsComponent comp) {
    return SsUtils.isSsComponentListenerAddedDebug(comp);
  }

  /** Put this in the global lookup to create debug row set listeners */
  public static class DebugRowSetListenerFlag {}

  // public static Container findRoot(Component c) {
  // 	return null;
  // }

  /**
   * Notify the user of something...
   */
  // TODO: add option to flash window/panel...
  public static void beep() {
    SsUtils.beep();
    // TODO:
    // UIManager.getLookAndFeel().provideErrorFeedback(JFormattedTextField.this);
  }

  /**
   * Get the size of a map taking into account possible weak keys.
   *
   * @param map get size of this map
   * @return map size
   */
  @SuppressWarnings("null")
  public static int size(Map<?, ?> map) {
    return SsUtils.size(map);
  }

  /**
   * Returns the lookup's current DbSupport.
   * Very fast; it listens.
   * @return DbSupport
   */
  public static DbSupport dbSupport() {
    return SsUtils.dbSupport();
  }

  /**
   * Setup a {@linkplain CachedRowSet}'s primary keys, use the component's
   * row set to get the database table's keys.
   * If not a CachedRowSet or the key is already set, do nothing.
   * Note a JoinRowSet is skipped; only want to set keys for single table.
   * @param comp component
   */
  // TODO: Could have an array of primary keys, one entry per column.
  //		 Could this be needed for joins?
  public static void setupDefaultPrimaryKeys(SsComponent comp) {
    SsUtils.setupDefaultPrimaryKeys(comp);
  }

  // TODO: keys: should spec catalog/schema?
  /**
   * @param dbMetaData
   * @param catalog
   * @param schema
   * @param tableName
   * @return
   * @throws SQLException
   */
  public static List<KeyInfo> getPrimaryKeyInfoForTable(DatabaseMetaData dbMetaData, String catalog,
                                                        String schema, String tableName)
      throws SQLException {
    return SsUtils.getPrimaryKeyInfoForTable(dbMetaData, catalog, schema, tableName);
  }

  /**
   *
   * @param rs
   * @param columnName
   * @return
   * @throws SQLException
   */

  public static List<KeyInfo> getPrimaryKeyInfoForTable(RowSet rs, String columnName)
      throws SQLException {
    return getPrimaryKeyInfoForTable(rs, rs.findColumn(columnName));
  }
  /**
   * Find {@link KeyInfo} for table associated with the specified column.
   * Starting with the column's RowSet, get the dbMetaData, determine keys.
   * @param rs
   * @param colIdx
   * @return
   * @throws SQLException
   */
  // TODO: isKey: lookup specializations for databases to access special result set info.
  //public static List<KeyInfo> getPrimaryKeyInfoForTable(RSC comp)
  public static List<KeyInfo> getPrimaryKeyInfoForTable(RowSet rs, int colIdx) throws SQLException {
    return SsUtils.getPrimaryKeyInfoForTable(rs, colIdx);
  }

  ////////////////////////////////////////////////////////////////////////////
  //
  // Debug Support
  //

  /**
   * This is for toString() of an SsComponent, rather than the JComponents string.
   * @param comp string for this
   * @return string from component
   */
  public static String ssComponentToString(SsComponent comp) {
    return SsUtils.ssComponentToString(comp);
  }

  /**
   * Return a unique name for an Object, for example "String@89AB".
   * Name is SimpleClassName followed by identityHashCode in hex.
   * Used primarily for debug messages.
   * @param o The Object
   * @return unique name for the object or "null"
   */
  // TODO: put this in utils/SsUtil
  public static String objectID(Object o) {
    return SsUtils.objectID(o);
  }

  /**
   * Return the name of the table where column 1 came from.
   * @param rs
   * @return
   */
  public static String tableName(RowSet rs) {
    return SsUtils.tableName(rs);
  }
  /**
   * @return true if junit is running.
   */
  public static boolean isJunit() {
    return SsUtils.isJunit();
  }

  /** Set when junit tests set up a logger. */
  public static String JUNIT_TEST_LOGGING = SsUtils.JUNIT_TEST_LOGGING;

  /**
   * true if in JUnit and NOT using JUnit logging
   * @return
   */
  public static boolean isJunitPrint() {
    return SsUtils.isJunitPrint();
  }
}
