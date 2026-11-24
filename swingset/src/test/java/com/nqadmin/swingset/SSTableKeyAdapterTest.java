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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

import org.junit.jupiter.api.Test;

/** Clipboard parsing and paste alignment regression tests. */
class SSTableKeyAdapterTest {

	@Test
	void preservesBlankColumnsAndCrLfLineEndings() {
		final String[][] rows = SSTableKeyAdapter.parseClipboardCells(
				"Smith\t\t11/23/2026\r\nJones\tAccounting\t11/24/2026\r\n");
		assertEquals(2, rows.length);
		assertArrayEquals(new String[] { "Smith", "", "11/23/2026" }, rows[0]);
		assertArrayEquals(new String[] { "Jones", "Accounting", "11/24/2026" }, rows[1]);
	}

	@Test
	void preservesLeadingAndTrailingEmptyCellsAndChecksWidestRow() {
		final String[][] rows = SSTableKeyAdapter.parseClipboardCells("A\n\tMiddle\t\n");
		assertEquals(2, rows.length);
		assertArrayEquals(new String[] { "A" }, rows[0]);
		assertArrayEquals(new String[] { "", "Middle", "" }, rows[1]);
		assertEquals(3, SSTableKeyAdapter.maxClipboardColumns(rows));
	}

	@Test
	void preservesInteriorBlankRowsButNotFinalLineSeparator() {
		final String[][] rows = SSTableKeyAdapter.parseClipboardCells("A\tB\n\nC\tD\n");
		assertEquals(3, rows.length);
		assertArrayEquals(new String[] { "A", "B" }, rows[0]);
		assertArrayEquals(new String[] { "" }, rows[1]);
		assertArrayEquals(new String[] { "C", "D" }, rows[2]);
		assertEquals(0, SSTableKeyAdapter.parseClipboardCells("").length);
	}

	@Test
	void copyWritesEmptyFieldInsteadOfLiteralNull() {
		final JTable table = new JTable(
				new Object[][] { { "Smith", null, "11/23/2026" } },
				new Object[] { "Name", "Department", "Date" });
		assertEquals("Smith\t\t11/23/2026\n",
				SSTableKeyAdapter.buildClipboardText(table, new int[] { 0 }, new int[] { 0, 1, 2 }));
	}

	@Test
	void pastePreservesOffsetsAndExistingValuesForBlankCells() throws Exception {
		final Object[][] existing = {
				{ "Old Smith", "Keep department", Date.valueOf("2026-01-01") },
				{ "Keep row", "Keep department", Date.valueOf("2026-01-02") },
				{ "Old Jones", "Old department", Date.valueOf("2026-01-03") }
		};
		final DefaultTableModel model = new DefaultTableModel(
				existing, new Object[] { "Name", "Department", "Date" }) {
			private static final long serialVersionUID = 1L;

			@Override
			public Class<?> getColumnClass(final int column) {
				return column == 2 ? Date.class : String.class;
			}
		};
		final JTable table = new JTable(model);
		final String[][] cells = SSTableKeyAdapter.parseClipboardCells(
				"Smith\t\t11/23/2026\n\nJones\tAccounting\t2026-11-24\n");

		SSTableKeyAdapter.pasteClipboardCells(table, cells, 0, 0, cells.length, existing.length);

		assertEquals("Smith", table.getValueAt(0, 0));
		assertEquals("Keep department", table.getValueAt(0, 1));
		assertEquals(Date.valueOf("2026-11-23"), table.getValueAt(0, 2));
		assertEquals("Keep row", table.getValueAt(1, 0));
		assertEquals(Date.valueOf("2026-01-02"), table.getValueAt(1, 2));
		assertEquals("Jones", table.getValueAt(2, 0));
		assertEquals("Accounting", table.getValueAt(2, 1));
		assertEquals(Date.valueOf("2026-11-24"), table.getValueAt(2, 2));
	}

	/**
	 * Simulates SSTableModel's extra editable insertion row: writing the first
	 * nonblank value at the end adds a real row and creates a new insertion row.
	 */
	private static class InsertingModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
		private final List<Object[]> records = new ArrayList<>();

		InsertingModel(final Object[]... existingRecords) {
			for (final Object[] record : existingRecords) {
				records.add(record);
			}
		}

		@Override
		public int getRowCount() {
			return records.size() + 1;
		}

		@Override
		public int getColumnCount() {
			return 3;
		}

		@Override
		public Class<?> getColumnClass(final int column) {
			return column == 2 ? Date.class : String.class;
		}

		@Override
		public Object getValueAt(final int row, final int column) {
			return row == records.size() ? null : records.get(row)[column];
		}

		@Override
		public void setValueAt(final Object value, final int row, final int column) {
			if (row == records.size()) {
				records.add(new Object[3]);
				fireTableRowsInserted(row, row);
			}
			records.get(row)[column] = value;
			fireTableCellUpdated(row, column);
		}
	}

	@Test
	void skipsBlankRowsDuringInsertionAndContinuesWithLaterRecords() throws Exception {
		final JTable table = new JTable(new InsertingModel());
		final String[][] cells = SSTableKeyAdapter.parseClipboardCells(
				"\t\t\nSmith\t\t11/23/2026\n \t \t \nJones\tAccounting\t2026-11-24\n");
		assertEquals(2, SSTableKeyAdapter.destinationRowsNeeded(cells, 0, 0));

		SSTableKeyAdapter.pasteClipboardCells(table, cells, 0, 0, cells.length, 0);

		assertEquals(3, table.getRowCount()); // two inserted records plus insertion row
		assertEquals("Smith", table.getValueAt(0, 0));
		assertEquals(Date.valueOf("2026-11-23"), table.getValueAt(0, 2));
		assertEquals("Jones", table.getValueAt(1, 0));
		assertEquals("Accounting", table.getValueAt(1, 1));
		assertEquals(Date.valueOf("2026-11-24"), table.getValueAt(1, 2));
	}

	@Test
	void preservesBlankExistingRowThenSkipsBlankInsertionRow() throws Exception {
		final JTable table = new JTable(new InsertingModel(
				new Object[] { "Original", "Preserve", Date.valueOf("2026-01-01") }));
		final String[][] cells = SSTableKeyAdapter.parseClipboardCells(
				"\t\t\n\t\t\nNew\tDept\t11/24/2026\n");
		assertEquals(2, SSTableKeyAdapter.destinationRowsNeeded(cells, 0, 1));

		SSTableKeyAdapter.pasteClipboardCells(table, cells, 0, 0, cells.length, 1);

		assertEquals(3, table.getRowCount()); // original, new record, insertion row
		assertEquals("Original", table.getValueAt(0, 0));
		assertEquals("Preserve", table.getValueAt(0, 1));
		assertEquals(Date.valueOf("2026-01-01"), table.getValueAt(0, 2));
		assertEquals("New", table.getValueAt(1, 0));
		assertEquals("Dept", table.getValueAt(1, 1));
	}
}
