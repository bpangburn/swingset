/*
 * Copyright (C) 2026, Brian E. Pangburn & The Pangburn Group
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.nqadmin.swingset.formatting;

import java.text.NumberFormat;
import java.util.function.ToIntFunction;

import javax.swing.JFormattedTextField.AbstractFormatterFactory;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

/** Reads the actual formatter settings instead of maintaining stale defaults. */
final class SSNumberFormatters {
  private SSNumberFormatters() {}

  static int getParameter(AbstractFormatterFactory factory, ToIntFunction<NumberFormat> parameter) {
    if (factory instanceof DefaultFormatterFactory defaults
        && defaults.getEditFormatter() instanceof NumberFormatter formatter
        && formatter.getFormat() instanceof NumberFormat format) {
      return parameter.applyAsInt(format);
    }
    return -1;
  }
}
