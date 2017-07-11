package thesandwichguys.sandwichstory;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

public class favorite extends Fragment {

    GridView gridView;

    String sandwichNames[] = {"BLT", "PBnJ", "McGangBang", "The BubbleRub", "Pastor Suprise"};
    int sandwichIcon[] = {R.drawable.burger,R.drawable.burger,R.drawable.burger,R.drawable.burger,R.drawable.burger,};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.favorite, container, false);

        gridView = (GridView) rootView.findViewById(R.id.gridview);
        gridadapter adapter = new gridadapter(getActivity(), sandwichIcon, sandwichNames);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent nextScreen = new Intent(getActivity(), sandwichinfo.class);
                startActivity(nextScreen);
            }
        });

        return rootView;
    }
}
