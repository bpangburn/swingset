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
package com.nqadmin.swingset;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.sql.RowSet;

import dev.visdb.seesaw.navigate.RowsModel;

/**
 * See {@link dev.visdb.seesaw.SsTable}.
 */
@SuppressWarnings("serial")
public class SSDataGrid extends dev.visdb.seesaw.SsTable {
  /**
   * See {@link dev.visdb.seesaw.SsTable}
   */
  public SSDataGrid() {}

  /**
   * See {@link dev.visdb.seesaw.SsTable}
   * @param rowSet
   * @deprecated use RowsModel
   */
  @Deprecated
  public SSDataGrid(RowSet rowSet) {
    super(RowsModel.create(rowSet));
  }

  /** {@inheritDoc } */
  @Override
  public SSTableModel getModel() {
    return (SSTableModel) super.getModel();
  }

  /** {@inheritDoc } */
  @Override
  protected SSTableModel createDefaultDataModel() {
    return new SSTableModel();
  }
  
//=====================================================================================
//2026-01-12_BP: The code BELOW is needed for SwingSet 4.0.x compatibility
//=====================================================================================     

  /**
   * Sets the column numbers that should be hidden.
   * 
   * @param _columnNumbers columms to hide
   * @deprecated use setHiddenColumns(List)}
   */
  @Deprecated
  public void setHiddenColumns(final int[] _columnNumbers) {
    if (_columnNumbers == null) {
      setHiddenColumns(Collections.emptyList());
      return;
    }
    setHiddenColumns(IntStream.of(_columnNumbers).boxed().collect(Collectors.toList()));
  }

  /**
   * Sets the column numbers that should be hidden.
   * 
   * @param _columnNames names
   * @throws SQLException SQL Exception
   * @deprecated use SetHiddenColumnsByName
   */
  @Deprecated
  public void setHiddenColumns(final String[] _columnNames) throws SQLException {
    // TODO: does null need to be supported?
    if (_columnNames == null) {
      setHiddenColumnsByName(Collections.emptyList());
      return;
    }
    setHiddenColumnsByName(Arrays.stream(_columnNames).collect(Collectors.toList()));
  }

  /**
   * Sets the implementation of the DataGridHandler, which is used to handle row
   * deletions and insertions.
   *
   * @param _dataGridHandler the implementation of the SSDataGridHandler
   *                         interface.
   * @deprecated Use {@code getModel().setSSDataGridHandler(_dataGridHandler)}
   *             instead.
   */
  @Deprecated
  public void setSSDataGridHandler(final SSDataGridHandler _dataGridHandler) {
    getModel().setSSDataGridHandler(_dataGridHandler);
  }

//=====================================================================================
//2026-01-12_BP: The code ABOVE is needed for SwingSet 4.0.x compatibility
//=====================================================================================   
    
}
