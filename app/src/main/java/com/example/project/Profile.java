package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Profile extends AppCompatActivity {

    private final String url = "http://40db11c9985e.ngrok.io/insert.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }

    public void submission(View view){
        Intent intent = getIntent();
        String id = intent.getStringExtra(MainActivity.MSGR);
        final Intent intent1 = new Intent(this,MainActivity.class);
        final EditText name = findViewById(R.id.editText2);
        final EditText age = findViewById(R.id.editText3);
        final EditText blood = findViewById(R.id.editText5);
        final EditText number = findViewById(R.id.editText6);
        final EditText parent = findViewById(R.id.editText7);
        final EditText pnumber = findViewById(R.id.editText8);

        String n = name.getText().toString();
        String a = age.getText().toString();
        String b = blood.getText().toString();
        String no = number.getText().toString();
        String pa = parent.getText().toString();
        String pno = pnumber.getText().toString();

        System.out.println(n+a+b+no+pa+pno);

        String query = "?t1="+id+"&t2="+n+"&t3="+a+"&t4="+b+"&t5="+no+"&t6="+pa+"&t7="+pno;

        class dbclass extends AsyncTask<String,Void,String> {
            protected void onPostExecute(String data){
                name.setText("");
                age.setText("");
                parent.setText("");
                blood.setText("");
                number.setText("");
                pnumber.setText("");
                if(data.equals("user created")){
                    startActivity(intent1);
                }else{
                    pnumber.setText(data);
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
}
