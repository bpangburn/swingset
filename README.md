# ReadMe file for the swingset-parent folder.

When SwingSet was converted to a Maven project, SwingSet and it's Demo were split into two separate modules, swingset and swingset-demo.

Each module has a fully independent pom.xml, but there is also a swingset-parent artifact and pom.xml that can be used to manage/compile both modules.

Unfortunately, for the most seamless integration with Eclipse, Eclipse wants to treat swingset-parent, swingset, and swingset-demo as three separate projects.

To view as a single project:
 1. Window->Show View->Project Explorer
 2. View Menu (down arrow/triangle)->Project Presentation->Hierarchical
 
 Detailed ReadMe files for swingset and swingset-demo can be found in  their respective folders.
