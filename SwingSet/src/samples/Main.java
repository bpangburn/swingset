/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2003, The Pangburn Company, Inc. and Prasanth R. Pasala
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
 
 import com.nqadmin.swingSet.*;
 import javax.swing.*;
 import javax.sql.*;
 import java.sql.*;
 import java.awt.*;
 import sun.jdbc.rowset.JdbcRowSet;
 
 
 
public class MainClass extends JFrame {
	
	JButton btnExample1 = new JButton("Example1");
	JButton btnExample2 = new JButton("Example2");
	JButton btnExample3 = new JButton("Example3");
	JButton btnExample4 = new JButton("Example4");
		
	public MainClass(){
		
		super("SwingSet Demo");
		setSize(400,500);
		
		btnExample1.addListener( new MyButtonListener());
		btnExample2.addListener( new MyButtonListener());
		btnExample3.addListener( new MyButtonListener());
		btnExample4.addListener( new MyButtonListener());
		
		setVisible(true);
		pack();
		

	}
	
	private class MyButtonListener implements ActionListener{
		
		public void actionPerformed( ActionEvent ae){
			if(ae.getSource() instanceof Example1){
				new Example1();
			}
			else if(ae.getSource() instanceof Example2){
				new Example2();
			}	
			else if(ae.getSource() instanceof Example3){
				new Example3();
			}
			else if(ae.getSource() instanceof Example4){
				new Example4();
			}
		}
	}
	
	public static void main (String[] args){
		new MainClass();
	}
}
