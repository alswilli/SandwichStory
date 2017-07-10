package thesandwichguys.sandwichstory;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Matthew Diep on 7/9/2017.
 */


public class AppInfo
{
    private static AppInfo instance = null;
    private Context my_context;



    //list of saved sandwiches
    public ArrayList<Sandwich> savedSandwich;
    public ArrayList<Ingredients>  ingredientsToEdit;
    public Sandwich sandwichToEdit;

    public static AppInfo getInstance(Context context)
    {
        if(instance == null)
        {
            instance = new AppInfo();
            instance.my_context = context;
            instance.savedSandwich = new ArrayList<>();

            instance.ingredientsToEdit = new ArrayList<>();
            instance.sandwichToEdit = null;
            //instance.queue = Volley.newRequestQueue(context);
        }
        return instance;
    }

    public void addSandwich(Sandwich sandwich)
    {
        instance.savedSandwich.add(sandwich);
    }
}
