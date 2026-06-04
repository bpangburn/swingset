# SwingSet FAQ

Frequently asked questions for the SwingSet Open Toolkit for Java Swing.

## Demo and setup

### 1. I am trying to compile and run the demo/samples. I see "Processing..." and nothing happens. What is wrong?

You may be missing a required dependency JAR or running the demo without the expected classpath.

For current build and execution instructions, see:

- [`../README.md`](../README.md) for the repository overview.
- [`../swingset-demo/README.md`](../swingset-demo/README.md) for demo-specific build and run instructions.

If you are running the executable demo JAR with bundled dependencies, use:

```bash
java -jar swingset-demo-x.y.z-jar-with-dependencies.jar
```

Replace `x.y.z` with the SwingSet version number.

If you are running the demo with an external database driver, place the database driver JAR on the classpath and run the demo main class directly rather than using `-jar`.

## `SSDataGrid`

### 2. I set the headers for an `SSDataGrid`. Why don't I see them on the screen?

Set the headers before setting the `RowSet`.

If you reverse the order, the headers may not appear as expected. If you plan to specify headers, use the empty constructor, configure the headers, and then assign the `RowSet`.

Typical order:

```java
SSDataGrid dataGrid = new SSDataGrid();

dataGrid.setHeaders(new String[] {"Part Name", "Color Code", "Weight", "City"});
dataGrid.setRowSet(rowSet);
```

### 3. Why am I getting an exception while setting renderers?

If you use column names rather than column numbers, set renderers only after setting the `RowSet`.

`SSDataGrid` uses the `RowSet` to resolve column names to column numbers. Until the `RowSet` is assigned, name-based column lookups cannot work.

Typical order:

```java
SSDataGrid dataGrid = new SSDataGrid();

dataGrid.setHeaders(new String[] {"Part Name", "Color Code", "Weight", "City"});
dataGrid.setRowSet(rowSet);

dataGrid.setComboRenderer(
    "color_code",
    new String[] {"Red", "Green", "Blue"},
    new Integer[] {Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(2)}
);
```

`SSDataGrid` provides convenience renderer methods such as:

- `setComboRenderer(...)`
- `setCheckBoxRenderer(...)`
- `setDateRenderer(...)`

Each of these supports column-number and column-name based overloads.

### 4. What order should I use when configuring an `SSDataGrid`?

A safe general order is:

1. Create the `SSDataGrid`.
2. Set headers, if any.
3. Set the `RowSet`.
4. Apply name-based column configuration, such as hidden columns, uneditable columns, renderers, default values, and primary-column settings.

Example:

```java
SSDataGrid dataGrid = new SSDataGrid();

dataGrid.setHeaders(new String[] {"Part Name", "Color Code", "Weight", "City"});
dataGrid.setRowSet(rowSet);

dataGrid.setHiddenColumns(new String[] {"part_id"});
dataGrid.setUneditableColumns(new String[] {"part_id"});
dataGrid.setComboRenderer(
    "color_code",
    new String[] {"Red", "Green", "Blue"},
    new Integer[] {Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(2)}
);
dataGrid.setDefaultValues(
    new String[] {"color_code", "weight", "city"},
    new Object[] {Integer.valueOf(0), Integer.valueOf(20), "New Orleans"}
);
dataGrid.setPrimaryColumn("part_id");
```

### 5. What happens to default values and custom settings when I change the underlying `RowSet`?

Custom `SSDataGrid` settings generally remain in place after changing the underlying `RowSet`.

This includes settings such as:

- Default values.
- Headers.
- Renderers.
- Hidden-column settings.

If you want to remove or replace those settings, explicitly call the appropriate setter methods again.

For example, if default values were configured and you later want to remove all default values:

```java
dataGrid.setDefaultValues(null, null);
```

### 6. I changed the query for an `SSDataGrid` rowset but returned the same columns. Why are my renderers or hidden columns not working as expected?

When changing the query used by an `SSDataGrid`, keep the returned columns in the same order unless you also update the grid settings that depend on column positions.

Renderers and hidden columns may be configured by column number or by column name. When configured by name, SwingSet resolves the name to a column number using the current `RowSet`. If a later query returns the same columns in a different order, older renderer or hidden-column settings may still refer to the previous column positions.

To avoid unexpected behavior:

- Keep the query column order stable; or
- Reapply renderer, hidden-column, uneditable-column, default-value, and primary-column settings after assigning the modified `RowSet`.

## Database support

### 7. I'm having trouble getting SwingSet to work with MySQL. Are there any special instructions?

Use a current MySQL Connector/J driver and make sure it is on the runtime classpath.

For example, when running the demo against MySQL, the database driver JAR must be included with the demo JAR on the classpath:

```bash
java -cp mysql-connector-j-x.y.z.jar:swingset-demo-x.y.z-jar-with-dependencies.jar \
    com.nqadmin.swingset.demo.MainClass -v -p property_file mysql
```

Replace `mysql-connector-j-x.y.z.jar`, `swingset-demo-x.y.z-jar-with-dependencies.jar`, and `property_file` with the appropriate file names for your environment.

Older versions of the MySQL JDBC driver may cause problems. Prefer a supported MySQL Connector/J version that matches your Java version and MySQL/MariaDB server environment.

See [`../swingset-demo/README.md`](../swingset-demo/README.md) for the current demo database configuration examples.

## Migration and deprecated classes

### 8. I see that `SSJdbcRowSetImpl` and `SSRowSet` have been deprecated. What should I use instead?

SwingSet has been updated to work with standard `RowSet` implementations rather than requiring SwingSet-specific rowset classes.

For Java 8 and later, the recommended replacement is:

- [`jdbcrowsetimpl`](https://github.com/bpangburn/jdbcrowsetimpl)

SwingSet also includes `com.nqadmin.swingset.datasources.RowSetOps`, which provides static helper methods intended to replace custom/convenience methods that were previously available through deprecated SwingSet rowset classes.

### 9. Are there instructions for migrating to SwingSet 4.x from earlier versions?

Yes. See the migration guide:

- [Migration to SwingSet 4.x](https://github.com/bpangburn/swingset/wiki/Migration-to-SwingSet-4.x)

## License

SwingSet is distributed under the BSD 3-Clause License. See [`../LICENSE.md`](../LICENSE.md).
