package com.nqadmin.swingset.formatting.factories;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.swing.text.MaskFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
 * We can create a class that extends a javax.swing.text.DefaultFormatterFactory.
 * e.g., public class SSDateFormatterFactory extends javax.swing.text.DefaultFormatterFactory {
 * 
 * Then we can construct a JFormattedTextField or an SSFormattedTextField with the factory as a parameter.
 * 
 * By creating a factory, we take care of setting the Formatter for various states of the Swing component:
 *  - setNullFormatter(): Null (used when the field has a null value)
 *  - setDefaultFormatter(): Default (used unless overridden at a finer-grained level)
 *  - setDisplayFormatter(): Display (used when not editing/field does not have focus)
 *  - setEditFormatter(): Editing (used when the field has the focus and editing can occur)
 *  
 * Per https://docs.oracle.com/javase/8/docs/api/javax/swing/text/DefaultFormatterFactory.html
 *  1. Is the passed in value null? Use the null formatter.
 *  2. Does the JFormattedTextField have focus? Use the edit formatter.
 *  3. Otherwise, use the display formatter.
 *  4. If a non-null AbstractFormatter has not been found, use the default formatter.
 *  
 * Note that these are all implementations of AbstractFormatter:
 * https://docs.oracle.com/javase/8/docs/api/javax/swing/JFormattedTextField.AbstractFormatter.html
 * 
 * The new java.time package complicates further because there does not appear to be any native support
 * in JFormattedTextFields for java.time.
 * 
 * java.time has it's own formatter, DateTimeFormatter:
 *   https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/format/DateTimeFormatter.html
 *   
 * There is also a DateFormatter, which takes a DateFormat such as SimpleDateFormat:
 *   https://docs.oracle.com/javase/8/docs/api/javax/swing/text/DateFormatter.html, which is an implementation of AbstractFormatter
 *   https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html
 *   
 * DateFormatter & DateTimeFormatter do NOT appear to be related/compatible in any way.
 * 
 * To avoid the java.date packages it's probably best to avoid DateFormatter and SimpleDateFormat completely.
 * 
 * Better to create a new implementation of AbstractFormatter or extend MaskFormatter and overwrite stringToValue() and
 * valueToString().
 * 
 * For SwingSet we also have to deal with SQL Date, Time, and TimeStamp. In JDBC 4.2 there is support to automatically
 * cast back and forth from the java.sql to java.time. However H2, which is used for the SwingSet demo does not yet support
 * JDBC 4.2 (supports 4.1) so for legacy support, we can't rely upon JDBC 4.2 features yet. See
 * https://github.com/bpangburn/swingset/issues/16#issuecomment-842603199 for additional discussion.
 * 
 * Seems the best approach is to developer FormatterFactory classes for java.time.LocalDate, java.time.LocalTime, and
 * java.time.LocalDateTime (and then possibly add versions with the timezone).
 * 
 * We can then add methods/code to RowSetOps to handle conversion from java.sql to java.time
 * 
 * Finally, we could deprecate the existing SSDateField, SSTimeField, & SSTimestampField.
 * 
 * Regarding timezones/points in time: https://stackoverflow.com/questions/8530545/java-sql-date-time
 * 
 * BEWARE: If you are trying to track actual moments, specific points on the timeline, then all this code above is wrong.
 * Search Stack Overflow to learn about Instant, ZoneId, and ZonedDateTime classes. Search both Stack Overflow and
 * dba.StackExchange.com to learn about the SQL-standard type TIMESTAMP WITH TIME ZONE.
 */

public class LocalDateMaskFormatter extends MaskFormatter {
	
	/**
	 * Enumeration for date formats.
	 */
	@SuppressWarnings("javadoc")
	public enum DateFormat {
		YYYYMMDD, MMDDYYYY, DDMMYYYY;
	}
	
	private char separatorCharacter = '-';
	private DateFormat dateFormat = null;
	private DateTimeFormatter formatter = null;
	private String emptyMask = null;
	

	private static final long serialVersionUID = 1L;
	
	/**
	 * Log4j Logger for component
	 */
	private static Logger logger = LogManager.getLogger();
	
	public LocalDateMaskFormatter(DateFormat _dateFormat, char _separatorCharacter, char _placeholderCharacter) {
		try {
			setDateFormat(_dateFormat);
			setSeparatorCharacter(_separatorCharacter);
			setPlaceholderCharacter(_placeholderCharacter);
			setAllowsInvalid(false);
			setOverwriteMode(true);
			setValueClass(LocalDate.class);
			updateMaskAndFormatter();
		} catch (ParseException _pe) {
			_pe.printStackTrace();
		} catch (Exception _e) {
			_e.printStackTrace();
		}
	}

	public LocalDateMaskFormatter() { // set mask and placeholder
		this(DateFormat.YYYYMMDD, '-', '_');
	}
	
	protected void updateMaskAndFormatter() throws Exception {
		switch(dateFormat) {
		case YYYYMMDD:
			setMask("####" + separatorCharacter + "##" + separatorCharacter + "##");
			formatter = DateTimeFormatter.ofPattern("yyyy" + separatorCharacter + "MM" + separatorCharacter + "dd");
			break;
		case MMDDYYYY:
			setMask("##" + separatorCharacter + "##" + separatorCharacter + "####");
			formatter = DateTimeFormatter.ofPattern("MM" + separatorCharacter + "dd" + separatorCharacter + "yyyy");
			break;
		case DDMMYYYY:
			setMask("##" + separatorCharacter + "##" + separatorCharacter + "####");
			formatter = DateTimeFormatter.ofPattern("dd" + separatorCharacter + "MM" + separatorCharacter + "yyyy");
			break;
		default:
			logger.debug("Encountered unknown DateFormat of: {}.", ()->dateFormat);
			throw new Exception("Encountered unknown DateFormat of: " + dateFormat + ".");
		}

		// Create an "empty" version of the mask where the placeholder character is shown for all fillable characters
		emptyMask = getMask().replaceAll("#", String.valueOf(getPlaceholderCharacter()));
	}

	@Override
	public Object stringToValue(String _string) throws ParseException {
		
		logger.debug("stringToValue() passed " + _string);
		
		LocalDate result = null;
		
		if (_string!=null) {
			try {
				result = LocalDate.parse(_string, formatter);
			} catch (DateTimeParseException _dtpe) {
				logger.debug("DateTimeParseException for " + _string + ". Returning null.");
			}
		}
		
		return result;
		
	}

	@Override
	public String valueToString(Object _value) throws ParseException {
		
		logger.debug("valueToString() passed " + _value);
		
		String result;
		
		if (_value!=null) {
			result = ((LocalDate)_value).format(formatter);
		} else if (getFormattedTextField().hasFocus()) {
		// When the field has the focus, we don't want to display null because the field will not be editable.
		// Instead show a string comprised of the placeholder for all fillable characters and the separator character
		// where expected.
			result = emptyMask;
		} else {
		// When the field does not have the focus, we want to show null so the field is blank.
			result = null;
		}
		
		return result;

	}

	/**
	 * @return the separator
	 */
	public char getSeparatorCharacter() {
		return separatorCharacter;
	}

	/**
	 * @param _separatorCharacter the separator to set
	 */
	public void setSeparatorCharacter(char _separatorCharacter) {
		separatorCharacter = _separatorCharacter;
	}

	/**
	 * @return the dateFormat
	 */
	public DateFormat getDateFormat() {
		return dateFormat;
	}

	/**
	 * @param _dateFormat the dateFormat to set
	 */
	public void setDateFormat(DateFormat _dateFormat) {
		dateFormat = _dateFormat;
	}
}
