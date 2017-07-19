package thesandwichguys.sandwichstory;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import static android.support.v4.content.PermissionChecker.checkCallingOrSelfPermission;


public class newSandwich extends Fragment {

    EditText name;
    ListView ingredients;
    ImageView sandwich_picture;
    Spinner qtySpinner;
    Spinner fracSpinner;
    Spinner measurementSpinner;
    EditText ingredientName;
    EditText addInstructions;
    View rootView;
    ArrayList<Ingredients> ingredient_list;
    String instructions = "";
    String sandwichName = "";
    String imageId;
    String encodedImage;
    private static final int GALLERY = 1;
    private static final int CAMERA = 2;
    private static final int PERMISSION_REQUEST_CODE = 123;
    String logTag = "Meeseeks";
    AppInfo appInfo;
    int index;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.new_sandwich, container, false);
        appInfo = AppInfo.getInstance(getActivity());

        ingredient_list = new ArrayList<>();
        init();

        //Save sandwich button
        Button saveSandwich = (Button) rootView.findViewById(R.id.save_sandwich_button);
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

    //Initialize everything in the view
    private void init(){
        name = (EditText) rootView.findViewById(R.id.editName);

        ingredients = (ListView) rootView.findViewById(R.id.ingredientList);
        ingredients.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true); //Allows us to scroll through the ingredients
                return false;
            }
        });

        final ingredientListAdapter adapter = new ingredientListAdapter(getActivity(), R.layout.adapter_view_layout, ingredient_list);
        ingredients.setAdapter(adapter);

        sandwich_picture = (ImageView) rootView.findViewById(R.id.sandwich_picture);
        ingredientName = (EditText) rootView.findViewById(R.id.ingredient_name);
        addInstructions = (EditText) rootView.findViewById(R.id.enterInstructions);
        Button pickImage = (Button) rootView.findViewById(R.id.add_photo_button); //opens activity for picking an image (not currently working)
        pickImage.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        if(hasPermissions() == true){
                            showPictureDialog();
                        }else{
                            requestPermissions();
                        }
                    }
                }
        );

        Button add_ingredient = (Button) rootView.findViewById(R.id.add_ingredient); // add ingredients to the list view
        add_ingredient.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        //Ingredient name:
                        String name = ingredientName.getText().toString();

                        if(name.isEmpty()) { //check if the user has actually entered a value
                            String toastMsg = "Please enter an ingredient name!"; //if not, prompted again
                            Toast toast= Toast.makeText(getActivity(),toastMsg,Toast.LENGTH_SHORT);
                            toast.show();
                            return;
                        }else{ //otherwise, we can add this ingredient to our list
                            String qty = (String) qtySpinner.getSelectedItem();
                            String fraction = (String)fracSpinner.getSelectedItem();
                            String measure = (String)measurementSpinner.getSelectedItem();

                            if(qty.equals("qty")) {
                                qty = "";
                            }
                            qty = qty + " " + fraction;

                            ingredient_list.add(new Ingredients(name, qty, measure));
                            adapter.notifyDataSetChanged();

                            //reset the text and spinners
                            qtySpinner.setSelection(0);
                            fracSpinner.setSelection(0);
                            measurementSpinner.setSelection(0);
                            ingredientName.setText("");
                        }
                    }
                }
        );
        setup_spinners();
    }

    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getActivity());
        pictureDialog.setTitle("Select Action");
        String [] pictureDialogItems = {
                "Select photo from gallery", "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int whichPictureMethod){
                switch (whichPictureMethod){
                    case 0:
                        choosePhotoFromGallery();
                        break;
                    case 1:
                        takePhotoFromCamera();
                        break;
                }
            }
        });
        pictureDialog.show();
    }

    public void choosePhotoFromGallery(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null){
            startActivityForResult(cameraIntent, CAMERA);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //sandwich_picture = (ImageView) getActivity().findViewById(R.id.sandwich_picture);
        if (resultCode == getActivity().RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {

                    Bitmap photo = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), contentURI);
                    //String path = saveImage(bitmap);  //PROBABLY DON'T WANT BECAUSE IT STACKS PICTURES EVERYTIME IT RUNS
                    //Toast.makeText(getActivity(), "Image Saved!", Toast.LENGTH_LONG).show();
                    sandwich_picture.setImageBitmap(photo);
                    encodedImage = turnBitMapToEncodedString(photo);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_LONG).show();
                }
            }
        }else if (requestCode == CAMERA) {
            Bitmap imageTakenFromCamera = (Bitmap) data.getExtras().get("data");
            sandwich_picture.setImageBitmap(imageTakenFromCamera);
            saveImage(imageTakenFromCamera);
            encodedImage = turnBitMapToEncodedString(imageTakenFromCamera);
            Toast.makeText(getActivity(), "Image Saved!", Toast.LENGTH_LONG).show();
        }
    }

    public String turnBitMapToEncodedString(Bitmap image){
        String encodedImageString;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        encodedImageString = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImageString;
    }

    public String saveImage(Bitmap imageToSave){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        imageToSave.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File imageDirectory = new File(Environment.getExternalStorageDirectory() + "/DirName");
        if (!imageDirectory.exists()) {
            imageDirectory.mkdir();
        }
        try {
            File file = new File(imageDirectory, Calendar.getInstance().getTimeInMillis() + ".jpg");
            file.createNewFile();
            FileOutputStream fileOutput = new FileOutputStream(file);
            fileOutput.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(getActivity(), new String[] {file.getPath()}, new String[]{"image/jpeg"}, null);
            fileOutput.close();
            Log.d("TAG", "File Saved::--->" + file.getAbsolutePath());
            return file.getAbsolutePath();
        }catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    private boolean hasPermissions(){
        int res = 0;
        //, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA
        // INCASE WE NEED TO ASK FOR MORE PERMISSIONS THOSE ARE THE POSSIBLE ONES
        // TO IMPLEMENT MULTIPLE PERMISSIONS, IT IS NOT THE SAME, MUST GOOGLE ANOTHER METHOD
        String [] permissions = new String []{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        for(String perms : permissions){
            res = checkCallingOrSelfPermission(getActivity(), perms);
            if(!(res == PackageManager.PERMISSION_GRANTED)){
                return false;
            }
        }
        return true;
    }

    private void requestPermissions(){
        String [] permissions = new String []{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermissions(permissions, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String [] permissions, @NonNull int [] grantResults){
        boolean allowed = true;
        switch (requestCode){
            case PERMISSION_REQUEST_CODE:   //if user granted all permissions
                for(int res : grantResults){
                    allowed = allowed && (res == PackageManager.PERMISSION_GRANTED);
                }
                break;
            default:    //if user not granted permissions
                allowed = false;
                break;
        }
        if (allowed){ //user granted all permissions we can do work
            showPictureDialog();
        }else{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    Toast.makeText(getActivity(), "Storage Permission Denied", Toast.LENGTH_LONG).show();
                }
            }
        }
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

    public void saveSandwich(View v) {
        sandwichName = name.getText().toString();
        instructions = addInstructions.getText().toString();
        imageId = encodedImage;

        ArrayList<Ingredients> ingredients = new ArrayList<>();

        for (Ingredients ingredient : ingredient_list) {                                    //add from listView to array list
            ingredients.add(ingredient);
        }

        if (sandwichName.isEmpty() || instructions.isEmpty() || ingredients.size() == 0 || sandwich_picture.getDrawable().getConstantState().equals
                (getResources().getDrawable(R.drawable.chooseimage).getConstantState())) {  //make sure all entries have been filled
            Toast.makeText(getActivity(), "Invalid recipe!", Toast.LENGTH_LONG).show();
            return;
        }else { //else, proceed to saving

            Sandwich recipe;

            recipe = new Sandwich(sandwichName, ingredients, instructions, imageId);
            appInfo.addSandwich(recipe);

            //save to database
            showSaveDialogue(recipe);
        }
    }

    //ask user if they want to add to database
    public void showSaveDialogue(final Sandwich recipe) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Recipe Added to List!");

        //ask user if they'd like to add to database
        alertDialog.setMessage(("Recipe saved to your list! Would you also like to add this recipe to the " +
                "Sandwich Story for all users to view?"))
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { //user selects yes, add to database

                        saveToLibrary(null, recipe);

                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        saveAsJSON();
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { //user selects no, only save locally & return home
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        saveAsJSON();
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });

        alertDialog.show();
    }

    public void saveAsJSON() {
        JSONArray jArray = new JSONArray();

        for(Sandwich savedRecipe : appInfo.savedSandwich){
            try{
                jArray.put(savedRecipe.sandwichAsJSON);
            } catch(Exception e){
                Log.e(logTag, "Error " + e.getStackTrace());
            }
        }

        SharedPreferences settings = getActivity().getSharedPreferences(MainActivity.MYPREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("sandwichesAsJSON", jArray.toString());
        editor.commit();
    }

    //save recipe to database
    public void saveToLibrary(View v, Sandwich recipe) {
        //create JSON representation of recipe being added to database
        JSONObject obj = new JSONObject();
        try{
            obj.put("name", recipe.getSandwichName());
            obj.put("msg", recipe.getInstructions());
            obj.put("imgId", recipe.getImageId());
//            obj.put("name", "Celine's Sandwich");
//            obj.put("msg", "Hello");
//            obj.put("imgId", 123);

//            JSONArray jArray = new JSONArray();
//
//            for (Ingredients ingredient : recipe.getIngredientList()) { //add the ingredients
//                jArray.put(ingredient.getJsonIngredient());
//            }
//
//            final String ing = jArray.toString();
//            obj.put("ingredients", ing);
        }catch (Exception e) {
            Log.d(logTag, "Something went wrong");
        }

        //backend method to add to database
        JsonObjectRequest jsobj = new JsonObjectRequest(
                "https://sandwichstory-172520.appspot.com/api/sadd_recipe_fancy", obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(logTag, "Received: " + response.toString());
                        try {
                            Log.d(logTag, "Saved properly to database");
                        } catch (Exception e) {
                            Log.d(logTag, "Not saved properly to database");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //nothing
            }
        });

        appInfo.queue.add(jsobj);
    }
}