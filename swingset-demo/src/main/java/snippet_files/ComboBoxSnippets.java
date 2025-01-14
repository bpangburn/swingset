package snippet_files;


import java.sql.Connection;

import javax.sql.rowset.JdbcRowSet;
import javax.swing.JFrame;

import com.nqadmin.swingset.SSDBComboBox;
import com.nqadmin.swingset.navigate.NavigateActions;

/**
 * javadoc examples.
 */
@SuppressWarnings("serial")
public class ComboBoxSnippets extends JFrame
{
	NavigateActions navigator;
	SSDBComboBox combo;

	// @start region=init
	/**
	 * Create NavigateActions for shipment_data and ComboBox to select part_id.
	 * The ComboBox displays part_name and provides part_id.
	 * Add the comboBox to this JFrame.
	 * @param connection used by SSDBComboBox
	 * @param rowSet to connect to shipment_data
	 */
	void init(Connection connection, JdbcRowSet rowSet)
	{
		try {
			// Table to examine and traverse.
			rowSet.setCommand("SELECT * FROM shipment_data;");
			
			// NavigateActions does execute() and next() on the rowSet.
			// If you are not using the data navigator you have to include those.
			//     rowSet.execute();
			//     rowSet.next();
			navigator = NavigateActions.get(rowSet);
			
			// Query for the combobox to map part_id to part_name.
			String query = "SELECT * FROM part_data;";
			
			// Create an instance of the SSDBComboBox with the connection object,
			// query, and column names.
			combo = new SSDBComboBox(connection, query, "part_id","part_name");
			
			// Execute the query.
			combo.execute();
			
			// This basically specifies the column and the rowset where updates have
			// to be made.
			combo.bind(rowSet,"part_id");
			
		} catch (Exception ex) {
			// Exception handler here...
		}
		
		// Add the ssdbcombobox to the JFrame.
		getContentPane().add(combo);
	}
	// @end region=init
}
