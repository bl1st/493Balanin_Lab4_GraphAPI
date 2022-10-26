package com.example.a493balanin_lab4_graphapi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.a493balanin_lab4_graphapi.classes.DB;
import com.example.a493balanin_lab4_graphapi.classes.GraphView;
import com.example.a493balanin_lab4_graphapi.classes.Request;
import com.example.a493balanin_lab4_graphapi.model.Graph;
import com.example.a493balanin_lab4_graphapi.model.Link;
import com.example.a493balanin_lab4_graphapi.model.Node;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;

public class GraphActivity extends AppCompatActivity {

    String token = "";
    GraphView gv;
    int ThreadCounter = 0;
    public ImageView iv;
    DB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        gv = findViewById(R.id.graphView);

        iv = findViewById(R.id.iv);
        iv.setImageResource(R.drawable.loading_spinner);
        gv.ctx = this;
        iv.invalidate();

        db = new DB(this, getResources().getString(R.string.DB_NAME),null,1);

        setTitle("Graph menu");

        Intent i = getIntent();
        token = i.getStringExtra("token");
        boolean isServerGraph = i.getBooleanExtra("isServerGraph",false);
        int id = i.getIntExtra("graphId",-1);

        if (isServerGraph) {
            gv.g.Id = id;
            gv.g.Name = i.getStringExtra("graphName");
            gv.g.isServerGraph = isServerGraph;
            gv.g.token = token;
            gv.g.ctx = this;
            Request r = new Request() {
                public void onSuccess(String res, Context ctx) throws Exception {
                    JSONArray array = new JSONArray(res);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        Link l = new Link();
                        l.Id = obj.getInt("id");
                        l.SourceNode = obj.getInt("source");
                        l.TargetNode = obj.getInt("target");
                        l.Value = (float) obj.getDouble("value");
                        gv.g.link.add(l);
                        Log.e("LINK_DOWNLOAD", l.Id + " | " + l.SourceNode + " | " + l.TargetNode + " | VALUE: " + l.Value);
                    }
                    ThreadsDone(1);
                }

                public void onFail(Context ctx) {
                    Toast t = Toast.makeText(ctx, "Something went wrong\nFailed to load links", Toast.LENGTH_SHORT);
                    t.show();
                    setResult(RESULT_CANCELED);
                    finish();
                }
            };

            r.send(this, "GET", "/link/list?token=" + token + "&id=" + gv.g.Id);

            r = new Request() {
                public void onSuccess(String res, Context ctx) throws Exception {
                    JSONArray array = new JSONArray(res);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        Node n = new Node();
                        n.Id = obj.getInt("id");
                        n.X = (float) obj.getDouble("x");
                        n.Y = (float) obj.getDouble("y");
                        String name = obj.getString("name");
                        if (name.equals("null"))
                            n.Name = null;
                        else
                            n.Name = name;
                        Log.e("NODE_DOWNLOAD", n.Id + " | " + n.X + " | " + n.Y);

                        gv.g.node.add(n);

                    }
                    ThreadsDone(1);
                }

                public void onFail(Context ctx) {
                    Toast t = Toast.makeText(ctx, "Something went wrong\nFailed to load nodes", Toast.LENGTH_SHORT);
                    t.show();
                    setResult(RESULT_CANCELED);
                    finish();
                }
            };

            r.send(this, "GET", "/node/list?token=" + token + "&id=" + gv.g.Id);
        }
        else //if local graph
        {
            Graph g = db.getGraphById(id);
            gv.g = g;
            gv.invalidate();
            iv.setImageResource(R.drawable.complete);
            iv.invalidate();
        }
        setTitle("Graph: " + gv.g.Name);

    }

    public void onButtonAddNode_Click(View v)
    {
        iv.setImageResource(R.drawable.loading_spinner);
        iv.invalidate();
        if (gv.g.isServerGraph) {
            Request r = new Request() {
                public void onSuccess(String res, Context ctx) throws Exception {
                    Node n = new Node();
                    n.X = 100;
                    n.Y = 100;
                    n.Name = null;
                    JSONObject obj = new JSONObject(res);
                    n.Id = obj.getInt("id");
                    gv.addNode(n);
                    iv.setImageResource(R.drawable.complete);
                }

                public void onFail(Context ctx) {
                    Toast t = Toast.makeText(ctx, "Something went wrong\nFailed to create node", Toast.LENGTH_SHORT);
                    t.show();
                    return;
                }
            };
            r.send(this, "PUT", "/node/create?token=" + token + "&id=" + gv.g.Id + "&x=100&y=100&name=");
        }
        else
        {
            Node n = new Node();
            n.X = 100;
            n.Y = 100;
            n.Name = null;
            db.createNode(n,gv.g.Id);
            gv.g.node.add(n);
            gv.invalidate();

        }

    }
    public void onButtonRemoveNode_Click(View v)
    {
        iv.setImageResource(R.drawable.loading_spinner);
        iv.invalidate();
        gv.removeNode(this);
        iv.setImageResource(R.drawable.complete);
    }

    public void onButtonLinkNodes_Click(View v)
    {
        iv.setImageResource(R.drawable.loading_spinner);
        iv.invalidate();

        gv.linkSelectedNodes(this);

        iv.setImageResource(R.drawable.complete);
        gv.invalidate();
    }

    public void onButtonRenameNode_Click(View v)
    {
        final String[] name = new String[1];
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Change node text");
        alert.setMessage("Insert new node text");
        //Create TextView
        final EditText input = new EditText(this);
        alert.setView(input);
        Node n = gv.getSelectedNode();
        Activity act = this;
        if (n == null)
            return;

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                name[0] = input.getText().toString();
                if (gv.g.isServerGraph) {
                    Request r = new Request()
                    {
                        public void onSuccess(String res, Context ctx) throws Exception {
                            gv.changeNodeName(name[0]);
                        }

                        public void onFail(Context ctx) {
                            Toast t = Toast.makeText(ctx, "Something went wrong\nFailed to rename node", Toast.LENGTH_SHORT);
                            t.show();
                            return;
                        }
                    };
                    r.send(act, "POST", "/node/update?token=" + token + "&id=" + n.Id + "&x=" + n.X + "&y=" + n.Y + "&name=" + name[0]);
                }
                else
                {
                    n.Name = name[0];
                    db.updateNode(n);
                    gv.invalidate();
                }
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) { } });
        alert.show();

    }
    public void onButtonChangeLinkValue_Click(View v){
        final float[] value = new float[1];
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Link value");
        alert.setMessage("Insert new link value");
        //Create TextView
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        alert.setView(input);
        Activity act = this;
        Link l = gv.getSelectedLink();
        if (l == null) return;

        //493 balanin
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                value[0] = Float.parseFloat(input.getText().toString());
                if (gv.g.isServerGraph) {

                    Request r = new Request() {
                        public void onSuccess(String res, Context ctx) throws Exception {

                            gv.changeLinkValue(value[0]);
                            gv.invalidate();
                            Log.e("LINK_CHANGED", l.Id + " | " + l.SourceNode + " | " + l.TargetNode + " | VALUE: " + l.Value);
                        }

                        public void onFail(Context ctx) {
                            Toast t = Toast.makeText(ctx, "Something went wrong\nFailed to rename node", Toast.LENGTH_SHORT);
                            t.show();
                            return;
                        }
                    };
                    r.send(act, "POST", "/link/update?token=" + token + "&id=" + l.Id + "&value=" + value[0]);
                }
                else {
                    l.Value = value[0];
                    db.updateLink(l);
                    gv.invalidate();
                }


            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) { } });
        alert.show();

    }
    public void button_RenameGraph_Click(View v){
        final String[] name = new String[1];
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Change graph name");
        alert.setMessage("Insert new graph name");
        //Create TextView
        final EditText input = new EditText(this);
        alert.setView(input);
        Activity act = this;

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                name[0] = input.getText().toString();
                if (gv.g.isServerGraph) {
                    Request r = new Request()
                    {
                        public void onSuccess(String res, Context ctx) throws Exception {
                            gv.g.Name = name[0];
                            Toast.makeText(act,"Changed graph name",Toast.LENGTH_SHORT).show();
                        }

                        public void onFail(Context ctx) {
                            Toast t = Toast.makeText(ctx, "Something went wrong\nFailed to rename node", Toast.LENGTH_SHORT);
                            t.show();
                            return;
                        }
                    };
                    r.send(act, "POST", "/graph/update?token=" + token + "&id=" + gv.g.Id + "&name=" + name[0]);
                }
                else
                {
                    gv.g.Name = name[0];
                    db.updateGraph(gv.g);
                    setTitle("Graph: " + gv.g.Name);
                }
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) { } });
        alert.show();
    }






    public void ThreadsDone(int i){
        ThreadCounter+= i;
        if (ThreadCounter == 2)
        {
            iv.setImageResource(R.drawable.complete);
            gv.invalidate();
        }
    }


}