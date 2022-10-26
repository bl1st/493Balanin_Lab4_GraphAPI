package com.example.a493balanin_lab4_graphapi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.a493balanin_lab4_graphapi.classes.DB;
import com.example.a493balanin_lab4_graphapi.classes.Request;
import com.example.a493balanin_lab4_graphapi.model.User;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    //493 Balanin
    EditText et_username, et_password;
    RadioButton rb_saveUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Authorization");

        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        rb_saveUser = findViewById(R.id.rb_saveUser);

        DB db = new DB(this,getResources().getString(R.string.DB_NAME),null,1);
        User usr = db.getUser();
        db.close();
        if (usr != null){

            et_username.setText(usr.login);
            et_password.setText(usr.password);
            onButtonAuthorize_Click(new View(this));
        }

    }
    //493 Balanin
    public void onButtonAuthorize_Click(View v)
    {
        //Context ctx = this;
        String username = et_username.getText().toString();
        String password = et_password.getText().toString();

        Request r = new Request(){
            public void onSuccess(String res, Context ctx) throws Exception
            {
                if (rb_saveUser.isChecked())
                {
                    DB db = new DB(ctx,getResources().getString(R.string.DB_NAME),null,1);
                    db.saveUser(username,password);
                    Toast.makeText(ctx, "Successful log in", Toast.LENGTH_SHORT).show();
                    db.close();
                }

                JSONObject obj = new JSONObject(res);
                String token = obj.getString("token");

                Intent i = new Intent(ctx, GraphList_Activity.class);
                i.putExtra("login",username);
                i.putExtra("token",token);
                startActivityForResult(i,2);

            }
            public void onFail(Context ctx)
            {
                Toast t = Toast.makeText(ctx,"Something went wrong\nInvalid username or password",Toast.LENGTH_SHORT);
                t.show();
            }
        };
        r.send(this,"PUT","/session/open?name=" + username + "&secret=" + password);
    }
    //493 Balanin
    public void onButtonRegister_Click(View v)
    {
        Intent i = new Intent(this, RegisterActivity.class);
        startActivityForResult(i,1);
    }
    //493 Balanin
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        try
        {
            if (requestCode == 1) //1 = registerActivivty
            {
                if (resultCode == RESULT_OK)
                {
                    String reg_login = data.getStringExtra("login");
                    String reg_password = data.getStringExtra("secret");
                    et_username.setText(reg_login);
                    et_password.setText(reg_password);
                }

            }
            if (requestCode == 2) //1 = GraphList Activity
            {
                DB db = new DB(this,getResources().getString(R.string.DB_NAME),null,1);
                db.clearUsers();
                db.close();
                if (resultCode == RESULT_OK)
                {
                   String token = data.getStringExtra("token");

                    Request r = new Request(){
                        public void onSuccess(String res, Context ctx) throws Exception
                        {
                            Toast.makeText(ctx,"Session closed", Toast.LENGTH_SHORT).show();

                        }
                        public void onFail(Context ctx)
                        {

                        }
                    };
                    r.send(this,"DELETE","/session/close?token=" + token);



                }

            }
        }
        catch (Exception ex)
        {
            Log.e("TEST","SOMETHING WENT WRONG ON ONACTIVITYRESULT");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}