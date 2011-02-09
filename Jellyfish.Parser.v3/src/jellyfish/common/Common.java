package jellyfish.common;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    public static String resourceLocation(Package location, String resourceName) {
        String folderName = location.getName().replaceAll("\\.", "/");
        return folderName + "/" + resourceName;
    }

    public static Properties loadPropertiesFromResource(InputStream inputStream) throws Exception
    {
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

    public static String traceToString(Exception e) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter( stringWriter );

        e.printStackTrace( printWriter );

        printWriter.flush();

        return stringWriter.toString();
    }

    public static File getTempDir()
    {
        String property = "java.io.tmpdir";
        return new File( System.getProperty( property ) );
    }

    public static File getAppDir()
    {
        String commonName = Common.class.getCanonicalName();
        String commonClassName = commonName.replaceAll( "\\.", "/" ) + ".class";
        ClassLoader sysClassLoader = ClassLoader.getSystemClassLoader();
        URL classURL = sysClassLoader.getResource( commonClassName );
        if (classURL!=null && classURL.getProtocol().equals( "file" )) {
            try {
                File f = new File( classURL.toURI() );
                int i = commonName.split( "\\." ).length;
                while (i>0) {
                    f = f.getParentFile();
                    --i;
                }
                return f;
            } catch ( URISyntaxException ex ) {
                return null;
            }
        } else {
            return null;
        }
    }

    public static void main( String[] args ) {
        System.out.println( getAppDir() );
    }
}
