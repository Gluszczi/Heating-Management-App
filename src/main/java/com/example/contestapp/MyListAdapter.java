package com.example.contestapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyListAdapter extends ArrayAdapter
{

    private final Activity context;
    private final String[] names;
    private final String[] typeNames;
    private final Integer[] images;

    public MyListAdapter(Activity context, String[] names, String[] typeNames, Integer[] images) {
        super(context, R.layout.simple_list_text_image, names);
        this.context = context;
        this.names = names;
        this.typeNames = typeNames;
        this.images = images;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.simple_list_text_image, null, true);
        TextView text = (TextView) rowView.findViewById(R.id.textView);
        TextView typeTextView = (TextView) rowView.findViewById(R.id.typeTextView);
        ImageView image = (ImageView) rowView.findViewById(R.id.imageView);

        text.setText(names[position]);
        typeTextView.setText(typeNames[position]);
        image.setImageResource(images[position]);

        return rowView;
    }
}
