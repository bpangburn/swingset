/* *****************************************************************************
 * Copyright (C) 2025-2026, Ernie R Rael. All rights reserved.
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
 * ****************************************************************************/
package com.nqadmin.swingset.demo;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.sql.SQLException;

import javax.sql.RowSet;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.nqadmin.swingset.navigate.RowSetState;
import com.nqadmin.swingset.navigate.RowsModel;
import com.nqadmin.swingset.utils.SSUtils;

import static com.nqadmin.swingset.utils.SSUtils.sf;

/**
 * x
 */
@SuppressWarnings("serial")
public abstract class RowSetButtons extends JPanel
{
    private static final Logger logger = SSUtils.getLogger();

	record ScreenInfo(Logger logger, RowsModel rowsModel){}
	abstract ScreenInfo getScreenInfo();

	/** Override for notification of "next" button press. */
	void nextRowSetButtonPush() {
		ScreenInfo lm = getScreenInfo();
		setNextDebugRowSet(lm.logger, lm.rowsModel);
	}

	/** Override for notification of "null" button press. */
	void nullRowSet() {
		ScreenInfo si = getScreenInfo();
		si.logger.log(Level.INFO, "nullRowSet");
		si.rowsModel.setRowSet(null);
	}

	/**
	 * Assign the next RowSet to the rowsModel.
	 * Set the cursor to the table name index for newly created RowSets.
	 * Typically used from nextRowSetButtonPush().
	 */
	void setNextDebugRowSet(Logger l, RowsModel rowsModel)
	{
		l.log(Level.INFO, "nextRowSetButtonPush");
		try {
			RowSet rs = getTableLoopRowSet();
			boolean newRowSet = !DemoExtraDB.isExecuted(rs);
			if (newRowSet) {
				rs.execute();
				rs.absolute(DemoExtraDB.findIdxTbl(rs));
			}
			rowsModel.setRowSet(rs); // SetRowSet should leave the row

			// DON'T LIKE INVOKELATER NEEDED.
			// if (newRowSet)
			// 	rowsModel.setRow(DemoExtraDB.findIdxTbl(rs)); //invokeLater...
			// else
			// 	DemoExtraDB.check();
			DemoExtraDB.check();
		} catch (SQLException | ClassNotFoundException ex) {
			l.log(Level.ERROR, (String) null, ex);
		}
	}

	// Default table cycle: 2, 3, 4, 2, 3, 4, ...
	private int tableLoopBase = 2;
	private int tableLoopCount = 3;
	private int tableLoopRowCountBase = 5;
	/** tableLoopIndex is added to base to form table ID. */
	private int tableLoopIndex = tableLoopCount - 1;

	/**
	 * x
	 */
	RowSetButtons()
	{
		init();
		create();
	}

	private void outStuff() {
		logger.log(Level.INFO, () -> sf("Active: RSState %d, NavState %d, RModel %d",
				RowSetState.count(), RowsModel.navCount(), RowsModel.count()));
	}

	private void init() {
		JButton button;
		String text;

		text = "<html><center>next<br>RS</center></html>";
		button = new JButton(text);
		add(button);
		button.addActionListener((e) -> {
			tableLoopIncr();
			nextRowSetButtonPush();
			outStuff();
		});

		text = "<html><center>null<br>RS</center></html>";
		button = new JButton(text);
		add(button);
		button.addActionListener((e) -> {
			nullRowSet();
			outStuff();
		});

		// TODO: could toggle weak/strong
		text = "<html><center>dref<br>RS</center></html>";
		button = new JButton(text);
		add(button);
		button.addActionListener((e) -> {
			DemoExtraDB.derefSupplierData(null);
			outStuff();
		});

		text = "<html><center>clean<br>DB</center></html>";
		button = new JButton(text);
		add(button);
		button.addActionListener((e) -> {
			create();
			outStuff();
		});

		text = "<html><center>garb<br>coll</center></html>";
		button = new JButton(text);
		add(button);
		button.addActionListener((e) -> {
			System.gc();
			outStuff();
		});
	}

	private void create() {
		try {
			H2Demo.clean();
			for (int i = 0; i < tableLoopCount; i++) {
				tableLoopIncr();
				createTableLoopRowSet();
			}
			this.tableLoopIndex = tableLoopCount - 1; // next will be first of sequence
		} catch (SQLException | ClassNotFoundException ex) {
			logger.log(Level.ERROR, (String) null, ex);
		}
	}

	void tableLoopIncr() {
		tableLoopIndex = ++tableLoopIndex % tableLoopCount;
	}

	/** Create the RowSet for the current table loop iteration.
	 */
	private void createTableLoopRowSet() throws SQLException, ClassNotFoundException {
		DemoExtraDB.createSimpleSupplierData(tableLoopBase + tableLoopIndex, tableLoopRowCountBase + tableLoopIndex);
		logger.log(Level.INFO, () -> sf("Creating tbl%d, nRows %d",
				tableLoopBase + tableLoopIndex, tableLoopRowCountBase + tableLoopIndex));
	}

	/** Return the RowSet for the current table loop iteration.
	 */
	RowSet getTableLoopRowSet() throws SQLException, ClassNotFoundException {
		RowSet rs = DemoExtraDB.findSimpleSupplierData(tableLoopBase + tableLoopIndex, tableLoopRowCountBase + tableLoopIndex);
		logger.log(Level.INFO, () -> sf("Using tbl%d, nRows %d",
				tableLoopBase + tableLoopIndex, tableLoopRowCountBase + tableLoopIndex));
		return rs;
	}

	@SuppressWarnings("unused")
	void setTableLoopParams( int tableLoopBase, int tableLoopCount, int tableLoopRowCount) {
		this.tableLoopBase = tableLoopBase;
		this.tableLoopCount = tableLoopCount;
		this.tableLoopRowCountBase = tableLoopRowCount;
		this.tableLoopIndex = tableLoopCount - 1; // next will be first of sequence
	}
}
