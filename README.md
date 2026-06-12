# SwingSet

SwingSet is an open source Java Swing toolkit that provides data-aware replacements and helpers for building database-backed Swing user interfaces.

This repository is a multi-module Maven project:

| Module | Description |
| --- | --- |
| [`swingset`](swingset/) | Core SwingSet library components. Start here for usage, dependency, build, and component documentation. |
| [`swingset-demo`](swingset-demo/) | Sample/demo application showing the SwingSet components with an in-memory H2 database. |

For library documentation, see [`swingset/README.md`](swingset/README.md).

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
