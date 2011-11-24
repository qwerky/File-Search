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

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class StatusBar extends JPanel {
  
  private static final long serialVersionUID = -7122036552595424849L;
  
  private JLabel label;
  
  public StatusBar() {
    super();
    setLayout(new BorderLayout());
    this.label = new JLabel();
    add(label, BorderLayout.WEST);
  }
  
  public void setText(String text) {
    label.setText(text);
  }
}
