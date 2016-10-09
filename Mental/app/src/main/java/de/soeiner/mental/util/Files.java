package de.soeiner.mental.util;

import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by sven on 10.10.16.
 */
public class Files {
    public static String readAsset(AssetManager assetManager, String fileName) {
        String content = "";
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
            String line;
            while ((line = r.readLine()) != null) {
                content += line + "\r\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }
}
