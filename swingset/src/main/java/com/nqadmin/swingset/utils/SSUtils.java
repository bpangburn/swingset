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
package com.nqadmin.swingset.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.StackLocatorUtil;

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
	 * @return the Logger
	 */
	public static Logger getLogger() {
		// NOTE: this can be re-implemented by examining
		// new Throwable().getStackTrace();
		Logger logger;
		try {
			return LogManager.getLogger(StackLocatorUtil.getCallerClass(2));
		} catch(UnsupportedOperationException ex) {}
		logger = LogManager.getRootLogger();
		// Note: can check for root logger with
		// logger.getName().isEmpty()
		logger.error("Using RootLogger", new Throwable());
		return logger;
	}

	/**
	 * Returns an unmodifiable list containing an arbitrary number of elements.
	 * This is not particularly efficient for small lists, but until java-9...
	 * @param <T> type of elements in the list
	 * @param args the elements of the list
	 * @return list
	 */
	@SafeVarargs
	public static <T> List<T> listOf(T... args) {
		Object[] arr = Arrays.copyOf(args, args.length);
		@SuppressWarnings("unchecked")
		List<T> list = (List<T>) Collections.unmodifiableList(Arrays.asList(arr));
		return list;
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

	////////////////////////////////////////////////////////////////////////////
	//
	// EventBus
	//
	//     posting Events
	//     finding a bus
	//
	// TODO:

}
