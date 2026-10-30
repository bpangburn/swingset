/*
 * Copyright (C) 2003-2026, Prasanth R. Pasala, Brian E. Pangburn,
 * The Pangburn Group, & Ernie R. Rael
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.nqadmin.swingset.utils;

/** SwingSet 4 enum namespace retained for source compatibility. */
public final class SSEnums {
  private SSEnums() {}

  /** Values used by legacy check-box integrations. */
  public enum CheckBoxValues {
    /** Checked state. */
    Checked(1, "true"),
    /** Unchecked state. */
    Unchecked(0, "false");

    private final int intValue;
    private final String stringValue;

    CheckBoxValues(int intValue, String stringValue) {
      this.intValue = intValue;
      this.stringValue = stringValue;
    }

    /** @return integer representation */
    public int getIntValue() {
      return intValue;
    }

    /** @return string representation */
    public String getStringValue() {
      return stringValue;
    }
  }

  /** Navigation operation. */
  public enum Navigation {
    /** Navigate to the first row. */
    First(3),
    /** Navigate to the last row. */
    Last(4),
    /** Navigate to the next row. */
    Next(1),
    /** Navigate to the previous row. */
    Previous(2);

    private final int value;

    Navigation(int value) {
      this.value = value;
    }

    /** @return legacy integer representation */
    public int getValue() {
      return value;
    }
  }

  /** SwingSet component type. */
  public enum SSComponent {
    JPanel,
    JScrollPane,
    JTabbedPane,
    SSCheckBox,
    SSComboBox,
    SSFormattedTextField,
    SSImage,
    SSLabel,
    SSSlider,
    SSTextArea,
    SSTextField
  }
}
