/*
 * SSImageField.java
 */

package com.nqadmin.swingSet.formatting;

import com.nqadmin.swingSet.SSDataNavigator;
import com.nqadmin.swingSet.datasources.SSRowSet;
import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import javax.sql.RowSetListener;
import javax.swing.ImageIcon;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

/**
 *
 * @author Diego Gil
 */
public class SSImageField extends JPanel implements SSField, RowSetListener, KeyListener, ComponentListener {
    
    private BufferedImage bufferedImage;
    private byte[] imageBytes;
    private ImageIcon fullIcon;
    private ImageIcon scaledIcon;
    private ImageIcon nullIcon;
    private JButton imageButton;
    private JButton getButton;
    private JScrollPane scrollPane;
    
    private java.awt.Color std_color = null;
    private String columnName = null;
    private int colType = -99;
    private SSRowSet rowset = null;
    private SSDataNavigator navigator = null;
    
    /** Creates a new instance of SSImageField */
    public SSImageField() {
        super();
        
        Set forwardKeys    = getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
        Set newForwardKeys = new HashSet(forwardKeys);
        newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
        newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, java.awt.event.InputEvent.SHIFT_MASK ));
        setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,newForwardKeys);
        
        Set backwardKeys    = getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS);
        Set newBackwardKeys = new HashSet(backwardKeys);
        newBackwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_UP, java.awt.event.InputEvent.SHIFT_MASK ));
        setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,newBackwardKeys);
        
        this.addKeyListener(this);
        
        this.addComponentListener(this);
        
        setLayout(new BorderLayout());
        
        imageButton = new JButton();
        imageButton.setFocusable(false);
        
        imageButton.setIconTextGap(0);
        imageButton.setBorder(null);
        imageButton.setMargin(new Insets(0,0,0,0));
        imageButton.setText("");
        nullIcon = new ImageIcon(getClass().getResource("/com/nqadmin/swingSet/formatting/image.png"));
        fullIcon = nullIcon;
        imageButton.setIcon(fullIcon);
        add(imageButton, BorderLayout.CENTER);
        validate();
        
//        scrollPane = new JScrollPane(imageButton);
//        add(scrollPane, BorderLayout.CENTER);
        
        getButton = new JButton("from file ...");
        getButton.setFocusable(false);
        
        getButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try{
                    //if (rowset != null) {
                    FileInputStream inStream = null;
                    File inFile = null;
                    JFileChooser fileChooser = new JFileChooser();
                    if(fileChooser.showOpenDialog(getButton) == JFileChooser.APPROVE_OPTION){
                        inFile = fileChooser.getSelectedFile();
                        inStream = new FileInputStream(inFile);
                        int totalLength = (int)inFile.length();
                        byte[] bytes = new byte[totalLength];
                        int bytesRead = inStream.read(bytes);
                        while (bytesRead < totalLength){
                            int read = inStream.read(bytes, bytesRead, totalLength - bytesRead);
                            if(read == -1)
                                break;
                            else
                                bytesRead += read;
                        }
                        //rowset.updateBytes(columnName, bytes);
                        imageBytes = bytes;
                        fullIcon = new ImageIcon(bytes);
                        try {
                            rowset.updateBytes(columnName, imageBytes);
                        } catch( SQLException se) {
                            
                        }
                        Rescale();
                    } else {
                        return;
                    }
                    //}
                    //}catch(SQLException se){
                    //    se.printStackTrace();
                }catch(IOException ioe){
                    ioe.printStackTrace();
                }
            }
        });
        add(getButton, BorderLayout.SOUTH);
    }
    
    private ImageIcon Thumbnail(Image image) {
        double scale, fw, fh;
        int wi, hi;
        int wo, ho;
        int ws, hs;
        Image scaled;
        
        imageButton.setIcon(null);
        this.validate();
        
        wi = imageButton.getWidth();
        hi = imageButton.getHeight();
        
        wo = image.getWidth(this);
        ho = image.getHeight(this);
        
        fw = (double) wi / (double) wo;
        fh = (double) hi / (double) ho;
        
        if (fw > fh) scale = fh;
        else scale = fw;
        
        ws = (int) (scale * wo);
        hs = (int) (scale * ho);
        
        
//        System.out.println("Field Size = " + wi + ", "  + hi);
//        System.out.println("Image Size = " + wo + ", "  + ho);
//        System.out.println("scale = " + scale);
        if (wi == 0 && hi ==0 ) {
            ws = wo; hs = ho;
        }
        scaled = image.getScaledInstance(ws, hs, image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }
    
    public void setColumnName(String columnName) {
        this.columnName = columnName;
        bind();
    }
    
    public String getColumnName() {
        return this.columnName;
    }
    
    public void setRowSet(SSRowSet rowset) {
        this.setSSRowSet(rowset);
    }
    
    public void setSSRowSet(SSRowSet rowset) {
        this.rowset = rowset;
        bind();
    }
    
    public SSRowSet getRowSet() {
        return this.getSSRowSet();
    }
    
    public SSRowSet getSSRowSet() {
        return this.rowset;
    }
    
     /**
     *
     * @deprecated
     **/
    public void setNavigator(SSDataNavigator navigator) {
        this.setSSDataNavigator(navigator);
    }
    
    /**
     *
     * @deprecated
     **/
    public SSDataNavigator getNavigator() {
        return this.getSSDataNavigator();
    }
    
    public void setSSDataNavigator(SSDataNavigator navigator) {
        this.navigator = navigator;
        setSSRowSet(navigator.getSSRowSet());
        bind();
    }
    
    public SSDataNavigator getSSDataNavigator() {
        return this.navigator;
    }
    private void DbToFm() {
        
        try {
            if (rowset.getRow() == 0) return;
            
            switch(colType) {
                
                case java.sql.Types.BINARY:
                    imageBytes = rowset.getBytes(columnName);
                    
                    if (imageBytes == null) {
                        fullIcon = nullIcon;
                        Rescale();
                        break;
                    }
                    
                    if (imageBytes.length > 0)
                        
                        fullIcon = new ImageIcon(imageBytes);
                    else
                        fullIcon = nullIcon;
                    Rescale();
                    break;
                    
                default:
                    break;
            }
        } catch (java.sql.SQLException sqe) {
            System.out.println("SSImageField --> Error in DbToFm() = " + sqe);
        }
    }
            
    /**
     * Sets the SSRowSet and column name to which the component is to be bound.
     *
     * @param _sSRowSet    datasource to be used.
     * @param _columnName  Name of the column to which this check box should be bound
     */
    public void bind(SSRowSet _sSRowSet, String _columnName) {
        rowset = _sSRowSet;
        columnName = _columnName;
        bind();
    }

    private void bind() {
        
        if (this.columnName == null) return;
        if (this.rowset  == null) return;
        
        try {
            colType = rowset.getColumnType(columnName);
        } catch(java.sql.SQLException sqe) {
            System.out.println("bind error = " + sqe);
        }
        rowset.addRowSetListener(this);
        DbToFm();
    }
    
    public void rowSetChanged(javax.sql.RowSetEvent event) {
        
    }
    
    public void rowChanged(javax.sql.RowSetEvent event) {
        
    }
    
    public void cursorMoved(javax.sql.RowSetEvent event) {
        DbToFm();
    }
    
    
    public void keyTyped(KeyEvent e) {
    }
    
    public void keyReleased(KeyEvent e) {
    }
    
    /**
     *  Catch severals keys, to implement some forms functionality (To be done).
     *
     */
    public void keyPressed(KeyEvent e) {
        
        if (e.getKeyCode() == KeyEvent.VK_F1) {
        }
        
        if (e.getKeyCode() == KeyEvent.VK_F2) {
        }
        
        if (e.getKeyCode() == KeyEvent.VK_F3) {
            
        }
        
        if (e.getKeyCode() == KeyEvent.VK_F4) {
            System.out.println("F4 ");
        }
        
        if (e.getKeyCode() == KeyEvent.VK_F5) {
            System.out.println("F5 = PROCESS");
            navigator.doCommitButtonClick();
        }
        
        if (e.getKeyCode() == KeyEvent.VK_F6) {
            System.out.println("F6 = DELETE");
            navigator.doDeleteButtonClick();
        }
        
        if (e.getKeyCode() == KeyEvent.VK_F8) {
            System.out.println("F8 ");
            navigator.doUndoButtonClick();
        }
        
        if (e.getKeyCode() == KeyEvent.VK_END) {
            System.out.println("END ");
        }
        
        if (e.getKeyCode() == KeyEvent.VK_DELETE) {
            System.out.println("DELETE ");
        }
        
        if (e.getKeyCode() == KeyEvent.VK_HOME) {
            System.out.println("HOME ");
        }
        
    }
    
    public void componentShown(java.awt.event.ComponentEvent e) {
    }
    
    public void componentResized(java.awt.event.ComponentEvent e) {
        //System.out.println("componentResized()");
        Rescale();
    }
    
    public void componentMoved(java.awt.event.ComponentEvent e) {
    }
    
    public void componentHidden(java.awt.event.ComponentEvent e) {
    }
    
    private void Rescale() {
        if (fullIcon != null) {
            if (!fullIcon.equals(nullIcon))
                scaledIcon = Thumbnail(fullIcon.getImage());
            else
                scaledIcon = fullIcon;
            imageButton.setIcon(scaledIcon);
            updateUI();
        }
    }

    public void cleanField() {
    }
}
