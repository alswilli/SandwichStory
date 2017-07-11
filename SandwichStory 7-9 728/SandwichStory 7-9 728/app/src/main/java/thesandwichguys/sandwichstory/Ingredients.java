package thesandwichguys.sandwichstory;

import org.json.JSONObject;
import android.util.Log;

/**
 * Created by Matthew Diep on 7/6E/2017.
 * Edited by David and Chris on 7/11/17 12:27PM
 */


public class Ingredients {
    public String qty;
    public String ratio;
    public String unit;
    public String ingredientName;
    public String logTag = "";

    public JSONObject jsonIngredientComponents;

    public Ingredients(String qty, String ratio, String unit) {

        this.qty = qty;
        this.ratio = ratio;
        this.unit = unit;

        //convenient to have JSON representation of this class for quick exchanges with database
        try {
            jsonIngredientComponents = new JSONObject();
            jsonIngredientComponents.put("qty", qty);
            jsonIngredientComponents.put("ratio", ratio);
            jsonIngredientComponents.put("name", unit);
        } catch (Exception e) {
            Log.d(logTag, "Didn't set JSON properly in Ingredient class");
        }
    }

    //getters and setters
    public String getQty() {
        return qty;
    }

    public String getratio() {
        return ratio;
    }

    public String getIngredient() {
        return unit;
    }

    public void setQty(String qty) {
        this.qty = qty;
        try {
            jsonIngredientComponents.put("qty", qty);
        } catch (Exception e) {

        }
    }

    public void setratio(String ratio) {
        this.ratio = ratio;
        try {
            jsonIngredientComponents.put("ratio", ratio);
        } catch (Exception e) {

        }
    }

    public void setIngredient(String unit) {
        this.unit = unit;
        try {
            jsonIngredientComponents.put("name", unit);
        } catch (Exception e) {
            Log.d(logTag, "Didn't set JSON properly in Ingredient class");
        }
    }

    public JSONObject getJsonIngredient() {
        return jsonIngredientComponents;
    }
}