/*
 * Copyright (C) 2003-2026, Prasanth R. Pasala, Brian E. Pangburn,
 * The Pangburn Group, & Ernie R. Rael
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.nqadmin.swingset.enums;

/** Predefined gender options retained for SwingSet 4 source compatibility. */
public enum Gender3 {
  /** Predefined "male" option. */
  MALE("Male"),
  /** Predefined "female" option. */
  FEMALE("Female"),
  /** Predefined "unisex" option. */
  UNISEX("Unisex");

  private final String displayValue;

  Gender3(String displayValue) {
    this.displayValue = displayValue;
  }

  @Override
  public String toString() {
    return displayValue;
  }
}
