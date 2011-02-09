/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package old;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;

public class Magnifier extends JComponent {
    private static final int SIDE = 64;
    private Point point;

    private BufferedImage image = new BufferedImage(SIDE, SIDE, BufferedImage.TYPE_INT_RGB);

    private MouseMotionListener l = new MouseMotionAdapter() {
        public void mouseMoved(MouseEvent e) {
            point = e.getPoint();
            repaint();
        }
    };

    public Magnifier() {
        addMouseMotionListener(l);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Window w = SwingUtilities.getWindowAncestor(this);
        if (point != null && w instanceof RootPaneContainer) {
            RootPaneContainer rpc = (RootPaneContainer) w;
            JLayeredPane lp = rpc.getLayeredPane();
            Graphics2D g2 = image.createGraphics();
            g2.scale(2, 2);
            g2.translate(-point.x-SIDE/4, -point.y+SIDE/4);
            lp.paint(g2);
            g2.dispose();
            g.drawImage(image, point.x, point.y-SIDE/2, null);
            g.setColor(Color.BLUE);
            g.drawRect(point.x, point.y-SIDE/2, SIDE-1, SIDE-1);
        }
    }

    //demo
    public static void main(String[] args) throws IOException {
        JFrame f = new JFrame("Magnifyer");
        final JComponent app = new Magnifier();
        JCheckBox onSwitch = new JCheckBox("Turn magnifier on");
        onSwitch.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent evt) {
                app.setVisible(((JCheckBox)evt.getSource()).isSelected());
            }
        });
        JDialog dlg = new JDialog(f, "Magnifier", false);
        dlg.getContentPane().add(onSwitch);
        dlg.pack();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setGlassPane(app);
        JComponent left = new JScrollPane(new JTree());
        JTextArea area = new JTextArea();
        area.read(new FileReader("src\\old\\Magnifier.java"), null);
        JComponent right = new JScrollPane(area);
        f.getContentPane().add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right));
        f.setSize(600,400);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
        dlg.setLocation(f.getX()+f.getWidth(), f.getY());
        dlg.setVisible(true);
    }
}
