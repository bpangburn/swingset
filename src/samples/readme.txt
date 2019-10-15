==============================================================================
ReadMe file for the SwingSet DEMO
==============================================================================

==============================================================================
LICENSE
==============================================================================

Copyright (C) 2003-2019, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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
INSTALLATION
==============================================================================

The SwingSet samples/demo requires Java 1.8 or later.

  1. make a swingset folder containing the demo jar file
  2. open a terminal / command line window and cd to the swingset folder
	(e.g. "cd /tmp/swingset" or "cd c:\downloads\swingset")
  3. unpackage the demo jar:
	jar -xf swingset-demo-{version}.jar
  4. this creates the following folders:
	./src - sample/demo source code files
	./bin - sample/demo Java classes
	./lib - dependency jars
	./META-INF - jar manifest
	./readme.txt - this file
	./suppliers_and_parts.sql - sql for sample database
  5. run the demo:
  	Linux:
		java -classpath ./bin:./lib/* MainClass suppliers_and_parts.sql
	Windows:
		java -classpath .\bin;.\lib\* MainClass suppliers_and_parts.sql
	
If you would like to experiment with the source code in /src, you can recompile
using the following commands:
	Linux:
		javac -d ./bin -classpath ./src:./lib/* ./src/MainClass.java
	Windows:
		javac -d .\bin -classpath .\src;.\lib\* .\src\MainClass.java
		
Note that if running the examples from Eclipse, right-click on MainClass.java
and select Run As->Run Configurations... and then under Arguments enter:
	./src/samples/suppliers_and_parts.sql
Then right-click on MainClass.java and select Run As->Java Application.	


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
Navigation can be accomplished using either the Part combo box or the
navigation bar. Since the part name is used for navigation it can't be
updated (note that none of the fields in these examples can actually be
updated since the demo database is read only).

Because the navigation can take place by multiple methods, the navigation
controls have to be synchronized.  This is done using a "helper" class
called SSSyncManager.

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
This example demonstrates the use of SSDataGrid with a combo box renderer.


***********************
Example7
***********************
This example demonstrates the use of SSDataGrid with two combo box renderers
and a date renderer. The mappings for the combo boxes are retrieved from another
table.
