package thesandwichguys.sandwichstory;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class library extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.library, container, false);
        return rootView;
    }
}


//package thesandwichguys.sandwichstory;
//
//
//import android.support.v4.app.Fragment;
//import android.content.Intent;
//import android.media.MediaPlayer;
//import android.support.v7.app.AppCompatActivity;
//import android.view.LayoutInflater;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.TextView;
//import android.widget.SearchView;
//import android.widget.ImageView;
//import com.android.volley.Request;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonObjectRequest;
//import org.json.JSONArray;
//import org.json.JSONObject;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.app.AlertDialog;
//import android.content.DialogInterface;
//import java.util.ArrayList;
//import android.app.ProgressDialog;
//
//public class library extends Fragment {
//    //searchview for search button
//    SearchView searchView;
//
//    //recycler views and their adapters/ arraylists displayed in this activity
//    public ArrayList<sandwich> dbSandwiches;
//    RecyclerView userCreatedRecyclerView;
//    RecyclerView.Adapter userCreatedAdapter;
//
//    public ArrayList<sandwich> popSandwiches;
//    RecyclerView popRecyclerView;
//    RecyclerView.Adapter popAdapter;
//
//    public ArrayList<sandwich> libSandwiches;
//    RecyclerView libRecyclerView;
//    RecyclerView.Adapter libAdapter;
//
//    public ArrayList<sandwich> searchSandwiches;
//    RecyclerView searchRecyclerView;
//    RecyclerView.Adapter searchAdapter;
//
//    public String logTag = "Library";
//
//    AppInfo appInfo;
//
//    AlertDialog.Builder alertDialog;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//        appInfo = AppInfo.getInstance(this);
//
//        final View rootView = inflater.inflate(R.layout.library, container, false);
//
//        searchView=(SearchView) rootView.findViewById(R.id.searchView);
//
//        //when search view is clicked, hids refresh button
//        searchView.setOnSearchClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Button btn = (Button) rootView.findViewById(R.id.refreshbutton);
//                //btn.setVisibility(View.GONE);
//                Log.d(logTag, "searchCLick");
//            }
//        });
//
//        //set action to perform when the query button is pressed
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//
//            @Override
//            public boolean onQueryTextSubmit(String query)
//            {
//                search(query, null, null);
//                searchView.clearFocus();
//                return true;
//            }
//            @Override
//            public boolean onQueryTextChange(String newText)
//            {
//                //necessary to override, boiler plate
//                return false;
//            }
//        });
//
//        // Catch event on [x] button inside search view
//        int searchCloseButtonId = searchView.getContext().getResources().getIdentifier("android:id/search_close_btn", null, null);
//        ImageView closeButton = (ImageView) this.searchView.findViewById(searchCloseButtonId);
//
//        //When pressed, collapse search view and make recycler views visible again, and hide search recycler view
//        closeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v)
//            {
//                searchView.onActionViewCollapsed();
//                searchView.setQuery("", false);
//                searchView.clearFocus();
//
//                if(searchRecyclerView != null)
//                {
//                    //make recycler views visible after search recycler view is set to invisible
//                    searchSandwiches.clear();
//                    searchAdapter.notifyDataSetChanged();
//                    searchRecyclerView.setVisibility(View.GONE);
//
//                    userCreatedRecyclerView.setVisibility(View.VISIBLE);
//                    libRecyclerView.setVisibility(View.VISIBLE);
//                    popRecyclerView.setVisibility(View.VISIBLE);
//
//                    TextView userText = (TextView)rootView.findViewById(R.id.textViewUserSandwiches);
//                    userText.setVisibility(View.VISIBLE);
//                    TextView libText = (TextView)rootView.findViewById(R.id.textViewLibrarySandwiches);
//                    libText.setVisibility(View.VISIBLE);
//                    TextView popText = (TextView)rootView.findViewById(R.id.textViewPopularSandwiches);
//                    popText.setVisibility(View.VISIBLE);
//                }
//                //Button btn = (Button) rootView.findViewById(R.id.refreshbutton);
//                //btn.setVisibility(View.VISIBLE);
//            }
//        });
//
//        initRecycleViews();
//
//        return rootView;
//    }
//
//    public void initRecycleViews()
//    {
//        //init array list
//        dbSandwiches = new ArrayList<>();
//
//        // get recycler view
//        userCreatedRecyclerView = (RecyclerView) getActivity().findViewById(R.id.recycler_view);
//        userCreatedRecyclerView.setHasFixedSize(false);
//
//        // set recycler view to horizontal layout
//        RecyclerView.LayoutManager userLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//        userCreatedRecyclerView.setLayoutManager(userLayoutManager);
//
//        //attach array list to adapter and attach adapter to recycler view on screen
//        userCreatedAdapter = new HorizontalSandwichAdapter(library.this, dbSandwiches);
//        userCreatedRecyclerView.setAdapter(userCreatedAdapter);
//
//        //add on touch listener that goes to sandwichinfo when touched
//        userCreatedRecyclerView.addOnItemTouchListener(
//                new RecyclerItemClickListener(this, userCreatedRecyclerView, new sandwichListener(dbSandwiches)));
//
//        //init array list
//        libSandwiches = new ArrayList<>();
//
//        // get recycler view
//        libRecyclerView = (RecyclerView) getActivity().findViewById(R.id.lib_recycler_view);
//        libRecyclerView.setHasFixedSize(false);
//
//        // set recycler view to horizontal layout
//        RecyclerView.LayoutManager  libLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//        libRecyclerView.setLayoutManager(libLayoutManager);
//
//        //attach array list to adapter and attach adapter to recycler view on screen
//        libAdapter = new HorizontalSandwichAdapter(library.this, libSandwiches);
//        libRecyclerView.setAdapter(libAdapter);
//
//        //add on touch listener that goes to sandwichinfo when touched
//        libRecyclerView.addOnItemTouchListener(
//                new RecyclerItemClickListener(this, userCreatedRecyclerView, new sandwichListener(libSandwiches)));
//
//        //init array list
//        popSandwiches = new ArrayList<>();
//
//        // get recycler view
//        popRecyclerView = (RecyclerView) getActivity().findViewById(R.id.pop_recycler_view);
//        popRecyclerView.setHasFixedSize(false);
//
//        // set recycler view to horizontal layout
//        RecyclerView.LayoutManager  popLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//        popRecyclerView.setLayoutManager(popLayoutManager);
//
//        //attach array list to adapter and attach adapter to recycler view on screen
//        popAdapter = new HorizontalSandwichAdapter(library.this, popSandwiches);
//        popRecyclerView.setAdapter(popAdapter);
//
//        //add on touch listener that goes to dandwichinfo when touched
//        popRecyclerView.addOnItemTouchListener(
//                new RecyclerItemClickListener(this, popRecyclerView, new sandwichListener(popSandwiches)));
//    }
//
//    //call data base to perform query
//    public void search(String q, final ArrayList<sandwich> sandwichArrayList, final RecyclerView.Adapter adapter)
//    {
//        JSONObject obj = new JSONObject();
//        try
//        {
//            obj.put("q",q);
//        }
//        catch(Exception e)
//        {
//            Log.d(logTag, "Problem putting query in POST");
//        }
//
//        //initialize search recycler view if it is null
//        if (searchRecyclerView == null)
//        {
//            //init arraylist and find recylcer view by id
//            searchSandwiches = new ArrayList<>();
//            searchRecyclerView = (RecyclerView) getActivity().findViewById(R.id.search_recycler_view);
//            searchRecyclerView.setHasFixedSize(false);
//
//            //set horizontal layout
//            RecyclerView.LayoutManager userLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//            searchRecyclerView.setLayoutManager(userLayoutManager);
//
//            //attack adapter to search array list and set this adapter to the recycler view on screen
//            searchAdapter = new HorizontalSandwichAdapter(library.this, searchSandwiches);
//            searchRecyclerView.setAdapter(searchAdapter);
//            //add touch listener that goes to sandwichinfo
//            searchRecyclerView.addOnItemTouchListener(
//                    new RecyclerItemClickListener(this, userCreatedRecyclerView, new sandwichListener(searchSandwiches)));
//        }
//
//        JsonObjectRequest jsobj = new JsonObjectRequest(
//                "https://sandwichstory-172520.appspot.com/api/search", obj,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response)
//                    {
//                        Log.d(logTag, "Received: " + response.toString());
//                        try
//                        {
//                            //hide all other recycler views and display the search results
//                            userCreatedRecyclerView.setVisibility(View.GONE);
//                            libRecyclerView.setVisibility(View.GONE);
//                            popRecyclerView.setVisibility(View.GONE);
//
//                            TextView userText = (TextView)getActivity().findViewById(R.id.textViewUserSandwiches);
//                            userText.setVisibility(View.GONE);
//                            TextView libText = (TextView)getActivity().findViewById(R.id.textViewLibrarySandwiches);
//                            libText.setVisibility(View.GONE);
//                            TextView popText = (TextView)getActivity().findViewById(R.id.textViewPopularSandwiches);
//                            popText.setVisibility(View.GONE);
//
//                            searchRecyclerView.setVisibility(View.VISIBLE);
//
//                            //load search results into the search arraylist and notify adapter to
//                            //update the scrren
//                            JSONArray receivedList = response.getJSONArray("result");
//                            decodeJSON(receivedList, searchSandwiches, searchAdapter);
//
//                        }
//                        catch(Exception e)
//                        {
//                            Log.d(logTag, "Issue with search response" + e.getStackTrace());
//                        }
//
//
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error)
//            {
//                //no error respsonse handling
//            }
//        });
//
//
//        appInfo.queue.add(jsobj);
//    }
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
//            //nothing, bolierplate necessary
//        }
//    }
//
//    @Override
//    public void onResume() {
//
//        refreshSandwiches(null);
//
//        /*
//        //load recipes
//        Bundle extras = getIntent().getExtras();
//
//        //see if this activity was started from library activity
//        if(extras.containsKey("fromMain") && extras.getBoolean("fromMain") && firstLoad)
//        {
//            //if this is called from main, load random set of user created sandwiches
//            refreshSandwiches(null);
//            firstLoad = false;
//        }
//        else
//        {
//            //if this is not the first load, only reload the library and popular sandwiches
//            //so that user sandwiches are only changed when the user wants them to be
//            loadRecipes(null, "get_lib_recipes", libSandwiches, libAdapter);
//            loadRecipes(null, "get_popular_recipes", popSandwiches, popAdapter);
//        }*/
//
//        super.onResume();
//    }
//
//    public void refreshSandwiches(View view)
//    {
//        //reload all recycler views
//        loadRecipes(view, "get_user_recipes", dbSandwiches, userCreatedAdapter);
//        loadRecipes(view, "get_lib_recipes", libSandwiches, libAdapter);
//        loadRecipes(view, "get_popular_recipes", popSandwiches, popAdapter);
//        Log.d(logTag, "refresh");
//    }
//
//    public void loadRecipes(View view, String apiMethod, final ArrayList<sandwich> sandwichArrayList, final RecyclerView.Adapter adapter)
//    {
//        //display spinning progress bar while waiting for database response
//        final ProgressDialog progress = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
//        progress.setTitle("Loading");
//        progress.setMessage("Loading recipes...");
//
//        //disable dismiss by tapping outside of the dialog
//        progress.setCancelable(false);
//        progress.show();
//
//        String url = "https://sandwichstory-172520.appspot.com/api/" + apiMethod;
//        JsonObjectRequest jsObjRequest = new JsonObjectRequest
//                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//
//                    @Override
//                    public void onResponse(JSONObject response) {
//
//                        progress.dismiss();
//                        Log.d(logTag, "Received: " + response.toString());
//
//                        try
//                        {
//                            //load received list into the given arraylist and update the screen
//                            JSONArray receivedList = response.getJSONArray("result");
//                            decodeJSON(receivedList, sandwichArrayList, adapter);
//
//                        } catch (Exception e)
//                        {
//                            Log.d(logTag, ""+ e.getStackTrace());
//                        }
//                    }
//                }, new Response.ErrorListener() {
//
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        // Dismiss progress bar and let user know there was an issue connecting to server
//                        Log.d(logTag, error.toString());
//                        progress.dismiss();
//
//                        if (alertDialog == null)
//                        {
//                            alertDialog = new AlertDialog.Builder(library.this);
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
//                    }
//                });
//
//        appInfo.queue.add(jsObjRequest);
//    }
//
//    public void decodeJSON(JSONArray jArray, ArrayList<sandwich> sandwichArrayList, RecyclerView.Adapter adapter)
//    {
//        try
//        {
//            //clear current arraylist to copy new sandwiches into it
//            sandwichArrayList.clear();
//            for(int i = 0; i < jArray.length(); i++)
//            {
//                JSONObject jsonSandwichRecipe = jArray.getJSONObject(i);
//
//                //get name
//                String name = jsonSandwichRecipe.getString("obj_name");
//
//                //get ingredient list
//                JSONArray jsonIngredients = jsonSandwichRecipe.getJSONArray("Ingredients"); //name may be wrong
//                ArrayList<Ingredients> ingredients = new ArrayList<>();
//
//                //add each component of json ingredient list to sandwich ingredient list
//                for(int j = 0; j < jsonIngredients.length(); j++)
//                {
//                    JSONObject jsonIngredientComponents = jsonIngredients.getJSONObject(j);
//                    String qty = jsonIngredientComponents.getString("qty");                  //name may be wrong
//                    String measure = jsonIngredientComponents.getString("measure");          //name may be wrong
//                    String ingName = jsonIngredientComponents.getString("name");             //name may be wrong
//                    ingredients.add(new Ingredients(qty, measure, ingName));         //constructor is wrong
//                }
//
//                //get img id
//                //int imgId = Integer.parseInt(jsonSandwichRecipe.getString("imgId")); //may not need this
//
//                //get unique id to keep track of sandwiches that are downloaded so we can track popularity
//                String uniqueId = jsonSandwichRecipe.getString("uniqueid"); //name may be wrong
//
//                //get message
//                String msg = jsonSandwichRecipe.getString("message");
//
//                //get downloads
//                int downloads = jsonSandwichRecipe.getInt("download_count");
//
//                //add this sandwich to the array list
//                sandwichArrayList.add(new Sandwich(name, ingredients, msg, imgId, uniqueId, downloads)); // constructor is probably wrong
//            }
//
//        }
//        catch(Exception e)
//        {
//            Log.d(logTag, "JSON loading failed");
//        }
//
//        adapter.notifyDataSetChanged();
//    }
//}