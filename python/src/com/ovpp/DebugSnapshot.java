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

import com.intellij.xdebugger.frame.XValue;
import com.intellij.xdebugger.frame.XValueChildrenList;
import com.jetbrains.python.debugger.PyDebugValue;
import com.jetbrains.python.debugger.PyDebuggerException;
import com.jetbrains.python.debugger.PyFrameAccessor;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Sehs on 2017-06-04.
 */
public class DebugSnapshot {
  XValueChildrenList objects;
  ArrayList<ArrayList<DebugChild>> childrenList;
  ArrayList<ArrayList<DebugParent>> parentList;
  DebugTypeManager myTypeManager;
  PyFrameAccessor myFrameAccessor;
  int namedRange;
  int objIndex;

  ArrayList<Rectangle> objectRect;
  //ArrayList<Integer> objectx;
  //ArrayList<Integer> objecty;

  public DebugSnapshot (PyFrameAccessor frameAccessor) {
    myFrameAccessor = frameAccessor;
  }

  public void build() {
   objects = new XValueChildrenList();
    myTypeManager = new DebugTypeManager();
    childrenList = new ArrayList<ArrayList<DebugChild>>();
    parentList = new ArrayList<ArrayList<DebugParent>>();
    //objectx = new ArrayList<Integer>();
    //objecty = new ArrayList<Integer>();
    objectRect = new ArrayList<Rectangle>();

    namedRange = 0;
    objIndex = 0;

    try {
      XValueChildrenList values = myFrameAccessor.loadFrame();
      for (int i = 0; i < values.size(); i++) {
        XValue value = values.getValue(i);

        if (value instanceof PyDebugValue) {
          PyDebugValue dvalue = (PyDebugValue)value;
          String name = dvalue.getName();
          String type = dvalue.getType();
          if (name.equals("__builtins__") ||
              name.equals("__doc__") ||
              name.equals("__file__") ||
              name.equals("__loader__") ||
              name.equals("__name__") ||
              name.equals("__package__") ||
              name.equals("__spec__") ||
              type.equals("module"))
            continue;
          boolean visited = false;
          for (int j = 0; j < objects.size(); j++) {
            if (((PyDebugValue)objects.getValue(j)).getValue().equals(dvalue.getValue())
              && ((PyDebugValue)objects.getValue(j)).getType().equals(dvalue.getType())) {
              visited = true;
            }
          }
          if (visited == false) {
            objects.add(dvalue);
            parentList.add(new ArrayList<DebugParent>());
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
              if (((PyDebugValue)objects.getValue(k)).getValue().equals(dvalue.getValue())
                  && ((PyDebugValue)objects.getValue(k)).getType().equals(dvalue.getType())) {
                visited = true;
                childIndex = k;
              }
            }
            if (visited == false) {
              childIndex = objects.size();
              objects.add(dvalue);
              parentList.add(new ArrayList<DebugParent>());
            }

            ((ArrayList<DebugChild>)childrenList.get(i)).add(new DebugChild(childIndex, dvalue.getName()));
            ((ArrayList<DebugParent>)parentList.get(childIndex)).add(new DebugParent(i, dvalue.getName()));
          }
        }
      }
      catch (PyDebuggerException e) {
        //
      }
      i++;
    }
  }

  public ArrayList getChildren(int objIndex) {
    return (ArrayList<DebugChild>)childrenList.get(objIndex);
  }

  public ArrayList getParent(int objIndex) {
    return (ArrayList<DebugParent>)parentList.get(objIndex);
  }

  public DebugType getType(PyDebugValue dvalue) {return null;}
}
