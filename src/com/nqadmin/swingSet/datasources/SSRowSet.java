/*******************************************************************************
 * Copyright (C) 2003-2019, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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
 ******************************************************************************/

 package com.nqadmin.swingSet.datasources;

 import java.io.Serializable;
 import javax.sql.RowSetListener;

 import java.sql.Array;
 import java.sql.SQLException;
 import java.sql.Date;
 import java.sql.ResultSetMetaData;
 import java.sql.Timestamp;
 import java.sql.Time;

 /**
  * SSRowSet.java
  * 
  * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
  * 
  * This interface provides functions which can be used by other swingset components
  * to get the values or update the values of a given column. This interface is a
  * stripped down version of RowSet interface.
  */
 public interface SSRowSet extends Serializable{
	 
	 /**
     * Retrieves the value of the designated column in the current row of this DataSet
     * object as a boolean in the Java programming language.
     * @param columnIndex - column number . first column is 1, second column is 2....
     * @return returns the column value of the current row, if the value is null then 0
     * is returned.
     * @throws SQLException - if a database access error occurs
     */
    public boolean getBoolean(int columnIndex) throws SQLException;

	/**
	 * Retrieves the value of the designated column in the current row of this DataSet
	 * object as an array in the Java programming language.
	 * @param columnIndex - column number . first column is 1, second column is 2....
	 * @return returns the column value of the current row.
     * @throws SQLException - if a database access error occurs
	 */	 
	public Array getArray(int columnIndex) throws SQLException;

    /**
     * Retrieves the value of the designated column in the current row of this DataSet
     * object as a int in the Java programming language.
     * @param columnIndex - column number . first column is 1, second column is 2....
     * @return returns the column value of the current row, if the value is null then 0
     * is returned.
     * @throws SQLException - if a database access error occurs
     */
    public int getInt(int columnIndex) throws SQLException;

    /**
     * Retrieves the value of the designated column in the current row of this DataSet
     * object as a long in the Java programming language.
     * @param columnIndex - column number . first column is 1, second column is 2....
     * @return returns the column value of the current row, if the value is null then 0
     * is returned.
     * @throws SQLException - if a database access error occurs
     */
    public long getLong(int columnIndex) throws SQLException;

    /**
     * Retrieves the value of the designated column in the current row of this DataSet
     * object as a float in the Java programming language.
     * @param columnIndex - column number . first column is 1, second column is 2....
     * @return returns the column value of the current row, if the value is null then 0
     * is returned.
     * @throws SQLException - if a database access error occurs
     */
    public float getFloat(int columnIndex) throws SQLException;

    /**
     * Retrieves the value of the designated column in the current row of this DataSet
     * object as a double in the Java programming language.
     * @param columnIndex - column number . first column is 1, second column is 2....
     * @return returns the column value of the current row, if the value is null then 0
     * is returned.
     * @throws SQLException - if a database access error occurs
     */
    public double getDouble(int columnIndex) throws SQLException;

    /**
     * Retrieves the value of the designated column in the current row of this DataSet
     * object as a String in the Java programming language.
     * @param columnIndex - column number . first column is 1, second column is 2....
     * @return returns the column value of the current row, if the value is null then a null
     * is returned.
     * @throws SQLException - if a database access error occurs
     */
    public String getString(int columnIndex) throws SQLException;

    /**
     * Retrieves the value of the designated column in the current row of this DataSet
     * object as a Date in the Java programming language.
     * @param columnIndex - column number . first column is 1, second column is 2....
     * @return returns the column value of the current row, if the value is null then null
     * is returned.
     * @throws SQLException - if a database access error occurs
     */
    public Date getDate(int columnIndex) throws SQLException;

    /**
     * Retrieves the value of the designated column in the current row of this ResultSet
     * object as a byte array in the Java programming language. The bytes represent the
     * raw values returned by the driver.
     * @param columnIndex - index number of the column
     * @return  returns the column value; if the value is SQL NULL, the value returned is null 
     * @throws SQLException - if a database access error occurs
     */    
    public byte[] getBytes(int columnIndex) throws SQLException;

    /**
     * Updates the designated column with a boolean value. The updater methods are used to
     * update column values in the current row or the insert row. The updater methods do
     * not update the underlying data source; instead the updateRow or insertRow methods are called
     * to update the underlying data source.
     * @param columnIndex - index number of the column. first column is 1, second column is 2......
     * @param x - new column value
     * @throws SQLException - if a database access error occurs
     */
    public void updateBoolean(int columnIndex, boolean x) throws SQLException;
    
    /**
     * Updates the designated column with an array value. The updater methods are used to
     * update column values in the current row or the insert row. The updater methods do
     * not update the underlying data source; instead the updateRow or insertRow methods are called
     * to update the underlying data source.
     * @param columnIndex - index of the column
     * @param x - new column value
     * @throws SQLException - if a database access error occurs
     */
    public void updateArray(int columnIndex, Array x) throws SQLException;

    /**
     * Updates the designated column with a int value. The updater methods are used to
     * update column values in the current row or the insert row. The updater methods do
     * not update the underlying data source; instead the updateRow or insertRow methods are called
     * to update the underlying data source.
     * @param columnIndex - index number of the column. first column is 1, second column is 2......
     * @param x - new column value
     * @throws SQLException - if a database access error occurs
     */
    public void updateInt(int columnIndex, int x) throws SQLException;

    /**
     * Updates the designated column with a long value. The updater methods are used to
     * update column values in the current row or the insert row. The updater methods do
     * not update the underlying data source; instead the updateRow or insertRow methods are called
     * to update the underlying data source.
     * @param columnIndex - index number of the column. first column is 1, second column is 2......
     * @param x - new column value
     * @throws SQLException - if a database access error occurs
     */
    public void updateLong(int columnIndex, long x) throws SQLException;

    /**
     * Updates the designated column with a float value. The updater methods are used to
     * update column values in the current row or the insert row. The updater methods do
     * not update the underlying data source; instead the updateRow or insertRow methods are called
     * to update the underlying data source.
     * @param columnIndex - index number of the column. first column is 1, second column is 2......
     * @param x - new column value
     * @throws SQLException - if a database access error occurs
     */
    public void updateFloat(int columnIndex, float x) throws SQLException;

    /**
     * Updates the designated column with a double value. The updater methods are used to
     * update column values in the current row or the insert row. The updater methods do
     * not update the underlying data source; instead the updateRow or insertRow methods are called
     * to update the underlying data source.
     * @param columnIndex - index number of the column. first column is 1, second column is 2......
     * @param x - new column value
     * @throws SQLException - if a database access error occurs
     */
    public void updateDouble(int columnIndex, double x) throws SQLException;

    /**
     * Updates the designated column with a String value. The updater methods are used to
     * update column values in the current row or the insert row. The updater methods do
     * not update the underlying data source; instead the updateRow or insertRow methods are called
     * to update the underlying data source.
     * @param columnIndex - index number of the column. first column is 1, second column is 2......
     * @param x - new column value
     * @throws SQLException - if a database access error occurs
     */
    public void updateString(int columnIndex, String x) throws SQLException;

    /**
     * Updates the designated column with a Date value. The updater methods are used to
     * update column values in the current row or the insert row. The updater methods do
     * not update the underlying data source; instead the updateRow or insertRow methods are called
     * to update the underlying data source.
     * @param columnIndex - index number of the column. first column is 1, second column is 2......
     * @param x - new column value
     * @throws SQLException - if a database access error occurs
     */
    public void updateDate(int columnIndex, Date x) throws SQLException;

    /**
     * Updates the designated column with a null value. The updater methods are used to
     * update column values in the current row or the insert row. The updater methods do
     * not update the underlying data source; instead the updateRow or insertRow methods are called
     * to update the underlying data source.
     * @param columnIndex - index number of the column. first column is 1, second column is 2......
     * @throws SQLException - if a database access error occurs
     */
    public void updateNull(int columnIndex) throws SQLException;

    /**
     * Updates the designated column with a byte array value. The updater methods are
     * used to update column values in the current row or the insert row. The updater
     * methods do not update the underlying database; instead the updateRow or insertRow
     * methods are called to update the database.
     * @param columnIndex - the index number of the column
     * @param x - the new column value 
     * @throws SQLException - if a database access error occurs
     */
    public void updateBytes(int columnIndex, byte[] x) throws SQLException;
    
    /**
     * Retrieves the value of the designated column in the current row of this DataSet
     * object as a boolean in the Java programming language.
     * @param columnName - name of the column
     * @return returns the column value of the current row, if the value is null then a false
     * is returned.
     * @throws SQLException - if a database access error occurs
     */
    public boolean getBoolean(String columnName) throws SQLException;
    
    /**
     * Retrieves the value of the designated column in the current row of this DataSet
     * object as an array in the Java programming language.
     * @param columnName - Name of the column.
     * @return returns the column value of the current row.
     * @throws SQLException - if a database access error occurs
     */
    public Array getArray(String columnName) throws SQLException;

    /**
     * Retrieves the value of the designated column in the current row of this DataSet
     * object as a int in the Java programming language.
     * @param columnName - name of the column
     * @return returns the column value of the current row, if the value is null then 0
     * is returned.
     * @throws SQLException - if a database access error occurs
     */
    public int getInt(String columnName) throws SQLException;

    /**
     * Retrieves the value of the designated column in the current row of this DataSet
     * object as a long in the Java programming language.
     * @param columnName - name of the column
     * @return returns the column value of the current row, if the value is null then 0
     * is returned.
     * @throws SQLException - if a database access error occurs
     */
    public long getLong(String columnName) throws SQLException;

    /**
     * Retrieves the value of the designated column in the current row of this DataSet
     * object as a float in the Java programming language.
     * @param columnName - name of the column
     * @return returns the column value of the current row, if the value is null then 0
     * is returned.
     * @throws SQLException - if a database access error occurs
     */
    public float getFloat(String columnName) throws SQLException;

    /**
     * Retrieves the value of the designated column in the current row of this DataSet
     * object as a double in the Java programming language.
     * @param columnName - name of the column
     * @return returns the column value of the current row, if the value is null then 0
     * is returned.
     * @throws SQLException - if a database access error occurs
     */
    public double getDouble(String columnName) throws SQLException;

    /**
     * Retrieves the value of the designated column in the current row of this DataSet
     * object as a String in the Java programming language.
     * @param columnName - name of the column
     * @return returns the column value of the current row, if the value is null then a null
     * is returned.
     * @throws SQLException - if a database access error occurs
     */
    public String getString(String columnName) throws SQLException;

    /**
     * Retrieves the value of the designated column in the current row of this DataSet
     * object as a Date in the Java programming language.
     * @param columnName - name of the column
     * @return returns the column value of the current row, if the value is null then a null
     * is returned.
     * @throws SQLException - if a database access error occurs
     */
    public Date getDate(String columnName) throws SQLException;
    
    /**
     * Retrieves the value of the designated column in the current row of this ResultSet
     * object as a byte array in the Java programming language. The bytes represent the
     * raw values returned by the driver.
     * @param columnName - the SQL name of the column
     * @return  returns the column value; if the value is SQL NULL, the value returned is null 
     * @throws SQLException - if a database access error occurs
     */    
    public byte[] getBytes(String columnName) throws SQLException;

    /**
     * Updates the designated column with a boolean value. The updater methods are used to
     * update column values in the current row or the insert row. The updater methods do
     * not update the underlying data source; instead the updateRow or insertRow methods are called
     * to update the underlying data source.
     * @param columnName - name of the column
     * @param x - new column value
     * @throws SQLException - if a database access error occurs
     */
    public void updateBoolean(String columnName, boolean x) throws SQLException;

    /**
     * Updates the designated column with an array value. The updater methods are used to
     * update column values in the current row or the insert row. The updater methods do
     * not update the underlying data source; instead the updateRow or insertRow methods are called
     * to update the underlying data source.
     * @param columnName - name of the column
     * @param x - new column value
     * @throws SQLException - if a database access error occurs
     */
    public void updateArray(String columnName, Array x) throws SQLException;

    /**
     * Updates the designated column with a int value. The updater methods are used to
     * update column values in the current row or the insert row. The updater methods do
     * not update the underlying data source; instead the updateRow or insertRow methods are called
     * to update the underlying data source.
     * @param columnName - name of the column
     * @param x - new column value
     * @throws SQLException - if a database access error occurs
     */
    public void updateInt(String columnName, int x) throws SQLException;

    /**
     * Updates the designated column with a long value. The updater methods are used to
     * update column values in the current row or the insert row. The updater methods do
     * not update the underlying data source; instead the updateRow or insertRow methods are called
     * to update the underlying data source.
     * @param columnName - name of the column
     * @param x - new column value
     * @throws SQLException - if a database access error occurs
     */
    public void updateLong(String columnName, long x) throws SQLException;

    /**
     * Updates the designated column with a float value. The updater methods are used to
     * update column values in the current row or the insert row. The updater methods do
     * not update the underlying data source; instead the updateRow or insertRow methods are called
     * to update the underlying data source.
     * @param columnName - name of the column
     * @param x - new column value
     * @throws SQLException - if a database access error occurs
     */
    public void updateFloat(String columnName, float x) throws SQLException;

    /**
     * Updates the designated column with a double value. The updater methods are used to
     * update column values in the current row or the insert row. The updater methods do
     * not update the underlying data source; instead the updateRow or insertRow methods are called
     * to update the underlying data source.
     * @param columnName - name of the column
     * @param x - new column value
     * @throws SQLException - if a database access error occurs
     */
    public void updateDouble(String columnName, double x) throws SQLException;

    /**
     * Updates the designated column with a String value. The updater methods are used to
     * update column values in the current row or the insert row. The updater methods do
     * not update the underlying data source; instead the updateRow or insertRow methods are called
     * to update the underlying data source.
     * @param columnName - name of the column
     * @param x - new column value
     * @throws SQLException - if a database access error occurs
     */
    public void updateString(String columnName, String x) throws SQLException;

    /**
     * Updates the designated column with a Date value. The updater methods are used to
     * update column values in the current row or the insert row. The updater methods do
     * not update the underlying data source; instead the updateRow or insertRow methods are called
     * to update the underlying data source.
     * @param columnName - name of the column
     * @param x - new column value
     * @throws SQLException - if a database access error occurs
     */
    public void updateDate(String columnName, Date x) throws SQLException;
    
    /**
     * Updates the designated column with a byte array value. The updater methods are
     * used to update column values in the current row or the insert row. The updater
     * methods do not update the underlying database; instead the updateRow or insertRow
     * methods are called to update the database.
     * @param columnName - the name of the column
     * @param x - the new column value 
     * @throws SQLException - if a database access error occurs
     */
    public void updateBytes(String columnName, byte[] x) throws SQLException;

    /**
     * Updates the designated column with a null value. The updater methods are used to
     * update column values in the current row or the insert row. The updater methods do
     * not update the underlying data source; instead the updateRow or insertRow methods are called
     * to update the underlying data source.
     * @param columnName - name of the column
     * @throws SQLException - if a database access error occurs
     */
    public void updateNull(String columnName) throws SQLException;

    /**
     * The listener will be notified whenever an event occurs on this RowSet object.
     *
     * Note: if the RowSetListener object is null, this method silently discards the null
     * value and does not add a null reference to the set of listeners.
     *
     * Note: if the listener is already set, and the new RowSetListerner instance is added
     * to the set of listeners already registered to receive event notifications from this
     * RowSet
     *
     * @param listener - an object that has implemented the javax.sql.RowSetListener interface
     * and wants to be notified of any events that occur on this RowSet object; May be null
     */
    public void addRowSetListener(RowSetListener listener);

    /**
     * Removes the designated object from this RowSet object's list of listeners. If the given
     * argument is not a registered listener, this method does nothing.
     * Note: if the RowSetListener object is null, this method silently discards the null value
     * @param listener - a RowSetListener object that is on the list of listeners for this RowSet object
     */
    public void removeRowSetListener(RowSetListener listener);

    /**
     * Maps the given column name to its column index
     * @param columnIndex - column number first column is 1, second column is 2 .....
     * @return the column name of the given column index
     * @throws SQLException - if the object does not contain columnIndex or a access
     * error occurs
     */
    public String getColumnName(int columnIndex) throws SQLException;


    /**
     * Get the designated column's index
     * @param columnName - name of the column
     * @return returns the corresponding column index.
     * @throws SQLException - if a data access error
     */
    public int getColumnIndex(String columnName) throws SQLException;

    /**
     * Retrieves the designated column's type
     * @param columnName - name of the column
     * @return SQL type from java.sql.Types
     * @throws SQLException - if a data access error occurs
     */
    public int getColumnType(String columnName) throws SQLException;

    /**
     * Retrieves the designated column's type
     * @param columnIndex - column number first column is 1, second column is 2 .....
     * @return SQL type from java.sql.Types
     * @throws SQLException - if a data access error occurs
     */
    public int getColumnType(int columnIndex) throws SQLException;

    /**
     * Retrieves the current row number. The first row is number 1, the second number 2,
     * and so on.
     * @return the current row number; 0 if there is no current row
     * @throws SQLException - if a data access error occurs
     */
    public int getRow() throws SQLException;

    /**
     * Returns the number of columns in this ResultSet object
     * @return the number of columns
     * @throws SQLException - if a data access error occurs
     */
    public int getColumnCount() throws SQLException;

    /**
     * Moves the cursor down one row from its current position. A ResultSet cursor is
     * initially positioned before the first row; the first call to the method next makes the
     * first row the current row; the second call makes the second row the current row,
     * and so on.
     * If an input stream is open for the current row, a call to the method next will
     * implicitly close it. A ResultSet object's warning chain is cleared when a new row
     * is read.
     * @return true if the new current row is valid; false if there are no more rows
     * @throws SQLException - if a data access error occurs
     */
    public boolean next() throws SQLException;

    /**
     * Moves the cursor to the previous row in this ResultSet object.
     * @return true if the cursor is on a valid row; false if it is off the result set
     * @throws SQLException - if a data access error occurs
     */
    public boolean previous() throws SQLException;

    /**
     * Moves the cursor to the last row in this ResultSet object.
     * @return true if the cursor is on a valid row; false if there are no rows in the
     * result set
     * @throws SQLException - if a data access error occurs
     */
    public boolean last() throws SQLException;

    /**
     * Moves the cursor to the first row in this ResultSet object.
     * @return true if the cursor is on a valid row; false if there are no rows in the
     * result set
     * @throws SQLException - if a data access error occurs
     */
    public boolean first() throws SQLException;

    /**
     * Retrieves whether the cursor is on the first row of this ResultSet object.
     * @return true if the cursor is on the first row; false otherwise
     * @throws SQLException - if a data access error occurs
     */
    public boolean isFirst() throws SQLException;

    /**
     * Retrieves whether the cursor is on the last row of this ResultSet object.
     * Note: Calling the method isLast may be expensive because the JDBC driver might
     * need to fetch ahead one row in order to determine whether the current row is the
     * last row in the result set.
     * @return true if the cursor is on the last row; false otherwise
     * @throws SQLException - if a data access error occurs
     */
    public boolean isLast() throws SQLException;

    /**
     * Moves the cursor to the front of this ResultSet object, just before the first row.
     * This method has no effect if the result set contains no rows.
     * @throws SQLException - if a data access error occurs
     */
    public void beforeFirst() throws SQLException;

    /**
     *<pre>
     * Moves the cursor to the given row number in this ResultSet object.
     *      If the row number is positive, the cursor moves to the given row number with
     * respect to the beginning of the result set. The first row is row 1, the second is
     * row 2, and so on.
     *      If the given row number is negative, the cursor moves to an absolute row position
     * with respect to the end of the result set. For example, calling the method absolute(-1)
     * positions the cursor on the last row; calling the method absolute(-2) moves the cursor
     * to the next-to-last row, and so on.
     * An attempt to position the cursor beyond the first/last row in the result set leaves
     * the cursor before the first row or after the last row.
     *</pre>
     * @param row - the number of the row to which the cursor should move.
     * @return true if the cursor is on the result set; false otherwise
     * @throws SQLException - if a database access error occurs
     */
    public boolean absolute(int row) throws SQLException;

    /**
     * Updates the underlying database with the new contents of the current row of this
     * ResultSet object. This method cannot be called when the cursor is on the insert row.
     * @throws SQLException - if a data access error occurs or if this method is called when
     * the cursor is on the insert row
     */
    public void updateRow() throws SQLException;

    /**
     * Moves the cursor to the remembered cursor position, usually the current row.
     * This method has no effect if the cursor is not on the insert row.
     * @throws SQLException - if a data access error occurs
     */
    public void moveToCurrentRow() throws SQLException;

    /**
     * Moves the cursor to the insert row. The current cursor position is remembered
     * while the cursor is positioned on the insert row. The insert row is a special
     * row associated with an updatable result set. It is essentially a buffer where
     * a new row may be constructed by calling the updater methods prior to inserting
     * the row into the result set. Only the updater, getter, and insertRow methods may
     * be called when the cursor is on the insert row. All of the columns in a result
     * set must be given a value each time this method is called before calling insertRow.
     * An updater method must be called before a getter method can be called on a column
     * value.
     * @throws SQLException - if a data access error occurs
     */
    public void moveToInsertRow() throws SQLException;

    /**
     * Inserts the contents of the insert row into this ResultSet object and into the
     * database. The cursor must be on the insert row when this method is called.
     * @throws SQLException - if a data access error occurs,if this method is called when
     * the cursor is not on the insert row, or if not all of non-nullable columns in the
     * insert row have been given a value
     */
    public void insertRow() throws SQLException;

    /**
     * Deletes the current row from this ResultSet object and from the underlying
     * database. This method cannot be called when the cursor is on the insert row.
     * @throws SQLException - if a data access error occurs or if this method is called
     * when the cursor is on the insert row
     */
    public void deleteRow() throws SQLException;

    /**
     * Refreshes the current row with its most recent value in the database. This
     * method cannot be called when the cursor is on the insert row.
     * The refreshRow method provides a way for an application to explicitly tell the
     * JDBC driver to refetch a row(s) from the database. An application may want to
     * call refreshRow when caching or prefetching is being done by the JDBC driver to
     * fetch the latest value of a row from the database. The JDBC driver may actually
     * refresh multiple rows at once if the fetch size is greater than one.
     * All values are refetched subject to the transaction isolation level and cursor
     * sensitivity. If refreshRow is called after calling an updater method, but before
     * calling the method updateRow, then the updates made to the row are lost. Calling
     * the method refreshRow frequently will likely slow performance
     * @throws SQLException - if a data access error occurs or if this method is called
     * when the cursor is on the insert row
     */
    public void refreshRow() throws SQLException;


    /**
     * Cancels the updates made to the current row in this ResultSet object. This method
     * may be called after calling an updater method(s) and before calling the method
     * updateRow to roll back the updates made to a row. If no updates have been made or
     * updateRow has already been called, this method has no effect
     * @throws SQLException - if a data access error occurs or if this method is called when
     * the cursor is on the insert row
     */
    public void cancelRowUpdates() throws SQLException;

    /**
     * Fills this RowSet object with data.
     * If the required properties have not been set, an exception is thrown. If this
     * method is successful, the current contents of the rowset are discarded. If there
     * are outstanding updates, they are ignored.
     * @throws SQLException - if a data access error occurs or any of the properties necessary
     * for making a connection have not been set
     */
    public void execute() throws SQLException;

    /**
     * Retrieves whether a row has been deleted. A deleted row may leave a visible "hole" in a 
     * result set. This method can be used to detect holes in a result set. The value returned 
     * depends on whether or not this ResultSet object can detect deletions.
     * @return true if a row was deleted and deletions are detected; false otherwise
     * @throws SQLException - if a database access error occurs
     */
    public boolean rowDeleted() throws SQLException;
    
    /**
     * Retrieves whether the current row has had an insertion. The value returned depends on
     * whether or not this ResultSet object can detect visible inserts.
     * @return true if a row has had an insertion and insertions are detected; false otherwise
     * @throws SQLException - if a database access error occurs
     */ 
    public boolean rowInserted() throws SQLException;
    
    /**
     * Retrieves whether the current row has been updated. The value returned depends on whether
     * or not the result set can detect updates.
     * @return true if both (1) the row has been visibly updated by the owner or another and 
     * (2) updates are detected
     * @throws SQLException - if a database access error occurs
     */
    public boolean rowUpdated() throws SQLException;
    
    /**
     * Retrieves the number, types and properties of underlying ResultSet object's columns.
     * @return the description of this ResultSet object's columns
     * @throws SQLException - if a database access error occurs
     */
    public ResultSetMetaData getMetaData() throws SQLException; 
    
    /**
     * Gets the value of the designated column in the current row of this ResultSet object as
     * an Object in the Java programming language.
     *
     * This method will return the value of the given column as a Java object. The type of the
     * Java object will be the default Java object type corresponding to the column's SQL type
     * , following the mapping for built-in types specified in the JDBC specification. If the
     * value is an SQL NULL, the driver returns a Java null.
     *
     * This method may also be used to read database-specific abstract data types.
     *
     * In the JDBC 2.0 API, the behavior of the method getObject is extended to materialize
     * data of SQL user-defined types. When a column contains a structured or distinct value,
     * the behavior of this method is as if it were a call to: getObject(columnIndex, 
     * this.getStatement().getConnection().getTypeMap()). 
     *
     * @param columnName - the SQL name of the column
     * @return a java.lang.Object holding the column value
     * @throws SQLException - if a database access error occurs
     */
    public Object getObject(String columnName) throws SQLException;
    
    /**
     * Gets the value of the designated column in the current row of this ResultSet object as
     * an Object in the Java programming language.
     *
     * This method will return the value of the given column as a Java object. The type of the
     * Java object will be the default Java object type corresponding to the column's SQL type
     * , following the mapping for built-in types specified in the JDBC specification. If the
     * value is an SQL NULL, the driver returns a Java null.
     *
     * This method may also be used to read database-specific abstract data types.
     *
     * In the JDBC 2.0 API, the behavior of the method getObject is extended to materialize
     * data of SQL user-defined types. When a column contains a structured or distinct value,
     * the behavior of this method is as if it were a call to: getObject(columnIndex, 
     * this.getStatement().getConnection().getTypeMap()). 
     *
     * @param columnIndex - the first column is 1, the second is 2, ...
     * @return a java.lang.Object holding the column value
     * @throws SQLException - if a database access error occurs
     */ 
    public Object getObject(int columnIndex) throws SQLException;
    
    /**
     * Updates the designated column with an Object value. The updater methods are used to update
     * column values in the current row or the insert row. The updater methods do not update the
     * underlying database; instead the updateRow or insertRow methods are called to update the
     * database.
     * @param columnName - the name of the column
     * @param x - the new column value
     * @throws SQLException - if a database access error occurs
     */
    public void updateObject(String columnName, Object x) throws SQLException;
    
    /**
     * Updates the designated column with an Object value. The updater methods are used to update
     * column values in the current row or the insert row. The updater methods do not update the
     * underlying database; instead the updateRow or insertRow methods are called to update the
     * database.
     * @param columnIndex - the first column is 1, the second is 2, ...
     * @param value - the new column value
     * @throws SQLException - if a database access error occurs
     */
    public void updateObject(int columnIndex, Object value) throws SQLException;
    
    
    /**
     * Retrieves the value of the designated column in the current row of this ResultSet
     * object as a java.sql.Time object in the Java programming language.
     * @param columnIndex - the first column is 1, the second is 2, ...
     * @return the column value; if the value is SQL NULL, the value returned is null
     * @throws SQLException - if a database access error occurs 
     */
    public Time getTime(int columnIndex) throws SQLException;
     
    /**
     * Retrieves the value of the designated column in the current row of this ResultSet
     * object as a java.sql.Time object in the Java programming language.
     * @param columnName - the SQL name of the column 
     * @return the column value; if the value is SQL NULL, the value returned is null 
     * @throws SQLException - if a database access error occurs
     */
    public Time getTime(String columnName) throws SQLException; 

    /**
     * Retrieves the value of the designated column in the current row of this ResultSet
     * object as a java.sql.Timestamp object in the Java programming language.
     * @param columnIndex - the first column is 1, the second is 2, ... 
     * @return the column value; if the value is SQL NULL, the value returned is null 
     * @throws SQLException - if a database access error occurs
     */
    public Timestamp getTimestamp(int columnIndex) throws SQLException;
    
    /**
     * Retrieves the value of the designated column in the current row of this ResultSet
     * object as a java.sql.Timestamp object.
     * @param columnName - the SQL name of the column 
     * @return the column value; if the value is SQL NULL, the value returned is null 
     * @throws SQLException - if a database access error occurs
     */
    public Timestamp getTimestamp(String columnName) throws SQLException;
    
    /**
     * Updates the designated column with a java.sql.Time value. The updater methods are
     * used to update column values in the current row or the insert row. The updater
     * methods do not update the underlying database; instead the updateRow or insertRow
     * methods are called to update the database.
     * @param columnIndex - the first column is 1, the second is 2, ...
     * @param x - the new column value 
     * @throws SQLException - if a database access error occurs
     */
    public void updateTime(int columnIndex, Time x) throws SQLException;
    
    /**
     * Updates the designated column with a java.sql.Time value. The updater methods are
     * used to update column values in the current row or the insert row. The updater
     * methods do not update the underlying database; instead the updateRow or insertRow
     * methods are called to update the database.
     * @param columnName - the SQL name of the column 
     * @param x - the new column value 
     * @throws SQLException - if a database access error occurs
     */
    public void updateTime(String columnName, Time x) throws SQLException;
    
    /**
     * Updates the designated column with a java.sql.Timestamp  value. The updater methods
     * are used to update column values in the current row or the insert row. The updater
     * methods do not update the underlying database; instead the updateRow or insertRow
     * methods are called to update the database.
     * @param columnIndex - the first column is 1, the second is 2, ...
     * @param x - the new column value 
     * @throws SQLException - if a database access error occurs
     */
    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException;
    
    /**
     * Updates the designated column with a java.sql.Timestamp  value. The updater methods
     * are used to update column values in the current row or the insert row. The updater
     * methods do not update the underlying database; instead the updateRow or insertRow
     * methods are called to update the database.
     * @param columnName - the SQL name of the column 
     * @param x - the new column value 
     * @throws SQLException - if a database access error occurs
     */
    public void updateTimestamp(String columnName, Timestamp x) throws SQLException;
 }

/*
 * $Log$
 * Revision 1.8  2005/05/26 19:26:35  prasanth
 * Added method get/update methods for Time & TimeStamp.
 *
 * Revision 1.7  2005/05/24 23:07:39  prasanth
 * 1. Added get/set methods for object.
 * 2. Added rowDeleted, rowInserted, rowUpdated methods
 * 3. Added getMetaData method.
 *
 * Revision 1.6  2005/02/09 23:04:01  yoda2
 * JavaDoc cleanup.
 *
 * Revision 1.5  2005/02/04 22:49:10  yoda2
 * API cleanup & updated Copyright info.
 *
 * Revision 1.4  2005/01/18 20:58:15  prasanth
 * Added function to get & set bytes.
 *
 * Revision 1.3  2004/11/11 14:45:57  yoda2
 * Using TextPad, converted all tabs to "soft" tabs comprised of four actual spaces.
 *
 * Revision 1.2  2004/11/01 15:53:19  yoda2
 * Fixed various JavaDoc errors.
 *
 * Revision 1.1  2004/10/25 21:47:50  prasanth
 * Initial Commit
 *
 */
