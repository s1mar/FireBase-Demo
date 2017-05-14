package Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by JAPC on 14-05-2017.
 */

public class ImageAdapter extends BaseAdapter {

    ArrayList<File> dataSet = new ArrayList<File>();
    int layoutToExpand;
    int elementInLayout;

    public ImageAdapter(ArrayList<File> imageDataSet,int layoutToExpand,int elementInLayout){

        this.layoutToExpand = layoutToExpand;
        this.elementInLayout = elementInLayout;
        for(File file : imageDataSet){
            dataSet.add(file);
        }
    }
    public void delete(int pos){
        dataSet.remove(pos);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    public File getItem(int position) {
        return dataSet.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){

            convertView = LayoutInflater.from(parent.getContext()).inflate(layoutToExpand,parent,false);

        }
        ImageView imageHolder = (ImageView)convertView.findViewById(elementInLayout);

        Picasso.with(parent.getContext()).load(dataSet.get(position)).centerCrop().fit().into(imageHolder);

        //tagNoText.setText(String.valueOf(position+1));
        return convertView;
    }
}

