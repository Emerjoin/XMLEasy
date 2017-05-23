package org.emerjoin.xmleasy;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Mário Júnior
 */
public class Paths {

    public static URL url(File file) throws MalformedURLException {

        return file.toURI().toURL();

    }

    public static URL url(String path) throws MalformedURLException{

        return url(new File(path));

    }

    public static URL[] urls(String[] paths) throws MalformedURLException{

        URL[] array = new URL[paths.length];
        for(int i=0;i<paths.length;i++)
            array[i] = url(paths[i]);

        return array;

    }
}
