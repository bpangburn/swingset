==============================================================================
ReadMe file for the SwingSet Open Toolkit for Java Swing.
==============================================================================

==============================================================================
LICENSE
==============================================================================

Copyright (c) 2003-2018, The Pangburn Group and Prasanth R. Pasala.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this
list of conditions and the following disclaimer.  Redistributions in binary
form must reproduce the above copyright notice, this list of conditions and
the following disclaimer in the documentation and/or other materials
provided with the distribution.  The names of its contributors may not be
used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.


==============================================================================
DESCRIPTION
==============================================================================

SwingSet is an open source Java toolkit containing data-aware replacements for
many of the standard Java Swing components. While there are several commercially
available solutions that perform similar tasks, SwingSet is the first known
open source solution.

The SwingSet feature-set currently includes:
1. data-aware replacements for JTextField, JTextArea, JComboBox, JCheckBox,
   JLabel, JSlider, & JFormattedTextField
2. binding of a "hidden" numeric column for combo boxes with text choices
   (e.g. 0, 1, & 2 are stored for "Yes," "No," & "Maybe," respectively)
3. population of combo boxes based on columns in a database query (can also
   be used for combo box-based record navigation)
4. a data-aware image component with support for JPEG & GIF image formats
5. a graphical record navigator
    (a) allows for database traversal, insertion, deletion, commit,
        and rollback
    (b) supplies current record index (editable) and total record count
6. a data grid component for creating datasheet/spreadsheet/table views of
   queries
    (a) allows cut & paste to/from spreadsheet programs or other data grids
    (b) allows custom column headings
    (c) allows hiding of specified columns
    (d) allows disabling of specified columns
    (e) allows columns to be displayed as text boxes or combo boxes
    (f) allows addition and deletion of records
    (g) allows deletion of multiple, non-consecutive records
    (h) allows data entry "masks" to be applied to text columns
7. a serializable datasource abstraction layer
8. JavaBean support for all major components
9. Formatted fields for various types like currency, percent, SSN, date etc.

More information on SwingSet is available from:
http://swingset.sourceforge.net 
and
http://sourceforge.net/projects/swingset

For questions regarding SwingSet, send e-mail to:
swingset#NO-SPAM#@pangburngroup.com


==============================================================================
INSTALLATION
==============================================================================

SwingSet 2.0.1 requires jre 1.5 or later.

Download the latest SwingSet binary JAR file from:
http://sourceforge.net/projects/swingset

Add the swingset JAR to your CLASSPATH. Alternatively, you can copy the JAR
to the the /jre/lib/ext subdirectory of your JDK (for compiling) and the 
/lib/ext subdirectory of your JRE (for execution).

SwingSet should work with all J2SE 1.5 or later releases.

Note that you may also need a JDBC driver for your target database.  If the
driver is available as a JAR file, it should be added to your CLASSPATH or
placed in the same /lib/ext subdirectories mentioned above.


==============================================================================
SAMPLE/DEMO PROGRAMS
==============================================================================

See readme.txt contained in the swingset-demo jar file.


==============================================================================
CLASS DESCRIPTIONS - SWINGSET COMPONENTS
==============================================================================
***********************
SSTextField
***********************
SSTextField extends the JTextField. This class provides SSRowSet binding and
different masks including a date mask, a social security number mask, etc.


***********************
SSTextArea
***********************
SSTextArea extends the JTextArea to include SSRowSet binding.


***********************
SSCheckBox
***********************
Used to display the boolean values stored in the database. The SSCheckBox
can be bound to either a numeric or boolean column.  A checked SSCheckBox
returns a '1' to the database and an unchecked SSCheckBox returns a '0'.


***********************
SSComboBox
***********************
Provides a way of displaying text corresponding to codes that are stored in
the database. By default the codes start from zero. If you want to provide a
different mapping for the items in the combo box then a string of integers
containing the corresponding numeric values for each choice must be provided.

e.g.
     SSComboBox combo = new SSComboBox();
     String[] options = {"111", "2222", "33333"};
     combo.setOptions(options);
  
     For the above items the combo box assumes that the values start from zero:
          "111" -> 0, "2222" -> 1, "33333" -> 2
    
     To give your own mappings  you can set the mappings separately or pass
     them along with the options:

     SSComboBox combo = new SSComboBox();
     String[] options = {"111", "2222", "33333"};
     int[] mappings = { 1,5,7 };
     combo.setOptions(options, mappings);

     // next line is assuming myrowset has been initialized and my_column is a
     // column in myrowset
     combo.bind(myrowset,"my_column");
     
  
     Note that if you DO NOT want to use the default mappings, the custom
     mappings must be set before calling the bind() method to bind the
     combo box to a database column.


***********************
SSDBComboBox
***********************
Similar to the SSComboBox, but used when both the 'bound' values and the
'display' values are pulled from a database table.  Generally the bound
value represents a foreign key to another table, and the combo box needs to
diplay a list of one (or more) columns from the other table.
  
e.g.

     Consider two tables:
       1. part_data (part_id, part_name, ...)
       2. shipment_data (shipment_id, part_id, quantity, ...)
  
     Assume you would like to develop a screen for the shipment table and you
     want to have a screen with a combo box where the user can choose a
     part and a text box where the user can specify a  quantity.

     In the combo box you would want to display the part name rather than
     part_id so that it is easier for the user to choose. At the same time you
     want to store the id of the part chosen by the user in the shipment
     table.

     SSConnection ssConnection = null;
     SSJdbcRowSetImpl ssJdbcRowSet = null;
     SSDataNavigator navigator = null;
     SSDBComboBox combo = null;
     
     try {

    // CREATE A DATABASE CONNECTION OBJECT
        ssConnection = new SSConnection(........);
          
    // CREATE AN INSTANCE OF SSJdbcRowSetImpl
        ssJdbcRowSet = new SSJdbcRowSetImpl(ssConnection);
        ssJdbcRowSet.setCommand("SELECT * FROM shipment_data;");
          
    // DATA NAVIGATOR CALLS THE EXECUTE AND NEXT FUNCTIONS ON THE SSROWSET.
    // IF YOU ARE NOT USING THE DATA NAVIGATOR YOU HAVE TO INCLUDE THOSE.
    //   ssJdbcRowSet.execute();
    //   ssJdbcRowSet.next();
        SSDataNavigator navigator = new SSDataNavigator(ssJdbcRowSet);

    // QUERY FOR THE COMBO BOX.
        String query = "SELECT * FROM part_data;";
          
    // CREATE AN INSTANCE OF THE SSDBCOMBOBOX WITH THE CONNECTION OBJECT
    // QUERY AND COLUMN NAMES
        combo = new SSDBComboBox(ssConnection, query, "part_id", "part_name");
          
    // THIS BASICALLY SPECIFIES THE COLUMN AND THE SSROWSET WHERE UPDATES HAVE
    // TO BE MADE.
        combo.bind(ssJdbcRowSet, "part_id");
        combo.execute();

    // CREATE A TEXTFIELD
        SSTextField myText = new SSTextField();
        
    // BIND TEXTFIELD
        myText.bind(ssJdbcRowSet, "quantity");
           
     } catch(Exception e) {
     // EXCEPTION HANDLER HERE...
     }


     // ADD THE SSDBCOMBOBOX TO THE JFRAME
          getContentPane().add(combo);
          
     // ADD THE SSTEXTFIELD TO THE JFRAME
          getContentPane().add(myText);
          
          
***********************
SSImage
***********************
SSImage is a component which can be used to load, store, & display JPEG & GIF
images stored in a database.


***********************
SSLabel
***********************
SSLabel extends the JLabel. This class provides SSRowSet binding and can be
used to display database values in a "read-only" JLabel.


***********************
SSSlider
***********************
SSLabel extends the JLabel. This class provides SSRowSet binding and can be
used to link a JSlider to a numeric column in a database.


***********************
SSDataNavigator
***********************
Component that can be used for data navigation. It provides buttons for
navigation, insertion, and deletion of records in a SSRowSet. The modification
of a SSRowSet can be prevented using the setModificaton() method.  Any changes
made to the columns of a record will be updated whenever there is a
navigation.

For example if you are displaying three columns using the JTextField and the
user changes the text in the text fields then the columns will be updated to
the new values when the user navigates the SSRowSet. If the user wants to revert
the changes he made he can press the Undo button, however this must be done
before any navigation.  Once navigation takes place changes can't be reverted
using Undo button (has to be done manually by the user).


***********************
SSDataGrid
***********************
SSDataGrid provides a way to display information from a database in a table 
format (aka "spreadsheet" or "datasheet" view). The SSDataGrid takes a SSRowSet
as a source of data. It also provides different cell renderers including a
combo box renderer and a date renderer.

SSDataGrid internally uses the SSTableModel to display the information in a 
table format. SSDataGrid also provides an easy means for displaying headers.
Columns can be hidden or made uneditable. In addition, it provides much finer
control over which cells can be edited and which cells can't be edited.  It
uses the SSCellEditing interface for achieving this. The implementation of
this interface also provides a way to specify what kind of information is valid
for each cell.

SSDataGrid uses the isCellEditable() method in SSCellEditing to determine if a
cell is editable or not.  The cellUpdateRequested() method of SSCellEditing is
used to notify a user program when an update is requested. While doing so it
provides the present value in the cell and also the new value. Based on this
information the new value can be rejected or accepted by the program.

SSDataGrid also provides an "extra" row to facilitate the addition of rows to
the table.  Default values for various columns can be set programmatically.  A
programmer can also specify which column is the primary key column for the
underlying SSRowSet and supply a primary key for that column when a new row is
being added.


==============================================================================
CLASS DESCRIPTIONS - DATASOURCES
==============================================================================
***********************
SSRowSet
***********************
Interface that extends serializable and basically clones the RowSet interface
methods required by SSTextDocument.  Used as the basis for datasources
throughout the SwingSet components.


***********************
SSRowSetAdapter
***********************
Abstract class that provides empty implementations of all the methods for the
SSRowSet interface.  

This class is provided for convenience so that users wishing to write their
own SSRowSet implementations can just extend the abstract class and override
the desired methods.


***********************
SSJdbcRowSetImpl
***********************
Implementation of SSRowSet that is basically a serializable wrapper for Sun's
JdbcRowSetImpl.

SSJdbcRowSetImpl can be extended with custom setXYZ() methods to handle
database updates via INSERT/UPDATE queries.  SSJdbcRowSetImpl can also serve as
a template for writing SSRowSet wrappers for other RowSets (e.g. CachedRowSet,
WebRowSet, etc.).


***********************
SSConnection
***********************
Serializable wrapper for Sun's Connection interface.

SSConnection handles serialization/deserialization of database connection info
(path, username, password, etc.).


==============================================================================
CLASS DESCRIPTIONS - "HELPER" CLASSES
==============================================================================
***********************
SSSyncManager
***********************
SSSyncManager simplifies synchronization of a data navigator and a navigation
combo box.


==============================================================================
CLASS DESCRIPTIONS - SUPPORTING CLASSES
==============================================================================
***********************
SSTextDocument
***********************
Java PlainDocument that is 'database-aware'.  When developing a database
application the SSTextDocument can be used in conjunction with the
SSDataNavigator to allow for both editing and navigation of the rows in a
database table.

The SSTextDocument takes a SSRowSet and either a column index or a column name
as arguments.  Whenever the cursor is moved (e.g. navigation occurs on the 
SSDataNavigator), the document property of the bound Swing control changes to
reflect the new value for the database column.

Note that a SSRowSet insert doesn't implicitly modify the cursor which is why 
the SSDBNavImp is provided for clearing controls followoing an insert.


***********************
SSDBNav
***********************
Interface that provides a set of functions to perform some custom operation
before a record is added, after a record is added, before a record is deleted
and after a record is deleted.
  
These functions are called by the SSDataNavigator if the SSDBNav datamember of
the SSDataNavigator is set using the setDBNav() function of the
SSDataNavigator.

     performPreInsertOps() is called when the user presses the insert button.
     
     performPostInsertOps() is called when the user presses the commit button
          after updating the values for the newly inserted row. If the user
          presses the Undo button after the insert button is pressed the
          insertion is cancelled and this function will not be called.

     performPreDeletionOps() is called when the user presses the delete
          button, but just before the deleteRow() method is called on the
          SSRowSet.

     performPostDeletionOps() is called when the user presses the delete
          button and after the deleteRow() method is called on the SSRowSet.

     Note that both the performPreDeletionOps() and performPostDeletionOps()
     will be executed when the user presses the delete button.


***********************
SSDBNavAdapter
***********************
Abstract class that provides empty implementations of all the methods for the
SSDBNav interface.  

This class is provided for convenience so that users wishing to write their
own SSDBNav implementations can just extend the abstract class and override
the desired methods.


***********************
SSDBNavImp
***********************
Custom implementation of SSDBNav that clears/resets the various database-aware
fields on a screen when the user adds a new record.  To achieve this, special
implementation of the performPreInsertOps() method is provided.  An instance of
this class can be created for the container where the fields are to be cleared
and passed to the data navigator.

The data navigator will call the performPreInsertOps() method whenever the
user presses the insert button on the navigator. This fuctions recursively
clears any JTextFields, JTextAreas, and SSCheckBoxes, and if their are any
SSComboBoxes or SSDBComboBoxes they will be reset to the first item in the
list.

This recursive behavior performed on all the components inside the JPanel or
JTabbedPane inside the specified container.


***********************
SSTableModel    
***********************
SSTableModel provides an implementation of the TableModel interface.
The SSDataGrid uses this class for providing a grid view for a SSRowSet. 
SSTableModel can be used without the SSDataGrid (e.g. in conjunction with a
JTable), but the cell renderers and hidden columns features of the SSDataGrid
will not be available.


***********************
SSDataValue
***********************
The SSDataValue interface specifies methods for SSTableModel to retrieve new
values for the primary key column in a JTable.


***********************
SSTableKeyAdapter    
***********************
The SSTableKeyAdapter class provides copy & paste support for JTable. This is 
also used by SSDataGrid. This class facilitates copy & paste between two
JTables or between a spread sheet and a JTable.


***********************
SSCellEditing
***********************
The SSCellEditing interface specifies the methods the SSTableModel will use to
determine whether or not a given cell can be edited or if a user-specified
value for a cell is valid or invalid.


***********************
SSCellEditingAdapter
***********************
This abstract adapter class is provided as a convenience for creating
custom SSCellEditing objects.  Extend this class to create a SSCellEditing
implementation.

SSCellEditingAdapter defines empty functions so that the programmer can define
only the functions desired.  Both isCellEditable() and cellUpdateRequested()
always return true.

***********************
SSDataGridHandler
***********************
The SSDataGridHandler interface specifies set of methods that can be used to
determine whether or not a given row can be deleted, and operation to be 
performed before and after deletion or insertion of a record.

***********************
SSDataGridAdapter
***********************
This abstract class is provided as a convenience for creating custom 
SSDataGridHandler objects. Extend this class to create a SSDataGridHandler 
implementation.

SSDataGridHandlerImpl defines empty functions so that the programmer can define
only the functions desired.
