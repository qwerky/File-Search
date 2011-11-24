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

import java.awt.Component;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;

public class SearchResponseListCellRenderer implements ListCellRenderer {

  private static final long serialVersionUID = -7873896660122694972L;

  private JTextField searchTermField;
  private JCheckBox searchTermIsRegex;
  
  public SearchResponseListCellRenderer(JTextField searchTermField, JCheckBox searchTermIsRegex) {
    this.searchTermField = searchTermField;
    this.searchTermIsRegex = searchTermIsRegex;
  }

  @Override
  public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
    
    SearchResponse response = (SearchResponse)value;
    String term = searchTermField.getText();
    
    JLabel label = new JLabel(response.toString());
    label.setEnabled(list.isEnabled());
    label.setFont(list.getFont());
    
    if (response.getHits().size() > 0) {
      StringBuilder buffer = new StringBuilder();
      buffer.append("<html>");
      
      Pattern pattern = null;
      if (searchTermIsRegex.isSelected()) {
        pattern = Pattern.compile(searchTermField.getText());
      }
      for (SearchHit hit : response.getHits()) {
        buffer.append("Line " + hit.getLine() + ": ");
        
        if (searchTermIsRegex.isSelected()) {
          Matcher matcher = pattern.matcher(hit.getHighlight());
          StringBuffer sb = new StringBuffer();
          while (matcher.find()) {
            matcher.appendReplacement(sb, "<b>" + matcher.group().replace(">", "&gt;").replace("<", "&lt;") + "</b>");
          }
          buffer.append(sb);
          StringBuffer tail = new StringBuffer();
          matcher.appendTail(tail);
          buffer.append(tail.toString().replace(">", "&gt;").replace("<", "&lt;"));
        } else {
          //Escape any < or > in the highlight
          String escaped = hit.getHighlight().replace("<", "&lt;");
          escaped = escaped.replace(">", "&gt;");
          buffer.append(escaped.replace(term, "<b>" + term + "</b>"));
        }
        buffer.append("<br/>");
      }
      buffer.append("</html>");
      label.setToolTipText(buffer.toString());
    }
  
    return label;
  }

}
