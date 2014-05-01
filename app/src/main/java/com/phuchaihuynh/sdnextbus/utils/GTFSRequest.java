package com.phuchaihuynh.sdnextbus.utils;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class GTFSRequest {

    private static final String TAG = "[Dynamic_GTFS_Request]";

    private final String URL = "http://sdmts.ealert.ws/SDMTSChallenge.asmx";
    private final String NAMESPACE = "http://ealert.com/";
    private final String SOAP_ACTION = "http://ealert.com/ProcessStop";
    private final String METHOD_NAME = "ProcessStop";

    private String processStopResults;

    public GTFSRequest() {
        this.processStopResults = "";
    }

    public void requestRoutes(String busStopId) {
        // Create request
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        // Property which holds input parameter
        PropertyInfo stopId = new PropertyInfo();
        stopId.setName("StopId");
        stopId.setValue(busStopId);
        stopId.setType(String.class);
        // Add property to request object
        request.addProperty(stopId);
        // Create envelop
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        // Set output SOAP object
        envelope.setOutputSoapObject(request);
        // Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        androidHttpTransport.debug = true;
        try {
            // Invole web service
            androidHttpTransport.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            androidHttpTransport.call(SOAP_ACTION, envelope);
            Log.d(TAG, "Request: " + androidHttpTransport.requestDump);
            Log.d(TAG, "Response: " + androidHttpTransport.responseDump);
            // Get the response
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            this.processStopResults = response.toString();
        }
        catch (Exception e) {
            Log.e(TAG, "Cannot send the SOAP request");
            e.printStackTrace();
        }
    }

    public String getProcessStopResults() {
        Log.d(TAG, "Result: " + this.processStopResults);
        return this.processStopResults;
    }
}
