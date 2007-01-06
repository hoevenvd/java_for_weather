package org.tom.weather.astro;

/**
 * OutlineComponent implements a hierarchical list, using ideas from an <a
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
 * @version 1.0 Set tabs every 4 characters. Requires Java 1.1 support
 */
// package Classes;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

public class OutlineComponent extends java.awt.Component implements
    MouseMotionListener, MouseListener {
  public static final int CLICK_SELECT = 0; /* Double-click */
  public static final int LIST_SELECT = 1; /* OutlineElement selected */
  public static final int LIST_DESELECT = 2; /* OutlineElmenet released */
  private OutlineElement root; /* The root of the element tree */
  private OutlineElement selected; /* The currently selected element */
  private Stack tree = new Stack();
  private int treeDepth = 0;
  /*
   * These parameters are used to adjust the parts of the display.
   */
  public static final int indicatorSlop = 2;
  public static final int indicatorSeparation = 3;
  public static final int columnIndent = 4;
  private boolean canSelectText;
  private boolean colorSelected;
  /*
   * Mouse click states and events. State mouseIdle mouseInButton mouseInText
   * mouseDown Start Track <error> <error> mouseDrag <ignore> Track Mouse Track
   * Mouse mouseUp <error> End Mouse Track End Mouse Track Events: Start Track:
   * Check whether mouse is in text or a triangle, Set mouseRect to the
   * text/triangle bounds. Set mouseState to mouseInButton or mouseInText Hilite
   * selected area. mouseInRect = true; Track Mouse: When mouse enters or leaves
   * the selection rectangle, invert the hilite state. End Track: If mouseInRect
   * is true, do the selection. Clear the hilite. mouseState = mouseIdle;
   * <error> Clear the hilite state. mouseState = mouseIdle; Continue as for
   * Start Track
   */
  public static final int mouseIdle = 0;
  public static final int mouseInButton = 1;
  public static final int mouseInText = 2;
  private int mouseState = mouseIdle;
  public static final int mouseClick = 1;
  public static final int mouseLeavesRect = 2;
  public static final int mouseEntersRect = 3;
  public static final int mouseRelease = 4;
  private boolean mouseInRect;
  private Rectangle mouseRect = new Rectangle();
  private OutlineElement mouseElement = null;
  private OutlinePanel outlinePanel;
  private transient ActionListener actionListener = null;

  /**
   * Create a new OutlineComponent. This must be put inside an OutlinePanel
   * (which is a ScrollPane)
   * 
   * @param canSelectText
   *          true if document elements can be selected. (Normally true for
   *          option lists, and false for software documentation.)
   * @param colorSelected
   *          true if the selected element should be colorized.
   */
  public OutlineComponent(OutlinePanel outlinePanel, boolean canSelectText,
      boolean colorSelected) {
    this.outlinePanel = outlinePanel;
    this.canSelectText = canSelectText;
    this.colorSelected = colorSelected;
    root = null;
    treeDepth = 0;
    addMouseListener(this);
    addMouseMotionListener(this);
  }

  public synchronized void addActionListener(ActionListener listener) {
    actionListener = AWTEventMulticaster.add(actionListener, listener);
  }

  /**
   * Append a new element at the end of the sequence. This element has a null
   * value object.
   * 
   * @param depth
   *          The current indentation depth. The caller must keep track of the
   *          current depth. This module fails with a RuntimeException if the
   *          depth is less than zero or more than "current depth" + 1. It would
   *          be trivial to extend this class to add elements at the current or
   *          child level, or to pop up one or more levels.
   * @param name
   *          The visual label for this element.
   * @return The element that was just added.
   * @throws RuntimeException
   *           if the depth is incorrect.
   */
  public OutlineElement addText(int depth, String name) {
    return (addText(depth, name, null));
  }

  /**
   * Append a new element at the end of the sequence. This element has a null
   * value object.
   * 
   * @param depth
   *          The current indentation depth. The caller must keep track of the
   *          current depth. This module fails with a RuntimeException if the
   *          depth is less than zero or more than "current depth" + 1. It would
   *          be trivial to extend this class to add elements at the current or
   *          child level, or to pop up one or more levels.
   * @param name
   *          The visual label for this element.
   * @param value
   *          An object attached to this element.
   * @return The element that was just added.
   * @throws RuntimeException
   *           if the depth is incorrect.
   */
  public OutlineElement addText(int depth, String name, Object value) {
    if (name == null) {
      name = "";
    }
    OutlineElement element = new OutlineElement(this, name, value);
    if (root == null) {
      if (depth != 0) {
        throw new RuntimeException("Initial depth must be zero for " + name);
      } else {
        root = element;
        tree.push(element);
        invalidate();
        outlinePanel.validate();
      }
    } else {
      if (depth == treeDepth) {
        tree.push(((OutlineElement) tree.pop()).setSibling(element));
      } else if (depth > treeDepth) {
        if (depth > treeDepth + 1) {
          throw new RuntimeException("Excessive depth error for " + name);
        } else {
          tree.push(((OutlineElement) tree.peek()).setChild(element));
        }
      } else /* if (depth < treeDepth) */{
        for (int i = depth; i < treeDepth; i++) {
          tree.pop();
        }
        tree.push(((OutlineElement) tree.pop()).setSibling(element));
      }
      treeDepth = depth;
    }
    return (element);
  }

  /**
   * Initialize the OutlineComponent, throwing away all content.
   */
  public void clearAllElements() {
    selectElement(null);
    root = null;
    treeDepth = 0;
    while (tree.empty() == false) {
      tree.pop();
    }
    outlinePanel.setScrollPosition(0, 0);
  }

  /**
   * Continue tracking the mouse, redrawing the selection state as the mouse
   * moves in and out of the selection area.
   */
  public void continueMouseTrack(MouseEvent event) {
    int x = event.getX();
    int y = event.getY();
    boolean nowInRect = mouseRect.contains(x, y);
    if (nowInRect != mouseInRect) {
      mouseInRect = nowInRect;
      switch (mouseState) {
        case mouseInButton:
          drawIndicator();
          break;
        case mouseInText:
          hiliteTextArea(mouseInRect);
          break;
        default:
          throw new RuntimeException("Bogus mouse state");
      }
    }
  }

  private void drawAnimation() {
    drawIndicator(getGraphics(), mouseElement, mouseRect.x, mouseRect.y, true);
    /*
     * Stall about 50 msec. for eye-candy.
     */
    try {
      Thread.sleep(54);
    } catch (Exception e) {
    }
    ;
  }

  private void drawIndicator() {
    drawIndicator(getGraphics(), mouseElement, mouseRect.x, mouseRect.y, false);
  }

  /**
   * Draw the correct indicator (called by OutlineElement).
   * 
   * @param g
   *          The graphics context
   * @param element
   *          The element to draw
   * @param x
   *          The x origin of the indicator
   * @param y
   *          The y origin of the indicator
   * @param animation
   *          True if this is the special animation triangle.
   */
  public void drawIndicator(Graphics g, OutlineElement element, int x, int y,
      boolean animation) {
    int indicatorSize = element.getIndicatorHeight();
    Polygon poly = null;
    int elementHeight = element.thisElementHeight();
    y += ((elementHeight - indicatorSize) / 2);
    int buttonSize = (indicatorSize) & ~1; /* Rounded up to an even number */
    int halfSize = buttonSize / 2;
    if (animation) {
      int animationSize = (buttonSize * 3) / 4;
      poly = new Polygon();
      poly.addPoint(x + animationSize, y);
      poly.addPoint(x + animationSize, y + animationSize);
      poly.addPoint(x, y + animationSize);
      poly.addPoint(x + animationSize, y);
    } else if (element.getChild() == null) {
      /*
       * No child: nothing to draw..
       */
    } else if (element.getOpen()) {
      /*
       * Draw an open element indicator.
       */
      poly = new Polygon();
      poly.addPoint(x, y + halfSize);
      poly.addPoint(x + buttonSize, y + halfSize);
      poly.addPoint(x + halfSize, y + buttonSize);
      poly.addPoint(x, y + halfSize);
    } else {
      /*
       * Draw a closed element indicator.
       */
      poly = new Polygon();
      poly.addPoint(x + halfSize, y);
      poly.addPoint(x + buttonSize, y + halfSize);
      poly.addPoint(x + halfSize, y + buttonSize);
      poly.addPoint(x + halfSize, y);
    }
    if (poly != null) {
      Color saveColor = getForeground();
      if (animation || (mouseState == mouseInButton && mouseInRect)) {
        g.setColor(Color.black);
      } else {
        g.setColor(Color.lightGray);
      }
      g.fillPolygon(poly);
      g.setColor(Color.black);
      g.drawPolygon(poly);
      g.setColor(saveColor);
    }
  }

  private int dumpLevel(OutlineElement element, int indent, int index) {
    StringBuffer work = new StringBuffer();
    if (indent < 10) {
      work.append(" ");
    }
    work.append(indent);
    work.append(" ");
    if (index < 10) {
      work.append(" ");
    }
    work.append(index);
    work.append(": " + element.getName());
    System.out.println(work);
    ++index;
    if (element.getChild() != null) {
      System.out.println("  Chi: " + element.getChild().getName());
    }
    for (OutlineElement sibling = element.getSibling(); sibling != null; sibling = sibling
        .getSibling()) {
      System.out.println("  Sib: " + sibling.getName());
    }
    ++index;
    if (element.getChild() != null) {
      index = dumpLevel(element.getChild(), indent + 1, index);
    }
    if (element.getSibling() != null) {
      index = dumpLevel(element.getSibling(), indent, index);
    }
    return (index);
  }

  /**
   * Debug method
   */
  public void dumpTree() {
    int total = dumpLevel(root, 0, 0);
    System.out.println(total + " total elements");
  }

  /**
   * The user released the mouse, if it's still in the selection area, perform
   * any selection actions, then reset the mouse state.
   */
  public void endMouseTrack(MouseEvent event) {
    switch (mouseState) {
      case mouseInButton:
        if (mouseInRect) {
          drawAnimation();
          mouseElement.setOpen(!mouseElement.getOpen());
          mouseState = mouseIdle; /* Needed to clear indicator */
          drawIndicator();
          /*
           * We need to do this to update the scroll bar constants, but it
           * causes the display to flash.
           */
          outlinePanel.resetScrollbars();
        }
        break;
      case mouseInText:
        hiliteTextArea(false);
        if (mouseInRect) {
          if (selected != mouseElement) {
            selectElement(mouseElement);
          } else if (selected != null && event.getClickCount() == 2
              && (selected.getChild() == null)) {
            ActionEvent aevt = new ActionEvent(this,
                ActionEvent.ACTION_PERFORMED, "", CLICK_SELECT);
            processEvent(aevt);
          }
        }
        break;
      default:
        throw new RuntimeException("Bogus mouse state");
    }
    mouseState = mouseIdle;
    repaint();
  }

  /**
   * Search the entire list of elements for a specific name.
   * 
   * @param nameToFind
   *          The name to search for.
   * @param ignoreCase
   *          True for case-insensitive comparison.
   * @return The found element or null. To do: make this into an enumeration so
   *         the caller can provide a more intelligent comparison algorithm.
   */
  public OutlineElement findElement(String nameToFind, boolean ignoreCase) {
    return (findElement(nameToFind, ignoreCase, true));
  }

  /**
   * Search the entire list of elements for a specific name.
   * 
   * @param nameToFind
   *          The name to search for.
   * @param ignoreCase
   *          True for case-insensitive comparison.
   * @param searchAllElements
   *          True to search both visible and invisible elements.
   * @return The found element or null.
   */
  public OutlineElement findElement(String nameToFind, boolean ignoreCase,
      boolean searchAllElements) {
    return (findElement(root, nameToFind, ignoreCase, searchAllElements));
  }

  private OutlineElement findElement(OutlineElement thisElement,
      String nameToFind, boolean ignoreCase, boolean searchAllElements) {
    if (thisElement == null) {
      return (null);
    }
    String thisName = thisElement.getName();
    if (ignoreCase) {
      if (nameToFind.equalsIgnoreCase(thisName)) {
        return (thisElement);
      } else if (nameToFind.equals(thisName)) {
        return (thisElement);
      }
    }
    if (thisElement.getChild() != null
        && (searchAllElements || thisElement.getOpen())) {
      OutlineElement result = findElement(thisElement.getChild(), nameToFind,
          ignoreCase, searchAllElements);
      if (result != null) {
        return (result);
      }
    }
    if (thisElement.getSibling() != null) {
      OutlineElement result = findElement(thisElement.getSibling(), nameToFind,
          ignoreCase, searchAllElements);
      return (result);
    } else {
      return (null);
    }
  }

  /**
   * Return the colorSelected flag.
   */
  boolean getColorSelected() {
    return (colorSelected);
  }

  public Dimension getMinimumSize() {
    Dimension d = new Dimension();
    if (root == null) {
      d.height = getFontMetrics(getFont()).getHeight();
      d.width = getFontMetrics(getFont()).getHeight();
    } else {
      d.height = root.thisElementHeight();
      d.width = root.thisElementWidth(0);
    }
    return (d);
  }

  /**
   * Using the magic of recursion, compute the y-coordinate of the element the
   * user just clicked on.
   * 
   * @return true if we found the desired element.
   */
  private boolean getMouseRect(OutlineElement thisElement) {
    if (thisElement == mouseElement) {
      return (true);
    } else {
      mouseRect.y += thisElement.thisElementHeight();
      if (thisElement.getOpen() && thisElement.getChild() != null) {
        if (getMouseRect(thisElement.getChild())) {
          return (true);
        }
      }
      if (thisElement.getSibling() != null) {
        return (getMouseRect(thisElement.getSibling()));
      }
    }
    return (false);
  }

  public Dimension getPreferredSize() {
    if (root == null) {
      return (getMinimumSize());
    } else {
      Dimension d = new Dimension();
      d.height = root.getHeight();
      d.width = root.getMaxElementWidth(0, 0);
      return (d);
    }
  }

  /**
   * Return the name of the selected object, if any.
   */
  public String getSelectedName() {
    if (selected == null) {
      return (null);
    } else {
      return (selected.getName());
    }
  }

  /**
   * Return the value of the selected object, if any.
   */
  public Object getSelectedValue() {
    if (selected == null) {
      return (null);
    } else {
      return (selected.getValue());
    }
  }

  /**
   * Hilite the text area when the user clicks in it. This is pretty crude, but
   * I don't want to mess with AWT's limited clipRect support. (You can't save
   * and restore a clipRect.)
   */
  private void hiliteTextArea(boolean invert) {
    Graphics g = getGraphics();
    if (invert) {
      g.setColor(Color.black);
    } else {
      g.setColor(Color.white);
    }
    g.drawRect(mouseRect.x - 1, mouseRect.y - 1, mouseRect.width + 2,
        mouseRect.height + 2);
  }

  public void mouseClicked(MouseEvent event) {
  }

  /*
   * MouseMotionListener handlers.
   */
  public void mouseDragged(MouseEvent event) {
    if (mouseState != mouseIdle) {
      continueMouseTrack(event);
    }
  }

  public void mouseEntered(MouseEvent event) {
  }

  public void mouseExited(MouseEvent event) {
  }

  public void mouseMoved(MouseEvent event) {
  }

  /*
   * MouseListener handlers.
   */
  public void mousePressed(MouseEvent event) {
    if (mouseState != mouseIdle) {
      mouseReleased(event);
    }
    startMouseTrack(event);
  }

  public void mouseReleased(MouseEvent event) {
    if (mouseState != mouseIdle) {
      endMouseTrack(event);
    }
  }

  /**
   * Repaint the Canvas.
   * 
   * @param g
   *          The graphics context.
   */
  public void paint(Graphics g) {
    Dimension d = getSize();
    g.setColor(Color.black);
    g.drawRect(0, 0, d.width, d.height);
    Point scrollPoint = outlinePanel.getScrollPosition();
    g.translate(-scrollPoint.x, -scrollPoint.y);
    g.setColor(getForeground());
    if (root != null) {
      paintEachElement( /* Recursively paint elements */
      g, /* Graphics context */
      root, /* Starting at the root */
      0, /* X position */
      0, /* Y position */
      0 /* Initial indent */
      );
    }
  }

  /**
   * Display this element and its children and siblings (internal).
   * 
   * @param g
   *          The graphics context.
   * @param indent
   *          The indentation level
   * @return The y-value for the next element
   */
  private int paintEachElement(Graphics g, OutlineElement element, int x,
      int y, int indent) {
    int height = element.thisElementHeight();
    int iconWidth = element.getIndicatorWidth();
    int iconHeight = element.getIndicatorHeight();
    drawIndicator(g, element, x, y, false);
    Color color = g.getColor();
    g.setFont(element.getFont());
    if (element.selected && getColorSelected()) {
      g.setColor(Color.red);
    }
    /* System.out.println(y + ": " + element.getName()); */
    element.drawElement(g, x + element.getIndicatorWidth()
        + indicatorSeparation + (columnIndent * indent), y);
    y += height;
    g.setColor(color);
    OutlineElement child = element.getChild();
    if (element.open && child != null) {
      y = paintEachElement(g, child, x, y, indent + 1);
    }
    OutlineElement sibling = element.getSibling();
    if (sibling != null) {
      y = paintEachElement(g, sibling, x, y, indent);
    }
    return (y);
  }

  protected void processActionEvent(ActionEvent aevt) {
    if (actionListener != null) {
      actionListener.actionPerformed(aevt);
    }
  }

  protected void processEvent(AWTEvent event) {
    if (event instanceof ActionEvent) {
      processActionEvent((ActionEvent) event);
      return;
    }
    super.processEvent(event);
  }

  public void removeActionListener(ActionListener listener) {
    actionListener = AWTEventMulticaster.remove(actionListener, listener);
  }

  /**
   * Select a specific element
   * 
   * @param newSelected
   *          the OutlineElement to select or null to clear selections.
   *          selectElement will post deselection and selection events.
   */
  public void selectElement(OutlineElement newSelected) {
    if (selected != newSelected) {
      if (selected != null) {
        selected.setSelected(false);
        ActionEvent aevt = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
            "", LIST_DESELECT);
        processEvent(aevt);
      }
      selected = newSelected;
      if (selected != null) {
        selected.setSelected(true);
        ActionEvent aevt = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
            "", LIST_SELECT);
        processEvent(aevt);
      }
    }
  }

  /**
   * Start tracking the mouse when the user clicks in the OutlineCanvas. If the
   * click is in an element's display area, enter either the button-tracking or
   * text tracking sequence. This continues (at continueMouseTrack) until the
   * user releases the mouse.
   */
  public void startMouseTrack(MouseEvent event) {
    int x = event.getX();
    int y = event.getY();
    Point scrollPoint = outlinePanel.getScrollPosition();
    mouseElement = root.findElement(scrollPoint.x + x, scrollPoint.y + y);
    if (mouseElement != null) {
      /*
       * mouseRect is the bounds of the element under the mouse in display
       * coordinate space. This uses a private, recursive, method that scans
       * through the currently expanded element list, incrementing the y-value
       * as each element is passed.
       */
      mouseRect.x = -scrollPoint.x;
      mouseRect.y = -scrollPoint.y;
      getMouseRect(root);
      int indicatorSize = mouseElement.getIndicatorHeight();
      mouseInRect = true;
      if (mouseElement.getChild() != null && x <= indicatorSize + indicatorSlop) {
        /*
         * This element has children and the user clicked in the indicator area,
         * so track the button click.
         */
        mouseState = mouseInButton;
        mouseRect.x = 0;
        mouseRect.width = indicatorSize + indicatorSlop;
        mouseRect.height = indicatorSize + indicatorSlop;
        drawIndicator();
      } else if (canSelectText && mouseElement.getChild() == null) {
        /*
         * This click is in the text area. Note that I've arbitrarily decided
         * not to allow selecting "heading" entries. This ought to be under user
         * control.
         */
        mouseState = mouseInText;
        mouseRect.x = indicatorSize;
        mouseRect.width = getSize().width - indicatorSize;
        mouseRect.height = mouseElement.thisElementHeight();
        mouseState = mouseInText;
        hiliteTextArea(true);
      }
    }
  }

  /**
   * Cut down on flashing when update is called.
   */
  public void update(Graphics g) {
    paint(g);
  }
}
