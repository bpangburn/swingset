# SwingSet wrapper compatibility review

Reviewed October 30, 2026 against:

- SwingSet `SS-5-WRAPPER`: `2c3b91c` (current remote branch when fetched).
- SeeSaw `main`: `a623980`, version `0.5.2`.
- The supplied `ss-5-wrapper_errors_2026-09-28.txt`.
- The legacy SwingSet sources in the repository's `master` branch.

## Applying the patch

This is a complete replacement for the earlier compatibility patch, not an
additional patch to apply on top of it. It is based on the current upstream
SwingSet branch above and preserves the recently added upstream wrappers.

Run from the SwingSet **Git repository root**, where `pom.xml` declares
`swingset-parent` and the `swingset/` and `swingset-demo/` directories reside.
Do not run from the nested `swingset/` Maven module directory.

```bash
git switch SS-5-WRAPPER
git status --short
git apply --check /path/to/0001-Refresh-SwingSet-4-compatibility-wrappers.patch
git am /path/to/0001-Refresh-SwingSet-4-compatibility-wrappers.patch
mvn test
```

Check that the branch contains the baseline commit and that any local changes
are safely committed or otherwise preserved. If `git apply --check` fails,
stop and inspect the differences; do not force the patch. If the original
patch is already applied, reconcile it with this replacement before proceeding.

The current parent POM already requests SeeSaw 0.5.2. Install matching SeeSaw
core and contrib artifacts first if they are not available to Maven. This
patch does not modify dependency versions or the parent POM.

## Changes checked

- Legacy combo mapping, display option, enum and option-list method names.
- Legacy list selection and option-list overloads, including the deprecated
  `getSelectedValues()` mapping array and its legacy `{-1}` no-selection result.
- `SSCellEditing` now builds on the upstream wrapper instead of replacing its
  source/license; the legacy serializable contract is retained.
- Navigation callback bridges preserve application override dispatch.
- Formatting classes retain the common legacy `SSFormattedTextField` base.
- Date/timestamp factory validation is adapted to that legacy hierarchy;
  strict converters reject impossible dates even before database binding.
- Adding the first second-display value preserves existing items, selection
  and the optional null item. Clearing for the D2 shape change does not
  prematurely recreate the null item.
- Default number factories no longer force a three-integer-digit maximum.
  Precision/decimal getters inspect the actual formatter settings.
- Numeric clear/null validation follows SeeSaw's numeric-field behavior.
- Inherited wrapper operations explicitly use `super.`. Calls to local
  helpers and application-overridable callback hooks intentionally do not.
- New/edited wrapper code uses two-space indentation with spaces, not tabs.

## Validation and remaining limits

The compatibility test suite contains 15 passing runtime tests and exercises
reported signatures at compile time,
plus runtime tests on Swing's event-dispatch thread. It covers both combo
models, enum mappings, null selection, D2 transitions, numeric formatting,
list multi-selection and date/timestamp parsing with and without JDBC type
metadata. The complete SwingSet reactor (including demo compilation) was tested
with matching SeeSaw artifacts built from the source revision above.

The verification environment has Java 17, so Maven's minimum-Java enforcer
was skipped with `-Denforcer.skip=true`; the configured Frgaal compiler still
compiled the sources to Java 17 bytecode. This does not establish a native
Java 25/Eclipse test result. Database round trips and application GUI flows
still require testing in the application.

This is targeted **source compatibility**, not a complete SwingSet 4 binary
or behavioral replacement. In particular:

- `SSBaseComboBox` is an interface, not the former `JComboBox` superclass.
  Existing variables/casts can use its declared legacy selection API, but
  subclassing the old base or passing it directly as an AWT component needs
  migration. The concrete combo wrappers remain Swing components.
- Actual internal model items are SeeSaw `ListItem` implementations. The
  compatibility `SSListItem` marker does not retroactively change those runtime
  types; the wrappers' `getChosenItem()` snapshot types implement the marker.
- Timestamp defaults and formatting infrastructure follow SeeSaw conventions;
  the old SwingSet timestamp display pattern is not reproduced verbatim.
- Application enum cases such as `BonusOnly`, `SalaryAndBonus`, `SalaryOnly`
  and `No` cannot be defined or conclusively diagnosed from an error list.
  They may be cascading errors; compile the application again to confirm.

The next acceptance step is compiling the application and testing representative
data-entry, list, table-editing and navigation workflows. Keep wrappers thin
and add a regression test whenever another compatibility gap is found.

## Follow-up: integer field and combo editor

The incremental follow-up adds `formatting.SSIntegerField` with the three legacy
constructors, integer precision accessors and SeeSaw number-field null handling.
It retains `SSFormattedTextField` as its legacy superclass and explicitly uses
`super.` for inherited operations. `SSBaseComboBox` now declares `getEditor()`;
the concrete combo wrappers already inherit the required Swing implementation.
The suite now contains 17 passing tests, and the complete Maven reactor passes
with enforcement enabled on Java 17 using the configured Frgaal compiler.

This follow-up patch applies on top of the refreshed compatibility patch, not
instead of it. Its paths deliberately omit Git's `a/` and `b/` prefixes:

- Eclipse Apply Patch: repository root, ignore **zero** leading path segments.
- Command line from repository root: `git apply -p0 --check /path/to/patch`,
  then `git apply -p0 /path/to/patch`.
- Run `mvn install -DskipTests` afterwards when Eclipse workspace dependency
  resolution remains disabled, then update the consuming Maven projects.

The reported `List<ListItem>` to `List<SSListItem>` mismatch requires the
application statement at ParticipantCallLogReports.java line 351 before choosing
a migration. No unchecked cast or raw-list override has been added. In
particular, SeeSaw deliberately disables `getSelectedValuesList()` by throwing
`UnsupportedOperationException`; merely changing the application's generic type
would not make that method safe to call. For selected keys use
`getSelectedMappings()`; for selected display strings SeeSaw provides
`getChosenDisplayValues()`. Neither should be substituted without checking how
the application consumes the result.
