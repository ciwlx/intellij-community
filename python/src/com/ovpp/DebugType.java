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

import com.intellij.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sehs on 2017-05-30.
 */

public class DebugType {
  String typename;
  List<String> fields  = new ArrayList<String>();

  public DebugType(String name) {
    this.typename = name;
  }

  public void addFields(String newfield) {
    for (String f : fields) {
      if (f.equals(newfield)) {
        return;
      }
    }
    fields.add(newfield);
  }
}
