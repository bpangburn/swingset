/*
 * Copyright (C) 2026, Brian E. Pangburn & The Pangburn Group
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.nqadmin.swingset;

import dev.visdb.seesaw.SsDbComboBox2;

/** Generic SwingSet combo box with a key and two display/data values. */
@SuppressWarnings("serial")
public class SSDBComboBox2<K, D, D2> extends SsDbComboBox2<K, D, D2> {
  public abstract static class AbstractBuilder<K, D, D2,
      T extends AbstractBuilder<K, D, D2, T>>
      extends SsDbComboBox2.AbstractBuilder<K, D, D2, T> {
    @Override
    public abstract SSDBComboBox2<K, D, D2> build();
  }

  public static class Builder<K, D, D2> extends AbstractBuilder<K, D, D2, Builder<K, D, D2>> {
    @Override
    protected Builder<K, D, D2> self() {
      return this;
    }

    @Override
    public SSDBComboBox2<K, D, D2> build() {
      return new SSDBComboBox2<>(this);
    }
  }

  protected SSDBComboBox2(AbstractBuilder<K, D, D2, ?> builder) {
    super(builder);
  }
}
