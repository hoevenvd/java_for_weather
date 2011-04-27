package org.tom.weather.posting;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.RemoteException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.apache.log4j.Logger;
import org.tom.weather.PeriodData;
import org.tom.weather.SnapShot;

public class GoogleAppsDataPosterImpl implements DataPoster {

  private static final Logger LOGGER = Logger.getLogger(GoogleAppsDataPosterImpl.class);
  private String password;
  private String location;
  private String target;
  
  public void setPassword(String password) {
    this.password = password;
  }
  
  public String getPassword() {
    return password;
  }

  public void post(SnapShot snap) throws RemoteException {
    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
    String json = gson.toJson(snap);
    try {
      postData(gson.toJson(new RequestEnvelope(password, json)));
    } catch (Exception e) {
      LOGGER.error(e);
      throw new RemoteException(e.getMessage());
    }
  }

  public void post(PeriodData periodData) {
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getLocation() {
    return location;
  }

    /**
     * @return the target
     */
    public String getUrl() {
        return target;
    }

    /**
     * @param target the target to set
     */
    public void setUrl(String url) {
        this.target = url;
    }


  public String postData(String content) {
    String response = null;
    try {
      URL url = new URL(target);
      URLConnection conn = url.openConnection();
      // Set connection parameters.
      conn.setDoInput (true);
      conn.setDoOutput (true);
      conn.setUseCaches (false);
      // Make server believe we are form data...
      conn.setRequestProperty("Content-Type", "application/json");
      DataOutputStream out = new DataOutputStream (conn.getOutputStream ());
      // Write out the bytes of the content string to the stream.
      out.writeBytes(content);
      out.flush ();
      out.close ();
      // Read response from the input stream.
      BufferedReader in = new BufferedReader (new InputStreamReader(conn.getInputStream ()));
      String temp;
      while ((temp = in.readLine()) != null){
        response += temp + "\n";
       }
      temp = null;
      in.close ();
    } catch (Exception e) {
      LOGGER.error("** Exception caught:", e);
    }
    LOGGER.debug(response);
    return response;
  }

  class RequestEnvelope {
    private final String password;
    private final String json;

    RequestEnvelope(String password, String json) {
      this.password = password;
      this.json = json;
    }

  }
}