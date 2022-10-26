package com.example.a493balanin_lab4_graphapi;

import static com.example.a493balanin_lab4_graphapi.R.layout.alert_dialog_interface;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.a493balanin_lab4_graphapi.classes.DB;
import com.example.a493balanin_lab4_graphapi.classes.GraphArrayAdapter;
import com.example.a493balanin_lab4_graphapi.classes.Request;
import com.example.a493balanin_lab4_graphapi.model.Graph;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

public class GraphList_Activity extends AppCompatActivity {
    //493 Balanin
    public String token = "";
    EditText et_graphName;
    ListView lv;
    String user_login;
    GraphArrayAdapter adp;
    @Override //493 Balanin
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_list);
        this.setTitle("Graph list");

        Intent i = getIntent();
        token = i.getStringExtra("token");
        user_login = i.getStringExtra("login");

        adp = new GraphArrayAdapter(this,
                R.layout.list_view_item, token);
        lv = findViewById(R.id.lv);
        lv.setAdapter(adp);
        //adp.add(new Graph(1,"Mock Graph",new Date(),false));
        adp.notifyDataSetChanged();
        loadLocalGraphs();
        loadServerGraphs();


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent j = new Intent(getBaseContext(), GraphActivity.class);
                j.putExtra("graphId", ((Graph)adp.getItem(i)).Id);
                j.putExtra("graphName", ((Graph)adp.getItem(i)).Name);
                j.putExtra("isServerGraph",((Graph)adp.getItem(i)).isServerGraph);
                j.putExtra("token",token);
                startActivityForResult(j,10);

            }
        });

        Toast.makeText(this,"Successfully logged in",Toast.LENGTH_SHORT).show();
    }
    //493 Balanin
    private void loadLocalGraphs()
    {
        DB db = new DB(this, getResources().getString(R.string.DB_NAME),null,1);
        List<Graph> graphList = db.getGraphs();
        for (int i = 0; i < graphList.size(); i++){
            adp.add(graphList.get(i));
        }
        adp.notifyDataSetChanged();
    }
    //493 Balanin
    public void onButtonCreateGraph_Click(View v)
    {
        Context ctx = this;
        Activity activity = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(alert_dialog_interface,null);
        final EditText et = view.findViewById(R.id.et_NewGraphName);
        RadioButton radioServer = view.findViewById(R.id.radioServer);

        builder.setView(view);
        builder.setMessage("Write your message here.");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        String graphName = et.getText().toString();
                        if (graphName.isEmpty()){
                            Toast.makeText(ctx,"Empty Graph name field!",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (radioServer.isChecked())
                        {
                            Request r = new Request(){
                                public void onSuccess(String res, Context ctx) throws Exception
                                {
                                    JSONObject obj = new JSONObject(res);
                                    int id = obj.getInt("id");
                                    Graph g = new Graph();
                                    g.Id = id;
                                    g.Date = new Date();
                                    g.isServerGraph = true;
                                    g.Name = graphName;
                                    adp.add(g);
                                    adp.notifyDataSetChanged();
                                }
                                public void onFail(Context ctx)
                                {
                                    Toast t = Toast.makeText(ctx,"Something went wrong\nFailed to create graph",Toast.LENGTH_SHORT);
                                    t.show();
                                }
                            };
                            r.send(activity,"PUT","/graph/create?token=" + token + "&name=" + graphName);
                        }
                        else {
                            DB db = new DB(ctx, getResources().getString(R.string.DB_NAME),null,1);
                            Graph g = new Graph();
                            g.Name = graphName;
                            g.isServerGraph = false;
                            g.Date = new Date();
                            db.createGraph(g);
                            adp.add(g);
                            adp.notifyDataSetChanged();
                            db.close();
                        }
                    }
                });
        builder.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();

    }

    //493 Balanin
    public void loadServerGraphs()
    {
        Request r = new Request(){
            public void onSuccess(String res, Context ctx) throws Exception
            {
                JSONArray obj = new JSONArray(res);
                for (int i = 0; i < obj.length(); i++)
                {
                    JSONObject graph = obj.getJSONObject(i);
                    Graph g = new Graph();
                    g.Id = graph.getInt("id");
                    g.Name = graph.getString("name");
                    g.Date = new Date(graph.getLong("timestamp")*1000);
                    g.isServerGraph = true;
                    adp.add(g);
                }
                adp.notifyDataSetChanged();
            }
            public void onFail()
            {
                Context ctx = getBaseContext();
                Toast t = Toast.makeText(ctx,"Something went wrong\nInvalid username or password",Toast.LENGTH_SHORT);
                t.show();
            }
        };
        r.send(this,"GET","/graph/list?token=" + token);
    }
    //493 Balanin
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.settings_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    //493 Balanin
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_listSessions:
                Intent i = new Intent(this, SessionsActivity.class);
                i.putExtra("token",token);
                startActivityForResult(i,100);
                break;
            case R.id.item_ChangePassword:
                final String[] password = new String[1];
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Change account password");
                alert.setMessage("Write new password:");
                final EditText input = new EditText(this);
                alert.setView(input);
                Activity act = this;

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        password[0] = input.getText().toString();
                            Request r = new Request()
                            {
                                public void onSuccess(String res, Context ctx) throws Exception {
                                   DB db = new DB(ctx, getResources().getString(R.string.DB_NAME),null,1);
                                   db.updateUser(user_login, password[0]);
                                   db.close();
                                    Toast.makeText(act,"Changed account password",Toast.LENGTH_SHORT).show();
                                }
                                public void onFail(Context ctx) {
                                    Toast t = Toast.makeText(ctx, "Something went wrong\nFailed to change password", Toast.LENGTH_SHORT);
                                    t.show();
                                    return;
                                }
                            };
                            r.send(act, "POST", "/account/update?token=" + token + "&secret=" + password[0]);
                        }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) { } });
                alert.show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }
    //493 Balanin
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        try
        {
            if (requestCode == 100) //1 = GraphList Activity
            {
                if (resultCode == RESULT_OK)
                {
                    Intent  i = new Intent();
                    i.putExtra("token",token);
                    setResult(RESULT_OK,i);
                    finish();
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