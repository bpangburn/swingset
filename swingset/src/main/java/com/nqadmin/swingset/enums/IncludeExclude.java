/*
 * Copyright (C) 2003-2026, Prasanth R. Pasala, Brian E. Pangburn,
 * The Pangburn Group, & Ernie R. Rael
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.nqadmin.swingset.enums;

/** Include/exclude options retained for SwingSet 4 source compatibility. */
public enum IncludeExclude {
  /** Predefined "include" option. */
  INCLUDE("Include"),
  /** Predefined "exclude" option. */
  EXCLUDE("Exclude");

  private final String displayValue;

  IncludeExclude(String displayValue) {
    this.displayValue = displayValue;
  }

  @Override
  public String toString() {
    return displayValue;
  }
}
