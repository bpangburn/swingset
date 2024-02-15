/* *****************************************************************************
 * Copyright (C) 2021, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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
/* *****************************************************************************
 * The conditions in the above copyright notice apply to this copyright notice.
 * Additions and modifications made by Ernie R. Rael are
 * copyright (C) 2024, Ernie R. Rael. All rights reserved.
 * ****************************************************************************/
package com.nqadmin.swingset.utils;

import java.awt.Toolkit;

import java.lang.StackWalker.Option;

import java.lang.System.Logger;

/**
 *
 * @author err
 */
public class SSUtils {
	private SSUtils() {}

	/**
	 * This is similar to LogManager.getLogger(), except that
	 * if getLogger fails then this method returns the root logger.
	 * So this is suitable for UI components that might get instantiated
	 * by a gui builder.
	 *
	 * See: https://github.com/bpangburn/swingset/pull/123
	 * 
	 * @return the Logger
	 */
	public static Logger getLogger() {
		Class<?> cc = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE)
				.getCallerClass();
		return System.getLogger(cc.getName());

		// // NOTE: this can be re-implemented by examining
		// // new Throwable().getStackTrace();
		// Logger logger;
		// try {
		// 	return LogManager.getLogger(StackLocatorUtil.getCallerClass(2));
		// } catch(UnsupportedOperationException ex) {}
		// logger = LogManager.getRootLogger();
		// // Note: can check for root logger with
		// // logger.getName().isEmpty()
		// logger.log(ERROR, "Using RootLogger", new Throwable());
		// return logger;
	}

	/**
	 * Shorthand for "String.format(fmt, args)".
	 * @param fmt format
	 * @param args args
	 * @return string
	 */
	public static String sf(String fmt, Object... args) {
		return args.length == 0 ? fmt : String.format(fmt, args);
	}

	// /**
	//  * Shorthand for "() -> String.format(fmt, args)".
	//  * @param fmt format
	//  * @param args args
	//  * @return string
	//  */
	// public static Supplier<String> ssf(String fmt, Object... args) {
	// 	return () -> sf(fmt, args);
	// }

	/**
	 * Notify the user of something...
	 */
	// TODO: add option to flash window/panel...
	public static void beep()
	{
		Toolkit.getDefaultToolkit().beep();
	}

	////////////////////////////////////////////////////////////////////////////
	//
	// Debug Support
	//

	/**
	 * Return a unique name for an Object, for example "String@89AB".
	 * Name is SimpleClassName followed by identityHashCode in hex.
	 * Used primarily for debug messages.
	 * @param o The Object
	 * @return unique name for the object or "null"
	 */
	// TODO: put this in utils/SSUtil
	public static String objectID(Object o) {
		if (o == null) {
			return "null";
		}
		return String.format("%s@%X", o.getClass().getSimpleName(), System.identityHashCode(o));
	}

}
