/*
 * Copyright 2000-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ovpp;

import com.intellij.openapi.actionSystem.Anchor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.util.ui.JBDimension;
import com.intellij.xdebugger.frame.XValueChildrenList;
import com.jetbrains.python.debugger.PyDebugValue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created by Sehs on 2017-05-31.
 */
public class DebugObjectViewer extends DialogWrapper implements ActionListener {
  private Project myProject;
  private JPanel myPanel;
  private JBLabel NAME;
  private JBLabel TYPE;
  private JBLabel objName;
  private JBLabel objType;
  private JBLabel VALUE;
  private JBLabel objValue;
  private JButton build;
  private JButton next;
  private JButton prev;
  private JBLabel SNAPSHOTNUM;
  private JBLabel snapshotNum;
  private JButton remove;
  private DebugCanvas debugCanvas;
  private JComponent myAnchor;

  public DebugManager dmanager;

  public DebugObjectViewer (DebugManager dmanager) {
    super(true);
    this.dmanager = dmanager;
    this.debugCanvas = new DebugCanvas(this);
    this.debugCanvas.setBackground(Color.WHITE);

    this.setTitle("Object Viewer");
    myPanel.add(debugCanvas, new GridConstraints(3, 1, 1, 2,
                                             GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                                             GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW,
                                             new JBDimension(debugCanvas.width, debugCanvas.height), new JBDimension(-1, -1), new JBDimension(-1, -1)));

    this.init();

    prev.addActionListener(this);
    next.addActionListener(this);
    build.addActionListener(this);
    remove.addActionListener(this);
    prev.setActionCommand("prev");
    next.setActionCommand("next");
    build.setActionCommand("build");
    remove.setActionCommand("remove");
  }

  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("NoAction")) {
      return;
    }
    else if (e.getActionCommand().equals("prev")) {
      dmanager.currentIndex--;
      if (dmanager.currentIndex < 0) {
        dmanager.currentIndex = 0;
      }
      snapshotNum.setText(Integer.toString((dmanager.currentIndex)));
      this.repaint();
    }
    else if (e.getActionCommand().equals("next")) {
      dmanager.currentIndex++;
      if (dmanager.currentIndex >= dmanager.snapshots.size()) {
        dmanager.currentIndex = dmanager.snapshots.size() - 1;
      }
      snapshotNum.setText(Integer.toString((dmanager.currentIndex)));
      this.repaint();
    }
    else if (e.getActionCommand().equals("build")) {
      dmanager.buildSnapshot();
      snapshotNum.setText(Integer.toString((dmanager.currentIndex)));
      this.repaint();
    }
    else if (e.getActionCommand().equals("remove")) {
      dmanager.removeSnapshot();
      snapshotNum.setText(Integer.toString((dmanager.currentIndex)));
      this.repaint();
    }
    else {
      /*
      try {
        int index = Integer.parseInt(e.getActionCommand());
        this.objIndex = index;
        showObject();
      }
      catch (NumberFormatException exc) {
        //
      }
      */
    }
  }

  public void showObject() {
    DebugSnapshot shot = dmanager.currentSnapshot();
    if (dmanager.snapshots.isEmpty()) {
      return;
    }
    if (shot.objects.size() == 0) {
      return;
    }

    int objIndex = shot.objIndex;
    PyDebugValue dvalue = (PyDebugValue)shot.objects.getValue(objIndex);
    if (objIndex < shot.namedRange) {
      setName(dvalue.getName());
    }
    else {
      setName("----");
    }
    setType(dvalue.getType());
    setValue(dvalue.getValue());

    myPanel.revalidate();
    myPanel.repaint();
  }

  public JComponent createCenterPanel() {
    return myPanel;
  }

  public void setName(String name) {
    objName.setText(name);
  }

  public void setType(String type) {
    objType.setText(type);
  }

  public void setValue(String value) {
    if (value.length() < 30) {
      objValue.setText(value);

    }
    else {
      objValue.setText(value.substring(0, 25)+"...");
    }
  }

  private void createUIComponents() {
  }
}
