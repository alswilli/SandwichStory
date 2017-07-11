package thesandwichguys.sandwichstory;

import java.util.ArrayList;

import thesandwichguys.sandwichstory.Ingredients;


//  [Created by Matthew Diep on 7/6/2017]

//begin [Orginal stuff before Edited on July-9-2017]
public class Sandwich {
    public ArrayList<Ingredients> ingredientList;
    private String sandwichName;
    private String msg;
    private boolean vegan = false;
    private boolean vegetarian = false;
    private boolean glutenFree = false;

    public boolean userMade;
    //public String downloads = "0";

    public Sandwich(String name, ArrayList<Ingredients> ingredients, String msg, boolean vegan, boolean vege, boolean glutenFree)
    {
        this.sandwichName = name;
        if(ingredients == null)
            ingredients = new ArrayList<>();
        this.ingredientList = ingredients;
        this.msg = msg;
        this.vegan = vegan;
        this.vegetarian = vege;
        this.glutenFree = glutenFree;
    }

    public ArrayList<Ingredients> getIngredientList()
    {
        return ingredientList;
    }
    public String getSandwichName()
    {
        return sandwichName;
    }
    public String getMsg()
    {
        return msg;
    }
    public Boolean getVegan()
    {
        return vegan;
    }
    public Boolean getVegetarian()
    {
        return vegetarian;
    }
    public Boolean getGlutenFree()
    {
        return glutenFree;
    }

    // end [Orginal stuff before Edited on July-9-2017]


    // begin [added by Chris Hahn July-9-2017]

    public void setSandwichName(String sandwichName)
    {
        this.sandwichName = sandwichName;
    }
    public void setMsg(String msg)
    {
        this.msg = msg;
    }
    public void setIngredientList(ArrayList<Ingredients> ingredients)
    {
        this.ingredientList = ingredients;
    }
    public void setVegan(Boolean vegan)
    {
        this.vegan = vegan;
    }
    public void setVegetarian(Boolean vegetarian)
    {
        this.vegetarian = vegetarian;
    }
    public void setGlutenFree(Boolean glutenFree)
    {
        this.glutenFree = glutenFree;
    }
    // end [added by Chris Hahn July-9-2017]

}

