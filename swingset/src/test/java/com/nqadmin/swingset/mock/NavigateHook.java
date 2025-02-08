/* *****************************************************************************
 * Copyright (C) 2024, Ernie R Rael. All rights reserved.
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
package com.nqadmin.swingset.mock;

import javax.sql.RowSet;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.SpinnerNumberModel;

import com.nqadmin.swingset.navigate.RowsModel;
import com.nqadmin.swingset.utils.SSUtils;

import static com.nqadmin.swingset.navigate.RowsAction.*;

/**
 *
 */
public class NavigateHook
{
	private final ActionMap actionMap;
	private final RowsModel rowsModel;

	public NavigateHook(RowSet rs)
	{
		this.rowsModel = SSUtils.findRowsModel(rs);
		this.actionMap = rowsModel.fillNavActionMap(null);
	}
	
	public void first() { actionMap.get(ACT_FIRST).actionPerformed(null); }
	public void last() { actionMap.get(ACT_LAST).actionPerformed(null); }
	public void next() { actionMap.get(ACT_NEXT).actionPerformed(null); }
	public void prev() { actionMap.get(ACT_PREVIOUS).actionPerformed(null); }
	public void commit() { actionMap.get(ACT_COMMIT).actionPerformed(null); }

	public void go(int row) {
		ModelAct spinModel = getSpinModelAct();
		if (spinModel.model != null) {
			spinModel.model.setValue(row);
			spinModel.action.actionPerformed(null);
		}
	}

	public int rowCount() {
		ModelAct spinModel = getSpinModelAct();
		if (spinModel.model != null)
			return (Integer)spinModel.model.getMaximum();
		return -1;
	}

	private record ModelAct(SpinnerNumberModel model, Action action){}
	private ModelAct getSpinModelAct() {
		Action act = actionMap.get(ACT_GOTOROW);
		Object value = act.getValue("SPINNER_MODEL");
		return new ModelAct((SpinnerNumberModel) value, act);
	}
}
