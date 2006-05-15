/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2004-2006, The Pangburn Company, Prasanth R. Pasala and
 * Diego Gil
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  The names of its contributors may not be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

package com.nqadmin.swingSet.formatting.helpers;

import java.awt.BorderLayout;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import ca.odell.glazedlists.event.ListEventListener;

/**
 *
 * @author  dags
 */
public class SelectorList extends JList implements ListDataListener, ListEventListener {
    
	private JTextField txtFilter = new JTextField();
	
	private JScrollPane scrollPane = new JScrollPane();
	
	private JPanel panel = new JPanel();
	
    /**
     * Creates a new instance of SelectorList
     */    
    public SelectorList() {
        super(new SelectorListModel());
        init();
    }
    
    /**
     * Contruts a SelectorList object with the sepecified list model.
     * @param model - list model to be used
     */
    public SelectorList(SelectorListModel model) {
        this.setModel(model);
        init();
    }
    
    private void init() {
    	((SelectorListModel)getModel()).setFilterEdit(txtFilter);
    	addComponents();
	}

	private void addComponents() {
		scrollPane.setViewportView(this);
		panel.setLayout(new BorderLayout());
		panel.add(scrollPane, BorderLayout.CENTER);
		panel.add(txtFilter, BorderLayout.SOUTH);
	}
	
	/**
	 * Returns a JPanel containing this list in a scroll pane
	 * @return returns the list component in a JScrollPane
	 */
	public JPanel getComponent() {
		return panel;
	}

	/**
     * Getter for property dataValue.
     * @return Value of property dataValue.
     */
    public Object getDataValue() {
        return ((SelectorListModel)getModel()).getSelectedBoundData(this.getSelectedIndex());
    }
    
    /* (non-Javadoc)
     * @see javax.swing.event.ListDataListener#intervalRemoved(javax.swing.event.ListDataEvent)
     */
    public void intervalRemoved(javax.swing.event.ListDataEvent e) {
    }
    
    /* (non-Javadoc)
     * @see javax.swing.event.ListDataListener#intervalAdded(javax.swing.event.ListDataEvent)
     */
    public void intervalAdded(javax.swing.event.ListDataEvent e) {
    }
    
    /* (non-Javadoc)
     * @see javax.swing.event.ListDataListener#contentsChanged(javax.swing.event.ListDataEvent)
     */
    public void contentsChanged(javax.swing.event.ListDataEvent e) {
    }

    /* (non-Javadoc)
     * @see ca.odell.glazedlists.event.ListEventListener#listChanged(ca.odell.glazedlists.event.ListEvent)
     */
    public void listChanged(ca.odell.glazedlists.event.ListEvent listEvent) {
        this.repaint();
    }

    /* (non-Javadoc)
     * @see javax.swing.JList#setModel(javax.swing.ListModel)
     */
    public void setModel(ListModel model) {

        super.setModel(model);
        ((SelectorListModel)model).addListDataListener(this);
        ((SelectorListModel)model).addListEventListener(this);
        ((SelectorListModel)model).setFilterEdit(txtFilter);
    }
}

/*
* $Log$
* Revision 1.7  2006/04/21 19:12:20  prasanth
* Added comments & CVS tags.
* Made changes to that it has a panel with list & a filter text field.
*
*/