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

import com.intellij.notification.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.components.JBLabel;
import com.intellij.xdebugger.frame.XValue;
import com.intellij.xdebugger.frame.XValueChildrenList;
import com.jetbrains.python.debugger.PyDebugValue;
import com.jetbrains.python.debugger.PyDebuggerException;
import com.jetbrains.python.debugger.PyFrameAccessor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sehs on 2017-05-23.
 */
public class DebugManager {
  XValueChildrenList objects;
  ArrayList<ArrayList<DebugChild>> childrenList;
  DebugTypeManager myTypeManager;
  PyFrameAccessor myFrameAccessor;
  int namedRange;

  public DebugManager (PyFrameAccessor frameAccessor) {
    myFrameAccessor = frameAccessor;
  }

  public void buildSnapshot() {
    objects = new XValueChildrenList();
    myTypeManager = new DebugTypeManager();
    childrenList = new ArrayList<ArrayList<DebugChild>>();
    namedRange = 0;

    ApplicationManager.getApplication().executeOnPooledThread(() -> {
      try {
        XValueChildrenList values = myFrameAccessor.loadFrame();
        for (int i = 0; i < values.size(); i++) {
          XValue value = values.getValue(i);

          if (value instanceof PyDebugValue) {
            PyDebugValue dvalue = (PyDebugValue)value;
            String name = dvalue.getName();
            if (name.equals("__builtins__") ||
                name.equals("__doc__") ||
                name.equals("__file__") ||
                name.equals("__loader__") ||
                name.equals("__name__") ||
                name.equals("__package__") ||
                name.equals("__spec__"))
              continue;
            boolean visited = false;
            for (int j = 0; j < objects.size(); j++) {
              if (((PyDebugValue)objects.getValue(j)).getValue().equals(dvalue.getValue())) {
                visited = true;
              }
            }
            if (visited == false) {
              objects.add(dvalue);
              namedRange++;
            }
          }
        }
      }
      catch (PyDebuggerException e) {
        //
      }

      int i = 0;
      while (i < objects.size()) {
        PyDebugValue pvalue = (PyDebugValue)objects.getValue(i);
        String typename = pvalue.getType();
        myTypeManager.addType(typename);
        childrenList.add(new ArrayList<DebugChild>());

        try {
          XValueChildrenList childvalues = myFrameAccessor.loadVariable(pvalue);

          for (int j = 0; j < childvalues.size(); j++) {
            XValue value= childvalues.getValue(j);

            if (value instanceof PyDebugValue) {
              PyDebugValue dvalue = (PyDebugValue)value;

              myTypeManager.addField(typename, dvalue.getName());

              boolean visited = false;
              int childIndex = 0;
              for (int k = 0; k < objects.size(); k++) {
                if (((PyDebugValue)objects.getValue(k)).getValue().equals(dvalue.getValue())) {
                  visited = true;
                  childIndex = k;
                }
              }
              if (visited == false) {
                childIndex = objects.size();
                objects.add(dvalue);
              }

              ((ArrayList<DebugChild>)childrenList.get(i)).add(new DebugChild(childIndex, dvalue.getName()));
            }
          }
        }
        catch (PyDebuggerException e) {
          //
        }
        i++;
      }
    });

    /*
    NotificationGroup ng = new NotificationGroup("VariableTesting", NotificationDisplayType.NONE, true);
    for (int i = 0; i < objects.size(); i++) {
      PyDebugValue v = (PyDebugValue)objects.getValue(i);

      Notification n = ng.createNotification(v.getId() + " " + v.getValue(), NotificationType.INFORMATION);
      Notifications.Bus.notify(n);

  */

    /*
    for (int i = 0; i < objects.size(); i++) {
      PyDebugValue v = (PyDebugValue)objects.getValue(i);
      JBLabel name = new JBLabel();
      name.setText(v.getName());

    }
    */

  }

  public ArrayList getChildren(int objIndex) {
    return (ArrayList<DebugChild>)childrenList.get(objIndex);
  }

  public DebugType getType(PyDebugValue dvalue) {return null;}

}
