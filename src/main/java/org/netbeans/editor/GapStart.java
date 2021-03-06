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

package org.netbeans.editor;

/**
 * A given object can publish this interface if it allows
 * an efficient access to its gap-based data storage
 * and wants to give its clients a hint about how to access
 * the data efficiently.
 * <P>For example {@link javax.swing.text.Document} instance
 * having gap-based document content can allow to get an instance
 * of GapStart as a property:<PRE>
 *      GapStart gs = (GapStart)doc.getProperty(GapStart.class);
 *      int gapStart = gs.getGapStart();
 * <PRE>
 * Once the start of the gap is known the client can optimize
 * access to the document's data. For example if the client
 * does not care about the chunks in which it gets the document's data
 * it can access the characters so that no character copying is done:<PRE>
 *      Segment text = new Segment();
 *      doc.getText(0, gapStart, text); // document's data below gap
 *      ...
 *      doc.getText(gapStart, doc.getLength(), text); // document's data over gap
 *      ...
 * <PRE>
 *
 * @author Miloslav Metelka
 * @version 1.00
 */

public interface GapStart {

    /**
     * Get the begining of the gap in the object's gap-based data.
     * @return &gt;=0 and &lt;= total size of the data of the object.
     */
    public int getGapStart();

}
