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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a493balanin_lab4_graphapi.R;
import com.example.a493balanin_lab4_graphapi.model.Graph;
import com.example.a493balanin_lab4_graphapi.model.Session;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class SessionArrayAdapter extends ArrayAdapter<Session> {
    private static final String TAG = "SessionArrayAdapter";
    private List<Session> sessionList = new ArrayList<Session>();

    static class SessionViewHolder {
        TextView token;
        TextView date;
        ImageButton btn_delete;
    }

    public SessionArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    @Override
    public void add(Session object) {
        sessionList.add(object);
        super.add(object);
    }

    @Override
    public int getCount() {
        return this.sessionList.size();
    }

    @Override
    public Session getItem(int index) {
        return this.sessionList.get(index);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        SessionViewHolder viewHolder;
        Session session = getItem(position);
        if (row == null)
        {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.listview_session_item, parent, false);
            viewHolder = new SessionViewHolder();

           viewHolder.token = (TextView) row.findViewById(R.id.tv_SessionToken);
           viewHolder.date = (TextView) row.findViewById(R.id.tv_SessionDate);
           viewHolder.btn_delete = (ImageButton) row.findViewById(R.id.btn_deleteSession);


            row.setTag(viewHolder);
        } else {
            viewHolder = (SessionViewHolder)row.getTag();
        }

        viewHolder.token.setText(session.Token);
        DateFormat df = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        viewHolder.date.setText(df.format(session.Date));
        viewHolder.btn_delete.setTag(position);
        viewHolder.btn_delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                int pos = (int) v.getTag();
                Activity ctx = (Activity) v.getContext();
                Session s = sessionList.get(pos);
                    Request r = new Request(){
                        public void onSuccess(String res, Context ctx) throws Exception
                        {
                           sessionList.remove(s);
                           SessionArrayAdapter.super.notifyDataSetChanged();
                        }
                        public void onFail(Context ctx)
                        {
                            Toast t = Toast.makeText(ctx,"Something went wrong\nFailed to delete session",Toast.LENGTH_SHORT);
                            t.show();
                            return;
                        }
                    };
                    r.send(ctx,"DELETE","/session/close?token=" + s.Token);

            }
        });
        return row;
    }


}
