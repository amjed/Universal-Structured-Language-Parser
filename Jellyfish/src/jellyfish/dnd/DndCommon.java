/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DragSource;

/**
 *
 * @author Xevia
 */
public class DndCommon {

    private static boolean initialized = false;
    private static DragSource dragSource;
    private static DataFlavor jvmLocalDataFlavor;


    private static synchronized void initialize() {
        if (initialized) return;

        try {
            dragSource = new DragSource();
            jvmLocalDataFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType);
            initialized = true;
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.exit(0);
        }
    }

    public static DragSource getDragSource() {
        if (!initialized) initialize();
        return dragSource;
    }

    public static DataFlavor getJvmLocalDataFlavor() {
        if (!initialized) initialize();
        return jvmLocalDataFlavor;
    }

}
