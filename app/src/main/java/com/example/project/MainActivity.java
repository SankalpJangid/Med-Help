package com.example.project;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    public static final String MSGR = "com.deviceId.MSGR";

    private final String url = "http://40db11c9985e.ngrok.io/example/check.php";

    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button cp = findViewById(R.id.process11);
        final Button send = findViewById(R.id.button);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        final String imei = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        final String query = "?t1="+imei;

        class dbclass extends AsyncTask<String,Void,String> {

            protected void onPostExecute(String data){

                if(data.equals("First Create Profile")){
                    send.setVisibility(View.INVISIBLE);
                }else{
                    cp.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            protected String doInBackground(String... param) {
                try {
                    URL url = new URL(param[0]);
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    return br.readLine();

                }catch (Exception ex){
                    return ex.getMessage();
                }
            }
        }
        dbclass obj = new dbclass();
        obj.execute(url+query);


    }

    public void Second(View view){
        String imei = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        String query = "?t1="+imei;

        class dbclass extends AsyncTask<String,Void,String> {
            protected void onPostExecute(String data){

                if(data.equals("First Create Profile")){
                    System.out.println(data);
                    TextView tv = findViewById(R.id.text);
                    tv.setText(data);
                    Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();

                }else{
                    System.out.println(data);

                    String[] sp = data.split("-");
                    String name = sp[2];
                    String age = sp[3];
                    String blood = sp[4];
                    String pName = sp[6];
                    String pNumber = sp[7];
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        getLocation(name,age,blood,pName,pNumber);
                    }else{
                        askLocationPermission();
                    }
                }
            }

            @Override
            protected String doInBackground(String... param) {
                try {
                    URL url = new URL(param[0]);
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    return br.readLine();

                }catch (Exception ex){
                    return ex.getMessage();
                }
            }
        }
        dbclass obj = new dbclass();
        obj.execute(url+query);
    }



    private void askLocationPermission(){
        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
            }else{
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Second(null);
            }else{

            }
        }
        if (requestCode == 43){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Again press the send button to send message", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this,MainActivity.class));
                finish();

            }else{

            }
        }
    }



    private void getLocation(final String name, final String age, final String blood, final String pName, final String pNumber){
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                android.location.Location locations = task.getResult();
                if (locations != null){
                    Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(locations.getLatitude(),locations.getLongitude(),1);
                        System.out.println("h");


                        String lat = String.valueOf(addresses.get(0).getLatitude());
                        String lon = String.valueOf(addresses.get(0).getLongitude());
                        String Area = String.valueOf(addresses.get(0).getAddressLine(0));
                        String subLocality = String.valueOf(addresses.get(0).getSubLocality());
                        String city = String.valueOf(addresses.get(0).getLocality());

                        link(name,age,blood,pName,pNumber,lat,lon,Area,subLocality,city,addresses);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }



    public void link(final String name, final String age, final String blood, final String pName, final String pNumber, String lat, String lon, String area, String subLocality, String city, final List<Address> addresses){
        final String la = lat;
        final String lo = lon;
        final String ar = area;
        final String sub = subLocality;
        final String ci = city;

        new Thread(new Runnable() {
            @Override
            public void run() {
                DecimalFormat df2 = new DecimalFormat("#.##");
                System.out.println("good");
                String ars = sub.replace(" ","-");
                System.out.println(ars);

                try {
                    Document doc = Jsoup.connect("https://justdial.com/"+ci+"/Hospitals-in-"+sub).get();
                    Elements ul = doc.select(".lng_cont_name");
                    Elements s = doc.select(".distnctxt");

                    /*ArrayList<String> d = new ArrayList<String>();

                    for(Element si : s){
                        String sankalp = (si.text().replace("AVAILABLE", "").replace("NOW", "").replace("Distance", "").replace("KM", "").replace(" ","").replace("25YearsExperience","").replace("YearsExperience",""));
                        if (sankalp.length() != 0) {
                            String jangid = sankalp.substring(0, 3);
                            double a = Float.parseFloat(String.valueOf(jangid));
                            df2.setRoundingMode(RoundingMode.DOWN);
                            String b = df2.format(a);
                            d.add(b);

                        }

                    }*/

                    ArrayList<String> n = new ArrayList<String>();

                    for(Element l : ul){
                        n.add(l.text().toString());
                    }

                    /*Hashtable<String, String> lmn = new Hashtable<String, String>();

                    for (int i=0; i < n.size() ; i++) {
                        lmn.put(d.get(i),n.get(i));
                    }

                    ArrayList<String> hosp = new ArrayList<String>();
                    for(double i =0 ; i<2.5; i+=0.01){
                        df2.setRoundingMode(RoundingMode.DOWN);
                        if (lmn.get(df2.format(i)) != null) {
                            System.out.println("good");
                            hosp.add(lmn.get(df2.format(i)));
                        }
                    }
                    System.out.println("awesome");
                    System.out.println(String.valueOf(hosp));*/


                    //Toast.makeText(Location.this, "awesome", Toast.LENGTH_SHORT).show();
                    String hospitals = String.valueOf(n);
                    if (ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
                        SmsManager smsManager = SmsManager.getDefault();
                        String longMessage = "Name = "+name+"\n"+"Age = "+age+"\n"+"Blood Group = "+blood+"\n"+"Parents Name = "+pName+"\n"+"Parents Number = "+pNumber+"\n"+"latitude = "+la+"\n"+"longitude = "+lo+"\n"+"area = "+ar+"\n"+"links = https://www.google.com/maps/search/?api=1&query=+"+la+","+lo + "\n"+"subLocality = "+sub+"\n" + "Hospital = "+hospitals+"\n";
                        ArrayList<String> parts = smsManager.divideMessage(longMessage);
                        smsManager.sendMultipartTextMessage("9001177514",null, parts,null,null);
                        smsManager.sendMultipartTextMessage(pNumber,null, parts,null,null);
                        System.out.println(addresses.get(0).getLatitude());
                        //TextView tv = findViewById(R.id.text);
                        //tv.setText("Message Send Successfully");
                    }else{
                        askSMSPermission();
                    }

                }catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("bad");
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Message Send Successfully", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        }).start();
    }


    public void askSMSPermission(){
        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.SEND_SMS)){
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS},43);
            }else{
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.SEND_SMS},43);
            }
        }

    }

    public void process(View view) {
        String imei = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Intent intent = new Intent(MainActivity.this, Profile.class);
        intent.putExtra(MSGR, imei);
        startActivity(intent);
    }
}
