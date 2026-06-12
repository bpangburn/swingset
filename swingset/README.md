# SwingSet Library

SwingSet is an open source Java Swing toolkit that provides data-aware replacements for many standard Swing components. It is intended for Java desktop applications that bind Swing forms, navigators, grids, and supporting controls to JDBC `RowSet`-style data sources.

## Features

SwingSet includes:

- Data-aware replacements for common Swing components, including `JTextField`, `JTextArea`, `JComboBox`, `JCheckBox`, `JLabel`, `JSlider`, and `JFormattedTextField`.
- Combo box support for storing numeric or database-backed values while displaying user-friendly text.
- Database-driven combo boxes that can populate choices from query results and optionally support record navigation.
- A data-aware image component for loading, storing, and displaying database-backed image data.
- A graphical record navigator for traversal, insertion, deletion, commit, rollback, current-record display, and total-record display.
- A data grid component for datasheet, spreadsheet, and table-style views of query results.
- Formatted field helpers for values such as currency, percent, Social Security number, date, and related formats.

## Requirements

- Java 8 or later.
- Maven 3.9.6 or later is recommended for project builds.

## Maven dependency

After SwingSet is available from your Maven repository, add the library dependency to your application POM:

```xml
<dependency>
    <groupId>com.nqadmin.swingset</groupId>
    <artifactId>swingset</artifactId>
    <version>x.y.z</version>
</dependency>
```

Replace `x.y.z` with the SwingSet release version you want to use.

## Build from source

Clone the repository and build from the repository root:

```bash
git clone https://github.com/bpangburn/swingset.git
cd swingset
mvn clean package
```

To build only the core library module:

```bash
cd swingset
mvn clean package
```

For release-style local builds that attach source and Javadoc artifacts, use the release profile from the repository root or module directory:

```bash
mvn clean package -Prelease
```

Compiled JAR files are written to the relevant `target/` directory.

## Demo and examples

The companion [`swingset-demo`](../swingset-demo/) module contains runnable examples that demonstrate the core library components using an in-memory H2 database. See [`swingset-demo/README.md`](../swingset-demo/README.md) for demo build and execution instructions.

## Component overview

### Core SwingSet components

| Component | Description |
| --- | --- |
| `SSTextField` | Extends `JTextField` with `RowSet` binding and optional masks such as date and Social Security number formats. |
| `SSTextArea` | Extends `JTextArea` with `RowSet` binding. |
| `SSCheckBox` | Displays and updates boolean-style database values. It can bind to numeric or boolean columns. |
| `SSComboBox` | Displays text choices while storing mapped numeric values in a bound database column. |
| `SSDBComboBox` | Displays values from a database query while binding a selected key/value to another `RowSet`. Useful for lookup tables and foreign-key style selections. |
| `SSImage` | Loads, stores, and displays database-backed image data. |
| `SSLabel` | Extends `JLabel` with read-only `RowSet` binding. |
| `SSSlider` | Links a `JSlider` to a numeric database column. |
| `SSDataNavigator` | Provides record navigation, insertion, deletion, commit, and rollback controls for a `RowSet`. |
| `SSDataGrid` | Displays `RowSet` data in a table/grid view with support for hidden columns, read-only columns, renderers, validation hooks, row insertion, and row deletion. |

### Data source utilities

| Class | Description |
| --- | --- |
| `RowSetOps` | Utility methods for working with `RowSet`, `ResultSet`, metadata, and database-type conversions. |

### Helper classes

| Class | Description |
| --- | --- |
| `SSCommon` | Shared binding state and methods used by SwingSet components. |
| `SSComponentInterface` | Interface with default methods shared by most SwingSet components. |
| `SSDataGridScreenHelper` | Helper for constructing screens based on `SSDataGrid`. |
| `SSFormViewScreenHelper` | Helper for constructing form-view screens using SwingSet components. |
| `SSSyncManager` | Synchronizes a data navigator and a navigation combo box. |

### Supporting classes and interfaces

| Class or interface | Description |
| --- | --- |
| `SSDBNav` | Hook interface for custom operations before/after insert and delete operations. |
| `SSDBNavImpl` | Default `SSDBNav` implementation that clears or resets bound fields during insert operations. |
| `SSTableModel` | Table model used by `SSDataGrid`; can also be used directly with `JTable`. |
| `SSDataValue` | Interface used by `SSTableModel` to obtain new primary-key values for inserted rows. |
| `SSTableKeyAdapter` | Copy/paste support for `JTable` and `SSDataGrid`, including spreadsheet interoperability. |
| `SSCellEditing` | Validation/editability interface used by `SSTableModel` and `SSDataGrid`. |
| `SSCellEditingAdapter` | Convenience adapter for creating `SSCellEditing` implementations. |
| `SSDataGridHandler` | Hook interface for row deletion, insertion, and related grid operations. |
| `SSDataGridAdapter` | Convenience adapter for creating `SSDataGridHandler` implementations. |

## Basic examples

### `SSComboBox` value mappings

By default, `SSComboBox` maps displayed options to zero-based numeric values:

```java
SSComboBox combo = new SSComboBox();
String[] options = {"Yes", "No", "Maybe"};
combo.setOptions(options);

// Default mappings:
// "Yes"   -> 0
// "No"    -> 1
// "Maybe" -> 2
```

Custom mappings can be supplied before binding the combo box:

```java
SSComboBox combo = new SSComboBox();
String[] options = {"Yes", "No", "Maybe"};
int[] mappings = {1, 0, 2};

combo.setOptions(options, mappings);
combo.bind(myRowSet, "my_column");
```

### `SSDBComboBox` lookup binding

`SSDBComboBox` is useful when a screen should display friendly text from a lookup table while storing a key value in the bound table.

```java
RowSet rowSet = new JdbcRowSetImpl(connection);
rowSet.setCommand("SELECT * FROM shipment_data");

SSDataNavigator navigator = new SSDataNavigator(rowSet);

String query = "SELECT * FROM part_data";
SSDBComboBox combo = new SSDBComboBox(connection, query, "part_id", "part_name");
combo.bind(rowSet, "part_id");
combo.execute();

SSTextField quantity = new SSTextField();
quantity.bind(rowSet, "quantity");
```

## Notes for maintainers

- Keep the repository-level `README.md` short and use this module README for library-specific documentation.
- Keep demo-specific execution instructions in `swingset-demo/README.md` so the library README remains focused on the reusable artifact.
- If screenshots are maintained in the repository, consider keeping them in a stable `docs/images/` or `swingset-demo/images/` folder and linking them from the demo README.

## Contributors

- Prasanth R. Pasala
- Brian E. Pangburn
- The Pangburn Group
- Ernie R. Rael
- Diego Gil
- Man "Bee" Vo

## License

SwingSet is distributed under the BSD 3-Clause License. See [`../LICENSE.md`](../LICENSE.md).

For questions regarding SwingSet, send an email to `swingset#NO-SPAM#@pangburngroup.com`.
