import com.nqadmin.swingSet.formatting.helpers.SelectorListModel;
import tests.SelectorPopupPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * 
 * @author dags
 */
public class SelectorExample01 extends JFrame{

	private ListModel model = null;
	
	// Variables declaration - do not modify
	javax.swing.JLabel jLabel1;

	com.nqadmin.swingSet.datasources.SSConnection sSConnection1;

	com.nqadmin.swingSet.formatting.helpers.SelectorList selectorList1;

	com.nqadmin.swingSet.formatting.helpers.SelectorListModel selectorListModel1;

	// End of variables declaration
	
	public SelectorExample01() {
		initComponents();
	}

	private void initComponents() {
		java.awt.GridBagConstraints gridBagConstraints;

		sSConnection1 = new com.nqadmin.swingSet.datasources.SSConnection();
		sSConnection1.setDriverName("org.postgresql.Driver");
		sSConnection1.setUrl("jdbc:postgresql://pgserver.greatmindsworking.com/suppliers_and_parts");
		sSConnection1.setUsername("swingset");
		sSConnection1.setPassword("test");

		try {
			sSConnection1.createConnection();
		} catch (java.lang.ClassNotFoundException nf) {
			System.out.println(nf);
		} catch (java.lang.Exception ex) {
			System.out.println(ex);
		}
		selectorListModel1 = new com.nqadmin.swingSet.formatting.helpers.SelectorListModel();
		selectorListModel1.setDataColumn("supplier_id");
		selectorListModel1.setListColumn("supplier_name");
		selectorListModel1.setOrderBy("supplier_name");
		selectorListModel1.setSsConnection(sSConnection1);
		selectorListModel1.setTable("supplier_data");
		selectorListModel1.refresh();
		
		selectorList1 = new com.nqadmin.swingSet.formatting.helpers.SelectorList(selectorListModel1);

		jLabel1 = new javax.swing.JLabel();
		jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jLabel1.setText("Selection Example");

		
		setLayout(new java.awt.BorderLayout());

		setFocusable(false);

		add(selectorList1.getComponent(), java.awt.BorderLayout.CENTER);
		add(jLabel1, java.awt.BorderLayout.NORTH);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(640, 480);
		setVisible(true);
		
		selectorList1.addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				System.out.println("Selection: " + selectorList1.getSelectedValue());
			}
			
		});

	}

	public static void main(String args[]) {
		new SelectorExample01();
	}
}
