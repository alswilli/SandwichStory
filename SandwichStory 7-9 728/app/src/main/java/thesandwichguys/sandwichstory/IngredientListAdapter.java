package thesandwichguys.sandwichstory;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class IngredientListAdapter extends ArrayAdapter<Ingredients>
{
    private Context mContext;
    int mResource;

    public IngredientListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<Ingredients> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String name = getItem(position).getIngredientName();
        String qty = getItem(position).getQty();
        String fraction = getItem(position).getMeasure();
        String unit = getItem(position).getUnit();

        Ingredients ingredients = new Ingredients(name, qty, fraction, unit);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource,parent,false);

        TextView tvName = (TextView) convertView.findViewById(R.id.name_tv);
        TextView tvQty = (TextView) convertView.findViewById(R.id.qty_tv);
        TextView tvFraction = (TextView) convertView.findViewById(R.id.frac_tv);
        TextView tvUnit = (TextView) convertView.findViewById(R.id.measurement_tv);

        tvName.setText(name);
        tvQty.setText(qty);
        tvFraction.setText(fraction);
        tvUnit.setText(unit);

        return convertView;
    }
}