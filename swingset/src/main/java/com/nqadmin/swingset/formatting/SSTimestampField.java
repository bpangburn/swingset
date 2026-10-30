/*
 * Copyright (C) 2003-2026, Prasanth R. Pasala, Brian E. Pangburn,
 * The Pangburn Group, & Ernie R. Rael
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.nqadmin.swingset.formatting;

import javax.swing.JFormattedTextField.AbstractFormatterFactory;

import dev.visdb.seesaw.formatting.SsFormat;
import dev.visdb.seesaw.formatting.SsTimestampField;

/** SwingSet 4 timestamp field backed by SeeSaw formatters. */
@SuppressWarnings("serial")
public class SSTimestampField extends SSFormattedTextField {
  /** Creates a timestamp field using SeeSaw's default timestamp format. */
  public SSTimestampField() {
    this(SSDateTimeFormatters.adapt(
        SsTimestampField.createFormatterFactory(SsFormat.TIMESTAMP)));
  }

  /** Creates a timestamp field using the supplied formatter factory. */
  public SSTimestampField(AbstractFormatterFactory factory) {
    super(factory);
    super.setValue(new java.util.Date());
  }

  @Override
  public void cleanField() {
    super.setValue(new java.util.Date());
  }
}
