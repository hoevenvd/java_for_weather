package org.tom.weather.upload;

import java.util.Date;

/**
 * @author tom
 * 
 */
public interface DataUploader {
  public Date getLatestArchiveRecord();
  public void upload(org.tom.weather.ArchiveEntry[] entries) throws Exception;
}
