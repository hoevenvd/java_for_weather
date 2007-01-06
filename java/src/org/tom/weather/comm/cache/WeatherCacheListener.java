/*
 * Created on Oct 24, 2005
 *
 */
package org.tom.weather.comm.cache;

import java.util.Iterator;
import java.util.Set;
import org.apache.log4j.Logger;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;
import com.tangosol.util.MapEvent;
import com.tangosol.util.MapListener;

public class WeatherCacheListener implements MapListener, Runnable {
  private static final Logger LOGGER = Logger
      .getLogger(WeatherCacheListener.class);
  // NamedCache weatherCache;
  NamedCache archiveCache;

  public WeatherCacheListener() {
    // weatherCache = CacheFactory.getReplicatedCache("WeatherCache");
    // weatherCache.addMapListener(this);
    archiveCache = CacheFactory.getCache("ArchiveCache");
    archiveCache.addMapListener(this);
  }

  /**
   * @param args
   * @throws InterruptedException
   */
  public static void main(String[] args) throws InterruptedException {
    WeatherCacheListener listener = new WeatherCacheListener();
    synchronized (listener) {
      new Thread(listener).run();
    }
  }

  public void run() {
    Set entries = archiveCache.entrySet();
    Iterator iterator = entries.iterator();
    LOGGER.info("entries: ");
    while (iterator.hasNext()) {
      LOGGER.info(iterator.next());
      iterator.remove();
    }
    LOGGER.debug("\n\nshow all again\n\n");
    while (iterator.hasNext()) {
      LOGGER.info(iterator.next());
      iterator.remove();
    }
    try {
      this.wait();
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void entryInserted(MapEvent arg0) {
    LOGGER.info("insert: " + arg0);
  }

  public void entryUpdated(MapEvent arg0) {
    LOGGER.info("update: " + arg0);
  }

  public void entryDeleted(MapEvent arg0) {
    LOGGER.info(arg0);
  }
}
