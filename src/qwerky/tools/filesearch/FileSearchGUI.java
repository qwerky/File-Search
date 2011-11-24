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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.WindowConstants;

public class FileSearchGUI implements ActionListener, SearchListener {

  private JFrame frame;
  private SearchTask searchTask;
  
  //GUI Components
  private JTextField searchDirField;
  private JTextField fileNameField;
  private JTextField searchTermField;
  private JButton browseButton;
  private JButton startButton;
  private JButton stopButton;
  private JList resultsList;
  private StatusBar statusBar;
  
  private JCheckBox ignoreSvnEntries;
  private JCheckBox searchTermIsRegex;
  private JCheckBox recurseSubdirs;
  
  /**
   * Constructor
   */
  public FileSearchGUI() {
    frame = new JFrame("File Search");
    
    try {
      Point p = MouseInfo.getPointerInfo().getLocation();
      frame.setBounds((int)p.getX(), (int)p.getY(), 400, 545);
    } catch (Exception ex) {
      frame.setSize(400, 545);
    }
    
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    createWidgets();
    
    ToolTipManager ttm = ToolTipManager.sharedInstance();
    ttm.setInitialDelay(100);
    ttm.setDismissDelay(30000);
  }
  
  private void createWidgets() {
    Container c = frame.getContentPane();
    c.setLayout(new BorderLayout());
    
    JPanel topPanel = new JPanel();
    topPanel.setLayout(new FlowLayout());
    
    JPanel controlPanel = new JPanel();
    controlPanel.setBorder(BorderFactory.createTitledBorder("Search Controls"));
    controlPanel.setLayout(new FlowLayout());
    controlPanel.setPreferredSize(new Dimension(370,240));
    
    JLabel dirLabel = new JLabel("Directory to search");
    controlPanel.add(dirLabel);
    searchDirField = new JTextField(25);
    searchDirField.addActionListener(this);
    controlPanel.add(searchDirField);
    
    browseButton = new JButton("...");
    browseButton.addActionListener(this);
    controlPanel.add(browseButton);
    
    JLabel fileLabel = new JLabel("File name pattern");
    controlPanel.add(fileLabel);
    fileNameField = new JTextField(30);
    fileNameField.addActionListener(this);
    controlPanel.add(fileNameField);
    
    JLabel searchLabel = new JLabel("Search term pattern");
    controlPanel.add(searchLabel);
    searchTermField = new JTextField(30);
    searchTermField.addActionListener(this);
    controlPanel.add(searchTermField);
    
    ignoreSvnEntries = new JCheckBox("Skip svn entries");
    ignoreSvnEntries.setToolTipText("If this box is ticked the search will ignore Subversion files.");
    ignoreSvnEntries.setSelected(true);
    controlPanel.add(ignoreSvnEntries);
    
    searchTermIsRegex = new JCheckBox("Regex term");
    searchTermIsRegex.setToolTipText("Tick this box if your search term is a regular expression.");
    controlPanel.add(searchTermIsRegex);
    
    recurseSubdirs = new JCheckBox("Recurse subdirs");
    recurseSubdirs.setToolTipText("If this box is ticked the search will scan through any subdirectiories found.");
    recurseSubdirs.setSelected(true);
    controlPanel.add(recurseSubdirs);
    
    startButton = new JButton("Start");
    startButton.addActionListener(this);
    controlPanel.add(startButton);
    
    stopButton = new JButton("Stop");
    stopButton.addActionListener(this);
    stopButton.setEnabled(false);
    controlPanel.add(stopButton);
    
    topPanel.add(controlPanel);
    c.add(topPanel, BorderLayout.NORTH);
    
    resultsList = new JList(new DefaultListModel());
    resultsList.setCellRenderer(new SearchResponseListCellRenderer(searchTermField, searchTermIsRegex));
    resultsList.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent evt) {
        if (evt.getClickCount() > 1) {
          int index = resultsList.locationToIndex(evt.getPoint());
          SearchResponse result = (SearchResponse)resultsList.getModel().getElementAt(index);
          openExplorer(result);
        }
      }
    });
    JScrollPane scrollPane = new JScrollPane(resultsList);
    c.add(scrollPane, BorderLayout.CENTER);
    
    statusBar = new StatusBar();
    statusBar.setText("Ready");
    c.add(statusBar, BorderLayout.SOUTH);
  }
  
  public void show() {
    frame.setVisible(true);
  }

  public void setSearchDir(File searchDir) {
    searchDirField.setText(searchDir.getAbsolutePath());
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    if (evt.getSource() == startButton || evt.getSource() instanceof JTextField) {
      try {
        startSearch();
      } catch (Exception ex) {
        StringBuilder msg = new StringBuilder(ex.getMessage());
        Throwable t = ex.getCause();
        if (t != null) msg.append("\n" + t.getMessage());
        
        JOptionPane.showMessageDialog(frame, msg, "Ooops!", JOptionPane.ERROR_MESSAGE);
      }
    } else if (evt.getSource() == stopButton) {
      try {
        stopSearch();
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(frame, ex, "Ooops!", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
      }
    } else if (evt.getSource() == browseButton) {
      browse();
    }
  }

  private void stopSearch() throws Exception{
    if (searchTask == null || searchTask.isDone()) {
      throw new Exception("Search is not running.");
    }
    searchTask.cancel(true);
  }

  private void startSearch() throws Exception {
    if (searchTask != null && !searchTask.isDone()) {
      throw new Exception("Search already running please stop first.");
    } else {
      searchTask = new SearchTask(startButton, stopButton, statusBar);
      
      searchTask.setIgnoreSVNEntries(ignoreSvnEntries.isSelected());
      searchTask.setRecurseSubdirectories(recurseSubdirs.isSelected());
      searchTask.setRegexSearchTerm(searchTermIsRegex.isSelected());
      
      File dir = new File(searchDirField.getText());
      if (!dir.exists()) {
        throw new Exception("Can't find directory.");
      }
      if (!dir.isDirectory()) {
        dir = dir.getParentFile();
      }
      searchTask.setDir(dir);
      
      String fileNameText = fileNameField.getText();
      if (fileNameText != null &! fileNameText.isEmpty()) {
        try {
          Pattern fileNameTerm = Pattern.compile(fileNameText);
          searchTask.setFileName(fileNameTerm);
        } catch (PatternSyntaxException pse) {
          throw new Exception("Invalid file name term, use a regex.", pse);
        }
      }
      
      String searchTermText = searchTermField.getText();
      if (searchTermIsRegex.isSelected()) {
        try {
          searchTask.setSearchTermPattern(searchTermText);
        } catch (PatternSyntaxException pse) {
          throw new Exception("Invalid search term, not a valid regex.", pse);
        }
      } else {
        searchTask.setSearchTerm(searchTermText);
      }
      
      searchTask.setListener(this);
      startButton.setEnabled(false);
      stopButton.setEnabled(true);
      ((DefaultListModel)(resultsList.getModel())).removeAllElements();
      searchTask.execute();
    }
  }

  private void openExplorer(SearchResponse result) {
    try {
      Runtime.getRuntime().exec(new String[]{"explorer.exe", result.getFile().getAbsolutePath()});
    } catch (IOException ioe) {
      JOptionPane.showMessageDialog(frame, ioe, "Ooops!", JOptionPane.ERROR_MESSAGE);
    }
  }
  
  private void browse() {
    File dir = new File(searchDirField.getText());
    if (!dir.exists()) {
      dir = new File("c:\\");
    } else if (!dir.isDirectory()) {
      dir = dir.getParentFile();
    }
    
    JFileChooser fileChooser = new JFileChooser(dir);
    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    fileChooser.setMultiSelectionEnabled(false);
    if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
      searchDirField.setText(fileChooser.getSelectedFile().getAbsolutePath());
    }
  }

  @Override
  public void handleSearchResponse(final SearchResponse response) {
    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        DefaultListModel model = (DefaultListModel)resultsList.getModel();
        model.addElement(response);
      }
      
    });
  }
  
  
}
