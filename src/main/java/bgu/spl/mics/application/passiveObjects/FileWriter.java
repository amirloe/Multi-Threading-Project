package bgu.spl.mics.application.passiveObjects;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

/**
 *A class That Responsible for the file writing, containts single static function.
 */

public class FileWriter {

    /**
     * A function That saves file to  a specific location
     * <p>
     * @param filename the file name
     * @param ObjectToWrite the
     * @param <T>
     */
    public static <T> void write(String filename, T ObjectToWrite){
        try {
            FileOutputStream fileOut = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(ObjectToWrite);
            out.close();
            fileOut.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

    }
}
