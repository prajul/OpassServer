package com.opassserver;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;


public class SmsReceiver extends BroadcastReceiver 
{
	// All available column names in SMS table
    // [_id, thread_id, address, 
	// person, date, protocol, read, 
	// status, type, reply_path_present, 
	// subject, body, service_center, 
	// locked, error_code, seen]
	
	public static final String SMS_EXTRA_NAME = "pdus";
	public static final String SMS_URI = "content://sms";
	
	public static final String ADDRESS = "address";
    public static final String PERSON = "person";
    public static final String DATE = "date";
    public static final String READ = "read";
    public static final String STATUS = "status";
    public static final String TYPE = "type";
    public static final String BODY = "body";
    public static final String SEEN = "seen";
    
    public static final int MESSAGE_TYPE_INBOX = 1;
    public static final int MESSAGE_TYPE_SENT = 2;
    
    public static final int MESSAGE_IS_NOT_READ = 0;
    public static final int MESSAGE_IS_READ = 1;
    
    public static final int MESSAGE_IS_NOT_SEEN = 0;
    public static final int MESSAGE_IS_SEEN = 1;
	
    // Change the password here or give a user possibility to change it
    public static final byte[] PASSWORD = new byte[]{ 0x20, 0x32, 0x34, 0x47, (byte) 0x84, 0x33, 0x58 };


    //Strings to register to create intent filter for registering the recivers
    private static final String ACTION_STRING_SERVICE = "ToService";
    private static final String ACTION_STRING_ACTIVITY = "ToActivity";

/*
	public void onReceive( Context context, Intent intent ) 
	{
		// Get SMS map from Intent
        Bundle extras = intent.getExtras();
        
        String messages = "";
        String address = "";
        if ( extras != null )
        {
            // Get received SMS array
            Object[] smsExtra = (Object[]) extras.get( SMS_EXTRA_NAME );
            
            // Get ContentResolver object for pushing encrypted SMS to incoming folder
//            ContentResolver contentResolver = context.getContentResolver();
            
            for ( int i = 0; i < smsExtra.length; ++i )
            {
            	SmsMessage sms = SmsMessage.createFromPdu((byte[])smsExtra[i]);
            	
            	String body = sms.getMessageBody().toString();
            	address = sms.getOriginatingAddress();
                messages += body;
                
                // Here you can add any your code to work with incoming SMS
                // I added encrypting of all received SMS 

//                    putSmsToDatabase(contentResolver, sms);
//                    this.abortBroadcast();



            }


            Intent newIntent = new Intent();
            newIntent.setAction("com.opassserver.SMS_MESSAGE_RECEIVED");
            newIntent.putExtra("SMS",messages);
            newIntent.putExtra("FROM",address);

            context.sendBroadcast(newIntent);

            Log.i("sms received >> ",messages);

            // Display SMS message
            Toast.makeText( context, messages, Toast.LENGTH_SHORT ).show();
        }
        
        // WARNING!!! 
        // If you uncomment next line then received SMS will not be put to incoming.
        // Be careful!
        // this.abortBroadcast(); 
	}

*/

    /*

    public void onReceive( Context context, Intent intent ) {

        Bundle bundle  = intent.getExtras();
        Object[] pdus = (Object[]) bundle.get("pdus");
        SmsMessage[] messages = new SmsMessage[pdus.length];
        for (int i = 0; i < pdus.length; i++)
        {
            messages[i] =
                    SmsMessage.createFromPdu((byte[]) pdus[i]);
        }

        SmsMessage sms = messages[0];
        String body;
        try {
            if (messages.length == 1 || sms.isReplace()) {
                body = sms.getDisplayMessageBody();
            } else {
                StringBuilder bodyText = new StringBuilder();
                for (int i = 0; i < messages.length; i++) {
                    bodyText.append(messages[i].getMessageBody());
                }
                body = bodyText.toString();
            }

            Intent newIntent = new Intent();
            newIntent.setAction("com.opassserver.SMS_MESSAGE_RECEIVED");
            newIntent.putExtra("SMS",messages);

            context.sendBroadcast(newIntent);

            Log.i("sms received >> ",body);

            // Display SMS message
            Toast.makeText( context, body, Toast.LENGTH_SHORT ).show();


        } catch (Exception e) {

        }
    }
*/

    @Override
    public void onReceive(Context context, Intent intent) {
        Map<String, String> msg = RetrieveMessages(intent);

        if (msg == null) {
            // unable to retrieve SMS
            return;
        } else  {
            // send all SMS via XMPP by sender
            for (String sender : msg.keySet()) {
                String message = msg.get(sender);
                Log.i("MESSAGE",message);

            }

        }
    }


private static Map<String, String> RetrieveMessages(Intent intent) {
    Map<String, String> msg = null;
    SmsMessage[] msgs = null;
    Bundle bundle = intent.getExtras();

    if (bundle != null && bundle.containsKey("pdus")) {
        Object[] pdus = (Object[]) bundle.get("pdus");

        if (pdus != null) {
            int nbrOfpdus = pdus.length;
            msg = new HashMap<String, String>(nbrOfpdus);
            msgs = new SmsMessage[nbrOfpdus];

            // There can be multiple SMS from multiple senders, there can be a maximum of nbrOfpdus different senders
            // However, send long SMS of same sender in one message
            for (int i = 0; i < nbrOfpdus; i++) {
                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);

                String originatinAddress = msgs[i].getOriginatingAddress();

                // Check if index with number exists
                if (!msg.containsKey(originatinAddress)) {
                    // Index with number doesn't exist
                    // Save string into associative array with sender number as index
                    msg.put(msgs[i].getOriginatingAddress(), msgs[i].getMessageBody());

                } else {
                    // Number has been there, add content but consider that
                    // msg.get(originatinAddress) already contains sms:sndrNbr:previousparts of SMS,
                    // so just add the part of the current PDU
                    String previousparts = msg.get(originatinAddress);
                    String msgString = previousparts + msgs[i].getMessageBody();
                    msg.put(originatinAddress, msgString);
                }
            }
        }
    }

    return msg;
}

	private void putSmsToDatabase( ContentResolver contentResolver, SmsMessage sms )
	{



		// Create SMS row
        ContentValues values = new ContentValues();
        values.put( ADDRESS, sms.getOriginatingAddress() );
        values.put( DATE, sms.getTimestampMillis() );
        values.put( READ, MESSAGE_IS_NOT_READ );
        values.put( STATUS, sms.getStatus() );
        values.put( TYPE, MESSAGE_TYPE_INBOX );
        values.put( SEEN, MESSAGE_IS_NOT_SEEN );
        try
        {
        	String encryptedPassword = StringCryptor.encrypt( sms.getMessageBody().toString() );
        	values.put( BODY, encryptedPassword );
        }
        catch ( Exception e ) 
        { 
        	e.printStackTrace(); 
    	}
        
        // Push row into the SMS table
        contentResolver.insert( Uri.parse( SMS_URI ), values );
	}
}
