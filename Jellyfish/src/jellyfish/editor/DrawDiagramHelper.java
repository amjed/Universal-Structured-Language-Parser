/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Line2D;

/**
 *
 * @author Xevia
 */
class DrawDiagramHelper {

    private static final int BORDER_GAP = 5;
    private static final int GRID_SIZE = 10;
    private static final int LINE_CLICK_ALLOWANCE = 2;

    private PanelDiagram diagram;

    public DrawDiagramHelper(PanelDiagram diagram) {
        this.diagram = diagram;
    }

    public Point calcCenterGridAlignedLoc(Point pt, Dimension dm)
    {
        int halfWidth = dm.width/2;
        int halfHeight = dm.height/2;
        int x = ((pt.x+halfWidth) % GRID_SIZE);
        int x2 = 0;
        if (x*2>=GRID_SIZE) x2 = GRID_SIZE; else x2 = 0;
        int y = ((pt.y+halfHeight) % GRID_SIZE);
        int y2 = 0;
        if (y*2>=GRID_SIZE) y2 = GRID_SIZE; else y2 = 0;
        return new Point(pt.x + x2 - x, pt.y + y2 - y);
    }

    public Point repelBorders(Point pt, Dimension dm)
    {
        int x = pt.x;
        int y = pt.y;

        if (pt.x<BORDER_GAP) {
            x = BORDER_GAP;
        }
        if (pt.y<BORDER_GAP) {
            y = BORDER_GAP;
        }
        if (pt.x+dm.width>diagram.getWidth()-BORDER_GAP) {
            x = diagram.getWidth()-dm.width-BORDER_GAP;
        }
        if (pt.y+dm.height>diagram.getHeight()-BORDER_GAP) {
            y = diagram.getHeight()-dm.height-BORDER_GAP;
        }
        
        return new Point(x, y);
    }

    public boolean pointInLine(Point src, Point dst, Point pt)
    {
        if (!(Math.min(src.x, dst.x)<=pt.x+LINE_CLICK_ALLOWANCE
                && pt.x-LINE_CLICK_ALLOWANCE<=Math.max(src.x, dst.x)
                && Math.min(src.y, dst.y)<=pt.y+LINE_CLICK_ALLOWANCE
                && pt.y-LINE_CLICK_ALLOWANCE<=Math.max(src.y, dst.y))) {
            return false;
        }

        java.awt.geom.Line2D ld = new java.awt.geom.Line2D.Double(src, dst);
        Rectangle rectangle = new Rectangle(
                pt.x-LINE_CLICK_ALLOWANCE,
                pt.y-LINE_CLICK_ALLOWANCE,
                LINE_CLICK_ALLOWANCE+LINE_CLICK_ALLOWANCE,
                LINE_CLICK_ALLOWANCE+LINE_CLICK_ALLOWANCE);

        boolean intersects = rectangle.intersectsLine(ld);

        return intersects;
    }

    public Point calcCenter(Component component)
    {
        Point p1 = component.getLocation();
        p1.translate(component.getWidth()/2, component.getHeight()/2);
        return p1;
    }

    final double[] triangleX = new double[]{-5,5,-5};
    final double[] triangleY = new double[]{ 5,0,-5};

    public void paintCenterTriangle(Graphics g, Point lineSrc, Point lineDst)
    {
        double lineCenterX = (lineSrc.x+lineDst.x)/2.0;
        double lineCenterY = (lineSrc.y+lineDst.y)/2.0;

        double lineAngle = Math.atan2((lineDst.y-lineSrc.y), lineDst.x-lineSrc.x);
        Polygon polygon = new Polygon();
        for (int i=0; i<3; ++i) {
            int x = (int)Math.round(lineCenterX+triangleX[i]*Math.cos(lineAngle)-triangleY[i]*Math.sin(lineAngle));
            int y = (int)Math.round(lineCenterY+triangleX[i]*Math.sin(lineAngle)+triangleY[i]*Math.cos(lineAngle));
            polygon.addPoint(x, y);
        }
        
        g.fillPolygon(polygon);
    }

    public Point calcIntersectionPoint(
            Point l1_p1, Point l1_p2, Point l2_p1, Point l2_p2) {
        double px = l1_p1.x,
                py = l1_p1.y,
                rx = l1_p2.x - px,
                ry = l1_p2.y - py;
        double qx = l2_p1.x,
                qy = l2_p1.y,
                sx = l2_p2.x - qx,
                sy = l2_p2.y - qy;

        double det = sx * ry - sy * rx;
        if (det == 0) {
            return null;
        } else {
            double z = (sx * (qy - py) + sy * (px - qx)) / det;
            if (z == 0 || z == 1) {
                return null;  // intersection at end point!
            }
            return new Point(
                    (int) (px + z * rx), (int) (py + z * ry));
        }
    }

    public Point calcExitPoint(Rectangle rc, Point innerPt, Point outerPt)
    {
        int rcWid = (int)rc.getWidth();
        int rcHei = (int)rc.getHeight();

        Point[] pts = new Point[] {
            rc.getLocation(),
            rc.getLocation(),
            rc.getLocation(),
            rc.getLocation(),
        };

        pts[1].translate(rcWid, 0);
        pts[2].translate(rcWid, rcHei);
        pts[3].translate(0, rcHei);

        Point prevPt = pts[0];
        for (int i=1; i<=pts.length; ++i)
        {
            Point pt = pts[i%pts.length];

            if (Line2D.linesIntersect(prevPt.x, prevPt.y, pt.x, pt.y, innerPt.x, innerPt.y, outerPt.x, outerPt.y))
            {
                Point intersect = calcIntersectionPoint(prevPt, pt, innerPt, outerPt);
                return intersect;
            }

            prevPt = pt;
        }

        return null;
    }

    

}
