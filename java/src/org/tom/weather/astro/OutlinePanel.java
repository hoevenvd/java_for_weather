package org.tom.weather.astro;

/**
 * OutlinePanel is the parent of an OutlineComponent and handles its scrollbars.
 * (ScrollPane is causing some problems.)
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
import java.awt.event.*;

public class OutlinePanel extends Panel implements AdjustmentListener {
  protected OutlineComponent outlineComponent;
  private Scrollbar hScroll = new Scrollbar(Scrollbar.HORIZONTAL);
  private Scrollbar vScroll = new Scrollbar(Scrollbar.VERTICAL);

  public OutlinePanel() {
    this(false, false);
  }

  public OutlinePanel(boolean canSelectText, boolean colorSelected) {
    outlineComponent = new OutlineComponent(this, canSelectText, colorSelected);
    /*
     * Component-specific
     */
    setLayout(new BorderLayout());
    add(outlineComponent, BorderLayout.CENTER);
    add(hScroll, BorderLayout.SOUTH);
    add(vScroll, BorderLayout.EAST);
    hScroll.addAdjustmentListener(this);
    vScroll.addAdjustmentListener(this);
    validate();
    /*
     * Initial data add fails if there isn't a font defined.
     */
    outlineComponent.setFont(new Font("SansSerif", Font.PLAIN, 10));
    resetScrollbars();
  }

  public synchronized void addActionListener(ActionListener listener) {
    outlineComponent.addActionListener(listener);
  }

  public OutlineElement addText(int depth, String name) {
    return (addText(depth, name, null));
  }

  /**
   * Add one line of text at the indicated depth.
   * 
   * @param depth
   *          The inset depth of this text element. This must be the current
   *          depth, the current depth + 1, or a value less than the current
   *          depth. (This should be redone so the caller need not manage the
   *          depth explicitly.)
   * @param text
   *          The line of text to add.
   * @throws RuntimeException
   *           if the depth is incorrect.
   */
  public OutlineElement addText(int depth, String name, Object value) {
    OutlineElement element = outlineComponent.addText(depth, name, value);
    return (element);
  }

  /*
   * Scrollbar management
   */
  public void adjustmentValueChanged(AdjustmentEvent event) {
    resetScrollbars();
    outlineComponent.repaint();
  }

  public void dumpTree() {
    outlineComponent.dumpTree();
  }

  /**
   * Search the entire list of elements for a specific name.
   * 
   * @param nameToFind
   *          The name to search for.
   * @param ignoreCase
   *          True for case-insensitive comparison.
   * @return The found element or null.
   */
  public OutlineElement findElement(String nameToFind, boolean ignoreCase) {
    return (outlineComponent.findElement(nameToFind, ignoreCase));
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
    return (outlineComponent.findElement(nameToFind, ignoreCase,
        searchAllElements));
  }

  /**
   * Return the OutlineComponent (needed to catch ActionEvents).
   */
  public OutlineComponent getOutlineComponent() {
    return (outlineComponent);
  }

  public Point getScrollPosition() {
    return (new Point(hScroll.getValue(), vScroll.getValue()));
  }

  public String getSelectedName() {
    return (outlineComponent.getSelectedName());
  }

  public Object getSelectedValue() {
    return (outlineComponent.getSelectedValue());
  }

  public void removeActionListener(ActionListener listener) {
    outlineComponent.removeActionListener(listener);
  }

  public void resetScrollbars() {
    Dimension rect = outlineComponent.getSize();
    Dimension size = outlineComponent.getPreferredSize();
    FontMetrics fm = getFontMetrics(outlineComponent.getFont());
    int lineHeight = fm.getHeight();
    hScroll.setValues(hScroll.getValue(), /* Value */
    rect.width, /* Visible */
    0, /* Minimum */
    size.width /* Maximum */
    );
    hScroll.setBlockIncrement((rect.width * 7) / 8);
    hScroll.setUnitIncrement(fm.getMaxAdvance());
    int visible = rect.height / lineHeight;
    vScroll.setValues(vScroll.getValue(), /* Value */
    rect.height, /* Visible */
    0, /* Minimum */
    size.height /* Maximum */
    );
    vScroll.setBlockIncrement(Math.max(lineHeight, rect.height - lineHeight));
    vScroll.setUnitIncrement(lineHeight);
    if (false) {
      System.out.println("hor" + ": unit = " + hScroll.getUnitIncrement()
          + ", page = " + hScroll.getBlockIncrement() + ", value = "
          + hScroll.getValue() + ", max = " + hScroll.getMaximum() + ", vis = "
          + hScroll.getVisibleAmount());
      System.out.println("ver" + ", unit = " + vScroll.getUnitIncrement()
          + ", page = " + vScroll.getBlockIncrement() + ", value = "
          + vScroll.getValue() + ", max = " + vScroll.getMaximum() + ", vis = "
          + vScroll.getVisibleAmount());
    }
  }

  /**
   * Select a specific element
   * 
   * @param newSelected
   *          the TwistElement to select or null to clear selections.
   */
  public void selectElement(OutlineElement newSelected) {
    outlineComponent.selectElement(newSelected);
  }

  /*
   * Compute the dimensions of the scrollbars.
   */
  public void setScrollPosition(int hValue, int vValue) {
    hScroll.setValue(hValue);
    vScroll.setValue(vValue);
    resetScrollbars();
  }
}
