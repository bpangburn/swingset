# Java compatibility policy

The 4.x line preserves Java 8 compatibility for the **SwingSet core library**.

| Branch | Minimum supported runtime | CI policy |
| --- | --- | --- |
| `master` (SwingSet 4.x) | Java 8 | Compile core with `--release 8` and build/test the core on JDK 8; test whole reactor on newer JDKs |
| `4.0.14-SNAPSHOT` (4.0.14 line) | Java 8 | Same as `master` |
| `SS-5-WRAPPER` | Java 17 or newer, pending validation | No Java 8 requirement; use a separate modern-JDK workflow |
| `SS-5-TESTING` | Java 17 or newer, pending validation | No Java 8 requirement; use a separate modern-JDK workflow |

For the 5.x branches, Java 11 compatibility is **not required**. Their exact
minimum runtime should be established by builds/tests and by inspecting
dependencies before a release; Java 17 is a provisional floor, not a tested claim.

The Maven CI for 4.x runs `mvn -B -ntp clean verify -pl swingset -am` under
JDK 8 because the demo module may require a more recent JDK. The complete
reactor is built on JDK 11/17/21/25. Java 8 compilation and tests help
validate compatibility, but cannot prove every runtime pathway is covered.

Changes to Java baseline or release compilation settings should be intentional,
reviewed separately, and reflected in this policy. In particular, moving the
4.x compilation target above Java 8 is not permitted.
