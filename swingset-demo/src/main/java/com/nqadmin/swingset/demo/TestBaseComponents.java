/*******************************************************************************
 * Copyright (C) 2003-2021, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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
 *   Ernie R. Rael
 ******************************************************************************/
package com.nqadmin.swingset.demo;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.sql.RowSet;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import com.nqadmin.swingset.SSCheckBox;
import com.nqadmin.swingset.SSComboBox;
import com.nqadmin.swingset.SSDBComboBox;
import com.nqadmin.swingset.SSDBNav;
import com.nqadmin.swingset.SSDBNavImpl;
import com.nqadmin.swingset.SSDataNavigator;
import com.nqadmin.swingset.SSImage;
import com.nqadmin.swingset.SSLabel;
import com.nqadmin.swingset.SSList;
import com.nqadmin.swingset.SSSlider;
import com.nqadmin.swingset.SSTextArea;
import com.nqadmin.swingset.SSTextField;
import com.nqadmin.swingset.demo.datepicker.DbDatePicker;
import com.nqadmin.swingset.models.SSCollectionModel;
import com.nqadmin.swingset.models.SSDbArrayModel;
import com.nqadmin.swingset.navigate.RowsModel;
import com.nqadmin.swingset.utils.SSComponent;
import com.nqadmin.swingset.utils.SSSyncManager;
import com.nqadmin.swingset.utils.SSUtils;

import static com.nqadmin.swingset.demo.TestBaseComponents.CompDim.*;
import static com.nqadmin.swingset.demo.TestBaseComponents.Comps.*;
import static com.nqadmin.swingset.utils.SSUtils.sf;
import static java.lang.System.Logger.Level.*;



/**
 * This example demonstrates all of the Base SwingSet Components
 * except for the SSDataGrid.
 * <p>
 * There is a separate example screen to demonstrate the
 * Formatted SwingSet Components.
 * <p>
 * IMPORTANT: The SSDBComboBox and the SSRowSet queries should select the same
 * records and in the same order. Otherwise the SSSyncManager will spend a lot of
 * time looping through records to match.
 */

@SuppressWarnings("serial")
public class TestBaseComponents extends JFrame
{
	// STAR means use "*" in query
	enum Comps {
		NAV, PK, CHECK, COMBO, ENUM_COMBO, DB_COMBO, IMAGE, LABEL,
		LIST, SLIDER, TEXT_AREA, TEXT_FIELD, DATE_PICKER,
		STAR, // This, and anything after, are not components.
	};
	enum CompDim {
		//NORMAL, TALL, VERY_TALL
		H1, H2, H3
	}

	private record Comp(String col, SSComponent comp, JLabel label, CompDim dim){};
	private Map<Comps, Comp> compInfo = new EnumMap<>(Comps.class);
	private EnumSet<Comps> activeComps = EnumSet.allOf(Comps.class);

	 // Thing in this set will not have their preferred height made smaller.
	private EnumSet<Comps> keepMinHeight = EnumSet.of(
			// H1
			// Commnet out the next line to get original test behavior
			NAV, PK, CHECK, COMBO, ENUM_COMBO, DB_COMBO, LABEL, SLIDER, TEXT_FIELD,
			DATE_PICKER

			// H2
			// LIST, TEXT_AREA,

			// H3
			// IMAGE
	);

	private void populateComps()
	{
		// This list must be in the same order as the Comps enum.
		List<Comp> tComps = List.of(
				new Comp(null,              cmbSSDBComboNav,   lblSSDBComboNav,   H1),
				new Comp("swingset_base_test_pk", txtSwingSetBaseTestPK,
						 lblSwingSetBaseTestPK, H1),
				new Comp("ss_check_box",    chkSSCheckBox,     lblSSCheckBox,     H1),
				new Comp("ss_combo_box",    cmbSSComboBox,     lblSSComboBox,     H1),
				new Comp("ss_combo_box",    cmbEnumSSComboBox, lblEnumSSComboBox, H1),
				new Comp("ss_db_combo_box", cmbSSDBComboBox,   lblSSDBComboBox,   H1),
				new Comp("ss_image",        imgSSImage,        lblSSImage,        H3),
				new Comp("ss_label",        lblSSLabel2,       lblSSLabel,        H1),
				new Comp("ss_list",         lstSSList,         lblSSList,         H2),
				new Comp("ss_slider",       sliSSSlider,       lblSSSlider,       H1),
				new Comp("ss_text_area",    txtSSTextArea,     lblSSTextArea,     H2),
				new Comp("ss_text_field",   txtSSTextField,    lblSSTextField,    H1),
				new Comp("ss_date_field_null",dpDatePicker,    lblDatePicker,     H1)
		);

		for (Comps comp : Comps.values()) {
			if (comp == STAR)
				break;
			compInfo.put(comp, tComps.get(comp.ordinal()));
		}
	}

	/**
	 * Log4j2 Logger
	 */
    private static final Logger logger = SSUtils.getLogger();
	
	/**
	 * map of 'hints' contianing info on which collection model to use
	 */
	private final Map<String,Object> hints;

	/**
	 * combo and list items
	 */
	enum ComboEnum {
		A("Combo Enum 0"),
		B("Combo Enum 1"),
		C("Combo Enum 2"),
		D("Combo Enum 3");
		private final String displayVal;
		private ComboEnum(String _displayVal) {
			displayVal = _displayVal;
		}

		@Override
		public String toString() {
			return displayVal;
		}
	}
	/** only used manually for testing */
	enum ListEnum {
		A("List Enum 1"),
		B("List Enum 2"),
		C("List Enum 3"),
		D("List Enum 4"),
		E("List Enum 5"),
		F("List Enum 6"),
		G("List Enum 7");
		private final String displayVal;
		private ListEnum(String _displayVal) {
			displayVal = _displayVal;
		}
		
		@Override
		public String toString() {
			return displayVal;
		}
	}
	private static final String[] comboItems = {"Combo Item 0","Combo Item 1", "Combo Item 2", "Combo Item 3"};
	//private static final int[] comboCodes = {0,1,2,3};
	private static final Integer[] comboCodesIntegers = new Integer[] {0,1,2,3};
	private static final Object[] listCodes = {1,2,3,4,5,6,7};
	private static final String[] listItems = {"List Item 1","List Item 2", "List Item 3", "List Item 4", "List Item 5", "List Item 6", "List Item 7"};
	
	/**
	 * screen label declarations
	 */
	JLabel lblSSDBComboNav = new JLabel("SSDBComboNav"); // SSDBComboBox used just for navigation
	JLabel lblSwingSetBaseTestPK = new JLabel("Record ID");
	JLabel lblSSCheckBox = new JLabel("SSCheckBox");
	JLabel lblSSComboBox = new JLabel("SSComboBox");
	JLabel lblEnumSSComboBox = new JLabel("enumSSComboBox");
	JLabel lblSSDBComboBox = new JLabel("SSDBComboBox");
	JLabel lblSSImage = new JLabel("SSImage");
	JLabel lblSSLabel = new JLabel("SSLabel");
	JLabel lblSSList = new JLabel("SSList");
	JLabel lblSSSlider = new JLabel("SSSlider");
	JLabel lblSSTextArea = new JLabel("SSTextArea");
	JLabel lblSSTextField = new JLabel("SSTextField");
	JLabel lblDatePicker = new JLabel("DbDatePicker");
	
	/**
	 * bound component declarations
	 */
	SSTextField txtSwingSetBaseTestPK = new SSTextField();
	SSCheckBox chkSSCheckBox = new SSCheckBox("labeled checkbox");
	SSComboBox cmbSSComboBox = new SSComboBox();
	SSComboBox cmbEnumSSComboBox = new SSComboBox();
	SSDBComboBox cmbSSDBComboBox = new SSDBComboBox();
	SSImage imgSSImage = new SSImage();
	SSLabel lblSSLabel2 = new SSLabel();
	final SSList lstSSList;
	SSSlider sliSSSlider = new SSSlider();
	SSTextArea txtSSTextArea = new SSTextArea();
	SSTextField txtSSTextField = new SSTextField();
	DbDatePicker dpDatePicker = new DbDatePicker();

	/**
	 * database component declarations
	 */
	Connection connection = null;
	SSDataNavigator navigator = null;
	RowsModel rowsModel;

	/**
	 * combo navigator and sync manger
	 */
	final SSDBComboBox cmbSSDBComboNav; // SSDBComboBox used just for navigation
	final SSSyncManager syncManager;

	RowSet getRowSet() {
		return rowsModel.getRowSet();
	}

	/**
	 * Method to obtain proper data structure/model for SSList based on database used
	 * @return collection model to use for lists based on underlying database
	 */
	private SSCollectionModel getCollectionModel() {
		@SuppressWarnings("unchecked")
		Supplier<SSCollectionModel> supl
				= (Supplier<SSCollectionModel>) hints.get("collectionModel");
		return supl == null ? new SSDbArrayModel(JDBCType.INTEGER) : supl.get();
	}

	/**
	 * Constructor for Base Component Test
	 * <p>
	 * @param _dbConn database connection
	 * @param _hints dynamic information on collection model, other
	 */
	@SuppressWarnings("LeakingThisInConstructor")
	public TestBaseComponents(final Connection _dbConn, Map<String, Object> _hints)
	{
		// SET SCREEN TITLE
		super("SwingSet Base Component Test");
		DemoUtil.initExampleFrame(this, null);
		
		// INITIALIZE SOME DYNAMIC INFORMATION
		hints =  _hints;
		lstSSList = new SSList(getCollectionModel());
		
		populateComps();
		//activeComps.remove(DATE_PICKER);
		
		//activeComps.removeAll(EnumSet.of(CHECK, LABEL));
		//activeComps.clear();
		//activeComps.addAll(EnumSet.of(
		//		STAR, PK, LABEL, LIST, TEXT_FIELD
		//		//NAV, LABEL, TEXT_FIELD
		//		// NAV, PK, CHECK, COMBO, ENUM_COMBO, DB_COMBO, IMAGE, LABEL,
		//		// LIST, SLIDER, TEXT_AREA, TEXT_FIELD
		//));
		
		// SET CONNECTION
		connection = _dbConn;
		
		// SET SCREEN DIMENSIONS
		setSize(MainClass.childScreenWidth, MainClass.childScreenHeightTall);
		
		// SET SCREEN POSITION
		setLocation(DemoUtil.getChildScreenLocation(this.getName()));
		
		// INITIALIZE DATABASE CONNECTION AND COMPONENTS
		try {
			RowSet rowset = DemoUtil.getNewRowSet(connection);
			String sql = sf("SELECT %s FROM swingset_base_test_data", getColumnsSQL());
			logger.log(INFO, sql);
			rowset.setCommand(sql);
			rowset.execute();
			rowsModel = RowsModel.create(rowset, createDbNav());
			navigator = new SSDataNavigator(rowsModel);
		} catch (final SQLException se) {
			logger.log(Level.ERROR, "SQL Exception.", se);
		}
		
		
		if (!activeComps.contains(NAV)) {
			cmbSSDBComboNav = null;
			syncManager = null;
		} else {
			// SETUP NAVIGATOR QUERY
			final String query = "SELECT * FROM swingset_base_test_data;";
			cmbSSDBComboNav = new SSDBComboBox(connection, query, "swingset_base_test_pk", "swingset_base_test_pk");
			try {
				cmbSSDBComboNav.execute();
			} catch (final SQLException se) {
				logger.log(Level.ERROR, "SQL Exception.", se);
			} catch (final Exception e) {
				logger.log(Level.ERROR, "Exception.", e);
			}
			
			// SETUP SYNCMANAGER, WHICH WILL TAKE CARE OF KEEPING THE COMBO NAVIGATOR AND
			// DATA NAVIGATOR IN SYNC.
			//
			// BEFORE CHANGING THE QUERY OR RE-EXECUTING THE QUERY FOR THE COMBO BOX,
			// YOU HAVE TO CALL THE .async() METHOD
			//
			// AFTER CALLING .execute() ON THE COMBO NAVIGATOR, CALL THE .sync() METHOD
			syncManager = new SSSyncManager(cmbSSDBComboNav, rowsModel);
			syncManager.setSyncColumnName("swingset_base_test_pk");
			syncManager.sync();
		}
		
		// SETUP COMBO AND LIST OPTIONS
		if (activeComps.contains(COMBO)) {
			// TODO if getAllowNull() is true then add blank item to SSComboBox
			cmbSSComboBox.setAllowNull(true);
			cmbSSComboBox.setDisplayValues(Arrays.asList(comboItems), Arrays.asList(comboCodesIntegers));
		}
		if (activeComps.contains(ENUM_COMBO)) {
			cmbEnumSSComboBox.setAllowNull(true);
			cmbEnumSSComboBox.setDisplayValues(ComboEnum.class);
		}
		
		// NOTE following enum has [0,N) mapping, but DB is [1,N]
		//      Fortunately test DB doesn't have a "7" in ss_list array
		//lstSSList.setDisplayValues(ListEnum.class);
		if (activeComps.contains(LIST)) {
			lstSSList.setDisplayValues(Arrays.asList(listItems), Arrays.asList(listCodes));
		}
		
		if (activeComps.contains(DB_COMBO)) {
			final String dbComboQuery = "SELECT * FROM part_data;";
			cmbSSDBComboBox = new SSDBComboBox(connection, dbComboQuery, "part_id", "part_name");
			cmbSSDBComboBox.setAllowNull(false);
			// TODO if getAllowNull() is false, user can still blank out the combo - we may want to prevent this
		}
		
		// SET SLIDER RANGE
		sliSSSlider.setMaximum(25);
		
		// SSComponents are setup, save info that may have changed.
		replaceComponent(NAV, cmbSSDBComboNav);
		replaceComponent(DB_COMBO, cmbSSDBComboBox);
		
		// Bind the components to their database columns.
		buildGui_bind();
		
		if (activeComps.contains(DB_COMBO)) {
			// Run db combo queries.
			try {
				cmbSSDBComboBox.execute();
			} catch (final SQLException se) {
				logger.log(Level.ERROR, "SQL Exception.", se);
			} catch (final Exception e) {
				logger.log(Level.ERROR, "Exception.", e);
			}
		}
		
		// Set the dimensions of the labels and components.
		buildGui_dim();
		
		JScrollPane lstScrollPane = null;
		if (activeComps.contains(LIST)) {
			// NEED TO MAKE SURE LIST IS TALLER THAN THE SCROLLPANE TO SEE THE SCROLLBAR
			lstSSList.setPreferredSize(new Dimension(MainClass.ssDimTall.width-20, MainClass.ssDimVeryTall.height));
			lstScrollPane = new JScrollPane(lstSSList);
			lstScrollPane.setPreferredSize(MainClass.ssDimTall);
		}
		
		// Setup the container and layout the components.
		final Container contentPane = getContentPane();
		contentPane.setLayout(new GridBagLayout());
		final GridBagConstraints constraints = new GridBagConstraints();
		
		// Add the components, there's a special case with the list scroll pane.
		buildGui_add(contentPane, constraints, lstScrollPane);

		constraints.gridx = 0;
		constraints.gridwidth = 2;
		contentPane.add(navigator, constraints);
		
		// Disable the primary key.
		txtSwingSetBaseTestPK.setEnabled(false);
		
		// Make the JFrame visible.
		setVisible(true);
		if (activeComps.contains(LIST)) {
			assert lstScrollPane != null;
			lstScrollPane.setPreferredSize(MainClass.ssDimTall);
		}
		pack();
	}

	private SSDBNav createDbNav() {
		/**
		 * Various navigator overrides needed to support H2
		 * <p>
		 * H2 does not fully support updatable rowset so it must be
		 * re-queried following insert and delete with rowset.execute()
		 */
		return new SSDBNavImpl(this)
		{
			/**
			 * Requery the rowset following a deletion. This is needed for H2.
			 */
			@Override
			public void performPostDeletionOps() {
				super.performPostDeletionOps();
				try {
					getRowSet().execute();
				} catch (final SQLException se) {
					logger.log(Level.ERROR, "SQL Exception.", se);
				}
				performRefreshOps();
			}
			
			/**
			 * Requery the rowset following an insertion. This is needed for H2.
			 */
			@Override
			public void performPostInsertOps() {
				super.performPostInsertOps();
				//TestBaseComponents.this.cmbSSDBComboNav.setEnabled(true);
				try {
					getRowSet().execute();
				} catch (final SQLException se) {
					logger.log(Level.ERROR, "SQL Exception.", se);
				}
				performRefreshOps();
			}
			
			/**
			 * Obtain and set the PK value for the new record & perform any other actions needed before an insert.
			 */
			@Override
			public void performPreInsertOps() {
				//
				// WHERE IS THE PRIMARY KEY SET? See example1
				//
				
				// SSDBNavImpl will clear the component values
				super.performPreInsertOps();
				
				setDefaultValues();
				
			}
			
			/**
			 * Manage sync manager during a Refresh
			 */
			@Override
			public void performRefreshOps() {
				super.performRefreshOps();
				if (syncManager == null)
					return;
				
				syncManager.async();
				try {
					cmbSSDBComboNav.execute();
				} catch (final SQLException se) {
					logger.log(Level.ERROR, "SQL Exception.", se);
				} catch (final Exception e) {
					logger.log(Level.ERROR, "Exception.", e);
				}
				syncManager.sync();
			}
			
			/**
			 * Re-enable DB Navigator following insertion Cancel
			 */
			@Override
			public void performCancelOps() {
				super.performCancelOps();
				if (cmbSSDBComboNav == null)
					return;
				
				cmbSSDBComboNav.setEnabled(true);
			}
		};
	}

	/** Some components aren't fully initialized until well after startup,
	 * so replace the component in the info. */
	private void replaceComponent(Comps eComp, SSComponent comp)
	{
		Comp info = compInfo.get(eComp);
		compInfo.put(eComp, new Comp(info.col, comp, info.label, info.dim));
	}

	private String getColumnsSQL()
	{
		if (activeComps.contains(STAR))
			return "*";
		List<String> l = getActiveCompInfo().stream()
				.filter((comp) -> comp.col != null)
				.filter((comp) -> comp.comp != cmbEnumSSComboBox) // skip dup column
				.map((comp) -> comp.col).collect(Collectors.toList());
		return String.join(", ", l);
	}

	/** For enabled components, return list of records. */
	private List<Comps> getActiveComps()
	{
		return activeComps.stream()
				.filter((eComp) -> eComp != STAR)
				.collect(Collectors.toList());
	}

	/** For enabled components, return list of records. */
	private List<Comp> getActiveCompInfo()
	{
		return getActiveComps().stream()
				.map((eComp) -> compInfo.get(eComp)).collect(Collectors.toList());
	}

	private void buildGui_bind()
	{
		for (Comp comp : getActiveCompInfo()) {
			if (comp.col != null)
				rowsModel.bind(comp.comp, comp.col);
		}
	}

	private void buildGui_dim()
	{
		for (Comps c : getActiveComps()) {
			Comp ci = compInfo.get(c);
			ci.label.setPreferredSize( switch (ci.dim) {
				case H1 -> MainClass.labelDim;
				case H2 -> MainClass.labelDimTall;
				case H3 -> MainClass.labelDimVeryTall;
			});

			Dimension targetDim = new Dimension(switch (ci.dim) {
				case H1 -> MainClass.ssDim;
				case H2 -> MainClass.ssDimTall;
				case H3 -> MainClass.ssDimVeryTall;
			});
			JComponent jc = (JComponent)ci.comp;
			if (keepMinHeight.contains(c)) {
				int curHeight = jc.getPreferredSize().height;
				if (curHeight > targetDim.height)
					targetDim.height = curHeight;
			}
			jc.setPreferredSize(targetDim);
		}
	}

	private void buildGui_add(Container contentPane, GridBagConstraints constraints, JScrollPane jspList)
	{
		constraints.gridx = 0;
		constraints.gridy = 0;

		for (Comp comp : getActiveCompInfo()) {
			constraints.gridx = 0;
			contentPane.add(comp.label, constraints);
			constraints.gridx = 1;
			if(comp.comp != lstSSList)
				contentPane.add((JComponent)comp.comp, constraints);
			else
				contentPane.add(jspList, constraints);
			constraints.gridy++;
		}
	}

	/**
	 * Method to set default values following an insert
	 */
	public void setDefaultValues() {

		// Get the new record id.
		try (ResultSet rs = connection
				.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)
				.executeQuery("SELECT nextval('swingset_base_test_seq') as nextVal;")) {
			rs.next();
			final int recordPK = rs.getInt("nextVal");
			txtSwingSetBaseTestPK.setText(String.valueOf(recordPK));
		} catch(final SQLException se) {
			logger.log(Level.ERROR, "SQL Exception occured during setting default values.",se);
		} catch(final Exception e) {
			logger.log(Level.ERROR, "Exception occured during setting default values.",e);
		}

		// SET OTHER DEFAULTS
//		chkSSCheckBox.setSelected(false);
//		cmbSSComboBox.setSelectedIndex(-1);
//		cmbEnumSSComboBox.setSelectedIndex(-1);
//		cmbSSDBComboBox.setSelectedIndex(-1);
//		imgSSImage.clearImage();
//		lblSSLabel2.setText(null);
//		lstSSList.clearSelection();
// TODO determine range for slider, 0 was not accepted
//		sliSSSlider.setValue(1);
//		txtSSTextArea.setText(null);
//		txtSSTextField.setText(null);


	}

}
