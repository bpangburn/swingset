/*
 * Copyright (C) 2026, Brian E. Pangburn & The Pangburn Group
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.nqadmin.swingset.formatting;

import java.text.NumberFormat;

import javax.swing.JFormattedTextField.AbstractFormatterFactory;
import javax.swing.SwingConstants;

import dev.visdb.seesaw.formatting.SsFormat;
import dev.visdb.seesaw.formatting.SsIntegerField;

/** SwingSet 4 integer field backed by SeeSaw's integer formatter factory. */
@SuppressWarnings("serial")
public class SSIntegerField extends SSFormattedTextField {
  /** Creates an integer field with the default number of integer digits. */
  public SSIntegerField() {
    this(SsIntegerField.createFormatterFactory(SsFormat.CUSTOM, null));
  }

  /** Creates an integer field with the supplied maximum integer digits. */
  public SSIntegerField(int precision) {
    this(SsIntegerField.createFormatterFactory(SsFormat.CUSTOM, precision));
  }

  /** Creates an integer field using the supplied formatter factory. */
  public SSIntegerField(AbstractFormatterFactory factory) {
    super(factory);
    super.setHorizontalAlignment(SwingConstants.RIGHT);
  }

  /** @return the formatter's maximum integer digits, or -1 if unavailable */
  public int getPrecision() {
    return SSNumberFormatters.getParameter(super.getFormatterFactory(),
        NumberFormat::getMaximumIntegerDigits);
  }

  /** Sets the maximum number of integer digits using SeeSaw's integer factory. */
  public void setPrecision(int precision) {
    super.setFormatterFactory(SsIntegerField.createFormatterFactory(SsFormat.CUSTOM, precision));
  }

  /** Clear to null when allowed, otherwise to zero, as in SeeSaw NumberField. */
  @Override
  public void cleanField() {
    super.setValue(super.getAllowNull() ? null : Integer.valueOf(0));
  }

  /** Retain SeeSaw's formatted-number null validation. */
  @Override
  public boolean componentValidate() {
    return super.getValue() != null || super.getAllowNull();
  }
}
