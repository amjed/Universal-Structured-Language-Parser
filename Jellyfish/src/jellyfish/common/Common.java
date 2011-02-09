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

    public static Properties loadPropertiesFromResource(Package location, String resourceName) throws Exception
    {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        String folderName = location.getName().replaceAll("\\.", "/");
        InputStream inputStream = classLoader.getResourceAsStream(folderName+"/connection.properties");
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
            return properties;
        } finally {
            inputStream.close();
        }
    }

    public static String streamToString(InputStream input) throws Exception {
        BufferedReader bufferedReader = null;
        StringBuilder textFromFile = new StringBuilder();

        bufferedReader = new BufferedReader(new InputStreamReader(input));

        // Read through the entire file
        boolean first = true;
        String currentLineFromFile = bufferedReader.readLine();
        while (currentLineFromFile != null) {
            // Add a carriage return (line break) to preserve the file formatting.
            if (first) {
                first = false;
            } else {
                textFromFile.append("\n");
            }

            textFromFile.append(currentLineFromFile);
            currentLineFromFile = bufferedReader.readLine();
        }

        return textFromFile.toString();
    }
    
}
