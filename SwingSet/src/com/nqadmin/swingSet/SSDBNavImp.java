/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2003-2004, The Pangburn Company, Inc. and Prasanth R. Pasala.
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



import java.awt.*;
import javax.swing.*;



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
 * @author	$Author$
 * @version	$Revision$
 */
public class SSDBNavImp extends SSDBNavAdapter {

	Container container = null;

	/**
	 * constructs a SSDBNavImp with the specified container.
	 */
	public SSDBNavImp( Container _container ) {
		container = _container;
	}

	/**
	 * clears all the JTextFields and resets the combo boxes to first item.
	 *
	 *	This is done for all the JTextFields and combo boxes recursively looking in to the
	 *JTabbedPanes and JPanels inside the given container.
	 */
	public void performPreInsertOps(){

		Component[] comps = container.getComponents();

		for(int i=0; i< comps.length; i++ ){
			//IF ITS TEXTFIELD SET ITS TEXT TO EMPTY STRING
			if(comps[i] instanceof JTextField){
				((JTextField)comps[i]).setText("");
			}
			else if (comps[i] instanceof JComboBox ){
				JComboBox combo = ((JComboBox)comps[i]);
				if(combo.getItemCount() > 0){
					combo.setSelectedIndex(0);
				}
			}
			// IF ITS A SSComboBox THEN SEE IF THERE ARE ANY ITEMS INSIDE IT  IF YES SET IT TO
			// FIRST ITEM IN THE COMBO
			else if (comps[i] instanceof SSComboBox ){
				JComboBox combo = ((SSComboBox)comps[i]).getComboBox();
				if(combo.getItemCount() > 0){
					combo.setSelectedIndex(0);
				}
			}
			// IF ITS A SSDBComboBox THEN SEE IF THERE ARE ANY ITEMS INSIDE IT  IF YES SET IT TO
			// FIRST ITEM IN THE COMBO
			else if (comps[i] instanceof SSDBComboBox ){
				JComboBox combo = ((SSDBComboBox)comps[i]).getComboBox();
				if(combo.getItemCount() > 0){
					combo.setSelectedIndex(0);
				}
			}
			//IF ITS A JPANEL RECURSIVELY SET THE FIELDS
			else if(comps[i] instanceof  JPanel) {
				setComponents((Container)comps[i]);
			}
			//IF ITS A JTABBEDPANE RECURSIVELY SET THE FIELDS
			else if(comps[i] instanceof JTabbedPane) {
				setComponents((Container)comps[i]);
			}


		}
	}
	
	/**
	 * function to clear the JTextFields and ComboBoxes recursively inside a JPanel or JTabbedPane
	 */
	private void setComponents(Container innerContainer){
		Component[] comps = innerContainer.getComponents();

		for(int i=0; i< comps.length; i++ ){
			if(comps[i] instanceof JTextField){
				((JTextField)comps[i]).setText("");
			}
			else if (comps[i] instanceof JComboBox ){
				JComboBox combo = ((JComboBox)comps[i]);
				if(combo.getItemCount() > 0){
					combo.setSelectedIndex(0);
				}
			}
			// IF ITS A SSComboBox THEN SEE IF THERE ARE ANY ITEMS INSIDE IT  IF YES SET IT TO
			// FIRST ITEM IN THE COMBO
			else if (comps[i] instanceof SSComboBox ){
				JComboBox combo = ((SSComboBox)comps[i]).getComboBox();
				if(combo.getItemCount() > 0){
					combo.setSelectedIndex(0);
				}
			}
			// IF ITS A SSDBComboBox THEN SEE IF THERE ARE ANY ITEMS INSIDE IT  IF YES SET IT TO
			// FIRST ITEM IN THE COMBO
			else if (comps[i] instanceof SSDBComboBox ){
				JComboBox combo = ((SSDBComboBox)comps[i]).getComboBox();
				if(combo.getItemCount() > 0){
					combo.setSelectedIndex(0);
				}
			}
			else if(comps[i] instanceof  JPanel) {
				setComponents((Container)comps[i]);
			}
			else if(comps[i] instanceof JTabbedPane) {
				setComponents((Container)comps[i]);
			}

		}
	}

}



/*
 * $Log$
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