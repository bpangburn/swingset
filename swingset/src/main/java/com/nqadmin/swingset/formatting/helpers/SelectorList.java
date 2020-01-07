/*******************************************************************************
 * Copyright (C) 2003-2020, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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
 ******************************************************************************/

package com.nqadmin.swingset.formatting.helpers;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;

/**
 * SelectorList.java
 * 
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 * 
 * Extension of JList with swing ListDataListener and GlazedList ListEventListener support.
 */
public class SelectorList extends JList<Object> implements ListDataListener, ListEventListener<Object> {
    
	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = 6085433762502942928L;

	private JTextField txtFilter = new JTextField();
	
	protected JScrollPane scrollPane = new JScrollPane();
	
	private JPanel panel = new JPanel();
	
    /**
     * Creates a new instance of SelectorList
     */    
    public SelectorList() {
        super(new SelectorListModel());
        init();
    }
    
    /**
     * Constructs a SelectorList object with the specified list model.
     * @param model - list model to be used
     */
    public SelectorList(SelectorListModel model) {
        this.setModel(model);
        init();
    }
    
    private void init() {
    	((SelectorListModel)getModel()).setFilterEdit(this.txtFilter);
    	addComponents();
	}
    
	private void addComponents() {
		this.txtFilter.addKeyListener(new KeyListener(){

			@Override
			public void keyTyped(KeyEvent e) {
				SelectorList.this.scrollPane.updateUI();
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		this.scrollPane.setViewportView(this);
		this.panel.setLayout(new BorderLayout());
		this.panel.add(this.scrollPane, BorderLayout.CENTER);
		this.panel.add(this.txtFilter, BorderLayout.SOUTH);
		
	}
	
	
	/**
	 * Returns a JPanel containing this list in a scroll pane
	 * @return returns the list component in a JScrollPane
	 */
	public JPanel getComponent() {
		return this.panel;
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
    @Override
	public void intervalRemoved(ListDataEvent e) {
    	// do nothing
    }
    
    /* (non-Javadoc)
     * @see javax.swing.event.ListDataListener#intervalAdded(javax.swing.event.ListDataEvent)
     */
    @Override
	public void intervalAdded(ListDataEvent e) {
    	// do nothing
    }
    
    /* (non-Javadoc)
     * @see javax.swing.event.ListDataListener#contentsChanged(javax.swing.event.ListDataEvent)
     */
    @Override
	public void contentsChanged(ListDataEvent e) {
    	// do nothing
    }

    /* (non-Javadoc)
     * @see ca.odell.glazedlists.event.ListEventListener#listChanged(ca.odell.glazedlists.event.ListEvent)
     */
    @Override
	public void listChanged(ListEvent<Object> listEvent) {
        this.repaint();
    }

    /* (non-Javadoc)
     * @see javax.swing.JList#setModel(javax.swing.ListModel)
     */
    @Override
	public void setModel(ListModel<Object> model) {

        super.setModel(model);
        ((SelectorListModel)model).addListDataListener(this);
        ((SelectorListModel)model).addListEventListener(this);//
        ((SelectorListModel)model).setFilterEdit(this.txtFilter);
    }
}

/*
* $Log$
* Revision 1.8  2006/05/15 15:50:09  prasanth
* Updated javadoc
*
* Revision 1.7  2006/04/21 19:12:20  prasanth
* Added comments & CVS tags.
* Made changes to that it has a panel with list & a filter text field.
*
*/
