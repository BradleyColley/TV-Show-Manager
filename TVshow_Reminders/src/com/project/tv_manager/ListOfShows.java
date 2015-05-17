package com.project.tv_manager;

/*
 * This class displays the alphabet list of shows for the user
 * to select which letter to view their TV show.
 */

import java.util.ArrayList;

import com.project.tvshow_reminders.R;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import static com.project.tv_manager.Global.*;

public class ListOfShows extends ListActivity {
	private ArrayList<String> alphabetical_list;

	/*
	 * Once the activity is called, the onCreate() method
	 * will launch first to load the layout.
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_list_layout);

		//Below outlines the navigation side bar layout and defines what gets displayed.
		navView = (ListView)findViewById(R.id.navList);
		navLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
		navList = new String[] { "Home", "My TV Shows", "Calendar", "Reminders"};
		mAdapter = new ArrayAdapter<String>(this,R.layout.nav_layout, navList);
		navView.setAdapter(mAdapter);
		navButtons();

		//This sets the activity title using the custom title bar.
		txt = (TextView) findViewById(R.id.txt_title);
		txt.setText("List of Shows");
		
		getAlphabet();
		loadListView();
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
					Intent i = new Intent(getApplicationContext(), MenuScreen.class); //Menu screen
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   
					startActivity(i); 
					Toast.makeText(getApplicationContext(), "Menu", Toast.LENGTH_SHORT).show();
				}
				else if(position == 1) {
					Intent i  = new Intent("my_shows"); //My TV Shows
					startActivity(i); 
					Toast.makeText(getApplicationContext(), "My TV Shows", Toast.LENGTH_SHORT).show();
				}
				else if(position == 2) {
					Intent i  = new Intent("view_calendar"); //Calendar
					startActivity(i); 
					Toast.makeText(getApplicationContext(), "Calendar", Toast.LENGTH_SHORT).show();
				}
				else if(position == 3) {
					Intent i  = new Intent("view_reminder"); //Reminders
					startActivity(i); 
					Toast.makeText(getApplicationContext(), "Reminders", Toast.LENGTH_SHORT).show();
				}
			}
		});   
	}

	/*
	 * This method loads the list view containing the 
	 * alphabetical letters from the ArrayList.
	 */
	public void loadListView() {
		setListAdapter(new ArrayAdapter<String>(this, R.layout.alphabetical_layout, R.id.Itemname, alphabetical_list));
		lView = getListView();

		lView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				tvStatus = position; //Sets status the list position the user has selected.
				Intent i = new Intent("view_all"); 
				startActivity(i);
			}
		});
	}

	/*
	 * This method gets the alphabet data from the database
	 * table by running a raw query and inputting the information
	 * into the ArrayList.
	 */
	public void getAlphabet() {
		alphabetical_list = new ArrayList<String>();
		Cursor c = tvShow_DB.rawQuery("SELECT * FROM " + TABLE_NAME_01, null);

		c.moveToFirst();
		if (!c.isAfterLast()) {
			do {
				String letter = c.getString(1); //Retrieves the letters.
				alphabetical_list.add(letter); //Adds the letters into the array.
			} while (c.moveToNext()); //Moves onto the next letter.
		}
		c.close(); //Closes the table cursor.
	}
	
	/*
	 * This method is responsible for modifying the back button  
	 * to re-launch the menu screen as the new intent.
	 */
	public void onBackPressed() {
		super.onBackPressed(); 
		Intent i = new Intent(getApplicationContext(), MenuScreen.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   
		this.startActivity(i); 
	}
}