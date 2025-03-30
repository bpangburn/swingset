/*
 * Portions created by Ernie Rael are
 * Copyright (C) 2025 Ernie Rael.  All Rights Reserved.
 *
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Ernie Rael <errael@raelity.com>
 */
package com.nqadmin.swingset.demo;

import java.lang.System.Logger;
import java.sql.SQLException;

import javax.sql.RowSet;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.nqadmin.swingset.utils.SSUtils;

import static com.nqadmin.swingset.utils.SSUtils.sf;

/**
 * x
 */
@SuppressWarnings("serial")
public class RowSetButtons extends JPanel
{
    private static final Logger logger = SSUtils.getLogger();

	/** Override for notification of "next" button press. */
	void nextRowSetButtonPush() {
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
	}

	private void init() {
		JButton button;
		String text;

		text = "<html><center>next<br>RowSet</center></html>";
		button = new JButton(text);
		add(button);
		button.addActionListener((e) -> {
			tableLoopIncr();
			nextRowSetButtonPush();
		});
	}

	void tableLoopIncr() {
		tableLoopIndex = ++tableLoopIndex % tableLoopCount;
	}

	/** Return the RowSet for the current table loop iteration.
	 */
	RowSet getTableLoopRowSet() throws SQLException, ClassNotFoundException {
		logger.log(Logger.Level.INFO, () -> sf("Using tbl%d, nRows %d",
				tableLoopBase + tableLoopIndex, tableLoopRowCountBase));
		return DemoExtraDB.findSimpleSupplierData(tableLoopBase + tableLoopIndex, tableLoopRowCountBase + tableLoopIndex);
	}

	@SuppressWarnings("unused")
	void setTableLoopParams( int tableLoopBase, int tableLoopCount, int tableLoopRowCount) {
		this.tableLoopBase = tableLoopBase;
		this.tableLoopCount = tableLoopCount;
		this.tableLoopRowCountBase = tableLoopRowCount;
		this.tableLoopIndex = tableLoopCount - 1; // next will be first of sequence
	}
}
