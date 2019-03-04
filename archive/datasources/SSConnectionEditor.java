/*******************************************************************************
 * Copyright (C) 2003-2019, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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

package com.nqadmin.swingSet.datasources;

import java.beans.PropertyEditorSupport;
import java.beans.beancontext.BeanContext;
import java.beans.beancontext.BeanContextProxy;
import java.beans.beancontext.BeanContextChild;
import java.beans.beancontext.BeanContextChildSupport;
import java.util.Vector;
import java.util.Iterator;

/**
 * SSConnectionEditor.java
 * 
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 * 
 * SSConnectionEditor is used to facilitate Java Bean editing of data source
 * connection information.
 */
public class SSConnectionEditor extends PropertyEditorSupport implements BeanContextProxy {
	
	private BeanContextChildSupport beanContextChildSupport = new BeanContextChildSupport();
	Vector<Object> vecSSConn = new Vector<>();
	
	@Override
	public BeanContextChild getBeanContextProxy(){
		return this.beanContextChildSupport;
	}
	
	@Override
	public String getAsText(){
		Object curValue = getValue();
		if(curValue == null)
			return "<none>";
		return curValue.toString();	
	}

	@Override
	public void setAsText(String value){
		if(value.trim().equals("<none>"))
			setValue(null);
		else{
			for(int i=0; i<this.vecSSConn.size(); i++){
				if(this.vecSSConn.elementAt(i).toString().equals(value)){
					setValue(this.vecSSConn.elementAt(i));
					break;
				}
			}
		}	
	}
	
	@Override
	public String[] getTags(){
		this.vecSSConn.removeAllElements();
		BeanContext beanContext = this.beanContextChildSupport.getBeanContext();
		if(beanContext == null){
			return new String[]{"Bean Context Null"};
		}
		Iterator<?> iterator = beanContext.iterator();
		while(iterator.hasNext()){
			Object sibling = iterator.next();
			if(sibling instanceof SSConnection){
				this.vecSSConn.add(sibling);
			}
		}
		String[] names = new String[this.vecSSConn.size() + 1];
		if(this.vecSSConn.size() > 0){
			
			for(int i=0; i<this.vecSSConn.size(); i++){
				names[i] = this.vecSSConn.elementAt(i).toString();
			}
			return names;
		}
		names[this.vecSSConn.size()] = String.valueOf(this.vecSSConn.size());
		return names;

	}
}
