package thesandwichguys.sandwichstory;

import android.content.Context;

import java.util.ArrayList;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class AppInfo{
    private static AppInfo instance = null;
    private Context my_context; //Objects call to get certain activities

    //to defeat instantiation
    protected AppInfo() {
    }

    //Added to handle volleys 7/11/2017
    public RequestQueue queue;

    //list of saved sandwiches
    public ArrayList<Sandwich> savedSandwich;
    public ArrayList<Sandwich> devSandwich;
    public ArrayList<Ingredients>  ingredientsToEdit;
    public Sandwich sandwichToEdit;

    public static AppInfo getInstance(Context context){
        if(instance == null){
            instance = new AppInfo();
            instance.my_context = context;
            instance.savedSandwich = new ArrayList<>();
            instance.devSandwich = new ArrayList<>();

            instance.ingredientsToEdit = new ArrayList<>();
            instance.sandwichToEdit = null;
            instance.queue = Volley.newRequestQueue(context);
        }
        return instance;
    }

    public void addSandwich(Sandwich sandwich){
        instance.savedSandwich.add(sandwich);
    }

    public void addDevSandwich(Sandwich sandwich){
        instance.devSandwich.add(sandwich);
    }
}
