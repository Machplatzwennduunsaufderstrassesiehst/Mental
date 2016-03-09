package de.soeiner.mental;

/**
 * Created by sven on 25.02.16.
 */
public class Logger {

    public static boolean DEBUG = true;

    public static void log(Object o) {
        if (DEBUG) {
            System.out.println(o.toString());
        }
    }
}
