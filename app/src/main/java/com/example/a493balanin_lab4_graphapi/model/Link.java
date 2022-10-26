package com.example.a493balanin_lab4_graphapi.model;

public class Link {   //493 Balanin
    public int Id;
    public int SourceNode;
    public int TargetNode;
    public float Value;

    public float x0,x1,y0,y1; //квадрат для нажатия

    public Link(int id, int sourceNode, int targetNode)
    {
        Id = id;
        SourceNode = sourceNode;
        TargetNode = targetNode;
    }
    public Link() {}

    public void setSquare(float x0,float x1, float y0,float y1)
    {
        this.x0 = x0;
        this.x1 = x1;
        this.y0 = y0;
        this.y1 = y1;
    }
    public void setValue(float value)
    {
        this.Value = value;
    }

    public boolean squareIsTouched(float x,float y)
    {
        return x > x0 && x < x1 && y > y0 && y < y1;
    }
}


