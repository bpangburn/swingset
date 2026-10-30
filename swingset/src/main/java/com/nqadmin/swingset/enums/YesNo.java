/*
 * Copyright (C) 2003-2026, Prasanth R. Pasala, Brian E. Pangburn,
 * The Pangburn Group, & Ernie R. Rael
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.nqadmin.swingset.enums;

/** Predefined yes/no options retained for SwingSet 4 source compatibility. */
public enum YesNo {
  /** Predefined "no" option. */
  NO("No"),
  /** Predefined "yes" option. */
  YES("Yes");

  private final String displayValue;

  YesNo(String displayValue) {
    this.displayValue = displayValue;
  }

  @Override
  public String toString() {
    return displayValue;
  }
}
