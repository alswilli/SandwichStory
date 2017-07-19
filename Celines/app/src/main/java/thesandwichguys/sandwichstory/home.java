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


public class home extends Fragment {

    GridView gridView;
    View rootView;
    gridAdapter adapter;
    ArrayList<Sandwich> listOfSandwiches;  // Create a new sandwich list to store the sandwich objects that need to be parsed
    AppInfo appInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.home, container, false);
        listOfSandwiches = new ArrayList<>();

        gridView = (GridView) rootView.findViewById(R.id.gridview);
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
        listOfSandwiches.clear();
        for (Sandwich recipe : appInfo.savedSandwich) {
            listOfSandwiches.add(recipe);
        }
        adapter.notifyDataSetChanged();
    }
}
