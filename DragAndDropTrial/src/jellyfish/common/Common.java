package jellyfish.common;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.io.*;
import java.util.*;

/**
 *
 * @author X
 */
public class Common {
    
    public static <E> HashMap<E,Integer> createInvMap(List<E> list)
    {
        HashMap<E,Integer> invMap = new HashMap<E,Integer>(list.size());
        for (int i=0; i<list.size(); ++i)
            invMap.put(list.get(i), i);
        return invMap;
    }

    public static String getTabs(int tabs)
    {
        String s = "";
        for (int i=0; i<tabs; ++i)
            s += "\t";
        return s;
    }

    public static void copyFile(String sourcePath, String destinationPath) throws Exception
    {
        File source = new File(sourcePath);
        File destination = new File(destinationPath);

        FileInputStream is = new FileInputStream(source);
        FileOutputStream os = new FileOutputStream(destination);
        int v;

        while ((v=is.read())!=-1) {
            os.write(v);
        }

        is.close();
        os.close();
    }

    

}
