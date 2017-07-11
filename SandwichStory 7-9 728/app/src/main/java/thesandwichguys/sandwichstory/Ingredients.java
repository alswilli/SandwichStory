package thesandwichguys.sandwichstory;

import org.json.JSONObject;

/**
 * Created by Matthew Diep on 7/6E/2017.
 */

public class Ingredients {
    public String qty;
    public String ratio;
    public String unit;
    public String ingredientName;

    public JSONObject jsonIngredientComponents;

    public Ingredients(String name, String qty, String ratio, String unit) {
        this.ingredientName = name;
        this.qty = qty;
        this.ratio = ratio;
        this.unit = unit;
    }

    public String getQty() {
        return qty;
    }

    public String getMeasure() {
        return ratio;
    }

    public String getUnit() {
        return unit;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public JSONObject getJsonIngredient() {
        return jsonIngredientComponents;
    }
}
