package hunga.lukeslog.de.model;

public class RecipeHelper {

    public static void fillIngredient(Recipie recipe, Food food, double amount, int number) {
        if(number==1) {
            recipe.setFood1(food);
            recipe.setAmount1(amount);
        }
    }

    public static String getIngredientsAsString(Recipie recipe) {
        return "";
    }
}
