/*
 ********************* [ P O C K E T C A M P U S ] *****************

 * [   MAINTAINER  ]	tarek.benoudina@epfl.ch
 * [     STATUS    ]    stable
 *
 **************************[ C O M M E N T S ]**********************
 *
 *
 *******************************************************************
 */

package org.pocketcampus.plugin.positioning;


/**
 * Author : Tarek
 *          Benoudina
 *          
 * GsmLocation,
 * 
 * returns : The position of the cellPhone according to CellId and Lac "Local Area Code".
 * 
 * the computation of this later is done on "http://www.google.com/glm/mmap"
 * by using a private API
 * 
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.osmdroid.util.GeoPoint;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

public class GsmLocation {
	
	private Context ctx_;
	private GsmCellLocation location;
    int cellID, lac;
    public TelephonyManager telephoneManager;

    
   public  GsmLocation(Context _ctx){
    	
	   ctx_ = _ctx;
	   telephoneManager = (TelephonyManager) ctx_.getSystemService(Context.TELEPHONY_SERVICE) ;
	   location = (GsmCellLocation) telephoneManager.getCellLocation(); 
       cellID = location.getCid();
       lac = location.getLac();
    }
    
   
   
   public int getCellID(){
	   return location.getCid();
   }
   
   
   public int getLac(){
	   return location.getLac();
   }
    
   public GeoPoint getGSMLocation() throws IOException{
	     
	   GeoPoint GsmPoint = null;
	
	   String urlString = "http://www.google.com/glm/mmap";            
	    
       // Connect to googleMapApi
       URL url = null;
	try {
		url = new URL(urlString);
	} catch (MalformedURLException e) {
		e.printStackTrace();
	} 
       URLConnection conn = url.openConnection();
       HttpURLConnection httpConn = (HttpURLConnection) conn;        
       httpConn.setRequestMethod("POST");
       httpConn.setDoOutput(true); 
       httpConn.setDoInput(true);
       httpConn.connect(); 
       
       //---write some custom data to Google Maps API---
       OutputStream outputStream = httpConn.getOutputStream();
       WriteData(outputStream, cellID, lac);       
       
      // Log.d("Url", " : "+outputStream.toString());
       //---get the response---
       InputStream inputStream = httpConn.getInputStream();  
       DataInputStream dataInputStream = new DataInputStream(inputStream);
       
      // Log.d("Url 2", " : "+dataInputStream.toString());
       
       //---interpret the response obtained---
       dataInputStream.readShort();
       dataInputStream.readByte();
       int code = dataInputStream.readInt();
       Log.d("code", " : "+code);
       //Log.d("data inpt", " : "+dataInputStream.readInt());
       //Log.d("data inpt", " : "+dataInputStream.readInt()/1000000D);
       if (code == 0) {
           double lat = (double) dataInputStream.readInt() / 1000000D;
           double lng = (double) dataInputStream.readInt() / 1000000D;
           dataInputStream.readInt();
           dataInputStream.readInt();
           dataInputStream.readUTF();
           
           //---display Google Maps---
//           String uriString = "geo:" + lat
//               + "," + lng;

           Log.d("latGsm :"+lat," longGsm : "+lng);
            GsmPoint = new GeoPoint(lat,lng); 
       }

	      
	   return GsmPoint;
   }
   
   
   private void WriteData(OutputStream out, int cellID, int lac) 
   throws IOException
   {    	
       DataOutputStream dataOutputStream = new DataOutputStream(out);
       dataOutputStream.writeShort(21);
       dataOutputStream.writeLong(0);
       dataOutputStream.writeUTF("en");
       dataOutputStream.writeUTF("Android");
       dataOutputStream.writeUTF("1.0");
       dataOutputStream.writeUTF("Web");
       dataOutputStream.writeByte(27);
       dataOutputStream.writeInt(0);
       dataOutputStream.writeInt(0);
       dataOutputStream.writeInt(3);
       dataOutputStream.writeUTF("");

       dataOutputStream.writeInt(cellID);  
       dataOutputStream.writeInt(lac);     

       dataOutputStream.writeInt(0);
       dataOutputStream.writeInt(0);
       dataOutputStream.writeInt(0);
       dataOutputStream.writeInt(0);
       dataOutputStream.flush();    	
   }
   
  
   
    
    

}
