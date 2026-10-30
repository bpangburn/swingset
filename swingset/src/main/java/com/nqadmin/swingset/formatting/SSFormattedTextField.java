/*
 * Copyright (C) 2003-2026, Prasanth R. Pasala, Brian E. Pangburn,
 * The Pangburn Group, & Ernie R. Rael
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.nqadmin.swingset.formatting;

import java.text.Format;

import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;
import javax.swing.text.InternationalFormatter;

import dev.visdb.seesaw.formatting.SsFormattedTextField;

/** SwingSet 4 name for SeeSaw's formatted text field. */
@SuppressWarnings("serial")
public class SSFormattedTextField extends SsFormattedTextField {
  /** Creates an unconfigured formatted field. */
  public SSFormattedTextField() {
    super();
  }

  /** Creates a field using the supplied formatter. */
  public SSFormattedTextField(AbstractFormatter formatter) {
    super(formatter);
  }

  /** Creates a field using the supplied formatter factory. */
  public SSFormattedTextField(AbstractFormatterFactory factory) {
    super(factory);
  }

  /** Creates a field using a formatter for the supplied format. */
  public SSFormattedTextField(Format format) {
    super(new InternationalFormatter(format));
  }

  /** @deprecated use {@link #getAllowNull()} */
  @Deprecated
  public boolean isNullable() {
    return super.getAllowNull();
  }

  /** @deprecated use {@link #setAllowNull(boolean)} */
  @Deprecated
  public void setNullable(boolean nullable) {
    super.setAllowNull(nullable);
  }
}
