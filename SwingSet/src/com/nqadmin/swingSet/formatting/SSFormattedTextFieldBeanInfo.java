/*
 * SSFormattedTextFieldBeanInfo.java
 *
 * Created on 19 de enero de 2005, 18:38
 */

package com.nqadmin.swingSet.formatting;

import java.beans.*;

/**
 * @author dags
 */
public class SSFormattedTextFieldBeanInfo extends SimpleBeanInfo {
    
    // Bean descriptor//GEN-FIRST:BeanDescriptor
    /*lazy BeanDescriptor*/
    private static BeanDescriptor getBdescriptor(){
        BeanDescriptor beanDescriptor = new BeanDescriptor  ( SSFormattedTextField.class , SSFormattedTextFieldCustomizer.class );
        beanDescriptor.setDisplayName ( "SwingSet's SSFormattedTextField" );
        beanDescriptor.setShortDescription ( "A FormattedTextField bound to a jdbc column" );//GEN-HEADEREND:BeanDescriptor
        
        // Here you can add code for customizing the BeanDescriptor.
        
        return beanDescriptor;         }//GEN-LAST:BeanDescriptor
    
    
    // Property identifiers//GEN-FIRST:Properties
    private static final int PROPERTY_accessibleContext = 0;
    private static final int PROPERTY_action = 1;
    private static final int PROPERTY_actionCommand = 2;
    private static final int PROPERTY_actionListeners = 3;
    private static final int PROPERTY_actionMap = 4;
    private static final int PROPERTY_actions = 5;
    private static final int PROPERTY_alignmentX = 6;
    private static final int PROPERTY_alignmentY = 7;
    private static final int PROPERTY_ancestorListeners = 8;
    private static final int PROPERTY_autoscrolls = 9;
    private static final int PROPERTY_background = 10;
    private static final int PROPERTY_backgroundSet = 11;
    private static final int PROPERTY_border = 12;
    private static final int PROPERTY_bounds = 13;
    private static final int PROPERTY_caret = 14;
    private static final int PROPERTY_caretColor = 15;
    private static final int PROPERTY_caretListeners = 16;
    private static final int PROPERTY_caretPosition = 17;
    private static final int PROPERTY_colorModel = 18;
    private static final int PROPERTY_columnName = 19;
    private static final int PROPERTY_columns = 20;
    private static final int PROPERTY_component = 21;
    private static final int PROPERTY_componentCount = 22;
    private static final int PROPERTY_componentListeners = 23;
    private static final int PROPERTY_componentOrientation = 24;
    private static final int PROPERTY_componentPopupMenu = 25;
    private static final int PROPERTY_components = 26;
    private static final int PROPERTY_containerListeners = 27;
    private static final int PROPERTY_cursor = 28;
    private static final int PROPERTY_cursorSet = 29;
    private static final int PROPERTY_debugGraphicsOptions = 30;
    private static final int PROPERTY_disabledTextColor = 31;
    private static final int PROPERTY_displayable = 32;
    private static final int PROPERTY_document = 33;
    private static final int PROPERTY_doubleBuffered = 34;
    private static final int PROPERTY_dragEnabled = 35;
    private static final int PROPERTY_dropTarget = 36;
    private static final int PROPERTY_editable = 37;
    private static final int PROPERTY_editValid = 38;
    private static final int PROPERTY_enabled = 39;
    private static final int PROPERTY_focusable = 40;
    private static final int PROPERTY_focusAccelerator = 41;
    private static final int PROPERTY_focusCycleRoot = 42;
    private static final int PROPERTY_focusCycleRootAncestor = 43;
    private static final int PROPERTY_focusListeners = 44;
    private static final int PROPERTY_focusLostBehavior = 45;
    private static final int PROPERTY_focusOwner = 46;
    private static final int PROPERTY_focusTraversable = 47;
    private static final int PROPERTY_focusTraversalKeys = 48;
    private static final int PROPERTY_focusTraversalKeysEnabled = 49;
    private static final int PROPERTY_focusTraversalPolicy = 50;
    private static final int PROPERTY_focusTraversalPolicyProvider = 51;
    private static final int PROPERTY_focusTraversalPolicySet = 52;
    private static final int PROPERTY_font = 53;
    private static final int PROPERTY_fontSet = 54;
    private static final int PROPERTY_foreground = 55;
    private static final int PROPERTY_foregroundSet = 56;
    private static final int PROPERTY_formatter = 57;
    private static final int PROPERTY_formatterFactory = 58;
    private static final int PROPERTY_graphics = 59;
    private static final int PROPERTY_graphicsConfiguration = 60;
    private static final int PROPERTY_height = 61;
    private static final int PROPERTY_helper = 62;
    private static final int PROPERTY_hierarchyBoundsListeners = 63;
    private static final int PROPERTY_hierarchyListeners = 64;
    private static final int PROPERTY_highlighter = 65;
    private static final int PROPERTY_horizontalAlignment = 66;
    private static final int PROPERTY_horizontalVisibility = 67;
    private static final int PROPERTY_ignoreRepaint = 68;
    private static final int PROPERTY_inheritsPopupMenu = 69;
    private static final int PROPERTY_inputContext = 70;
    private static final int PROPERTY_inputMethodListeners = 71;
    private static final int PROPERTY_inputMethodRequests = 72;
    private static final int PROPERTY_inputVerifier = 73;
    private static final int PROPERTY_insets = 74;
    private static final int PROPERTY_keyListeners = 75;
    private static final int PROPERTY_keymap = 76;
    private static final int PROPERTY_layout = 77;
    private static final int PROPERTY_lightweight = 78;
    private static final int PROPERTY_locale = 79;
    private static final int PROPERTY_locationOnScreen = 80;
    private static final int PROPERTY_managingFocus = 81;
    private static final int PROPERTY_margin = 82;
    private static final int PROPERTY_maximumSize = 83;
    private static final int PROPERTY_maximumSizeSet = 84;
    private static final int PROPERTY_minimumSize = 85;
    private static final int PROPERTY_minimumSizeSet = 86;
    private static final int PROPERTY_mouseListeners = 87;
    private static final int PROPERTY_mouseMotionListeners = 88;
    private static final int PROPERTY_mousePosition = 89;
    private static final int PROPERTY_mouseWheelListeners = 90;
    private static final int PROPERTY_name = 91;
    private static final int PROPERTY_navigationFilter = 92;
    private static final int PROPERTY_navigator = 93;
    private static final int PROPERTY_nextFocusableComponent = 94;
    private static final int PROPERTY_opaque = 95;
    private static final int PROPERTY_optimizedDrawingEnabled = 96;
    private static final int PROPERTY_paintingTile = 97;
    private static final int PROPERTY_parent = 98;
    private static final int PROPERTY_peer = 99;
    private static final int PROPERTY_preferredScrollableViewportSize = 100;
    private static final int PROPERTY_preferredSize = 101;
    private static final int PROPERTY_preferredSizeSet = 102;
    private static final int PROPERTY_propertyChangeListeners = 103;
    private static final int PROPERTY_registeredKeyStrokes = 104;
    private static final int PROPERTY_requestFocusEnabled = 105;
    private static final int PROPERTY_rootPane = 106;
    private static final int PROPERTY_rowSet = 107;
    private static final int PROPERTY_scrollableTracksViewportHeight = 108;
    private static final int PROPERTY_scrollableTracksViewportWidth = 109;
    private static final int PROPERTY_scrollOffset = 110;
    private static final int PROPERTY_selectedText = 111;
    private static final int PROPERTY_selectedTextColor = 112;
    private static final int PROPERTY_selectionColor = 113;
    private static final int PROPERTY_selectionEnd = 114;
    private static final int PROPERTY_selectionStart = 115;
    private static final int PROPERTY_showing = 116;
    private static final int PROPERTY_text = 117;
    private static final int PROPERTY_toolkit = 118;
    private static final int PROPERTY_toolTipText = 119;
    private static final int PROPERTY_topLevelAncestor = 120;
    private static final int PROPERTY_transferHandler = 121;
    private static final int PROPERTY_treeLock = 122;
    private static final int PROPERTY_UI = 123;
    private static final int PROPERTY_UIClassID = 124;
    private static final int PROPERTY_valid = 125;
    private static final int PROPERTY_validateRoot = 126;
    private static final int PROPERTY_value = 127;
    private static final int PROPERTY_verifyInputWhenFocusTarget = 128;
    private static final int PROPERTY_vetoableChangeListeners = 129;
    private static final int PROPERTY_visible = 130;
    private static final int PROPERTY_visibleRect = 131;
    private static final int PROPERTY_width = 132;
    private static final int PROPERTY_x = 133;
    private static final int PROPERTY_y = 134;

    // Property array 
    /*lazy PropertyDescriptor*/
    private static PropertyDescriptor[] getPdescriptor(){
        PropertyDescriptor[] properties = new PropertyDescriptor[135];
    
        try {
            properties[PROPERTY_accessibleContext] = new PropertyDescriptor ( "accessibleContext", SSFormattedTextField.class, "getAccessibleContext", null );
            properties[PROPERTY_action] = new PropertyDescriptor ( "action", SSFormattedTextField.class, "getAction", "setAction" );
            properties[PROPERTY_actionCommand] = new PropertyDescriptor ( "actionCommand", SSFormattedTextField.class, null, "setActionCommand" );
            properties[PROPERTY_actionListeners] = new PropertyDescriptor ( "actionListeners", SSFormattedTextField.class, "getActionListeners", null );
            properties[PROPERTY_actionMap] = new PropertyDescriptor ( "actionMap", SSFormattedTextField.class, "getActionMap", "setActionMap" );
            properties[PROPERTY_actions] = new PropertyDescriptor ( "actions", SSFormattedTextField.class, "getActions", null );
            properties[PROPERTY_alignmentX] = new PropertyDescriptor ( "alignmentX", SSFormattedTextField.class, "getAlignmentX", "setAlignmentX" );
            properties[PROPERTY_alignmentY] = new PropertyDescriptor ( "alignmentY", SSFormattedTextField.class, "getAlignmentY", "setAlignmentY" );
            properties[PROPERTY_ancestorListeners] = new PropertyDescriptor ( "ancestorListeners", SSFormattedTextField.class, "getAncestorListeners", null );
            properties[PROPERTY_autoscrolls] = new PropertyDescriptor ( "autoscrolls", SSFormattedTextField.class, "getAutoscrolls", "setAutoscrolls" );
            properties[PROPERTY_background] = new PropertyDescriptor ( "background", SSFormattedTextField.class, "getBackground", "setBackground" );
            properties[PROPERTY_backgroundSet] = new PropertyDescriptor ( "backgroundSet", SSFormattedTextField.class, "isBackgroundSet", null );
            properties[PROPERTY_border] = new PropertyDescriptor ( "border", SSFormattedTextField.class, "getBorder", "setBorder" );
            properties[PROPERTY_bounds] = new PropertyDescriptor ( "bounds", SSFormattedTextField.class, "getBounds", "setBounds" );
            properties[PROPERTY_caret] = new PropertyDescriptor ( "caret", SSFormattedTextField.class, "getCaret", "setCaret" );
            properties[PROPERTY_caretColor] = new PropertyDescriptor ( "caretColor", SSFormattedTextField.class, "getCaretColor", "setCaretColor" );
            properties[PROPERTY_caretListeners] = new PropertyDescriptor ( "caretListeners", SSFormattedTextField.class, "getCaretListeners", null );
            properties[PROPERTY_caretPosition] = new PropertyDescriptor ( "caretPosition", SSFormattedTextField.class, "getCaretPosition", "setCaretPosition" );
            properties[PROPERTY_colorModel] = new PropertyDescriptor ( "colorModel", SSFormattedTextField.class, "getColorModel", null );
            properties[PROPERTY_columnName] = new PropertyDescriptor ( "columnName", SSFormattedTextField.class, null, "setColumnName" );
            properties[PROPERTY_columnName].setPreferred ( true );
            properties[PROPERTY_columnName].setPropertyEditorClass ( SSFormattedTextFieldColumnNamePropertyEditor.class );
            properties[PROPERTY_columns] = new PropertyDescriptor ( "columns", SSFormattedTextField.class, "getColumns", "setColumns" );
            properties[PROPERTY_columns].setPreferred ( true );
            properties[PROPERTY_component] = new IndexedPropertyDescriptor ( "component", SSFormattedTextField.class, null, null, "getComponent", null );
            properties[PROPERTY_componentCount] = new PropertyDescriptor ( "componentCount", SSFormattedTextField.class, "getComponentCount", null );
            properties[PROPERTY_componentListeners] = new PropertyDescriptor ( "componentListeners", SSFormattedTextField.class, "getComponentListeners", null );
            properties[PROPERTY_componentOrientation] = new PropertyDescriptor ( "componentOrientation", SSFormattedTextField.class, "getComponentOrientation", "setComponentOrientation" );
            properties[PROPERTY_componentPopupMenu] = new PropertyDescriptor ( "componentPopupMenu", SSFormattedTextField.class, "getComponentPopupMenu", "setComponentPopupMenu" );
            properties[PROPERTY_components] = new PropertyDescriptor ( "components", SSFormattedTextField.class, "getComponents", null );
            properties[PROPERTY_containerListeners] = new PropertyDescriptor ( "containerListeners", SSFormattedTextField.class, "getContainerListeners", null );
            properties[PROPERTY_cursor] = new PropertyDescriptor ( "cursor", SSFormattedTextField.class, "getCursor", "setCursor" );
            properties[PROPERTY_cursorSet] = new PropertyDescriptor ( "cursorSet", SSFormattedTextField.class, "isCursorSet", null );
            properties[PROPERTY_debugGraphicsOptions] = new PropertyDescriptor ( "debugGraphicsOptions", SSFormattedTextField.class, "getDebugGraphicsOptions", "setDebugGraphicsOptions" );
            properties[PROPERTY_disabledTextColor] = new PropertyDescriptor ( "disabledTextColor", SSFormattedTextField.class, "getDisabledTextColor", "setDisabledTextColor" );
            properties[PROPERTY_displayable] = new PropertyDescriptor ( "displayable", SSFormattedTextField.class, "isDisplayable", null );
            properties[PROPERTY_document] = new PropertyDescriptor ( "document", SSFormattedTextField.class, "getDocument", "setDocument" );
            properties[PROPERTY_doubleBuffered] = new PropertyDescriptor ( "doubleBuffered", SSFormattedTextField.class, "isDoubleBuffered", "setDoubleBuffered" );
            properties[PROPERTY_dragEnabled] = new PropertyDescriptor ( "dragEnabled", SSFormattedTextField.class, "getDragEnabled", "setDragEnabled" );
            properties[PROPERTY_dropTarget] = new PropertyDescriptor ( "dropTarget", SSFormattedTextField.class, "getDropTarget", "setDropTarget" );
            properties[PROPERTY_editable] = new PropertyDescriptor ( "editable", SSFormattedTextField.class, "isEditable", "setEditable" );
            properties[PROPERTY_editValid] = new PropertyDescriptor ( "editValid", SSFormattedTextField.class, "isEditValid", null );
            properties[PROPERTY_enabled] = new PropertyDescriptor ( "enabled", SSFormattedTextField.class, "isEnabled", "setEnabled" );
            properties[PROPERTY_focusable] = new PropertyDescriptor ( "focusable", SSFormattedTextField.class, "isFocusable", "setFocusable" );
            properties[PROPERTY_focusAccelerator] = new PropertyDescriptor ( "focusAccelerator", SSFormattedTextField.class, "getFocusAccelerator", "setFocusAccelerator" );
            properties[PROPERTY_focusCycleRoot] = new PropertyDescriptor ( "focusCycleRoot", SSFormattedTextField.class, "isFocusCycleRoot", "setFocusCycleRoot" );
            properties[PROPERTY_focusCycleRootAncestor] = new PropertyDescriptor ( "focusCycleRootAncestor", SSFormattedTextField.class, "getFocusCycleRootAncestor", null );
            properties[PROPERTY_focusListeners] = new PropertyDescriptor ( "focusListeners", SSFormattedTextField.class, "getFocusListeners", null );
            properties[PROPERTY_focusLostBehavior] = new PropertyDescriptor ( "focusLostBehavior", SSFormattedTextField.class, "getFocusLostBehavior", "setFocusLostBehavior" );
            properties[PROPERTY_focusOwner] = new PropertyDescriptor ( "focusOwner", SSFormattedTextField.class, "isFocusOwner", null );
            properties[PROPERTY_focusTraversable] = new PropertyDescriptor ( "focusTraversable", SSFormattedTextField.class, "isFocusTraversable", null );
            properties[PROPERTY_focusTraversalKeys] = new IndexedPropertyDescriptor ( "focusTraversalKeys", SSFormattedTextField.class, null, null, "getFocusTraversalKeys", "setFocusTraversalKeys" );
            properties[PROPERTY_focusTraversalKeysEnabled] = new PropertyDescriptor ( "focusTraversalKeysEnabled", SSFormattedTextField.class, "getFocusTraversalKeysEnabled", "setFocusTraversalKeysEnabled" );
            properties[PROPERTY_focusTraversalPolicy] = new PropertyDescriptor ( "focusTraversalPolicy", SSFormattedTextField.class, "getFocusTraversalPolicy", "setFocusTraversalPolicy" );
            properties[PROPERTY_focusTraversalPolicyProvider] = new PropertyDescriptor ( "focusTraversalPolicyProvider", SSFormattedTextField.class, "isFocusTraversalPolicyProvider", "setFocusTraversalPolicyProvider" );
            properties[PROPERTY_focusTraversalPolicySet] = new PropertyDescriptor ( "focusTraversalPolicySet", SSFormattedTextField.class, "isFocusTraversalPolicySet", null );
            properties[PROPERTY_font] = new PropertyDescriptor ( "font", SSFormattedTextField.class, "getFont", "setFont" );
            properties[PROPERTY_fontSet] = new PropertyDescriptor ( "fontSet", SSFormattedTextField.class, "isFontSet", null );
            properties[PROPERTY_foreground] = new PropertyDescriptor ( "foreground", SSFormattedTextField.class, "getForeground", "setForeground" );
            properties[PROPERTY_foregroundSet] = new PropertyDescriptor ( "foregroundSet", SSFormattedTextField.class, "isForegroundSet", null );
            properties[PROPERTY_formatter] = new PropertyDescriptor ( "formatter", SSFormattedTextField.class, "getFormatter", null );
            properties[PROPERTY_formatterFactory] = new PropertyDescriptor ( "formatterFactory", SSFormattedTextField.class, "getFormatterFactory", "setFormatterFactory" );
            properties[PROPERTY_graphics] = new PropertyDescriptor ( "graphics", SSFormattedTextField.class, "getGraphics", null );
            properties[PROPERTY_graphicsConfiguration] = new PropertyDescriptor ( "graphicsConfiguration", SSFormattedTextField.class, "getGraphicsConfiguration", null );
            properties[PROPERTY_height] = new PropertyDescriptor ( "height", SSFormattedTextField.class, "getHeight", null );
            properties[PROPERTY_helper] = new PropertyDescriptor ( "helper", SSFormattedTextField.class, null, "setHelper" );
            properties[PROPERTY_helper].setPreferred ( true );
            properties[PROPERTY_hierarchyBoundsListeners] = new PropertyDescriptor ( "hierarchyBoundsListeners", SSFormattedTextField.class, "getHierarchyBoundsListeners", null );
            properties[PROPERTY_hierarchyListeners] = new PropertyDescriptor ( "hierarchyListeners", SSFormattedTextField.class, "getHierarchyListeners", null );
            properties[PROPERTY_highlighter] = new PropertyDescriptor ( "highlighter", SSFormattedTextField.class, "getHighlighter", "setHighlighter" );
            properties[PROPERTY_horizontalAlignment] = new PropertyDescriptor ( "horizontalAlignment", SSFormattedTextField.class, "getHorizontalAlignment", "setHorizontalAlignment" );
            properties[PROPERTY_horizontalVisibility] = new PropertyDescriptor ( "horizontalVisibility", SSFormattedTextField.class, "getHorizontalVisibility", null );
            properties[PROPERTY_ignoreRepaint] = new PropertyDescriptor ( "ignoreRepaint", SSFormattedTextField.class, "getIgnoreRepaint", "setIgnoreRepaint" );
            properties[PROPERTY_inheritsPopupMenu] = new PropertyDescriptor ( "inheritsPopupMenu", SSFormattedTextField.class, "getInheritsPopupMenu", "setInheritsPopupMenu" );
            properties[PROPERTY_inputContext] = new PropertyDescriptor ( "inputContext", SSFormattedTextField.class, "getInputContext", null );
            properties[PROPERTY_inputMethodListeners] = new PropertyDescriptor ( "inputMethodListeners", SSFormattedTextField.class, "getInputMethodListeners", null );
            properties[PROPERTY_inputMethodRequests] = new PropertyDescriptor ( "inputMethodRequests", SSFormattedTextField.class, "getInputMethodRequests", null );
            properties[PROPERTY_inputVerifier] = new PropertyDescriptor ( "inputVerifier", SSFormattedTextField.class, "getInputVerifier", "setInputVerifier" );
            properties[PROPERTY_insets] = new PropertyDescriptor ( "insets", SSFormattedTextField.class, "getInsets", null );
            properties[PROPERTY_keyListeners] = new PropertyDescriptor ( "keyListeners", SSFormattedTextField.class, "getKeyListeners", null );
            properties[PROPERTY_keymap] = new PropertyDescriptor ( "keymap", SSFormattedTextField.class, "getKeymap", "setKeymap" );
            properties[PROPERTY_layout] = new PropertyDescriptor ( "layout", SSFormattedTextField.class, "getLayout", "setLayout" );
            properties[PROPERTY_lightweight] = new PropertyDescriptor ( "lightweight", SSFormattedTextField.class, "isLightweight", null );
            properties[PROPERTY_locale] = new PropertyDescriptor ( "locale", SSFormattedTextField.class, "getLocale", "setLocale" );
            properties[PROPERTY_locationOnScreen] = new PropertyDescriptor ( "locationOnScreen", SSFormattedTextField.class, "getLocationOnScreen", null );
            properties[PROPERTY_managingFocus] = new PropertyDescriptor ( "managingFocus", SSFormattedTextField.class, "isManagingFocus", null );
            properties[PROPERTY_margin] = new PropertyDescriptor ( "margin", SSFormattedTextField.class, "getMargin", "setMargin" );
            properties[PROPERTY_maximumSize] = new PropertyDescriptor ( "maximumSize", SSFormattedTextField.class, "getMaximumSize", "setMaximumSize" );
            properties[PROPERTY_maximumSizeSet] = new PropertyDescriptor ( "maximumSizeSet", SSFormattedTextField.class, "isMaximumSizeSet", null );
            properties[PROPERTY_minimumSize] = new PropertyDescriptor ( "minimumSize", SSFormattedTextField.class, "getMinimumSize", "setMinimumSize" );
            properties[PROPERTY_minimumSizeSet] = new PropertyDescriptor ( "minimumSizeSet", SSFormattedTextField.class, "isMinimumSizeSet", null );
            properties[PROPERTY_mouseListeners] = new PropertyDescriptor ( "mouseListeners", SSFormattedTextField.class, "getMouseListeners", null );
            properties[PROPERTY_mouseMotionListeners] = new PropertyDescriptor ( "mouseMotionListeners", SSFormattedTextField.class, "getMouseMotionListeners", null );
            properties[PROPERTY_mousePosition] = new PropertyDescriptor ( "mousePosition", SSFormattedTextField.class, "getMousePosition", null );
            properties[PROPERTY_mouseWheelListeners] = new PropertyDescriptor ( "mouseWheelListeners", SSFormattedTextField.class, "getMouseWheelListeners", null );
            properties[PROPERTY_name] = new PropertyDescriptor ( "name", SSFormattedTextField.class, "getName", "setName" );
            properties[PROPERTY_navigationFilter] = new PropertyDescriptor ( "navigationFilter", SSFormattedTextField.class, "getNavigationFilter", "setNavigationFilter" );
            properties[PROPERTY_navigator] = new PropertyDescriptor ( "navigator", SSFormattedTextField.class, "getNavigator", "setNavigator" );
            properties[PROPERTY_navigator].setPreferred ( true );
            properties[PROPERTY_navigator].setDisplayName ( "SSDataNavigator" );
            properties[PROPERTY_navigator].setShortDescription ( "Linked SSDataNavigator" );
            properties[PROPERTY_nextFocusableComponent] = new PropertyDescriptor ( "nextFocusableComponent", SSFormattedTextField.class, "getNextFocusableComponent", "setNextFocusableComponent" );
            properties[PROPERTY_opaque] = new PropertyDescriptor ( "opaque", SSFormattedTextField.class, "isOpaque", "setOpaque" );
            properties[PROPERTY_optimizedDrawingEnabled] = new PropertyDescriptor ( "optimizedDrawingEnabled", SSFormattedTextField.class, "isOptimizedDrawingEnabled", null );
            properties[PROPERTY_paintingTile] = new PropertyDescriptor ( "paintingTile", SSFormattedTextField.class, "isPaintingTile", null );
            properties[PROPERTY_parent] = new PropertyDescriptor ( "parent", SSFormattedTextField.class, "getParent", null );
            properties[PROPERTY_peer] = new PropertyDescriptor ( "peer", SSFormattedTextField.class, "getPeer", null );
            properties[PROPERTY_preferredScrollableViewportSize] = new PropertyDescriptor ( "preferredScrollableViewportSize", SSFormattedTextField.class, "getPreferredScrollableViewportSize", null );
            properties[PROPERTY_preferredSize] = new PropertyDescriptor ( "preferredSize", SSFormattedTextField.class, "getPreferredSize", "setPreferredSize" );
            properties[PROPERTY_preferredSizeSet] = new PropertyDescriptor ( "preferredSizeSet", SSFormattedTextField.class, "isPreferredSizeSet", null );
            properties[PROPERTY_propertyChangeListeners] = new PropertyDescriptor ( "propertyChangeListeners", SSFormattedTextField.class, "getPropertyChangeListeners", null );
            properties[PROPERTY_registeredKeyStrokes] = new PropertyDescriptor ( "registeredKeyStrokes", SSFormattedTextField.class, "getRegisteredKeyStrokes", null );
            properties[PROPERTY_requestFocusEnabled] = new PropertyDescriptor ( "requestFocusEnabled", SSFormattedTextField.class, "isRequestFocusEnabled", "setRequestFocusEnabled" );
            properties[PROPERTY_rootPane] = new PropertyDescriptor ( "rootPane", SSFormattedTextField.class, "getRootPane", null );
            properties[PROPERTY_rowSet] = new PropertyDescriptor ( "rowSet", SSFormattedTextField.class, null, "setRowSet" );
            properties[PROPERTY_rowSet].setPreferred ( true );
            properties[PROPERTY_scrollableTracksViewportHeight] = new PropertyDescriptor ( "scrollableTracksViewportHeight", SSFormattedTextField.class, "getScrollableTracksViewportHeight", null );
            properties[PROPERTY_scrollableTracksViewportWidth] = new PropertyDescriptor ( "scrollableTracksViewportWidth", SSFormattedTextField.class, "getScrollableTracksViewportWidth", null );
            properties[PROPERTY_scrollOffset] = new PropertyDescriptor ( "scrollOffset", SSFormattedTextField.class, "getScrollOffset", "setScrollOffset" );
            properties[PROPERTY_selectedText] = new PropertyDescriptor ( "selectedText", SSFormattedTextField.class, "getSelectedText", null );
            properties[PROPERTY_selectedTextColor] = new PropertyDescriptor ( "selectedTextColor", SSFormattedTextField.class, "getSelectedTextColor", "setSelectedTextColor" );
            properties[PROPERTY_selectionColor] = new PropertyDescriptor ( "selectionColor", SSFormattedTextField.class, "getSelectionColor", "setSelectionColor" );
            properties[PROPERTY_selectionEnd] = new PropertyDescriptor ( "selectionEnd", SSFormattedTextField.class, "getSelectionEnd", "setSelectionEnd" );
            properties[PROPERTY_selectionStart] = new PropertyDescriptor ( "selectionStart", SSFormattedTextField.class, "getSelectionStart", "setSelectionStart" );
            properties[PROPERTY_showing] = new PropertyDescriptor ( "showing", SSFormattedTextField.class, "isShowing", null );
            properties[PROPERTY_text] = new PropertyDescriptor ( "text", SSFormattedTextField.class, "getText", "setText" );
            properties[PROPERTY_toolkit] = new PropertyDescriptor ( "toolkit", SSFormattedTextField.class, "getToolkit", null );
            properties[PROPERTY_toolTipText] = new PropertyDescriptor ( "toolTipText", SSFormattedTextField.class, "getToolTipText", "setToolTipText" );
            properties[PROPERTY_topLevelAncestor] = new PropertyDescriptor ( "topLevelAncestor", SSFormattedTextField.class, "getTopLevelAncestor", null );
            properties[PROPERTY_transferHandler] = new PropertyDescriptor ( "transferHandler", SSFormattedTextField.class, "getTransferHandler", "setTransferHandler" );
            properties[PROPERTY_treeLock] = new PropertyDescriptor ( "treeLock", SSFormattedTextField.class, "getTreeLock", null );
            properties[PROPERTY_UI] = new PropertyDescriptor ( "UI", SSFormattedTextField.class, "getUI", "setUI" );
            properties[PROPERTY_UIClassID] = new PropertyDescriptor ( "UIClassID", SSFormattedTextField.class, "getUIClassID", null );
            properties[PROPERTY_valid] = new PropertyDescriptor ( "valid", SSFormattedTextField.class, "isValid", null );
            properties[PROPERTY_validateRoot] = new PropertyDescriptor ( "validateRoot", SSFormattedTextField.class, "isValidateRoot", null );
            properties[PROPERTY_value] = new PropertyDescriptor ( "value", SSFormattedTextField.class, "getValue", "setValue" );
            properties[PROPERTY_verifyInputWhenFocusTarget] = new PropertyDescriptor ( "verifyInputWhenFocusTarget", SSFormattedTextField.class, "getVerifyInputWhenFocusTarget", "setVerifyInputWhenFocusTarget" );
            properties[PROPERTY_vetoableChangeListeners] = new PropertyDescriptor ( "vetoableChangeListeners", SSFormattedTextField.class, "getVetoableChangeListeners", null );
            properties[PROPERTY_visible] = new PropertyDescriptor ( "visible", SSFormattedTextField.class, "isVisible", "setVisible" );
            properties[PROPERTY_visibleRect] = new PropertyDescriptor ( "visibleRect", SSFormattedTextField.class, "getVisibleRect", null );
            properties[PROPERTY_width] = new PropertyDescriptor ( "width", SSFormattedTextField.class, "getWidth", null );
            properties[PROPERTY_x] = new PropertyDescriptor ( "x", SSFormattedTextField.class, "getX", null );
            properties[PROPERTY_y] = new PropertyDescriptor ( "y", SSFormattedTextField.class, "getY", null );
        }
        catch( IntrospectionException e) {}//GEN-HEADEREND:Properties
        
        // Here you can add code for customizing the properties array.
        
        return properties;         }//GEN-LAST:Properties
    
    // Event set information will be obtained from introspection.//GEN-FIRST:Events
    private static EventSetDescriptor[] eventSets = null;
    private static EventSetDescriptor[] getEdescriptor(){//GEN-HEADEREND:Events
        
        // Here you can add code for customizing the event sets array.
        
        return eventSets;     }//GEN-LAST:Events
    
    // Method information will be obtained from introspection.//GEN-FIRST:Methods
    private static MethodDescriptor[] methods = null;
    private static MethodDescriptor[] getMdescriptor(){//GEN-HEADEREND:Methods
        
        // Here you can add code for customizing the methods array.
        
        return methods;     }//GEN-LAST:Methods
    
    
    private static final int defaultPropertyIndex = -1;//GEN-BEGIN:Idx
    private static final int defaultEventIndex = -1;//GEN-END:Idx
    
    
//GEN-FIRST:Superclass
    
    // Here you can add code for customizing the Superclass BeanInfo.
    
//GEN-LAST:Superclass
    
    /**
     * Gets the bean's <code>BeanDescriptor</code>s.
     *
     * @return BeanDescriptor describing the editable
     * properties of this bean.  May return null if the
     * information should be obtained by automatic analysis.
     */
    public BeanDescriptor getBeanDescriptor() {
        return getBdescriptor();
    }
    
    /**
     * Gets the bean's <code>PropertyDescriptor</code>s.
     *
     * @return An array of PropertyDescriptors describing the editable
     * properties supported by this bean.  May return null if the
     * information should be obtained by automatic analysis.
     * <p>
     * If a property is indexed, then its entry in the result array will
     * belong to the IndexedPropertyDescriptor subclass of PropertyDescriptor.
     * A client of getPropertyDescriptors can use "instanceof" to check
     * if a given PropertyDescriptor is an IndexedPropertyDescriptor.
     */
    public PropertyDescriptor[] getPropertyDescriptors() {
        return getPdescriptor();
    }
    
    /**
     * Gets the bean's <code>EventSetDescriptor</code>s.
     *
     * @return  An array of EventSetDescriptors describing the kinds of
     * events fired by this bean.  May return null if the information
     * should be obtained by automatic analysis.
     */
    public EventSetDescriptor[] getEventSetDescriptors() {
        return getEdescriptor();
    }
    
    /**
     * Gets the bean's <code>MethodDescriptor</code>s.
     *
     * @return  An array of MethodDescriptors describing the methods
     * implemented by this bean.  May return null if the information
     * should be obtained by automatic analysis.
     */
    public MethodDescriptor[] getMethodDescriptors() {
        return getMdescriptor();
    }
    
    /**
     * A bean may have a "default" property that is the property that will
     * mostly commonly be initially chosen for update by human's who are
     * customizing the bean.
     * @return  Index of default property in the PropertyDescriptor array
     * 		returned by getPropertyDescriptors.
     * <P>	Returns -1 if there is no default property.
     */
    public int getDefaultPropertyIndex() {
        return defaultPropertyIndex;
    }
    
    /**
     * A bean may have a "default" event that is the event that will
     * mostly commonly be used by human's when using the bean.
     * @return Index of default event in the EventSetDescriptor array
     *		returned by getEventSetDescriptors.
     * <P>	Returns -1 if there is no default event.
     */
    public int getDefaultEventIndex() {
        return defaultEventIndex;
    }
}

