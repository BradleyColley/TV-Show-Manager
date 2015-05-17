package com.project.tv_manager.list_screens;

/*
 * This class is responsible for creating a custom list view containing
 * text views and image views. 
 */

import java.util.ArrayList;
import com.project.tvshow_reminders.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("ViewHolder")
public class CustomListAdapter extends ArrayAdapter<String> {
	private final Activity context;
	private final ArrayList<String> itemName;
	private final Integer[] imgId;
	
	public CustomListAdapter(Activity context, ArrayList<String> itemname, Integer[] imgid) {
		super(context, R.layout.view_all_layout, itemname);		
		this.context=context;
		this.itemName=itemname;
		this.imgId=imgid;
	}
	
	/*
	 * This method creates the custom view.  
	 */
	public View getView(int position,View view,ViewGroup parent) {
		LayoutInflater inflater=context.getLayoutInflater();
		View rowView=inflater.inflate(R.layout.view_all_layout, null, true);
		
		//Defines the Text and Image views.
		TextView txtTitle = (TextView) rowView.findViewById(R.id.textView1);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
		
		txtTitle.setText(itemName.get(position)); //Sets the TV show title.
		imageView.setImageResource(imgId[position]); //Sets the image.
		imageView.setAlpha(120); //Makes the image transparent.
		return rowView; //Returns the row.
	};
}