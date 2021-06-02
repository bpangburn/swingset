package com.nqadmin.swingset.formatting.factories;
import javax.swing.text.MaskFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LocalDateFormatterFactory extends javax.swing.text.DefaultFormatterFactory {

	/**
	 * Log4j Logger for component
	 */
	private static Logger logger = LogManager.getLogger();
	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = 1L;

	private MaskFormatter defaultFormatter = new LocalDateMaskFormatter();
	//private MaskFormatter nullFormatter = new NullMaskFormatter();;

	/**
	 * Creates a default SSSSNFormatterFactory
	 */
	public LocalDateFormatterFactory() {

		setDefaultFormatter(defaultFormatter);
		//setNullFormatter(nullFormatter);
		//setEditFormatter(defaultFormatter);
		//setDisplayFormatter(defaultFormatter);

	}

}
