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

import java.util.ArrayList;

/**
 * Created by Sehs on 2017-05-30.
 */
public class DebugTypeManager {
  ArrayList<DebugType> types;

  public DebugTypeManager() {
    types = new ArrayList<DebugType>();
  }

  public void addField(String typename, String fieldname) {
    DebugType type = findType(typename);
    type.addFields(fieldname);
  }

  public void addType(String typename) {
    findType(typename);
  }

  public DebugType findType(String typename) {
    for (DebugType t : types) {
      String tname = t.typename;
      if (typename.equals(tname)) {
        return t;
      }
    }
    DebugType t = new DebugType(typename);
    types.add(t);
    return t;
  }

}
