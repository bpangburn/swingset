==============================================================================
ReadMe file for the SwingSet Open Toolkit for Java Swing.
==============================================================================

$Id$


==============================================================================
LICENSE
==============================================================================

Copyright (c) 2003-2004, The Pangburn Company, Inc. and Prasanth R. Pasala.
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

SwingSet is an open source Java toolkit that allows the standard Java Swing
components to be made database-aware.  While there are several commercially
available solutions that perform similar tasks, SwingSet is the first known
open source solution.

The SwingSet feature-set currently includes:
1. database binding for text boxes, text areas, combo boxes, and check boxes
2. masked editing of text boxes
3. binding of a "hidden" numeric column for combo boxes with text choices
   (e.g. 0, 1, & 2 are stored for "Yes," "No," & "Maybe," respectively)
4. population of combo boxes based on columns in a database query (can also
   be used for combobox-based record navigation)
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
    
For version 0.8.0 all components have been made into Java Beans which will
allow for better integration with Java IDEs.  As part of this, the SwingSet
components were serialized and the methods for accessing each component were
standardized across the toolkit.  Major usability enhancements include cut &
paste support from the data grid to/from spreadsheet programs and/or other
data grids, addition of current record index and total record count to the data
navigator, and default selection of an "empty" item for combo boxes.  See
ChangeLog.txt for more information.

Note that the 0.8.3 release is the last planned release in the 0.8.X series and
contains primarily bug fixes and usability enhancements.  The 0.9.0 release will
follow shortly and will contain a new serialized datasource abstraction layer.
This will greatly facilitate serialization/deserialization of the various
SwingSet components and will pave the way for SwingSet compatibility with non-
up datable rowsets and other non-database datasources.  Unfortunately,
applications written for 0.8.X and earlier versions of SwingSet will require
some small changes related to database connections and rowset objects in order
to work with the 0.9.X and later versions.  We will make every effort to
constrain and minimize the required changes.  The 0.8.3 release should supply
developers with the latest fixes and provide the maximum migration time as
0.8.3 and 0.9.0 should be identical in all aspects other than the datasource
abstraction layer.
    
    
==============================================================================
DETAILS
==============================================================================

SwingSet utilizes SSTextDocument, an extension of the standard PlainDocument
class to link the JTextField or JTextArea to a database column within a
RowSet.  In addition, custom classes are provided to replace the standard
JComboBox and JCheckBox. The SSComboBox provides an Access-like combobox that
can be used to display user-specified text choices based on an underlying
numeric column (e.g. allows my_table!choice_code, an integer column with valid
values of 0, 1, & 2, to be displayed as "yes," "no," & "maybe").  The
SSDBComboBox operates in a similar fashion, but is used when both the values
and their corresponding text choices are stored in a table (e.g.
my_table!part_id is stored as a foreign key, but my_table!part_name is
displayed).  By writing a custom event listener, SSDBComboBox may also be used
to navigate a RowSet based on a combobox selection.  The SSCheckBox allows a
checkbox to be linked to an underlying numeric database column.  

The SSTextField, which extends the JTextField, provides editing masks for data
entry (e.g. dates, social security numbers, specified number of decimals, etc.).

The SSDataGrid can display database information in a "datasheet" or
"spreadsheet" style view.  It provides functions to set column headers, hide
columns, and make columns uneditable.  In addition, individual columns in the
SSDataGrid can be displayed as either text fields or combo boxes.  For text
columns, editing masks can be specified.  SSDataGrid uses the SSTableModel,
which extends AbstractTableModel. The SSCellEditing and SSDataValue interfaces
provide fine control over the working of the grid.  The SSTableKeyAdapter
provides support for cut & paste from a data grid to/from spreadsheet programs
and/or other data grids.

The SSDataNavigator class provides traversal, insertion, deletion, commit, and
rollback  of a RowSet. The numerical index is show for the current record and
the total number of records is displayed. Changes to the current record are
auto-committed when a navigation takes place (also similar to Access).

More information on SwingSet is available from:
http://swingset.sourceforge.net 
and
http://sourceforge.net/projects/swingset

For questions regarding SwingSet, send e-mail to:
swingset@nqadmin.com


==============================================================================
INSTALLATION
==============================================================================

If SwingSet is to be used with a J2SE prior to 1.5, Sun's Reference
Implementation of the JDBC RowSet is required (free registration required).
It is available in a Zip file from:
http://developer.java.sun.com/developer/earlyAccess/jdbc/jdbc-rowset.html

If using J2SE 1.5.0 Beta 1 or later, RowSet is already included.

Download the latest SwingSet binary JAR file from:
http://sourceforge.net/projects/swingset

If needed, unzip the file rowset.jar from the Sun file to the same location as
swingset.jar and add both JAR files to your CLASSPATH. Alternatively, you can
copy both files to the the /jre/lib/ext subdirectory of your JDK (for
compiling) and the /lib/ext subdirectory of your JRE (for execution).

SwingSet has been tested with J2SE 1.4.2 and 1.5.0, but should work with
all J2SE 1.4 or 1.5 releases.

Note that you will also need a JDBC driver for your target database.  If the
driver is available as a JAR file, it should be added to your CLASSPATH or
placed in the same /lib/ext subdirectories mentioned above.


==============================================================================
SAMPLE/DEMO PROGRAMS
==============================================================================

The sample/demo programs provided with SwingSet utilize a read-only PostgreSQL
database based on the suppliers-and-parts database referenced in the classic
database textbook, "An Introduction to Database Systems,"  by C. J. Date.

The demo is available as a Java Web Start application from:
http://swingset.sourceforge.net/SwingSet.jnlp

Alternatively, the demo can be downloaded and run from the command line.  This
method also requires downloading the PostgreSQL JDBC driver.  The JDBC JAR file
is available from:
http://jdbc.postgresql.org/download.html

This file should be added to your CLASSPATH or placed in the same /lib/ext
subdirectories mentioned under "INSTALLATION."

After installing the JDBC driver, download the latest SwingSet demo JAR file
to the location of your choice.  The file is available from:
http://sourceforge.net/projects/swingset

This is an executable JAR so on many platforms, you only need to double-click
the JAR file ssdemo.jar to launch the demo.  If that doesn't work then type:
  java -jar <demo jar file name here>
  
  e.g.
       java -jar swingset-demo_0.8.3_beta.jar
  
Please note that the demo requires both the rowset.jar and latest SwingSet
binary JAR files (e.g. swingset-bin_0.8.3_beta.jar). See the "INSTALLATION"
section above for more information.

The demo will attempt to connect to a small, remote, read only database so an
Internet connection is required.


***********************
Example1
***********************
This example demonstrates the use of SSTextDocument to display information in
JTextField (Name, City, and Status). The navigation is done with
SSDataNavigator.


***********************
Example2
***********************
This example demonstrates the use of SSTextDocument to display information in
JTextField (Name and City) and SSComboBox (Status). The navigation is done with
SSDataNavigator.


***********************
Example3
***********************
This example demonstrates the use of SSTextDocument to display information in
SSDBComboBox (Supplier and Part) and JTextField (Quantity). The navigation
is done with SSDataNavigator.


***********************
Example4
***********************
This example demonstrates the use of SSDBComboBox for record navigation.
Navigation can be accomplished using either the Part combobox or the
navigation bar. Since the part name is used for navigation it can't be
updated (note that none of the fields in these examples can actually be
updated since the demo database is read only).

Because the navigation can take place by multiple methods, the navigation
controls have to be synchronized.  This is done using a hidden JTextField
containing the part_id and an event listener.

This example also demonstrates the use of SSTextDocument to display
information in SSComboBox (Color) and JTextField (Weight and City).


***********************
Example5
***********************
This example demonstrates the use of SSDataGrid to display information
in a table format. If the database were editable, users could also delete rows
by selecting the row to be deleted and pressing Ctrl-X. By default, a
confirmation message is displayed before deletion.


***********************
Example6
***********************
This example demonstrates the use of SSDataGrid with a combobox renderer.


***********************
Example7
***********************
This example demonstrates the use of SSDataGrid with two combobox renderers
and a date renderer. The mappings for the combo boxes are retrieved from another
table.


==============================================================================
CLASS DESCRIPTIONS - SWINGSET COMPONENTS
==============================================================================
***********************
SSTextField
***********************
SSTextField extends the JTextField. This class provides different masks
including a date mask, a social security number mask, etc.


***********************
SSTextArea
***********************
SSTextArea extends the JTextArea to include rowset binding.


***********************
SSDBCheckBox
***********************
Used to display the boolean values stored in the database. The SSDBCheckBox
can currently only be bound to a numeric database column.  A checked
SSDBCheckBox returns a '1' to the database and an uncheck SSDBCheckBox will
returns a '0'.  In the future an option may be added to allow the user to
specify the values returned for the checked and unchecked checkbox states.


***********************
SSComboBox
***********************
Provides a way of displaying text corresponding to codes that are stored in
the database. By default the codes start from zero. If you want to provide a
different mapping for the items in the combobox then a string of integers
containing the corresponding numeric values for each choice must be provided.

e.g.
     SSComboBox combo = new SSComboBox();
     String[] options = {"111", "2222", "33333"};
     combo.setOption(options);
  
     For the above items the combobox assumes that the values start from zero:
          "111" -> 0, "2222" -> 1, "33333" -> 2
    
     To give your own mappings  you can set the mappings separately or pass
     them along with the options:

     SSComboBox combo = new SSComboBox();
     String[] options = {"111", "2222", "33333"};
     int[] mappings = { 1,5,7 };
     combo.setOption(options, mappings);

     // next line is assuming myrowset has been initialized and my_column is a
     // column in myrowset
     combo.bind(myrowset,"my_column");
     
  
     Note that if you DO NOT want to use the default mappings, the custom
     mappings must be set before calling the bind() method to bind the
     combobox to a database column.


***********************
SSDBComboBox
***********************
Similar to the SSComboBox, but used when both the 'bound' values and the
'display' values are pulled from a database table.  Generally the bound
value represents a foreign key to another table, and the combobox needs to
diplay a list of one (or more) columns from the other table.
  
e.g.

     Consider two tables:
       1. part_data (part_id, part_name, ...)
       2. shipment_data (shipment_id, part_id, quantity, ...)
  
     Assume you would like to develop a screen for the shipment table and you
     want to have a screen with a combobox where the user can choose a
     part and a text box where the user can specify a  quantity.

     In the combobox you would want to display the part name rather than
     part_id so that it is easier for the user to choose. At the same time you
     want to store the id of the part chosen by the user in the shipment
     table.

     Connection connection = null;
     JdbcRowSet rowset = null;
     SSDataNavigator navigator = null;
     SSDBComboBox combo = null;
     
     try {

     // CREATE A DATABASE CONNECTION OBJECT
          Connection connection = new Connection(........);
          
     // CREATE AN INSTANCE OF JDBC ROWSET
          JdbcRowset rowset = new JdbcRowSet();
          rowset.setUrl("<database path>");
          rowset.setUsername("user");
          rowset.setPassword("pass");
          rowset.setCommand("SELECT * FROM shipment_data;");
          
     // DATA NAVIGATOR CALLS THE EXECUTE AND NEXT FUNCTIONS ON THE ROWSET.
     // IF YOU ARE NOT USING THE DATA NAVIGATOR YOU HAVE TO INCLUDE THOSE.
     //   rowset.execute();
     //   rowset.next();
          SSDataNavigator navigator = new SSDataNavigator(rowset);

     // QUERY FOR THE COMBOBOX.
          String query = "SELECT * FROM part_data;";
          
     // CREATE AN INSTANCE OF THE SSDBCOMBOBOX WITH THE CONNECTION OBJECT
     // QUERY AND COLUMN NAMES
          combo = new SSDBComboBox(connection,query,"part_id","part_name");
          
     // THIS BASICALLY SPECIFIES THE COLUMN AND THE ROWSET WHERE UPDATES HAVE
     // TO BE MADE.
          combo.bind(rowset,"part_id");
          combo.execute();

    // CREATE A TEXTFIELD
        JTextField myText = new JTextField();
        
    // BIND TEXTFIELD - OLD WAY
    //  myText.setDocument(new SSTextDocument(rowset, "quantity");
    // BIND TEXTFIELD - PREFERRED WAY.
    	myText.bind(rowset, "quantity");
           
     } catch(Exception e) {
     // EXCEPTION HANDLER HERE...
     }


     // ADD THE SSDBCOMBOBOX TO THE JFRAME
          getContentPane().add(combo.getComboBox());
          
     // ADD THE JTEXTFIELD TO THE JFRAME
          getContentPane().add(myText);


***********************
SSDataNavigator
***********************
Component that can be used for data navigation. It provides buttons for
navigation, insertion, and deletion of records in a RowSet. The modification
of a RowSet can be prevented using the setModificaton() method.  Any changes
made to the columns of a record will be updated whenever there is a
navigation.

For example if you are displaying three columns using the JTextField and the
user changes the text in the text fields then the columns will be updated to
the new values when the user navigates the RowSet. If the user wants to revert
the changes he made he can press the Undo button, however this must be done
before any navigation.  Once navigation takes place changes can't be reverted
using Undo button (has to be done manually by the user).


***********************
SSDataGrid
***********************
SSDataGrid provides a way to display information from a database in a table 
format (aka "spreadsheet" or "datasheet" view). The SSDataGrid takes a rowset
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
underlying rowset and supply a primary key for that column when a new row is
being added.


==============================================================================
CLASS DESCRIPTIONS - SUPPORTING CLASSES
==============================================================================


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
          RowSet.

     performPostDeletionOps() is called when the user presses the delete
          button and after the deleteRow() method is called on the RowSet.

     Note that both the performPreDeletionOps() and performPostDeletionOps()
     will be executed when the user presses the delete button.


***********************
SSDBNavAdapter
***********************
Abstract class that provides empty implementations of all the methods for the
SSDBNav interface.  

This class is provided for convenience. so that users wishing to write their
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
SSTextDocument
***********************
Java PlainDocument that is 'database-aware'.  When developing a database
application the SSTextDocument can be used in conjunction with the
SSDataNavigator to allow for both editing and navigation of the rows in a
database table.

The SSTextDocument takes a RowSet and either a column index or a column name
as arguments.  Whenever the cursor is moved (e.g. navigation occurs on the 
SSDataNavigator), the document property of the bound Swing control changes to
reflect the new value for the database column.

Note that a RowSet insert doesn't implicitly modify the cursor which is why 
the SSDBNavImp is provided for clearing controls followoing an insert.


***********************
SSTableModel    
***********************
SSTableModel provides an implementation of the TableModel interface.
The SSDataGrid uses this class for providing a grid view for a rowset. 
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