==============================================================================
ReadMe file for the SwingSet Open Toolkit for Java Swing.
==============================================================================

$Id$


==============================================================================
LICENSE
==============================================================================

Copyright (c) 2003, The Pangburn Company, Inc. and Prasanth R. Pasala.
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
diplayed).  By writing a custom event listener, SSDBComboBox may also be used
to navigate a RowSet based on a combobox selection.  The SSCheckBox allows a
checkbox to be linked to an underlying numeric database column.  Finally, a
SSDataNavigator class is provided to allow traversal, insertion, deletion,
commit, and rollback  of a RowSet. Changes to the current record are auto-
committed when a navigation takes place (also similar to Access).

More information on SwingSet is available from:
http://swingset.sourceforge.net 
and
http://sourceforge.net/projects/swingset

For questions regarding SwingSet, send e-mail to:
swingset@nqadmin.com


==============================================================================
INSTALLATION
==============================================================================

Currently SwingSet requires Sun's Reference Implementation of the JDBC RowSet,
which is available in a Zip file from:
http://developer.java.sun.com/developer/earlyAccess/jdbc/jdbc-rowset.html

Please note that a free registration is required to download the file.

Download the latest SwingSet binary JAR file from:
http://sourceforge.net/projects/swingset

Unzip the file rowset.jar from the Sun file to the same location as
swingset.jar and add both JAR files to your CLASSPATH. Alternatively, you can
copy both files to the the /jre/lib/ext subdirectory of both your JDK and JRE.

SwingSet has been tested with J2SE 1.4.2.


==============================================================================
DEMO PROGRAMS
==============================================================================

Download the latest SwingSet demo JAR file from:
http://sourceforge.net/projects/swingset

This is an executable JAR so on many platforms, you only need to double-click
the JAR file ssdemo.jar to launch the demo.  If that doesn't work then type:
  java -jar ssdemo.jar
  
Please note that the demo requires both the rowset.jar and swingset.jar files.
See the "INSTALLATION" section above for more information.

The demo will attempt to connect to a small, remote, read only database so an
Internet connection is required.


==============================================================================
CLASS DESCRIPTIONS
==============================================================================
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
SSDBCheckBox
***********************
Used to display the boolean values stored in the database. The SSDBCheckBox
can currently only be bound to a numeric database column.  A checked
SSDBCheckBox returns a '1' to the database and an uncheck SSDBCheckBox will
returns a '0'.  In the future an option may be added to allow the user to
specify the values returned for the checked and unchecked checkbox states.


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
     part and a textbox where the user can specify a  quantity.

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
     	myText.setDocument(new SSTextDocument(rowset, "quantity");
           
     } catch(Exception e) {
     // EXCEPTION HANDLER HERE...
     }


     // ADD THE SSDBCOMBOBOX TO THE JFRAME
          getContentPane().add(combo.getComboBox());
          
     // ADD THE JTEXTFIELD TO THE JFRAME
 	  getContentPane().add(myText);	     
     

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
be done before any navigation.  Once navigation takes place changes can't be
reverted using Undo button (has to be done manually by the user).


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