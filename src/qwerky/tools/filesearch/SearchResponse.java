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
import java.util.ArrayList;
import java.util.List;

public class SearchResponse {
  
  private File file;
  private List<SearchHit> hits;
  
  public SearchResponse() {
    this.hits = new ArrayList<SearchHit>();
  }
  
  public File getFile() {
    return file;
  }
  
  public void setFile(File file) {
    this.file = file;
  }
  
  public void addHit(SearchHit hit) {
    this.hits.add(hit);
  }
  
  public List<SearchHit> getHits() {
    return hits;
  }
  
  public void setHits(List<SearchHit> hits) {
    this.hits = hits;
  }
  
  public String toString() {
    if (hits.size() > 0) {
      return file.getAbsolutePath() + "(" + hits.size() + ")";
    } else {
      return file.getAbsolutePath();
    }
  }
}
