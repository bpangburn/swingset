/*
 * Copyright (C) 2026, Brian E. Pangburn & The Pangburn Group
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.nqadmin.swingset;

import javax.swing.ComboBoxEditor;

/**
 * Source-compatibility view of the SwingSet 4 combo-box API.
 *
 * <p>SeeSaw split the old {@code SSBaseComboBox} implementation into typed
 * combo-box classes. This interface preserves the common mapping/option API
 * used by SwingSet 4 applications while allowing the wrappers to retain their
 * appropriate SeeSaw superclass.</p>
 *
 * <p>This is a source-compatibility interface, not the former Swing component
 * superclass. Code extending SSBaseComboBox or relying on its JComboBox
 * inheritance needs migration; binary compatibility is not provided.</p>
 *
 * @param <M> mapping/key type
 * @param <O> option/display-value type
 * @param <O2> optional second display-value type
 */
public interface SSBaseComboBox<M, O, O2> {
  /**
   * Returns the editor supplied by the concrete Swing combo-box superclass.
   * @return the combo-box editor
   */
  ComboBoxEditor getEditor();

  /** @return the mapping/key for the selected item */
  M getSelectedMapping();

  /** Select an item by its mapping/key. */
  void setSelectedMapping(M mapping);

  /** @return the option/display value for the selected item */
  O getSelectedOption();

  /** Select an item by its option/display value. */
  void setSelectedOption(O option);
}
