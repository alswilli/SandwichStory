package thesandwichguys.sandwichstory;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import java.util.ArrayList;
import android.widget.SearchView;
import android.widget.ImageView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONArray;
import org.json.JSONObject;
import android.support.v7.widget.RecyclerView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.app.ProgressDialog;

public class library extends Fragment {

    GridView gridView;                      //gridView for library sandwiches
    gridAdapter adapter;
    ArrayList<Sandwich> listOfSandwiches;

    GridView searchGridView;                //gridView for search result sandwiches
    gridAdapter searchAdapter;
    ArrayList<Sandwich> searchSandwiches;

    View rootView;
    AppInfo appInfo;
    SearchView searchView;
    String logTag = "hey";
    AlertDialog.Builder alertDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.library, container, false);
        listOfSandwiches = new ArrayList<>();

        searchView = (SearchView) rootView.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query, null, null);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText){
                return false;
            }
        });

        int searchCloseButtonId = searchView.getContext().getResources().getIdentifier("android:id/search_close_btn", null, null);
        ImageView closeButton = (ImageView) this.searchView.findViewById(searchCloseButtonId);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.onActionViewCollapsed();
                searchView.setQuery("", false);
                searchView.clearFocus();

                if (searchGridView != null) {
                    //make recycler views visible after search recycler view is set to invisible
                    searchSandwiches.clear();
                    searchAdapter.notifyDataSetChanged();
                    searchGridView.setVisibility(View.GONE);
                    gridView.setVisibility(View.VISIBLE);
                }
            }
        });

        gridView = (GridView) rootView.findViewById(R.id.gridView);
        adapter = new gridAdapter(getActivity(), listOfSandwiches);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //when user selects an item, opens up the info page for that specific recipe
                Intent nextScreen = new Intent(getActivity(), sandwichInfo.class);
                nextScreen.putExtra("index", position);
                startActivity(nextScreen);
            }
        });

        appInfo = AppInfo.getInstance(getActivity());
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRecipes(null, "get_user_recipes", listOfSandwiches, adapter);
        listOfSandwiches.clear();
        for (Sandwich recipe : appInfo.savedSandwich) {
            listOfSandwiches.add(recipe);
        }
        adapter.notifyDataSetChanged();
    }


    //call data base to perform query
    public void search(String q, final ArrayList<Sandwich> sandwichArrayList, final RecyclerView.Adapter adapter) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("q", q);
        } catch (Exception e) {
            Log.d(logTag, "Problem putting query in POST");
        }

        //initialize search recycler view if it is null
        if (searchGridView == null) {
            //init arraylist and find recylcer view by id
            searchSandwiches = new ArrayList<>();
            searchGridView = (GridView) getActivity().findViewById(R.id.searchGridView);
            searchAdapter = new gridAdapter(getActivity(), searchSandwiches);
            searchGridView.setAdapter(searchAdapter);
            searchGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //when user selects an item, opens up the info page for that specific recipe
                    Intent nextScreen = new Intent(getActivity(), sandwichInfo.class);
                    nextScreen.putExtra("index", position);
                    startActivity(nextScreen);
                }
            });
        }

        JsonObjectRequest jsobj = new JsonObjectRequest(
                "https://sandwichstory-172520.appspot.com/api/search", obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(logTag, "Received: " + response.toString());
                        try {
                            //hide all other recycler views and display the search results
                            gridView.setVisibility(View.GONE);

                            searchGridView.setVisibility(View.VISIBLE);

                            //load search results into the search arraylist and notify adapter to
                            //update the scrren
                            JSONArray receivedList = response.getJSONArray("result");
                            decodeJSON(receivedList, searchSandwiches, searchAdapter);

                        } catch (Exception e) {
                            Log.d(logTag, "Issue with search response" + e.getStackTrace());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //no error respsonse handling
            }
        });


        appInfo.queue.add(jsobj);
    }

//
//    /*
//   same on click listener used by every recycler view. On click, it goes to sandwichinfo
//   for the sandwiches that has been clicked on
//    */
//    class sandwichListener implements RecyclerItemClickListener.OnItemClickListener
//    {
//        ArrayList<sandwich> sandwiches;
//        sandwichListener(ArrayList<sandwich> sandwiches)
//        {
//            this.sandwiches = sandwiches;
//        }
//
//        @Override public void onItemClick(View view, int position)
//        {
//            //store sandwich to be displayed in appInfo
//            appInfo.sandwichFromLib = sandwiches.get(position); //appInfo function may be wrong
//
//            //create intent to go to sandwichinfo
//            //Intent intent = new Intent(library.this, sandwichinfo.class);  //may not need this, BUT PROBABLY WILL
//
//            //put extra that specifies that we are coming from lib activity
//            //intent.putExtra("fromLib", true);                              //may not need this, BUT PROBABLY WILL
//
//            //startActivity(intent);                                         //may not need this, BUT PROBABLY WILL
//        }
//        @Override public void onItemLongClick(View v, int pos)
//        {
//            //nothing, boilerplate necessary
//        }
//    }



    public void loadRecipes(View view, String apiMethod, final ArrayList<Sandwich> sandwichArrayList, final gridAdapter adapt){
        //display spinning progress bar while waiting for database response
        final ProgressDialog progress = new ProgressDialog(getActivity(), ProgressDialog.STYLE_SPINNER);
        progress.setTitle("Loading");
        progress.setMessage("Loading recipes...");

        //disable dismiss by tapping outside of the dialog
        progress.setCancelable(false);
        progress.show();

        String url = "https://sandwichstory-172520.appspot.com/api/" + apiMethod;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        progress.dismiss();
                        Log.d(logTag, "Received: " + response.toString());

                        try {
                            //load received list into the given arraylist and update the screen
                            JSONArray receivedList = response.getJSONArray("result");
                            decodeJSON(receivedList, sandwichArrayList, adapt);

                        } catch (Exception e)
                        {
                            Log.d(logTag, ""+ e.getStackTrace());
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Dismiss progress bar and let user know there was an issue connecting to server
                        Log.d(logTag, error.toString());
                        progress.dismiss();

//                        if (alertDialog == null){
//                            alertDialog = new AlertDialog.Builder(getContext());
//                            alertDialog.setTitle("No Connection");
//                            alertDialog.setMessage("Sorry, there was an issue connecting to server.")
//                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int id) {
//
//                                            dialog.dismiss();
//                                            alertDialog = null;
//                                        }
//                                    });
//                            alertDialog.show();
//                        }
                    }
                });

        appInfo.queue.add(jsObjRequest);
    }

    public void decodeJSON(JSONArray jArray, ArrayList<Sandwich> sandwichArrayList, gridAdapter adapt){
        try{
            //clear current arraylist to copy new sandwiches into it
            sandwichArrayList.clear();
            for(int i = 0; i < jArray.length(); i++){
                JSONObject jsonSandwichRecipe = jArray.getJSONObject(i);

                //get name
                String name = jsonSandwichRecipe.getString("obj_name");

                //get ingredient list
                JSONArray jsonIngredients = jsonSandwichRecipe.getJSONArray("ingredients"); //name may be wrong
                ArrayList<Ingredients> ingredients = new ArrayList<>();

                //add each component of json ingredient list to sandwich ingredient list
                for(int j = 0; j < jsonIngredients.length(); j++){
                    JSONObject jsonIngredientComponents = jsonIngredients.getJSONObject(j);
                    String ingName = jsonIngredientComponents.getString("ingredientName");
                    String qty = jsonIngredientComponents.getString("qty");
                    String unit = jsonIngredientComponents.getString("unit");
                    ingredients.add(new Ingredients(ingName, qty, unit ));
                }

                //get img id
                int imgId = Integer.parseInt(jsonSandwichRecipe.getString("imageId")); //may not need this

                //get unique id to keep track of sandwiches that are downloaded so we can track popularity
                String uniqueId = jsonSandwichRecipe.getString("uniqueid"); //name may be wrong

                //get message
                String msg = jsonSandwichRecipe.getString("message");

                //get downloads
                int downloads = jsonSandwichRecipe.getInt("download_count");

                //add this sandwich to the array list
                sandwichArrayList.add(new Sandwich(name, ingredients, msg)); // constructor is probably wrong
            }
        }
        catch(Exception e){
            Log.d(logTag, "JSON loading failed");
        }

        adapt.notifyDataSetChanged();
    }
}