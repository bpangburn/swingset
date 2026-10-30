/*
 * Copyright (C) 2026, Brian E. Pangburn & The Pangburn Group
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.nqadmin.swingset.formatting;

import java.text.DateFormat;
import java.util.List;

import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;

import dev.visdb.seesaw.datasources.DateTime;
import dev.visdb.seesaw.datasources.RSC;
import dev.visdb.seesaw.formatting.MaskFormatterFactory;
import dev.visdb.seesaw.formatting.MaskFormatterFactory.SSMaskFormatter;

/** Adapts SeeSaw's date validation to the legacy formatted-field hierarchy. */
final class SSDateTimeFormatters {
  private SSDateTimeFormatters() {}

  static DefaultFormatterFactory adapt(DefaultFormatterFactory factory) {
    if (!(factory instanceof MaskFormatterFactory source)
        || !(source.getDefaultFormatter() instanceof SSMaskFormatter formatter)) {
      return factory;
    }

    // The converter must reject impossible dates even before database binding.
    if (formatter.getConverter() instanceof DateFormatter converter
        && converter.getFormat() instanceof DateFormat format) {
      format.setLenient(false);
    }

    // SeeSaw's stock validator requires instanceof DateTimeField. These wrappers
    // deliberately retain SSFormattedTextField as their common legacy base.
    return new MaskFormatterFactory.Builder<>(formatter.getMask())
        .ssFormat(source.getSSFormat())
        .converter(formatter.getConverter())
        .placeholderCharacter(formatter.getPlaceholderCharacter())
        .valueContainsLiterals(formatter.getValueContainsLiteralCharacters())
        .stringValidator(SSDateTimeFormatters::validate)
        .build();
  }

  private static boolean validate(List<String> strings, RSC component) {
    if (!(component instanceof SSFormattedTextField field)) {
      return false;
    }
    if (!DateTime.isHandledDateTimeComp(component)) {
      // Unbound fields have already been checked by the strict converter.
      return true;
    }
    if (!field.containsUserText() && DateTime.dateTimeColumnValidate("", component)) {
      return true;
    }
    return strings.stream().anyMatch(text -> DateTime.dateTimeColumnValidate(text, component));
  }
}
