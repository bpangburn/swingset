package samples;

import com.nqadmin.swingSet.formatting.helpers.SelectorListModel;
import com.nqadmin.swingSet.formatting.helpers.SelectorPopupPanel;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.ListModel;


/**
 *
 * @author  dags
 */
public class SelectorExample01 extends javax.swing.JPanel implements ActionListener {
    
    private ListModel model = null;
    
    public SelectorExample01() {
        initComponents();
        ((SelectorListModel)selectorListModel1).setFilterEdit(jTextField1);
    }
    
    public SelectorExample01(javax.swing.ListModel model) {
        initComponents();
        ((SelectorListModel)model).setFilterEdit(jTextField1);
        selectorList1.setModel(model);
    }
    
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        sSConnection1 = new com.nqadmin.swingSet.datasources.SSConnection();
        selectorListModel1 = new com.nqadmin.swingSet.formatting.helpers.SelectorListModel();
        jScrollPane1 = new javax.swing.JScrollPane();
        selectorList1 = new com.nqadmin.swingSet.formatting.helpers.SelectorList();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();

        sSConnection1.setDriverName("org.postgresql.Driver");
        sSConnection1.setUrl("jdbc:postgresql://pgserver.greatmindsworking.com/suppliers_and_parts");
        sSConnection1.setUsername("swingset");
        sSConnection1.setPassword("test");
        
        try {
            sSConnection1.createConnection();
        }
        catch (java.lang.ClassNotFoundException nf) {
            System.out.println(nf);
        }
        catch (java.lang.Exception ex) {
            System.out.println(ex);
        }
        selectorListModel1.setDataColumn("supplier_id");
        selectorListModel1.setListColumn("supplier_name");
        selectorListModel1.setOrderBy("supplier_name");
        selectorListModel1.setSsConnection(sSConnection1);
        selectorListModel1.setTable("supplier_data");
        selectorListModel1.refresh();

        setLayout(new java.awt.BorderLayout());

        setBorder(new javax.swing.border.TitledBorder("Selector"));
        setFocusable(false);
        jScrollPane1.setFocusable(false);
        selectorList1.setModel(selectorListModel1);
        selectorList1.setDoubleBuffered(true);
        jScrollPane1.setViewportView(selectorList1);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Selection Example");
        add(jLabel1, java.awt.BorderLayout.NORTH);

        jTextField1.setColumns(20);

        add(jTextField1, java.awt.BorderLayout.SOUTH);

    }

    public void setModel(javax.swing.ListModel model) {
        this.model = model;
        selectorList1.setModel(this.model);
    }
    
    // Variables declaration - do not modify
    javax.swing.JLabel jLabel1;
    javax.swing.JScrollPane jScrollPane1;
    javax.swing.JTextField jTextField1;
    com.nqadmin.swingSet.datasources.SSConnection sSConnection1;
    com.nqadmin.swingSet.formatting.helpers.SelectorList selectorList1;
    com.nqadmin.swingSet.formatting.helpers.SelectorListModel selectorListModel1;
    // End of variables declaration
    
    public static void main(String args[]) {
        // ejemplo
        javax.swing.JFrame frame = new javax.swing.JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new java.awt.BorderLayout());
        frame.getContentPane().add(new SelectorExample01(), java.awt.BorderLayout.CENTER);
        frame.setSize(640, 480);
        frame.setVisible(true);
    }
    
    private void setFilter() {
        
        SelectorListModel model = null;
        String[] newFilter = new String[1];
        newFilter[0] = jTextField1.getText().toUpperCase();
        model = (SelectorListModel)(selectorList1.getModel());
        model.setFilterText(newFilter);
        
        this.selectorList1.setModel(this.selectorListModel1);
        this.selectorList1.repaint();
        
    }

    public void actionPerformed(java.awt.event.ActionEvent e) {
    }
}
