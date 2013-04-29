/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/ Copyright (C) 2013
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

import Tools.ProgressBar;
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

    ProgressBar progressBar;

    public FileInputStreamProgress(FileDescriptor fdObj) {
        super(fdObj);
    }

    public FileInputStreamProgress(File file) throws FileNotFoundException {
        super(file);
        progressBar = new ProgressBar(file.length());
    }

    public FileInputStreamProgress(String name) throws FileNotFoundException {
        this(new File(name));
    }

    @Override
    public int read() throws IOException {
        progressBar.progress(1);
        return super.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        int readed = super.read(b);
        progressBar.progress(readed);
        return readed;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int readed = super.read(b, off, len);
        progressBar.progress(readed);
        return readed;
    }
}
