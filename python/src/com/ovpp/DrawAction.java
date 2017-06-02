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

import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.frame.XStackFrame;
import com.jetbrains.python.console.*;
import com.jetbrains.python.debugger.PyDebugProcess;
import com.jetbrains.python.debugger.PyDebugSessionFactory;
import com.jetbrains.python.debugger.PyDebugValue;
import com.jetbrains.python.debugger.PyFrameAccessor;
import com.jetbrains.python.debugger.containerview.PyDataView;

/**
 * Created by Sehs on 2017-05-31.
 */
public class DrawAction extends AnAction {

  public DrawAction() {
    super("Draw Object");
  }

  public void actionPerformed(AnActionEvent e) {
    ProcessHandler a = XDebuggerManager.getInstance(e.getProject()).getCurrentSession().getDebugProcess().getProcessHandler();
    PyFrameAccessor frameAccessor = PyDataView.getInstance(e.getProject()).getFrameAccessor(a);
    DebugManager dmanager = new DebugManager(frameAccessor);
    dmanager.buildSnapshot();

    DebugObjectViewer view = new DebugObjectViewer(dmanager);
    view.show();
  }
}
