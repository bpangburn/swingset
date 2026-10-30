/*******************************************************************************
 * Copyright (C) 2003-2021, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * Contributors:
 *   Prasanth R. Pasala
 *   Brian E. Pangburn
 *   Diego Gil
 *   Man "Bee" Vo
 *   Ernie R. Rael
 ******************************************************************************/
/* *****************************************************************************
 * The conditions in the above copyright notice apply to this copyright notice.
 * Additions and modifications made by Ernie R. Rael are
 * copyright (C) 2024-2026, Ernie R. Rael. All rights reserved.
 * ****************************************************************************/
package com.nqadmin.swingset;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import dev.visdb.seesaw.SsDbComboBox2;
import dev.visdb.seesaw.models.Item2;

/**
 * See {@link SsDbComboBox2}.
 */
@SuppressWarnings("serial")
public class SSDBComboBox extends SsDbComboBox2<Long, Object, Object>
    implements SSBaseComboBox<Long, Object, Object> {
  private static final String LEGACY_D2_COLUMN = "_swingset_option2_";

  /**
   * Builder.
   */
  public static class Builder extends SsDbComboBox2.AbstractBuilder<Long, Object, Object, Builder> {
    /** defaults to ModelTYpe.GLAZED */
    public Builder() {
      super.modelType(ModelType.GLAZED);
      // SeeSaw requires both names during construction. SwingSet 4 allowed an
      // empty constructor followed by setters, so use inert placeholders until
      // the application supplies the real database column names.
      super.primaryKeyColumnName("_swingset_mapping_");
      super.displayColumnName("_swingset_option_");
    }

    /** self type idiom */
    @Override
    protected Builder self() {
      return this;
    }

    /** @return */
    @Override
    public SSDBComboBox build() {
      return new SSDBComboBox(this);
    }
  }
  /** @param builder */
  private SSDBComboBox(Builder builder) {
    super(builder);
  }

  /**
   * Create SSDBComboBox with GlazedLists.
   * GlazedLists is configured strict.
   * Use {@link #getAutoComplete() } to change its configuration.
   */
  public SSDBComboBox() {
    this(new Builder());
  }

  /**
   * Create SSDBComboBox with GlazedLists.
   * GlazedLists is configured strict.
   * Use {@link #getAutoComplete() } to change its configuration.
   *
   * @param _connection
   * @param _primaryKeyColumnName
   * @param _displayColumnName
   */
  public SSDBComboBox(Connection _connection, String _primaryKeyColumnName,
                      String _displayColumnName) {
    this(new Builder()
             .connection(_connection)
             .primaryKeyColumnName(_primaryKeyColumnName)
             .displayColumnName(_displayColumnName));
  }

  /**
   * Create SSDBComboBox with GlazedLists.
   * GlazedLists is configured strict.
   * Use {@link #getAutoComplete() } to change its configuration.
   *
   * @param _connection
   * @param _query
   * @param _primaryKeyColumnName
   * @param _displayColumnName
   */
  public SSDBComboBox(Connection _connection, String _query, String _primaryKeyColumnName,
                      String _displayColumnName) {
    this(new Builder()
             .connection(_connection)
             .query(_query)
             .primaryKeyColumnName(_primaryKeyColumnName)
             .displayColumnName(_displayColumnName));
  }

  /**
   * @return a copy of the chosenItem with getKey(), getDisplayValue(), getD2()
   */
  @Override
  public Item getChosenItem() {
    //   return new Item(getChosenKey(), getChosenDisplayValue());
    Optional<Item> item = super.getChosenItem((remodel, lItem) -> {
      return new Item(remodel.getKey(lItem), remodel.getDisplayValue(lItem),
          super.hasD2() ? remodel.getD2(lItem) : Item2.NO_D2);
    });
    return item.orElse(new Item(null, null, null));
  }

  /** SwingSet 4 name for {@link #getChosenKey()}. */
  @Override
  public Long getSelectedMapping() {
    return super.getChosenKey();
  }

  /** SwingSet 4 name for {@link #setChosenKey(Long)}. */
  @Override
  public void setSelectedMapping(Long mapping) {
    super.setChosenKey(mapping);
  }

  /** SwingSet 4 name for {@link #getChosenDisplayValue()}. */
  @Override
  public Object getSelectedOption() {
    return super.getChosenDisplayValue();
  }

  /** SwingSet 4 name for {@link #setChosenDisplayValue(Object)}. */
  @Override
  public void setSelectedOption(Object option) {
    if (!super.hasD2()) {
      super.setChosenDisplayValue(option);
      return;
    }

    int index = super.getDisplayValues().indexOf(option);
    if (index >= 0) {
      super.setChosenKey(super.getKeys().get(index));
    } else {
      super.setSelectedItem(option);
    }
  }

  /** SwingSet 4 name for {@link #addDisplayValue(Object, Long)}. */
  public void addOption(Object option, Long mapping) {
    super.addDisplayValue(option, mapping);
  }

  /** SwingSet 4 name for {@link #addDisplayValue(Object, Object, Long)}. */
  public void addOption(Object option, Object option2, Long mapping) {
    if (!super.hasD2()) {
      enableLegacyD2(option, option2, mapping);
      return;
    }
    super.addDisplayValue(option, option2, mapping);
  }

  private void enableLegacyD2(Object option, Object option2, Long mapping) {
    Long selectedMapping = super.getChosenKey();
    int selectedIndex = super.getSelectedIndex();
    List<Long> mappings = new ArrayList<>(super.getKeys());
    List<Object> options = new ArrayList<>(super.getDisplayValues());

    if (super.getAllowNull() && !mappings.isEmpty() && mappings.get(0) == null) {
      mappings.remove(0);
      options.remove(0);
    }

    // SeeSaw permits its D2 model shape to change only while the list is empty.
    // Do not use setDisplayValuesInternal to clear here: when nulls are allowed,
    // it immediately inserts a null item and the D2 shape change then fails.
    try (var remodel = super.keyVisual.getRemodel()) {
      remodel.clear();
    }
    super.setD2ColumnName(LEGACY_D2_COLUMN);

    List<Object> option2s = new ArrayList<>(Collections.nCopies(options.size(), null));
    mappings.add(mapping);
    options.add(option);
    option2s.add(option2);
    super.setDisplayValuesInternal(options, mappings, option2s);
    // Rebuilding the model must not silently change an existing selection.
    if (selectedIndex < 0) {
      super.setSelectedIndex(-1);
    } else {
      super.setChosenKey(selectedMapping);
    }
  }

  /** SwingSet 4 name for {@link #updateDisplayValue(Long, Object)}. */
  public boolean updateOption(Long mapping, String option) {
    return super.updateDisplayValue(mapping, option);
  }

  /**
   * For non generic getChosenItem().
   */
  public static class Item extends Item2<Long, Object, Object>
      implements com.nqadmin.swingset.models.SSListItem {
    /** Create an Item
     * @param key
     * @param displayValue
     * @param d2 */
    public Item(Long key, Object displayValue, Object d2) {
      super(key, displayValue, d2);
    }
  }

  public void setSecondDisplayColumnName(final String secondDisplayColumnName) {
    super.setD2ColumnName(secondDisplayColumnName);
  }

  public void setSeparator(final String separator) {
    super.getListItemFormat().setSeparator(separator);
  }
}
