package com.example.a493balanin_lab4_graphapi.model;

public class Node {   //493 Balanin
    public int Id;
    public int GraphId;
    public float X;
    public float Y;
    public String Name;

    public Node(int id, int graphId, float x, float y, String name){
        this.Id = id;
        this.GraphId = graphId;
        this.X = x;
        this.Y = y;
        this.Name = name;

    }
    public Node(){}

    public void setName(String name){
        this.Name = name;
    }


}
