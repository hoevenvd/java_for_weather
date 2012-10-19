package org.tom.weather.astro;
// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SunrisePixels.java

import java.awt.*;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.PrintStream;
import java.util.Date;

public class SunrisePixels
{

    public SunrisePixels(Image image, Component component)
    {
        riseSet = new double[2];
        twilight = new double[2];
        eventIndex = new int[5];
        eventDivisor = new int[5];
        latOffset = 0;
        longOffset = 0;
        MediaTracker mediatracker = new MediaTracker(component);
        mediatracker.addImage(image, 0);
        try
        {
            mediatracker.waitForAll();
        }
        catch(InterruptedException interruptedexception)
        {
            System.err.println("SunrisePixels: image load interrupted: " + interruptedexception);
        }
        imageWidth = image.getWidth(null);
        imageHeight = image.getHeight(null);
        latOffset = imageHeight / 8;
        longOffset = imageWidth / 8;
        mapPixels = new int[imageWidth * imageHeight];
        PixelGrabber pixelgrabber = new PixelGrabber(image, 0, 0, imageWidth, imageHeight, mapPixels, 0, imageWidth);
        try
        {
            pixelgrabber.grabPixels();
        }
        catch(InterruptedException interruptedexception1)
        {
            System.err.println("SunrisePixels: image conversion interrupted: " + interruptedexception1);
            mapPixels = null;
        }
        if((pixelgrabber.status() & 0x80) != 0)
        {
            System.err.println("SunrisePixels: image conversion failed");
            mapPixels = null;
        }
        resultPixels = new int[mapPixels.length];
        try
        {
            String s = System.getProperty("java.version");
            animationSupported = s.compareTo("1.1") >= 0;
        }
        catch(SecurityException securityexception)
        {
            animationSupported = false;
        }
        memoryImageSource = new MemoryImageSource(imageWidth, imageHeight, resultPixels, 0, imageWidth);
        if(animationSupported)
            memoryImageSource.setAnimated(true);
        sunriseImage = Toolkit.getDefaultToolkit().createImage(memoryImageSource);
        setDate(new Date());
    }

    public int getImageWidth()
    {
        return imageWidth;
    }

    public int getImageHeight()
    {
        return imageHeight;
    }

    public int[] getImagePixels()
    {
        return resultPixels;
    }

    public Image getImage()
    {
        return sunriseImage;
    }

    public void setDate(Date date1)
    {
        if(sunriseImage == null)
            return;
        date = date1;
        DATE = midnightMJD(date1);
        double d = (date1.getTime() / 60000L) % 1440L;
        timeOfDayGMT = mod((d + 720D) / 60D, 24D);
        int i = 0;
        for(int j = 0; j < imageHeight; j++)
        {
            latitude = pixelToLatitude(j);
            if(latitude >= 89.900000000000006D)
                latitude = 89.900000000000006D;
            else
            if(latitude <= -89.900000000000006D)
                latitude = -89D;
            nEvents = 0;
            riseSet(riseSet, -0.83333333333333337D);
            riseSet(twilight, -6D);
            if(isAbove(twilight) && isAbove(riseSet))
                store(imageWidth, 0);
            else
            if(isBelow(twilight) && isBelow(riseSet))
            {
                store(imageWidth, 3);
            } else
            {
                if(isEvent(twilight))
                {
                    store(timeToPixel(twilight[0]), 3);
                    if(isEvent(riseSet))
                    {
                        store(timeToPixel(riseSet[0]), 2);
                        store(timeToPixel(riseSet[1]), 0);
                    }
                    store(timeToPixel(twilight[1]), 2);
                } else
                {
                    store(timeToPixel(riseSet[0]), 2);
                    store(timeToPixel(riseSet[1]), 0);
                }
                store(imageWidth, eventDivisor[0]);
            }
            int k = 0;
            for(int l = 0; l < imageWidth; l++)
            {
                int i1 = mapPixels[i];
                for(; k < 5 && l > eventIndex[k]; k++);
                int j1 = eventDivisor[k];
                if(j1 != 0)
                {
                    int k1 = i1 & 0xff000000;
                    int l1 = (i1 & 0xff0000) >> 16;
                    int i2 = (i1 & 0xff00) >> 8;
                    int j2 = i1 & 0xff;
                    l1 /= j1;
                    i2 /= j1;
                    j2 /= j1;
                    i1 = k1 | l1 << 16 | i2 << 8 | j2;
                }
                resultPixels[i++] = i1;
            }

        }

        if(animationSupported)
            memoryImageSource.newPixels();
        else
            sunriseImage.flush();
    }

    protected void store(int i, int j)
    {
        int k;
        for(k = nEvents; k > 0 && i < eventIndex[k - 1]; k--)
        {
            eventIndex[k] = eventIndex[k - 1];
            eventDivisor[k] = eventDivisor[k - 1];
        }

        eventIndex[k] = i;
        eventDivisor[k] = j;
        nEvents++;
    }

    protected int timeToPixel(double d)
    {
        d = mod((d - timeOfDayGMT) / 24D, 1.0D);
        return (int)(d * (double)imageWidth);
    }

    protected double pixelToLatitude(int i)
    {
        double d = 90D - ((double)i / (double)imageHeight) * 180D;
        return d;
    }

    protected double pixelToLongitude(int i)
    {
        double d = ((double)i / (double)imageWidth) * 360D - 180D;
        return d;
    }

    public void riseSet(double ad[], double d)
    {
        sinLatitude = Math.sin(latitude * 0.017453292519943295D);
        cosLatitude = Math.cos(latitude * 0.017453292519943295D);
        sinHorizon = Math.sin(d * 0.017453292519943295D);
        ad[0] = computeRiseSet(1.0D);
        ad[1] = computeRiseSet(-1D);
    }

    protected double computeRiseSet(double d)
    {
        double d1 = 0.0D;
        int i = 0;
        int j = 0;
        double d2 = 12D;
        for(int k = 0; k < 20 && i < 3 && j < 3; k++)
        {
            solarEphemeris(d2);
            double d4 = (sinHorizon - sinLatitude * sinDeclin) / (cosLatitude * cosDeclin);
            double d3;
            if(d4 > 1.0D)
            {
                j++;
                d3 = 0.0D;
            } else
            if(d4 < -1D)
            {
                i++;
                d3 = 180D;
            } else
            {
                i = 0;
                j = 0;
                d3 = Math.acos(d4) / 0.017453292519943295D;
            }
            d1 = d2 - (GHA + longitude + d * d3) / 15D;
            d1 = mod(d1, 24D);
            if(Math.abs(d1 - d2) < 0.00080000000000000004D)
                break;
            d2 = d1;
        }

        if(j > 0)
            d1 = (-1.0D / 0.0D);
        else
        if(i > 0)
            d1 = (1.0D / 0.0D);
        else
            d1 = mod(d1, 24D);
        return d1;
    }

    protected void solarEphemeris(double d)
    {
        double d1 = ((DATE + d / 24D) - 51544.5D) / 36525D;
        double d2 = mod(280.45999999999998D + 36000.769999999997D * d1, 360D);
        double d3 = mod(357.52800000000002D + 35999.050000000003D * d1, 360D);
        double d4 = d3 * 0.017453292519943295D;
        double d5 = Math.sin(d4);
        double d6 = Math.sin(d4 * 2D);
        double d7 = 1.915D * d5 + 0.02D * d6;
        double d8 = d2 + d7;
        double d9 = d8 * 0.017453292519943295D;
        double d10 = 23.439299999999999D - 0.012999999999999999D * d1;
        double d11 = d10 * 0.017453292519943295D;
        double d12 = (-d7 + 2.4660000000000002D * Math.sin(d9 * 2D)) - 0.052999999999999999D * Math.sin(d9 * 4D);
        GHA = (15D * d - 180D) + d12;
        sinDeclin = Math.sin(d11) * Math.sin(d9);
        double d13 = Math.asin(sinDeclin);
        cosDeclin = Math.cos(d13);
    }

    public static double midnightMJD(Date date1)
    {
        double d = Math.floor(MJD(date1));
        return d;
    }

    public static double MJD(Date date1)
    {
        long l = date1.getTime() + getSystemTimezone();
        double d = (double)l / 86400000D;
        d += 40587D;
        return d;
    }

    public static long getSystemTimezone()
    {
        long l = 0L;
        int i = -(new Date()).getTimezoneOffset();
        l = i * 60000;
        return l;
    }

    public static double mod(double d, double d1)
    {
        double d2 = Math.IEEEremainder(d, d1);
        if(d2 < 0.0D)
            d2 += d1;
        return d2;
    }

    public static double sin(double d)
    {
        return Math.sin(d * 0.017453292519943295D);
    }

    public static double cos(double d)
    {
        return Math.cos(d * 0.017453292519943295D);
    }

    public static double acos(double d)
    {
        return Math.acos(d) / 0.017453292519943295D;
    }

    public static boolean isAbove(double ad[])
    {
        return ad[0] == (1.0D / 0.0D) || ad[1] == (1.0D / 0.0D);
    }

    public static boolean isBelow(double ad[])
    {
        return ad[0] == (-1.0D / 0.0D) || ad[1] == (-1.0D / 0.0D);
    }

    public static boolean isEvent(double ad[])
    {
        return ad[0] != (1.0D / 0.0D) && ad[0] != (-1.0D / 0.0D) && ad[1] != (1.0D / 0.0D) && ad[1] != (-1.0D / 0.0D);
    }

    public static final double SUNRISE = -0.83333333333333337D;
    public static final double CIVIL_TWILIGHT = -6D;
    public static final int RISE = 0;
    public static final int SET = 1;
    public static final double ABOVE_HORIZON = (1.0D / 0.0D);
    public static final double BELOW_HORIZON = (-1.0D / 0.0D);
    protected static final int DARK = -1;
    protected static final int BRIGHT = -2;
    protected static final int DAYLIGHT = 0;
    protected static final int TWILIGHT = 2;
    protected static final int NIGHT = 3;
    protected static final int MAX_EVENTS = 5;
    protected static final int LOC = 0;
    protected static final int DIV = 1;
    protected Date date;
    protected double DATE;
    protected double timeOfDayGMT;
    protected double latitude;
    protected double longitude;
    protected double sinHorizon;
    protected double sinLatitude;
    protected double cosLatitude;
    protected double GHA;
    protected double sinDeclin;
    protected double cosDeclin;
    protected double riseSet[];
    protected double twilight[];
    protected static final double RISING = 1D;
    protected static final double SETTING = -1D;
    public static final double DegRad = 0.017453292519943295D;
    public static final double javaEpochMJD = 40587D;
    public static final double msecPerDay = 86400000D;
    protected int eventIndex[];
    protected int eventDivisor[];
    protected int nEvents;
    protected int mapPixels[];
    protected int resultPixels[];
    protected int imageWidth;
    protected int imageHeight;
    protected boolean animationSupported;
    protected MemoryImageSource memoryImageSource;
    protected Image sunriseImage;
    protected static final boolean drawLatLongLines = false;
    protected static final int latLongFraction = 8;
    protected int latOffset;
    protected int longOffset;
}
