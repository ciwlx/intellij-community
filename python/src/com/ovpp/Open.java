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
 */
import java.awt.*;
import java.applet.*;
import java.util.*;

public class Open extends Applet implements Runnable {
  Thread t,t1;
  public void start() {
    t = new Thread(this);
    t.start();
  }
  public void run() {
    t1 = Thread.currentThread();
    while(t1 == t) {
      repaint();
      try {
        t1.sleep(1000);
      }
      catch(InterruptedException e){}
    }
  }
  public void paint(Graphics g) {
    ArrayList<Data> show = new ArrayList<>();
    show.add(new Data("a", "int", 3));
    show.add(new Data("b", "double",  2.7));
    show.add(new Data("c", "String", "msg"));

    drawData(g, show);
  }
  public int drawData(Graphics g, ArrayList<Data> data){
    int posX = 50; int posY = 50; int len_x = 0; int cnt_y = 1;

    for(Data d:data){
      String s = d.toString();
      len_x = Math.max(len_x, s.length());
      g.drawString(s, posX + 10, posY + 15 * cnt_y);
      cnt_y++;
    }

    int total_x = len_x * 8;
    // Filling Square
    g.drawRect(posX, posY, total_x, cnt_y * 15);

    return total_x;
  }
}
