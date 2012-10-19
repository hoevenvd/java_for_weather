package org.tom.weather.astro;
// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SimpleTimeBarCanvas.java

import java.awt.*;
import java.util.Date;

public class SimpleTimeBarCanvas extends Canvas
{

    public SimpleTimeBarCanvas(SunriseCanvas sunrisecanvas)
    {
        instant = new Date();
        sunriseCanvas = sunrisecanvas;
    }

    public Dimension preferredSize()
    {
        FontMetrics fontmetrics = getFontMetrics(getFont());
        Dimension dimension = new Dimension(sunriseCanvas.size().width + fontmetrics.charWidth('0') * 10, fontmetrics.getHeight());
        return dimension;
    }

    public Dimension getPreferredSize()
    {
        return preferredSize();
    }

    public synchronized void paint(Graphics g)
    {
        if(sunriseCanvas.bounds() != null)
        {
            FontMetrics fontmetrics = g.getFontMetrics();
            instant = sunriseCanvas.getDate();
            theTimeAtGMT = (double)(instant.getTime() % 0x5265c00L) / 3600000D;
            Rectangle rectangle = sunriseCanvas.bounds();
            int i = rectangle.width;
            int j = i / 6;
            int k = rectangle.x - bounds().x;
            if(fontmetrics.stringWidth(" 00:00") < j)
            {
                k -= fontmetrics.stringWidth("00:00") / 2;
                for(int l = 0; l <= i; l += j)
                {
                    double d = xPixelToTime(l);
                    g.drawString(timeString(d), l + k, fontmetrics.getAscent());
                }

            }
        }
    }

    private double xPixelToTime(int i)
    {
        try
        {
            int j = sunriseCanvas.bounds().width;
            double d = (double)(i - j / 2) / (double)j;
            double d1;
            for(d1 = theTimeAtGMT + d * 24D; d1 >= 24D; d1 -= 24D);
            for(; d1 < 0.0D; d1 += 24D);
            return d1;
        }
        catch(Exception exception)
        {
            return 0.0D;
        }
    }

    public static String timeString(double d)
    {
        int i = (int)Math.floor(d);
        d = (d - (double)i) * 60D;
        int j = (int)(d + 0.5D);
        if(j >= 60)
        {
            i++;
            j -= 60;
        }
        if(i >= 24)
            i -= 24;
        StringBuffer stringbuffer = new StringBuffer();
        if(i < 10)
            stringbuffer.append('0');
        stringbuffer.append((new Integer(i)).toString());
        stringbuffer.append(':');
        if(j < 10)
            stringbuffer.append('0');
        stringbuffer.append((new Integer(j)).toString());
        return stringbuffer.toString();
    }

    protected SunriseCanvas sunriseCanvas;
    protected Date instant;
    protected double theTimeAtGMT;
}
