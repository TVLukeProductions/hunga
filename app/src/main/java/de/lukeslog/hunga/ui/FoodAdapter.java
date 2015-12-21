package de.lukeslog.hunga.ui;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.lukeslog.hunga.R;
import de.lukeslog.hunga.model.Food;
import de.lukeslog.hunga.model.FoodCombinationHelper;
import de.lukeslog.hunga.model.FoodGroup;
import de.lukeslog.hunga.model.Ingredient;
import de.lukeslog.hunga.model.Proposal;
import de.lukeslog.hunga.support.HungaConstants;
import de.lukeslog.hunga.support.Logger;

public class FoodAdapter extends BaseAdapter {

    public static final String TAG = HungaConstants.TAG;

    Context context;
    LayoutInflater inflater;

    List<Food> foods;
    Proposal proposal;

    public FoodAdapter(Activity ctx, List<Food> foods, Proposal proposal) {
        this.context = ctx;
        this.foods = foods;
        this.proposal = proposal;
    }

    @Override
    public int getCount() {
        return foods.size();
    }

    @Override
    public Object getItem(int position) {
        return foods.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.foodlistitem, parent, false);
        try {
            setFood(position, itemView);
        } catch (Exception e) {

        }
        return itemView;
    }

    private void setFood(int position, View itemView) {
        Food food = foods.get(position);
        TextView nameView = (TextView) itemView.findViewById(R.id.foodname);
        TextView details = (TextView) itemView.findViewById(R.id.foodDetails);
        ImageView addicon = (ImageView) itemView.findViewById(R.id.addfoodicon);
        List<Ingredient> ingredients = FoodCombinationHelper.getListOfIngredients(proposal);
        //Logger.d(TAG, "--><--"+ingredients.size());
        nameView.setText(food.getName());
        String detailsText = "";
        String unit = "ml";
        if(food.isSolid()) {
            unit = "g";
        }
        if(food.getIsItemGood()) {
            detailsText = food.getBasisMenge()+" "+food.getBaseUnit()+" ("+food.getWeightPerServing()+ " "+unit+")";
        } else {
            detailsText = food.getWeightPerServing()+ " "+unit;
        }
        details.setText(detailsText);
        if(ingredients.size() > 0) {
            if (FoodCombinationHelper.containsFood(proposal, food)) {
                addicon.setImageResource(R.drawable.ic_action_cancel);
            } else {
                addicon.setImageResource(R.drawable.ic_action_drop);
            }
        } else {
            FoodGroup foodGroup = FoodGroup.getFoodGroupByFoodGroupName(food.getFoodGroup());
            addicon.setImageResource(foodGroup.getIcon());
        }
    }
}
