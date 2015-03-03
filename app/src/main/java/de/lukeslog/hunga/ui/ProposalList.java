package de.lukeslog.hunga.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

import de.lukeslog.hunga.R;
import de.lukeslog.hunga.model.Proposal;
import de.lukeslog.hunga.support.HungaConstants;

public class ProposalList extends Activity {

    public static final String TAG = HungaConstants.TAG;
    Context ctx;

    private List<Proposal> proposals = new ArrayList<Proposal>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipielist);
        ctx = this;
        proposals = new Select().from(Proposal.class).orderBy("phe DESC").execute();
        ListView listViewWithRecipies = (ListView) findViewById(R.id.listView);
        ProposalAdapter adapter = new ProposalAdapter(this, proposals);
        listViewWithRecipies.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listViewWithRecipies.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(ctx, ProposalActivity.class);
                i.putExtra("position", ""+position);
                i.putExtra("name", ""+proposals.get(position).getName());
                startActivity(i);
            }
        });
    }
}