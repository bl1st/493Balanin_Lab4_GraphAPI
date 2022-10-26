package com.example.a493balanin_lab4_graphapi.classes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a493balanin_lab4_graphapi.R;
import com.example.a493balanin_lab4_graphapi.model.Graph;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class GraphArrayAdapter extends ArrayAdapter<Graph> {
    private static final String TAG = "GraphArrayAdapter";
    private List<Graph> graphList = new ArrayList<Graph>();
    private String token = "";

    static class GraphViewHolder {
        ImageView graphImg;
        TextView graphName;
        TextView graphDate;
        Button graphDelete;

    }

    public GraphArrayAdapter(Context context, int textViewResourceId, String token) {
        super(context, textViewResourceId);
        this.token = token;
    }

    @Override
    public void add(Graph object) {
        graphList.add(object);
        super.add(object);
    }

    @Override
    public int getCount() {
        return this.graphList.size();
    }

    @Override
    public Graph getItem(int index) {
        return this.graphList.get(index);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        GraphViewHolder viewHolder;
        Graph graph = getItem(position);
        if (row == null)
        {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_view_item, parent, false);
            viewHolder = new GraphViewHolder();
            viewHolder.graphImg = (ImageView) row.findViewById(R.id.img_graph);
            viewHolder.graphName = (TextView) row.findViewById(R.id.tv_graphName);
            viewHolder.graphDate = (TextView) row.findViewById(R.id.tv_dateTime);
            viewHolder.graphDelete = (Button) row.findViewById(R.id.btn_delete);

            row.setTag(viewHolder);
        } else {
            viewHolder = (GraphViewHolder)row.getTag();
        }

        viewHolder.graphImg.setImageResource(graph.getGraphStasteImg());
        viewHolder.graphName.setText(graph.Name);
        DateFormat df = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        viewHolder.graphDate.setText(df.format(graph.Date));
        viewHolder.graphDelete.setTag(position);
        viewHolder.graphDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Are you sure you want to delete this graph?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        int pos = (int) v.getTag();
                        Activity ctx = (Activity) v.getContext();
                        Graph g = graphList.get(pos);
                        if (g.isServerGraph)
                        {
                            Request r = new Request(){
                                public void onSuccess(String res, Context ctx) throws Exception
                                {
                                    Log.e("TEST","GRAPH DELETE RESPONSE CODE 200");
                                }
                                public void onFail(Context ctx)
                                {
                                    Toast t = Toast.makeText(ctx,"Something went wrong\nFailed to create graph",Toast.LENGTH_SHORT);
                                    t.show();
                                    return;
                                }
                            };
                            r.send(ctx,"DELETE","/graph/delete?token=" + token + "&id=" + g.Id);
                        }
                        else
                        {
                            DB db = new DB(ctx,ctx.getResources().getString(R.string.DB_NAME),null,1);
                            db.deleteGraph(g);
                        }
                        graphList.remove(pos);
                        GraphArrayAdapter.super.notifyDataSetInvalidated();

                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { dialog.dismiss(); }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });


        return row;
    }

    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}
