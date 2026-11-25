# IDE setup — SwingSet SS-5-WRAPPER

This guide applies to the `SS-5-WRAPPER` branch of `bpangburn/swingset`. It is a Maven reactor with the root `pom.xml` and modules `swingset/` (library) and `swingset-demo/` (demo). Work from the repository root when importing or building.

## Toolchain and compilation

- Have JDK 25 available to Eclipse and Maven; confirm your Eclipse release understands Java 25.
- The parent POM uses the **frgaal** compiler (`compilerId=frgaal`), configured with **Java 25 source** (`java-source=25`) and a **Java 17 bytecode target** (`java-target=17`). This permits supported newer language syntax with an older class-file target. Do not replace this with ordinary `javac --release 17`; doing so would reject newer syntax.
- The Maven POM defines `project.build.sourceEncoding=UTF-8`. No separately tracked Eclipse `encoding/<project>=UTF-8` preferences are necessary.
- Eclipse JDT is **not frgaal**. Eclipse uses its own Java compiler for incremental builds; set its compliance/source level to Java 25 to parse the newer syntax. Both modules use local JDT preferences. Check their compliance and set it to 25 if m2e defaults to Java 17 or another level.
- **Java 17 target bytecode alone does not prove Java 17 runtime compatibility**: inspect API usage, dependencies, and multi-release JAR behavior and test on a Java 17 runtime.
- The existing Maven plugin and dependency versions are unchanged. Run `mvn clean verify` (with an appropriate Maven version, such as 3.9.6) to validate the authoritative CLI build.

## Import into Eclipse (m2e)

1. Clone/check out `SS-5-WRAPPER` (or the PR branch) and install a Java-25-capable Eclipse with Maven Integration for Eclipse (m2e).
2. Use **File → Import → Maven → Existing Maven Projects**.
3. Choose the **repository directory containing the parent `pom.xml`**, not the `pom.xml` file itself. Import the parent and both child modules.
4. m2e creates local `.project`, `.classpath`, and other generated metadata. The entire `.settings/` directory is ignored in both modules; Eclipse preferences are local and can be regenerated.
5. Check **Project → Properties → Java Compiler** and **Java Build Path**. Ensure both modules' JDT source/compliance settings support Java 25 syntax if required, and check annotation processing if needed. JDT output is not the frgaal release artifact.
6. After Maven changes, use **Maven → Update Project**. For authoritative compilation use `mvn clean verify` from the root.

**Reimporting a broken or previously registered Eclipse project:** If Eclipse reports a missing `.project`, first remove the project *registration* using Project Explorer → Delete, with **Delete project contents on disk unchecked**. Then import the Maven reactor again. Do not repeatedly delete local `.project` while Eclipse is using the project.

## Import into NetBeans

Use **File → Open Project** on the Maven reactor's root directory (or open the root parent `pom.xml` as a Maven project). Both child modules are declared in the parent POM. The demo has customized, tracked `swingset-demo/nbactions.xml` (run/debug/profile with `--enable-preview`) and `swingset-demo/nb-configuration.xml` (editor formatting and inspection rules). These settings do **not** get recreated exactly during a fresh IDE import. Preserve them unless intentionally changing those workflows.

## Ignore rules and tracked project configuration

The top-level `.gitignore` applies recursively to both modules. It ignores generated Maven output, Eclipse `.project` and `.classpath`, all `.settings/` directories, annotation-processing *output* folders, and standard IDE caches. Module-local `.gitignore` rules are retained for now, including the demo's `log/` exclusion.

Existing Eclipse users may need **Maven → Update Project** or a re-import after pulling the cleanup. Custom JDT warning and annotation-processing preferences are no longer shared through Git; configure them locally if needed. The maintainer confirmed Eclipse works after removing both `.settings/` directories.

**Tracked configuration deliberately retained:**

| File | Why retain it? |
| --- | --- |
| `swingset-demo/nbactions.xml` | Custom NetBeans run/debug/profile behavior |
| `swingset-demo/nb-configuration.xml` | Custom NetBeans formatting (two-space indent) and Java hint/inspection preferences |
| `.clang-format` | Existing maintained formatting configuration; do not assume it is generated |
| `.hgignore` | Existing Mercurial ignore file; consider only if Mercurial support is intentionally retired |

These retained files are **not** in `.gitignore`. Git does not stop tracking an already tracked file merely because it appears in an ignore file.

## Future .gitignore/deletion candidates — review before removing

- `swingset-demo/nbactions.xml`: replace custom run/debug/profile actions with equivalent Maven configurations and documented commands before removal.
- `swingset-demo/nb-configuration.xml`: retain or export formatter/hint rules; Maven will not reproduce these IDE-specific settings automatically.
- `swingset/.gitignore` and `swingset-demo/.gitignore`: consolidate duplicated entries into the root file after confirming module-specific exclusions remain in effect.
- `.hgignore`: remove only if this repository no longer needs Mercurial compatibility.
- Possible future generated artifacts (`build/`, `dist/`, `*.jar`, `*.log`, `.launch`, IDE formatting profiles): inspect actual usage before blanket ignoring. Shared configuration and committed binary resources are not necessarily disposable.

**Scope:** This change does not refactor source code, change dependencies, or claim the Maven build passes; run the build and test the Java 17 runtime target separately.
