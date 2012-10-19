package org.tom.weather.astro;
// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SimpleSunApplet.java

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.util.Date;

public class SimpleSunApplet extends Applet
    implements Runnable
{

    public SimpleSunApplet()
    {
        currentHour = (new Date()).getHours();
        displayFont = new Font("Helvetica", 0, 10);
        test = false;
        testDate = (new Date()).getTime();
    }

    public void init()
    {
        showStatus("Copyright 1996-98 Martin Minow. All Rights Reserved, version 1.0.1");
        try
        {
            new Thread();
            Thread.sleep(100L);
        }
        catch(Exception exception) { }
        showStatus("");
        setFont(displayFont);
        sunriseCanvas = new SunriseCanvas(getImage(getCodeBase(), "EarthImage270.gif"));
        timeBarCanvas = new SimpleTimeBarCanvas(sunriseCanvas);
        getBackgroundColor();
        Panel panel = new Panel();
        GridBagLayout gridbaglayout = new GridBagLayout();
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 0;
        gridbagconstraints.gridwidth = 1;
        gridbagconstraints.gridheight = 1;
        gridbagconstraints.fill = 2;
        gridbagconstraints.anchor = 15;
        gridbagconstraints.insets = new Insets(0, 0, 0, 0);
        gridbaglayout.setConstraints(sunriseCanvas, gridbagconstraints);
        panel.add(sunriseCanvas);
        gridbagconstraints.gridy = 1;
        gridbagconstraints.anchor = 11;
        gridbaglayout.setConstraints(timeBarCanvas, gridbagconstraints);
        panel.add(timeBarCanvas);
        setLayout(new BorderLayout());
        add("Center", panel);
        Label label = new Label("Time values are referenced to GMT, not local time", 1);
        validate();
    }

    public void paint(Graphics g)
    {
        g.setColor(getBackground());
        g.fillRect(0, 0, size().width - 1, size().height - 1);
        g.setColor(Color.black);
        g.drawRect(0, 0, size().width - 1, size().height - 1);
    }

    public void start()
    {
        if(runner == null)
        {
            runner = new Thread(this);
            runner.start();
        }
    }

    public void run()
    {
        showStatus("");
        while(runner != null) 
        {
            Date date;
            if(test)
            {
                date = new Date(testDate);
                testDate += 0x53417a0L;
            } else
            {
                date = new Date();
                int i = date.getHours();
                if(hourBell != null && i != currentHour)
                {
                    currentHour = i;
                    hourBell.play();
                }
            }
            sunriseCanvas.setDate(date);
            timeBarCanvas.repaint();
            showStatus(date.toGMTString());
            long l;
            if(test)
                l = 500L;
            else
                for(l = 60000L - System.currentTimeMillis() % 60000L; l < 30000L; l += 60000L);
            try
            {
                SimpleSunApplet _tmp = this;
                Thread.sleep(l);
            }
            catch(InterruptedException interruptedexception) { }
        }
    }

    public void stop()
    {
        runner = null;
        hourBell = null;
    }

    public void destroy()
    {
        stop();
    }

    public String getAppletInfo()
    {
        String s = getClass().getName() + ", " + "Copyright 1996-98 Martin Minow. All Rights Reserved" + ", " + "mailto:minow@merrymeet.com";
        return s;
    }

    public String[][] getParameterInfo()
    {
        return parameterInfo;
    }

    public void getBackgroundColor()
    {
        String s = getParameter("bgcolor");
        if(s != null)
        {
            if(s.charAt(0) == '#')
                s = s.substring(1);
            try
            {
                int i = Integer.parseInt(s, 16);
                setBackground(new Color(i));
            }
            catch(NumberFormatException numberformatexception) { }
        }
    }

    public static final String copyright = "Copyright 1996-98 Martin Minow. All Rights Reserved";
    public static final String author = "mailto:minow@merrymeet.com";
    public static final String name = "SimpleSunApplet";
    public static final String version = "1.0.1";
    public static final String imageName = "EarthImage270.gif";
    public static final String chimeName = "DecClockTower.au";
    public static final String parameterInfo[][] = {
        {
            "bgcolor", "color", "The background color, #RRGGBB (in hex)"
        }
    };
    protected SimpleTimeBarCanvas timeBarCanvas;
    protected SunriseCanvas sunriseCanvas;
    protected AudioClip hourBell;
    protected int currentHour;
    protected Font displayFont;
    protected Thread runner;
    protected boolean test;
    protected long testDate;

}
