/*
 * Copyright (C) 2003-2026, Prasanth R. Pasala, Brian E. Pangburn,
 * The Pangburn Group, & Ernie R. Rael
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.nqadmin.swingset.formatting;

import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.JFormattedTextField.AbstractFormatterFactory;
import javax.swing.SwingConstants;

import dev.visdb.seesaw.formatting.SsCurrencyField;
import dev.visdb.seesaw.formatting.SsFormat;

/** SwingSet 4 currency field backed by SeeSaw formatters. */
@SuppressWarnings("serial")
public class SSCurrencyField extends SSFormattedTextField {
  private Integer decimals;
  private Integer precision;
  private Locale editorLocale;
  private Locale displayLocale;

  /** Creates a currency field using default precision and locale. */
  public SSCurrencyField() {
    this(SsCurrencyField.createFormatterFactory(SsFormat.CUSTOM, null, null, null, null));
  }

  /** Creates a currency field with the supplied precision and decimals. */
  public SSCurrencyField(int precision, int decimals) {
    this(precision, decimals, null, null);
  }

  /** Creates a localized currency field. */
  public SSCurrencyField(
      int precision, int decimals, Locale editorLocale, Locale displayLocale) {
    this(SsCurrencyField.createFormatterFactory(
        SsFormat.CUSTOM, precision, decimals, editorLocale, displayLocale));
    this.precision = precision;
    this.decimals = decimals;
    this.editorLocale = editorLocale;
    this.displayLocale = displayLocale;
  }

  /** Creates a currency field using the supplied formatter factory. */
  public SSCurrencyField(AbstractFormatterFactory factory) {
    super(factory);
    super.setHorizontalAlignment(SwingConstants.RIGHT);
    super.setValue(Double.valueOf(0.0));
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
    super.setFormatterFactory(SsCurrencyField.createFormatterFactory(
        SsFormat.CUSTOM, precision, decimals, editorLocale, displayLocale));
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
