package thesandwichguys.sandwichstory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONArray;
import java.util.ArrayList;

public class sandwichInfo extends Activity {

    AppInfo appInfo;
    String name;
    String qty;
    String ratio;
    String unit;
    String instructions;
    String ingredientName;
    ImageView sandwich_picture;
    ArrayList<Ingredients> ingredients_list = new ArrayList<>();
    Sandwich displayedSandwich;
    int index;
    int devIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.sandwichinfo);
        appInfo = AppInfo.getInstance(this);
    }

    protected void onResume() {
        super.onResume();

        ingredients_list.clear();

        sandwich_picture = (ImageView) findViewById(R.id.sandwich_picture);
        TextView sandwichName = (TextView) findViewById(R.id.sandwich_name);
        TextView sandwichMessage = (TextView) findViewById(R.id.instructions);
        ListView ingredients = (ListView) findViewById(R.id.listview);
        Button editButton = (Button) findViewById(R.id.editButton);
        Button deleteButton = (Button) findViewById(R.id.deleteButton);

        Bundle extras = getIntent().getExtras();
        index = extras.getInt("index");

        if(index < 0){
            devIndex = extras.getInt("devIndex");
            displayedSandwich = appInfo.devSandwich.get(devIndex);
            ingredients_list = displayedSandwich.getIngredientList();
            sandwich_picture.setImageResource(R.drawable.basic_sandwich);
            editButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
        }else {
            displayedSandwich = appInfo.savedSandwich.get(index);
            appInfo.sandwichToEdit = displayedSandwich;
            ingredients_list = displayedSandwich.getIngredientList();
            sandwich_picture.setImageBitmap(turnEncodedStringToBitmap(displayedSandwich.getImageId()));
        }
            ingredients.setOnTouchListener(new View.OnTouchListener() {
                // Setting on Touch Listener for handling the touch inside ScrollView
                @Override
                public boolean onTouch(View v, MotionEvent event) { //allows us to scroll through the ingredient list
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });

            ingredientDisplayAdapter adapter = new ingredientDisplayAdapter(this, R.layout.list_display_layout, ingredients_list);
            ingredients.setAdapter(adapter);
            adapter.notifyDataSetChanged();

//            displayed_ingredients_list.clear();
//            for (int i = 0; i < ingredients_list.size(); i++) {
//                qty = ingredients_list.get(i).getQty();
//                unit = ingredients_list.get(i).getUnit();
//                ingredientName = ingredients_list.get(i).getIngredientName();
//                displayed_ingredients_list.add(new Ingredients(ingredientName, qty, unit));
//                adapter.notifyDataSetChanged();
//            }
            name = displayedSandwich.getSandwichName();
            instructions = displayedSandwich.getInstructions();
            sandwichName.setText(name);
            sandwichMessage.setText(instructions);
    }

    public void onClickEdit(View v) {
        Intent intent = new Intent(this, editRecipe.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("index", index);
        startActivity(intent);
    }

    public void onClickDelete(View v) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(sandwichInfo.this);
        alertDialog.setTitle("Delete recipe?");

        alertDialog.setMessage("Are you sure you want to delete this recipe?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { //user says yes, delete recipe
                        appInfo.savedSandwich.remove(index);
                        Intent intent = new Intent(sandwichInfo.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        saveAsJSON();
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { //user says no, dismiss dialog
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public Bitmap turnEncodedStringToBitmap(String encodedImage){
        byte[] b = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap photo = BitmapFactory.decodeByteArray(b, 0, b.length);
        return photo;
    }

    public void saveAsJSON() {
        JSONArray jArray = new JSONArray();
        for (Sandwich savedRecipe : appInfo.savedSandwich) {
            try{
                jArray.put(savedRecipe.sandwichAsJSON);
            } catch (Exception e) {
                Log.e("test", "Error saving as json: " + e.getStackTrace());
            }
        }

        SharedPreferences settings = getSharedPreferences(MainActivity.MYPREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("sandwichesAsJSON", jArray.toString());
        editor.commit();
    }
}

