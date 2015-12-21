package de.lukeslog.hunga.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import de.lukeslog.hunga.R;
import de.lukeslog.hunga.model.FoodCombination;
import de.lukeslog.hunga.model.FoodCombinationHelper;
import de.lukeslog.hunga.model.Proposal;
import de.lukeslog.hunga.model.RecipeHelper;

public class ProposalAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;

    List<Proposal> proposals;

    protected ProposalAdapter(Activity ctx, List<Proposal> proposals) {
        context = ctx;
        this.proposals = proposals;
    }

    @Override
    public int getCount() {
        return proposals.size();
    }

    @Override
    public Proposal getItem(int position) {
        return proposals.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0l;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.recipieitem, parent, false);
        try {
            setRecipieInfo(position, itemView);
        } catch (Exception e) {

        }
        return itemView;
    }

    private void setRecipieInfo(int position, View itemView) {
        if(position%2==0) {
            LinearLayout layout = (LinearLayout) itemView.findViewById(R.id.line);
            layout.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
        Proposal proposal = proposals.get(position);
        TextView nameView = (TextView) itemView.findViewById(R.id.name);
        TextView mainValue = (TextView) itemView.findViewById(R.id.mainV);
        TextView secondValue = (TextView) itemView.findViewById(R.id.secondV);
        TextView ingrediensText = (TextView) itemView.findViewById(R.id.ingredients);
        TextView weightText = (TextView) itemView.findViewById(R.id.weight);

        nameView.setText(proposal.getName());
        mainValue.setText((int)proposal.getKcal()+" kcal");
        secondValue.setText((int)proposal.getWeight()+" g");
        if(!FoodCombinationHelper.containsItemGood(proposal)){
            weightText.setText(""+(int)proposal.getWeight()+" g");
        } else {
            weightText.setText(""+proposal.getFactor()+" s");
        }
        ingrediensText.setText(FoodCombinationHelper.getIngredientsEquivGroupAsString(proposal));
    }
}
