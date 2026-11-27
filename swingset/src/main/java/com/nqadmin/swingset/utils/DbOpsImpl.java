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
 * CONSEQUENTIAL DAMAGES (INCLUING, BUT NOT LIMITED TO, PROCUREMENT OF
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
 * copyright (C) 2026, Ernie R. Rael. All rights reserved.
 * ****************************************************************************/
package com.nqadmin.swingset.utils;

import java.awt.Container;
import java.lang.System.Logger;

import dev.visdb.seesaw.datasources.DbOps;
import dev.visdb.seesaw.utils.SsUtils;

import static java.lang.System.Logger.Level.*;

/**
 * Implementation of DbOps that implements performPreInsertOps() to
 * clear/initialize the various SSComponents on a screen before the
 * user edits the new record.
 * <p>
 * {@code DbOps} is associated with a RowsModel/RowSet, see
 * {@link dev.visdb.seesaw.navigate.RowsModel#create(javax.sql.RowSet, dev.visdb.seesaw.datasources.DbOps) RowsModel(RowSet, DbOps)}.
 * {@link #performPreInsertOps()} searches the container provided to the
 * constructor to find the {@link dev.visdb.seesaw.utils.SsComponent}s to clean.
 * <p>
 * When the user requests to insert a new row, typically a button push,
 * performPreInsertOps() is invoked
 *  and it clears the fields before the user starts editing.
 * @deprecated use {@link dev.visdb.seesaw.datasources.products.DbOpsBase}
 */
@Deprecated
public abstract class DbOpsImpl implements DbOps {
  /**
   * Logger for component
   */
  protected static final Logger logger = System.getLogger(DbOpsImpl.class.getName());

  /**
   * Screen where components to be cleared are located.
   */
  // TODO: find out a way that this is not embedded in the class.
  protected Container container = null;

  /**
   * Constructs a DbOpsImpl with the specified container.
   *
   * @param container	GUI Container to scan for Swing components to clear/reset
   */
  public DbOpsImpl(Container container) {
    this.container = container;
  }

  /**
   * Performs pre-insertion operations, in particular
   * {@link #cleanComponents(Container) }.
   */
  @Override
  public void performPreInsertOps() {
    cleanComponents(container);
  }

  /**
   * In the specified container, clear/initialize SSComponents.
   * Typically done for a new record/row. Uses
   * {@link SsUtils#visitSsComponents(Container, java.util.function.Consumer) }
   * to run {@link dev.visdb.seesaw.utils.SsComponent#cleanField() }.
   * <p>
   * This is done for all SwingSet components,
   * for example text fields, and text areas,
   * recursively looking in to the JTabbedPanes and JPanels inside the given
   * container as needed.
   *
   * @param container container in which to recursively initialize components
   */
  protected void cleanComponents(Container container) {
    logger.log(DEBUG, "Clear/clean container SSComponents recursively.");
    if (container == null)
      return;
    SsUtils.visitSsComponents(container, comp -> comp.cleanField());
  }

  /**
   * @deprecated  use performPostInsertOps(RowsModel)
   */
  @Deprecated
  public void performPostInsertOps() {}

  /**
   * @deprecated use performPostDeletionOps(RowsModel)
   */
  @Deprecated
  public void performPostDeletionOps() {}

  /**
   * @deprecated use performPostUpdateOps(RowsModel)
   */
  @Deprecated
  public void performPostUpdateOps() {}
} // end public class DbOpsImpl
