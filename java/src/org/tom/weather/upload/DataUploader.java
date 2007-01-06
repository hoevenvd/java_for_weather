package org.tom.weather.upload;

/**
 * @author tom
 * 
 */
public interface DataUploader {
  public void upload(org.tom.weather.ArchiveEntry[] entries);
}
