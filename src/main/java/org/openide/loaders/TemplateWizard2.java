 /*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.openide.loaders;

import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.awt.event.KeyEvent;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.openide.ErrorManager;
import org.openide.explorer.propertysheet.DefaultPropertyModel;
import org.openide.explorer.propertysheet.PropertyPanel;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/** Dialog that can be used in create from template.
 *
 * @author  Jaroslav Tulach, Jiri Rechtacek
 */
final class TemplateWizard2 extends javax.swing.JPanel implements DocumentListener {
    
    /** listener to changes in the wizard */
    private ChangeListener listener;
    
    private static final String PROP_LOCATION_FOLDER = "locationFolder"; // NOI18N
    private DataFolder locationFolder;
    private Reference  fileSystemRef = new WeakReference (null);
    private DefaultPropertyModel locationFolderModel;

    /** File extension of the template and of the created file -
     * it is used to test whether file already exists.
     */
    private String extension;

    /** Creates new form TemplateWizard2 */
    public TemplateWizard2() {
        initLocationFolder ();
        initComponents ();
        setName (DataObject.getString("LAB_TargetLocationPanelName"));  // NOI18N

        // registers itself to listen to changes in the content of document
        java.util.ResourceBundle bundle = org.openide.util.NbBundle.getBundle(TemplateWizard2.class);
        newObjectName.getDocument().addDocumentListener(this);
        newObjectName.unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
        newObjectName.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_NewObjectName")); // NOI18N
        
        
    }
    
    /** This method is called from within the constructor to
     * initialize the location folder and make it accessible.
     * The getter/setter methods must be accessible for purposes introspection.
     * Because this class is not public then these methods are made accessible explicitly.
     */
    private void initLocationFolder () {
        PropertyDescriptor pd = null;
        try {
            Method getterMethod = this.getClass ().getDeclaredMethod("getLocationFolder", new Class[] {}); // NOI18N
            getterMethod.setAccessible (true);
            Method setterMethod = this.getClass ().getDeclaredMethod("setLocationFolder", new Class[] {DataFolder.class}); // NOI18N
            setterMethod.setAccessible (true);
            pd = new PropertyDescriptor (PROP_LOCATION_FOLDER, getterMethod, setterMethod);
        } catch (java.beans.IntrospectionException ie) {
            ErrorManager.getDefault ().notify (ie);
        } catch (NoSuchMethodException nsme) {
            ErrorManager.getDefault ().notify (nsme);
        } 
        locationFolderModel = new DefaultPropertyModel (this, pd);
    }

    /** Getter for default name of a new object.
    * @return the default name.
    */
    private static String defaultNewObjectName () {
        return DataObject.getString ("FMT_DefaultNewObjectName"); // NOI18N
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        namePanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        newObjectName = new javax.swing.JTextField();
        dataFolderPanel = dataFolderPanel = new PropertyPanel (locationFolderModel, PropertyPanel.PREF_CUSTOM_EDITOR);

        setLayout(new java.awt.BorderLayout());

        namePanel.setLayout(new java.awt.BorderLayout(12, 0));

        jLabel1.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/openide/loaders/Bundle").getString("CTL_NewObjectName_Mnemonic").charAt(0));
        jLabel1.setLabelFor(newObjectName);
        jLabel1.setText(java.util.ResourceBundle.getBundle("org/openide/loaders/Bundle").getString("CTL_NewObjectName"));
        namePanel.add(jLabel1, java.awt.BorderLayout.WEST);

        newObjectName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                TemplateWizard2.this.newObjectNameFocusGained(evt);
            }
        });

        namePanel.add(newObjectName, java.awt.BorderLayout.CENTER);

        add(namePanel, java.awt.BorderLayout.NORTH);

        add(dataFolderPanel, java.awt.BorderLayout.CENTER);

    }//GEN-END:initComponents

    private void newObjectNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_newObjectNameFocusGained
        //Code below works perfectly on Solaris with JDK 1.3 or 1.4.  The
        //lines below show up in CVS annotate as by jglick, Apr 25, 2000, which
        //is the major reformat/SPL addition for open sourcing NB.  I'm 
        //guessing this was some 1.2 bug that is irrelevant now, but leaving
        //it in place in case of a problem.  -TDB
        /*
        if (
            Utilities.getOperatingSystem() == Utilities.OS_SOLARIS |
            Utilities.getOperatingSystem() == Utilities.OS_SUNOS
        ) {
            // does not work on CDE window manager, so better do nothin
            return;
        }
         */
        newObjectName.selectAll ();
    }//GEN-LAST:event_newObjectNameFocusGained

    /** Add a listener to changes of the panel's validity.
     * @param l the listener to add
     * @see #isValid
     *
     */
    void addChangeListener(ChangeListener l) {
        if (listener != null) throw new IllegalStateException ();

        listener = l;
    }    
    
    public void addNotify () {
        super.addNotify();
        //Fix for issue 31086, initial focus on Back button 
        newObjectName.requestFocus();
    }
    
    /** Helper implementation of WizardDescription.Panel for TemplateWizard.Panel2.
     * Provides the wizard panel with the current data--either
     * the default data or already-modified settings, if the user used the previous and/or next buttons.
     * This method can be called multiple times on one instance of <code>WizardDescriptor.Panel</code>.
     * <p>The settings object is originally supplied to {@link WizardDescriptor#WizardDescriptor(WizardDescriptor.Iterator,Object)}.
     * In the case of a <code>TemplateWizard.Iterator</code> panel, the object is
     * in fact the <code>TemplateWizard</code>.
     * @param settings the object representing wizard panel state
     * @exception IllegalStateException if the the data provided
     * by the wizard are not valid.
     *
     */
    void implReadSettings(Object settings) {
        TemplateWizard wizard = (TemplateWizard)settings;

        DataObject template = wizard.getTemplate ();
        if (template != null) {
            extension = template.getPrimaryFile().getExt();
        }
        
        setNewObjectName (wizard.getTargetName ());

        try {
            setLocationFolder (wizard.getTargetFolder ());
        } catch (IOException ioe) {
            setLocationFolder (null);
        }
    }
    
    /** Remove a listener to changes of the panel's validity.
     * @param l the listener to remove
     *
     */
    public void removeChangeListener(ChangeListener l) {
        listener = null;
    }
    
    /** Helper implementation of WizardDescription.Panel for TemplateWizard.Panel2.
     * Provides the wizard panel with the opportunity to update the
     * settings with its current customized state.
     * Rather than updating its settings with every change in the GUI, it should collect them,
     * and then only save them when requested to by this method.
     * Also, the original settings passed to {@link #readSettings} should not be modified (mutated);
     * rather, the object passed in here should be mutated according to the collected changes,
     * in case it is a copy.
     * This method can be called multiple times on one instance of <code>WizardDescriptor.Panel</code>.
     * <p>The settings object is originally supplied to {@link WizardDescriptor#WizardDescriptor(WizardDescriptor.Iterator,Object)}.
     * In the case of a <code>TemplateWizard.Iterator</code> panel, the object is
     * in fact the <code>TemplateWizard</code>.
     * @param settings the object representing wizard panel state
     *
     */
    void implStoreSettings(Object settings) {
        TemplateWizard wizard = (TemplateWizard)settings;

        wizard.setTargetFolder (locationFolder);

        String name = newObjectName.getText ();
        if (name.equals (defaultNewObjectName ())) {
            name = null;
        }
        wizard.setTargetName (name);
    }

    /** Helper implementation of WizardDescription.Panel for TemplateWizard.Panel2.
    * Test whether the panel is finished and it is safe to proceed to the next one.
    * If the panel is valid, the "Next" (or "Finish") button will be enabled.
    * @return <code>null</code> if the user has entered satisfactory information
    * or localized string describing the error.
    */
    String implIsValid () {
        // test whether the selected folder on selected filesystem already exists
        FileSystem fs = (FileSystem)fileSystemRef.get ();
        if (locationFolder == null || fs == null)
            return NbBundle.getMessage(TemplateWizard2.class, "MSG_fs_or_folder_does_not_exist"); // NOI18N
        
        // target filesystem should be writable
        if (((FileSystem)fileSystemRef.get ()).isReadOnly ())
            return NbBundle.getMessage(TemplateWizard2.class, "MSG_fs_is_readonly"); // NOI18N
        
        if (locationFolder == null) locationFolder = DataFolder.findFolder (fs.getRoot());
        
        // test whether the selected name already exists
        StringBuffer sb = new StringBuffer ();
        sb.append (locationFolder.getPrimaryFile ().getPath ());
        sb.append ("/");
        sb.append (newObjectName.getText ());
        if ("" != extension) { // NOI18N
            sb.append ('.');
            sb.append (extension);
        }
        FileObject f = fs.findResource (sb.toString ());
        if (f != null) {
            return NbBundle.getMessage(TemplateWizard2.class, "MSG_file_already_exist", sb.toString()); // NOI18N
        }

        // all ok
        return null;
    }
    
    /** Gives notification that an attribute or set of attributes changed.
     *
     * @param e the document event
     *
     */
    public void changedUpdate(DocumentEvent e) {
        if (e.getDocument () == newObjectName.getDocument ()) {
            SwingUtilities.invokeLater (new Updater ());
        }
    }
    
    /** Gives notification that there was an insert into the document.  The
     * range given by the DocumentEvent bounds the freshly inserted region.
     *
     * @param e the document event
     *
     */
    public void insertUpdate(DocumentEvent e) {
        changedUpdate (e);
    }
    
    /** Gives notification that a portion of the document has been
     * removed.  The range is given in terms of what the view last
     * saw (that is, before updating sticky positions).
     *
     * @param e the document event
     *
     */
    public void removeUpdate(DocumentEvent e) {
        // so just check the name
        if (e.getDocument () == newObjectName.getDocument ()) {
            SwingUtilities.invokeLater (new Updater ());
        }
    }
    
    /** Does the update called from changedUpdate, insertUpdate and
     *  removeUpdate methods.
     */
    private class Updater implements Runnable {
        Updater() {}
        public void run () {
            if (newObjectName.getText().equals ("")) { // NOI18N
                setNewObjectName (""); // NOI18N
            }

            fireStateChanged ();
        }
    }

    /** Request focus.
    */
    public void requestFocus () {
        newObjectName.requestFocus();
        newObjectName.selectAll ();
    }

    /** Sets the class name to some reasonable value.
    * @param name the name to set the name to
    */
    private void setNewObjectName (String name) {
        String n = name;
        if (name == null || name.length () == 0) {
            n = defaultNewObjectName ();
        }

        newObjectName.getDocument().removeDocumentListener(this);
        newObjectName.setText (n);
        newObjectName.getDocument().addDocumentListener(this);

        if (name == null || name.length () == 0) {
            newObjectName.selectAll ();
        }
    }

    /** Fires info to listener.
    */
    private void fireStateChanged () {
        if (listener != null) {
            listener.stateChanged (new ChangeEvent (this));
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField newObjectName;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel namePanel;
    private org.openide.explorer.propertysheet.PropertyPanel dataFolderPanel;
    // End of variables declaration//GEN-END:variables

    public void setLocationFolder (DataFolder fd) {
        if (locationFolder == fd)
            return ;
        if (locationFolder != null && locationFolder.equals (fd))
            return ;
        DataFolder oldLocation = locationFolder;
        locationFolder = fd;
        firePropertyChange (PROP_LOCATION_FOLDER, oldLocation, locationFolder);
        if (fd != null) {
            try {
                fileSystemRef = new WeakReference (fd.getPrimaryFile ().getFileSystem ());
            } catch (org.openide.filesystems.FileStateInvalidException fsie) {
                fileSystemRef = new WeakReference (null);
            }
        }
        fireStateChanged ();
    }
    
    public DataFolder getLocationFolder () {
        return locationFolder;
    }
    
}
