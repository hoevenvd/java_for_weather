package org.tom.weather.astro;

/**
 * InfoDialog displays a modal dialog.<p>
 *
 * Copyright &copy; 1996 Martin Minow. All Rights Reserved.<p>
 *
 * Permission to use, copy, modify, and redistribute this software and its
 * documentation for personal, non-commercial use is hereby granted provided that
 * this copyright notice and appropriate documentation appears in all copies. This
 * software may not be distributed for fee or as part of commercial, "shareware,"
 * and/or not-for-profit endevors including, but not limited to, CD-ROM collections,
 * online databases, and subscription services without specific license.<p>
 *
 * @author <a href="mailto:minow@merrymeet.com">Martin Minow</a>
 * @version 1.0
 * 1996.07.24
 * 1997.04.12 Make sure preferred size doesn't exceed screen size.
 * Set tabs every 4 characters.
 */
import java.util.*;
import java.awt.*;
import java.awt.event.*;
/**
 * InfoDialog implements a simple dialog.
 */
import java.util.*;
import java.awt.*;

class InfoDialog extends Dialog implements ActionListener, FocusListener {
  protected Button okButton = new Button(" OK ");

  public InfoDialog(Frame frame, String title, String text, boolean isModal) {
    super(frame, title, isModal);
    setResizable(true);
    okButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
    this.setLayout(new BorderLayout(16, 16));
    this.setFont(new Font("SansSerif", Font.PLAIN, 12));
    this.setBackground(Color.white);
    TextArea textArea = new TextArea(text);
    textArea.setEditable(false);
    this.add("Center", textArea);
    /*
     * Create the OK button in a separate panel so it doesn't stretch across the
     * entire bottom of the window.
     */
    Panel p = new Panel();
    p.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 16));
    p.add(okButton);
    this.add("South", p);
    okButton.addActionListener(this);
    addFocusListener(this);
    pack(); /* Resize and construct the window */
    setVisible(true);
  }

  public void actionPerformed(ActionEvent event) {
    if (event.getSource() == okButton) {
      setVisible(false);
      dispose();
    }
  }

  public void focusGained(FocusEvent event) {
    okButton.requestFocus();
  }

  public void focusLost(FocusEvent event) {
    okButton.transferFocus();
  }

  public Dimension getPreferredSize() {
    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    d.width = Math.min(d.width - 4, 324);
    d.height = Math.min(d.height - 32, 242);
    return (d);
  }
}
