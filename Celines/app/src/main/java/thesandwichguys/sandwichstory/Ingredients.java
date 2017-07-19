package thesandwichguys.sandwichstory;

import org.json.JSONObject;

public class Ingredients {
    public String qty;
    public String unit;
    public String ingredientName;

    public JSONObject jsonIngredientComponents;

    public Ingredients(String name, String qty, String unit) {
        this.ingredientName = name;
        this.qty = qty;
        this.unit = unit;

        //Creating a JSON representation of this class for quick exchanges with database
        try{
            jsonIngredientComponents = new JSONObject();
            jsonIngredientComponents.put("qty", qty);
            jsonIngredientComponents.put("measure", unit);
            jsonIngredientComponents.put("name", ingredientName);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getQty() {
        return qty;
    }


    public String getUnit() {
        return unit;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setQty(String qty){
        this.qty = qty;
        try{
            jsonIngredientComponents.put("qty", qty);
        }catch(Exception e){

        }
    }

    public void setUnit(String unit){
        this.unit = unit;
        try{
            jsonIngredientComponents.put("unit", unit);
        }catch(Exception e){

        }
    }

    public void setIngredientName(String ingredientName){
        this.ingredientName = ingredientName;
        try{
            jsonIngredientComponents.put("ingredientName", ingredientName);
        }catch(Exception e){

        }
    }

    public JSONObject getJsonIngredient() {
        return jsonIngredientComponents;
    }
}
