package com.example.a493balanin_lab4_graphapi.classes;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.widget.Toast;

import com.example.a493balanin_lab4_graphapi.R;
import com.example.a493balanin_lab4_graphapi.model.Graph;
import com.example.a493balanin_lab4_graphapi.model.Link;
import com.example.a493balanin_lab4_graphapi.model.Node;

import org.json.JSONObject;

import java.util.Iterator;

public class GraphView extends SurfaceView {

    public Graph g = new Graph();
    Paint p;
    int selected1 = -1;
    int selected2 = -1;
    int lastSelected = -1;
    boolean linkSelected = false;
    int linkID = -1;
    float rad = 40.0f;
    float squareRadius = 10.0f;
    float last_x;
    float last_y;
    float next_X;
    float next_Y;
    public Context ctx;
    boolean ActionMoved = false;

    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.drawColor(Color.rgb(255,255,255));
        p.setColor(Color.argb(127,50,0,0));
        p.setTextAlign(Paint.Align.CENTER);
        p.setTextSize(40.0f);
        for (int i=0; i < g.link.size(); i++)
        {
            Link l = g.link.get(i);
            Node na = g.getNodeById(l.SourceNode);
            Node nb = g.getNodeById(l.TargetNode);

            float lowCat = Math.abs(nb.X - na.X);
            float highCat = Math.abs(nb.Y -na.Y);

            float gip = (float) Math.sqrt(lowCat * lowCat + highCat * highCat);
            float cos = lowCat/gip;
            float sin = highCat/gip;

            float xa = cos;
            float ya = sin;
            float xb = -xa;
            float yb = -ya;

            float angle = (float) Math.acos(cos);

            float deviationUp = (float) (angle + Math.PI / 7);
            float deviationDown = (float) (angle - Math.PI / 7);

            float arrow1_cos = (float) Math.cos(deviationUp);
            float arrow1_sin = (float) Math.sin(deviationUp);
            float arrow2_cos = (float) Math.cos(deviationDown);
            float arrow2_sin = (float) Math.sin(deviationDown);

            if (na.X > nb.X){
                xa *=-1;
                xb *=-1;
                arrow1_cos *= -1;
                arrow2_cos *=-1;
            }
            if (na.Y > nb.Y)
            {
                ya *=-1;
                yb*=-1;
                arrow1_sin *=-1;
                arrow2_sin *=-1;
            }

            float na_x = na.X + rad * xa;
            float na_y = na.Y + rad * ya;
            float nb_x = nb.X + rad * xb;
            float nb_y = nb.Y + rad * yb;

            float arrow1_x = nb_x + (rad * 1.5f) * (-1 * arrow1_cos);
            float arrow1_y = nb_y + (rad * 1.5f) * (-1 * arrow1_sin);
            float arrow2_x = nb_x + (rad * 1.5f) * (-1 * arrow2_cos);
            float arrow2_y = nb_y + (rad * 1.5f) * (-1 * arrow2_sin);

            canvas.drawLine(nb_x,nb_y,arrow1_x,arrow1_y,p);
            canvas.drawLine(nb_x,nb_y,arrow2_x,arrow2_y,p);
            //connecting line
            canvas.drawLine(na_x,na_y, nb_x,nb_y,p);

            float bx = (na.X + nb.X) * 0.5f;
            float by = (na.Y + nb.Y) * 0.5f;
            float x0 = bx - squareRadius;
            float x1 = bx + squareRadius;
            float y0 = by - squareRadius;
            float y1 = by + squareRadius;

            l.setSquare(x0,x1,y0,y1);
            p.setStyle(Paint.Style.FILL);
            if (linkID == i){ //493 balanin
                p.setColor(Color.argb(255,128,128,0));
            }
            else
            {
                p.setColor(Color.argb(50,50,0,0));
            }
            canvas.drawRect(l.x0,l.y0,l.x1,l.y1,p);
            float x_value = (l.x0 + l.x1) /2;
            float y_value = (l.y0 + l.y1) /2;
            p.setColor(Color.argb(64,50,0,0));
            if (l.Value != 0.0f){
                canvas.drawText(String.valueOf(l.Value),x_value, (float) (y_value + (rad*1.5)),p);
            }
            p.setColor(Color.argb(127,50,0,0));
        }

        for (int i = 0; i < g.node.size(); i++)
        {
            Node n = g.node.get(i);
            p.setStyle(Paint.Style.FILL);
            if (i == selected1) p.setColor(Color.argb(50,127,0,255));
            else if (i == selected2) p.setColor(Color.argb(50,255,0,50));
            else  p.setColor(Color.argb(50,0,127,255));

            canvas.drawCircle(n.X,n.Y, rad,p);
            if (n.Name != null){
                canvas.drawText(n.Name,n.X, n.Y + (2 * rad),p);
            }
            p.setStyle(Paint.Style.STROKE);
            if (i == selected1) p.setColor(Color.rgb(127,0,255));
            else p.setColor(Color.rgb(0,127,255));

            canvas.drawCircle(n.X,n.Y, rad,p);
        }

    }

    public void linkSelectedNodes(Context ctx) {
        if (linkSelected) return;
        if (selected1 < 0) return;
        if (selected2 < 0) return;


        Node na = g.node.get(selected1);
        Node nb = g.node.get(selected2);
        int sourceNode = na.Id;
        int targetNode = nb.Id;
        for (int i = 0; i < g.link.size(); i++) {
            Link l = g.link.get(i);
            if (sourceNode == l.SourceNode && targetNode == l.TargetNode) {
                Toast.makeText(ctx, "This link already exists", Toast.LENGTH_SHORT);
                return;
            }
        }
        int finalSourceNode = sourceNode;
        int finalTargetNode = targetNode;
        if (g.isServerGraph)
        {
            Request r = new Request() {
                public void onSuccess(String res, Context ctx) throws Exception {
                    JSONObject obj = new JSONObject(res);
                    int id = obj.getInt("id");
                    Link l = new Link();
                    l.Id = id;
                    l.SourceNode = finalSourceNode;
                    l.TargetNode = finalTargetNode;
                    g.link.add(l);
                    Log.e("SERVER_LINK_CREATED:", l.Id + " | source:" + l.SourceNode + " | target:" + l.TargetNode);
                    invalidate();

                }

                public void onFail(Context ctx) {
                    Toast t = Toast.makeText(ctx, "Something went wrong\nFailed to create link", Toast.LENGTH_SHORT);
                    t.show();
                    return;
                }
            };
            r.send((Activity) ctx, "PUT", "/link/create?token=" + g.token + "&source=" + sourceNode + "&target=" + targetNode + "&value=0");
        }
        else
        {
            DB  db = new DB(ctx, getResources().getString(R.string.DB_NAME),null,1);
            Link l = new Link();
            l.SourceNode = finalSourceNode;
            l.TargetNode = finalTargetNode;
            l.Value = 0;
            db.createLink(l);
            g.link.add(l);
            db.close();
            invalidate();

        }


    }

    public int get_link_at_xy(float x,float y){

        for (int i = 0; i < g.link.size();i++){
            Link l = g.link.get(i);
            boolean isTouched = l.squareIsTouched(x,y);
            if (isTouched)
                return i;
        }
        return -1;
    }

    public int get_node_at_xy(float x,float y){
        for (int i = g.node.size() - 1; i >= 0; i--)
        {
            Node n = g.node.get(i);
            float dx = x - n.X;
            float dy = y - n.Y;
            if (dx * dx + dy * dy <= rad * rad) {
                return i;
            }
        }
        return -1;
    }

    public GraphView(Context context, AttributeSet attrs) {

        super(context, attrs);
        p = new Paint();
        p.setAntiAlias(true);
        //В последнюю очередь
        setWillNotDraw(false);
    }

    public Node getSelectedNode(){
        if (selected1 ==-1){
            return null;
        }
        Node n = g.node.get(selected1);
        return n;
    }


    public void addNode(Node n){
        g.addNode(n);
        invalidate();
    }


    public void removeNode(Activity ctx){

        DB  db = new DB(ctx, getResources().getString(R.string.DB_NAME),null,1);
        if (linkSelected)
        {
            Link l = g.link.get(linkID);
            int id = l.Id;
            if (g.isServerGraph) {

                Request r = new Request() {
                    public void onSuccess(String res, Context ctx) throws Exception {
                        g.link.remove(linkID);
                        lastSelected = -1;
                        selected1 = -1;
                        selected2 = -1;
                        invalidate();
                        Log.e("SERVER_LINK_REMOVED", l.Id + " | source:" + l.SourceNode + " | target:" + l.TargetNode);
                    }

                    public void onFail(Context ctx) {
                        Toast t = Toast.makeText(ctx, "Something went wrong\nFailed to delete link", Toast.LENGTH_SHORT);
                        t.show();
                        return;
                    }
                };
                r.send(ctx, "DELETE", "/link/delete?token=" + g.token + "&id=" + l.Id);
            }
            else
            {
                db.deleteLink(l);
                g.link.remove(l);
                invalidate();
                db.close();
            }

            return;
        }
        if (selected1 < 0) return;
        int pos = selected1;

        Node n =g.node.get(pos);
        int id = n.Id;

        Iterator<Link> iter = g.link.iterator();
        while (iter.hasNext())
        {
            Link l = iter.next();
            // if this node has links connected
            if (l.SourceNode == id || l.TargetNode == id){
                if (g.isServerGraph) {
                    Request r = new Request() {
                        public void onSuccess(String res, Context ctx) throws Exception {
                            g.link.remove(l);

                        }

                        public void onFail(Context ctx) {
                            Toast t = Toast.makeText(ctx, "Something went wrong\nFailed to delete connected link", Toast.LENGTH_SHORT);
                            t.show();
                            return;
                        }
                    };
                    r.send(ctx, "DELETE", "/link/delete?token=" + g.token + "&id=" + l.Id);
                }
                else {
                    db.deleteLink(l);
                    g.link.remove(l);
                    invalidate();
                    db.close();
                }
                iter.remove();
            }
        }

        if (g.isServerGraph) {
            Request r = new Request() {
                public void onSuccess(String res, Context ctx) throws Exception {
                    g.node.remove(pos);
                    selected1 = -1;
                    invalidate();
                }

                public void onFail(Context ctx) {
                    Toast t = Toast.makeText(ctx, "Something went wrong\nFailed to delete node", Toast.LENGTH_SHORT);
                    t.show();
                    return;
                }
            };
            r.send(ctx, "DELETE", "/node/delete?token=" + g.token + "&id=" + n.Id);
        }
        else
        {
            db.deleteNode(n);
            g.node.remove(pos);
            selected1 = -1;
            invalidate();
            db.close();
        }

    }
    public void changeNodeName(String name)
    {
        if (selected1 ==-1)
        {
            return;
        }
        g.renameNode(selected1, name);
        invalidate();
    }
    public Link getSelectedLink()
    {
        if (linkSelected)
        {
            Link l = g.link.get(linkID);
            return l;
        }
        return null;

    }

    public void changeLinkValue(float value)
    {
        if (linkSelected)
        {
            g.changeLinkValue(linkID,value);
            invalidate();
        }
        else{
            return;
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event)
    { //493 balanin
        float x = event.getX();
        float y = event.getY();
        int action = event.getAction();
        switch (action)
        {
            case MotionEvent.ACTION_DOWN:

                Log.e("ACTION_DOWN", String.valueOf(ActionMoved));
                linkSelected = false;
                int i = get_link_at_xy(x,y);

                if (i > -1){ //link is touched
                    linkSelected = true;
                    linkID = i;
                    invalidate();
                    return true;
                }
                i = get_node_at_xy(x,y);
                linkID = -1;
                int s1 = -1;
                int s2 = -1;

                if (i < 0)
                {
                    selected1 = s1;
                    selected2 = s2;
                    lastSelected = -1;
                    invalidate();
                    return true;
                }
                if (i == selected1)
                {
                    s1= i;
                }
                if (selected1 >=0  && i != selected1)
                {
                    s2 = i;
                    selected2 = s2;
                    s1 = selected1;
                }
                if (selected2 < 0)
                {
                    s1 = i;
                }
                lastSelected = i;
                selected1 = s1;
                selected2 = s2;
                last_x = x;
                last_y = y;
                invalidate();
                return true;

            case MotionEvent.ACTION_UP:
                if (this.ActionMoved)
                {
                    int id = get_node_at_xy(x,y);
                    if (id == -1) return true;
                    Node n = g.node.get(lastSelected);
                    if (g.isServerGraph)
                    {
                        Request r = new Request()
                        {
                            public void onSuccess(String res, Context ctx) throws Exception
                            {
                                setActionMovedFalse();
                                Log.e("MOVE_STOP","ID: " + n.Id + " | X: " + n.X + " |  Y: "  + n.Y);
                            }
                            public void onFail(Context ctx)
                            {
                                Toast t = Toast.makeText(ctx,"Something went wrong\nFailed to move node",Toast.LENGTH_SHORT);
                                t.show();
                                return;
                            }
                        };
                        r.send(g.ctx,"POST","/node/update?token=" + g.token + "&id=" + n.Id + "&x=" + next_X + "&y=" + next_Y + "&name=" + n.Name);
                    }
                    else
                    {
                        DB  db = new DB(ctx, getResources().getString(R.string.DB_NAME),null,1);
                        db.updateNode(n);
                        db.close();
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:

                if (lastSelected >= 0)
                {
                    Node n = g.node.get(lastSelected);
                    next_X = n.X + (x-last_x);
                    next_Y = n.Y + (y-last_y);
                    n.X = next_X;
                    n.Y = next_Y;
                    last_x = x;
                    last_y = y;
                    invalidate();
                    ActionMoved = true;
                    Log.e("TEST","ActionMoved = true");
                }
               return true;

        }
        return super.onTouchEvent(event);

    }

    public void setActionMovedFalse()
    {
        ActionMoved = false;
        Log.e("Test","ActionMoved = false");

    }

}
