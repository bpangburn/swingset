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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainClass extends JFrame {
	
	private static final long serialVersionUID = -6316984401822746124L;
	JButton btnExample1 = new JButton("Example1");
    JButton btnExample2 = new JButton("Example2");
    JButton btnExample3 = new JButton("Example3");
    JButton btnExample4 = new JButton("Example4");
    JButton btnExample5 = new JButton("Example5");
    JButton btnExample6 = new JButton("Example6");
    JButton btnExample7 = new JButton("Example7");
    
    String url;
    
    public MainClass(String[] url){
    
        super("SwingSet Demo");
        setSize(400,500);
        this.url = url[0];
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
                new Example1(url);
                btnExample1.setText("Example1");
            }
            else if(ae.getSource().equals(btnExample2)){
                new Example2(url); 
            }
            else if(ae.getSource().equals(btnExample3)){
                new Example3(url);
            }
            else if(ae.getSource().equals(btnExample4)){
                new Example4(url);
            }
            else if(ae.getSource().equals(btnExample5)){
                new Example5(url);
            }
            else if(ae.getSource().equals(btnExample6)){
                new Example6(url);
            }
            else if(ae.getSource().equals(btnExample7)){
                new Example7(url);
            }
        }
    }

    public static void main(String[] url){
        new MainClass(url);
    }
}

/*
 * $Log$
 * Revision 1.8  2012/06/07 15:54:38  beevo
 * Modified example for compatibilty with H2 database.
 *
 * Revision 1.7  2005/02/04 22:40:12  yoda2
 * Updated Copyright info.
 *
 * Revision 1.6  2004/11/11 15:04:38  yoda2
 * Using TextPad, converted all tabs to "soft" tabs comprised of four actual spaces.
 *
 */
