/*
 * Copyright (C) 2003-2026, Prasanth R. Pasala, Brian E. Pangburn,
 * The Pangburn Group, & Ernie R. Rael
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.nqadmin.swingset.formatting;

import javax.swing.JFormattedTextField.AbstractFormatterFactory;

import dev.visdb.seesaw.formatting.SsDateField;
import dev.visdb.seesaw.formatting.SsFormat;

/** SwingSet 4 date field backed by SeeSaw formatters. */
@SuppressWarnings("serial")
public class SSDateField extends SSFormattedTextField {
  /** Month/day/year format. */
  public static final int MMDDYYYY = 0;

  /** Day/month/year format. */
  public static final int DDMMYYYY = 1;

  /** Creates a date field using month/day/year format. */
  public SSDateField() {
    this(MMDDYYYY);
  }

  /** Creates a date field using a SwingSet 4 format constant. */
  public SSDateField(int format) {
    this(SSDateTimeFormatters.adapt(SsDateField.createFormatterFactory(toSsFormat(format))));
  }

  /** Creates a date field using the supplied formatter factory. */
  public SSDateField(AbstractFormatterFactory factory) {
    super(factory);
    super.setValue(new java.util.Date());
  }

  @Override
  public void cleanField() {
    super.setValue(new java.util.Date());
  }

  private static SsFormat toSsFormat(int format) {
    return switch (format) {
      case MMDDYYYY -> SsFormat.DATE_MMDDYYYY_SLASH;
      case DDMMYYYY -> SsFormat.DATE_DDMMYYYY_SLASH;
      default -> throw new IllegalArgumentException("Unknown date format: " + format);
    };
  }
}
