package com.example.a493balanin_lab4_graphapi;

import android.app.Activity;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Request {

    String base = "http://89.108.78.244:5000";

    public void onSuccess(String res) throws Exception
    {


    }
    public void onFail()
    {

    }

    public void send (Activity ctx, String method, String request)
    {
        Runnable r = new Runnable()
        {

            @Override
            public void run() {

                try
                {
                    URL url = new URL(base + request);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod(method);

                    InputStream is = con.getInputStream();
                    BufferedInputStream inp = new BufferedInputStream(is);

                    byte[] buf = new byte[512];
                    String str = "";

                    while (true)
                    {
                        int len = inp.read(buf);
                        if (len <0) break;

                        str += new String(buf,0,len);
                    }
                    con.disconnect();

                    final String res = str;

                    ctx.runOnUiThread(() ->
                    {
                        try { onSuccess(res); } catch (Exception e){  }
                    });

                } catch (Exception ex)
                {
                    ctx.runOnUiThread(() ->
                    {
                     Toast t = Toast.makeText(ctx,"Reques failed!",Toast.LENGTH_SHORT);
                     t.show();
                     onFail();
                    });

                }


            }
        };
        Thread t = new Thread(r);
        t.start();
    }
}
