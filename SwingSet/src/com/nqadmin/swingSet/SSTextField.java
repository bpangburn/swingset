/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2003-2004, The Pangburn Company, Inc. and Prasanth R. Pasala
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

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.StringTokenizer;


/**
 * SSTextField.java
 *<p>
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 *<p><pre>	
 *	
 *	SSTextField extends the JTextField. This class provides different masks
 *like date mask, SSN mask etc.
 *
 */
public class SSTextField extends JTextField {
	
	/**
	 *	use this mask if mm/dd/yyyy format is required.
	 */
	public static final int  MMDDYYYY =1;
	
	/**
	 *	use this mask if mm/dd/yyyy format is required.
	 */
	public static final int  DDMMYYYY =2;
	
	/**
	 *	use this if the text field contains SSN
	 */
	 public static final int SSN =3;
	
	 /**
	 *	use this if the text field contains decimal number and want to limit
	 *number of decimal places.
	 */
	 public static final int DECIMAL = 4;
	 
	 	 	
	// TYPE OF MASK TO BE USED FOR THIS TEXTFIELD
	private int mask = MMDDYYYY;
	
	// NUMBER OF DECIMALS REQUIRED
	private int numDecimals = 2;
	
	
	
	/**
	 * This function is provided to know if the object has been deserialized.
	 *In which case the listeners have to be added again.
	 */
	private void readObject(ObjectInputStream objIn) throws IOException, ClassNotFoundException{
		objIn.defaultReadObject();
		init();
	}
	
	
	/**
	 *Constructs a new TextField with the specified mask and initialized
	 *with given text. A default model is created, the initial string is
	 *null, and the number of columns is set to 0. 
	 *@param _text the text to be displayed.
	 *@param _mask the mask required for this textfield.
	 */
	public SSTextField(String _text, int _mask){
		super(_text);
		mask = _mask;
		init();
	}
	
	/**
	 *Constructs a new TextField with the specified mask. A default model 
	 *is created, the initial string is null, and the number of columns 
	 *is set to 0. 
	 *@param _mask the mask required for this textfield.
	 */
	public SSTextField(int _mask){
		super();
		mask = _mask;
		init();
	}
	
	/**
	 *Constructs a new TextField. A default model is created, the initial
	 *string is null, and the number of columns is set to 0. 
	 */
	public SSTextField(){
		super();
	}
	
	/**
	 *	sets the mask for this textfield to _mask.
	 *@param _mask the mask required for this textfield.
	 */
	 public void setMask(int _mask){
	 	mask = _mask;
	 	init();
	 }
	 
	 /**
	 *	sets the mask for this textfield to _mask.
	 *Use this constructor only if you are using a decimal mask.
	 *@param _mask the mask required for this textfield.
	 *@param _numDecimals  number of decimal places required
	 *
	 */
	 public SSTextField(int _mask, int _numDecimals){
	 	mask = _mask;
	 	numDecimals = _numDecimals;
	 	init();
	 }
	 
	 /**
	 *	sets the mask for this textfield to _mask and aligns the text as specified
	 *(Horizontal alignment).
	 *Valid keys are: 
	 *	JTextField.LEFT 
	 *	JTextField.CENTER 
	 *	JTextField.RIGHT 
	 *	JTextField.LEADING 
	 *	JTextField.TRAILING 
	 *
	 *Use this constructor only if you are using a decimal mask.
	 *@param _mask the mask required for this textfield.
	 *@param _numDecimals  number of decimal places required
	 *@param _align alignment required. Valid values are JTextField
	 *
	 */
	 public SSTextField(int _mask, int _numDecimals, int _align){
	 	mask = _mask;
	 	numDecimals = _numDecimals;
	 	setHorizontalAlignment(_align);
	 	init();
	 }
	 
	 /**
	  *	sets the number of decimal places required.
	  *This number is used only when mask is set to DECIMAL.
	  *Default value is 2.
	  */
	 public void setNumberOfDecimalPlaces(int _numDecimals){
	 	numDecimals = _numDecimals;
	 }
	 
	 /**
	  *	Initializes the text field.
	  */
	 private void  init(){
	 	
	 	// ADD KEY LISTENER FOR THE TEXT FIELD
	 	this.addKeyListener( new KeyListener(){
	 		
	 			
	 		public void keyPressed(KeyEvent ke){
	 		}
	 		
	 		public void keyTyped(KeyEvent ke){
	 		}
	 		
	 		public synchronized void keyReleased(KeyEvent ke){
	 			
	 				
		 			String str = SSTextField.this.getText();
	 			
		 			// IF THE KEY PRESSED IS ANY OF THE FOLLOWING DO NOTHING
		 			if( ke.getKeyCode() == KeyEvent.VK_BACK_SPACE  || 
		 					ke.getKeyCode() == KeyEvent.VK_DELETE  ||
		 					ke.getKeyCode() == KeyEvent.VK_LEFT    ||
		 					ke.getKeyCode() == KeyEvent.VK_RIGHT   ||
		 					ke.getKeyCode() == KeyEvent.VK_HOME    ||
		 					ke.getKeyCode() == KeyEvent.VK_END	   ||
		 					ke.getKeyCode() == KeyEvent.VK_ENTER	)
		 				return;
		 			
		 			// BASED ON TYPE OF MASK REQUESTED MODIFY THE TEXT
		 			// ACCORDINGLY
		 			switch(mask){
		 				case MMDDYYYY:
		 				case DDMMYYYY:
		 					SSTextField.this.setText(dateMask(str, ke));
		 					break;
		 				case SSN:
		 					SSTextField.this.setText(ssnMask(str,ke));
		 					break;
		 				case DECIMAL:
		 					SSTextField.this.setText(decimalMask(str,numDecimals,ke));
		 					break;	
		 			}
	 			
	 				 				 			
	 		}
	  	});
	 }
	 
	 
	 // HANDLES THE DATE MASK.
	 // SETTING THE SLASHES FOR THE USER.
	 /**
	 *	Fucntion to format date string.
	 *@param str the present string in the text field.
	 *@param ke the KeyEvent that occured
	 *@return returns the formated string.
	 */
	 private String dateMask(String str, KeyEvent ke){
	 	switch(str.length()){
			case 2:
				if( ke.getKeyChar() == '/' ){
					
					str =  "0" + str ;
					
				}
				else{
					str = str + "/";
					
				}
				break;
			case 5:
				if( ke.getKeyChar() == '/' ){
					String newStr = str.substring(0,3);
					newStr = newStr + "0" + str.substring(3,4) + "/";
					str = newStr;
					
				}
				else{
					str = str + "/";
					
				}
				break;
			case 3:
			case 6:
				if( ke.getKeyChar() != '/' ){
					str = str + "/";
					
				}
				break;
			case 4:
			case 7:
				if( ke.getKeyChar() == '/' ){
					str = str.substring(0,str.length()-1);
					
				}
				break;
		}
		return str;	
	}
	
	/**
	 *	Fucntion to format SSN
	 *@param str the present string in the text field.
	 *@param ke the KeyEvent that occured
	 *@return returns the formated string.
	 */
	private String ssnMask(String str, KeyEvent ke){
		switch(str.length()){
			case 3:
			case 6:
				str = str + "-";
				break;
			case 5:
			case 8:
				if(ke.getKeyChar() == '-')
					str = str.substring(0,str.length()-1);
				break;
		}
		return str;
	}
	
	/**
	 *	Function to modify the text for a decimal number as needed.
	 *@param str the present string in the text field.
	 *@param numDecimals number of decimal places allowed
	 *@param ke the KeyEvent that occured
	 *@return returns the formated string.
	 */
	private String decimalMask(String str, int numDecimals, KeyEvent ke){
		StringTokenizer strtok = new StringTokenizer(str,".",false);
		String intPart = "";
		String decimalPart = "";
		String returnStr = str;
		//  BREAK THE STRING IN TO INTERGER AND DECIMAL PARTS
		if( strtok.hasMoreTokens())
			intPart = strtok.nextToken();
		if(strtok.hasMoreTokens())
			decimalPart = strtok.nextToken();
		// IF THE DECIMAL PART IS MORE THAN SPECIFIED
		// TRUNCATE THE EXTRA DECIMAL PLACES
		if( decimalPart.length() > numDecimals )
			returnStr = intPart +"."+ decimalPart.substring(0,numDecimals);
			
		return returnStr;	
	}
	
	
	
}


/*
 * $Log$
 * Revision 1.3  2004/03/08 16:43:37  prasanth
 * Updated copy right year.
 *
 * Revision 1.2  2004/02/23 16:45:51  prasanth
 * Added new constructor
 * public SSTextField(int _mask, int _numDecimals, int _align)
 *
 * Revision 1.1  2003/12/16 18:02:47  prasanth
 * Initial version.
 *
 */