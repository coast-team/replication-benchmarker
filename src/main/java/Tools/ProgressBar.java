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

/**
 *
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public class ProgressBar {
    long max = -1;
    long pas = -1;
    long count = -1;
    long countTen = 0;

    public ProgressBar(long max) {
        this.max = max;
        pas = max / 100;
    }

    public void progress(long c) {
        if (c > 0) {
            count += c;
            if (count >= pas) {
                do {
                    count -= pas;
                    countTen++;
                    if (countTen >= 10) {
                        countTen = 0;
                        System.out.print("+");
                    } else {
                        System.out.print(".");
                    }
                } while (count >= pas);
                System.out.flush();
            }
        }
    }
}
