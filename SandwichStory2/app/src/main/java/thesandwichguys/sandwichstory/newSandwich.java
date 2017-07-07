package thesandwichguys.sandwichstory;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class newSandwich extends Fragment {

    Button pickImage;
    EditText editName;
    TextView nameCharCount;
    ListView ingredients;
    ImageView sandwich_picture;
    Spinner qtySpinner;
    Spinner fracSpinner;
    Spinner measurementSpinner;
    TextView ingCharCount;
    EditText editIngredientName;
    TextView instrCharCount;
    EditText addInstructions;
    View rootView;
    //ArrayList<Ingredient> rowItems;

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
        Button addSandwichButton = (Button) rootView.findViewById(R.id.addSandwichBtn);
        addSandwichButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //PUT YOUR ADD SANDWICH FUNCTIONALITY HERE
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
        editName = (EditText) getActivity().findViewById(R.id.editName);
        ingredients = (ListView) getActivity().findViewById(R.id.ingredientList);
        sandwich_picture = (ImageView) getActivity().findViewById(R.id.sandwich_picture);
        pickImage = (Button) getActivity().findViewById(R.id.add_photo_button);
        editIngredientName = (EditText) getActivity().findViewById(R.id.ingredient_name);
        addInstructions = (EditText) getActivity().findViewById(R.id.enterInstructions);
        //dietary_restrictions_dialog();
        setup_spinners();
    }

    public void onClickAdd(View v){
        //Add ingredient to the list view
    }

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

//    public void dietary_restrictions_dialog(){
//
//        Button btn = (Button) rootView.findViewById(R.id.addButton);
//
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Build an AlertDialog
//                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//
//                // String array for alert dialog multi choice items
//                String[] colors = new String[]{
//                        "Vegan",
//                        "Vegetarian",
//                        "Gluten-Free"
//                };
//
//                // Boolean array for initial selected items
//                final boolean[] checkedItems = new boolean[]{
//                        false, // Vegan
//                        false, // Vegetarian
//                        false, // Gluten-Free
//                };
//
//                // Convert the color array to list
//                final List<String> colorsList = Arrays.asList(colors);
//
//                // Set multiple choice items for alert dialog
//                /*
//                    AlertDialog.Builder setMultiChoiceItems(CharSequence[] items, boolean[]
//                    checkedItems, DialogInterface.OnMultiChoiceClickListener listener)
//                        Set a list of items to be displayed in the dialog as the content,
//                        you will be notified of the selected item via the supplied listener.
//                 */
//                /*
//                    DialogInterface.OnMultiChoiceClickListener
//                    public abstract void onClick (DialogInterface dialog, int which, boolean isChecked)
//
//                        This method will be invoked when an item in the dialog is clicked.
//
//                        Parameters
//                        dialog The dialog where the selection was made.
//                        which The position of the item in the list that was clicked.
//                        isChecked True if the click checked the item, else false.
//                 */
//                builder.setMultiChoiceItems(colors, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
//
//                        // Update the current focused item's checked status
//                        checkedItems[which] = isChecked;
//
//                        // Get the current focused item
//                        String currentItem = colorsList.get(which);
//                    }
//                });
//
//                // Specify the dialog is not cancelable
//                builder.setCancelable(false);
//
//                // Set a title for alert dialog
//                builder.setTitle("Dietary Restrictions");
//
//                // Set the positive/yes button click listener
//                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        //do something with selections
//                    }
//                });
//
//                // Set the neutral/cancel button click listener
//                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // Do something when click the neutral button
//                    }
//                });
//
//                AlertDialog dialog = builder.create();
//                // Display the alert dialog on interface
//                dialog.show();
//            }
//        });
//    }
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
