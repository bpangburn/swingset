# SwingSet

SwingSet is an open source Java Swing toolkit that provides data-aware replacements and helpers for building database-backed Swing user interfaces.

See [IDE_SETUP.md](IDE_SETUP.md) for Eclipse m2e / NetBeans import instructions, Java 25 development settings, and tracked IDE configuration.

This repository is a multi-module Maven project:

| Module | Description |
| --- | --- |
| [`swingset`](swingset/) | Core SwingSet library components. |
| [`swingset-demo`](swingset-demo/) | Sample/demo application showing the SwingSet components with an in-memory H2 database. |

## SwingSet 5 date picker

`com.nqadmin.swingset.formatting.SSLGoodDatePicker` exposes Seesaw's database-aware LGoodDatePicker integration through SwingSet. It supports JDBC `DATE` columns and uses `java.time.LocalDate` for date values. The existing `SSDateField` remains available.

```java
import com.nqadmin.swingset.formatting.SSLGoodDatePicker;

SSLGoodDatePicker picker = new SSLGoodDatePicker(rowsModel, "effective_date");
```

A no-argument constructor is also available for subsequent binding via the existing rows model. The LGoodDatePicker integration is a transitive dependency of SwingSet; Maven applications do not need a separate dependency declaration. See TestBaseComponents in the demo for a working example.

## Quick build

```bash
git clone https://github.com/bpangburn/swingset.git
cd swingset
mvn clean package
```

The built artifacts will be written to each module's `target/` directory.

## More information

- Demo documentation: [`swingset-demo/README.md`](swingset-demo/README.md)
- Issues: <https://github.com/bpangburn/swingset/issues>
- Contact: `swingset#NO-SPAM#@pangburngroup.com`

## License

SwingSet is distributed under the BSD 3-Clause License. See [`LICENSE.md`](LICENSE.md).
