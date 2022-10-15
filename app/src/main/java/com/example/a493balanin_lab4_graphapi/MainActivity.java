package com.example.a493balanin_lab4_graphapi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    EditText et_username, et_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
    }

    public void onButtonAuthorize_Click(View v)
    {
        String username = et_username.getText().toString();
        String password = et_password.getText().toString();

        Request r = new Request(){
            public void onSuccess(String res) throws Exception
            {
                JSONObject obj = new JSONObject(res);
                String token = obj.getString("token");

                Intent i = new Intent();
                i.putExtra("token",token);
                startActivity(i);

            }
        };

        r.send(this,"PUT","/session/open/?name=" + username + "&secret=" + password);




        String token = "";

    }


    public void onButtonRegister_Click(View v)
    {
        Intent i = new Intent(this, RegisterActivity.class);
        startActivityForResult(i,1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        try
        {
            if (requestCode == 1  && resultCode  == RESULT_OK)
            {
                String reg_login = data.getStringExtra("login");
                String reg_password = data.getStringExtra("password");
                et_username.setText(reg_login);
                et_password.setText(reg_password);

            }
        }
        catch (Exception ex)
        {
            Log.e("TEST","SOMETHING WENT WRONG ON ONACTIVITYRESULT");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}