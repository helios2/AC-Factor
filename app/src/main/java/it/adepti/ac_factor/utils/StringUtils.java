package it.adepti.ac_factor.utils;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class StringUtils {

    private final String TAG = "StringUtils";

    public static String dateToString(Calendar toConvertDate){
        SimpleDateFormat dataFormat = new SimpleDateFormat("ddMMyy");
        String dataDirectoryName = dataFormat.format(toConvertDate.getTime());
        dataDirectoryName = dataDirectoryName.concat("/");
        return dataDirectoryName;
    }

    public static String readFromFile(String fileName, File path) {
        BufferedReader br = null;
        String response = null;
        try {
            StringBuffer output = new StringBuffer();
            br = new BufferedReader(new FileReader(path.toString() + "/" + fileName + ".txt"));
            String line = "";
            while ((line = br.readLine()) != null) {
                output.append(line +"\n");
            }
            response = output.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return response;
    }


}
