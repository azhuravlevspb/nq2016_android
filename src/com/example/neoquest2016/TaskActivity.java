package com.example.neoquest2016;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;

import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.neoquest2016.Globals.host_address;

/**
 * Created by Anton on 13.01.2016.
 */
public class TaskActivity extends Activity {

    Handler myHandler = new Handler();
    volatile int clock;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_layout);
        Bundle extras = getIntent().getExtras();
        Bitmap bmp = extras.getParcelable("image");
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(bmp);
        TextView timer = (TextView) findViewById(R.id.timerString);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (clock = 5; clock > -1; clock--) {
                    myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            timer.setText("Осталось " + clock + " секунд");
                            if (clock == 0) {
                                Intent intent = new Intent(TaskActivity.this, MyActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        EditText answer = (EditText) findViewById(R.id.translateField);
        Button sendAnswer = (Button) findViewById(R.id.translateButton);
        sendAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String hash = extras.getString("hash");
                final String token = extras.getString("token");
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL(host_address + "language_test?token=" + token + "&hash=" + hash + "&answer=" + answer.getText());
                            HttpURLConnection uc = (HttpURLConnection) url.openConnection();
                            String hash = uc.getHeaderField("hash");
                            if (!(hash == null || hash.isEmpty())) {
                                Bitmap image = BitmapFactory.decodeStream(uc.getInputStream());
                                Bundle extras = new Bundle();
                                extras.putParcelable("image", image);
                                extras.putString("hash", hash);
                                Intent intent = new Intent(TaskActivity.this, TaskActivity.class);
                                intent.putExtras(extras);
                                startActivity(intent);
                            } else {
                                String answer = Globals.convertStreamToString(uc.getInputStream());
                                answer = answer.substring(answer.indexOf("{\n  \"") + 5, answer.indexOf("\n}"));
                                show(answer);
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

    private void show(final String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }
}

