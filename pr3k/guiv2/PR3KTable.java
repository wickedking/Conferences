
package pr3k.guiv2;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

@SuppressWarnings("serial")
/**
 * A subclass of JTable to make the fields to be non-Editable.
 * An non-editable JTable.
 * 
 * @version November 2013
 * @author Cody Shafer
 */
public class PR3KTable extends JTable {
	
	/**
	 * Constructor for the table.
	 * @param data The data to input in the data.
	 * @param headers The column names.
	 */
	public PR3KTable(Object[][] data, String[] headers) {
		super(data, headers);
		this.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
	}

	@Override
	/**
	 * Override method for seeing if cell is editable. Returns false to deny editing.
	 */
	public boolean isCellEditable(final int the_row, final int the_column){
		return false;
	}

}
