package org.tom.weather.upload;

import org.tom.weather.Cacheable;

/**
 * @author tom
 * 
 */
public interface DataUploader {
  public void upload(org.tom.weather.ArchiveEntry[] entries) throws Exception;
}
