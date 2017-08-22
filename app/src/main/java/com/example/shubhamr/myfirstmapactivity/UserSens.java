package com.example.shubhamr.myfirstmapactivity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.ActionMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class UserSens extends AppCompatActivity {

    Button seeuser,navigate,picked,dropped;
    static double locationofuserlat,locationofuserlong;
    static String userlocationurl = "http://35.154.177.242/ambulance/server.php";
    static TextView userlattext,userlongtext,emergencytext,agetext;
    String Username,emergency,aadhaar,age,idofambulancestring;
    ProgressDialog progressDialog;
    Handler handler = new Handler(Looper.getMainLooper());
    Runnable runnable;
    TextView username,aadhaartext,userinfotext;
    double ambulancelat,ambulancelong;
    double hospitallat,hospitallong;
    NotificationCompat.Builder notification;
    private static final int UID = 4512;
    GPSTracker gps;
    double return_ambulance_lat,return_ambulance_long;
    String droppedip;

    long start = System.currentTimeMillis();
    long end = start + 10*1000; // 10 seconds * 1000 ms/sec










    @Override
    public void onBackPressed() {

      // Intent intent = new Intent(UserSens.this,MainControlActivity.class);
       // startActivity(intent);
       // finish();
        progressDialog.dismiss();
       //// intent.addCategory(Intent.CATEGORY_HOME);
       // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
       // startActivity(intent);
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sens);
        progressDialog=new ProgressDialog(this);
        gps = new GPSTracker(UserSens.this);
       hospitallat = 28.474463;
         hospitallong = 77.482903;



        notification = new NotificationCompat.Builder(this);
        notification.setAutoCancel(true);

        userlattext = (TextView) findViewById(R.id.userlattext);
        userlongtext = (TextView) findViewById(R.id.userlondtext);
        username = (TextView) findViewById(R.id.username);
        emergencytext = (TextView) findViewById(R.id.emergency);
        agetext = (TextView) findViewById(R.id.age);

        aadhaartext = (TextView) findViewById(R.id.aadhaartext);
        userinfotext = (TextView) findViewById(R.id.userinfotext);

        navigate = (Button) findViewById(R.id.navigate);
        picked = (Button) findViewById(R.id.picked);
        dropped = (Button) findViewById(R.id.dropped);

        Bundle bundle=getIntent().getExtras();
        ambulancelat = bundle.getDouble("latitudeofambulance");
        ambulancelong = bundle.getDouble("longitudeofambulance");
        idofambulancestring = bundle.getString("id");

        navigate.setVisibility(View.INVISIBLE);
        picked.setVisibility(View.INVISIBLE);
        dropped.setVisibility(View.INVISIBLE);

        progressDialog.show();

        runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(runnable, 3000);

                showuserdata();

                Log.i("UserSens","value is");
            }
        };

        runnable.run();


       picked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                gps = new GPSTracker(UserSens.this);

                // check if GPS enabled
                if(gps.canGetLocation()) {

                    return_ambulance_lat = gps.getLatitude();
                    return_ambulance_long = gps.getLongitude();
                    Log.i("LoginSuccess", "value of latitude is" + return_ambulance_lat);
                    Log.i("LoginSuccess", "value of longitude is" + return_ambulance_long);
                }



                String uri = "http://maps.google.com/maps?f=d&hl=en&saddr=" + return_ambulance_lat + "," + return_ambulance_long + "&daddr=" + hospitallat + "," + hospitallong;
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));

                startActivity(Intent.createChooser(intent, "Select an application"));


                if (hospitallat == locationofuserlat && hospitallong == locationofuserlong) {

                    Toast.makeText(getApplicationContext(), "Reached to destination", Toast.LENGTH_LONG).show();
                }


            }
        });

        dropped.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Dropped Successfully",Toast.LENGTH_LONG).show();

                StringRequest stringrequest = new StringRequest(Request.Method.POST, droppedip,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    JSONArray jsonArray = new JSONArray(response);


                                } catch (JSONException e) {
                                    e.printStackTrace();

                                }

                            }


                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        error.printStackTrace();


                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Dropped", "Dropped");

                        return params;
                    }
                };
                MySingleton.getInstance(UserSens.this).addToRequestque(stringrequest);


            }
        });


navigate.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {



        if (locationofuserlat == 0 && locationofuserlong == 0 || ambulancelat == 0 && ambulancelong == 0) {

            Toast.makeText(getApplicationContext(), "No Victim Found...", Toast.LENGTH_LONG).show();
            //  navigatevictim.setText("NAVIGATE VICTIM");
            //   navigatevictim.setBackgroundColor(Color.argb(255,12,150,166));

        } else {
            String uri = "http://maps.google.com/maps?f=d&hl=en&saddr=" + ambulancelat + "," + ambulancelong + "&daddr=" + locationofuserlat + "," + locationofuserlong;
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));

            startActivity(Intent.createChooser(intent, "Select an application"));


            if (ambulancelat == locationofuserlat && ambulancelong == locationofuserlong) {

                Toast.makeText(getApplicationContext(), "Reached to destination", Toast.LENGTH_LONG).show();

            }


        }

    }
});

    }



    private void showuserdata() {


        progressDialog.setTitle("Waiting for response....");
        progressDialog.setMessage("Waiting for response...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, userlocationurl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);

                            //   Intent intent = new Intent(LoginSuccess.this, MapsActivity.class);
                            Bundle bundle = new Bundle();
//                            String username = jsonObject.getString("");
                            locationofuserlat = jsonObject.getDouble("latitude");
                            locationofuserlong = jsonObject.getDouble("longitude");
                            Username = jsonObject.getString("pname");
                            aadhaar = jsonObject.getString("aadhaar");
                            emergency = jsonObject.getString("typename");
                            age = jsonObject.getString("age");

                        } catch (JSONException e) {
                            e.printStackTrace();

                        }

                    }

                    private void displayAlert(String message) {
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //    progress.dismiss();
                //   Toast.makeText(LoginSuccess.this, "ERROR Connection Failed",Toast.LENGTH_LONG).show();
                error.printStackTrace();


            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();


                  params.put("id", idofambulancestring);

                return params;
            }
        };
        MySingleton.getInstance(UserSens.this).addToRequestque(stringRequest);
userinfotext.setText("User information");
        userlattext.setText(String.valueOf(locationofuserlat));
        userlongtext.setText(String.valueOf(locationofuserlong));
        username.setText(Username);
       emergencytext.setText(emergency);
       agetext.setText(age);
        aadhaartext.setText(aadhaar);

        navigate.setVisibility(View.VISIBLE);
        picked.setVisibility(View.VISIBLE);
        dropped.setVisibility(View.VISIBLE);




        if (locationofuserlat>0&&locationofuserlong>0){

            notification.setTicker("");
            notification.setSmallIcon(R.drawable.final_logo);
            notification.setWhen(System.currentTimeMillis());
            notification.setContentTitle("Victim is nearby...");
            Intent intent = new Intent(this,UserSens.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            notification.setContentIntent(pendingIntent);

            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.notify(UID,notification.build());






            progressDialog.dismiss();
        }




    }







}
