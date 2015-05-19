package com.project.tv_manager;

/*
 * The Calendar class displays the current reminders that have been set within 
 * the Reminders class. The data is retrieved by pulling the information out of
 * a table inside my SQLiteDatabase. 
 * 
 */

import static com.project.tv_manager.Global.*;
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
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.project.tvshow_reminders.R;

public class Calendar extends Activity {
	private Cursor c;
	
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
		navList = new String[] { "Home", "My TV Shows", "List of Shows", "Reminders"};
		mAdapter = new ArrayAdapter<String>(this,R.layout.nav_layout, navList);
		navView.setAdapter(mAdapter);
		navButtons();

		//This sets the activity title using the custom title bar.
		txt = (TextView) findViewById(R.id.txt_title);
		txt.setText("Calendar");
		
		loadListView(); //Loads the calendar list.
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
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //Closes the other activities on top of the main activity and this intent
					startActivity(i); 							//will be delivered at the top as a new intent instead of re-launching.
					Toast.makeText(getApplicationContext(), "Menu", Toast.LENGTH_SHORT).show();
				} 
				else if(position == 1) {
					Intent i  = new Intent("my_shows");
					startActivity(i); 
					Toast.makeText(getApplicationContext(), "My TV Shows", Toast.LENGTH_SHORT).show();
				}
				else if(position == 2) {
					Intent i  = new Intent("list_of_shows");
					startActivity(i); 
					Toast.makeText(getApplicationContext(), "List of Shows", Toast.LENGTH_SHORT).show();
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
	 * This method retrieves the required information from the query 
	 * then displays the content within the required text views in the layout
	 * which will show up on each list position within the ListView.
	 */
	public void loadListView() {
		c = getData(); //Cursor "c" equals the getData() method gaining the information.
		
		//Defines the required table fields.
		String[] tvFields = new String[] {COLUMN_ID, POSITION, TV_NAME, SEASON, EP_NUMBER, TIME, DATE}; 
		//Links up the text views to the specific table fields to display the required content.
		int[] views = new int[] {0, 0, R.id.view_large2, R.id.view_small, R.id.view_small2, R.id.view_large, R.id.view_medium}; 

		//Maps table columns from the cursor to the TextViews.
		customAdapter = new SimpleCursorAdapter(this, R.layout.calendar_layout, c, tvFields, views, 0); 
		lView = (ListView) findViewById(R.id.listView);
		lView.setAdapter(customAdapter);
	}

	/*
	 * This method runs the raw query method within the database
	 * to pull out the information required based on the query 
	 * conditions then returns the cursor containing the data.
	 */
	public Cursor getData() {
		//Retrieves data in all columns within update_reminder table apart from retrieving a row 
		//containing the word "ADD REMINDER".
		Cursor c = tvShow_DB.rawQuery("SELECT * FROM update_reminder WHERE name_of_show NOT LIKE "
										+ "'%ADD REMINDER%' ORDER BY " + DATE +" DESC", null);
		return c;
	}	
}
