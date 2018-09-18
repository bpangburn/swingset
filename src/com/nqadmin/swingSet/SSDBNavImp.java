/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2003-2006, The Pangburn Company and Prasanth R. Pasala.
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

package com.nqadmin.swingSet;

import java.awt.Component;
import java.awt.Container;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import com.nqadmin.swingSet.formatting.SSFormattedTextField;

/**
 * SSDBNavImp.java
 *<p>
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 *<p><pre>
 * Custom implementation of SSDBNav that clears/resets the various database-aware
 * fields on a screen when the user adds a new record.  To achieve this, special
 * implementation of the performPreInsertOps() method is provided.  An instance of
 * this class can be created for the container where the fields are to be cleared
 * and passed to the data navigator.
 *
 * The data navigator will call the performPreInsertOps() method whenever the
 * user presses the insert button on the navigator. This fuctions recursively
 * clears any JTextFields, JTextAreas, and SSCheckBoxes, and if their are any
 * SSComboBoxes or SSDBComboBoxes they will be reset to the first item in the
 * list.
 *
 * This recursive behavior performed on all the components inside the JPanel or
 * JTabbedPane inside the specified container.
 *</pre><p>
 * @author  $Author$
 * @version $Revision$
 */
public class SSDBNavImp extends SSDBNavAdapter {

    /**
	 * 
	 */
	private static final long serialVersionUID = -4632505399798312457L;
	/**
     * Screen where componets to be cleared are located.
     */
    protected Container container = null;

    /**
     * Constructs a SSDBNavImp with the specified container.
     */
    public SSDBNavImp(Container _container) {
        container = _container;
    }

    /**
     * Performs pre-insertion operations.
     */
    public void performPreInsertOps() {
        
        setComponents(container);

    } // end public void performPreInsertOps() {

    /**
     * Clears all the JTextFields and resets the combo boxes to first item.
     *
     * This is done for all SwingSet components, text fields, & text areas,
     * recursively looking in to the JTabbedPanes and JPanels inside the given
     * container as needed.
     *
     * @param _container    container in which to recursively initialize components 
     */
    protected void setComponents(Container _container) {

        Component[] comps = _container.getComponents();

        for (int i=0; i< comps.length; i++ ) {
            if (comps[i] instanceof JTextField) {	            
                // IF IT IS A SSFormattedTextField SET ITS VALUE TO NULL (to avoid parse exception)
                   if(comps[i] instanceof SSFormattedTextField){
        	       		((SSFormattedTextField)comps[i]).setValue(null);
                   }
                   else{
                   // IF IT IS A JTextField SET ITS TEXT TO EMPTY STRING
		                ((JTextField)comps[i]).setText("");
                   }
                   
            } else if(comps[i] instanceof JTextArea) {
            // IF IT IS A JTextArea, SET TO EMPTY STRING
                ((JTextArea)comps[i]).setText("");                
            } else if (comps[i] instanceof JComboBox) {
            // IF IT IS A JComboBox THEN SET IT TO 'EMPTY' ITEM BEFORE FIRST ITEM
                ((JComboBox)comps[i]).setSelectedIndex(-1);
            } else if(comps[i] instanceof SSImage) {
            // IF IT IS A SSImage CLEAR THE IMAGE.
                ((SSImage)comps[i]).clearImage();
            } else if(comps[i] instanceof JCheckBox) {
            // IF IT IS A JCheckBox UNCHECK
                ((JCheckBox)comps[i]).setSelected(false);
            } else if(comps[i] instanceof SSLabel) {
            // IF IT IS A SSLabel, SET TO EMPTY STRING
                ((SSLabel)comps[i]).setText("");
            } else if(comps[i] instanceof JSlider) {
            // IF IT IS A JSlider, SET TO AVERAGE OF MIN/MAX VALUES
                ((JSlider)comps[i]).setValue((((JSlider)comps[i]).getMinimum() + ((JSlider)comps[i]).getMaximum()) / 2);
            } else if(comps[i] instanceof JPanel) {
            // IF IT IS A JPanel RECURSIVELY SET THE FIELDS
                setComponents((Container)comps[i]);
            } else if(comps[i] instanceof JTabbedPane) {
            // IF IT IS A JTabbedPane RECURSIVELY SET THE FIELDS
                setComponents((Container)comps[i]);
            } else if(comps[i] instanceof JScrollPane) {
            // IF IT IS A JScrollPane GET THE VIEW PORT AND RECURSIVELY SET THE FIELDS IN VIEW PORT    
                setComponents((Container) ((JScrollPane)comps[i]).getViewport());
            }            
        }

    } // end protected void setComponents(Container _container) {

} // end public class SSDBNavImp extends SSDBNavAdapter {



/*
 * $Log$
 * Revision 1.15  2006/05/15 16:10:38  prasanth
 * Updated copy right
 *
 * Revision 1.14  2005/03/08 16:06:40  prasanth
 * Added JScrollPane to the list of items to look for in setComponents function.
 *
 * Revision 1.13  2005/02/09 17:12:53  yoda2
 * Consolidated logic, added text area, check box, slider, & SSLabel, and cleaned up JavaDoc.
 *
 * Revision 1.12  2005/02/04 23:05:02  yoda2
 * no message
 *
 * Revision 1.11  2005/02/04 22:48:53  yoda2
 * API cleanup & updated Copyright info.
 *
 * Revision 1.10  2005/01/19 16:46:11  yoda2
 * Updated SSDBComboBox stuff to get rid of getComboBox() calls.
 *
 * Revision 1.9  2005/01/18 22:27:28  yoda2
 * Changed to extend JComboBox rather than JComponent.  Deprecated bind(), setSSRowSet(), & setColumnName().
 *
 * Revision 1.8  2005/01/18 21:00:47  prasanth
 * Added support for SSImage in performPreInsertOps.
 *
 * Revision 1.7  2004/11/11 14:45:48  yoda2
 * Using TextPad, converted all tabs to "soft" tabs comprised of four actual spaces.
 *
 * Revision 1.6  2004/08/10 22:06:58  yoda2
 * Added/edited JavaDoc, made code layout more uniform across classes, made various small coding improvements suggested by PMD.
 *
 * Revision 1.5  2004/08/09 21:28:50  prasanth
 * The default selection of first item is removed.
 * Now no item will be selected (setting the selection to -1)
 *
 * Revision 1.4  2004/03/08 16:43:37  prasanth
 * Updated copy right year.
 *
 * Revision 1.3  2003/12/16 18:01:40  prasanth
 * Documented versions for release 0.6.0
 *
 * Revision 1.2  2003/09/25 14:27:45  yoda2
 * Removed unused Import statements and added preformatting tags to JavaDoc descriptions.
 *
 * Revision 1.1.1.1  2003/09/25 13:56:43  yoda2
 * Initial CVS import for SwingSet.
 *
 */
