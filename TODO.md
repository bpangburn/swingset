# SS-5-WRAPPER — outstanding tasks

This branch is the SwingSet 5 wrapper development line. It uses **JDK 25**
and frgaal to compile Java 25 source with a **Java 17 bytecode target**.
Do not confuse the build JDK with the minimum supported runtime.

## 1. Publish the required SeeSaw dependency

- [ ] Release and publish `dev.visdb.seesaw:seesaw:0.5.2` to the Maven repository
      available to GitHub Actions (currently Maven Central).
- [ ] Confirm both its POM and JAR resolve using a clean Maven repository.
- [ ] Keep `<version.visdb>0.5.2</version.visdb>` in this branch's parent POM.
      **Do not downgrade to 0.5.1** merely to make CI pass; this wrapper
      needs 0.5.2.
- [ ] Verify the release's transitive dependencies and its intended Java
      runtime/API compatibility.

The first runs of PR #275 failed during dependency resolution because
`dev.visdb.seesaw:seesaw:jar:0.5.2` was not yet available in Maven Central.
That failure did not establish that the wrapper's source compiles or fails.

## 2. Re-enable Maven and CodeQL automation after SeeSaw 0.5.2 is available

The following workflows exist but are **manual-only** until publication:

- `.github/workflows/maven-build.yml` — `mvn -B -ntp clean verify`
- `.github/workflows/codeql-analysis.yml` — `mvn -B -ntp clean compile`
  followed by CodeQL analysis

Both use Temurin **JDK 25**. To re-enable automatic checks, create a
**separate, small PR** changing each workflow's `on:` block from:

```yaml
on:
  workflow_dispatch:
```

to:

```yaml
on:
  workflow_dispatch:
  push:
    branches: [SS-5-WRAPPER]
  pull_request:
    branches: [SS-5-WRAPPER]
```

For CodeQL, optionally also restore the weekly scan:

```yaml
  schedule:
    - cron: '0 6 * * 1'
```

- [ ] First run both workflows manually after the SeeSaw release; resolve
      any further compilation, test, or CodeQL issues they reveal.
- [ ] Restore the triggers above and verify a PR-triggered build and a
      post-merge push build both pass.
- [ ] Confirm any desired branch protection / required checks are configured
      after the workflows produce stable results.
- [ ] Add a **Java 17 runtime smoke test** using compiled artifacts.
      Running Maven under JDK 17 is *not* equivalent to compiling Java 25
      source with frgaal under JDK 25. Bytecode level 17 does not guarantee
      that dependencies or invoked JDK APIs work on Java 17.
- [ ] Check the SwingSet/SeeSaw integration and demo behavior.

Do **not** describe an absent check as passing. While workflows are
manual-only, pull requests do not receive automatic build/CodeQL verification.

## 3. JUnit 5 versus JUnit 6

**Current setting:** `<version.junit-jupiter>5.14.1</version.junit-jupiter>`
(see `pom.xml`). Mockito is also used in the test dependency set.

**Decision for now:** Remain on JUnit 5. The other maintained SwingSet
branches (`master` and `4.0.14-SNAPSHOT`) run Java 8 compatibility
checks, so they should continue with JUnit 5 while that testing baseline
is maintained.

**Potential future migration:** Evaluate JUnit 6 **only on
`SS-5-WRAPPER`**, once SeeSaw 0.5.2 is available and automated tests
run reliably. JUnit 6 requires **Java 17+**, which is compatible with
the wrapper's JDK 25 test environment in principle, but this must be
validated rather than assumed.

A Dependabot rule in the *default branch's*
`.github/dependabot.yml` temporarily ignores major upgrades of
`org.junit.jupiter:junit-jupiter` for this wrapper branch. JUnit 5
minor/patch updates remain eligible.

Before considering JUnit 6:

- [ ] Make JUnit 5 tests green and review test coverage first.
- [ ] Open a **separate JUnit 6 migration PR** targeting this branch.
- [ ] Remove only the `SS-5-WRAPPER` JUnit-major ignore rule from
      `master:.github/dependabot.yml` when ready. Do not change the
      rules for `master` or `4.0.14-SNAPSHOT`.
- [ ] Read the official [JUnit 6 migration guide](https://github.com/junit-team/junit-framework/wiki/Upgrading-to-JUnit-6.0)
      and [JUnit release notes](https://docs.junit.org/6.0.0/release-notes.html).
      JUnit 6 raises the baseline to Java 17, aligns Platform/Jupiter/
      Vintage artifact versions, and removes some long-deprecated APIs.
- [ ] Check the complete resolved JUnit Platform/Jupiter/Mockito dependency
      tree for consistent versions and compatible test engines.
- [ ] Confirm that Maven Surefire (currently managed in the POM) discovers
      and executes all tests using JDK 25. Check `@Test`, extensions,
      parameterized tests, and any deprecated JUnit APIs in use.
- [ ] Run full tests and exercise SwingSet demo integration. A successful
      compile without executed tests is insufficient.
- [ ] If tests or IDE integration reveal compatibility problems, keep
      JUnit 5 on this branch until those issues are resolved.

**Related PRs:** #275 prepared manual CI, #276 updated Log4j,
#273 is the deferred automated JUnit 6 proposal.
