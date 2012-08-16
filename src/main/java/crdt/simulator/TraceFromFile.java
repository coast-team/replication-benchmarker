/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/ Copyright (C) 2012
 * LORIA / Inria / SCORE Team
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package crdt.simulator;

import java.io.*;
import java.util.Enumeration;

class FileInputStreamProgress extends FileInputStream {

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

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class TraceFromFile implements Trace {

    ObjectInputStream input;

    public TraceFromFile(String str) throws IOException {
        this(new ObjectInputStream(new FileInputStream(str)));
    }

    public TraceFromFile(File file) throws IOException {
        this(new ObjectInputStream(new FileInputStream(file)));
    }

    public TraceFromFile(File file, boolean progress) throws IOException {
        if (progress) {
            this.input = new ObjectInputStream(new FileInputStreamProgress(file));
        } else {
            this.input = new ObjectInputStream(new FileInputStream(file));
        }
    }

    public TraceFromFile(String file, boolean progress) throws IOException {
        if (progress) {
            this.input = new ObjectInputStream(new FileInputStreamProgress(file));
        } else {
            this.input = new ObjectInputStream(new FileInputStream(file));
        }
    }

    public TraceFromFile(ObjectInputStream input) {
        this.input = input;
    }

    @Override
    public Enumeration<TraceOperation> enumeration() {
        return new Enumeration<TraceOperation>() {

            TraceOperation nextElement;

            @Override
            public boolean hasMoreElements() {
                nextElement = readOperation();
                return nextElement != null;
            }

            @Override
            public TraceOperation nextElement() {

                return nextElement;

            }

            private TraceOperation readOperation() {
                try {
                    Object obj = input.readObject();
                    if (obj instanceof TraceOperation) {
                        return (TraceOperation) obj;
                    } else {
                        throw new Exception("File is not a trace");
                    }
                } catch (EOFException ex1) {
                    return null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return null;
                }
            }
        };
    }
}
