/*
 * Copyright (C) 2026, Brian E. Pangburn & The Pangburn Group
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.nqadmin.swingset.formatting;

import dev.visdb.seesaw.contrib.lgooddatepicker.SsLGoodDatePicker;
import dev.visdb.seesaw.navigate.RowsModel;

/**
 * Database-aware calendar date picker backed by Seesaw's LGoodDatePicker integration.
 * Supports binding to JDBC DATE columns through the existing rows model.
 */
@SuppressWarnings("serial")
public class SSLGoodDatePicker extends SsLGoodDatePicker {
  public SSLGoodDatePicker() {
    super();
  }

  public SSLGoodDatePicker(RowsModel rowsModel, String boundColumnName) {
    super(rowsModel, boundColumnName);
  }
}
