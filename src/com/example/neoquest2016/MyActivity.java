package com.example.neoquest2016;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.net.HttpURLConnection;
import java.net.URL;
import static com.example.neoquest2016.Globals.host_address;
public class MyActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        TextView regButton = (TextView) findViewById(R.id.registrateButton);
        regButton.setClickable(true);
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyActivity.this, AuthActivity.class);
                startActivity(intent);
            }
        });
        EditText tokenField = (EditText) findViewById(R.id.tokenTextField);
        regButton.setClickable(true);
        TextView wrongToken = (TextView) findViewById(R.id.wrongToken);
        wrongToken.setVisibility(View.INVISIBLE);
        Button loginButton = (Button) findViewById(R.id.startButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL(host_address + "language_test?token=" + tokenField.getText());
                            HttpURLConnection uc = (HttpURLConnection) url.openConnection();
                            String hash = uc.getHeaderField("hash");
                            if(hash == null || hash.isEmpty()){
                                wrongToken.setVisibility(View.VISIBLE);
                            }else{
                                Bitmap image = BitmapFactory.decodeStream(uc.getInputStream());
                                Bundle extras = new Bundle();
                                extras.putParcelable("image", image);
                                extras.putString("hash", hash);
                                extras.putString("token", String.valueOf(tokenField.getText()));
                                Intent intent = new Intent(MyActivity.this, TaskActivity.class);
                                intent.putExtras(extras);
                                startActivity(intent);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }
}
