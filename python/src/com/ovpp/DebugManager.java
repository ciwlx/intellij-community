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
  PyFrameAccessor myFrameAccessor;
  ArrayList<DebugSnapshot> snapshots;
  int currentIndex;

  public DebugManager (PyFrameAccessor frameAccessor) {
    myFrameAccessor = frameAccessor;
    snapshots = new ArrayList<DebugSnapshot>();
    currentIndex = 0;
  }

  public DebugSnapshot currentSnapshot() {
    return snapshots.get(currentIndex);
  }

  public void buildSnapshot() {
    DebugSnapshot shot = new DebugSnapshot(myFrameAccessor);
    shot.build();
    snapshots.add(shot);
    currentIndex = snapshots.size() - 1;
  }

  public void removeSnapshot() {
    if (!snapshots.isEmpty()) {
      snapshots.remove(currentIndex);
      currentIndex = snapshots.size() - 1 ;
    }
    if (snapshots.isEmpty()) {
      currentIndex = 0;
    }
  }
}
