package thesandwichguys.sandwichstory;

import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    AppInfo appInfo;
    static final public String MYPREFS = "myprefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        // Adding images for the tabs
        tabLayout.getTabAt(0).setIcon(R.drawable.my_list);
        tabLayout.getTabAt(1).setIcon(R.drawable.preset);
        tabLayout.getTabAt(2).setIcon(R.drawable.create);
        tabLayout.getTabAt(3).setIcon(R.drawable.library);
        appInfo = AppInfo.getInstance(this);
        loadRecipes();
    }

    public void onPause(){
        saveAsJSON();
        super.onPause();
    }

    public void saveAsJSON(){
        JSONArray jArray = new JSONArray();
        for(Sandwich savedRecipe : appInfo.savedSandwich){
            try{
                jArray.put(savedRecipe.sandwichAsJSON);
            }catch(Exception e){
                Log.e("test", "Error saving sandwiches as json: " + e.getStackTrace());
            }
        }

        SharedPreferences settings = getSharedPreferences(MYPREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("sandwichesAsJSON", jArray.toString());
        editor.commit();
    }
    public void loadRecipes(){
        //Get Preferences
        SharedPreferences settings = getSharedPreferences(MYPREFS, 0);
        String sandwichesAsJSON = settings.getString("sandwichesAsJSON", null);
        Log.d("Celine", sandwichesAsJSON);

        if(sandwichesAsJSON == null){
            Log.d("debug", "No sandwiches!");
            return;
        }
        try{
            JSONArray allSandwiches = new JSONArray(sandwichesAsJSON);      //Creates a JSONArray for the sandwich Objects
            appInfo.savedSandwich.clear();                                  //Clear the array list before adding elements
            for(int i = 0; i < allSandwiches.length(); i++){                //For the number of sandwiches in the JSON array
                JSONObject recipe = allSandwiches.getJSONObject(i);         //get sandwich from the list to manipulate
                String name = recipe.getString("name");                     //get name of sandwich
                String instructions = recipe.getString("msg");              //get instructions
                String img = recipe.getString("imageId");                         //get image

                //Get each ingredient as JSON and convert to an Ingredient Object
                JSONArray ingredientJSONList = recipe.getJSONArray("ingredients");  //Stores the Ingredients list from the sandwich recipe into a JSONArray
                ArrayList<Ingredients> ingredientArrayList = new ArrayList<>();    // Creates an ArrayList to store the JSONArray of ingredients list
                for(int j = 0; j < ingredientJSONList.length(); j++){   //For the number of ingredients in the JSON array
                    JSONObject ingredientComponents = ingredientJSONList.getJSONObject(j);
                    String qty = ingredientComponents.getString("qty");     //Stores the quantity of the ingredient
                    String unit = ingredientComponents.getString("unit");   //Stores the unit of measurement
                    String ingredientName = ingredientComponents.getString("ingredientName");   //Stores the ingredient name

                    ingredientArrayList.add(new Ingredients(ingredientName, qty, unit)); //Adds the ingredient data into the Ingredients list
                }
                appInfo.savedSandwich.add(new Sandwich(name, ingredientArrayList, instructions, img));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



// MOST LIKELY DELETABLE, TO BE DETERMINED IF EVERYTHING STILL WORKS WITH THIS COMMENTED
    // DELETE AT END OF PROJECT
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

    // MOST LIKELY DELETABLE, TO BE DETERMINED IF EVERYTHING STILL WORKS WITH THIS COMMENTED
    // DELETE AT END OF PROJECT
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    home tab1 = new home();
                    return tab1;
                case 1:
                    presetRecipes tab2 = new presetRecipes();
                    return tab2;
                case 2:
                    newSandwich tab3 = new newSandwich();
                    return tab3;
                case 3:
                    library tab4 = new library();
                    return tab4;
                default:
                    return null;
            }
        }

        // Used to display the number of tabs at the top navigation bar
        @Override
        public int getCount() {
            return 4;
        }

        // Displays
        // the name for each tab
//        @Override
//        public CharSequence getPageTitle(int position) {
//            switch (position) {
//                case 0:
//                    return "Home";
//                case 1:
//                    return "Preset";
//                case 2:
//                    return "Create";
//                case 3:
//                    return "Library";
//            }
//            return null;
//        }
    }
}
