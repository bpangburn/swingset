/*
 * SSCurrencyFieldBeanInfo.java
 *
 * Created on 19 de enero de 2005, 18:58
 */

package com.nqadmin.swingSet.formatting;

import java.beans.*;

/**
 * @author dags
 */
public class SSCurrencyFieldBeanInfo extends SimpleBeanInfo {
    
    // Bean descriptor//GEN-FIRST:BeanDescriptor
    /*lazy BeanDescriptor*/
    private static BeanDescriptor getBdescriptor(){
        BeanDescriptor beanDescriptor = new BeanDescriptor  ( SSCurrencyField.class , null );
        beanDescriptor.setShortDescription ( "A FormattedTextField with currency formating capabilities" );//GEN-HEADEREND:BeanDescriptor
        
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
    private static final int PROPERTY_decimals = 31;
    private static final int PROPERTY_disabledTextColor = 32;
    private static final int PROPERTY_displayable = 33;
    private static final int PROPERTY_document = 34;
    private static final int PROPERTY_doubleBuffered = 35;
    private static final int PROPERTY_dragEnabled = 36;
    private static final int PROPERTY_dropTarget = 37;
    private static final int PROPERTY_editable = 38;
    private static final int PROPERTY_editValid = 39;
    private static final int PROPERTY_enabled = 40;
    private static final int PROPERTY_focusable = 41;
    private static final int PROPERTY_focusAccelerator = 42;
    private static final int PROPERTY_focusCycleRoot = 43;
    private static final int PROPERTY_focusCycleRootAncestor = 44;
    private static final int PROPERTY_focusListeners = 45;
    private static final int PROPERTY_focusLostBehavior = 46;
    private static final int PROPERTY_focusOwner = 47;
    private static final int PROPERTY_focusTraversable = 48;
    private static final int PROPERTY_focusTraversalKeys = 49;
    private static final int PROPERTY_focusTraversalKeysEnabled = 50;
    private static final int PROPERTY_focusTraversalPolicy = 51;
    private static final int PROPERTY_focusTraversalPolicyProvider = 52;
    private static final int PROPERTY_focusTraversalPolicySet = 53;
    private static final int PROPERTY_font = 54;
    private static final int PROPERTY_fontSet = 55;
    private static final int PROPERTY_foreground = 56;
    private static final int PROPERTY_foregroundSet = 57;
    private static final int PROPERTY_formatter = 58;
    private static final int PROPERTY_formatterFactory = 59;
    private static final int PROPERTY_graphics = 60;
    private static final int PROPERTY_graphicsConfiguration = 61;
    private static final int PROPERTY_height = 62;
    private static final int PROPERTY_helper = 63;
    private static final int PROPERTY_hierarchyBoundsListeners = 64;
    private static final int PROPERTY_hierarchyListeners = 65;
    private static final int PROPERTY_highlighter = 66;
    private static final int PROPERTY_horizontalAlignment = 67;
    private static final int PROPERTY_horizontalVisibility = 68;
    private static final int PROPERTY_ignoreRepaint = 69;
    private static final int PROPERTY_inheritsPopupMenu = 70;
    private static final int PROPERTY_inputContext = 71;
    private static final int PROPERTY_inputMethodListeners = 72;
    private static final int PROPERTY_inputMethodRequests = 73;
    private static final int PROPERTY_inputVerifier = 74;
    private static final int PROPERTY_insets = 75;
    private static final int PROPERTY_keyListeners = 76;
    private static final int PROPERTY_keymap = 77;
    private static final int PROPERTY_layout = 78;
    private static final int PROPERTY_lightweight = 79;
    private static final int PROPERTY_locale = 80;
    private static final int PROPERTY_locationOnScreen = 81;
    private static final int PROPERTY_managingFocus = 82;
    private static final int PROPERTY_margin = 83;
    private static final int PROPERTY_maximumSize = 84;
    private static final int PROPERTY_maximumSizeSet = 85;
    private static final int PROPERTY_minimumSize = 86;
    private static final int PROPERTY_minimumSizeSet = 87;
    private static final int PROPERTY_mouseListeners = 88;
    private static final int PROPERTY_mouseMotionListeners = 89;
    private static final int PROPERTY_mousePosition = 90;
    private static final int PROPERTY_mouseWheelListeners = 91;
    private static final int PROPERTY_name = 92;
    private static final int PROPERTY_navigationFilter = 93;
    private static final int PROPERTY_navigator = 94;
    private static final int PROPERTY_nextFocusableComponent = 95;
    private static final int PROPERTY_opaque = 96;
    private static final int PROPERTY_optimizedDrawingEnabled = 97;
    private static final int PROPERTY_paintingTile = 98;
    private static final int PROPERTY_parent = 99;
    private static final int PROPERTY_peer = 100;
    private static final int PROPERTY_precision = 101;
    private static final int PROPERTY_preferredScrollableViewportSize = 102;
    private static final int PROPERTY_preferredSize = 103;
    private static final int PROPERTY_preferredSizeSet = 104;
    private static final int PROPERTY_propertyChangeListeners = 105;
    private static final int PROPERTY_registeredKeyStrokes = 106;
    private static final int PROPERTY_requestFocusEnabled = 107;
    private static final int PROPERTY_rootPane = 108;
    private static final int PROPERTY_rowSet = 109;
    private static final int PROPERTY_scrollableTracksViewportHeight = 110;
    private static final int PROPERTY_scrollableTracksViewportWidth = 111;
    private static final int PROPERTY_scrollOffset = 112;
    private static final int PROPERTY_selectedText = 113;
    private static final int PROPERTY_selectedTextColor = 114;
    private static final int PROPERTY_selectionColor = 115;
    private static final int PROPERTY_selectionEnd = 116;
    private static final int PROPERTY_selectionStart = 117;
    private static final int PROPERTY_showing = 118;
    private static final int PROPERTY_text = 119;
    private static final int PROPERTY_toolkit = 120;
    private static final int PROPERTY_toolTipText = 121;
    private static final int PROPERTY_topLevelAncestor = 122;
    private static final int PROPERTY_transferHandler = 123;
    private static final int PROPERTY_treeLock = 124;
    private static final int PROPERTY_UI = 125;
    private static final int PROPERTY_UIClassID = 126;
    private static final int PROPERTY_valid = 127;
    private static final int PROPERTY_validateRoot = 128;
    private static final int PROPERTY_value = 129;
    private static final int PROPERTY_verifyInputWhenFocusTarget = 130;
    private static final int PROPERTY_vetoableChangeListeners = 131;
    private static final int PROPERTY_visible = 132;
    private static final int PROPERTY_visibleRect = 133;
    private static final int PROPERTY_width = 134;
    private static final int PROPERTY_x = 135;
    private static final int PROPERTY_y = 136;

    // Property array 
    /*lazy PropertyDescriptor*/
    private static PropertyDescriptor[] getPdescriptor(){
        PropertyDescriptor[] properties = new PropertyDescriptor[137];
    
        try {
            properties[PROPERTY_accessibleContext] = new PropertyDescriptor ( "accessibleContext", SSCurrencyField.class, "getAccessibleContext", null );
            properties[PROPERTY_action] = new PropertyDescriptor ( "action", SSCurrencyField.class, "getAction", "setAction" );
            properties[PROPERTY_actionCommand] = new PropertyDescriptor ( "actionCommand", SSCurrencyField.class, null, "setActionCommand" );
            properties[PROPERTY_actionListeners] = new PropertyDescriptor ( "actionListeners", SSCurrencyField.class, "getActionListeners", null );
            properties[PROPERTY_actionMap] = new PropertyDescriptor ( "actionMap", SSCurrencyField.class, "getActionMap", "setActionMap" );
            properties[PROPERTY_actions] = new PropertyDescriptor ( "actions", SSCurrencyField.class, "getActions", null );
            properties[PROPERTY_alignmentX] = new PropertyDescriptor ( "alignmentX", SSCurrencyField.class, "getAlignmentX", "setAlignmentX" );
            properties[PROPERTY_alignmentY] = new PropertyDescriptor ( "alignmentY", SSCurrencyField.class, "getAlignmentY", "setAlignmentY" );
            properties[PROPERTY_ancestorListeners] = new PropertyDescriptor ( "ancestorListeners", SSCurrencyField.class, "getAncestorListeners", null );
            properties[PROPERTY_autoscrolls] = new PropertyDescriptor ( "autoscrolls", SSCurrencyField.class, "getAutoscrolls", "setAutoscrolls" );
            properties[PROPERTY_background] = new PropertyDescriptor ( "background", SSCurrencyField.class, "getBackground", "setBackground" );
            properties[PROPERTY_backgroundSet] = new PropertyDescriptor ( "backgroundSet", SSCurrencyField.class, "isBackgroundSet", null );
            properties[PROPERTY_border] = new PropertyDescriptor ( "border", SSCurrencyField.class, "getBorder", "setBorder" );
            properties[PROPERTY_bounds] = new PropertyDescriptor ( "bounds", SSCurrencyField.class, "getBounds", "setBounds" );
            properties[PROPERTY_caret] = new PropertyDescriptor ( "caret", SSCurrencyField.class, "getCaret", "setCaret" );
            properties[PROPERTY_caretColor] = new PropertyDescriptor ( "caretColor", SSCurrencyField.class, "getCaretColor", "setCaretColor" );
            properties[PROPERTY_caretListeners] = new PropertyDescriptor ( "caretListeners", SSCurrencyField.class, "getCaretListeners", null );
            properties[PROPERTY_caretPosition] = new PropertyDescriptor ( "caretPosition", SSCurrencyField.class, "getCaretPosition", "setCaretPosition" );
            properties[PROPERTY_colorModel] = new PropertyDescriptor ( "colorModel", SSCurrencyField.class, "getColorModel", null );
            properties[PROPERTY_columnName] = new PropertyDescriptor ( "columnName", SSCurrencyField.class, null, "setColumnName" );
            properties[PROPERTY_columnName].setPreferred ( true );
            properties[PROPERTY_columns] = new PropertyDescriptor ( "columns", SSCurrencyField.class, "getColumns", "setColumns" );
            properties[PROPERTY_columns].setPreferred ( true );
            properties[PROPERTY_component] = new IndexedPropertyDescriptor ( "component", SSCurrencyField.class, null, null, "getComponent", null );
            properties[PROPERTY_componentCount] = new PropertyDescriptor ( "componentCount", SSCurrencyField.class, "getComponentCount", null );
            properties[PROPERTY_componentListeners] = new PropertyDescriptor ( "componentListeners", SSCurrencyField.class, "getComponentListeners", null );
            properties[PROPERTY_componentOrientation] = new PropertyDescriptor ( "componentOrientation", SSCurrencyField.class, "getComponentOrientation", "setComponentOrientation" );
            properties[PROPERTY_componentPopupMenu] = new PropertyDescriptor ( "componentPopupMenu", SSCurrencyField.class, "getComponentPopupMenu", "setComponentPopupMenu" );
            properties[PROPERTY_components] = new PropertyDescriptor ( "components", SSCurrencyField.class, "getComponents", null );
            properties[PROPERTY_containerListeners] = new PropertyDescriptor ( "containerListeners", SSCurrencyField.class, "getContainerListeners", null );
            properties[PROPERTY_cursor] = new PropertyDescriptor ( "cursor", SSCurrencyField.class, "getCursor", "setCursor" );
            properties[PROPERTY_cursorSet] = new PropertyDescriptor ( "cursorSet", SSCurrencyField.class, "isCursorSet", null );
            properties[PROPERTY_debugGraphicsOptions] = new PropertyDescriptor ( "debugGraphicsOptions", SSCurrencyField.class, "getDebugGraphicsOptions", "setDebugGraphicsOptions" );
            properties[PROPERTY_decimals] = new PropertyDescriptor ( "decimals", SSCurrencyField.class, "getDecimals", "setDecimals" );
            properties[PROPERTY_decimals].setPreferred ( true );
            properties[PROPERTY_disabledTextColor] = new PropertyDescriptor ( "disabledTextColor", SSCurrencyField.class, "getDisabledTextColor", "setDisabledTextColor" );
            properties[PROPERTY_displayable] = new PropertyDescriptor ( "displayable", SSCurrencyField.class, "isDisplayable", null );
            properties[PROPERTY_document] = new PropertyDescriptor ( "document", SSCurrencyField.class, "getDocument", "setDocument" );
            properties[PROPERTY_doubleBuffered] = new PropertyDescriptor ( "doubleBuffered", SSCurrencyField.class, "isDoubleBuffered", "setDoubleBuffered" );
            properties[PROPERTY_dragEnabled] = new PropertyDescriptor ( "dragEnabled", SSCurrencyField.class, "getDragEnabled", "setDragEnabled" );
            properties[PROPERTY_dropTarget] = new PropertyDescriptor ( "dropTarget", SSCurrencyField.class, "getDropTarget", "setDropTarget" );
            properties[PROPERTY_editable] = new PropertyDescriptor ( "editable", SSCurrencyField.class, "isEditable", "setEditable" );
            properties[PROPERTY_editValid] = new PropertyDescriptor ( "editValid", SSCurrencyField.class, "isEditValid", null );
            properties[PROPERTY_enabled] = new PropertyDescriptor ( "enabled", SSCurrencyField.class, "isEnabled", "setEnabled" );
            properties[PROPERTY_focusable] = new PropertyDescriptor ( "focusable", SSCurrencyField.class, "isFocusable", "setFocusable" );
            properties[PROPERTY_focusAccelerator] = new PropertyDescriptor ( "focusAccelerator", SSCurrencyField.class, "getFocusAccelerator", "setFocusAccelerator" );
            properties[PROPERTY_focusCycleRoot] = new PropertyDescriptor ( "focusCycleRoot", SSCurrencyField.class, "isFocusCycleRoot", "setFocusCycleRoot" );
            properties[PROPERTY_focusCycleRootAncestor] = new PropertyDescriptor ( "focusCycleRootAncestor", SSCurrencyField.class, "getFocusCycleRootAncestor", null );
            properties[PROPERTY_focusListeners] = new PropertyDescriptor ( "focusListeners", SSCurrencyField.class, "getFocusListeners", null );
            properties[PROPERTY_focusLostBehavior] = new PropertyDescriptor ( "focusLostBehavior", SSCurrencyField.class, "getFocusLostBehavior", "setFocusLostBehavior" );
            properties[PROPERTY_focusOwner] = new PropertyDescriptor ( "focusOwner", SSCurrencyField.class, "isFocusOwner", null );
            properties[PROPERTY_focusTraversable] = new PropertyDescriptor ( "focusTraversable", SSCurrencyField.class, "isFocusTraversable", null );
            properties[PROPERTY_focusTraversalKeys] = new IndexedPropertyDescriptor ( "focusTraversalKeys", SSCurrencyField.class, null, null, "getFocusTraversalKeys", "setFocusTraversalKeys" );
            properties[PROPERTY_focusTraversalKeysEnabled] = new PropertyDescriptor ( "focusTraversalKeysEnabled", SSCurrencyField.class, "getFocusTraversalKeysEnabled", "setFocusTraversalKeysEnabled" );
            properties[PROPERTY_focusTraversalPolicy] = new PropertyDescriptor ( "focusTraversalPolicy", SSCurrencyField.class, "getFocusTraversalPolicy", "setFocusTraversalPolicy" );
            properties[PROPERTY_focusTraversalPolicyProvider] = new PropertyDescriptor ( "focusTraversalPolicyProvider", SSCurrencyField.class, "isFocusTraversalPolicyProvider", "setFocusTraversalPolicyProvider" );
            properties[PROPERTY_focusTraversalPolicySet] = new PropertyDescriptor ( "focusTraversalPolicySet", SSCurrencyField.class, "isFocusTraversalPolicySet", null );
            properties[PROPERTY_font] = new PropertyDescriptor ( "font", SSCurrencyField.class, "getFont", "setFont" );
            properties[PROPERTY_fontSet] = new PropertyDescriptor ( "fontSet", SSCurrencyField.class, "isFontSet", null );
            properties[PROPERTY_foreground] = new PropertyDescriptor ( "foreground", SSCurrencyField.class, "getForeground", "setForeground" );
            properties[PROPERTY_foregroundSet] = new PropertyDescriptor ( "foregroundSet", SSCurrencyField.class, "isForegroundSet", null );
            properties[PROPERTY_formatter] = new PropertyDescriptor ( "formatter", SSCurrencyField.class, "getFormatter", null );
            properties[PROPERTY_formatterFactory] = new PropertyDescriptor ( "formatterFactory", SSCurrencyField.class, "getFormatterFactory", "setFormatterFactory" );
            properties[PROPERTY_graphics] = new PropertyDescriptor ( "graphics", SSCurrencyField.class, "getGraphics", null );
            properties[PROPERTY_graphicsConfiguration] = new PropertyDescriptor ( "graphicsConfiguration", SSCurrencyField.class, "getGraphicsConfiguration", null );
            properties[PROPERTY_height] = new PropertyDescriptor ( "height", SSCurrencyField.class, "getHeight", null );
            properties[PROPERTY_helper] = new PropertyDescriptor ( "helper", SSCurrencyField.class, null, "setHelper" );
            properties[PROPERTY_helper].setPreferred ( true );
            properties[PROPERTY_hierarchyBoundsListeners] = new PropertyDescriptor ( "hierarchyBoundsListeners", SSCurrencyField.class, "getHierarchyBoundsListeners", null );
            properties[PROPERTY_hierarchyListeners] = new PropertyDescriptor ( "hierarchyListeners", SSCurrencyField.class, "getHierarchyListeners", null );
            properties[PROPERTY_highlighter] = new PropertyDescriptor ( "highlighter", SSCurrencyField.class, "getHighlighter", "setHighlighter" );
            properties[PROPERTY_horizontalAlignment] = new PropertyDescriptor ( "horizontalAlignment", SSCurrencyField.class, "getHorizontalAlignment", "setHorizontalAlignment" );
            properties[PROPERTY_horizontalVisibility] = new PropertyDescriptor ( "horizontalVisibility", SSCurrencyField.class, "getHorizontalVisibility", null );
            properties[PROPERTY_ignoreRepaint] = new PropertyDescriptor ( "ignoreRepaint", SSCurrencyField.class, "getIgnoreRepaint", "setIgnoreRepaint" );
            properties[PROPERTY_inheritsPopupMenu] = new PropertyDescriptor ( "inheritsPopupMenu", SSCurrencyField.class, "getInheritsPopupMenu", "setInheritsPopupMenu" );
            properties[PROPERTY_inputContext] = new PropertyDescriptor ( "inputContext", SSCurrencyField.class, "getInputContext", null );
            properties[PROPERTY_inputMethodListeners] = new PropertyDescriptor ( "inputMethodListeners", SSCurrencyField.class, "getInputMethodListeners", null );
            properties[PROPERTY_inputMethodRequests] = new PropertyDescriptor ( "inputMethodRequests", SSCurrencyField.class, "getInputMethodRequests", null );
            properties[PROPERTY_inputVerifier] = new PropertyDescriptor ( "inputVerifier", SSCurrencyField.class, "getInputVerifier", "setInputVerifier" );
            properties[PROPERTY_insets] = new PropertyDescriptor ( "insets", SSCurrencyField.class, "getInsets", null );
            properties[PROPERTY_keyListeners] = new PropertyDescriptor ( "keyListeners", SSCurrencyField.class, "getKeyListeners", null );
            properties[PROPERTY_keymap] = new PropertyDescriptor ( "keymap", SSCurrencyField.class, "getKeymap", "setKeymap" );
            properties[PROPERTY_layout] = new PropertyDescriptor ( "layout", SSCurrencyField.class, "getLayout", "setLayout" );
            properties[PROPERTY_lightweight] = new PropertyDescriptor ( "lightweight", SSCurrencyField.class, "isLightweight", null );
            properties[PROPERTY_locale] = new PropertyDescriptor ( "locale", SSCurrencyField.class, "getLocale", "setLocale" );
            properties[PROPERTY_locationOnScreen] = new PropertyDescriptor ( "locationOnScreen", SSCurrencyField.class, "getLocationOnScreen", null );
            properties[PROPERTY_managingFocus] = new PropertyDescriptor ( "managingFocus", SSCurrencyField.class, "isManagingFocus", null );
            properties[PROPERTY_margin] = new PropertyDescriptor ( "margin", SSCurrencyField.class, "getMargin", "setMargin" );
            properties[PROPERTY_maximumSize] = new PropertyDescriptor ( "maximumSize", SSCurrencyField.class, "getMaximumSize", "setMaximumSize" );
            properties[PROPERTY_maximumSizeSet] = new PropertyDescriptor ( "maximumSizeSet", SSCurrencyField.class, "isMaximumSizeSet", null );
            properties[PROPERTY_minimumSize] = new PropertyDescriptor ( "minimumSize", SSCurrencyField.class, "getMinimumSize", "setMinimumSize" );
            properties[PROPERTY_minimumSizeSet] = new PropertyDescriptor ( "minimumSizeSet", SSCurrencyField.class, "isMinimumSizeSet", null );
            properties[PROPERTY_mouseListeners] = new PropertyDescriptor ( "mouseListeners", SSCurrencyField.class, "getMouseListeners", null );
            properties[PROPERTY_mouseMotionListeners] = new PropertyDescriptor ( "mouseMotionListeners", SSCurrencyField.class, "getMouseMotionListeners", null );
            properties[PROPERTY_mousePosition] = new PropertyDescriptor ( "mousePosition", SSCurrencyField.class, "getMousePosition", null );
            properties[PROPERTY_mouseWheelListeners] = new PropertyDescriptor ( "mouseWheelListeners", SSCurrencyField.class, "getMouseWheelListeners", null );
            properties[PROPERTY_name] = new PropertyDescriptor ( "name", SSCurrencyField.class, "getName", "setName" );
            properties[PROPERTY_navigationFilter] = new PropertyDescriptor ( "navigationFilter", SSCurrencyField.class, "getNavigationFilter", "setNavigationFilter" );
            properties[PROPERTY_navigator] = new PropertyDescriptor ( "navigator", SSCurrencyField.class, "getNavigator", "setNavigator" );
            properties[PROPERTY_navigator].setPreferred ( true );
            properties[PROPERTY_nextFocusableComponent] = new PropertyDescriptor ( "nextFocusableComponent", SSCurrencyField.class, "getNextFocusableComponent", "setNextFocusableComponent" );
            properties[PROPERTY_opaque] = new PropertyDescriptor ( "opaque", SSCurrencyField.class, "isOpaque", "setOpaque" );
            properties[PROPERTY_optimizedDrawingEnabled] = new PropertyDescriptor ( "optimizedDrawingEnabled", SSCurrencyField.class, "isOptimizedDrawingEnabled", null );
            properties[PROPERTY_paintingTile] = new PropertyDescriptor ( "paintingTile", SSCurrencyField.class, "isPaintingTile", null );
            properties[PROPERTY_parent] = new PropertyDescriptor ( "parent", SSCurrencyField.class, "getParent", null );
            properties[PROPERTY_peer] = new PropertyDescriptor ( "peer", SSCurrencyField.class, "getPeer", null );
            properties[PROPERTY_precision] = new PropertyDescriptor ( "precision", SSCurrencyField.class, "getPrecision", "setPrecision" );
            properties[PROPERTY_precision].setPreferred ( true );
            properties[PROPERTY_preferredScrollableViewportSize] = new PropertyDescriptor ( "preferredScrollableViewportSize", SSCurrencyField.class, "getPreferredScrollableViewportSize", null );
            properties[PROPERTY_preferredSize] = new PropertyDescriptor ( "preferredSize", SSCurrencyField.class, "getPreferredSize", "setPreferredSize" );
            properties[PROPERTY_preferredSizeSet] = new PropertyDescriptor ( "preferredSizeSet", SSCurrencyField.class, "isPreferredSizeSet", null );
            properties[PROPERTY_propertyChangeListeners] = new PropertyDescriptor ( "propertyChangeListeners", SSCurrencyField.class, "getPropertyChangeListeners", null );
            properties[PROPERTY_registeredKeyStrokes] = new PropertyDescriptor ( "registeredKeyStrokes", SSCurrencyField.class, "getRegisteredKeyStrokes", null );
            properties[PROPERTY_requestFocusEnabled] = new PropertyDescriptor ( "requestFocusEnabled", SSCurrencyField.class, "isRequestFocusEnabled", "setRequestFocusEnabled" );
            properties[PROPERTY_rootPane] = new PropertyDescriptor ( "rootPane", SSCurrencyField.class, "getRootPane", null );
            properties[PROPERTY_rowSet] = new PropertyDescriptor ( "rowSet", SSCurrencyField.class, null, "setRowSet" );
            properties[PROPERTY_rowSet].setExpert ( true );
            properties[PROPERTY_rowSet].setPreferred ( true );
            properties[PROPERTY_scrollableTracksViewportHeight] = new PropertyDescriptor ( "scrollableTracksViewportHeight", SSCurrencyField.class, "getScrollableTracksViewportHeight", null );
            properties[PROPERTY_scrollableTracksViewportWidth] = new PropertyDescriptor ( "scrollableTracksViewportWidth", SSCurrencyField.class, "getScrollableTracksViewportWidth", null );
            properties[PROPERTY_scrollOffset] = new PropertyDescriptor ( "scrollOffset", SSCurrencyField.class, "getScrollOffset", "setScrollOffset" );
            properties[PROPERTY_selectedText] = new PropertyDescriptor ( "selectedText", SSCurrencyField.class, "getSelectedText", null );
            properties[PROPERTY_selectedTextColor] = new PropertyDescriptor ( "selectedTextColor", SSCurrencyField.class, "getSelectedTextColor", "setSelectedTextColor" );
            properties[PROPERTY_selectionColor] = new PropertyDescriptor ( "selectionColor", SSCurrencyField.class, "getSelectionColor", "setSelectionColor" );
            properties[PROPERTY_selectionEnd] = new PropertyDescriptor ( "selectionEnd", SSCurrencyField.class, "getSelectionEnd", "setSelectionEnd" );
            properties[PROPERTY_selectionStart] = new PropertyDescriptor ( "selectionStart", SSCurrencyField.class, "getSelectionStart", "setSelectionStart" );
            properties[PROPERTY_showing] = new PropertyDescriptor ( "showing", SSCurrencyField.class, "isShowing", null );
            properties[PROPERTY_text] = new PropertyDescriptor ( "text", SSCurrencyField.class, "getText", "setText" );
            properties[PROPERTY_toolkit] = new PropertyDescriptor ( "toolkit", SSCurrencyField.class, "getToolkit", null );
            properties[PROPERTY_toolTipText] = new PropertyDescriptor ( "toolTipText", SSCurrencyField.class, "getToolTipText", "setToolTipText" );
            properties[PROPERTY_topLevelAncestor] = new PropertyDescriptor ( "topLevelAncestor", SSCurrencyField.class, "getTopLevelAncestor", null );
            properties[PROPERTY_transferHandler] = new PropertyDescriptor ( "transferHandler", SSCurrencyField.class, "getTransferHandler", "setTransferHandler" );
            properties[PROPERTY_treeLock] = new PropertyDescriptor ( "treeLock", SSCurrencyField.class, "getTreeLock", null );
            properties[PROPERTY_UI] = new PropertyDescriptor ( "UI", SSCurrencyField.class, "getUI", "setUI" );
            properties[PROPERTY_UIClassID] = new PropertyDescriptor ( "UIClassID", SSCurrencyField.class, "getUIClassID", null );
            properties[PROPERTY_valid] = new PropertyDescriptor ( "valid", SSCurrencyField.class, "isValid", null );
            properties[PROPERTY_validateRoot] = new PropertyDescriptor ( "validateRoot", SSCurrencyField.class, "isValidateRoot", null );
            properties[PROPERTY_value] = new PropertyDescriptor ( "value", SSCurrencyField.class, "getValue", "setValue" );
            properties[PROPERTY_verifyInputWhenFocusTarget] = new PropertyDescriptor ( "verifyInputWhenFocusTarget", SSCurrencyField.class, "getVerifyInputWhenFocusTarget", "setVerifyInputWhenFocusTarget" );
            properties[PROPERTY_vetoableChangeListeners] = new PropertyDescriptor ( "vetoableChangeListeners", SSCurrencyField.class, "getVetoableChangeListeners", null );
            properties[PROPERTY_visible] = new PropertyDescriptor ( "visible", SSCurrencyField.class, "isVisible", "setVisible" );
            properties[PROPERTY_visibleRect] = new PropertyDescriptor ( "visibleRect", SSCurrencyField.class, "getVisibleRect", null );
            properties[PROPERTY_width] = new PropertyDescriptor ( "width", SSCurrencyField.class, "getWidth", null );
            properties[PROPERTY_x] = new PropertyDescriptor ( "x", SSCurrencyField.class, "getX", null );
            properties[PROPERTY_y] = new PropertyDescriptor ( "y", SSCurrencyField.class, "getY", null );
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

