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

import com.nqadmin.swingset.navigate.NavigateActions;

import static com.nqadmin.swingset.navigate.NavAction.*;

/**
 *
 * @author err
 */
public class NavigateHook
{
	private final NavigateActions navActs;
	private final ActionMap actionMap;

	//public Navigate(NavigateActions navActs)
	public NavigateHook(RowSet rs)
	{
		this.navActs = NavigateActions.get(rs);
		actionMap = navActs.createActionMap();
	}

	public NavigateActions getNavActs()
	{
		return navActs;
	}
	
	public void first() { actionMap.get(NAV_FIRST).actionPerformed(null); }
	public void last() { actionMap.get(NAV_LAST).actionPerformed(null); }
	public void next() { actionMap.get(NAV_NEXT).actionPerformed(null); }
	public void prev() { actionMap.get(NAV_PREVIOUS).actionPerformed(null); }

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
		Action act = actionMap.get(NAV_GOTOROW);
		Object value = act.getValue("SPINNER_MODEL");
		return new ModelAct((SpinnerNumberModel) value, act);
	}
}
