/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * HorizontalLine.java
 *
 * Created on Nov 7, 2010, 7:51:19 PM
 */

package jellyfish.editor;

import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author Xevia
 */
public class HorizontalLine extends javax.swing.JPanel {

    private int thickness = 1;

    /** Creates new form HorizontalLine */
    public HorizontalLine() {
        initComponents();
    }

    public int getThickness() {
        return thickness;
    }

    public void setThickness(int thickness) {
        this.thickness = Math.max(thickness,1);
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setMaximumSize(new java.awt.Dimension(32767, 3));
        setMinimumSize(new java.awt.Dimension(0, 3));
        setOpaque(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(this.getForeground());
        for (int i=0; i<thickness; ++i)
            g.drawLine(0, i, getWidth(), i);
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables



}
