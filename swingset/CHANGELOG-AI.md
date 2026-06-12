# CHANGELOG-AI.md

Supplemental AI-generated changelog for the SwingSet project.

This file is intended to complement, not replace, the maintainer-written `CHANGELOG`,
`CHANGELOG-POMS`, GitHub release notes, commit messages, and Maven metadata. It emphasizes
human-friendly summaries of meaningful user-visible, API-visible, build, demo,
documentation, and maintenance changes from the early CVS-converted history through
SwingSet 4.0.12.

## About this AI-generated changelog

This changelog was generated from a review of:

- GitHub release tags.
- Published GitHub release notes where available.
- Compare/patch output between release tags, especially for the 4.x series.
- Project documentation and release housekeeping visible in the repository history.

Because this is AI-generated, it should be treated as a reviewed draft rather than an
authoritative source of truth. When preparing formal release notes, verify any item that
could affect compatibility, public API, behavior, or dependency policy.

## Scope

- Start: `arelease` / `02e8413fae1105a2d691a86d4925f70434e9f984`
- End: `4.0.13-SNAPSHOT` branch, based on comparison from `swingset-4.0.12`
- Repository: `https://github.com/bpangburn/swingset`

## Notes on interpretation

Some early tags were manufactured by `cvs2svn`, so the early history may not have the
same clean semantic boundaries as later GitHub/Maven releases. For those releases, the
entries below are intentionally conservative and describe broad project evolution rather
than claiming every change was newly introduced in a particular semantic-release sense.

For the 4.x series, the changelog reflects both the published release notes and additional
details found by reviewing compare/patch output between tags. The 4.0.13 entry is based on the `4.0.13-SNAPSHOT` branch and should be finalized when the release tag is created.

---

# Summary of major eras

SwingSet is a Java Swing toolkit that provides data-aware replacements and helpers for
standard Swing components, with emphasis on binding UI widgets to JDBC `RowSet` data
models.

Long-term project themes include:

- Data-aware Swing components for text fields, formatted fields, combo boxes, lists,
  tables/grids, check boxes, and navigators.
- JDBC `RowSet` integration and helper classes for reading, updating, inserting, and
  navigating records.
- Demo applications and sample database scripts showing practical usage patterns.
- Gradual modernization of packaging, build tooling, dependencies, tests, Javadocs,
  and release processes.
- Significant API and design cleanup in the 4.x series.

---

# Release history, newest first


## SwingSet 4.0.13 — Released 2026-06-12

Tag/commit: pending. Reviewed from the `4.0.13-SNAPSHOT` branch compared with `swingset-4.0.12`.

Release-preparation, dependency, build-tool, and documentation maintenance release.

Patch-derived details:

- Updated Maven project versions from `4.0.12` to `4.0.13`.
- Updated Maven and plugin version properties across the parent, library, and demo modules.
- Split Log4j version properties into separate `log4j-api` and `log4j-core` properties
  and updated both to the same 2.25.x line.
- Updated the demo H2 dependency to the 2.4.x line.
- Updated OWASP dependency-check and other Maven build plugins.
- Added `CHANGELOG-AI.md` as a supplemental AI-assisted project-history changelog.
- Converted `CHANGELOG.txt` to `CHANGELOG.md` and `CHANGELOG-POMS.txt` to
  `CHANGELOG-POMS.md`.
- Converted `FAQ.txt` to `FAQ.md`, reorganizing the FAQ with Markdown headings, code
  blocks, updated links, and clearer migration/database notes.
- Added a top-level `LICENSE.md` and removed duplicated module-level license text files.
- Reworked the top-level README and module README files for clearer repository,
  build, demo, and component documentation.
- Removed `FOLDERS.txt` after moving the useful repository-layout/Eclipse project
  information into the top-level README.
- Updated copyright years and license references in documentation.

Human-friendly summary:

- Mostly a maintenance and release-prep update rather than a new SwingSet component
  feature release.
- Modernizes dependencies and Maven plugin versions.
- Cleans up documentation structure by moving old text files to Markdown and removing
  duplicated/outdated files.
- Adds an AI-generated historical changelog intended to supplement the maintainer
  changelog.

Likely user impact:

- Maven consumers should generally only need to update to version `4.0.13` once released.
- Developers working from source should use the Markdown docs going forward.
- Users should consult `CHANGELOG-POMS.md` for dependency/plugin details and
  `CHANGELOG-AI.md` for broader historical context.

## SwingSet 4.0.12 — December 16, 2023

Tag/commit: `swingset-4.0.12` / `c836643`

Dependency, plugin, and documentation maintenance release.

Patch-derived details:

- Updated project dependencies and Maven plugins.
- Updated Log4j from the 2.19.x line to 2.22.x.
- Updated H2 used by the demo from the 2.1.x line to 2.2.x.
- Updated JUnit Jupiter from the 5.9.x line to 5.10.x.
- Updated OWASP dependency-check Maven plugin from the 8.x line to 9.x.
- Updated Maven Surefire/Failsafe, compiler, Javadoc, site, deploy, install, resources,
  source, GPG, assembly, enforcer, and versions plugin versions.
- Adjusted Java release/build configuration to use release-style Java 8 configuration
  rather than older `1.8` style in project metadata.
- Removed older manual classpath build/run instructions from README files.
- Reoriented README instructions around Maven-based builds and running the demo.
- Included a small demo-code cleanup removing an unnecessary semicolon.
- Per release notes, no new SwingSet component features were intended.

Human-friendly summary:

- Maintenance release focused on keeping dependencies and Maven plugin tooling current.
- Cleaned up documentation so users are guided toward the Maven build path.
- No intended new user-facing component features.

Likely user impact:

- Recommended maintenance upgrade for users staying on the 4.0.x line.
- Projects already consuming SwingSet through Maven should generally only need to update
  the version.
- Users following README instructions should see less obsolete manual-classpath guidance.

## SwingSet 4.0.11 — February 7, 2023

Tag/commit: `swingset-4.0.11` / `df7307e`

Advanced demo and data-grid helper improvements.

Patch-derived details:

- Added `Example4Advanced`, providing a richer example of advanced SwingSet usage.
- Added `DataGridExampleSupport` to support the expanded demo/example structure.
- Improved combo-box list-item formatting so missing options can display the underlying
  mapping value rather than failing to show useful information.
- Added or expanded list-item-format configurability through getter/setter-style APIs.
- Fixed default parent-ID updates in `SSDataGridScreenHelper`.
- Added `updateScreen(Long parentID)` as a convenience method for parent-aware screen
  refreshes.
- Continued documentation/changelog/release housekeeping.

Human-friendly summary:

- Added a more advanced demo path for learning and testing SwingSet behavior.
- Improved parent/child data-grid refresh behavior.
- Improved combo/list formatting behavior when option data is incomplete or missing.
- Made parent-aware screen updates easier to call.

Likely user impact:

- Users building parent/child screens or master/detail grids should benefit from
  improved helper behavior.
- Users have better examples for more advanced usage.
- Combo boxes/lists can present more useful fallback display text when mapped options
  are missing.

## SwingSet 4.0.10 — May 31, 2022

Tag/commit: `swingset-4.0.10` / `82abc67`

Data-grid helper, list, and formatting improvements.

Patch-derived details:

- Updated `SSDataGridScreenHelper` to use `dataGrid.getComponent()` rather than wrapping
  the grid directly in a new `JScrollPane`.
- Deprecated `isGridCellEditable()` in favor of configuring cell editing through
  `dataGrid.setSSCellEditing()` and `configureSSCellEditing()`.
- Adjusted `updateScreen()` visibility/behavior in helper contexts.
- Updated `SSList` defaults so the default SQL type became `JDBCType.INTEGER` rather
  than `JDBCType.NULL`.
- Expanded `SSListItemFormat` with more flexible `Format`-based behavior.
- Added or expanded tests around list-item formatting.

Human-friendly summary:

- Improved data-grid helper composition and reduced assumptions about how grids are
  wrapped/displayed.
- Moved grid-cell editability configuration toward explicit `SSCellEditing` setup.
- Improved list/list-item formatting flexibility and test coverage.
- Cleaned up default list SQL-type behavior.

Likely user impact:

- Existing subclasses using `isGridCellEditable()` should migrate toward the newer
  `SSCellEditing` configuration approach.
- Data grids embedded through helper classes should behave better when using their own
  component/wrapper.
- List formatting should be more flexible.

## SwingSet 4.0.9 — March 16, 2022

Tag/commit: `swingset-4.0.9` / `359b768`

Action-based data navigator improvements.

Patch-derived details:

- Exposed `SSDataNavigator` operations through its `ActionMap`.
- Enabled users to wire hotkeys, extra toolbar buttons, or other controls through
  standard Swing `InputMap`/`ActionMap` patterns.
- Added access paths such as `getSSDataNavigator()` through `SSComponentInterface`.
- Moved navigator behavior toward an action-centric design.
- Fixed at least one action-enablement bug where Delete action state checked the wrong
  button reference.

Human-friendly summary:

- Made `SSDataNavigator` easier to customize and integrate with Swing actions.
- Improved keyboard shortcut and custom-button support.
- Fixed a real navigator button/action enablement issue.

Likely user impact:

- Applications can more easily bind navigator commands to keyboard shortcuts or custom UI.
- Navigator action state should be more reliable.

## SwingSet 4.0.8 — February 9/10, 2022

Tag/commit: `swingset-4.0.8` / `75a5891`

Formatted text-field validation/decorator improvements and H2 2.x demo update.

Patch-derived details:

- Expanded `SSFormattedTextField` behavior around validation and visual decoration.
- Added or refined focus-background-color handling.
- Expanded `InputVerifier` behavior for formatted fields.
- Added a guard against duplicate property-change handling when validation calls
  `setValue()`.
- Deprecated `updateTextColor()` in favor of `updateTextDecorator()`, indicating a
  broader, more flexible decoration model than simply changing text color.
- Updated the demo to use H2 2.x.
- Updated demo SQL/test data where needed for H2 2.x compatibility, including array
  column syntax such as `INTEGER ARRAY`.

Human-friendly summary:

- Made formatted text fields more robust and easier to decorate visually.
- Improved validation behavior and avoided duplicate property-change side effects.
- Modernized the demo database dependency to H2 2.x and updated demo schema/test data
  accordingly.

Likely user impact:

- Applications using `SSFormattedTextField` may see improved validation behavior.
- Code overriding or calling `updateTextColor()` should migrate toward
  `updateTextDecorator()`.
- Demo users benefit from compatibility with newer H2 versions.

## SwingSet 4.0.7 — December 14, 2021

Tag/commit: `swingset-4.0.7` / `3f8c156`

Log4j/security maintenance and combo-navigator nullability fix.

Patch-derived details:

- Updated Log4j as part of late-2021 dependency/security maintenance.
- Overrode `SSBaseComboBox.getAllowNull()` behavior so combo-box navigators do not treat
  nullability exactly like ordinary bound combo boxes.

Human-friendly summary:

- Security/dependency maintenance release.
- Included a small but meaningful combo-box navigator behavior fix.

Likely user impact:

- Users should prefer this over older 4.0.x builds because of Log4j-era dependency
  maintenance.
- Combo-box navigators should handle nullability more appropriately.

## SwingSet 4.0.6 — December 10, 2021

Tag/commit: `swingset-4.0.6` / `8c8d6d2`

Maintenance release.

Human-friendly summary:

- Continued 4.x maintenance.
- Updated release/build metadata and dependencies.
- No major user-facing API changes identified from the high-level release history.

Likely user impact:

- Primarily a maintenance upgrade.

## SwingSet 4.0.5 — November 22, 2021

Tag/commit: `swingset-4.0.5` / `4f1ac8c`

Date/time binding groundwork and maintenance updates.

Patch-derived details:

- Added demo support for a date column to test a base `SSTextField` bound to a date
  field.
- Adjusted `RowSetOps` date/time/timestamp conversion behavior toward safer string
  handling.
- Improved handling of null date/time values.
- Included dependency and plugin maintenance updates.

Human-friendly summary:

- Improved date/time handling paths around RowSet-backed fields.
- Added demo coverage useful for testing text-field binding with date fields.
- Continued dependency/build maintenance.

Likely user impact:

- Date/time values bound through SwingSet components should be handled more safely,
  especially around nulls and string conversion.
- Demo code better illustrates date-field scenarios.

## SwingSet 4.0.4 — March 25, 2021

Tag/commit: `swingset-4.0.4` / `38d9274`

Combo-box update and list-item formatting improvements.

Patch-derived details:

- Updated `SSDBComboBox.updateOption()` so changing the selected option text preserves
  or reselects the current item. This worked around GlazedLists selection behavior.
- Refactored combo-box update logic by moving shared `updateSSComponent()` behavior from
  specific combo-box classes into `SSBaseComboBox`.
- Added handling for `Integer` mappings used by `SSComboBox`.
- Added handling for `Long` mappings used by `SSDBComboBox`.
- Improved consistency between ordinary combo boxes and database-backed combo boxes.

Human-friendly summary:

- Fixed combo-box selection behavior when list option text changes.
- Centralized shared combo-box update behavior.
- Made list-item mapping behavior more consistent across combo-box implementations.

Likely user impact:

- Combo boxes should better preserve selection when display labels change.
- Applications using mapped combo-box values should see more predictable update behavior.

## SwingSet 4.0.3 — February 22, 2021

Tag/commit: `swingset-4.0.3` / `05244b5`

Combo-box update and navigator insert-mode fixes.

Patch-derived details:

- Updated `SSBaseComboBox` behavior so it no longer relied on checking
  `getBoundColumnText()` before writing changes. This avoided problems where RowSet
  reads did not reflect `setBoundColumnText()` changes until `updateRow()` completed.
- Updated data-navigator Add behavior so pending changes are committed before moving
  into insert mode.
- Deferred `performPreInsertOps()` through `SwingUtilities.invokeLater()` to improve
  sequencing around insert-mode transitions.
- Updated OWASP dependency-check Maven plugin to 6.1.1.

Human-friendly summary:

- Fixed subtle combo-box persistence/update behavior.
- Improved the reliability of data navigator insert workflows.
- Included build-security plugin maintenance.

Likely user impact:

- Combo-box edits should be less likely to be lost or skipped because of stale RowSet
  state.
- Add/insert behavior through the navigator should be more reliable when there are
  pending edits.

## SwingSet 4.0.2 — February 19, 2021

Tag/commit: `swingset-4.0.2` / `803a2bc`

Component-listener and combo-box behavior improvements.

Patch-derived details:

- Refactored listener management so `SSCommon` became more responsible for adding and
  removing component listeners based on component type.
- Introduced or exposed `SSDocumentListener` behavior as part of the common listener
  support.
- Moved toward lazier listener creation and cleaner listener lifecycle management.
- Improved component type handling for document-aware components.
- Added an `SSDBComboBox` constructor pattern that allows the query to be set later
  using `setQuery()` and executed with `execute()`.

Human-friendly summary:

- Improved how SwingSet attaches listeners to bound Swing components.
- Made listener behavior more centralized and maintainable.
- Improved flexibility for database-backed combo boxes whose query is not available at
  construction time.

Likely user impact:

- Users with custom components or subclasses may see cleaner listener behavior.
- Users building dynamic combo boxes gained a more flexible initialization path.

## SwingSet 4.0.1 — February 5, 2021

Tag/commit: `swingset-4.0.1` / `f0a7c6e`

Screen-helper cleanup and API visibility refinements.

Patch-derived details:

- Changed several `SS*ScreenHelper` methods from `public` to `protected`, clarifying
  that they are subclass extension points rather than general public API.
- Updated abstract/helper methods such as data-grid configuration, default column
  accessors, header accessors, grid editability hooks, rowset query hooks, and screen
  update hooks to better match intended subclass usage.
- Replaced older `getJMenuBar()` usage patterns with `getCustomMenu()`.
- Updated Javadocs and examples around helper method usage.
- Included version-preparation and changelog housekeeping.

Human-friendly summary:

- Tightened the 4.0.0 helper-class API.
- Reduced accidental public surface area.
- Made screen-helper subclassing expectations clearer.
- Improved consistency between demo/helper classes and the revised 4.x API.

Likely user impact:

- Code that called certain screen-helper methods directly from outside a subclass may
  need adjustment.
- Subclass-based usage should be more consistent after the visibility cleanup.

## SwingSet 4.0.0 — February 1, 2021

Tag/commit: `swingset-4.0.0` / `1829d6a`

Major rewrite and API modernization release.

Human-friendly summary:

- Major 4.x redesign and cleanup.
- Introduced broad internal and API changes that may require downstream code updates.
- Reworked screen-helper patterns and data-aware component support.
- Continued Maven-based project organization.
- Improved Javadocs, examples, and maintainability compared with earlier lines.
- This release is best treated as a major-version upgrade rather than a drop-in patch.

Likely user impact:

- Existing users of 2.x/3.x helper classes or component APIs may need source changes.
- Subclasses and custom screen-helper implementations should be reviewed carefully.
- Users benefit from a cleaner, more maintainable 4.x foundation.

## gl-strict-contains-workaround — January 22, 2021

Tag/commit: `gl-strict-contains-workaround` / `bc86f4e`

SwingSet 4 pre-release.

Human-friendly summary:

- Pre-release checkpoint for SwingSet 4 work.
- Added workarounds related to GlazedLists strict/contains behavior.
- Useful historical marker for the transition from the 3.x Mavenized codebase to the
  larger 4.0.0 rewrite.

## SwingSet 3.0.0 — January 10, 2020

Tag/commit: `swingset-3.0.0` / `9f7d51d`

First Maven release.

Human-friendly summary:

- Introduced Maven-based project structure and release/build process.
- Marked a major packaging and dependency-management modernization.
- Made the project easier to consume, build, test, and publish using standard Java
  tooling.
- Established the foundation for later 4.x modernization work.

## SwingSet 2.3.1 — November 11, 2019

Tag/commit: `swingset-2.3.1` / `ed7b9d1`

Small patch release.

Human-friendly summary:

- Small patch focused on `SSDataNavigator`.
- Likely addressed a narrow navigator behavior or maintenance issue rather than adding
  broad new functionality.
- Serves as the final 2.x tag before the first Maven release.

## SwingSet 2.3.0 — November 2019

Tag/commit: `swingset-2.3.0` / `44234e3`

Final larger 2.x release before the 2.3.1 patch and later Maven transition.

Human-friendly summary:

- Continued 2.x maintenance and cleanup.
- Prepared the codebase for the late 2.x patch line and later structural modernization.
- Preserved compatibility with the established 2.x component architecture.

## SwingSet 2.2.0 — February 27, 2019

Tag/commit: `swingset-2.2.0` / `56c3973`

Maintenance and improvement release in the 2.x line.

Human-friendly summary:

- Continued improvements to the 2.x SwingSet API and support classes.
- Maintained the established data-aware Swing component model.
- Further stabilized the project before the 2.3.x and 3.x transition.

## SwingSet 2.1.0 — September 18, 2018

Tag/commit: `swingset-2.1.0` / `9796880`

Maintenance and feature release in the 2.x line.

Human-friendly summary:

- Continued maintenance of the 2.x codebase after migration to GitHub.
- Preserved the pre-Maven project structure while moving toward more formal release
  management.
- Served as a stepping stone toward the 2.2.x and 2.3.x releases.

## SwingSet 2.0.0 — August/September 2018 tag history, released as 2012-era code

Tag/commit: `swingset-2.0.0` / `68252e8`

The tag message identifies this as code released for 2.0.0 on 2012-08-10, with the Git tag
appearing in September 2018.

Human-friendly summary:

- Represents the mature 2.0.0 code line as imported/tagged in GitHub.
- Provides a major historical checkpoint between the early CVS-era releases and the
  later actively Mavenized project.
- Preserves the pre-3.x architecture before the first Maven release.

## SwingSet 0.6.0 Beta — December 18, 2003

Tag/commit: `swingset-0-6-0-beta` / `88dec5c`

Early beta release.

This release continued expanding the early SwingSet API and examples. The project was
moving from proof-of-concept alpha toward a more usable toolkit with more complete
demo/support code and additional documentation.

Human-friendly summary:

- Continued development of the early data-aware component set.
- Expanded demo and sample support.
- Improved documentation and release notes around the early API.
- Continued work on table/grid, text-field, and editing behavior.

## SwingSet 0.5.0 Alpha — September 26, 2003

Tag/commit: `swingset-0-5-0-alpha` / `ad932c8`

Early alpha release.

Notable changes from the initial archival baseline include the first substantial public
shape of the project: sample code, documentation/changelog material, and early data-aware
Swing components. The project already centered on Swing widgets backed by database-aware
models and helper classes.

Human-friendly summary:

- Established the first recognizable alpha release of SwingSet.
- Added or organized early sample/demo material.
- Began documenting the project through changelog/FAQ-style files.
- Included early versions of core components such as data grids, table models, text
  fields, and cell editing support.

## arelease — September 25, 2003

Tag/commit: `arelease` / `02e8413`

Initial CVS-converted project baseline.

This tag represents the earliest imported project state available in the GitHub history.
It serves as the starting point for the public historical changelog. Because this tag was
manufactured from CVS history, it is best treated as an archival baseline rather than a
modern semantic release.

---

# Suggested migration notes by major era

## From pre-Maven releases to 3.x+

The move to 3.0.0 is primarily a build and packaging modernization. Users working from
old source drops or manual classpaths should adopt the Maven project structure.

## From 3.x to 4.0.0+

The 4.x line is a meaningful API/design modernization. Downstream applications that
subclass screen helpers, customize data navigators, or directly interact with internal
component helper methods should review source compatibility carefully.

## Within 4.0.x

The 4.0.x releases are not all simple dependency bumps. Several patch releases include
behavioral and API-adjacent changes:

- 4.0.1: helper method visibility and subclassing API cleanup.
- 4.0.2: listener management and delayed-query combo-box construction.
- 4.0.3: combo-box update/persistence fix and navigator insert sequencing.
- 4.0.4: combo-box selection preservation and shared update refactor.
- 4.0.8: formatted text-field validation/decorator improvements.
- 4.0.9: data navigator action-map/customization improvements.
- 4.0.10: data-grid helper and `SSCellEditing` migration path.
- 4.0.11: advanced demo, parent-aware grid helper fixes, and list/combo formatting fallback.
- 4.0.12: dependency/plugin/doc maintenance.
- 4.0.13: dependency/plugin updates, Markdown documentation conversion, AI changelog, and release-prep cleanup.

---

# Source URLs used for review

Tag pages:

- https://github.com/bpangburn/swingset/tags
- https://github.com/bpangburn/swingset/tags?after=swingset-4.0.3
- https://github.com/bpangburn/swingset/tags?after=swingset-2.0.0
- https://github.com/bpangburn/swingset/tags?after=swingset-0-6-0-beta

Selected compare/patch URLs reviewed, especially for 4.x details:

- https://github.com/bpangburn/swingset/compare/swingset-4.0.0...swingset-4.0.1.patch
- https://github.com/bpangburn/swingset/compare/swingset-4.0.1...swingset-4.0.2.patch
- https://github.com/bpangburn/swingset/compare/swingset-4.0.2...swingset-4.0.3.patch
- https://github.com/bpangburn/swingset/compare/swingset-4.0.3...swingset-4.0.4.patch
- https://github.com/bpangburn/swingset/compare/swingset-4.0.4...swingset-4.0.5.patch
- https://github.com/bpangburn/swingset/compare/swingset-4.0.5...swingset-4.0.6.patch
- https://github.com/bpangburn/swingset/compare/swingset-4.0.6...swingset-4.0.7.patch
- https://github.com/bpangburn/swingset/compare/swingset-4.0.7...swingset-4.0.8.patch
- https://github.com/bpangburn/swingset/compare/swingset-4.0.8...swingset-4.0.9.patch
- https://github.com/bpangburn/swingset/compare/swingset-4.0.9...swingset-4.0.10.patch
- https://github.com/bpangburn/swingset/compare/swingset-4.0.10...swingset-4.0.11.patch
- https://github.com/bpangburn/swingset/compare/swingset-4.0.11...swingset-4.0.12.patch
