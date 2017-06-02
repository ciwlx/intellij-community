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

/**
 * Created by toz57 on 2017. 5. 27..
 * Temporary class for test visualize.
 */
public class Data {
  String var_name;
  String type_name;
  String value_str;
  String reference = "";
  Object object;

  Data(String name, String type, Object o) {
    var_name = name;
    type_name = type;
    value_str = o.toString();
    object = o;
  }

  Data(String name, String type, Object o, String ref) {
    this(name, type, o);
    reference = ref;
  }

  public String toString() {
    return var_name + "\t" + type_name + "\t" + value_str + "\t" + reference;
  }
}
