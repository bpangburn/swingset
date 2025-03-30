package snippet_files;


import java.sql.Connection;
import java.util.List;

import javax.sql.RowSet;
import javax.sql.rowset.JdbcRowSet;
import javax.swing.JFrame;

import com.nqadmin.swingset.SSComboBox;
import com.nqadmin.swingset.SSDBComboBox;
import com.nqadmin.swingset.core.ComboBox1;
import com.nqadmin.swingset.core.Item1;
import com.nqadmin.swingset.navigate.RowsModel;

/**
 * javadoc examples.
 */
@SuppressWarnings("serial")
public class ComboBoxSnippets extends JFrame
{
	RowsModel rowsModel;
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
			
			// RowsModel does execute() and next() on the rowSet.
			// If you are not using the RowsModel you have to include:
			//     rowSet.execute();
			//     rowSet.next();
			rowsModel = RowsModel.create(rowSet);
			
			// Query for the combobox to map part_id to part_name.
			String query = "SELECT * FROM part_data;";
			
			// Create an instance of the SSDBComboBox with the connection object,
			// query, and column names.
			combo = new SSDBComboBox(connection, query, "part_id","part_name");
			
			// Execute the query.
			combo.execute();
			
			// This basically specifies the column and the rowset where updates have
			// to be made.
			combo.bind(rowsModel,"part_id");
			
		} catch (Exception ex) {
			// Exception handler here...
		}
		
		// Add the ssdbcombobox to the JFrame.
		getContentPane().add(combo);
	}
	// @end region=init

	@SuppressWarnings("unused")
	void autoGen() {
		// @start region=auto_gen
		SSComboBox combo = new SSComboBox();
		List<String> options = List.of("111", "2222", "33333");
		combo.setDisplayValues(options);
		// @end region=auto_gen
	}

	RowSet rowSet;

	@SuppressWarnings("unused")
	void customKey() {
		// @start region=custom_key
		SSComboBox combo = new SSComboBox();
		List<String> options = List.of("111", "2222", "33333");
		List<Integer> mappings = List.of(1, 5, 7 );
		combo.setDisplayValues(options, mappings);
		
		// Next line is assuming rowsModel has been initialized
		// and "my_column" is a column in it's rowSet.
		combo.bind(rowsModel, "my_column");
		// @end region=custom_key
	}

	/** x */
	@SuppressWarnings("serial")
	// @start region=chosen_item
	public class ComboBoxIntString extends ComboBox1<Integer, String> {
		public static class Item extends Item1<Integer, String> {
			public Item(Integer getKey, String getDisplayValue) {
				super(getKey, getDisplayValue);
			}
		}
		@Override
		public Item getChosenItem() {
			return new Item(getChosenKey(), getChosenDisplayValue());
		}
	}
	// @end region=chosen_item
}
