package thesandwichguys.sandwichstory;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class gridadapter extends BaseAdapter{

    private int icons[];

    private String names[];

    private Context context;

    private LayoutInflater inflater;

    public gridadapter(Context context, int icons[], String names[]){
        this.context = context;
        this.icons = icons;
        this.names = names;
    }

    @Override
    public int getCount() {
        return names.length;
    }

    @Override
    public Object getItem(int position) {
        return names[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View gridView = convertView;
        if(convertView == null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridView = inflater.inflate(R.layout.custom_gridview_layout,null);
        }

        ImageView icon = (ImageView) gridView.findViewById(R.id.sandwichpic1);
        TextView name = (TextView) gridView.findViewById(R.id.sandwichname1);

        icon.setImageResource(icons[position]);
        name.setText(names[position]);


        return gridView;
    }
}
