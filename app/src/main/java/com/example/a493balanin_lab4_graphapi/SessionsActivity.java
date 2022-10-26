package com.example.a493balanin_lab4_graphapi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.a493balanin_lab4_graphapi.classes.DB;
import com.example.a493balanin_lab4_graphapi.classes.GraphArrayAdapter;
import com.example.a493balanin_lab4_graphapi.classes.Request;
import com.example.a493balanin_lab4_graphapi.classes.SessionArrayAdapter;
import com.example.a493balanin_lab4_graphapi.model.Graph;
import com.example.a493balanin_lab4_graphapi.model.Session;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;

public class SessionsActivity extends AppCompatActivity {

    SessionArrayAdapter adp;
    ListView lv;
    //493 Balanin
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sessions);
        lv = findViewById(R.id.lv);
        this.setTitle("Session list");
        adp = new SessionArrayAdapter(this,
                R.layout.listview_session_item);

        Intent i = getIntent();
        String token = i.getStringExtra("token");

        Request r = new Request(){
            public void onSuccess(String res, Context ctx) throws Exception
            {
                JSONArray obj = new JSONArray(res);
                for (int i = 0; i < obj.length(); i++)
                {
                    JSONObject session = obj.getJSONObject(i);
                    Session s = new Session();
                    s.Id = session.getInt("id");
                    s.Token = session.getString("token");
                    s.Date = new Date(session.getLong("timestamp")*1000);
                    adp.add(s);
                }
                lv.setAdapter(adp);
            }
            public void onFail()
            {
                Context ctx = getBaseContext();
                Toast t = Toast.makeText(ctx,"Something went wrong\nInvalid username or password",Toast.LENGTH_SHORT);
                t.show();
            }
        };
        r.send(this,"GET","/session/list?token=" + token);
    }
    //493 Balanin
    public void onButtonCloseCurrentSession_Click(View v)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to close current session?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                setResult(RESULT_OK);
                finish();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { dialog.dismiss(); }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }


}