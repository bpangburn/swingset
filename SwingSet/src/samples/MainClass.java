/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2003-2005, The Pangburn Company and Prasanth R. Pasala
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

import java.io.File;
import java.sql.*;
import java.awt.*;
import java.awt.event.*;

public class MainClass extends JFrame {
	

    JButton btnExample1 = new JButton("Example1");
    JButton btnExample2 = new JButton("Example2");
    JButton btnExample3 = new JButton("Example3");
    JButton btnExample4 = new JButton("Example4");
    JButton btnExample5 = new JButton("Example5");
    JButton btnExample6 = new JButton("Example6");
    JButton btnExample7 = new JButton("Example7");
    
    public MainClass(){

        super("SwingSet Demo");
        setSize(400,500);

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        btnExample1.addActionListener( new MyButtonListener());
        btnExample2.addActionListener( new MyButtonListener());
        btnExample3.addActionListener( new MyButtonListener());
        btnExample4.addActionListener( new MyButtonListener());
        btnExample5.addActionListener( new MyButtonListener());
        btnExample6.addActionListener( new MyButtonListener());
        btnExample7.addActionListener( new MyButtonListener());

        btnExample1.setPreferredSize(new Dimension(150,25));
        btnExample2.setPreferredSize(new Dimension(150,25));
        btnExample3.setPreferredSize(new Dimension(150,25));
        btnExample4.setPreferredSize(new Dimension(150,25));
        btnExample5.setPreferredSize(new Dimension(150,25));
        btnExample6.setPreferredSize(new Dimension(150,25));
        btnExample7.setPreferredSize(new Dimension(150,25));
        
        
        getContentPane().setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));
        getContentPane().add(btnExample1);
        getContentPane().add(btnExample2);
        getContentPane().add(btnExample3);
        getContentPane().add(btnExample4);
        getContentPane().add(btnExample5);
        getContentPane().add(btnExample6);
        getContentPane().add(btnExample7);

        setVisible(true);
        pack();
    }

    private class MyButtonListener implements ActionListener{

        public void actionPerformed( ActionEvent ae){
            if(ae.getSource().equals(btnExample1)){
                btnExample1.setText("Processing..");
                new Example1();
                btnExample1.setText("Example1");
            }
            else if(ae.getSource().equals(btnExample2)){
                new Example2();
            }
            else if(ae.getSource().equals(btnExample3)){
                new Example3();
            }
            else if(ae.getSource().equals(btnExample4)){
                new Example4();
            }
            else if(ae.getSource().equals(btnExample5)){
                new Example5();
            }
            else if(ae.getSource().equals(btnExample6)){
                new Example6();
            }
            else if(ae.getSource().equals(btnExample7)){
                new Example7();
            }
        }
    }

    public static void main (String[] args){
        new MainClass();
    }
}

/*
 * $Log$
 * Revision 1.7  2005/02/04 22:40:12  yoda2
 * Updated Copyright info.
 *
 * Revision 1.6  2004/11/11 15:04:38  yoda2
 * Using TextPad, converted all tabs to "soft" tabs comprised of four actual spaces.
 *
 */