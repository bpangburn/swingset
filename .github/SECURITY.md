# Security Policy

## Supported Versions

SwingSet is maintained on a best-effort basis. The actively maintained 4.x
line is developed on `master` and `4.0.14-SNAPSHOT`, with Java 8
compatibility preserved for the core library.

| Version / Branch | Security Updates |
| --- | --- |
| 4.0.x (`master` and `4.0.14-SNAPSHOT`) | Supported on a best-effort basis |
| 5.x (`SS-5-WRAPPER` and `SS-5-TESTING`) | Experimental development; security fixes are not guaranteed |
| Earlier than 4.0 | Not actively supported |

The 5.x branches may use newer Java requirements and are not yet represented
as a supported, stable 5.x release line. This policy can be updated when 5.x
releases are ready. Fixes for older versions may be considered at the
maintainers' discretion.

## Reporting a Vulnerability

Please submit potential vulnerabilities **privately** through GitHub's
[Report a vulnerability](https://github.com/bpangburn/swingset/security/advisories/new)
feature.

Please do not open a public GitHub issue or disclose exploit details before
the maintainers have had an opportunity to investigate.

A useful report includes:

- A description of the vulnerability and the potential security impact.
- The affected version, branch, or commit.
- Reproduction steps or a proof of concept, if available.
- Suggested mitigations or fixes, if known.

## Response and Disclosure

SwingSet is maintained on a best-effort basis. Maintainers will **aim to
acknowledge reports within seven days**, but cannot guarantee response or
remediation deadlines.

Reports will be reviewed for reproducibility and security impact. When a
vulnerability is confirmed, maintainers will endeavor to coordinate a fix and
responsible disclosure; a GitHub security advisory or patched release may be
published when appropriate.

Not every report will result in a software update or security advisory.
