package de.lukeslog.hunga.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.activeandroid.query.Select;

import java.util.Date;
import java.util.List;

import de.lukeslog.hunga.R;
import de.lukeslog.hunga.model.Food;
import de.lukeslog.hunga.model.ProtokollItem;
import de.lukeslog.hunga.support.HungaUtils;

public class ProtokollItemAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;

    List<ProtokollItem> protokollItems;

    protected ProtokollItemAdapter(Activity ctx, List<ProtokollItem> protokollItems) {
        this.context = ctx;
        this.protokollItems = protokollItems;
    }

    @Override
    public int getCount() {
        return protokollItems.size();
    }

    @Override
    public ProtokollItem getItem(int position) {
        return protokollItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0l;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.activity_protokoll_adapter, parent, false);
        try {
            setProposal(position, itemView);
        } catch (Exception e) {

        }
        itemView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                Intent i = new Intent(context, ProtokollItemSetting.class);
                ProtokollItem protokollItem = protokollItems.get(position);
                SharedPreferences defsettings = PreferenceManager.getDefaultSharedPreferences(context);
                String accId = defsettings.getString("googleAccId", "");
                i.putExtra("timestamp", protokollItem.getTimestamp());
                i.putExtra("barcodeForUse", protokollItem.getBarcodeForUse());
                i.putExtra("userAcc", accId);
                i.putExtra("amount", protokollItem.getAmount());
                context.startActivity(i);
                return true;
            }
        });
        return itemView;
    }

    private void setProposal(int position, View itemView) {
        ProtokollItem protokollItem = protokollItems.get(position);
        String barcodeForUse = protokollItem.getBarcodeForUse();
        Food food = new Select().from(Food.class).where("barcodeForUse = ?", barcodeForUse).executeSingle();
        TextView foodName = (TextView) itemView.findViewById(R.id.foodname);
        foodName.setText(food.getName()+" ("+protokollItem.getAmount()+" "+ HungaUtils.getUnit(food)+")");

        TextView time = (TextView) itemView.findViewById(R.id.time);
        time.setText(new Date(protokollItem.getTimestamp()).toString());
    }
}
