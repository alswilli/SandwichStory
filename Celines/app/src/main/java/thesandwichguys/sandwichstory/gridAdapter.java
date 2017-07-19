package thesandwichguys.sandwichstory;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class gridAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<Sandwich> data;
    AppInfo appInfo;
    private LayoutInflater inflater;


    public gridAdapter(Context context, ArrayList<Sandwich> data){
        this.context = context;
        this.data = data;
        appInfo = AppInfo.getInstance(this.context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return data.indexOf(getItem(position));
    }

    //Add another grid item
    public void add(Sandwich item){
        data.add(item);
        notifyDataSetChanged();
    }

    class ViewHolder{
        ImageView icon;
        TextView name;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        View gridView = convertView;
        ViewHolder holder = null;
        if(gridView == null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridView = inflater.inflate(R.layout.custom_gridview_layout,null);

            //Convert code data to items to View types that display on screen
            holder = new ViewHolder();
            holder.icon = (ImageView) gridView.findViewById(R.id.sandwichpic1);
            holder.name = (TextView) gridView.findViewById(R.id.sandwichname1);
            gridView.setTag(holder);
        }else{
            //Initialize holder to the values in the gridview's ViewHolder tag
            holder = (ViewHolder) gridView.getTag();
        }
        //Set view's data field to data fields in gridview
        Sandwich gridItem = data.get(position);
        String encodedImage = gridItem.getImageId();
        if(encodedImage.equals("2130837589")){
            holder.icon.setImageResource(R.drawable.basic_sandwich);
        }else{
            holder.icon.setImageBitmap(turnEncodedStringToBitmap(encodedImage));
        }

        holder.name.setText(gridItem.getSandwichName());
        return gridView;
    }

    public Bitmap turnEncodedStringToBitmap(String encodedImage){
        byte[] b = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap photo = BitmapFactory.decodeByteArray(b, 0, b.length);
        return photo;
    }
}
