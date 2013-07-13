package com.opassserver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.app.Activity;
import android.telephony.SmsManager;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

//import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.*;

import org.apache.http.entity.StringEntity;
import org.json.*;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends Activity {

    //Strings to register to create intent filter for registering the recivers
    private static final String ACTION_STRING_SERVICE = "ToService";
    private static final String ACTION_STRING_ACTIVITY = "ToActivity";

    private  BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("Recieved intent","broadcast receiver");
            Toast.makeText(context, "Intent Detected."+intent.getExtras().getString("SMS"), Toast.LENGTH_LONG).show();
            Bundle extras = intent.getExtras();
            if (extras != null) {
                String value = extras.getString("SMS");
                String from = extras.getString("FROM");
                Log.i("SMS passed by intent = >>>", value);
                TextView view = (TextView)findViewById(R.id.sms);
                String text =  view.getText().toString();
                view.setText(text+value);
                Log.i("SMS>>",text+value);

//                handleSMS(value,from);
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.opassserver.SMS_MESSAGE_RECEIVED");
        this.registerReceiver(this.receiver, filter);


    }


public  void  handleSMS(final String message, final String from) {

    try {


        StringEntity entity = null;
        try {
            entity = new StringEntity(message);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }



        AsyncHttpClient client = new AsyncHttpClient();

        client.post(getBaseContext(),"http://10.0.2.2/registration_complete.php",entity,"application/json",new AsyncHttpResponseHandler(){

            @Override
            public void onSuccess(String s) {
                super.onSuccess(s);
//                SmsManager smsManager = SmsManager.getDefault();
//                smsManager.sendTextMessage(from, null, s, null, null);

                try {
                    JSONObject responseObject = new JSONObject(s);
                    if(responseObject.getString("status").equals("1")){

                        Toast.makeText(getBaseContext(),"Registration Completed",2).show();


                    }
                    else {

                        Toast.makeText(getBaseContext(),"Already Registered",2).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


    } catch (Exception e) {
        e.printStackTrace();
    }

}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
