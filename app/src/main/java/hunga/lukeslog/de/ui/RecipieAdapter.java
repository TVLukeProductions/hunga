package hunga.lukeslog.de.ui;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.activeandroid.query.Select;

import java.util.List;

import hunga.lukeslog.de.R;
import hunga.lukeslog.de.model.RecipeHelper;
import hunga.lukeslog.de.model.Recipie;
import hunga.lukeslog.de.model.SearchResult;

public class RecipieAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;

    protected RecipieAdapter(Activity ctx) {
        context = ctx;
    }

    @Override
    public int getCount() {
        return new Select().from(SearchResult.class).count();
    }

    @Override
    public SearchResult getItem(int position) {
        List<SearchResult> searchresults;
        searchresults = new Select().from(SearchResult.class).execute();
        return searchresults.get(position);
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
        List<SearchResult> searchresults;
        searchresults = new Select().from(SearchResult.class).execute();
        SearchResult searchResult = searchresults.get(position);
        TextView nameView = (TextView) itemView.findViewById(R.id.name);
        TextView mainValue = (TextView) itemView.findViewById(R.id.mainV);
        TextView secondValue = (TextView) itemView.findViewById(R.id.secondV);
        TextView ingrediensText = (TextView) itemView.findViewById(R.id.ingredients);
        nameView.setText(searchResult.getRecipie().getName());
        mainValue.setText(searchResult.getPhe() + " phe");
        secondValue.setText(searchResult.getKcal()+" kcal");
        ingrediensText.setText(getIngredients(searchResult.getRecipie()));
    }

    private String getIngredients(Recipie recipie) {
        return RecipeHelper.getIngredientsAsString(recipie);
    }
}
