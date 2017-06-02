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
  private JBScrollPane ObjectView;
  private JButton nextObjectButton;
  private JBLabel VALUE;
  private JBLabel objValue;
  private JButton childValue1;
  private JButton childValue2;
  private JComponent myAnchor;
  private DebugManager dmanager;

  ArrayList<JButton> childButtons;

  int objIndex;

  public DebugObjectViewer (DebugManager dmanager) {
    super(true);
    this.objIndex = 0;
    this.dmanager = dmanager;
    this.childButtons = new ArrayList<JButton>();

    this.setTitle("Object Viewer");

    this.init();
    showObject();

    nextObjectButton.setActionCommand("next");
    nextObjectButton.addActionListener(this);
  }

  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("NoAction")) {
      return;
    }
    else if (e.getActionCommand().equals("next")) {
      objIndex++;
      if (objIndex == dmanager.objects.size()) {
        objIndex--;
      }
      showObject();
    }
    else {
      try {
        int index = Integer.parseInt(e.getActionCommand());
        this.objIndex = index;
        showObject();
      }
      catch (NumberFormatException exc) {
        //
      }
    }
  }

  public void showObject() {
    PyDebugValue dvalue = (PyDebugValue)dmanager.objects.getValue(objIndex);
    if (objIndex < dmanager.namedRange) {
      setName(dvalue.getName());
    }
    else {
      setName("----");
    }
    setType(dvalue.getType());
    setValue(dvalue.getValue());

    ArrayList<DebugChild> childrenList = dmanager.getChildren(objIndex);

    for (JButton cbutton : childButtons) {
      myPanel.remove(cbutton);
    }
    childButtons.clear();

    int childNumber = 0;
    for (DebugChild c : childrenList) {
      PyDebugValue cvalue = (PyDebugValue)dmanager.objects.getValue(c.index);
      JButton cbutton = new JButton();
      cbutton.setText(cvalue.getType() + " " + c.fieldName + " : " + cvalue.getValue());
      cbutton.setActionCommand(new Integer(c.index).toString());
      cbutton.addActionListener(this);
      myPanel.add(cbutton, new GridConstraints(childNumber, 2, 1, 1,
                                               GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE,
                                               GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_GROW,
                                               new JBDimension(-1, -1), new JBDimension(-1, -1), new JBDimension(-1, -1)));
      childButtons.add(cbutton);
      childNumber++;
    }

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

  public void setValue(String type) {
    objValue.setText(type);
  }


  private void createUIComponents() {
  }
}
