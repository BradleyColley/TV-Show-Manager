package com.project.tv_manager.list_screens;

/*
 * This class is responsible for displaying the correct TV show
 * information based on the users selection from the List of Shows 
 * screen choosing a specific letter. The class will display the TV
 * show images within the list view.  
 */

import java.util.ArrayList;

import com.project.tv_manager.MenuScreen;
import com.project.tvshow_reminders.R;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import static com.project.tv_manager.Global.*;

public class TVLists extends Activity {
	private ArrayList<String> listOfNames;
	private String letter;
	private Cursor c;
	private Integer[] selector, imgList;
	
	/*
	 * Once the activity is called, the onCreate() method
	 * will launch first to load the layout.
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_shows_layout);

		//Below outlines the navigation side bar layout and defines what gets displayed.
		navView = (ListView)findViewById(R.id.navList);
		navLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
		navList = new String[] { "Home", "My TV Shows", "Calendar", "Reminders"};
		mAdapter = new ArrayAdapter<String>(this,R.layout.nav_layout, navList);
		navView.setAdapter(mAdapter);
		navButtons();
		
		//Loads the required data based on the users previous screen selection.
		loadData();
		if(tvStatus > 0) { //If tvStatus position is more than 0 (Based on the users selection of a list from the list view from the previous screen).
			
			//This sets the activity title using the custom title bar.
			txt = (TextView) findViewById(R.id.txt_title);
			txt.setText("Shows Beginning with " + letter + "'");
			
			//Retrieves all of the TV show names based on the letter.
			c = tvShow_DB.rawQuery("SELECT * FROM " + TABLE_NAME_02 + " WHERE " + TV_NAME + " LIKE " + letter
					+ "%'" + " ORDER BY " + TV_NAME, null);
		}
		addList(); //Adds the query data into the array list.
		loadListView(); //Loads the list view.
	}
	
	/*
	 * This method defines the action upon pressing the 
	 * navigation button to open the side bar.
	 */
	private void navButtons() {
		Button btnNav = (Button)findViewById(R.id.nav);

		btnNav.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addNavData();
				navLayout.openDrawer(Gravity.LEFT);
			}
		});
		reminderButton();
	}
	
	/*
	 * This method defines the reminder button action 
	 * to start a new intent loading the reminder activity.
	 */
	public void reminderButton() {
		btnReminder = (Button)findViewById(R.id.reminders);
		btnReminder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i  = new Intent("view_reminder");
				startActivity(i); 
				Toast.makeText(getApplicationContext(), "Reminders", Toast.LENGTH_SHORT).show();
			}
		});
	}

	/*
	 * This method is responsible for retrieving the 
	 * users on click list position within the ListView of
	 * navigation tab and define a new intent to load the 
	 * required activity.
	 */
	private void addNavData() {	    
		navView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(position == 0) {
					Intent i = new Intent(getApplicationContext(), MenuScreen.class);
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   
					startActivity(i); 
					Toast.makeText(getApplicationContext(), "Menu", Toast.LENGTH_SHORT).show();
				}
				else if(position == 1) {
					Intent i  = new Intent("my_shows");
					startActivity(i); 
					Toast.makeText(getApplicationContext(), "My TV Shows", Toast.LENGTH_SHORT).show();
				}
				else if(position == 2) {
					Intent i  = new Intent("view_calendar"); 
					startActivity(i); 
					Toast.makeText(getApplicationContext(), "Calendar", Toast.LENGTH_SHORT).show();
				}
				else if(position == 3) {
					Intent i  = new Intent("view_reminder"); 
					startActivity(i); 
					Toast.makeText(getApplicationContext(), "Reminders", Toast.LENGTH_SHORT).show();
				}
			}
		});   
	}

	/*
	 * This method loads the list view containing the users 
	 * show data displaying as a list. The information is 
	 * retrieved by running a query within the specific table 
	 * inside the database.
	 */
	public void loadListView() {
		//Loads the custom list view adapter to display the images by using the CustomListAdapter class.
		CustomListAdapter adapter=new CustomListAdapter(this, listOfNames, selector); //Loads the show names and images.
		lView=(ListView)findViewById(R.id.listView);
		lView.setAdapter(adapter);

		lView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
				nameStatus = position; //Sets the seasontvStatus based on the users TV show selection.
				Intent i = new Intent("view_seasons");
				startActivity(i);  //Loads the season activity.
			}
		});
	}

	/*
	 * This method inputs the TV show information
	 * from the database table into the ArrayList.
	 */
	public void addList() {
		listOfNames = new ArrayList<String>(); 
		c.moveToFirst();
		if(!c.isAfterLast()) {
			do {
				String showName = c.getString(1);
				listOfNames.add(showName);
			} while (c.moveToNext());
		}
		c.close();
	}	

	/*
	 * This method is responsible for displaying the correct images based on 
	 * the letter selection from the previous screen.
	 */
	public void loadData() {
		if(tvStatus == 0) { //If position tvStatus equals element 0 then display all of the images.
			
			//Sets the title for the custom title bar.
			txt = (TextView) findViewById(R.id.txt_title);
			txt.setText("All Shows");

			//Retrieves all of the TV show names.
			c = tvShow_DB.rawQuery("SELECT * FROM " + TABLE_NAME_02 + " ORDER BY " + TV_NAME, null);
			selector = img_all; //Loads all of the images from the Integer array in Global class.
		}
		else if(tvStatus == 1) { //If position tvStatus equals element 1 then display images based on shows beginning with "A".
			letter = "'A"; //Sets the letter to "A" to set the title in the custom title bar in the onCreate() method.
			imgList = new Integer[]{R.drawable.show1, R.drawable.show2, R.drawable.show3}; //Loads the specific images.
			selector = imgList; //Selector equals imgList array.
		}
		else if(tvStatus == 2) {
			letter = "'B";
			imgList = new Integer[]{R.drawable.show4, R.drawable.show5, R.drawable.show6, R.drawable.show7};
			selector = imgList;
		}
		else if(tvStatus == 3) {
			letter = "'C";
			imgList = new Integer[]{R.drawable.show8, R.drawable.show9, R.drawable.show10};
			selector = imgList;
		}
		else if(tvStatus == 4) {			
			letter = "'D";
			imgList = new Integer[]{R.drawable.show11, R.drawable.show12, R.drawable.show13};
			selector = imgList;
		}
		else if(tvStatus == 5) {
			letter = "'E";
			imgList = new Integer[]{R.drawable.show14};
			selector = imgList;
		}
		else if(tvStatus == 6) {
			letter = "'F";
			imgList = new Integer[]{R.drawable.show15, R.drawable.show16, R.drawable.show17};
			selector = imgList;
		}
		else if(tvStatus == 7) {
			letter = "'G";
			imgList = new Integer[]{R.drawable.show18, R.drawable.show19, R.drawable.show20};
			selector = imgList;
		}
		else if(tvStatus == 8) {
			letter = "'H";
			imgList = new Integer[]{R.drawable.show21, R.drawable.show22, R.drawable.show23};
			selector = imgList;
		}
		else if(tvStatus == 9) {		
			letter = "'I";
			imgList = new Integer[]{R.drawable.show24};
			selector = imgList;
		}
		else if(tvStatus == 10) {
			letter = "'J";
			imgList = new Integer[]{R.drawable.show25};
			selector = imgList;
		}
		else if(tvStatus == 11) {
			letter = "'K";
			imgList = new Integer[]{R.drawable.show26};
			selector = imgList;
		}
		else if(tvStatus == 12) {
			letter = "'L";
			imgList = new Integer[]{R.drawable.show27, R.drawable.show28};
			selector = imgList;
		}
		else if(tvStatus == 13) {
			letter = "'M";
			imgList = new Integer[]{R.drawable.show29, R.drawable.show30};
			selector = imgList;
		}
		else if(tvStatus == 14) {
			letter = "'N";
			imgList = new Integer[]{R.drawable.show31, R.drawable.show32};
			selector = imgList;
		}
		else if(tvStatus == 15) {
			letter = "'O";
			imgList = new Integer[]{R.drawable.show33, R.drawable.show34, R.drawable.show35};
			selector = imgList;
		}
		else if(tvStatus == 16) {
			letter = "'P";
			imgList = new Integer[]{R.drawable.show36, R.drawable.show37};
			selector = imgList;
		}
		else if(tvStatus == 17) {
			letter = "'Q";
			imgList = new Integer[]{R.drawable.show38};
			selector = imgList;
		}
		else if(tvStatus == 18) {
			letter = "'R";
			imgList = new Integer[]{R.drawable.show39, R.drawable.show40, R.drawable.show41};
			selector = imgList;
		}
		else if(tvStatus == 19) {
			letter = "'S";
			imgList = new Integer[]{R.drawable.show42, R.drawable.show43, R.drawable.show44, 
									R.drawable.show45, R.drawable.show46, R.drawable.show47};
			selector = imgList;
		}
		else if(tvStatus == 20) {
			letter = "'T";
			imgList = new Integer[]{R.drawable.show48, R.drawable.show49, R.drawable.show50, 
					R.drawable.show51, R.drawable.show52, R.drawable.show53, R.drawable.show54, 
					R.drawable.show55, R.drawable.show56};
			selector = imgList;
		}
		else if(tvStatus == 21) {
			letter = "'U";
			imgList = new Integer[]{R.drawable.show57};
			selector = imgList;
		}
		else if(tvStatus == 22) {
			letter = "'V";
			imgList = new Integer[]{R.drawable.show58};
			selector = imgList;
		}
		else if(tvStatus == 23) {
			letter = "'W";
			imgList = new Integer[]{R.drawable.show59};
			selector = imgList;
		}
		else if(tvStatus == 24) {
			letter = "'X";
			imgList = new Integer[]{R.drawable.show60};
			selector = imgList;
		}
		else if(tvStatus == 25) {
			letter = "'Y";
			imgList = new Integer[]{R.drawable.show61};
			selector = imgList;
		}	
		else if(tvStatus == 26) {
			letter = "'Z";
			imgList = new Integer[]{R.drawable.show62};
			selector = imgList;
		}
	}
	
	/*
	 * This method is responsible for modifying the back button  
	 * to re-launch the list of shows as the new intent.
	 */
	public void onBackPressed() {
		super.onBackPressed(); 
		Intent i = new Intent("list_of_shows");
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
		startActivity(i);
	}
}