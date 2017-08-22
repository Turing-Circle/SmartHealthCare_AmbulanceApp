package com.example.shubhamr.myfirstmapactivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class Login extends AbsRuntimePermission {
    private static final int REQUEST_PERMISSION = 10;
    TextView textView;
    Button login_button;
    EditText password,idambu;
    String Password,id;
    String login_url = "http://35.154.177.242/ambulance/login.php";
    private static final String TAG = "MyActivity";
    // String login_url = "http://10.0.3.2/login.php";
    AlertDialog.Builder builder;
    ProgressDialog progress;
    private  Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session = new Session(this);

        //mysesion
        if (session.loggedin()){
            startActivity(new Intent(Login.this, MainControlActivity.class));
            finish();



        }


        requestAppPermissions(new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_NETWORK_STATE},
                R.string.msg,REQUEST_PERMISSION);


        progress=new ProgressDialog(this);

        login_button=(Button) findViewById(R.id.bn_login);
        password=(EditText)findViewById(R.id.login_password);
        idambu = (EditText) findViewById(R.id.idambu);


        builder = new AlertDialog.Builder(Login.this);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestAppPermissions(new String[]{
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_NETWORK_STATE},
                        R.string.msg,REQUEST_PERMISSION);

                if (isNetworkConnected()) {


                    progress.setMessage("Logging in");

                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progress.setIndeterminate(true);
                    ;
                    progress.setProgress(0);
                    progress.show();

                    Log.v(TAG, "index");
                    id = idambu.getText().toString();
                    Password = password.getText().toString();

                    if (id.equals("") || Password.equals("")) {

                        displayAlert("Enter a valid username or password");
                        progress.dismiss();

                    } else {

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, login_url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                        try {
                                            JSONArray jsonArray = new JSONArray(response);
                                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                                            String code = jsonObject.getString("code");

                                            if (code.equals("Unknown")) {
                                                String message = jsonObject.getString("message");
                                                displayAlert(message);
                                                Toast.makeText(getApplicationContext(), "invalid user", Toast.LENGTH_LONG).show();
                                                progress.dismiss();
                                            } else if (code.equals("Failure")) {
                                                String message = jsonObject.getString("message");
                                                displayAlert(message);
                                                Toast.makeText(getApplicationContext(), "invalid password", Toast.LENGTH_LONG).show();
                                                progress.dismiss();
                                            } else {
                                                session.setLoggedin(true);



                                                Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                                                progress.dismiss();

                                                Intent intent = new Intent(Login.this, MainControlActivity.class);

                                                Bundle bundle = new Bundle();
                                                bundle.putString("idofambulance",id);

                                                intent.putExtras(bundle);


                                                startActivity(intent);

                                                finish();

                                              //  Bundle bundle = new Bundle();
                                               // bundle.putString("pname", jsonObject.getString("pname"));
                                               // bundle.putString("aadhaar", jsonObject.getString("aadhaar"));
                                               // intent.putExtras(bundle);


                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(Login.this, "ERROR", Toast.LENGTH_LONG).show();
                                error.printStackTrace();
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("id", id);
                                params.put("pass", Password);
                                return params;
                            }
                        };
                        MySingleton.getInstance(Login.this).addToRequestque(stringRequest);
                    }


                }


                else{
                    displayAlert("Turn on internet");
                }
            }
        });
    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    private boolean isNetworkConnected() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }


    public void displayAlert(String message)
    {
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                idambu.setText("");
                password.setText("");
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}

