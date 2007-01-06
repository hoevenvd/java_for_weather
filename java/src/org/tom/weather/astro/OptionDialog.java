package org.tom.weather.astro;

/**
 * OptionDialog allows configuring the SunClock applet.<p>
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
 * @version 1.0
 * 1996.07.24
 * 1997.04.12 Make sure preferred size doesn't exceed screen size.
 */
// package Classes;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

/**
 * The SunClock configuration dialog. It implements Observer so that, if the
 * user clicks on the map, the dialog will update its notion of latitude and
 * longitude.
 */
class OptionDialog extends Dialog implements ActionListener, /*
                                                               * Buttons and
                                                               * checkboxes
                                                               */
FocusListener, /* Transfers focus to the OK button */
ItemListener, /* For the TimeZone choice menu */
Observer, /* Detect clicks in the world map */
Constants /* Our private constant database */
{
  private Button okButton = new Button(" OK ");
  private Button cancelButton = new Button(" Cancel ");
  private Button findButton = new Button(" Find ");
  private CityList cityList;
  private TextField locationName = new TextField();
  private TextField latDegrees = new TextField();
  private TextField latMinutes = new TextField();
  private TextField longDegrees = new TextField();
  private TextField longMinutes = new TextField();
  private Choice timeZoneChoice = new Choice();
  private Label timeZoneInfo = new Label();
  private Checkbox latNorthCheck = new Checkbox("North");
  private Checkbox longEastCheck = new Checkbox("East");
  private Checkbox silentCheck = new Checkbox("Supress hour chime");
  private SunClockData sunClockData;
  private TextArea textArea;
  private double latitude;
  private double longitude;
  private TimeZone timeZone;
  private String location;

  public OptionDialog(SunClockData sunClockData) {
    super(getFrame(sunClockData.getSunClockApplet()), false); /* Non-modal */
    this.sunClockData = sunClockData;
    buildTimeZoneChoice();
    setTimeZone(sunClockData.getTimeZone().getID(), true);
    timeZoneChoice.addItemListener(this);
    setBackground(Color.white);
    setFont(new Font("SansSerif", Font.PLAIN, 12));
    setLayout(new GridBagLayout());
    setResizable(true);
    cityList = new CityList();
    makeField(cityList, 0, 0, 6, 1, GridBagConstraints.BOTH, 1.0, 1.0);
    makeField(locationName, 0, 1, 5, 1, GridBagConstraints.HORIZONTAL, 1.0, 0.0);
    makeField(findButton, 5, 1, 1);
    makeDialogField(2, "Latitude: ", "Deg", "Min", latDegrees, latMinutes,
        latNorthCheck);
    makeDialogField(3, "Longitude: ", "Deg", "Min", longDegrees, longMinutes,
        longEastCheck);
    makeField(timeZoneChoice, 0, 4, 2);
    makeField(timeZoneInfo, 2, 4, GridBagConstraints.REMAINDER, 1,
        GridBagConstraints.HORIZONTAL, 1.0, 0.0);
    makeField(silentCheck, 0, 5, GridBagConstraints.REMAINDER);
    makeField(cancelButton, 2, 5, 1);
    makeField(okButton, 5, 5, 1);
    locationName.setText(sunClockData.getLocationName());
    setField(sunClockData.getLatitude(), latDegrees, latMinutes, latNorthCheck);
    setField(sunClockData.getLongitude(), longDegrees, longMinutes,
        longEastCheck);
    /*
     * Setup the action linkages.
     */
    sunClockData.addObserver(this);
    cityList.addActionListener(this);
    okButton.addActionListener(this);
    cancelButton.addActionListener(this);
    /*
     * Finally, resize and display the dialog
     */
    pack();
    setVisible(true);
  }

  /**
   * Implement the ActionListener interface.
   */
  public void actionPerformed(ActionEvent event) {
    Object source = event.getSource();
    if (source == okButton) {
      try {
        double latitude = textValue(latDegrees, latMinutes, latNorthCheck
            .getState(), 90.0);
        double longitude = textValue(longDegrees, longMinutes, longEastCheck
            .getState(), 180.0);
        sunClockData.setLocation(locationName.getText(), latitude, longitude,
            timeZone);
        sunClockData.setSilentFlag(silentCheck.getState());
        sunClockData.deleteObserver(this);
        setVisible(false);
        dispose();
      } catch (NumberFormatException e) {
        InfoDialog dialog = new InfoDialog(getFrame(), "Number format error", e
            .toString(), true);
      }
    } else if (source == cancelButton) {
      sunClockData.deleteObserver(this);
      setVisible(false);
      dispose();
    } else if (source == findButton) {
      OutlineElement element = cityList.findElement(locationName.getText(),
          true);
      if (element != null) {
        cityList.selectElement(element); /* This sends us a selection event */
      }
    } else if (source == cityList.getOutlineComponent()) {
      int action = event.getModifiers();
      switch (action) {
        case OutlineComponent.LIST_SELECT:
        case OutlineComponent.CLICK_SELECT:
          double latitude = cityList.getLatitude();
          double longitude = cityList.getLongitude();
          setTimeZone(cityList.getTimeZoneID(), true);
          locationName.setText(cityList.getLocationName());
          setField(latitude, latDegrees, latMinutes, latNorthCheck);
          setField(longitude, longDegrees, longMinutes, longEastCheck);
          break;
        default: /* OutlineComponent.LIST_DESELECT */
          break;
      }
    } else {
      System.out.println("OptionDialog: strange action event from "
          + source.getClass().getName());
    }
  }

  private String augmentedTimeZoneString(String timeZoneID) {
    return (Astro.getTimeZoneInfo(timeZoneID, sunClockData.getJavaDate()));
  }

  private void buildTimeZoneChoice() {
    /*
     * Instantiate the timezone choice component.
     */
    String[] zones = TimeZone.getAvailableIDs();
    for (int i = 0; i < zones.length; i++) {
      timeZoneChoice.add(augmentedTimeZoneString(zones[i]));
    }
  }

  public void focusGained(FocusEvent event) {
    okButton.requestFocus();
  }

  public void focusLost(FocusEvent event) {
    okButton.transferFocus();
  }

  public Frame getFrame() {
    return (getFrame(sunClockData.getSunClockApplet()));
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

  /**
   * Implement the ItemListener interface.
   */
  public void itemStateChanged(ItemEvent event) {
    Object source = event.getSource();
    if (source == timeZoneChoice) {
      switch (event.getStateChange()) {
        case ItemEvent.SELECTED:
          String newZone = timeZoneChoice.getSelectedItem();
          if (newZone != null) {
            int end = newZone.charAt(' ');
            if (end > 0) {
              newZone = newZone.substring(end);
            }
            setTimeZone(newZone, false);
          }
          break;
        case ItemEvent.DESELECTED:
          break;
        default:
      }
    }
  }

  /**
   * Constrain a dialog element. The label goes in row 0, the text in row 1
   * 
   * @param component
   *          The dialog element (TextField)
   * @param String
   *          The dialog label
   * @param gridX
   *          The position of the dialog.
   */
  private void makeDialogField(int row, String rowLabel, String field1Label,
      String field2Label, TextField textField1, TextField textField2,
      Checkbox checkbox) {
    makeField(rowLabel, 0, row, 1, 1, GridBagConstraints.NONE, 0.0, 0.0);
    makeField(textField1, 1, row, 1, 1, GridBagConstraints.HORIZONTAL, 1.0, 0.0);
    makeField(field1Label, 2, row, 1, 1, GridBagConstraints.NONE, 0.0, 0.0);
    makeField(textField2, 3, row, 1, 1, GridBagConstraints.HORIZONTAL, 1.0, 0.0);
    makeField(field2Label, 4, row, 1, 1, GridBagConstraints.NONE, 0.0, 0.0);
    makeField(checkbox, 5, row, 1, 1, GridBagConstraints.NONE, 0.0, 0.0);
  }

  /**
   * Add a component to the dialog.
   * 
   * @param arg
   *          The object to add.
   * @param gridx
   *          The horizontal grid location
   * @param gridy
   *          The vertical grid location
   * @param gridWidth
   *          The component's width in grid cells
   * @param gridHeight
   *          The component's height in grid cells fill Always
   *          GridBagConstraints.NONE weightx Always 0.0 (no stretch width)
   *          weighty Always 0.0 (no stretch height)
   */
  private void makeField(Object arg, int gridx, int gridy, int gridwidth) {
    makeField(arg, gridx, gridy, gridwidth, 1, GridBagConstraints.NONE, 0.0,
        0.0);
  }

  /**
   * Add a component to the dialog.
   * 
   * @param arg
   *          The object to add.
   * @param gridx
   *          The horizontal grid location
   * @param gridy
   *          The vertical grid location
   * @param gridWidth
   *          The component's width in grid cells
   * @param gridHeight
   *          The component's height in grid cells
   * @param fill
   *          The GridBagConstraints fill parameter
   * @param weightx
   *          The stretch width
   * @param weighty
   *          The stretch height
   */
  private void makeField(Object arg, int gridx, int gridy, int gridwidth,
      int gridheight, int fill, double weightx, double weighty) {
    GridBagConstraints c = new GridBagConstraints();
    Component component;
    c.gridx = gridx;
    c.gridy = gridy;
    c.gridwidth = gridwidth;
    c.gridheight = gridheight;
    c.weightx = weightx;
    c.weighty = weighty;
    c.fill = fill;
    c.insets = new Insets(2, 2, 2, 2);
    if (arg instanceof String) {
      component = new Label((String) arg);
    } else if (arg instanceof Checkbox) {
      component = (Component) arg;
      c.anchor = GridBagConstraints.WEST;
    } else {
      component = (Component) arg;
    }
    add(component);
    ((GridBagLayout) getLayout()).setConstraints(component, c);
  }

  public Dimension getPreferredSize() {
    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    d.width = Math.min(d.width - 4, 376);
    d.height = Math.min(d.height - 32, 400);
    return (d);
  }

  private void setField(double value, TextField intField, TextField fracField,
      Checkbox plusCheckbox) {
    double absValue = Math.abs(value);
    double intPart = Math.floor(absValue);
    double fracPart = (absValue - intPart) * 60.0;
    intField.setText(Format.format(intPart, 0, 0));
    fracField.setText(Format.format(fracPart, 0, 0));
    plusCheckbox.setState(value > 0.0);
  }

  private void setTimeZone(String timeZoneID, boolean updateChoice) {
    TimeZone newZone = TimeZone.getTimeZone(timeZoneID);
    if (newZone == null) {
      newZone = TimeZone.getDefault();
    }
    timeZone = newZone;
    String tzString = augmentedTimeZoneString(timeZone.getID());
    if (updateChoice) {
      timeZoneChoice.select(tzString);
    }
    if (timeZone.useDaylightTime() == false) {
      tzString = tzString + " (no DST)";
    }
    timeZoneInfo.setText(tzString);
  }

  private double textValue(TextField textField, double maxValue) {
    double result;
    try {
      result = new Double(textField.getText()).doubleValue();
      if (Math.abs(result) >= maxValue) {
        throw new NumberFormatException("Exceeded max value (" + result + ")");
      }
    } catch (NumberFormatException e) {
      InfoDialog d = new InfoDialog(getFrame(sunClockData.getSunClockApplet()),
          "Incorrect Entry", "Please enter a number between "
              + Format.format(-maxValue, 0, 0) + " and "
              + Format.format(maxValue, 0, 0) + ". \"" + textField.getText()
              + "\" invalid: " + e, true);
      d.show();
      throw (e);
    }
    return (result);
  }

  private double textValue(TextField intField, TextField fracField,
      boolean isPositive, double maxValue) {
    double value = textValue(intField, maxValue);
    double fraction = textValue(fracField, 60.0);
    value += fraction / 60.0;
    if (isPositive == false) {
      value = (-value);
    }
    return (value);
  }

  /**
   * Update is called by SunClockData when someone updates the data.
   */
  public void update(Observable observable, Object object) {
    if (observable == sunClockData) {
      int whatChanged = ((Integer) object).intValue();
      if ((whatChanged & LOCATION_CHANGED) != 0) {
        setField(sunClockData.getLatitude(), latDegrees, latMinutes,
            latNorthCheck);
        setField(sunClockData.getLongitude(), longDegrees, longMinutes,
            longEastCheck);
      }
    }
  }
}
