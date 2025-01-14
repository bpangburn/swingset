/* *****************************************************************************
 * Copyright (C) 2024, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * Contributors:
 *   Prasanth R. Pasala
 *   Brian E. Pangburn
 *   Diego Gil
 *   Man "Bee" Vo
 *   Ernie R. Rael
 * ****************************************************************************/
package com.nqadmin.swingset.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.lang.System.Logger;
import static java.lang.System.Logger.Level.*;

// TODO: could add isNull() method for 0.0.0-SNAPSHOT
// TODO: could add getMajor(), ... methods
// TODO: could handle optional "<branch>branch-name</branch> i the pom

/**
 * Version string is of the form "{@literal <digits>.<digits>.<digits>-SNAPSHOT}"
 * where "-SNAPSHOT" is optional and missing components are 0; so 4.1 is treated
 * as 4.1.0.
 * <p>
 * In addition, a version may end in {@literal -<anything>} and is
 * considered pre-release. The pre-release string is converted to lower case.<br>
 * For comparisons regular-release {@literal >} pre-release {@literal >} snapshot.<br>
 * For example 1.2.3 {@literal >} 1.2.3-RC1 {@literal >} 1.2.3-SNAPSHOT.<br>
 * For example 1.2.3-RC2 {@literal >} 1.2.3-RC1
 * <p>
 * 
 *{@linkplain SSVersion} is comparable, for example,
 * {@snippet :
 * if (SSVersion.get().compareTo(SSVersion.get("4.0.11")) >= 0) {
 *     // Do this for SwingSet version 4.0.11 and later
 * }
 * assert SSVersion.get("4.0").compareTo(SSVersion.get("4.0.0")) == 0;
 * assert SSVersion.get("4.0.11-SNAPSHOT").compareTo(SSVersion.get("4.0.11")) < 0;
 * }
 */
public class SSVersion implements Comparable<SSVersion> {
	/** Log4j Logger for component */
	private static final Logger logger = SSUtils.getLogger();

	/** Name of SwingSet version resource file. */
	private final static String SS_VERSION_FILENAME = "swingsetVersion.properties";

	/** Name of version key in resource file. */
	private final static String VERSION_KEY = "version";

	/** Use all zeros version if there's a parsing error. */
	private final static String ERROR_VERSION = "0.0.0-SNAPSHOT";

	/** Versions that end with this are considered snapshots. */
	private final static String SNAP = "-SNAPSHOT";

	private static SSVersion SINGLETON;

	/**
	 * Returns the singleton SSVersion of the running SwingSet library.
	 * If the version property is not found, or there is a parse error,
	 * then 0.0.0-SNAPSHOT is used.
	 * @return version
	 */
	public static SSVersion get() {
		if (SINGLETON == null) {
			SINGLETON = parse(getVersionProperty());
		}
		return SINGLETON;
	}

	/**
	 * Returns the SSVersion resulting from parsing the {@code sVer}
	 * param. If there is a parse error, then 0.0.0-SNAPSHOT is used.
	 * The return values are useful for comparing.
	 * @param sVer version string to parse
	 * @return version
	 */
	public static SSVersion get(String sVer) {
		return parse(sVer);
	}

	// These three values are used to specify the type of version, used for compare
	private static final int VER_REGULAR = 3;
	private static final int VER_PRE_RELEASE = 2;
	private static final int VER_SNAPSHOT = 1;

	/** Version number components. */
	private final List<Integer> versionSequence;
	/** Is the version a snapshot? */
	private final boolean isSnapshot;
	private final String preRelease;
	private final int versionType;

	private SSVersion(List<Integer> _versionSequence, boolean _isSnapshot, String _pre) {
		versionSequence = new ArrayList<>(_versionSequence);
		isSnapshot = _isSnapshot;
		preRelease = _pre.toLowerCase();
		versionType = !preRelease.isEmpty() ? VER_PRE_RELEASE
				: isSnapshot ? VER_SNAPSHOT : VER_REGULAR;
	}

	/**
	 * Check if this release is a "-SNAPSHOT".
	 * @return true if snapshot
	 */
	public boolean isSnapshot() {
		return isSnapshot;
	}

	/**
	 * Check if this release is a pre-release; has "-xxx" appended.
	 * @return true if pre-release
	 */
	public boolean isPreRelease() {
		return !preRelease.isEmpty();
	}

	/**
	 * Get the lower case pre-release string; empty if not a prerelease.
	 * @return pre-release string as lower case
	 */
	public String preRelease() {
		return preRelease;
	}
	/** {@inheritDoc} */
	@Override
	public int compareTo(SSVersion o) {
		for (int i = 0; i < versionSequence.size(); ++i) {
			int diff = versionSequence.get(i) - o.versionSequence.get(i);
			if (diff != 0) {
				return diff;
			}
		}
		// The 'x.y.z' parts are equal
		if (versionType == VER_PRE_RELEASE && o.versionType == VER_PRE_RELEASE) {
			return preRelease.compareToIgnoreCase(o.preRelease);
		}
		return versionType - o.versionType;
	}

	/** Parse a version string; return parse of "0.0.0-SNAPSHOT" if parse error.
	 * @param _sVer version string to parse
	 * @return An SSVersion
	 */
	private static SSVersion parse(String _sVer) {
		String sVer = _sVer != null ? _sVer : ERROR_VERSION;
		boolean isSnap = false;
		boolean is_error = false;
		String pre = "";
		if (sVer.endsWith(SNAP)) {
			// strip off the "-SNAPSHOT"
			sVer = sVer.substring(0, sVer.length() - SNAP.length());
			isSnap = true;
		}
		int preIdx = sVer.indexOf('-');
		if (preIdx >= 0) {
			// remove the pre release indicator
			if (preIdx + 1 == sVer.length() || isSnap) {
				// a version tag of only "-" or a x.y.z-pre-SNAPSHOT
				is_error = true;
			} else {
				pre = sVer.substring(preIdx + 1);
				sVer = sVer.substring(0, preIdx);
			}
		}
		if (!is_error) {
			Matcher m = Pattern.compile("^(\\d+)(\\.(\\d+))?(\\.(\\d+))?$").matcher(sVer);
			// group({1, 3, 5}) are the components
			if (m.matches()) {
				List<Integer> seq = new ArrayList<>();
				seq.add(Integer.valueOf(m.group(1)));
				seq.add(m.group(3) != null ? Integer.valueOf(m.group(3)) : (Integer)0);
				seq.add(m.group(5) != null ? Integer.valueOf(m.group(5)) : (Integer)0);
				return new SSVersion(seq, isSnap, pre);
			}
		}
                logger.log(ERROR, () -> "Version parse error: " + _sVer);
		return parse(ERROR_VERSION);
	}

	/**
	 * Return the SwingSet version number or null if there is an issue.
	 * 
	 * @return the SwingSet version number or null if there is an issue.
	 */
	private static String getVersionProperty() {
		String ver = null;
		Properties props = new Properties();
		try {
			props.load(SSVersion.class.getClassLoader().getResourceAsStream(SS_VERSION_FILENAME));
			ver = props.getProperty(VERSION_KEY);
		} catch (IOException | NullPointerException ex) {
			logger.log(ERROR, "Unable to load SwingSet version properties file.", ex);
		}

		return ver;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return versionSequence.get(0)
				+ "." + versionSequence.get(1)
				+ "." + versionSequence.get(2)
				+ (isSnapshot ? "-SNAPSHOT" : "")
				+ (preRelease.isEmpty() ? "" : "-" + preRelease());
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		int hash = 5;
		hash = 53 * hash + Objects.hashCode(this.versionSequence);
		hash = 53 * hash + (this.isSnapshot ? 1 : 0);
		return hash;
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final SSVersion other = (SSVersion) obj;
		if (this.isSnapshot != other.isSnapshot) {
			return false;
		}
		return Objects.equals(this.versionSequence, other.versionSequence);
	}
}
