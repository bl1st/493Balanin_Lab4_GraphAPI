package com.example.a493balanin_lab4_graphapi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.a493balanin_lab4_graphapi.classes.Request;

public class RegisterActivity extends AppCompatActivity {

    EditText et_username, et_password;
    //493 Balanin
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setTitle("Registration");
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
    }

    //493 Balanin
    public void onButtonRegister_Click(View v)
    {
        String login = et_username.getText().toString();
        String secret = et_password.getText().toString();

        Request r = new Request()
        {
            public void onSuccess(String res, Context tx) throws Exception
            {
                Intent i = new Intent();
                i.putExtra("login",login);
                i.putExtra("secret",secret);
                setResult(RESULT_OK,i);
                finish();
            }
            public void onFail(Context ctx)
            {
                Toast t = Toast.makeText(ctx,"Something went wrong\nThis user is probably already exists",Toast.LENGTH_SHORT);
                t.show();
            }
        };

        r.send(this,"PUT","/account/create?name=" + login + "&secret=" + secret);
    }

    public void onSuccessRegistration(String login, String password)
    {

        Intent j = new Intent();
        j.putExtra("username",login);
        j.putExtra("password",password);

        setResult(RESULT_OK,j);
        finish();
    }
}