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

import com.jetbrains.python.debugger.PyDebugValue;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;


/**
 * Created by Sehs on 2017-06-03.
 */
public class DebugCanvas extends JPanel implements MouseMotionListener, MouseInputListener, MouseWheelListener {
  int fontsize = 13;
  int lineheight = 14;

  int width = 700;
  int height = 500;

  int rectsize = 100;
  int rectoffset = 110;
  int col = 6;

  int left;
  int up;

  int pressedObjectIndex;
  int xoff;
  int yoff;

  DebugObjectViewer viewer;
  boolean selected;

  public DebugCanvas(DebugObjectViewer viewer) {
    super();
    this.viewer = viewer;
    this.addMouseListener(this);
    this.addMouseMotionListener(this);
    this.addMouseWheelListener(this);
    left = 0;
    up = 0;
  }

  public Dimension getPrefferedSize() {
    return new Dimension(width, height);
  }

  public void mouseWheelMoved(MouseWheelEvent e) {
    // wheel up is negative; wheel down is positive
    //this.left += e.getWheelRotation();
    //this.repaint();
  }

  public void mouseMoved(MouseEvent e) {

  }

  public void mouseExited(MouseEvent e) {

  }

  public void mouseEntered(MouseEvent e) {

  }

  public void mouseClicked(MouseEvent e) {

  }

  public void mousePressed(MouseEvent e) {
    int x = e.getX();
    int y = e.getY();
    DebugSnapshot shot = viewer.dmanager.currentSnapshot();
    for (int i = 0; i < shot.objects.size(); i++) {
      Rectangle cur = shot.objectRect.get(i);
      if (cur.contains(new Point(x+left, y+up))) {
        shot.objIndex = i;
        pressedObjectIndex = i;
        xoff = x + left - cur.x;
        yoff = y + up - cur.y;
        selected = true;
        viewer.showObject();
        return;
      }
    }
    xoff = x + left;
    yoff = y + up;
    selected = false;
  }

  public void mouseReleased(MouseEvent e) {
    selected = false;
  }

  public void mouseDragged(MouseEvent e) {
    DebugSnapshot shot = viewer.dmanager.currentSnapshot();
    if (selected == true) {
      int x = e.getX();
      int y = e.getY();
      shot.objectRect.get(pressedObjectIndex).setLocation(x + left - xoff, y + up - yoff);
    }
    else {
      int x = e.getX();
      int y = e.getY();
      left = xoff - x;
      up = yoff - y;
    }

    this.repaint();
  }

  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    g.setFont(new Font("Helvetica", Font.PLAIN, fontsize));
    if (viewer.dmanager.snapshots.isEmpty()) {
      return;
    }
    DebugSnapshot shot = viewer.dmanager.currentSnapshot();
    g.setColor(Color.WHITE);

    if (shot.objectRect.isEmpty()) {
      int count = 0;
      int rowMaxHeight= 0;
      int curY = 10;
      for (int i = 0; i < shot.objects.size(); i++) {
        PyDebugValue dvalue = (PyDebugValue)(shot.objects.getValue(i));
        String dtype = dvalue.getType();

          if (dtype.equals("int") ||
             dtype.equals("float") ||
             dtype.equals("complex") ||
             dtype.equals("ellipsis") ||
             dtype.equals("bool") ||
             dtype.equals("NoneType") ||
             dtype.equals("str")) {
            if (i >= shot.namedRange) {
              shot.objectRect.add(new Rectangle(0, 0, 0, 0));
            }
            else {
              int height = lineheight * 3;
              int c = count % col;
              int r = count / col;

              if (c == 0) {
                curY += rowMaxHeight + 10;
                rowMaxHeight = 0;
              }
              shot.objectRect.add(new Rectangle(c * rectoffset + 10, curY, rectsize, height));
              count++;
              if (rowMaxHeight < height) {
                rowMaxHeight = height;
              }
            }
        }
        else {
          ArrayList<DebugChild> children = shot.getChildren(i);
          int csize = children.size();
          int height = (csize+2) * lineheight;
          int c = count % col;
          int r = count / col;

          if (c == 0) {
            curY += rowMaxHeight + 10;
            rowMaxHeight = 0;
          }
          shot.objectRect.add(new Rectangle(c * rectoffset + 10, curY, rectsize, height));
          count++;
          if (rowMaxHeight < height) {
            rowMaxHeight = height;
          }
        }
      }
    }

    for (int i = 0; i < shot.objects.size(); i++) {
      PyDebugValue dvalue = (PyDebugValue)shot.objects.getValue(i);
      String dtype = dvalue.getType();
      Rectangle rect = shot.objectRect.get(i);
      Point loc = rect.getLocation();
      Dimension size = rect.getSize();

      int x = loc.x - left;
      int y = loc.y - up;

      if (dtype.equals("int") ||
          dtype.equals("float") ||
          dtype.equals("complex") ||
          dtype.equals("ellipsis") ||
          dtype.equals("bool") ||
          dtype.equals("NoneType") ||
          dtype.equals("str")) {
        if (i >= shot.namedRange) {
          continue;
        }
        else {
          if (i == shot.objIndex) {
            g.setColor(new Color(0, 0, 255, 255));
            g.drawRoundRect(x, y, size.width, size.height, 2, 2);
            g.setColor(new Color(192, 192,192, 32));
            g.fillRoundRect(x, y, size.width, size.height, 2, 2);
            g.setColor(new Color(192, 64, 64, 255));
            g.drawString(dvalue.getValue(), x + 5, y + lineheight*2);
            g.setColor(new Color(0, 0, 0, 255));
            g.drawString(dvalue.getType() + " " + dvalue.getName(), x + 5, y + lineheight);
          }
          else {
            g.setColor(new Color(0, 0, 255, 64));
            g.drawRoundRect(x, y, size.width, size.height, 2, 2);
            g.setColor(new Color(192, 192,192, 16));
            g.fillRoundRect(x, y, size.width, size.height, 2, 2);
            g.setColor(new Color(192, 64, 64, 64));
            g.drawString(dvalue.getValue(), x + 5, y + lineheight*2);
            g.setColor(new Color(0, 0, 0, 64));
            g.drawString(dvalue.getType() + " " + dvalue.getName(), x + 5, y + lineheight);
          }
        }
      }
      else {
        String name = dvalue.getName();
        if (i >= shot.namedRange) {
          name = "*";
        }
        if (i == shot.objIndex) {
          g.setColor(new Color(0, 0, 0, 255));
          g.drawString(dvalue.getType() + " " + name, x + 5, y + lineheight);
          g.setColor(new Color(0, 0, 255, 255));
          g.drawRoundRect(x, y, size.width, size.height, 2, 2);
          g.setColor(new Color(192, 192,192, 32));
          g.fillRoundRect(x, y, size.width, size.height, 2, 2);
        }
        else {
          g.setColor(new Color(0, 0, 0, 64));
          g.drawString(dvalue.getType() + " " + name, x + 5, y + lineheight);
          g.setColor(new Color(0, 0, 255, 64));
          g.drawRoundRect(x, y, size.width, size.height, 2, 2);
          g.setColor(new Color(192, 192,192, 16));
          g.fillRoundRect(x, y, size.width, size.height, 2, 2);
        }

        ArrayList<DebugChild> children = shot.getChildren(i);
        for (int j = 0; j < children.size(); j++) {
          int cindex = children.get(j).index;
          PyDebugValue cvalue = (PyDebugValue)shot.objects.getValue(cindex);
          String ctype = cvalue.getType();

          if (ctype.equals("int") ||
              ctype.equals("float") ||
              ctype.equals("complex") ||
              ctype.equals("ellipsis") ||
              ctype.equals("bool") ||
              ctype.equals("NoneType") ||
              ctype.equals("str")) {

            if (i == shot.objIndex) {
              g.setColor(new Color(255,0, 0, 255));
            }
            else {
              g.setColor(new Color(255, 0, 0, 48));
            }
            g.drawString(children.get(j).fieldName + " :", x + 5, y + lineheight * (j+2));
            g.drawString(cvalue.getValue(), x + rectsize / 2, y + lineheight * (j+2));
          }

          else {
            Rectangle crect = shot.objectRect.get(cindex);
            Point cloc = crect.getLocation();
            int cx = cloc.x - left;
            int cy = cloc.y - up;

            Point start = new Point(x + rectsize / 2, y + lineheight*(j+1) + lineheight/2 + 2);
            Point end = new Point(cx + 2, cy + 2);

            if (i == shot.objIndex) {
              g.setColor(new Color(255, 0, 0, 255));
            }
            else {
              g.setColor(new Color(255, 0, 0, 48));
            }

            g.drawString(children.get(j).fieldName + " :", x + 5, y + lineheight*(j+2));
            g.drawLine(start.x, start.y, end.x, end.y);
            g.fillOval(start.x-4, start.y-4, 8, 8);
            Point a = new Point(end.x, end.y);
            double angle = Math.atan2(start.y - end.y, start.x - end.x);
            double cw = angle + Math.PI / 6;
            double ccw = angle - Math.PI / 6;

            Point b = new Point(a.x + (int)Math.round(10 * Math.cos(cw)), a.y + (int)Math.round(10 * Math.sin(cw)));
            Point c = new Point(a.x + (int)Math.round(10 * Math.cos(ccw)), a.y + (int)Math.round(10 * Math.sin(ccw)));
            g.drawPolyline(new int[]{b.x, a.x, c.x}, new int[]{b.y, a.y, c.y}, 3);
          }
        }
      }
    }
  }
}
