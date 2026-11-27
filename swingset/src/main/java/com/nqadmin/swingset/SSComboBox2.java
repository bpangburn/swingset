/*
 * Copyright (C) 2026, Brian E. Pangburn & The Pangburn Group
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.nqadmin.swingset;

import dev.visdb.seesaw.SsComboBox2;

/** Generic SwingSet combo box with a key and two display/data values. */
@SuppressWarnings("serial")
public class SSComboBox2<K, D, D2> extends SsComboBox2<K, D, D2> {
  public abstract static class AbstractBuilder<K, D, D2,
      T extends AbstractBuilder<K, D, D2, T>>
      extends SsComboBox2.AbstractBuilder<K, D, D2, T> {
    @Override
    public abstract SSComboBox2<K, D, D2> build();
  }

  public static class Builder<K, D, D2> extends AbstractBuilder<K, D, D2, Builder<K, D, D2>> {
    @Override
    protected Builder<K, D, D2> self() {
      return this;
    }

    @Override
    public SSComboBox2<K, D, D2> build() {
      return new SSComboBox2<>(this);
    }
  }

  protected SSComboBox2(AbstractBuilder<K, D, D2, ?> builder) {
    super(builder);
  }
}
