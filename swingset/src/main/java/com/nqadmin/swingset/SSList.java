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

import java.sql.JDBCType;
import java.util.Arrays;
import java.util.List;

import dev.visdb.seesaw.SsList1;
import dev.visdb.seesaw.models.DbCollection;
import dev.visdb.seesaw.utils.SsComponent;

/**
 * See {@link SsList1}.
 */
// TODO: Long or Integer?
@SuppressWarnings("serial")
public class SSList extends SsList1<Object, String> implements SsComponent {
  /**
   * Creates an object of SSList with key type of {@code JDBCType.INTEGER}.
   */
  public SSList() {
    // 2022-05-04: Changing from JDBCType.NULL to INTEGER as that will be the most likely
    //  key type and NULL is known to generate errors.
    this(JDBCType.INTEGER);
  }

  /**
   * Creates a SsList1 with default
of {@link dev.visdb.seesaw.models.SSDbArray}
   * of specified jdbcType.
   *
   * @param jdbcType type of key of database elements
   */
  public SSList(JDBCType jdbcType) {
    super(jdbcType);
  }

  /**
   * Creates a SsList1 with specified model.
   *
   * @param collectionModel model to read/write the database
   */
  public SSList(DbCollection collectionModel) {
    super(collectionModel);
  }

  /** SwingSet 4 name for {@link #getChosenKeys()}. */
  public List<Object> getSelectedMappings() {
    return super.getChosenKeys();
  }

  /**
   * Returns selected mappings, not SeeSaw's internal list-item objects.
   * @deprecated use {@link #getSelectedMappings()}
   */
  @Deprecated
  @Override
  public Object[] getSelectedValues() {
    if (super.getSelectedIndex() == -1) {
      return new Object[] {Integer.valueOf(-1)};
    }
    return super.getChosenKeys().toArray();
  }

  /** SwingSet 4 name for SeeSaw display values. */
  public void setOptions(List<String> options) {
    super.setDisplayValues(options);
  }

  /** SwingSet 4 name for SeeSaw display values and keys. */
  public void setOptions(List<String> options, List<Object> mappings) {
    super.setDisplayValues(options, mappings);
  }

  /** SwingSet 4 array overload for SeeSaw display values and keys. */
  public boolean setOptions(String[] options, Object[] mappings) {
    super.setDisplayValues(Arrays.asList(options), mappings == null ? null : Arrays.asList(mappings));
    return true;
  }

  /** SwingSet 4 name for SeeSaw display values. */
  public void setOptions(String[] options) {
    super.setDisplayValues(Arrays.asList(options));
  }

  /** SwingSet 4 name for SeeSaw enum display values. */
  public <T extends Enum<T>> void setOptions(Class<T> enumOptions) {
    super.setDisplayValues(enumOptions);
  }

  /** SwingSet 4 name for {@link #setChosenKeys(Object[])}. */
  public void setSelectedValues(Object[] selectedMappings) {
    super.setChosenKeys(selectedMappings);
  }
}
