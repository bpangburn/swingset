==============================================================================
ReadMe file for the SwingSet DEMO
==============================================================================

==============================================================================
LICENSE
==============================================================================

Copyright (C) 2003-2020, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice,
   this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors
   may be used to endorse or promote products derived from this software
   without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.

Contributors:
  Prasanth R. Pasala
  Brian E. Pangburn
  Diego Gil
  Man "Bee" Vo


==============================================================================
3rd PARTY LICENSES
==============================================================================

This software contains unmodified binary redistributions for
H2 database engine (http://www.h2database.com/),
which is dual licensed and available under the MPL 2.0
(Mozilla Public License) or under the EPL 1.0 (Eclipse Public License).
An original copy of the license agreement can be found at:
http://www.h2database.com/html/license.html

This software contains unmodified binary redistributions for
Glazed Lists List Transformation toolkit (http://glazedlists.com)
which is dual licensed and available under the MPL 2.0
(Mozilla Public License)
or under the LGPL 2.1 (GNU Lesser General Public License).


==============================================================================
DESCRIPTION
==============================================================================

SwingSet is an open source Java toolkit containing data-aware replacements for
many of the standard Java Swing components.

This file contains sample/demo SwingSet source code and Java class files.

The sample/demo programs provided with SwingSet utilize the h2 database,
which is run in memory.

The sample database is based on the suppliers-and-parts database referenced
in the classic database textbook, "An Introduction to Database Systems,"
by C. J. Date.

For questions regarding SwingSet, send an email to:
swingset#NO-SPAM#@pangburngroup.com


==============================================================================
EXECUTION
==============================================================================

The SwingSet samples/demo requires Java 1.8 or later.

  1. Beginning with SwingSet 3.0.0. we're providing an executable jar
     with all dependencies.
  2. Download/save swingset-demo-x.y.z-jar-with-dependencies.jar to a local folder
  3. Type:
  		java -jar swingset-demo-x.y.z-jar-with-dependencies.jar
  		
  		
==============================================================================
COMPILATION
==============================================================================

The SwingSet samples/demo requires Java 1.8 or later.

Git/Maven/Eclipse:
  git clone https://git.code.sf.net/p/swingset/code swingset-code 
  
Command line:

  1. Download/save swingset-demo-x.y.z-sources.jar to a local
     swingset folder
  2. Open a terminal / command line window and cd to the swingset folder
	 (e.g. "cd /tmp/swingset" or "cd c:\downloads\swingset")
  3. unpackage the demo jar:
	 jar -xf swingset-demo-x.y.z-sources.jar
  4. Make a "lib" subdirectory
     mkdir lib
  5. Save the following jar files to the "lib" subdirectory
     a. Download the latest SwingSet jar (swingset-x.y.z.jar) from:
        https://sf.net/p/swingset
     b. Download the latest GlazedLists jar (glazedlists-x.y.z.jar) from:
        https://repo1.maven.org/maven2/com/glazedlists/glazedlists/
     c. Download the latest H2 Database jar (h2-x.y.z.jar)
        https://repo1.maven.org/maven2/com/h2database/h2/
  6. Compile the demo:
  	 Linux:
		javac -classpath ./lib/h2-x.y.z.jar:./lib/swingset-x.y.z.jar:./lib/glazedlists-x.y.x.jar ./com/nqadmin/swingset/demo/*.java
	 Windows:
		javac -classpath .\lib\h2-x.y.z.jar;.\lib\swingset-x.y.z.jar;.\lib\glazedlists-x.y.x.jar .\com\nqadmin\swingset\demo\*.java
  7. Run the demo:
  	 Linux:
		java -classpath .:./lib/h2-x.y.z.jar:./lib/swingset-x.y.x.jar:./lib/glazedlists-x.y.z.jar com.nqadmin.swingset.demo.MainClass
	 Windows:
		java -classpath .;.\lib\h2-x.y.z.jar;.\lib\swingset-x.y.x.jar;.\lib\glazedlists-x.y.z.jar com.nqadmin.swingset.demo.MainClass


==============================================================================
CLASS DESCRIPTIONS
==============================================================================

***********************
MainClass
***********************
A JFrame with buttons to launch each of the SwingSet example/demo screens.


***********************
Example1
***********************
This example displays data from the supplier_data table. SSTextFields are
used to display supplier id, name, city, and status.

Record navigation is handled with a SSDataNavigator.


***********************
Example2
***********************
This example displays data from the supplier_data table. SSTextFields are
used to display supplier id, name, and city. SSComboBox is used to display
status.

Record navigation is handled with a SSDataNavigator.


***********************
Example3
***********************
This example displays data from the supplier_part_data table. SSTextFields
are used to display supplier-part id and quantity. SSDBComboBoxes are used
to display supplier name and part name based on queries against the
supplier_data and part_data tables.

Record navigation is handled with a SSDataNavigator.


***********************
Example4
***********************
This example displays data from the part_data table. SSTextFields are used
to display part id, name, weight, and city. SSComboBox is used to display
color.

Record navigation can be handled with a SSDataNavigator or with a
SSDBComboBox.

Since the navigation can take place by multiple methods, the navigation
controls have to be synchronized. This is accomplished with the
SSSyncManager.


***********************
Example5
***********************
This example demonstrates the use of an SSDataGrid to display a tabular view
of the part_data table.

For an editable table, users can delete rows by selecting the row to be deleted
and pressing Ctrl-X. By default, a confirmation message is displayed before
deletion.


***********************
Example6
***********************
This example is similar to Example5, demonstrating the use of an SSDataGrid
to display a tabular view of the part_data table. It adds a
ComboRenderer for the color column.


***********************
Example7
***********************
This example demonstrates the use of an SSDataGrid to display a tabular view
of the supplier_part_data table.

It adds a ComboRenderer with a lookup to the supplier_data table for the
supplier name, and adds a DateRenderer for the ship date column.


***********************
TestBaseComponents
***********************
This example demonstrates all of the Base SwingSet Components except for
the SSDataGrid.

There is a separate example screen to demonstrate the Formatted SwingSet
Components.

***********************
TestFormattedComponents
***********************
This example demonstrates all of the Formatted SwingSet Components.

There is a separate example screen to demonstrate the Base SwingSet
Components.
