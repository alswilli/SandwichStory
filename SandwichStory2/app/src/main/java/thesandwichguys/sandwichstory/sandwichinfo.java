package thesandwichguys.sandwichstory;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class sandwichinfo extends Activity {

    ArrayList<String> sandwiches;
    String sandName = "Test Burger";
    String sandMessage = "TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST!!!!!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.sandwichinfo);

        ListView sandwichList = (ListView) findViewById(R.id.listview);

        sandwiches = new ArrayList<String>();
        getIngredients();

        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, sandwiches);
        //Set The Adapter
        sandwichList.setAdapter(arrayAdapter);

        //register onCLickListener to handle click events on each item
        sandwichList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3)
            {
                String selectedingredient = sandwiches.get(position);
                Toast.makeText(getApplicationContext(), "Sandwich Selected : " + selectedingredient, Toast.LENGTH_LONG).show();
            }
        });
        /***********************************************************************************/
        /*text view*/
        TextView sandwichName = (TextView) findViewById(R.id.textView);
        sandwichName.setText(sandName);
        TextView sandwichMessage = (TextView) findViewById(R.id.textView2);
        sandwichMessage.setText(sandMessage);
    }

    public void onClickEdit(View v)
    {

    }

    public void onClickDelete(View v)
    {

    }

    void getIngredients()
    {
        sandwiches.add("Tomato Slice");
        sandwiches.add("Grilled Chicken Breast");
        sandwiches.add("Pesto Sauce");
        sandwiches.add("Spinach");
        sandwiches.add("Item 1");
        sandwiches.add("Item 2");
    }


    /*
    //Name of the sandwich
    String sandwichName = "";
    //Ingredients of Sandwich
    ArrayList<Ingredients> ingredients;
    //details of ingredients
    String quanitiy = "";
    String ingredient = "";
    String textMessage = "";
    */

}
