package org.tom.weather.astro;

/**
 * OutlineDialog displays a modal dialog that contains an OutlineComponent.<p>
 *
 * Copyright &copy; 1996-1998 Martin Minow. All Rights Reserved.<p>
 *
 * Permission to use, copy, modify, and redistribute this software and its
 * documentation for personal, non-commercial use is hereby granted provided that
 * this copyright notice and appropriate documentation appears in all copies. This
 * software may not be distributed for fee or as part of commercial, "shareware,"
 * and/or not-for-profit endevors including, but not limited to, CD-ROM collections,
 * online databases, and subscription services without specific license.<p>
 *
 * @author <a href="mailto:minow@merrymeet.com">Martin Minow</a>
 * @version 2.0
 * Set tabs every 4 characters.
 */
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * OutlineDialog implements a dialog containing a hierarchical text list. The
 * string is contains a sequence of lines that must have the following format:
 * 
 * <pre>
 * 	&lt;number&gt;.&lt;text&gt;&lt;newline&gt;
 * </pre>
 * 
 * where <number> is the indentation level.
 */
class OutlineDialog extends Dialog implements ActionListener, WindowListener {
  protected OutlinePanel outlinePanel;
  protected Button okButton = new Button(" OK ");
  private Cursor cursor;

  /**
   * Construct the dialog for this component with the specified title. The
   * dialog will not be visible on exit. Call makeVisible() after adding the
   * dialog text.
   */
  public OutlineDialog(Component component, String title) {
    this(getFrame(component), title);
  }

  /**
   * Construct the dialog as a child of this component with the specified title.
   * The dialog will be visible on exit.
   */
  public OutlineDialog(Component component, String title, String text) {
    this(getFrame(component), title, text);
  }

  /**
   * Construct the dialog for this parent Frame with the specified title. The
   * dialog will not be visible on exit. Call makeVisible() after adding the
   * dialog text.
   */
  public OutlineDialog(Frame parent, String title) {
    super(parent, title, false); /* Non-modal dialog */
    cursor = getCursor();
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    setResizable(true);
    setBackground(Color.white);
    outlinePanel = new OutlinePanel(false, false);
    setFont(new Font("SansSerif", Font.PLAIN, 12));
    /*
     * Some browsers don't provide a close box in dialogs. Make sure that there
     * is a way to leave the dialog.
     */
    Panel okPanel = new Panel();
    okPanel.setLayout(new FlowLayout());
    okPanel.add(okButton);
    add("South", okPanel);
    add("Center", outlinePanel);
    okButton.addActionListener(this);
    addWindowListener(this);
  }

  /**
   * Construct the dialog for this parent Frame with the specified title. The
   * dialog will be visible on exit.
   */
  public OutlineDialog(Frame parent, String title, String text) {
    this(parent, title);
    okButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
    addText(text);
    makeDialogVisible();
  }

  public void actionPerformed(ActionEvent event) {
    if (event.getSource() == okButton) {
      setVisible(false);
      dispose();
    }
  }

  public void addText(String text) {
    StringTokenizer t = new StringTokenizer(text, "\t\n");
    try {
      while (t.hasMoreTokens()) {
        String depthString = t.nextToken();
        String thisLine = t.nextToken();
        int depth = Integer.parseInt(depthString);
        /*
         * \f signals fixed-width font. Ugly hack, sorry.
         */
        boolean fixedWidth = thisLine.charAt(0) == '\f';
        if (fixedWidth) {
          thisLine = thisLine.substring(1);
        }
        OutlineElement elem = outlinePanel.addText(depth, thisLine);
        if (fixedWidth) {
          int size = elem.getFont().getSize();
          elem.setFont(new Font("Courier", Font.PLAIN, size));
        }
      }
    } catch (NoSuchElementException e) {
      System.err.println("OutlineDialog text error: " + e);
    }
  }

  public static Frame getFrame(Component component) {
    Component c;
    for (c = component; c != null; c = c.getParent()) {
      if (c instanceof Frame) {
        break;
      }
    }
    if (c == null) {
      throw new RuntimeException("No frame for component");
    }
    return ((Frame) c);
  }

  public Dimension getMinimumSize() {
    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    d.width = Math.min(d.width - 4, 324);
    d.height = Math.min(d.height - 32, 242);
    return (d);
  }

  public Dimension getPreferredSize() {
    Dimension d = outlinePanel.getPreferredSize();
    d.height += okButton.getPreferredSize().height;
    d.height += 16;
    Dimension min = getMinimumSize();
    if (d.width < min.width) {
      d.width = min.width;
    }
    if (d.height < min.height) {
      d.height = min.height;
    }
    return (d);
  }

  /**
   * Complete the dialog initialization, making it visible and active.
   */
  public void makeDialogVisible() {
    pack(); /* Resize and construct the window */
    setVisible(true);
    setCursor(cursor);
  }

  public void setFont(Font font) {
    // super.setFont(font);
    outlinePanel.getOutlineComponent().setFont(font);
  }

  public void windowActivated(WindowEvent event) {
  }

  public void windowClosed(WindowEvent event) {
  }

  public void windowClosing(WindowEvent event) {
    setVisible(false);
    dispose();
  }

  public void windowDeactivated(WindowEvent event) {
  }

  public void windowDeiconified(WindowEvent event) {
  }

  public void windowIconified(WindowEvent event) {
  }

  public void windowOpened(WindowEvent event) {
  }
}
