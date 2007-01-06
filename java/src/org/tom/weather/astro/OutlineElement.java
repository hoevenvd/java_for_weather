package org.tom.weather.astro;

/**
 * OutlineElement implements a hierarchical text list, using ideas from an <a
 * href="http://www.javaworld.com/javaworld/jw-01-1997/step/Sextant.java">
 * article</a> in Java World and from <a href="http://www.apple.com">my article</a>
 * in Apple's develop magazine.
 * <p>
 * 
 * Copyright &copy; 1996-1998 Martin Minow. All Rights Reserved.
 * <p>
 * 
 * Permission to use, copy, modify, and redistribute this software and its
 * documentation for personal, non-commercial use is hereby granted provided
 * that this copyright notice and appropriate documentation appears in all
 * copies. This software may not be distributed for fee or as part of
 * commercial, "shareware," and/or not-for-profit endevors including, but not
 * limited to, CD-ROM collections, online databases, and subscription services
 * without specific license.
 * <p>
 * 
 * @author <a href="mailto:minow@merrymeet.com">Martin Minow</a>
 * @version 1.0 Set tabs every 4 characters.
 */
// package Classes;
import java.util.*;
import java.awt.*;

public class OutlineElement extends Object {
  private OutlineComponent owner; /* Who is displaying this element */
  public String name; /* What is to be displayed. */
  public Object value; /* This is returned on selection */
  public boolean open; /* Is this expanded? */
  public boolean selected; /* Is this selected? */
  private OutlineElement sibling; /* Next on this indent level */
  private OutlineElement child; /* Start of the next indent level */
  private Font font; /* This element's font */

  /**
   * Create a OutlineElement for this canvas.
   * 
   * @param owner
   *          The OutlineComponent that contains this text.
   * @param name
   *          The text that will be displayed.
   */
  public OutlineElement(OutlineComponent owner, String name, Object value) {
    this.owner = owner;
    this.name = name;
    this.value = value;
    this.font = owner.getFont();
  }

  /**
   * Draw this element at the indicated coordinates. This may be overriden.
   * 
   * @param g
   *          The graphics context
   * @param x
   *          The x location Ê@param y The y location
   */
  public void drawElement(Graphics g, int x, int y) {
    FontMetrics fm = getFontMetrics();
    g.drawString(name, x, y + ((thisElementHeight() - fm.getHeight()) / 2)
        + fm.getAscent());
  }

  /**
   * Return the element that the mouse coordinates point to. The coordinates are
   * with respect to the Component bounds.
   * 
   * @param x
   *          The mouse horizontal coordinate
   * @param y
   *          The mouse vertical coordinate
   * @return The element (null if none).
   */
  OutlineElement findElement(int x, int y) {
    int height = thisElementHeight();
    int iconWidth = getIndicatorWidth();
    int textWidth = getFontMetrics().stringWidth(name);
    int thisWidth = iconWidth + OutlineComponent.indicatorSeparation
        + textWidth;
    if (x >= 0 && y >= 0 && x < thisWidth && y < height) {
      return (this);
    } else {
      y -= height;
      if (open && child != null) {
        OutlineElement childElement = child.findElement(x, y);
        if (childElement != null) {
          return (childElement);
        }
        y -= child.getHeight();
      }
      if (sibling != null) {
        return (sibling.findElement(x, y));
      } else {
        return (null);
      }
    }
  }

  public OutlineElement getChild() {
    return (child);
  }

  /**
   * Return this element's font. This may be overridden. The default uses the
   * component's font.
   * 
   * @return the element's font.
   */
  public Font getFont() {
    return (font);
  }

  /**
   * Return this element's font metrics. This may be overridden. The default
   * uses the element's font..
   * 
   * @return the element's font.
   */
  public FontMetrics getFontMetrics() {
    return (owner.getFontMetrics(getFont()));
  }

  /**
   * Return the height of this element and all child and sibling elements.
   * 
   * @return the total text height.
   */
  public int getHeight() {
    int height = thisElementHeight();
    if (open && child != null) {
      height += child.getHeight();
    }
    if (sibling != null) {
      height += sibling.getHeight();
    }
    return (height);
  }

  /**
   * Return this element's indicator height. This may be overridden. The default
   * uses the owner's indicator height.
   * 
   * @return indicator height in pixels.
   */
  public int getIndicatorHeight() {
    return (getFontMetrics().getAscent());
  }

  /**
   * Return this element's indicator width. This may be overridden. The default
   * uses the owner's indicator width.
   * 
   * @return indicator width in pixels.
   */
  public int getIndicatorWidth() {
    return (getIndicatorHeight());
  }

  /**
   * Traverse the open elements and return the display width of the widest line.
   */
  public int getMaxElementWidth(int indent, int currentMax) {
    int width = thisElementWidth(indent);
    if (width > currentMax) {
      currentMax = width;
    }
    if (open && child != null) {
      currentMax = child.getMaxElementWidth(indent + 1, currentMax);
    }
    if (sibling != null) {
      currentMax = sibling.getMaxElementWidth(indent, currentMax);
    }
    return (currentMax);
  }

  public String getName() {
    return (name);
  }

  /**
   * Return the open flag.
   */
  public boolean getOpen() {
    return (open && child != null);
  }

  /**
   * Return the selected flag
   */
  public boolean getSelected() {
    return (selected);
  }

  public OutlineElement getSibling() {
    return (sibling);
  }

  public Object getValue() {
    return (value);
  }

  public OutlineElement setChild(OutlineElement child) {
    this.child = child;
    return (child);
  }

  /**
   * Set a specific font for this element..
   * 
   * @param The
   *          font to set..
   */
  public void setFont(Font font) {
    this.font = font;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * Set the open flag.
   */
  public void setOpen(boolean newFlag) {
    if (child != null) {
      open = newFlag;
    }
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }

  public OutlineElement setSibling(OutlineElement sibling) {
    this.sibling = sibling;
    return (sibling);
  }

  public void setValue() {
    this.value = value;
  }

  /**
   * Return the height of this text element. This can be overridden for
   * variable-height elements.
   * 
   * @return this text element height.
   */
  public int thisElementHeight() {
    int height = Math.max(getIndicatorHeight(), getFontMetrics().getHeight());
    return (height);
  }

  /**
   * Return the width of this element, including the width of the indicator and
   * the indentation.
   */
  public int thisElementWidth(int indent) {
    int width = getIndicatorWidth() + owner.indicatorSeparation
        + (indent * owner.indicatorSeparation)
        + getFontMetrics().stringWidth(name);
    return (width);
  }

  /**
   * Return a string representation (for debugging)
   */
  public String toString() {
    StringBuffer result = new StringBuffer();
    if (name != null) {
      result.append(name);
    }
    if (value != null) {
      if (name != null) {
        result.append(" = ");
      }
      result.append("{" + value.toString() + "}");
    }
    if (selected) {
      result.append(",+SEL");
    }
    if (open) {
      result.append(",+OPEN");
    }
    return (result.toString());
  }
}
