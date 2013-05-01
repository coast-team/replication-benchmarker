/*
 *  Replication Benchmarker
 *  https://github.com/score-team/replication-benchmarker/
 *  Copyright (C) 2013 LORIA / Inria / SCORE Team
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package Tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/**
 *
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public class FileReaderProgress extends Reader{
    FileReader reader;
    ProgressBar progress;

    public FileReaderProgress(String filename) throws FileNotFoundException {
        this(new File(filename));
    }
    public FileReaderProgress(File file) throws FileNotFoundException {
        progress=new ProgressBar(file.length());
        reader=new FileReader(file);
    }
    
    
    @Override
    public int read(char[] chars, int i, int i1) throws IOException {
        int read=reader.read(chars, i, i1);
        progress.progress(read);
        return read;
    }

    @Override
    public void close() throws IOException {
        reader.read();
    }
    
}
