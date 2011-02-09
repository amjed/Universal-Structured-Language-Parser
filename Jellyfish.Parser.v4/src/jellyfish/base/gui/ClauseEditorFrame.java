
package jellyfish.base.gui;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import jellyfish.base.ClauseBase;
import jellyfish.base.DebugClauses;
import jellyfish.base.JellyfishBase;
import jellyfish.common.Common;
import jellyfish.common.Pair;
import jellyfish.matcher.MatchResult;
import jellyfish.triplestore.model.Language;
import jellyfish.triplestore.xml.XmlTripleStore;

/**
 *
 * @author Umran
 */
public class ClauseEditorFrame
		extends javax.swing.JPanel
{
	private static final String clearString =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<root>\n" +
			"\t<!--\n" +
			"\t\tTODO: ADD CLAUSES DESCRIBING WHAT THE USER'S INPUT E.G.:\n" +
			"\t<clause name=\"test\" primary=\"true\">\n" +
			"\t\tshow me the <o>big</o> money\n" +
			"\t</clause>\n" +
			"\t-->\n" +
			"</root>\n";

	private JFrame rootFrame;
	private JellyfishBase jellyfishBase;
	private String language;
	private ClauseBase clauseBase;
	private List<MatchResult> results = Collections.EMPTY_LIST;
	private int currentResult = -1;
	private String xmlString = "";
	private boolean xmlTextIsDirty = false;

	/** Creates new form ClauseEditorFrame */
	public ClauseEditorFrame( JFrame rootFrame, JellyfishBase jellyfishBase, String language ) {
		this.rootFrame = rootFrame;
		this.jellyfishBase = jellyfishBase;
		this.language = language;
		initComponents();
		String xml = readClauseFile();
		System.out.println( "setting initial xml" );
		txtXml.setText( xml );
		System.out.println( "done with construction" );
		setXmlTextIsDirty( false );
	}

	private String readClauseFile() {
		Language l = this.jellyfishBase.getTripleStore().getLanguage( language );
		InputStream inputStream = null;
		try {
			inputStream = new BufferedInputStream( new FileInputStream( l.getClausesFile() ) );
			return Common.streamToString( inputStream );
		} catch ( Exception ex ) {
			throw new RuntimeException( "Exception while reading from clause file: " + l.getClausesFile(),
										ex );
		} finally {
			if (inputStream!=null) {
				try {
					inputStream.close();
				} catch ( IOException ex ) {
				}
			}
		}
	}

	private void writeToClauseFile(String text) {
		Language l = this.jellyfishBase.getTripleStore().getLanguage( language );
		PrintWriter printWriter = null;
		try {
			printWriter = new PrintWriter( l.getClausesFile() );
			printWriter.write( text );
		} catch ( Exception ex ) {
			throw new RuntimeException( "Exception while writing to clause file: " + l.getClausesFile(),
										ex );
		} finally {
			if ( printWriter != null ) {
				try {
					printWriter.close();
				} catch ( Exception ex ) {
				}
			}
		}
	}

	public boolean isXmlTextIsDirty() {
		return xmlTextIsDirty;
	}

	final public void setXmlTextIsDirty( boolean xmlTextIsDirty ) {
		if ( this.xmlTextIsDirty != xmlTextIsDirty ) {
			System.out.println( "xml is dirty = "+xmlTextIsDirty );
			this.firePropertyChange(
					"xmlTextIsDirty",
					this.xmlTextIsDirty,
					this.xmlTextIsDirty = xmlTextIsDirty );
		}
	}

	public String getXmlString() {
		return xmlString;
	}

	final public void setXmlString( String xmlString ) {
		if ( !this.xmlString.equals( xmlString ) ) {
			this.firePropertyChange(
					"xmlString",
					this.xmlString,
					this.xmlString = xmlString );
			this.setXmlTextIsDirty( true );
		}
	}

	private class CreateClauseBase
			implements Runnable
	{

		DlgWait dlgWait;

		public CreateClauseBase( DlgWait dlgWait ) {
			this.dlgWait = dlgWait;
		}

		public void run() {

			System.out.println( "RELOADING" );

			try {
				jellyfishBase.reloadClauseBase( language );

				clauseBase = jellyfishBase.getClauseBase( language );

			} catch ( Exception ex ) {
				String trace = Common.traceToString( ex );
				dlgWait.setVisible( false );
				JOptionPane.showMessageDialog( rootFrame, trace, "Error While Loading", JOptionPane.ERROR_MESSAGE );
				ex.printStackTrace( System.out );
			} finally {
				dlgWait.setVisible( false );
			}

		}
	}

	private void ensureLatestClauseBase( ) {

		DlgWait dlgWait = new DlgWait( rootFrame );

		Thread thread = new Thread( new CreateClauseBase( dlgWait ) );
		thread.start();
		dlgWait.setVisible( true );
	}

	private void refreshResultPane() {
		if ( currentResult >= 0 && currentResult < results.size() ) {
			lblResultIndex.setText( (currentResult + 1) + "/" + results.size() );
		} else {
			lblResultIndex.setText( "<Empty>" );
		}

		btnPrevResult.setEnabled( currentResult > 0 );
		btnNextResult.setEnabled( currentResult < results.size() - 1 );
	}

	private void displayResults() {
		if ( currentResult >= 0 && currentResult < results.size() ) {
			MatchResult matchResult = results.get( currentResult );
			Pair<String, String> p = DebugClauses.resultTreeToString( clauseBase, matchResult );
			txtOutput.setText( p.getFirst() );
			txtConcatedResult.setText( p.getSecond() );
		} else {
			txtOutput.setText( "" );
			txtConcatedResult.setText( "" );
		}

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
        btnReloadXml = new javax.swing.JButton();
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
        txtInput = new javax.swing.JTextField();
        javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
        btnMatch = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setResizeWeight(1.0);

        jSplitPane2.setResizeWeight(1.0);

        jPanel2.setLayout(new java.awt.BorderLayout());

        btnReloadXml.setText("Reload");
        btnReloadXml.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReloadXmlActionPerformed(evt);
            }
        });
        jPanel3.add(btnReloadXml);

        btnSaveXml.setText("Save & Compile");
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

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${xmlTextIsDirty}"), xmlContainer, org.jdesktop.beansbinding.BeanProperty.create("opaque"));
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

        jPanel5.setMinimumSize(new java.awt.Dimension(300, 36));
        jPanel5.setPreferredSize(new java.awt.Dimension(300, 36));

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

        txtInput.setText("*");
        bottomPanel.add(txtInput, java.awt.BorderLayout.CENTER);

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));

        btnMatch.setText("Match");
        btnMatch.setPreferredSize(new java.awt.Dimension(70, 30));
        btnMatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMatchActionPerformed(evt);
            }
        });
        jPanel1.add(btnMatch);

        bottomPanel.add(jPanel1, java.awt.BorderLayout.EAST);

        jSplitPane1.setRightComponent(bottomPanel);

        add(jSplitPane1, java.awt.BorderLayout.CENTER);

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveXmlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveXmlActionPerformed

		writeToClauseFile( txtXml.getText() );
		ensureLatestClauseBase( );
		setXmlTextIsDirty( false );

    }//GEN-LAST:event_btnSaveXmlActionPerformed

    private void btnClearXmlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearXmlActionPerformed

		txtXml.setSyntaxEditingStyle( "text/xml" );
		txtXml.setText( clearString );
		txtXml.setEditable( true );

    }//GEN-LAST:event_btnClearXmlActionPerformed

    private void xmlContainerPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_xmlContainerPropertyChange

		if ( evt.getPropertyName().equals( "opaque" ) ) {
			xmlContainer.repaint();
		}

    }//GEN-LAST:event_xmlContainerPropertyChange

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

    private void btnMatchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMatchActionPerformed

		if ( !txtXml.isEditable() ) {
			return;
		}

		if (clauseBase==null || xmlTextIsDirty) {
			writeToClauseFile( txtXml.getText() );
			setXmlTextIsDirty( false );
			ensureLatestClauseBase();
		}

		results = clauseBase.match( txtInput.getText() );

		if ( results.isEmpty() ) {
			currentResult = -1;
		} else {
			currentResult = 0;
		}

		refreshResultPane();
		displayResults();

    }//GEN-LAST:event_btnMatchActionPerformed

    private void btnReloadXmlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReloadXmlActionPerformed

		setXmlString( readClauseFile() );
		ensureLatestClauseBase( );

    }//GEN-LAST:event_btnReloadXmlActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClearXml;
    private javax.swing.JButton btnMatch;
    private javax.swing.JButton btnNextResult;
    private javax.swing.JButton btnPrevResult;
    private javax.swing.JButton btnReloadXml;
    private javax.swing.JButton btnSaveXml;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JLabel lblResultIndex;
    private javax.swing.JTextPane txtConcatedResult;
    private javax.swing.JTextField txtInput;
    private javax.swing.JEditorPane txtOutput;
    private org.fife.ui.rsyntaxtextarea.RSyntaxTextArea txtXml;
    private javax.swing.JPanel xmlContainer;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

	public static void main( String args[] ) {

		try {
			System.out.println( "Loading..." );
			System.out.println( "===========" );
			XmlTripleStore tripleStore = new XmlTripleStore( new File( "semnet.xml" ) );
			final JellyfishBase jellyfishBase = new JellyfishBase( tripleStore );

			java.awt.EventQueue.invokeLater( new Runnable()
			{

				public void run() {
					JFrame frame = new JFrame();
					frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
					ClauseEditorFrame editorFrame = new ClauseEditorFrame( frame, jellyfishBase,
																		   "english" );
					frame.setContentPane( editorFrame );
					frame.pack();
					frame.setVisible( true );
				}
			} );
		} catch ( Exception ex ) {
			ex.printStackTrace( System.out );
		}

	}
}
