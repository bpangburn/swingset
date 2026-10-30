/*
 * Copyright (C) 2003-2026, Prasanth R. Pasala, Brian E. Pangburn,
 * The Pangburn Group, & Ernie R. Rael
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.nqadmin.swingset.formatting;

import java.text.NumberFormat;

import javax.swing.JFormattedTextField.AbstractFormatterFactory;
import javax.swing.SwingConstants;

import dev.visdb.seesaw.formatting.SsFormat;
import dev.visdb.seesaw.formatting.SsNumericField;

/** SwingSet 4 numeric field backed by SeeSaw formatters. */
@SuppressWarnings("serial")
public class SSNumericField extends SSFormattedTextField {
  private Integer decimals;
  private Integer precision;

  /** Creates a numeric field using default precision. */
  public SSNumericField() {
    this(SsNumericField.createFormatterFactory(SsFormat.CUSTOM, null, null));
  }

  /** Creates a numeric field with the supplied precision and decimals. */
  public SSNumericField(int precision, int decimals) {
    this(SsNumericField.createFormatterFactory(SsFormat.CUSTOM, precision, decimals));
    this.precision = precision;
    this.decimals = decimals;
  }

  /** Creates a numeric field using the supplied formatter factory. */
  public SSNumericField(AbstractFormatterFactory factory) {
    super(factory);
    super.setHorizontalAlignment(SwingConstants.RIGHT);
  }

  /** @return maximum fraction digits */
  public int getDecimals() {
    return SSNumberFormatters.getParameter(super.getFormatterFactory(),
        NumberFormat::getMaximumFractionDigits);
  }

  /** @return maximum integer digits */
  public int getPrecision() {
    return SSNumberFormatters.getParameter(super.getFormatterFactory(),
        NumberFormat::getMaximumIntegerDigits);
  }

  /** Sets maximum fraction digits. */
  public void setDecimals(int decimals) {
    this.decimals = decimals;
    resetFormatterFactory();
  }

  /** Sets maximum integer digits. */
  public void setPrecision(int precision) {
    this.precision = precision;
    resetFormatterFactory();
  }

  private void resetFormatterFactory() {
    super.setFormatterFactory(
        SsNumericField.createFormatterFactory(SsFormat.CUSTOM, precision, decimals));
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
