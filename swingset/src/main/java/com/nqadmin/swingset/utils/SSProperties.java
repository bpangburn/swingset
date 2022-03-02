/*******************************************************************************
 * Copyright (C) 2003-2021, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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
 ******************************************************************************/
package com.nqadmin.swingset.utils;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// SSProperties.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * Loads GUI default properties for SwingSet
 */
public class SSProperties {
	
	/**
	 * Log4j Logger for component
	 */
	private static Logger logger = SSUtils.getLogger();
	
	/**
	 * SwingSet key/value pair properties from swingset.properties file.
	 */
	private static Properties ssProps = null;
	
	/**
	 * Name of SwingSet properties file located with other project resource files.
	 */
	
	private final static String ssPropsFileName = "swingset.properties";
	
	/**
	 * Obtains the contents of the SwingSet properties file.
	 * 
	 * @return SwingSet properties object
	 */
	public static Properties getProperties() {
		if (ssProps==null) {
			ssProps = new Properties();
			try {

				ssProps.load(SSProperties.class.getClassLoader().getResourceAsStream(ssPropsFileName));
		
			} catch (final IOException _ioe) {
				logger.error("Unable to load SwingSet properties file.",_ioe);
			}
		}
		
		return ssProps;
	}
	
	/**
	 * Main method for testing loading of SwingSet properties file.
	 * <p>
	 * @param _args - optional command line arguments, which are ignored by this program
	 */
	public static void main(final String _args[]) {
	
		final Properties myProps = getProperties();
		
	    // Print all properties to the console
		for(final Entry<Object, Object> e : myProps.entrySet()) {
			System.out.println(e);
		}

	}

}
