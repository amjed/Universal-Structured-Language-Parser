/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DragPanel.java
 *
 * Created on Nov 8, 2010, 2:55:55 AM
 */

package jellyfish.editor;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MediaTracker;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JToolTip;

/**
 *
 * @author Xevia
 */
public class DragPanel extends javax.swing.JPanel {

    public static final String TEXTURE_LOCATION = DragPanel.class.getPackage().getName().replaceAll("\\.", "/");
    private static final String TEXTURE_FILE_NAME = "drag_texture.gif";
    private static BufferedImage TEXTURE = null;

    static {
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        String resName = TEXTURE_LOCATION+"/"+TEXTURE_FILE_NAME;
//        System.out.println("resName = " + resName);
        URL url = cl.getResource(resName);
//        System.out.println("url="+url);
        ImageIcon icon = new ImageIcon(url);
        System.out.println("icon.width="+icon.getIconWidth()+", icon.height="+icon.getIconHeight()+", status="+icon.getImageLoadStatus());
        
        BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = (Graphics2D)image.getGraphics();
        g.drawImage(icon.getImage(), 0, 0, null);

        TEXTURE = image;
    }

    /** Creates new form DragPanel */
    public DragPanel() {
        initComponents();
    }
    

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
    }// </editor-fold>//GEN-END:initComponents

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        Paint pt = g2d.getPaint();
        g2d.setPaint(new TexturePaint(TEXTURE, new Rectangle(0,0,TEXTURE.getWidth(),TEXTURE.getHeight())));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.setPaint(pt);
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables


}
