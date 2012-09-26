/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.simulator;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author score
 */
//public class FileInputStreamProgress {
   public class FileInputStreamProgress extends FileInputStream {

    long fileMax = -1;
    long pas = -1;
    long count = -1;
    long countTen = 0;

    public FileInputStreamProgress(FileDescriptor fdObj) {
        super(fdObj);
    }

    public FileInputStreamProgress(File file) throws FileNotFoundException {
        super(file);
        fileMax = file.length();
        pas = fileMax / 100;
    }

    public FileInputStreamProgress(String name) throws FileNotFoundException {
        this(new File(name));
    }

    @Override
    public int read() throws IOException {
        progress(1);
        return super.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        int readed = super.read(b);
        progress(readed);
        return readed;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int readed = super.read(b, off, len);
        progress(readed);
        return readed;
    }

    void progress(long c) {
        if (c > 0) {
            count += c;
            if (count >= pas) {
                do {
                    count -= pas;
                    countTen++;
                    if (countTen >= 10) {
                        countTen=0;
                        System.out.print("+");
                    } else {
                        System.out.print(".");
                    }

                    System.out.flush();
                } while (count >= pas);
            }
        }
    }
}
