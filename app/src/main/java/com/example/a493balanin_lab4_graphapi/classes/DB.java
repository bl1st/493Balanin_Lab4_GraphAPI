package com.example.a493balanin_lab4_graphapi.classes;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.a493balanin_lab4_graphapi.model.User;
import com.example.a493balanin_lab4_graphapi.model.Graph;
import com.example.a493balanin_lab4_graphapi.model.Link;
import com.example.a493balanin_lab4_graphapi.model.Node;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DB extends SQLiteOpenHelper
{
    //493 balanin
    public DB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String sql = "CREATE TABLE Graphs (Id integer PRIMARY KEY AUTOINCREMENT,Name TEXT NOT NULL UNIQUE,Date REAL NOT NULL);";
        db.execSQL(sql);
        sql="CREATE TABLE Nodes (id INTEGER PRIMARY KEY, graphID INTEGER NOT NULL,X REAL NOT NULL, Y REAL NOT NULL,Name TEXT NOT NULL, FOREIGN KEY (graphID) REFERENCES Graphs(id) ON DELETE CASCADE);" ;
        db.execSQL(sql);
        sql="CREATE TABLE Links (id integer PRIMARY KEY,sourceNode INTEGER not null, targetNode INTEGER NOT NULL,value REAL NOT NULL, FOREIGN KEY (sourceNode) REFERENCES Nodes(id) ON DELETE CASCADE,FOREIGN KEY (targetNode) REFERENCES Nodes(id) ON DELETE CASCADE);";
        db.execSQL(sql);
        sql = "CREATE TABLE Users (id integer primary key AUTOINCREMENT, login text NOT NULL, secret TEXT NOT NULL );";
        db.execSQL(sql);
    }

    public List<Graph> getGraphs(){
        List<Graph> gList = new ArrayList<Graph>();
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * From Graphs;";
        Cursor cur = db.rawQuery(sql,null);
        //493 balanin
        if (cur.moveToFirst() == true) {
            do {
                Graph g = new Graph();
                g.Id = cur.getInt(0);
                g.Name = cur.getString(1);
                g.Date = new Date(cur.getLong(2));
                g.isServerGraph = false;
                gList.add(g);
            }while (cur.moveToNext() == true);
        }
        return gList;
    }

    public User getUser(){
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM Users;";
        Cursor cur = db.rawQuery(sql,null);
        if (cur.moveToFirst()){
            User usr = new User();
            usr.login = cur.getString(1);
            usr.password = cur.getString(2);
            return usr;
        }
        return null;
    }
    public void updateUser(String login, String secret){
        SQLiteDatabase db = getWritableDatabase();
        String sql = "UPDATE Users Set secret='" + secret + "' WHERE login='" + login + "';";
        db.execSQL(sql);
    }
    public void clearUsers(){
        SQLiteDatabase db = getWritableDatabase();
        String sql = "DELETE FROM Users;";
        db.execSQL(sql);
    }

    public void saveUser(String login, String password){
        SQLiteDatabase db = getWritableDatabase();
        String sql = "INSERT INTO Users(login, secret) VALUES('"+ login + "','" + password + "');";
        db.execSQL(sql);
    }
    public void updateGraph(Graph g){
        SQLiteDatabase db = getWritableDatabase();
        String sql = "UPDATE Graphs Set Name='" + g.Name + "', WHERE id=" + g.Id + ";";
        db.execSQL(sql);
    }

    public void createGraph(Graph g){
        SQLiteDatabase db = getWritableDatabase();
        String sql = "Insert into Graphs(Name,Date) VALUES('" + g.Name + "'," + g.Date.getTime() + ");";
        db.execSQL(sql);
        db = getReadableDatabase();

        //everything lower may not work
        sql = "SELECT last_insert_rowid();";
        Cursor cur = db.rawQuery(sql,null);
        if (cur.moveToFirst()){
            int id = cur.getInt(0);
            g.Id = id;
        }
    }

    public void deleteGraph(Graph g)
    {    //493 balanin
        int id = g.Id;
        String sql ="";
        //Удаляем старые сведения о графе
        SQLiteDatabase db = getWritableDatabase();
        sql = "DELETE FROM Graphs WHERE id = "+ id +";";
        db.execSQL(sql);
        Log.e("TEST","Graph " + id + " deleted from DB");
    }

    public Graph getGraphById(int id)
    {
        Graph g = new Graph();
        g.isServerGraph = false;
        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT * FROM Graphs WHERE id = "+ id +";";
        Cursor cur = db.rawQuery(sql,null);
        if (cur.moveToFirst()){
            g.Id = cur.getInt(0);
            g.Name = cur.getString(1);
            g.Date = new Date(cur.getLong(2));
        }
        //493 balanin
        sql = "SELECT * FROM Nodes WHERE GraphId = "+ id +";";
        cur = db.rawQuery(sql,null);

        if (cur.moveToFirst()) {
            do {
                Node n = new Node();
                n.Id = cur.getInt(0);
                n.GraphId = cur.getInt(1);
                n.X = cur.getFloat(2);
                n.Y = cur.getFloat(3);
                String text = cur.getString(4);
                if (text.equals("null")) text = null;
                n.setName(text);
                g.node.add(n);

                sql = "SELECT * FROM LINKS WHERE sourceNode=" + n.Id + ";";
                Cursor cursorLinks = db.rawQuery(sql,null);
                if (cursorLinks.moveToFirst())
                    do {

                        Link l = new Link(cursorLinks.getInt(0),cursorLinks.getInt(1),cursorLinks.getInt(2));
                        float value = cursorLinks.getFloat(3);
                        l.setValue(value);
                        if (!g.link.contains(l))
                            g.link.add(l);
                    }
                        while (cursorLinks.moveToNext());
            }while (cur.moveToNext());
        }
        return g;
    }

    public void createNode(Node n, int gId)
    {
        float x = n.X;
        float y = n.Y;
        String name = n.Name;
        SQLiteDatabase db = getWritableDatabase();
        String sql = "Insert into Nodes(graphID,X,Y,Name) VALUES(" + gId + "," + x + "," + y + ",'" + name + "');";
        db.execSQL(sql);
        db = getReadableDatabase();

        //everything lower may not work
        sql = "SELECT last_insert_rowid();";
        Cursor cur = db.rawQuery(sql,null);
        if (cur.moveToFirst()){
            int id = cur.getInt(0);
            n.Id = id;
        }
    }
    public void createLink(Link l)
    {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "Insert into Links(sourceNode,targetNode,value) VALUES(" + l.SourceNode + ", " + l.TargetNode + ", " + l.Value + ");";
        db.execSQL(sql);
        db = getReadableDatabase();

        //everything lower may not work
        sql = "SELECT last_insert_rowid();";
        Cursor cur = db.rawQuery(sql,null);
        if (cur.moveToFirst()){
            int id = cur.getInt(0);
            l.Id = id;
        }
    }

    public void deleteLink(Link l)
    {
        int id = l.Id;
        SQLiteDatabase db = getWritableDatabase();
        String sql = "DELETE FROM Links WHERE id=" + id + ";";
        db.execSQL(sql);
    }
    public void deleteNode(Node n)
    {
        int id = n.Id;
        SQLiteDatabase db = getWritableDatabase();
        String sql = "DELETE FROM Nodes WHERE id=" + id + ";";
        db.execSQL(sql);
    }
    public void updateNode(Node n){
        SQLiteDatabase db = getWritableDatabase();
        String sql = "UPDATE Nodes Set X=" + n.X + " , Y=" + n.Y + ", Name='" + n.Name + "' WHERE id=" + n.Id + ";";
        db.execSQL(sql);

    }
    public void updateLink(Link l)
    {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "UPDATE Links Set  Value='" + l.Value + "' WHERE id=" + l.Id + ";";
        db.execSQL(sql);
    }



    //493 balanin
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}