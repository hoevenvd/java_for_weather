package org.tom.weather.astro;
// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SunriseCanvas.java

import java.awt.*;
import java.util.Date;

public class SunriseCanvas extends Canvas
{

    public SunriseCanvas(Image image)
    {
        instant = new Date();
        sunrisePixels = new SunrisePixels(image, this);
        imageWidth = sunrisePixels.getImageWidth();
        imageHeight = sunrisePixels.getImageHeight();
    }

    public void setDate(Date date)
    {
        instant = date;
        sunrisePixels.setDate(date);
        repaint();
    }

    public Date getDate()
    {
        return instant;
    }

    public void update(Graphics g)
    {
        paint(g);
    }

    public synchronized void paint(Graphics g)
    {
        Dimension dimension = super.size();
        int i = (dimension.width - imageWidth) / 2;
        int j = dimension.height - imageHeight;
        g.drawImage(sunrisePixels.getImage(), i, j, this);
    }

    public Dimension preferredSize()
    {
        return new Dimension(imageWidth, imageHeight);
    }

    public Dimension getPreferredSize()
    {
        return preferredSize();
    }

    public Dimension minimumSize()
    {
        return preferredSize();
    }

    public Dimension getMinimumSize()
    {
        return preferredSize();
    }

    protected SunrisePixels sunrisePixels;
    protected int imageWidth;
    protected int imageHeight;
    protected Date instant;
}
