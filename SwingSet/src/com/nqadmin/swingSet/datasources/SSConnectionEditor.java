package com.nqadmin.swingSet.datasources;

import java.beans.PropertyEditorSupport;
import java.beans.beancontext.BeanContext;
import java.beans.beancontext.BeanContextProxy;
import java.beans.beancontext.BeanContextChild;
import java.beans.beancontext.BeanContextChildSupport;
import java.util.Vector;
import java.util.Iterator;

public class SSConnectionEditor extends PropertyEditorSupport implements BeanContextProxy {
	
	private BeanContextChildSupport beanContextChildSupport = new BeanContextChildSupport();
	Vector vecSSConn = new Vector();
	
	public BeanContextChild getBeanContextProxy(){
		System.out.println("getBeanContextProxy Called");
		return beanContextChildSupport;
	}
	
	public String getAsText(){
		Object curValue = getValue();
		if(curValue == null)
			return "<none>";
		return curValue.toString();	
	}

	public void setAsText(String value){
		if(value.trim().equals("<none>"))
			setValue(null);
		else{
			for(int i=0; i<vecSSConn.size(); i++){
				if(vecSSConn.elementAt(i).toString().equals(value)){
					setValue(vecSSConn.elementAt(i));
					break;
				}
			}
		}	
	}
	
	public String[] getTags(){
		vecSSConn.removeAllElements();
		BeanContext beanContext = beanContextChildSupport.getBeanContext();
		if(beanContext == null){
			return new String[]{"Bean Context Null"};
		}
		Iterator iterator = beanContext.iterator();
		while(iterator.hasNext()){
			Object sibling = iterator.next();
			if(sibling instanceof SSConnection){
				vecSSConn.add(sibling);
			}
		}
		String[] names = new String[vecSSConn.size() + 1];
		if(vecSSConn.size() > 0){
			
			for(int i=0; i<vecSSConn.size(); i++){
				names[i] = vecSSConn.elementAt(i).toString();
			}
			return names;
		}
		names[vecSSConn.size()] = String.valueOf(vecSSConn.size());
		return names;

	}
}