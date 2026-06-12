# SwingSet Demo

The `swingset-demo` module contains runnable sample applications for the SwingSet library.

The demo shows how SwingSet components can be used to build database-aware Java Swing screens. By default, it uses an in-memory H2 database populated with sample supplier-and-parts data based on the database described in C. J. Date's classic database textbook, *An Introduction to Database Systems*.

For the core library documentation, see [`../swingset/README.md`](../swingset/README.md).

## Requirements

- Java 11 or later.
- Maven 3.9.6 or later is recommended for building from source.

## Running the executable demo JAR

Beginning with SwingSet 3.0.0, the demo module can produce an executable JAR with dependencies.

After downloading or building the demo JAR, run:

```bash
java -jar swingset-demo-x.y.z-jar-with-dependencies.jar
```

Replace `x.y.z` with the SwingSet version number.

The demo uses an in-memory H2 database by default, so no external database server is required for normal demo usage.

## Building from source

Clone the repository and build from the repository root:

```bash
git clone https://github.com/bpangburn/swingset.git
cd swingset
mvn clean package
```

To build only the demo module from the repository root:

```bash
mvn clean package -pl swingset-demo -am
```

To build from inside the demo module directory:

```bash
cd swingset-demo
mvn clean package
```

For release-style local builds that attach source and Javadoc artifacts, use the release profile:

```bash
mvn clean package -Prelease
```

The executable demo JAR is written to:

```text
swingset-demo/target/
```

Run it with:

```bash
cd swingset-demo/target
java -jar swingset-demo-x.y.z-jar-with-dependencies.jar
```

## Multi-monitor placement

The default screen for the demo can be specified with the `JAVA_PREFERRED_SCREEN` environment variable.

For example, on a dual-monitor Linux system:

```bash
export JAVA_PREFERRED_SCREEN=1
java -jar swingset-demo-x.y.z-jar-with-dependencies.jar
```

If the environment variable is not present or is out of bounds, the default screen is used.

## Demo screens

### `MainClass`

Main demo launcher. Opens a `JFrame` with buttons for each SwingSet example/demo screen.

### `Example1`

Displays data from the `supplier_data` table.

Demonstrates:

- `SSTextField` for supplier ID, name, city, and status.
- `SSDataNavigator` for record navigation.

### `Example2`

Displays data from the `supplier_data` table.

Demonstrates:

- `SSTextField` for supplier ID, name, and city.
- `SSComboBox` for status values.
- `SSDataNavigator` for record navigation.

### `Example3`

Displays data from the `supplier_part_data` table.

Demonstrates:

- `SSTextField` for supplier-part ID and quantity.
- `SSDBComboBox` for displaying supplier name and part name from lookup queries against `supplier_data` and `part_data`.
- `SSDataNavigator` for record navigation.

### `Example4`

Displays data from the `part_data` table.

Demonstrates:

- `SSTextField` for part ID, name, weight, and city.
- `SSComboBox` for color.
- Record navigation using either `SSDataNavigator` or `SSDBComboBox`.
- `SSSyncManager` for synchronizing navigation controls when more than one navigation mechanism is available.

### `Example4Advanced`

Extends `Example4`.

Demonstrates:

- Custom handling of a missing option, such as `Red`, in the color `SSComboBox`.
- `InputMap` and `ActionMap` usage for custom key handling.
- Extra button handling with `F3` through `F11` mnemonics corresponding to navigator buttons.
- Additional first-record and last-record navigation buttons at the bottom of the screen.

### `Example4UsingHelper`

Same general demonstration as `Example4`, but implemented by extending `SSFormViewScreenHelper`.

### `Example5`

Displays a tabular view of the `part_data` table.

Demonstrates:

- `SSDataGrid` for table/grid display.
- Editable table behavior.
- Row deletion using `Ctrl-X`.
- Default delete confirmation behavior.

### `Example6`

Similar to `Example5`, but adds a combo-box renderer for the color column.

Demonstrates:

- `SSDataGrid`.
- Custom column rendering with `ComboRenderer`.

### `Example7`

Displays a tabular view of the `supplier_part_data` table.

Demonstrates:

- `SSDataGrid`.
- `ComboRenderer` with a lookup to `supplier_data` for displaying supplier names.
- `DateRenderer` for the ship-date column.

### `Example7UsingHelper`

Same general demonstration as `Example7`, but implemented by extending `SSDataGridScreenHelper`.

### Base component test screen

Demonstrates the base SwingSet components except for `SSDataGrid`.

### Formatted component test screen

Demonstrates formatted SwingSet components, such as fields intended for formatted values.

## Using alternate database servers

The demo uses an in-memory H2 database by default. It can also work with user-supplied connection properties and SQL scripts to initialize another database.

Show command-line help with:

```bash
java -jar swingset-demo-x.y.z-jar-with-dependencies.jar -h
```

### Connection properties

Connection properties use the standard Java properties-file format.

Example MySQL properties file:

```properties
# This is a standard Java properties file.
DB_DRIVER_CLASS = com.mysql.cj.jdbc.Driver
DB_URL = jdbc:mysql://localhost/swingset_demo_suppliers_and_parts
user = some_user
password = some_password
serverTimezone = UTC
```

The `DB_DRIVER_CLASS` and `DB_URL` properties are used internally with:

```java
Class.forName(driverClass);
DriverManager.getConnection(url, props);
```

Before running the demo against MySQL, create the target database. For example:

```text
swingset_demo_suppliers_and_parts
```

The SQL scripts to initialize the MySQL database tables are included in the demo module.

### Running with an external database driver

To run with an external database driver, place the database driver JAR on the classpath. Do not use `-jar` when using `-cp`; specify the main class directly.

Example:

```bash
java -cp mysql-connector-java-8.0.21.jar:swingset-demo-x.y.z-jar-with-dependencies.jar \
    com.nqadmin.swingset.demo.MainClass -v -p property_file mysql
```

### Extracting database scripts

To extract the included MySQL scripts:

```bash
java -jar swingset-demo-x.y.z-jar-with-dependencies.jar -d mysql
```

This writes files similar to the following into the current directory:

```text
dump.mysql.swingset-demo-app.sql
dump.mysql.swingset-demo-components.sql
```

These files can be edited as needed for another database. If edited, save them under these names:

```text
swingset-demo-app.sql
swingset-demo-components.sql
```

Then run the demo with the custom scripts:

```bash
java -cp some_db_driver.jar:swingset-demo-x.y.z-jar-with-dependencies.jar \
    com.nqadmin.swingset.demo.MainClass -v \
    -p property_file \
    -s swingset-demo-app.sql \
    -s swingset-demo-components.sql
```

The supplied connection properties and SQL scripts initialize the database before the demo starts.

## Third-party libraries

The demo uses third-party libraries, including:

- H2 Database Engine, which is dual licensed under the Mozilla Public License 2.0 or the Eclipse Public License 1.0.
- Glazed Lists, which is dual licensed under the Mozilla Public License 2.0 or the GNU Lesser General Public License 2.1.

Review the applicable third-party licenses before redistributing the demo or bundled dependencies.

## License

SwingSet is distributed under the BSD 3-Clause License. See [`../LICENSE.md`](../LICENSE.md).

For questions regarding SwingSet, send an email to `swingset#NO-SPAM#@pangburngroup.com`.
