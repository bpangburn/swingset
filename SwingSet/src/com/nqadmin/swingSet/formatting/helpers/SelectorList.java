/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2004-2005, The Pangburn Company, Prasanth R. Pasala and
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

import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.event.ListEvent;

import javax.swing.*;
import javax.swing.event.ListDataListener;

/**
 *
 * @author  dags
 */
public class SelectorList extends JList implements ListDataListener, ListEventListener {
    
    /**
     * Creates a new instance of SelectorList
     */
    
    public SelectorList() {
        super(new SelectorListModel());

        //        this.getModel().addListDataListener(this);
//        ((SelectorListModel)this.getModel()).addListEventListener(this);
    }
    
    public SelectorList(SelectorListModel model) {
        this.setModel(model);
//        model.addListDataListener(this);
//        model.addListEventListener(this);
    }
    
    private void Init() {
    }
    
    /**
     * Getter for property dataValue.
     * @return Value of property dataValue.
     */
    public Object getDataValue() {
        return ((SelectorListModel)getModel()).getSelectedBoundData(this.getSelectedIndex());
    }
    
    public void intervalRemoved(javax.swing.event.ListDataEvent e) {
        System.out.println("SelectorList --> intervalRemoved");
    }
    
    public void intervalAdded(javax.swing.event.ListDataEvent e) {
        System.out.println("SelectorList ---> intervalAdded");
    }
    
    public void contentsChanged(javax.swing.event.ListDataEvent e) {
        System.out.println("SelectorList ---> contentsChanged");
    }

    public void listChanged(ca.odell.glazedlists.event.ListEvent listEvent) {
        System.out.println("SelectorList --> listChanged");
        this.repaint();
    }

    public void setModel(ListModel model) {

        super.setModel(model);
        ((SelectorListModel)model).addListDataListener(this);
        ((SelectorListModel)model).addListEventListener(this);
        
    }

    public int getSelectedIndex() {

        int retValue;
        
        retValue = super.getSelectedIndex();
        return retValue;
    }
}
