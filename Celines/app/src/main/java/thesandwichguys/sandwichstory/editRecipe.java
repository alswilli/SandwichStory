package thesandwichguys.sandwichstory;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import static android.support.v4.content.PermissionChecker.checkCallingOrSelfPermission;


public class editRecipe extends Activity {

    EditText editName;
    ListView ingredients;
    ImageView sandwich_picture;
    Spinner qtySpinner;
    Spinner fracSpinner;
    Spinner measurementSpinner;
    EditText ingredientName;
    EditText addInstructions;
    ArrayList<Ingredients> ingredient_list = new ArrayList<>();
    String instructions = "";
    String sandwichName = "";
    String imageId;
    String encodedImage;
    private static final int GALLERY = 1;
    private static final int CAMERA = 2;
    private static final int PERMISSION_REQUEST_CODE = 123;
    int index;
    AppInfo appInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //Hides the top bar
        setContentView(R.layout.edit_recipe);          //edit_recipe.xml corresponds to this class
        appInfo = AppInfo.getInstance(this);
        init();                                        //sets everything else up
    }

    private void init(){
        editName = (EditText) findViewById(R.id.editName);
        editName.setText(appInfo.sandwichToEdit.getSandwichName()); //pre-fills sandwich name at top

        ingredients = (ListView) findViewById(R.id.ingredientList);
        ingredients.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true); //Allows us to scroll through the ingredients
                return false;
            }
        });

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            index = extras.getInt("index");
        }

        final ingredientListAdapter adapter = new ingredientListAdapter(this,
                R.layout.adapter_view_layout, ingredient_list);
        ingredients.setAdapter(adapter);

        if(appInfo.sandwichToEdit.getIngredientList() != null){ //add ingredients to the listView
            ingredient_list.clear();
            for(Ingredients ingredient : appInfo.sandwichToEdit.getIngredientList()){
                ingredient_list.add(new Ingredients(ingredient.getIngredientName(),
                        ingredient.getQty(), ingredient.getUnit()));
            }
            adapter.notifyDataSetChanged();
        }

        sandwich_picture = (ImageView) findViewById(R.id.sandwich_picture);
        sandwich_picture.setImageBitmap(turnEncodedStringToBitmap(appInfo.sandwichToEdit.getImageId()));
        encodedImage = appInfo.sandwichToEdit.getImageId();

        ingredientName = (EditText) findViewById(R.id.ingredient_name);     //editText for the ingredient being added
        addInstructions = (EditText) findViewById(R.id.enterInstructions);
        addInstructions.setText(appInfo.sandwichToEdit.getInstructions());  //pre-fill instructions in editText

        Button pickImage = (Button) findViewById(R.id.add_photo_button);    //add photo button
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

        Button add_ingredient = (Button) findViewById(R.id.add_ingredient); //"+" add ingredient button
        add_ingredient.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        String name = ingredientName.getText().toString();  //get ingredient name

                        if(name.isEmpty()) {                                //check if the user has actually entered a value
                            String toastMsg = "Please enter an ingredient name!"; //if not, prompted again
                            Toast toast= Toast.makeText(getBaseContext(),toastMsg,Toast.LENGTH_SHORT);
                            toast.show();
                            return;
                        }else{                                              //otherwise, we can add this ingredient to our list
                            String qty = (String)qtySpinner.getSelectedItem();
                            String fraction = (String)fracSpinner.getSelectedItem();
                            String measure = (String)measurementSpinner.getSelectedItem();

                            ingredient_list.add(new Ingredients(name, qty, measure));
                            adapter.notifyDataSetChanged();                 //add this ingredient to our list and update

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
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
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
        if (cameraIntent.resolveActivity(this.getPackageManager()) != null){
            startActivityForResult(cameraIntent, CAMERA);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //sandwich_picture = (ImageView) getActivity().findViewById(R.id.sandwich_picture);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {

                    Bitmap photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    //String path = saveImage(bitmap);  //PROBABLY DON'T WANT BECAUSE IT STACKS PICTURES EVERYTIME IT RUNS
                    //Toast.makeText(getActivity(), "Image Saved!", Toast.LENGTH_LONG).show();
                    sandwich_picture.setImageBitmap(photo);
                    encodedImage = turnBitMapToEncodedString(photo);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed!", Toast.LENGTH_LONG).show();
                }
            }
        }else if (requestCode == CAMERA) {
            Bitmap imageTakenFromCamera = (Bitmap) data.getExtras().get("data");
            sandwich_picture.setImageBitmap(imageTakenFromCamera);
            saveImage(imageTakenFromCamera);
            encodedImage = turnBitMapToEncodedString(imageTakenFromCamera);
            Toast.makeText(this, "Image Saved!", Toast.LENGTH_LONG).show();
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

    public Bitmap turnEncodedStringToBitmap(String encodedImage){
        byte[] b = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap photo = BitmapFactory.decodeByteArray(b, 0, b.length);
        return photo;
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
            MediaScannerConnection.scanFile(this, new String[] {file.getPath()}, new String[]{"image/jpeg"}, null);
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
            res = checkCallingOrSelfPermission(perms);
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
                    Toast.makeText(this, "Storage Permission Denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void setup_spinners(){
        //Spinner 1: Whole number quantity spinner
        qtySpinner = (Spinner) findViewById(R.id.qtySpinner);                              //assign the spinner from xml to our global variable "qtySpinner"
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,        //values shown in spinner are "qty_array" in strings.xml
                R.array.qty_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);   // Specify the layout to use when the list of choices appears
        qtySpinner.setAdapter(adapter1);                                                   // Apply the adapter to the spinner

        //Spinner 2: Fractional quantity spinner
        fracSpinner = (Spinner) findViewById(R.id.fracSpinner);                            //assign the spinner from xml to our global variable "fracSpinner"
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,        //values shown in spinner are "frac_array" in strings.xml
                R.array.frac_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);   // Specify the layout to use when the list of choices appears
        fracSpinner.setAdapter(adapter2);                                                  // Apply the adapter to the spinner

        //Spinner 3: Measurement spinner
        measurementSpinner = (Spinner) findViewById(R.id.measurement_spinner);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,         //values shown in spinner are "measurements_array" in strings.xml
                R.array.measurements_array, android.R.layout.simple_spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);    // Specify the layout to use when the list of choices appears
        measurementSpinner.setAdapter(adapter3);                                            // Apply the adapter to the spinner
    }

    public void saveSandwich(View v) {
        instructions = addInstructions.getText().toString();                         //get the instructions
        sandwichName = editName.getText().toString();                                //get the name
        imageId = encodedImage;
        Sandwich recipe;

        ArrayList<Ingredients> ingredients = new ArrayList<>();

        for (Ingredients ingredient : ingredient_list) {                            //add from listView to array list
            ingredients.add(ingredient);
        }

        //make sure all values are filled out
        if (sandwichName.isEmpty() || instructions.isEmpty() || ingredients.size() == 0 || sandwich_picture.getDrawable().getConstantState().equals
                (getResources().getDrawable(R.drawable.chooseimage).getConstantState())) {
            Toast.makeText(this, "Invalid recipe!", Toast.LENGTH_LONG).show();
            return;
        }else{ //otherwise, save the recipe
            recipe = appInfo.savedSandwich.get(index);
            recipe.setSandwichName(sandwichName);
            recipe.setIngredientList(ingredients);
            recipe.setInstructions(instructions);
            recipe.setImageId(imageId);
        }
        showSaveDialog(recipe);
    }

    public void showSaveDialog(final Sandwich recipe) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Recipe added to list!");

        //ask user if they'd like to add to database
        alertDialog.setMessage(("Recipe saved to your list! Would you also like to add this recipe to the " +
                "Sandwich Story for all users to view?"))
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {  //user selects yes, add to database
                        //saveToLibrary(null, recipe);

                        Intent intent = new Intent(editRecipe.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        saveAsJSON();
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { //user selects no, only save locally & return home
                        Intent intent = new Intent(editRecipe.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        saveAsJSON();
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
            }
            catch(Exception e){
                Log.e("help", "Error" + e.getStackTrace());
            }
        }

        SharedPreferences settings = this.getSharedPreferences(MainActivity.MYPREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("sandwichesAsJSON", jArray.toString());

        editor.commit();
    }
}
