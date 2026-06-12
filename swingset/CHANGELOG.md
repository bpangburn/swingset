# CHANGELOG.md

ChangeLog for the SwingSet Open Toolkit for Java Swing.

This file is the maintainer changelog for user-visible SwingSet changes. Dependency,
Maven, and plugin version changes beginning with SwingSet 4.0.11 are tracked separately
in [`CHANGELOG-POMS.md`](CHANGELOG-POMS.md).

## SwingSet 4.0.14 — Released TBD

1. Something goes here...

## SwingSet 4.0.13 — Released 2026-06-12

1. SwingSet 4.0.13 is primarily a release-preparation, dependency, build-tool, and
   documentation maintenance release. See [`CHANGELOG-POMS.md`](CHANGELOG-POMS.md) for
   dependency, Maven, and plugin version updates.
2. Added [`CHANGELOG-AI.md`](CHANGELOG-AI.md), an AI-assisted supplemental changelog
   summarizing the project history from the early CVS-converted releases through the
   current 4.0.x line.
3. Converted the maintainer changelog from `CHANGELOG.txt` to `CHANGELOG.md`.
4. Converted the POM/dependency changelog from `CHANGELOG-POMS.txt` to
   `CHANGELOG-POMS.md`.
5. Converted the FAQ from `FAQ.txt` to `FAQ.md` and updated FAQ wording, links, and
   formatting.
6. Added a top-level [`LICENSE.md`](../LICENSE.md) and removed duplicated module-level
   license text files.
7. Updated the top-level README and module README files for clearer Maven build,
   repository-structure, demo, and component documentation.
8. Removed the top-level `FOLDERS.txt` file after moving the useful repository-layout
   information into the top-level README.
9. Updated copyright years and license references in README documentation.
10. Updated Maven project versions from `4.0.12` to `4.0.13`.

## SwingSet 4.0.12 — Released 2023-12-16

1. SwingSet 4.0.12 is an update for dependencies and plugins. See
   [`CHANGELOG-POMS.md`](CHANGELOG-POMS.md).

## SwingSet 4.0.11 — Released 2023-02-07

1. Due to the many version changes on an ongoing basis, starting with SwingSet 4.0.11,
   POM changes have been moved to [`CHANGELOG-POMS.md`](CHANGELOG-POMS.md) in the same folder.
2. Fix for component updates following navigation:
   <https://github.com/bpangburn/swingset/issues/144>
3. `SSComboBox` / `SSDBComboBox`: ability to display the underlying database value when
   a combo box does not have a corresponding option:
   <https://github.com/bpangburn/swingset/issues/145>
4. Add `updateSSDBComboBoxes` to `SSDataGridScreenHelper`:
   <https://github.com/bpangburn/swingset/issues/153>
5. Fix for `SSTableModel` draw after a deletion:
   <https://github.com/bpangburn/swingset/issues/156>
6. Fix for updating the default parent ID when using `SSDataGridScreenHelper`:
   <https://github.com/bpangburn/swingset/issues/157>
7. Add screen-helper method to combine `setParentID()` and `updateScreen()`:
   <https://github.com/bpangburn/swingset/issues/159>
8. Add `Example4 Advanced` to the SwingSet Demo:
   - Move code from `Example4` that demonstrates navigator actions:
     - Assigning actions to function keys.
     - Assigning actions to buttons.
   - Add code showing use of `MissingOptionControl` and custom `ListItemFormat` for
     null/missing options.
9. Add `DataGridExampleSupport`, used by SwingSet Demo `Example5`:
   - Provides more testing/examples of grid usage.
   - Improves H2 handling so the demo can now do row insertion/deletion into the grid
     (note that this exposed item 5 above).
10. Add ability to use the `JAVA_PREFERRED_SCREEN` environment variable to control
    placement of the SwingSet demo in a multi-monitor environment.
11. SwingSet Demo button, screen name, and README cleanup.

## SwingSet 4.0.10 — Released 2022-05-31

1. Remove/deprecate `isGridCellEditable()` from `SSDataGridHelper`:
   <https://github.com/bpangburn/swingset/issues/128>
2. `SSDataGridHelper`: unable to set the size of the data grid when it is added to
   another component:
   <https://github.com/bpangburn/swingset/issues/129>
3. Updated Dependency Check to 7.1.0.
4. Updated H2 Database to 2.1.212.
5. Updated Maven Failsafe Plugin to 3.0.0-M6.
6. Updated Maven Surefire Plugin to 3.0.0-M6.
7. Issue #134: fix display of `Example4UsingHelper` and `Example7UsingHelper`.
8. Issue #131: `SSComboBox` causes `IllegalArgumentException`.
9. Issue #132: `SSCheckBox` throws a `NullPointerException` when the column value is null.
10. Issue #133: `SSList` causes a `NullPointerException` when the database value is null.
11. Public visibility for `updateScreen()` and `getParentID()` / `setParentID()` in
    screen-helper classes.
12. `SSList`: use default mapping type of `JDBCType.INTEGER` rather than `JDBCType.NULL`
    in the empty constructor.
13. Component null pointer fixes:
    <https://github.com/bpangburn/swingset/issues/139>
14. Use `Format` for `SSListItemFormat` for all types. See PR:
    <https://github.com/bpangburn/swingset/pull/138>
    - Each `JDBCType` can have a default `Format`.
    - Each `SSListItem` element can have a `Format`.
    - Use of a format string is deprecated. It still works and is now built on the new
      `Format` mechanisms.
    - Previously only date/time types could have a format string; everything else used
      `toString()`.
15. Issue #140: `SSDateField` bind fails when there are no records in the rowset.
    See commit to `SSFormattedTextField`:
    <https://github.com/bpangburn/swingset/commit/e1d38b3>
16. Overloaded `SSDBComboBox.addOption()` to support multi-column combo boxes.
    See commit to `SSDBComboBox`:
    <https://github.com/bpangburn/swingset/commit/e1d38b3>
17. Issue #141: `SSTextField` date mask throws exceptions while composing the date.
18. Updated `junit-jupiter` dependencies from 5.8.2 to 5.9.0-M1.

## SwingSet 4.0.9 — Released 2022-03-16

1. Add `SSComponentInterface.getSSDataNavigator()` to allow all components to access the
   data navigator. This is deprecated because there should generally not be a need to
   access the data navigator now that screens can add their own mnemonics/hotkeys that
   work regardless of which field has focus.
2. Added Actions for `SSDataNavigator` buttons, allowing easier implementation of
   mnemonics/hotkeys using an `InputMap`. See:
   <https://github.com/bpangburn/swingset/pull/124>
3. Workaround for Log4j `LogManager.getLogger()` issue with the NetBeans GUI builder.
   See:
   - <https://github.com/bpangburn/swingset/pull/123>
   - <https://issues.apache.org/jira/browse/LOG4J2-3420>
   - <https://lists.apache.org/thread/sp2ngsp0qmhptvts2rwhprqfrb7f0bpl>
   - <https://lists.apache.org/thread/plkhc38f5g7cxgv6p53hg5s1k9x3mrvt>
   - <https://github.com/bpangburn/swingset/discussions/93#discussioncomment-2258596>
4. Minor code cleanup to eliminate some Eclipse warnings.
5. `SSDataGrid` container bug. See:
   <https://github.com/bpangburn/swingset/issues/121>
6. Updated Dependency Check Maven Plugin to 7.0.0.
7. Updated Log4j to 2.17.2.
8. Added variable and updated Versions Maven Plugin to 2.10.0.
9. Updated Maven Deploy Plugin to 3.0.0-M2.
10. Updated Maven Site Plugin to 3.11.0.
11. Updated Maven Compiler Plugin to 3.10.1.
12. Updated Maven JAR Plugin to 3.2.2.
13. Updated Maven Javadoc Plugin to 3.3.2.
14. Updated Nexus Staging Maven Plugin to 1.6.12.

## SwingSet 4.0.8 — Released 2022-02-10

1. `SSFormattedTextField`: added setter/getter for the background color used when focus
   is gained.
2. `SSFormattedTextField`: added logic to immediately return from the property change
   listener when `setValue()` is called from the input verifier.
3. `SSCommon`: fixed minor logging inconsistency when the RowSet listener was added
   versus removed.
4. Updated Log4j to 2.17.1.
5. Updated H2 to 2.0.204. Added type for `ARRAY` in the demo (`INTEGER`) as required
   by H2 2.x+.
6. Updated H2 to 2.1.110.

## SwingSet 4.0.7 — Released 2021-12-14

1. Updated Log4j dependencies to 2.16.0 to further address CVE-2021-44228.
2. Override the default implementation of `getAllowNull()` in `SSBaseComboBox` to take
   combo-box navigator behavior into account.

## SwingSet 4.0.6 — Released 2021-12-10

1. Updated Log4j dependencies to 2.15.0 to address CVE-2021-44228.
2. Updated `junit-jupiter-api` dependency to 5.8.2.

## SwingSet 4.0.5 — Released 2021-11-22

1. Updated POM dependencies.
2. Fix for issue #95 where adding a record to an empty rowset throws an exception.

## SwingSet 4.0.4 — Released 2021-03-25

1. `SSBaseComboBox.updateOption()`: combo editor was not updating consistently.
   See <https://github.com/bpangburn/swingset/issues/85>
2. Updated Log4j and JUnit Maven dependencies.
3. Added explicit Maven plugin versions in POMs to eliminate warnings.
4. Initial fix for issue #46, moving `updateSSComponent` out of `SSComboBox` and
   `SSDBComboBox`.
5. `SSTableKeyAdapter`: updated `getObjectToSet` to use `Date.valueOf` for converting
   string values to date objects. See:
   <https://github.com/bpangburn/swingset/issues/89>
6. `SSSyncManager`: call `updatePresentRow()` on `SSDataNavigator` in the combo
   `ActionListener` only when row navigation is needed.
7. Demo: logging level set to `DEBUG` by default.
8. Demo: `Example4UsingHelper` updates the combo navigator dynamically when Part Name
   is modified.

## SwingSet 4.0.3 — Released 2021-02-22

1. `SSBaseComboBox`: remove check against `getBoundColumnText()` because the rowset was
   not updating as expected. New value from `setBoundColumnText()` is not reflected in
   `getBoundColumnText()` until after the call to `updateRow()`.
2. Add auto commit to the Add button to be consistent with other buttons. `dBNav.performPreInsertOps()`
   had to be placed in `SwingUtilities.invokeLater()` or the insert row would show
   values from the newly committed record.

## SwingSet 4.0.2 — Released 2021-02-18

1. `SSDBComboBox`: added constructor without `_query` parameter.
2. `SSFormViewScreenHelper` API change, replacing:

   ```java
   protected abstract void retrieveAndSetNewPrimaryKey();
   ```

   with:

   ```java
   protected abstract String retrieveNewPrimaryKey();
   ```

3. Moved `addSSComponentListener()` and `removeSSComponentListener()` to `SSCommon`,
   making companion methods in `SSComponentInterface` static.
4. Added abstract method `EventListener getSSComponentListener()` to
   `SSComponentInterface` and implemented it in applicable SwingSet components.
5. Component change listener for binding is stored as a private data member in `SSCommon`
   and initialized in the `bind()` method.

## SwingSet 4.0.1 — Released 2021-02-04

1. Changed some screen-helper class methods from public to protected.
2. Changed abstract `getJMenuBar()` to `getCustomMenu()` in `SSScreenHelperCommon` to
   avoid a naming collision. Updated demos.
3. Added static menu-helper methods to `SSScreenHelperCommon`.
4. Added `setSelectedEnum()` to `SSComboBox`.
5. Added some Log4j warnings to `SSDBComboBox.addOption()` and `updateOption()`.

## SwingSet 4.0.0 — Released 2021-01-31

1. Demo: moved instantiation of `MainClass` to `SwingUtilities.invokeLater()`.
2. Moved recurring data members and logic to `SSCommon`.
3. Established `SSComponentInterface`, implemented by all SwingSet components, to
   eliminate redundant code.
4. Eliminated bound `JTextField` / `DocumentListener` handling for components not based
   on `JTextComponent`.
5. Generic `SSDocumentListener` in `SSCommon` deals with multiple calls better by moving
   logic to `changedUpdate()` and `SwingUtilities.invokeLater()`.
6. Added `allowNull` flag to `SSCommon` to allow users to leave certain components
   unselected, such as combo boxes, and write null back to the rowset bound column.
7. Added Log4j support.
8. Replaced dependency on `com.sun.rowset` with `com.nqadmin.rowset` using the new
   `jdbcrowsetimpl` Maven artifact. See:
   <https://github.com/bpangburn/jdbcrowsetimpl>
9. Moved `SSListItem` to `com.nqadmin.swingset.utils`.
10. Deleted `com.nqadmin.swingset.formatting.helpers`, which only contained classes no
    longer used for `SSDBComboBox` filtering.
11. Deprecated `SSBooleanField`, `SSImageField`, `SSMemoField`, and `SSStringField` in
    the formatting package.
12. Added line wrapping to `SSTextArea`.
13. Added preliminary support for the `JDBCType` enum.
14. Demo: MySQL support.
15. Deprecated `SSConnection`, `SSRowSet`, and `SSJdbcRowSetImpl`. Now using
    `java.sql.Connection`, `javax.sql.RowSet` with `com.nqadmin.rowset.JdbcRowSetImpl`,
    and `com.nqadmin.swingset.datasources.RowSetOps`, which is a utility class with
    rowset convenience methods.
16. For GlazedLists, restored normal `JComboBox` behavior of not going past the first or
    last item.
17. Added `SSFormViewScreenHelper` and `SSDataGridScreenHelper` to the `utils` package
    to assist with standardized screen creation.
18. Added GlazedLists support to `SSComboBox`, enabled by default.
19. Added support for null/empty first item in combo boxes via special `nullItem`.
20. Added `hasItems()` method to combo boxes to determine when a combo box has no items,
    excluding the null first item.
21. Demo: added new screens that exercise all available components.

## SwingSet 3.0.0 — Released 2020-01-10

1. Changed package from `com.nqadmin.swingSet` to `com.nqadmin.swingset`.
2. Migrated to Maven project.
3. Separated demo.
4. Created POM files.
5. Javadoc updates.
6. Updated POM for deployment to Maven Central via Nexus OSSRH.

## SwingSet 2.3.1 — Released 2019-11-11

1. `SSDataNavigator.setSSRowSet()` only enables add/delete if `this.modification == true`.

## SwingSet 2.3.0 — Released 2019-11-09

1. Removed BeanInfo and `SSFormattedComboBox` classes, moved to archive.
2. Addressed additional Eclipse compiler warnings.
3. Addressed additional Eclipse Javadoc warnings.
4. Made many method parameters final.
5. Allowed Eclipse to format code when saving a class.
6. Removed unqualified data-member access, for example using `this.x`.
7. Made inner classes protected rather than private.
8. Eliminated `SSRowSetAdapter` and `SSConnectionEditor`.
9. Removed Bean property code from `com.nqadmin.swingSet.datasources` classes.
10. Rewrote `SSRowSet` to extend `RowSet` and added default methods.
11. Rewrote `SSJdbcRowSetImpl` to extend `JdbcRowSetImpl` and implement `SSRowSet`.
12. Rewrote `SSDBNav` to add default methods from `SSDBNavAdapter`.
13. Deprecated `SSDBNavAdapter`.
14. Deprecated `SSDBNavImp` and replaced it with `SSDBNavImpl`.
15. Updated `@deprecated` Javadoc annotation, providing links to suggested replacements.
16. Removed `SSDBCheckBox`, deprecated long ago.
17. Fixed error in `SSDBNavImpl` when trying to clear fields for a new record.
18. Fixed `SSDataNavigator` to eliminate some redundant code and better support H2.
19. Example code cleanup and documentation.

## SwingSet 2.2.0 — Released 2019-02-27

1. Updated code to support Java generics.
2. Addressed various Eclipse compilation and Javadoc warnings.
3. Set build source and target to Java 8 / 1.8.
4. Cleaned up licensing and file-header consistency using the Eclipse Copyright Generator.

## SwingSet 2.1.0 — Released 2018-09-18

1. Fixed first/last buttons not being enabled/disabled correctly when navigation happens
   directly on the rowset.
2. Fixed newly added item not being displayed in `SSDBComboBox`.
3. Fixed `NullPointerException` in `SSDataGrid` when users enter an empty string in a
   date column.
4. Fixed exception in `SSList` while updating display if the underlying rowset has no
   records.
5. In `SSSyncManager`, update the navigator inside the combo box listener only if the
   combo box has focus, meaning `actionPerformed` is due to user action.
6. Use a locking mechanism while making changes to items in `SSDBComboBox`.
7. Made updates/fixes to various build scripts.
8. Updated project support email address in project docs.
9. Added `populate.sql` used for sample/demo to the project. It was formerly available
   only online.
10. Corrected instructions for sample/demo.
11. Updated GlazedLists to 1.11.0.
12. Updated H2 used for sample/demo to 1.4.197.

## SwingSet 2.0.0 — Released 2012-08-10

### Classes or interfaces added

- `SSDataGridAdapter`
- `SSDataGridHandler`
- `SSList`
- `SSArray`

### Changes

#### `SSCheckBox`

1. Added constructor that takes text for the check box.
2. Removed the preferred-size setting.
3. Changed the way focus is transferred for Enter key.

#### `SSComboBox`

1. Changed the way focus is transferred for Enter key.
2. Added `updateDisplay(boolean useTextField)`, which can be used in `setSelectedValue`.
3. Uses listener on rowset to update the value in the combo box. Does not use the
   listener on `SSTextDocument` anymore. As a result, removed the deprecated constructor
   that takes `SSTextDocument`.

#### `SSDataGrid`

1. Added `SSDataGridHandler`.
2. Disabled focus traversal keys in default editor and date editor, allowing Tab to move
   to the next cell while editing.
3. When using a combo renderer and the underlying value is null, return index `-1`.
4. Added ability to specify whether deletions should be allowed.

#### `SSDataNavigator`

1. Reset `onInsertRow` flag when a new rowset is set.
2. Added conditional call to `doCommitButtonClick` in `updatePresentRow` to make sure
   the navigator is on a valid row.
3. Added try/catch in `updatePresentRow`, including when calling `doCommitButtonClick`.
4. When on insert row and `allowInsertion` returns false, stay in insertion row rather
   than calling `moveToCurrentRow()`.
5. In `updatePresentRow`, `allowUpdate` was not called before `updateRow()`.
6. Modified code to call `allowUpdate` and `performPostUpdateOps` in `updatePresentRow`.
7. While deleting the row, check `confirmDeletes` to display the confirmation dialog.

#### `SSDBComboBox`

1. Added a text filter for items in the combo box. `setFilterable()` can turn the filter
   on or off.
2. Changed the way focus is transferred for Enter key.
3. In key listener, ignore function keys.

#### `SSDBNavImp`

1. Set default value to null if it is an instance of `SSFormattedTextField`.

#### `SSTableKeyAdapter`

1. When pasting, create object only when string value is not null.

#### `SSTableModel`

1. Call functions on the data grid handler.
2. When the underlying column is timestamp, use `updateTimestamp` rather than `updateDate`.
3. `getValueAt` now returns null if a given column has null.
4. It previously returned `0` for numeric fields because `getInt` and `getDouble` return
   `0` for null columns.
5. Trim spaces on either side to avoid `NumberFormatException` when copying data to the
   spreadsheet.

#### `SSTextArea`

1. Removed the `init` method that set preferred size, which caused problems with the
   scroll pane.

#### `SSTextDocument`

1. When the underlying column is timestamp, use `updateTimestamp` rather than `updateDate`.
2. `getText` now returns null if `getObject` returns null.

#### `SSTextField`

1. Changed the way focus is transferred for Enter key.

#### `SSJdbcRowSetImpl`

1. Added get/update methods for `Array`.
2. Added get/update methods for `Time` and `Timestamp`.
3. Added get/set methods for object.
4. Added `rowDeleted`, `rowInserted`, and `rowUpdated` methods.
5. Added `getMetaData` method.

#### `SSJdbcRowSetImplBeanInfo`

1. Property descriptor for command was named query; changed it to command.

#### `SSRowSet`

1. Added get/update methods for `Array`.
2. Added get/update methods for `Time` and `Timestamp`.
3. Added get/set methods for object.
4. Added `rowDeleted`, `rowInserted`, and `rowUpdated` methods.
5. Added `getMetaData` method.

#### `SSRowSetAdapter`

1. Added get/update methods for `Array`.
2. Added get/update methods for `Time` and `Timestamp`.
3. Added get/set methods for object.
4. Added `rowDeleted`, `rowInserted`, and `rowUpdated` methods.
5. Added `getMetaData` method.

#### `SelectorComboBoxModel`

1. Modified to support navigation in the `SSDBComboBox` filter.
2. Changed parameter name in Javadoc.

#### `SSFormattedTextField`

1. In `DbToFm`, changed function calls from `this.setValue` to `super.setValue` because
   `setValue` is now overridden in this class. Also changed this in a couple of other
   functions.
2. Deprecated `updateValue` and added `setValue`.
3. When the underlying value is null, set component value to null.

## SwingSet 1.0.0-PR4 — Released 2006-06-06

### `SSDataNavigator`

1. Fixed check for `confirmDeletes` flag during row deletion.

## SwingSet 1.0.0-PR3 — Released 2006-05-15

### Classes added

1. `SSCuitField`
2. `SSImageField`
3. `SSStringField`
4. `SSTimeField`
5. `SSTimestampField`

### `SSDBNav`

1. Added four new functions to the interface:
   - `allowInsertion`
   - `allowDeletion`
   - `allowUpdate`
   - `performPostUpdateOps`

### `SSDBNavAdapter`

1. Added default implementations for `allowInsertion` and `allowDeletion`. Both return
   true by default.
2. Added default implementations for `allowUpdate` and `performPostUpdateOps`.

### `SSDataNavigator`

1. Call `allowInsertion` and `allowDeletion` prior to adding or deleting rows.
2. In `updatePresentRow`, check the modification variable before updating the row.
3. Added calls to `allowUpdate` and `performPostUpdateOps`.

### `SSFormattedTextField`

1. Added `updateValue` to programmatically change the value in the field. This was needed
   because `setText` will not work with formatting.

### `SSDateField`

1. Added constructor to take the date format as an argument.
2. Added two constants to represent date formats.

### `SSDateFormatterFactory`

1. Added constructor to take the date format as an argument.
2. Added two constants to represent date formats.

### `SSTextField`

1. Transfer focus on key released event of `VK_ENTER` rather than key pressed.

## SwingSet 1.0.0-PR2 — Released 2005-03-09

### `SSDBNavImp`

1. Added support for `JScrollPane` in `setComponents`. This should clear components in
   `JScrollPane`.

### `SSDataNavigator`

1. Updated the undo button listener. Based on insert-row flag, call `moveToCurrentRow()`.
2. Update current row number in undo button listener.

### `SSDataGrid`

1. Added custom editor to numeric, string, and object type columns.
2. Modified `ComboRenderer` to extend `DefaultCellRenderer.UIResource`.
3. Added `setSurrendersFocusOnKeystroke(true)` to `init()` to force `JTable` to surrender
   to the editor when keystrokes activate the editor.

### `SSTableModel`

1. Added `TIMESTAMP` to handled column types. Timestamp is handled the same way as date.

### `SSTextDocument`

1. Added `TIMESTAMP` to handled SQL types. Timestamp is handled the same way as date.

## SwingSet 1.0.0-PR1 — Released 2005-02-21

### Classes or interfaces added

1. `SSCheckBox`, added to replace deprecated `SSDBCheckBox`.
2. `SSLabel`, a new read-only label component based on `JLabel`.
3. `SSSlider`, a new slider component based on `JSlider`.
4. `SSImage`, a new image component capable of storing, retrieving, and displaying GIF
   and JPEG files.
5. BeanInfo support classes for managing bean-related properties. Currently these only
   handle icon display:
   - `SSCheckBoxBeanInfo`
   - `SSComboBoxBeanInfo`
   - `SSDataGridBeanInfo`
   - `SSDataNavigatorBeanInfo`
   - `SSDBComboBoxBeanInfo`
   - `SSImageBeanInfo`
   - `SSLabelBeanInfo`
   - `SSSliderBeanInfo`
   - `SSTextAreaBeanInfo`
   - `SSTextFieldBeanInfo`
   - `SSConnectionBeanInfo`
   - `SSJdbcRowSetImplBeanInfo`
6. `SSSyncManager`, added to `com.nqadmin.swingSet.utils` to simplify creation of screens
   with both combo-box-based and navigator-based navigation.

### `SSComboBox`

1. Rewrote to extend `JComboBox` rather than `JComponent`.

### `SSDBCheckBox`

1. Rewrote to extend `JComboBox` rather than `JComponent`.
2. Added support for boolean database columns. Tested only with PostgreSQL.
3. Updated Javadoc.
4. Deprecated in favor of `SSCheckBox` to match naming conventions.

### `SSDBComboBox`

1. Added support for non-numeric primary keys.

### `SSTextDocument`

1. Fixed boolean rowset update.

### All classes

1. Cleaned up entire API to make methods as consistent as possible across all components.
2. Deprecated methods that followed older naming conventions, such as `setRowSet()` rather
   than `setSSRowSet()`.
3. Made private data members or methods protected unless they were listener-related.
4. Added, debugged, and standardized Javadoc across all components, `readme.txt`,
   `FAQ.txt`, and `ChangeLog.txt`.
5. Converted all tabs to soft tabs comprised of four spaces.

## SwingSet 0.9.2-beta — Released 2004-11-01

### `SSJdbcRowSetImpl`

1. Fixed issue with `setCommand()` not updating underlying `JdbcRowSetImpl`.

### `SSTableModel`

1. Added support for `NUMERIC`.
2. Made type support consistent across SwingSet:
   - `INTEGER`, `SMALLINT`, `TINYINT` → `Integer`
   - `BIGINT` → `Long`
   - `FLOAT` → `Float`
   - `DOUBLE`, `NUMERIC` → `Double`
   - `BOOLEAN`, `BIT` → `Boolean`
   - `DATE` → `Date`
   - `CHAR`, `VARCHAR`, `LONGVARCHAR` → `String`
3. Added `this.fireTableDataChanged();` to `init()` to alert listeners to data changes.

### `SSTextDocument`

1. Added support for `NUMERIC`.
2. Made type support consistent across SwingSet using the same mapping as `SSTableModel`.

### All classes

1. Fixed Javadoc errors.

## SwingSet 0.9.1-beta — Released 2004-10-28

### `SSJdbcRowSetImpl`

1. Call `setType` and `setConcurrency` methods on the `JdbcRowSetImpl` instance variable.

## SwingSet 0.9.0-beta — Released 2004-10-26

### Classes or interfaces added

1. `SSConnection`
2. `SSRowSet`
3. `SSRowSetAdapter`
4. `SSJdbcRowSetImpl`

### `SSConnection`

Acts as a wrapper class for `Connection` and supports serialization.

### `SSRowSet`

Interface based on `RowSet`, with just the required functions. Also extends `Serializable`.

### `SSRowSetAdapter`

Provides empty functions for `SSRowSet` interface functions.

### `SSJdbcRowSetImpl`

Extends `SSRowSetAdapter`. Wrapper for `JdbcRowSetImpl` with support for serialization.

### Component changes

1. `SSComboBox`: use `SSRowSet` instead of `RowSet`.
2. `SSDBCheckBox`: use `SSRowSet` instead of `RowSet`.
3. `SSDBComboBox`: use `SSRowSet` instead of `RowSet`.
4. `SSDBComboBox`: use `SSConnection` instead of `Connection`.
5. `SSDataGrid`: use `SSRowSet` instead of `RowSet`.
6. `SSTableModel`: use `SSRowSet` instead of `RowSet`.
7. `SSDataNavigator`: use `SSRowSet` instead of `RowSet`.

## SwingSet 0.8.3-beta — Released 2004-10-22

### Classes or interfaces added

1. `SSTextArea`

### `SSDataGrid`

1. Use `SSTextField` as editor when a date editor is needed, rather than duplicating date
   mask code in `SSDataGrid`.
2. Added function to specify default column width for the grid.
3. Column width can be specified when requesting a combo renderer.
4. Added `getCellEditorValue` function for `DateEditor` subclass. Because of this, cell
   values for `Date` columns are returned as `Date` objects rather than strings. This may
   require changes in `SSCellEditing` implementations if `String` is expected as
   `_newValue` for date columns.
5. Call `createDefaultColumnModel` when a new rowset is specified. This fixes the problem
   of not displaying new columns added to the rowset.

### `SSTableModel`

1. Value for a `Date` column can be set using a `String` in `mm/dd/yyyy` or `yyyy-mm-dd`
   format, or a `Date` object. This helps copy/paste functionality with dates.

### `SSTextDocument`

1. Added support for `CHAR` types.

### `SSTextField`

1. Modified mask handling to handle more keys.
2. Enter key transfers focus to the next component regardless of whether a mask is used.
3. Added focus listener to select complete text when the text field receives focus.

## SwingSet 0.8.2-beta — Released 2004-09-21

### `SSDataNavigator`

1. Changed button names in code to more meaningful names, such as `button1` to `firstButton`.
2. Refresh was calling `next()` twice, causing problems when the rowset had just one record.
3. Move to the last record when the user presses commit to save the record, keeping the user
   in the added record.
4. Added rowset listener to keep track of the record number when the rowset is navigated
   outside the navigation bar.
5. Display exception messages if one occurs when a button is pressed.

### `SSComboBox`

1. Added support for mapping values in `setDisplay`.
2. Combo listener was not handling mapping values; added that support.
3. Added constant to indicate no selection in combo box. This value is returned when the
   selected index is `-1`.
4. Changed codes for `MALE` and `FEMALE` constants and changed the order in which they are
   added to the combo.

### `SSDBComboBox`

1. Update `numberOfItems` in `deleteItem` and `addItem`.

### `SSTextField`

1. Default mask is set to nothing. Previously it was `MMDDYYYY`.

## SwingSet 0.8.1-beta — Released 2004-08-13

### `SSComboBox`

1. When the selected index in the combo box is `-1`, set the database value to null.

### `SSDBComboBox`

1. When the selected index in the combo box is `-1`, set the database value to null.

### `SSDataNavigator`

1. When a record is deleted or added to the current rowset using delete/add buttons on the
   navigator, update the displayed row count.
2. Reduced the default button size and increased the size of the text field and label that
   display current row number and row count.

## SwingSet 0.8.0-beta — Released 2004-08-11

### Classes or interfaces added

1. `SSTableKeyAdapter`

### `SSCellEditingAdapter`

1. Implements `Serializable`.

### `SSComboBox`

1. Made all variables protected.
2. Added `getColumnName()`.
3. Added `getRowSet()`.
4. Added `setColumnName()`.
5. Added `setRowSet()`.
6. Added `setSelectedValue()`, which sets the selected item corresponding to a given
   value and changes the bound column value.
7. Added private `addComponent` and `removeListener` functions.
8. Inner listener classes implement `Serializable`.

### `SSDataGrid`

1. Added `getSelectedColumns()` and `getSelectedColumnCount()`, overriding `JTable`
   methods to avoid hidden columns being selected. This supports copy/paste behavior.
2. Added copy and paste support using `SSTableKeyAdapter`.
3. Default item selected in a combo box renderer when no default value is specified is
   now `-1`; previously it was the first item.

### `SSDataNavigator`

1. Added getter functions for rowset, button size, modification, deletion, insertion, and
   confirm deletes.
2. Added `rowCount` and `currentRow` variables to track current row number and total rows.
3. Added text field and label to display total rows and current record number.

### `SSDBCheckBox`

1. Added constructor taking a rowset and column name.
2. Added `bind` function taking a rowset and column name.
3. Added `getColumnName()`.
4. Deprecated `setTextField`, `getTextField`, and the constructor taking a text field.
5. Added key listener to transfer focus when Enter is pressed.

### `SSDBComboBox`

1. Deprecated `setTextField`.
2. Added `setSelectedValue()`.
3. Added key listener to transfer focus when Enter is pressed.
4. Remove items from combo before adding new items.

### `SSDBNavAdapter`

1. Implements `Serializable`.

### `SSDBNavImp`

1. Default item selected in combo box is now `-1` rather than `0`, so the column value can
   be null if a value is not selected.

### `SSTableKeyAdapter`

1. Added key listener to `JTable` and provides copy/paste support for the given table.

### `SSTableModel`

1. Made all variables protected.

### `SSTextDocument`

1. Added `setColumnName`, `setColumnIndex`, and `setRowSet` functions.
2. Added `getColumnName`, `getColumnIndex`, and `getRowSet` functions.
3. Added `bind` function to bind the text field to a column in the rowset.
4. Added transfer focus in key listener. Focus transfers to next component when Enter is
   pressed.

## SwingSet 0.7.0-beta — Released 2004-02-23

### `SSDataNavigator`

1. Reduced default button size to 50 × 20.
2. First and Last buttons are disabled when the rowset is at the first or last row,
   respectively.
3. Commit button is enabled all the time. When the rowset is on insert row, pressing
   commit inserts the row. When not on insert row, it updates the present row.
4. Added button graphics.
5. Added `setCallExecute()`. This can be used to indicate to the navigator that a MySQL
   database is being used. If set to false, the navigator will not call `execute()` on
   the specified rowset. See question #6 in the FAQ.

### `SSTextDocument`

1. In the rowset listener, replaced calls to `remove`, `removeUpdate`, `insertString`,
   and `insertUpdate` with a single call to `replace`. This appears to fix occasional
   `NullPointerException` problems when `SSTextDocument` is set to a `JTextArea` and the
   text contains new line characters.

### `SSComboBox`

1. Added `GENDER_OPTION`, which displays `Male` and `Female`.

## SwingSet 0.6.0-beta — Released 2003-12-18

For the 0.6.0 release of SwingSet, a grid control, `SSDataGrid`, was added. It can display
database information in a datasheet/spreadsheet-style view. `SSDataGrid` provides functions
to set column headers, hide columns, and make columns uneditable.

Individual columns in `SSDataGrid` can be displayed as text fields or combo boxes. For text
columns, editing masks can be specified. `SSDataGrid` uses `SSTableModel`, which extends
`AbstractTableModel`. The `SSCellEditing` and `SSDataValue` interfaces provide fine control
over grid behavior.

Also added in 0.6.0 is `SSTextField`, which extends `JTextField`. `SSTextField` provides
editing masks for data entry, such as dates, social security numbers, and specified number
of decimals.

### Classes or interfaces added

1. `SSCellEditing`
2. `SSCellEditingAdapter`
3. `SSDataGrid`
4. `SSDataValue`
5. `SSTableModel`
6. `SSTextField`

### New functions added to existing classes or interfaces

#### `SSComboBox`

1. `getSelectedIndex()` returns the index of the selected item in the combo box.
2. `getSelectedValue()` returns the value associated with the selected item.

#### `SSDataNavigator`

1. `setConfirmDeletes()`: if true, every time the delete button is pressed, the navigator
   pops up a confirmation dialog.
2. `updatePresentRow()`: updates the present row.

#### `SSDBCheckBox`

1. `execute()`: initializes the check box by getting the value from the text field.

#### `SSDBComboBox`

1. `addItem(String _name, long _value)`
2. `deleteItem(String _name)`
3. `deleteItem(long _value)`
4. `deleteItem(String _name, long _value)`
5. `updateItem(long _value, String _name)`

#### `SSDBNav`

1. `performCancelOps()`: called when the user is on the insert row and cancels the insert
   by clicking undo.
2. `performNavigationOps(int navigationType)`: called whenever navigation takes place.
3. `performRefreshOps()`: called when the user wants to refresh displayed information.

## SwingSet 0.5.0-alpha — Released 2003-09-26

Initial SourceForge release.
