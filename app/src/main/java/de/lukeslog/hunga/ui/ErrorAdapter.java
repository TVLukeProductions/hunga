package de.lukeslog.hunga.ui;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.List;

import de.lukeslog.hunga.R;
import de.lukeslog.hunga.model.HungaError;

public class ErrorAdapter extends BaseAdapter {

    LayoutInflater inflater;
    List<HungaError> errors;
    private Context ctx;

    public ErrorAdapter(Activity ctx, List<HungaError> errors) {
        this.ctx=ctx;
        this.errors = errors;
    }

    @Override
    public int getCount() {
        return errors.size();
    }

    @Override
    public Object getItem(int position) {
        return errors.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.erroradapter, parent, false);
        try {
            TextView timestamp = (TextView) itemView.findViewById(R.id.timestamp);
            TextView errortext = (TextView) itemView.findViewById(R.id.errortext);
            timestamp.setText(new DateTime(errors.get(position).getTimestamp()).toString());
            errortext.setText(errors.get(position).getMessage());
        } catch (Exception e) {

        }
        return itemView;
    }
}
