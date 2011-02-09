/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * GuiTest.java
 *
 * Created on Nov 15, 2010, 11:24:30 PM
 */
package jellyfish.base;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import jellyfish.common.Common;
import jellyfish.matcher.AliasMatcherMap;
import jellyfish.matcher.AliasTreeNode;
import jellyfish.matcher.MatchResult;
import jellyfish.matcher.StorageTable;
import jellyfish.matcher.nodes.MatcherNode;
import jellyfish.tokenizer.Tokenizer;
import jellyfish.tokenizer.english.EnglishTokenizer;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

/**
 *
 * @author Xevia
 */
public final class GuiTest
        extends javax.swing.JFrame
{

    private Tokenizer tokenizer = new EnglishTokenizer( true );
    private boolean xmlTextIsDirty = false;
    private ClauseBase clauseBase;
    private List<MatchResult> results = Collections.EMPTY_LIST;
    private int currentResult = -1;
    private File currentDir = null;
    private String clearString =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<root>\n" +
            "    <clause name=\"field\">\n" +
            "        <g alias=\"field_def\">\n" +
            "            <o>\n" +
            "                <o>\n" +
            "                    <g alias=\"schema_name\">a</g>.\n" +
            "                </o>\n" +
            "                <g alias=\"table_name\">b</g>.\n" +
            "            </o>\n" +
            "            <g alias=\"field_name\">c</g>\n" +
            "        </g>\n" +
            "    </clause>\n" +
            "    <clause name=\"fields\" primary=\"true\">\n" +
            "        <ref>field</ref>\n" +
            "        <o>\n" +
            "            <r max=\"2\">\n" +
            "                ,<ref>field</ref>\n" +
            "            </r>\n" +
            "        </o>\n" +
            "    </clause>\n" +
            "</root>\n";
    private String xmlString = "";
    
    /** Creates new form GuiTest */
    public GuiTest() {
        initComponents();
        setXmlString( clearString );
    }

    public boolean isXmlTextIsDirty() {
        return xmlTextIsDirty;
    }

    public void setXmlTextIsDirty( boolean xmlTextIsDirty ) {
        if (this.xmlTextIsDirty!=xmlTextIsDirty)
            this.firePropertyChange(
                    "xmlTextIsDirty",
                    this.xmlTextIsDirty,
                    this.xmlTextIsDirty = xmlTextIsDirty
                    );
    }

    public String getXmlString() {
        return xmlString;
    }

    public void setXmlString( String xmlString ) {
        if (!this.xmlString.equals( xmlString )) {
            this.firePropertyChange(
                    "xmlString",
                    this.xmlString,
                    this.xmlString = xmlString
                    );
            this.setXmlTextIsDirty( true );
        }
    }

    private class CreateClauseBase implements Runnable {

        DlgWait dlgWait;

        public CreateClauseBase( DlgWait dlgWait ) {
            this.dlgWait = dlgWait;
        }
        
        public void run() {
            String xml = txtXml.getText();
            java.io.ByteArrayInputStream inputStream = new ByteArrayInputStream( xml.getBytes() );
            ClauseBase newClauseBase = new ClauseBase( tokenizer );
            try {
                newClauseBase.build( inputStream );
                clauseBase = newClauseBase;
                setXmlTextIsDirty( false );
            } catch ( Exception ex ) {
//                ex.printStackTrace( System.out );
                JOptionPane.showMessageDialog( rootPane, Common.traceToString( ex ) );
            } finally {
                try {
                    inputStream.close();
                } catch ( IOException ex ) {
                }
            }

            dlgWait.setVisible( false );
        }

    }

    private void ensureLatestClauseBase() {
        if ( clauseBase != null && !xmlTextIsDirty ) {
            return;
        }

        DlgWait dlgWait = new DlgWait( GuiTest.this );

        Thread thread = new Thread( new CreateClauseBase(dlgWait) );
        thread.start();
        dlgWait.setVisible( true );
    }

    private void refreshResultPane()
    {
        if (currentResult>=0 && currentResult<results.size())
            lblResultIndex.setText( (currentResult+1)+"/"+results.size() );
        else
            lblResultIndex.setText( "<Empty>" );

        btnPrevResult.setEnabled( currentResult>0 );
        btnNextResult.setEnabled( currentResult<results.size()-1 );
    }

    private class DisplayStruct {
        AliasTreeNode node;
        int depth;

        public DisplayStruct( AliasTreeNode node, int depth ) {
            this.node = node;
            this.depth = depth;
        }
    }

    private void displayResults()
    {
        StringBuilder aliasTree = new StringBuilder();

        if (currentResult>=0 && currentResult<results.size()) {
            MatchResult matchResult = results.get( currentResult );

            MatcherNode matchingNode = matchResult.getMatchingNode();
            AliasMatcherMap aliasMatcherMap = matchingNode.getAliasMatcherMap();

            StorageTable storageTable = new StorageTable(aliasMatcherMap.getRootNode());
            matchResult.fillValues( storageTable );

            Stack<DisplayStruct> displayStructs = new Stack<DisplayStruct>();
            displayStructs.add( new DisplayStruct( aliasMatcherMap.getRootNode(), 0 ) );
            while (!displayStructs.isEmpty()) {
                DisplayStruct displayStruct = displayStructs.pop();

                for (int i=0; i<displayStruct.depth; ++i)
                    aliasTree.append( "  " );
                aliasTree.append( displayStruct.node.getIndexedName() ).append( " - " );
                aliasTree.append( storageTable.get( displayStruct.node ) );
                aliasTree.append( "\n" );

                List<AliasTreeNode> aliasChildren = displayStruct.node.getChildren();
                for (int i=aliasChildren.size()-1; i>=0; --i) {
                    AliasTreeNode node = aliasChildren.get( i );
                    displayStructs.push( new DisplayStruct( node, displayStruct.depth+1) );
                }
            }

            ArrayList values = new ArrayList();
            matchResult.fillValues( values );
            ArrayList<String> valueStr = new ArrayList<String>(values.size());
            for (Object val:values)
                valueStr.add( val.toString() );
            txtConcatedResult.setText( clauseBase.getTokenizer().combine( valueStr ) );

        }
        txtOutput.setText( aliasTree.toString() );
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings( "unchecked" )
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        javax.swing.JSplitPane jSplitPane1 = new javax.swing.JSplitPane();
        jSplitPane2 = new javax.swing.JSplitPane();
        javax.swing.JPanel jPanel2 = new javax.swing.JPanel();
        javax.swing.JPanel jPanel3 = new javax.swing.JPanel();
        btnLoadXml = new javax.swing.JButton();
        btnSaveXml = new javax.swing.JButton();
        btnClearXml = new javax.swing.JButton();
        xmlContainer = new javax.swing.JPanel();
        javax.swing.JScrollPane xmlScrollpane = new javax.swing.JScrollPane();
        txtXml = new org.fife.ui.rsyntaxtextarea.RSyntaxTextArea();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        btnPrevResult = new javax.swing.JButton();
        lblResultIndex = new javax.swing.JLabel();
        btnNextResult = new javax.swing.JButton();
        javax.swing.JScrollPane outputScrollpane = new javax.swing.JScrollPane();
        txtOutput = new javax.swing.JEditorPane();
        javax.swing.JScrollPane jScrollPane2 = new javax.swing.JScrollPane();
        txtConcatedResult = new javax.swing.JTextPane();
        javax.swing.JPanel bottomPanel = new javax.swing.JPanel();
        javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
        txtInput = new javax.swing.JTextArea();
        javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
        btnMatch = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setResizeWeight(1.0);

        jSplitPane2.setResizeWeight(0.5);

        jPanel2.setLayout(new java.awt.BorderLayout());

        btnLoadXml.setText("Load");
        btnLoadXml.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadXmlActionPerformed(evt);
            }
        });
        jPanel3.add(btnLoadXml);

        btnSaveXml.setText("Save");
        btnSaveXml.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveXmlActionPerformed(evt);
            }
        });
        jPanel3.add(btnSaveXml);

        btnClearXml.setText("Clear");
        btnClearXml.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearXmlActionPerformed(evt);
            }
        });
        jPanel3.add(btnClearXml);

        jPanel2.add(jPanel3, java.awt.BorderLayout.PAGE_START);

        xmlContainer.setBackground(new java.awt.Color(255, 153, 153));
        xmlContainer.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, this, org.jdesktop.beansbinding.ELProperty.create("${xmlTextIsDirty}"), xmlContainer, org.jdesktop.beansbinding.BeanProperty.create("opaque"));
        bindingGroup.addBinding(binding);

        xmlContainer.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                xmlContainerPropertyChange(evt);
            }
        });
        xmlContainer.setLayout(new java.awt.BorderLayout());

        txtXml.setColumns(20);
        txtXml.setRows(5);
        txtXml.setSyntaxEditingStyle("text/xml");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${xmlString}"), txtXml, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        xmlScrollpane.setViewportView(txtXml);

        xmlContainer.add(xmlScrollpane, java.awt.BorderLayout.CENTER);

        jPanel2.add(xmlContainer, java.awt.BorderLayout.CENTER);

        jSplitPane2.setLeftComponent(jPanel2);

        jPanel4.setLayout(new java.awt.BorderLayout());

        btnPrevResult.setText("Prev");
        btnPrevResult.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrevResultActionPerformed(evt);
            }
        });
        jPanel5.add(btnPrevResult);

        lblResultIndex.setText("<empty>");
        lblResultIndex.setMaximumSize(new java.awt.Dimension(50, 16));
        lblResultIndex.setMinimumSize(new java.awt.Dimension(50, 16));
        lblResultIndex.setPreferredSize(new java.awt.Dimension(50, 16));
        jPanel5.add(lblResultIndex);

        btnNextResult.setText("Next");
        btnNextResult.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextResultActionPerformed(evt);
            }
        });
        jPanel5.add(btnNextResult);

        jPanel4.add(jPanel5, java.awt.BorderLayout.PAGE_START);

        txtOutput.setEditable(false);
        outputScrollpane.setViewportView(txtOutput);

        jPanel4.add(outputScrollpane, java.awt.BorderLayout.CENTER);

        jScrollPane2.setPreferredSize(new java.awt.Dimension(9, 50));

        txtConcatedResult.setEditable(false);
        jScrollPane2.setViewportView(txtConcatedResult);

        jPanel4.add(jScrollPane2, java.awt.BorderLayout.SOUTH);

        jSplitPane2.setRightComponent(jPanel4);

        jSplitPane1.setLeftComponent(jSplitPane2);

        bottomPanel.setLayout(new java.awt.BorderLayout());

        txtInput.setColumns(20);
        txtInput.setRows(5);
        txtInput.setText("a.b.c");
        jScrollPane1.setViewportView(txtInput);

        bottomPanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel1.setPreferredSize(new java.awt.Dimension(100, 100));
        jPanel1.setLayout(new java.awt.BorderLayout());

        btnMatch.setText("Match");
        btnMatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMatchActionPerformed(evt);
            }
        });
        jPanel1.add(btnMatch, java.awt.BorderLayout.CENTER);

        bottomPanel.add(jPanel1, java.awt.BorderLayout.EAST);

        jSplitPane1.setRightComponent(bottomPanel);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        bindingGroup.bind();

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-656)/2, (screenSize.height-518)/2, 656, 518);
    }// </editor-fold>//GEN-END:initComponents

    private void btnLoadXmlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadXmlActionPerformed

        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter( new FileFilter() {
            @Override
            public boolean accept( File f ) {
                if (f.isDirectory())
                    return true;
                return f.getPath().toLowerCase().endsWith( ".xml" ) && f.canRead() && f.canWrite();
            }

            @Override
            public String getDescription() {
                return "XML file";
            }
        } );
        chooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
        if (currentDir!=null)
            chooser.setCurrentDirectory( currentDir );

        if ( chooser.showOpenDialog( this ) == JFileChooser.APPROVE_OPTION ) {
            currentDir = chooser.getCurrentDirectory();
            
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream( chooser.getSelectedFile() );
                String contents = Common.streamToString( fileInputStream );
                txtXml.setSyntaxEditingStyle( "text/xml" );
                txtXml.setText( contents );
                txtXml.setEditable( true );
            } catch ( Exception ex ) {
                txtXml.setSyntaxEditingStyle( "text/text" );
                txtXml.setText( Common.traceToString( ex ) );
                txtXml.setEditable( false );
            } finally {
                if ( fileInputStream != null ) {
                    try {
                        fileInputStream.close();
                    } catch ( Exception ex ) {
                    }
                }
            }

//            setXmlTextIsDirty( true );
        }

    }//GEN-LAST:event_btnLoadXmlActionPerformed

    private void btnSaveXmlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveXmlActionPerformed

        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter( new FileFilter() {
            @Override
            public boolean accept( File f ) {
                if (f.isDirectory())
                    return true;
                return f.getPath().toLowerCase().endsWith( ".xml" ) && f.canRead() && f.canWrite();
            }

            @Override
            public String getDescription() {
                return "XML file";
            }
        } );
        chooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
        if (currentDir!=null)
            chooser.setCurrentDirectory( currentDir );

        if ( chooser.showSaveDialog( this ) == JFileChooser.APPROVE_OPTION ) {
            currentDir = chooser.getCurrentDirectory();
            PrintWriter printWriter = null;
            try {
                printWriter = new PrintWriter( chooser.getSelectedFile() );
                printWriter.write( txtXml.getText() );
            } catch ( Exception ex ) {
                txtXml.setSyntaxEditingStyle( "text/text" );
                txtXml.setText( Common.traceToString( ex ) );
                txtXml.setEditable( false );
            } finally {
                if ( printWriter != null ) {
                    try {
                        printWriter.close();
                    } catch ( Exception ex ) {
                    }
                }
            }
        }


    }//GEN-LAST:event_btnSaveXmlActionPerformed

    private void btnClearXmlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearXmlActionPerformed

        txtXml.setSyntaxEditingStyle( "text/xml" );
        txtXml.setText( clearString );
        txtXml.setEditable( true );

//        setXmlTextIsDirty( true );

    }//GEN-LAST:event_btnClearXmlActionPerformed

    private void btnMatchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMatchActionPerformed

        if ( !txtXml.isEditable() ) {
            return;
        }

        ensureLatestClauseBase();

        results = clauseBase.match( txtInput.getText() );

        if (results.isEmpty())
            currentResult = -1;
        else
            currentResult = 0;

        refreshResultPane();
        displayResults();

    }//GEN-LAST:event_btnMatchActionPerformed

    private void btnPrevResultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrevResultActionPerformed

        --currentResult;
        refreshResultPane();
        displayResults();

    }//GEN-LAST:event_btnPrevResultActionPerformed

    private void btnNextResultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextResultActionPerformed

        ++currentResult;
        refreshResultPane();
        displayResults();

    }//GEN-LAST:event_btnNextResultActionPerformed

    private void xmlContainerPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_xmlContainerPropertyChange

        if (evt.getPropertyName().equals("opaque")) {
            xmlContainer.repaint();
        }

    }//GEN-LAST:event_xmlContainerPropertyChange

    /**
     * @param args the command line arguments
     */
    public static void main( String args[] ) {
        java.awt.EventQueue.invokeLater( new Runnable()
        {

            public void run() {
                new GuiTest().setVisible( true );
            }
        } );
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClearXml;
    private javax.swing.JButton btnLoadXml;
    private javax.swing.JButton btnMatch;
    private javax.swing.JButton btnNextResult;
    private javax.swing.JButton btnPrevResult;
    private javax.swing.JButton btnSaveXml;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JLabel lblResultIndex;
    private javax.swing.JTextPane txtConcatedResult;
    private javax.swing.JTextArea txtInput;
    private javax.swing.JEditorPane txtOutput;
    private org.fife.ui.rsyntaxtextarea.RSyntaxTextArea txtXml;
    private javax.swing.JPanel xmlContainer;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

}
