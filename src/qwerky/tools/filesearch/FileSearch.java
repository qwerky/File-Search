/**
 *  Copyright (C) 2011  Neil Taylor (https://github.com/qwerky/)
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
package qwerky.tools.filesearch;

import java.io.File;

public class FileSearch {

  public static void main (String[] args) throws Exception {
    if (args.length == 1) {
      File file = new File(args[0]);
      if (file.isDirectory()) {
        openSearch(file);
      } else {
        openSearch(file.getParentFile());
      }
    } else {
      openSearch(new File("c:\\"));
    }
  }

  private static void openSearch(File searchDir) {
    FileSearchGUI gui = new FileSearchGUI();
    gui.setSearchDir(searchDir);
    gui.show();
  }
}
