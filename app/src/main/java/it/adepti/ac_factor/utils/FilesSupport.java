package it.adepti.ac_factor.utils;

;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FilesSupport {

    private final String TAG = "FilesSupport";

    public static String dateTodayToString(){
        Calendar todayDate = Calendar.getInstance();
        SimpleDateFormat dataFormat = new SimpleDateFormat("ddMMyy");
        String dataDirectoryName = dataFormat.format(todayDate.getTime());
        return dataDirectoryName;
    }

    public static String readTextFromFile(String filepath) {
        BufferedReader reader = null;
        String response = null;
        try {
            StringBuffer output = new StringBuffer();
            reader = new BufferedReader(new FileReader(filepath));
            String line = "";
            while ((line = reader.readLine()) != null) {
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
