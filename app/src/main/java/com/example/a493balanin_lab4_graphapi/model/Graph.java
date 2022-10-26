package com.example.a493balanin_lab4_graphapi.model;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.example.a493balanin_lab4_graphapi.R;
import com.example.a493balanin_lab4_graphapi.classes.Request;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
//493 Balanin
public class Graph {

    public int Id;
    public String Name;
    public java.util.Date Date;
    public boolean isServerGraph;
    public String token;
    public ArrayList<Node> node = new ArrayList<Node>();
    public ArrayList<Link> link = new ArrayList<Link>();
    public Activity ctx;

    public Graph(int id, String name, Date date, boolean isServerGraph) {
        this.Id = id;
        this.Name = name;
        this.Date = date;
        this.isServerGraph = isServerGraph;

    }
    public Graph(){ }

    public void addNode(Node n)
    {
        n.GraphId = this.Id;
        n.Name = null;
        node.add(n);
    }
    public void renameNode(int pos,String name)
    {
        Node n =node.get(pos);
        n.setName(name);
    }
    public void changeLinkValue(int pos,float value)
    {
        Link l =link.get(pos);
        l.setValue(value);
    }

    public int getGraphStasteImg()
    {
        if (isServerGraph)
        {
            return R.drawable.cloud_graph;
        }
        //Load all info from graph, create custom surfaceView, draw Graph and save it to Bitmap then return
        return R.drawable.local_graph;
    }
    public Node getNodeById(int id){
        for (int i=0; i < node.size();i++){
            Node n = node.get(i);
            if (n.Id == id)
                return n;
        }
        return null;
    }
}
