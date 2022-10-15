package com.example.a493balanin_lab4_graphapi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {

    EditText et_username, et_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);

       // Intent i = getIntent();
    }




    public void onButtonRegister_Click(View v)
    {
        //open connection
        String username = et_username.getText().toString();
        String password = et_password.getText().toString();




        //if (success) onSuccessRegistration(username,password);
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