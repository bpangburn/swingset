# CHANGELOG-POMS.md

POM ChangeLog for the SwingSet Open Toolkit for Java Swing.

Latest non-beta versions were originally determined based on review of
<https://mvnrepository.com/>.

Starting with SwingSet 4.0.11, POM updates are tracked in this separate file.

## SwingSet 4.0.13 — Pending release / 4.0.13-SNAPSHOT

### All modules — Java and Maven version

1. `maven`: 3.6.3 → 3.9.6  
   Latest noted: 3.9.12; 4.0.0-rc-5 also available.
2. `java`: 1.8 → 8  
   Uses `<maven.compiler.release>` rather than `<maven.compiler.source>` and
   `<maven.compiler.target>`.

### `swingset-parent` dependencies

1. None.

### `swingset` dependencies

1. `glazedlists`: 1.11.1203 → no change.
2. `log4j-core`: 2.22.0 → 2.25.3  
   Latest noted: 3.0.0-beta3.
3. `jdbcrowsetimpl`: 1.0.3 → no change.

### `swingset-demo` dependencies

1. `swingset`: 4.0.12 → 4.0.13.
2. `log4j-api`: 2.22.0 → 2.25.3  
   Latest noted: 3.0.0-beta3.
3. `log4j-core`: 2.22.0 → 2.25.3  
   Latest noted: 3.0.0-beta3.
4. `h2`: 2.2.224 → 2.4.240.
5. `java-getopt`: 1.0.13 → no change.
6. `raelity-lib`: 1.2.0 → no change.

### `swingset-parent`, `swingset`, and `swingset-demo` plugins

1. `versions-maven-plugin`: 2.16.2 → 2.20.1.
2. `maven-enforcer-plugin`: 3.4.1 → 3.6.2.
3. `maven-clean-plugin`: 3.3.2 → 3.5.0  
   Latest noted: 4.0.0-beta-2.
4. `maven-deploy-plugin`: 3.1.1 → 3.1.4  
   Latest noted: 4.0.0-beta-2.
5. `maven-install-plugin`: 3.1.1 → 3.1.4  
   Latest noted: 4.0.0-beta-2.
6. `maven-site-plugin`: 4.0.0-M12 → 4.0.0-M16.
7. `dependency-check-maven`: 9.0.6 → 12.1.9.

### `swingset` and `swingset-demo` plugins

1. `maven-jar-plugin`: 3.3.0 → 3.5.0  
   Latest noted: 4.0.0-beta-1.
2. `maven-resources-plugin`: 3.3.1 → 3.4.0  
   Latest noted: 4.0.0-beta-1.
3. `maven-compiler-plugin`: 3.11.0 → 3.14.1  
   Latest noted: 4.0.0-beta-3.
4. `maven-gpg-plugin`: 3.1.0 → 3.2.8.
5. `maven-surefire-plugin`: 3.2.3 → 3.5.4.
6. `maven-source-plugin`: 3.3.0 → 3.4.0  
   Latest noted: 4.0.0-beta-1.

### `swingset` plugins

1. `maven-javadoc-plugin`: 3.6.3 → 3.12.0.
2. `nexus-staging-maven-plugin`: 1.6.13 → 1.7.0.
3. `maven-failsafe-plugin`: 3.2.3 → 3.5.4.
4. `junit-jupiter`: 5.10.1 → 5.14.1  
   Latest noted: 6.1.0-M1.

### `swingset-demo` plugins

1. `maven-assembly-plugin`: 3.6.0 → 3.8.0.

## SwingSet 4.0.12 — 2023-12-16

### All modules — Java and Maven version

1. `maven`: 3.6.3 → no change for now.  
   Latest noted: 3.9.6.
2. `java`: 1.8 → 8  
   Uses `<maven.compiler.release>` rather than `<maven.compiler.source>` and
   `<maven.compiler.target>`.

### `swingset-parent` dependencies

1. None.

### `swingset` dependencies

1. `glazedlists`: 1.11.1203 → no change.
2. `log4j`: 2.19.0 → 2.22.0.
3. `jdbcrowsetimpl`: 1.0.3 → no change.

### `swingset-demo` dependencies

1. `swingset`: 4.0.11 → 4.0.12.
2. `log4j`: 2.19.0 → 2.22.0.
3. `h2`: 2.1.214 → 2.2.224.
4. `java-getopt`: 1.0.13 → no change.
5. `raelity-lib`: 1.2.0 → no change.

### `swingset-parent`, `swingset`, and `swingset-demo` plugins

1. `versions-maven-plugin`: 2.13.0 → 2.16.2.
2. `maven-enforcer-plugin`: 3.1.0 → 3.4.1.
3. `maven-clean-plugin`: 3.2.0 → 3.3.2.
4. `maven-deploy-plugin`: 3.0.0 → 3.1.1.
5. `maven-install-plugin`: 3.1.0 → 3.1.1.
6. `maven-site-plugin`: 4.0.0-M3 → 4.0.0-M12.

### `swingset` and `swingset-demo` plugins

1. `maven-jar-plugin`: 3.3.0 → no change.
2. `maven-resources-plugin`: 3.3.0 → 3.3.1.
3. `maven-compiler-plugin`: 3.10.1 → 3.11.0.
4. `dependency-check-maven`: 8.0.2 → 9.0.6.
5. `maven-gpg-plugin`: 3.0.1 → 3.1.0.
6. `maven-surefire-plugin`: 3.0.0-M8 → 3.2.3.
7. `maven-source-plugin`: 3.2.1 → 3.3.0.

### `swingset` plugins

1. `maven-javadoc-plugin`: 3.4.1 → 3.6.3.
2. `nexus-staging-maven-plugin`: 1.6.13 → no change.
3. `maven-failsafe-plugin`: 3.0.0-M8 → 3.2.3.
4. `junit-jupiter`: 5.9.2 → 5.10.1.

### `swingset-demo` plugins

1. `maven-assembly-plugin`: 3.4.2 → 3.6.0.

## SwingSet 4.0.11 — 2023-02-07

### All modules — Java and Maven version

1. `maven`: 3.6.3 → no change for now.  
   Latest noted: 3.9.0.
2. `java`: 1.8 → no change for now.

### `swingset-parent` dependencies

1. None.

### `swingset` dependencies

1. `glazedlists`: 1.11.1203 → no change.
2. `log4j`: 2.17.2 → 2.19.0.
3. `jdbcrowsetimpl`: 1.0.3 → no change.

### `swingset-demo` dependencies

1. `swingset`: 4.0.10 → 4.0.11.
2. `log4j`: 2.17.2 → 2.19.0.
3. `h2`: 2.1.212 → 2.1.214.
4. `java-getopt`: 1.0.13 → no change.
5. `raelity-lib`: new → 1.2.0.

### `swingset-parent`, `swingset`, and `swingset-demo` plugins

1. `versions-maven-plugin`: 2.10.0 → 2.13.0.
2. `maven-enforcer-plugin`: 3.0.0 → 3.1.0.
3. `maven-clean-plugin`: 3.1.0 → 3.2.0.
4. `maven-deploy-plugin`: 3.0.0-M2 → 3.0.0.
5. `maven-install-plugin`: 3.0.0-M1 → 3.1.0.
6. `maven-site-plugin`: 3.11.0 → 4.0.0-M3.

### `swingset` and `swingset-demo` plugins

1. `maven-jar-plugin`: 3.2.2 → 3.3.0.
2. `maven-resources-plugin`: 3.2.0 → 3.3.0.
3. `maven-compiler-plugin`: 3.10.1 → no change.
4. `dependency-check-maven`: 7.1.0 → 8.0.2.
5. `maven-gpg-plugin`: 3.0.1 → no change.
6. `maven-surefire-plugin`: 3.0.0-M7 → 3.0.0-M8.
7. `maven-source-plugin`: 3.2.1 → no change.

### `swingset` plugins

1. `maven-javadoc-plugin`: 3.3.2 → 3.4.1.
2. `nexus-staging-maven-plugin`: 1.6.12 → 1.6.13.
3. `maven-failsafe-plugin`: 3.0.0-M6 → 3.0.0-M8.
4. `junit-jupiter`: 5.9.0-M1 → 5.9.2.

### `swingset-demo` plugins

1. `maven-assembly-plugin`: 3.3.0 → 3.4.2.
