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

package com.nqadmin.swingSet.formatting;

import com.nqadmin.swingSet.SSDBNav;
import java.util.Locale;


/**
 *
 * @author  dags
 */
public class FormattingTests extends javax.swing.JFrame implements SSDBNav {
    
    /** Creates new form FormattingTests */
    public FormattingTests() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        sSConnection1 = new com.nqadmin.swingSet.datasources.SSConnection();
        sSJdbcRowSetImpl1 = new com.nqadmin.swingSet.datasources.SSJdbcRowSetImpl();
        rowSetHelperPopup1 = new com.nqadmin.swingSet.formatting.helpers.RowSetHelperPopup();
        helperPopup1 = new com.nqadmin.swingSet.formatting.helpers.HelperPopup();
        helperPopup2 = new com.nqadmin.swingSet.formatting.helpers.HelperPopup();
        helperPopup3 = new com.nqadmin.swingSet.formatting.helpers.HelperPopup();
        sSDataNavigator1 = new com.nqadmin.swingSet.SSDataNavigator();
        jPanel1 = new javax.swing.JPanel();
        sSFormattedTextField1 = new com.nqadmin.swingSet.formatting.SSFormattedTextField();
        sSFormattedTextField2 = new com.nqadmin.swingSet.formatting.SSFormattedTextField();
        sSDateField1 = new com.nqadmin.swingSet.formatting.SSDateField();
        sSDateField2 = new com.nqadmin.swingSet.formatting.SSDateField();
        sSBooleanField1 = new com.nqadmin.swingSet.formatting.SSBooleanField();
        sSFormattedTextField3 = new com.nqadmin.swingSet.formatting.SSFormattedTextField();
        sSIntegerField1 = new com.nqadmin.swingSet.formatting.SSIntegerField();
        jScrollPane1 = new javax.swing.JScrollPane();
        sSMemoField1 = new com.nqadmin.swingSet.formatting.SSMemoField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();

        sSConnection1.setDriverName("org.postgresql.Driver");
        sSConnection1.setUrl("jdbc:postgresql://localhost/mag");
        sSConnection1.setUsername("dags");
        try {
            sSConnection1.createConnection();
        }
        catch(java.lang.ClassNotFoundException nfe) {
            System.out.println(nfe);
        }
        catch(java.lang.Exception ex) {
            System.out.println(ex);
        }

        sSJdbcRowSetImpl1.setSSConnection(sSConnection1);
        sSJdbcRowSetImpl1.setCommand("select * from accounts ORDER BY account_id");

        try {
            sSJdbcRowSetImpl1.execute();
        }
        catch (java.sql.SQLException se) {
            System.out.println(se);
        }

        rowSetHelperPopup1.setConnection(sSConnection1);
        rowSetHelperPopup1.setDataColumn("account_id");
        rowSetHelperPopup1.setListColumn("account_name");
        rowSetHelperPopup1.setOrderBy("account_name");
        rowSetHelperPopup1.setRowSet(sSJdbcRowSetImpl1);

        helperPopup1.setConnection(sSConnection1);
        helperPopup1.setDataColumn("account_id");
        helperPopup1.setListColumn("account_name");
        helperPopup1.setOrderBy("account_name");
        helperPopup1.setTable("accounts");

        helperPopup2.setColumnType(1);
        helperPopup2.setConnection(sSConnection1);
        helperPopup2.setDataColumn("currency_id");
        helperPopup2.setListColumn("currency_name");
        helperPopup2.setOrderBy("currency_name");
        helperPopup2.setTable("currencies");
        
        helperPopup3.setColumnType(0);
        helperPopup3.setConnection(sSConnection1);
        helperPopup3.setDataColumn("account_type_id");
        helperPopup3.setListColumn("account_type_name");
        helperPopup3.setOrderBy("account_type_name");
        helperPopup3.setTable("accounts_types");
        

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        sSDataNavigator1.setDBNav(this);
        sSDataNavigator1.setFocusable(false);
        sSDataNavigator1.setSSRowSet(sSJdbcRowSetImpl1);
        getContentPane().add(sSDataNavigator1, java.awt.BorderLayout.SOUTH);

        jPanel1.setLayout(null);

        sSFormattedTextField1.setColumns(10);
        sSFormattedTextField1.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        sSFormattedTextField1.setColumnName("account_id");
        sSFormattedTextField1.setHelper(rowSetHelperPopup1);
        sSFormattedTextField1.setNavigator(sSDataNavigator1);
        sSFormattedTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sSFormattedTextField1ActionPerformed(evt);
            }
        });

        jPanel1.add(sSFormattedTextField1);
        sSFormattedTextField1.setBounds(120, 30, 70, 19);

        sSFormattedTextField2.setColumnName("account_name");
        sSFormattedTextField2.setNavigator(sSDataNavigator1);
        jPanel1.add(sSFormattedTextField2);
        sSFormattedTextField2.setBounds(120, 60, 310, 19);

        sSDateField1.setColumnName("valid_since");
        sSDateField1.setNavigator(sSDataNavigator1);
        jPanel1.add(sSDateField1);
        sSDateField1.setBounds(120, 120, 110, 19);

        sSDateField2.setColumnName("valid_until");
        sSDateField2.setNavigator(sSDataNavigator1);

        jPanel1.add(sSDateField2);
        sSDateField2.setBounds(120, 150, 110, 19);

        sSBooleanField1.setText("Habilitada");
        sSBooleanField1.setColumnName("is_enabled");
        sSBooleanField1.setNavigator(sSDataNavigator1);
        sSBooleanField1.setRowSet(sSJdbcRowSetImpl1);
        jPanel1.add(sSBooleanField1);
        sSBooleanField1.setBounds(210, 30, 110, 23);

        sSFormattedTextField3.setColumns(6);
        sSFormattedTextField3.setColumnName("currency_id");
        sSFormattedTextField3.setHelper(helperPopup2);
        sSFormattedTextField3.setNavigator(sSDataNavigator1);
        sSFormattedTextField3.setSSRowSet(sSJdbcRowSetImpl1);
        jPanel1.add(sSFormattedTextField3);
        sSFormattedTextField3.setBounds(120, 90, 70, 19);

        sSIntegerField1.setColumnName("account_type_id");
        sSIntegerField1.setHelper(helperPopup3);
        sSIntegerField1.setNavigator(sSDataNavigator1);
        sSIntegerField1.setSSRowSet(sSJdbcRowSetImpl1);
        jPanel1.add(sSIntegerField1);
        sSIntegerField1.setBounds(120, 180, 60, 19);

        sSMemoField1.setColumnName("instructions");
        sSMemoField1.setNavigator(sSDataNavigator1);
        sSMemoField1.setNextFocusableComponent(sSFormattedTextField1);
        sSMemoField1.setRowSet(sSJdbcRowSetImpl1);
        jScrollPane1.setViewportView(sSMemoField1);

        jPanel1.add(jScrollPane1);
        jScrollPane1.setBounds(120, 210, 430, 100);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Cuenta");
        jPanel1.add(jLabel1);
        jLabel1.setBounds(10, 30, 100, 15);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Denominacion");
        jPanel1.add(jLabel2);
        jLabel2.setBounds(10, 60, 100, 15);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Moneda");
        jPanel1.add(jLabel3);
        jLabel3.setBounds(10, 90, 100, 15);

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Vigente desde");
        jPanel1.add(jLabel5);
        jLabel5.setBounds(10, 120, 100, 15);

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Vigente Hasta");
        jPanel1.add(jLabel6);
        jLabel6.setBounds(10, 150, 100, 15);

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Tipo");
        jPanel1.add(jLabel7);
        jLabel7.setBounds(10, 180, 100, 15);

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Instrucciones");
        jPanel1.add(jLabel8);
        jLabel8.setBounds(10, 210, 100, 15);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-594)/2, (screenSize.height-376)/2, 594, 376);
    }//GEN-END:initComponents
    
    private void sSFormattedTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sSFormattedTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sSFormattedTextField1ActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FormattingTests().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    com.nqadmin.swingSet.formatting.helpers.HelperPopup helperPopup1;
    com.nqadmin.swingSet.formatting.helpers.HelperPopup helperPopup2;
    com.nqadmin.swingSet.formatting.helpers.HelperPopup helperPopup3;
    javax.swing.JLabel jLabel1;
    javax.swing.JLabel jLabel2;
    javax.swing.JLabel jLabel3;
    javax.swing.JLabel jLabel5;
    javax.swing.JLabel jLabel6;
    javax.swing.JLabel jLabel7;
    javax.swing.JLabel jLabel8;
    javax.swing.JPanel jPanel1;
    javax.swing.JScrollPane jScrollPane1;
    com.nqadmin.swingSet.formatting.helpers.RowSetHelperPopup rowSetHelperPopup1;
    com.nqadmin.swingSet.formatting.SSBooleanField sSBooleanField1;
    com.nqadmin.swingSet.datasources.SSConnection sSConnection1;
    com.nqadmin.swingSet.SSDataNavigator sSDataNavigator1;
    com.nqadmin.swingSet.formatting.SSDateField sSDateField1;
    com.nqadmin.swingSet.formatting.SSDateField sSDateField2;
    com.nqadmin.swingSet.formatting.SSFormattedTextField sSFormattedTextField1;
    com.nqadmin.swingSet.formatting.SSFormattedTextField sSFormattedTextField2;
    com.nqadmin.swingSet.formatting.SSFormattedTextField sSFormattedTextField3;
    com.nqadmin.swingSet.formatting.SSIntegerField sSIntegerField1;
    com.nqadmin.swingSet.datasources.SSJdbcRowSetImpl sSJdbcRowSetImpl1;
    com.nqadmin.swingSet.formatting.SSMemoField sSMemoField1;
    // End of variables declaration//GEN-END:variables
    
    public void performNavigationOps(int _navigationType) {
        System.out.println("performNavigationOps");
    }
    
    public void performRefreshOps() {
    }
    
    public void performPreInsertOps() {
        System.out.println("performPreInsertOps");
        
        sSFormattedTextField1.setValue(new Integer(99999999));
        sSFormattedTextField2.setValue(new String("nombre de la nueva cuenta"));
        sSFormattedTextField3.setValue(new String("ARS"));
        sSDateField1.setValue(new java.sql.Date(2000));
        sSDateField2.setValue(new java.sql.Date(2001));
        sSIntegerField1.setValue(new Integer(1));
        sSMemoField1.setText("Esta es una instruccion de ejemplo");
        sSBooleanField1.setSelected(true);
        
        /*
        try {
           
            rowset.updateInt(    "account_id"     , new Integer(999999));
            rowset.updateString( "account_name"   , "Nueva Cuenta");
            rowset.updateString( "currency_id"    , "ARS");
            rowset.updateString( "instructions"   , "Aqui van las instrucciones");
            rowset.updateInt(    "account_type_id", new Integer(1));
            rowset.updateBoolean("is_enabled"     , false);
            rowset.updateDate(   "valid_since"    , new java.sql.Date(1000));
            rowset.updateDate(   "valid_until"    , new java.sql.Date(1001));
            rowset.insertRow();
            rowset.moveToCurrentRow();
             
            System.out.println("defaults setted");
                    
        } catch (java.sql.SQLException se) {
            System.out.println("performPreInsertOps ---> Exception : " + se);
        }
         */
    }
    
    public void performPreDeletionOps() {
    }
    
    public void performPostInsertOps() {
        System.out.println("performPostInsertOptions");
    }
    
    public void performPostDeletionOps() {
    }
    
    public void performCancelOps() {
    }
    
}
