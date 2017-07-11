package thesandwichguys.sandwichstory;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class newSandwich extends Fragment {

    Button pickImage;
    EditText editName;
    ListView ingredients;
    ImageView sandwich_picture;
    Spinner qtySpinner;
    Spinner fracSpinner;
    Spinner measurementSpinner;
    EditText ingredient_name;
    EditText addInstructions;
    View rootView;
    //ArrayList<Ingredient> rowItems;
    ArrayList<Ingredients> ingredient_list = new ArrayList<>();

    AppInfo appInfo;

    final int REQUEST_CODE_GALLERY = 999;
    public static SQLiteHelper sqLiteHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.newsandwich, container, false);
        init();

//        sqLiteHelper = new SQLiteHelper(getActivity(), "sandwichDB.sqlite", null, 1);
//        sqLiteHelper.quaryData("Empty Data");

//        pickImage.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                    ActivityCompat.requestPermissions(
//                        getActivity(),
//                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                        REQUEST_CODE_GALLERY
//                );
//            }
//        });


//        Button addIngredientButton = (Button) rootView.findViewById(R.id.add);
//        addIngredientButton.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        //PUT YOUR ADD SANDWICH FUNCTIONALITY HERE
//                        add(null);
//                    }
//                }
//        );

        Button saveSandwich = (Button) rootView.findViewById(R.id.addSandwichBtn);
        saveSandwich.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveSandwich(null);
                    }
                }
        );

        return rootView;
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//
//        if(requestCode == REQUEST_CODE_GALLERY){
//            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                Intent intent = new Intent(Intent.ACTION_PICK);
//                intent.setType("image/*");
//                startActivityForResult(intent, REQUEST_CODE_GALLERY);
//            }
//            else{
//                Toast.makeText(getContext(), "You don't have permission to access file location!", Toast.LENGTH_SHORT).show();
//            }
//            return;
//        }
//
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        if(requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null){
//            Uri uri = data.getData();
//            try {
//                InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
//
//                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                sandwich_picture.setImageBitmap(bitmap);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//        }
//
//        super.onActivityResult(requestCode,resultCode,data);
//    }

    private void init(){
        editName = (EditText) rootView.findViewById(R.id.editName);

        ingredients = (ListView) rootView.findViewById(R.id.ingredientList);
        ingredients.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        final IngredientListAdapter adapter = new IngredientListAdapter(getActivity(), R.layout.adapter_view_layout, ingredient_list);
        ingredients.setAdapter(adapter);

        sandwich_picture = (ImageView) rootView.findViewById(R.id.sandwich_picture);
        ingredient_name = (EditText) rootView.findViewById(R.id.ingredient_name);
        addInstructions = (EditText) rootView.findViewById(R.id.enterInstructions);
        Button pickImage = (Button) rootView.findViewById(R.id.add_photo_button);
        pickImage.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        Intent intent = new Intent(newSandwich.this.getActivity(), chooseImage.class);
                        startActivity(intent);
                    }
                }
        );

        Button add_ingredient = (Button) rootView.findViewById(R.id.add);
        add_ingredient.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        //Ingredient name:
                        String name = ingredient_name.getText().toString();

                        if(name.equals("")) { //check if the user has actually entered a value
                            String toastMsg = "Please enter an ingredient name!"; //if not, prompted again
                            Toast toast= Toast.makeText(getActivity(),toastMsg,Toast.LENGTH_SHORT);
                            toast.show();
                            return;
                        }else{ //otherwise, we can add this ingredient to our list
                            String qty = (String)qtySpinner.getSelectedItem();
                            String measure = (String)measurementSpinner.getSelectedItem();
                            String fraction = (String)fracSpinner.getSelectedItem();

                            ingredient_list.add(new Ingredients(name, qty, fraction, measure));
                            adapter.notifyDataSetChanged();

                            //reset the text and spinners
                            qtySpinner.setSelection(0);
                            fracSpinner.setSelection(0);
                            measurementSpinner.setSelection(0);
                            ingredient_name.setText("");
                        }
                    }
                }
        );
        setup_spinners();
    }

//    void getIngredients() {
//        ingredient_list.add("1","1/2","tbs", "tomato");
//    }

    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView  ****/

    public void setup_spinners(){
        //Spinner 1: Whole number quantity spinner
        qtySpinner = (Spinner) rootView.findViewById(R.id.qtySpinner);                              //assign the spinner from xml to our global variable "qtySpinner"
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getContext(),         //values shown in spinner are "qty_array" in strings.xml
                R.array.qty_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);            // Specify the layout to use when the list of choices appears
        qtySpinner.setAdapter(adapter1);                                                            // Apply the adapter to the spinner

        //Spinner 2: Fractional quantity spinner
        fracSpinner = (Spinner) rootView.findViewById(R.id.fracSpinner);                            //assign the spinner from xml to our global variable "fracSpinner"
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getContext(),         //values shown in spinner are "frac_array" in strings.xml
                R.array.frac_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);            // Specify the layout to use when the list of choices appears
        fracSpinner.setAdapter(adapter2);                                                           // Apply the adapter to the spinner

        //Spinner 3: Measurement spinner
        measurementSpinner = (Spinner) rootView.findViewById(R.id.measurement_spinner);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(getContext(),         //values shown in spinner are "measurements_array" in strings.xml
                R.array.measurements_array, android.R.layout.simple_spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);            // Specify the layout to use when the list of choices appears
        measurementSpinner.setAdapter(adapter3);                                                    // Apply the adapter to the spinner

    }
    //    public void add(View v)
//    {
//        String name = ingredient_name.getText().toString();
//        String qty = (String)qtySpinner.getSelectedItem();
//        String fraction = (String)measurementSpinner.getSelectedItem();
//        String measure = (String)fracSpinner.getSelectedItem();
//
//        ingredient_list.add(new Ingredients(name, qty, fraction, measure));
//
//        qtySpinner.setSelection(0);
//        fracSpinner.setSelection(0);
//        measurementSpinner.setSelection(0);
//        editName.setText("");
//    }
    String msg;
    String sandwichName;
    public void saveSandwich(View v)
    {
        msg = addInstructions.getText().toString();
        sandwichName = editName.getText().toString();

        ArrayList<Ingredients> ingredients = new ArrayList<>();
        for(Ingredients sandwichIngredient : ingredient_list)
        {
            ingredients.add(sandwichIngredient);
        }

        Sandwich recipe;
        recipe = new Sandwich(sandwichName, ingredients, msg, true,true,true);
        appInfo.addSandwich(recipe);

    }
}


//package thesandwichguys.sandwichstory;
//
//import android.support.v4.app.Fragment;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//
//public class newSandwich extends Fragment {
//
//    EditText editName, editIngredients;
//    Button btnChoose, btnAdd, btnList;
//    ImageView imageView;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.newsandwich, container, false);
//        return rootView;
//
//       // init();
//    }
//
//    private void init(){
//        editName = (EditText) getView().findViewById(R.id.editName);
//        editIngredients = (EditText) getView().findViewById(R.id.editIngredients);
//        btnChoose = (Button) getView().findViewById(R.id.btnChoose);
//        btnAdd = (Button) getView().findViewById(R.id.btnAdd);
//        btnList = (Button) getView().findViewById(R.id.btnList);
//        imageView = (ImageView) getView().findViewById(R.id.imageView);
//    }
//}