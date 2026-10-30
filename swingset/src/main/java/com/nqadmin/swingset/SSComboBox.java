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
 * copyright (C) 2024-2025, Ernie R. Rael. All rights reserved.
 * ****************************************************************************/
package com.nqadmin.swingset;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import dev.visdb.seesaw.SsComboBox1;
import dev.visdb.seesaw.models.Item1;

/**
 * This class provides a ComboBox with an Integer key and String value.
 * See {@link dev.visdb.seesaw.SsComboBox2} for detailed documentation.
 */
@SuppressWarnings("serial")
public class SSComboBox extends SsComboBox1<Integer, String>
    implements SSBaseComboBox<Integer, String, Object> {
  /** Builder */
  public static class Builder extends SsComboBox1.AbstractBuilder<Integer, String, Builder> {
    /** self type idiom */
    @Override
    protected Builder self() {
      return this;
    }

    /** @return */
    @Override
    public SSComboBox build() {
      return new SSComboBox(this);
    }
  }

  /** @param builder */
  private SSComboBox(Builder builder) {
    super(builder);
  }

  /**
   * Creates an SSComboBox.
   * GlazedLists is configured strict.
   * Use {@link #getAutoComplete() } to change its configuration.
   */
  public SSComboBox() {
    this(ModelType.GLAZED);
  }

  /**
   * Creates an object of SSComboBox.
   * <p>
   * If GlazedLists is specified, it is configured strict.
   * Use {@link #getAutoComplete() } to change its configuration.
   *
   * @param modelType whether to use SWING or GLAZED combo model
   */
  public SSComboBox(ModelType modelType) {
    this(new Builder().modelType(modelType));
  }

  /** SwingSet 4 constructor selecting GlazedLists or the Swing model. */
  public SSComboBox(boolean useGlazedLists) {
    this(useGlazedLists ? ModelType.GLAZED : ModelType.SWING);
  }

  /**
   * @return a copy of the chosenItem with methods getKey(), getDisplayValue()
   */
  @Override
  public Item getChosenItem() {
    //   return new Item(getChosenKey(), getChosenDisplayValue());
    Optional<Item> item = super.getChosenItem((remodel, lItem) -> {
      return new Item(remodel.getKey(lItem), remodel.getDisplayValue(lItem));
    });
    return item.orElse(new Item(null, null));
  }

  /** SwingSet 4 name for {@link #getChosenKey()}. */
  @Override
  public Integer getSelectedMapping() {
    return super.getChosenKey();
  }

  /** SwingSet 4 name for {@link #setChosenKey(Integer)}. */
  @Override
  public void setSelectedMapping(Integer mapping) {
    super.setChosenKey(mapping);
  }

  /** SwingSet 4 name for {@link #getChosenDisplayValue()}. */
  @Override
  public String getSelectedOption() {
    return super.getChosenDisplayValue();
  }

  /** SwingSet 4 name for {@link #setChosenDisplayValue(String)}. */
  @Override
  public void setSelectedOption(String option) {
    super.setChosenDisplayValue(option);
  }

  /** SwingSet 4 name for {@link #getChosenEnum()}. */
  public Enum<?> getSelectedEnum() {
    return super.getChosenEnum();
  }

  /** SwingSet 4 name for {@link #setChosenEnum(Enum)}. */
  public void setSelectedEnum(Enum<?> option) {
    super.setChosenEnum(option);
  }

  /** SwingSet 4 name for SeeSaw display values. */
  public void setOptions(String[] options) {
    super.setDisplayValues(Arrays.asList(options));
  }

  /**
   * SwingSet 4 array overload for SeeSaw display values and keys.
   *
   * @return {@code true}, retained for the legacy method contract
   */
  public boolean setOptions(String[] options, int[] mappings) {
    List<Integer> mappingList = mappings == null
        ? null
        : IntStream.of(mappings).boxed().collect(Collectors.toList());
    super.setDisplayValues(Arrays.asList(options), mappingList);
    return true;
  }

  /** SwingSet 4 name for SeeSaw display values and keys. */
  public void setOptions(List<String> options, List<Integer> mappings) {
    super.setDisplayValues(options, mappings);
  }

  /** SwingSet 4 name for SeeSaw display values. */
  public void setOptions(List<String> options) {
    super.setDisplayValues(options);
  }

  /** SwingSet 4 name for SeeSaw enum display values. */
  public <T extends Enum<T>> void setOptions(Class<T> enumOptions) {
    super.setDisplayValues(enumOptions);
  }

  /**
   * For non generic getChosenItem().
   */
  public static class Item extends Item1<Integer, String>
      implements com.nqadmin.swingset.models.SSListItem {
    /** Create an Item
     * @param key
     * @param displayValue */
    public Item(Integer key, String displayValue) {
      super(key, displayValue);
    }
  }
} // end public class SSComboBox
