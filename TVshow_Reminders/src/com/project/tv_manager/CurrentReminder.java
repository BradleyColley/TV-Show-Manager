package com.project.tv_manager;

/*
 * This class is responsible for displaying the current reminder information
 * based on the TV show the user has chosen to remind themselves about. 
 * 
 */

import static com.project.tv_manager.Global.*;

import com.project.tv_manager.database.SQLOperations;
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
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class CurrentReminder extends Activity {
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
		navList = new String[] { "Home", "My TV Shows", "List of Shows", "Calendar", "Reminders"};
		mAdapter = new ArrayAdapter<String>(this,R.layout.nav_layout, navList);
		navView.setAdapter(mAdapter);
		navButtons();

		//This sets the activity title using the custom title bar.
		txt = (TextView) findViewById(R.id.txt_title);
		txt.setText("Current Reminder");
		
		NotificationService.notificationManager.cancel(AddReminder.id); //Cancels the active notification.
		NotificationService.mp.stop(); //Stops the notification sound.

		//Database operations
		readOperations = new SQLOperations(this);
		readOperations.generateDB(); //Generates the SQLiteDatabase.
		tvShow_DB = readOperations.openDataBase(); //Opens the SQLiteDatabase.
		loadListView();
		readOperations.deleteRow2(AddReminder.id); //Removes the active reminder from the table row.
		readOperations.close();	//Closes the database.
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
					Intent i  = new Intent("list_of_shows");
					startActivity(i); 
					Toast.makeText(getApplicationContext(), "List of Shows", Toast.LENGTH_SHORT).show();
				}
				else if(position == 3) {
					Intent i  = new Intent("view_calendar"); 
					startActivity(i); 
					Toast.makeText(getApplicationContext(), "Calendar", Toast.LENGTH_SHORT).show();
				}
				else if(position == 4) {
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
		c = getData(); //Loads the table data.

		//Defines the required table fields.
		String[] fields = new String[] {COLUMN_ID, POSITION, TV_NAME, SEASON, EP_NUMBER, TIME, DATE};
		//Links up the text views to the specific table fields to display the required content.
		int[] views = new int[] {0, 0, R.id.view_large, R.id.view_medium, R.id.view_medium2, R.id.TextView01, 0};
		
		//Maps table columns from the cursor to the TextViews.
		customAdapter = new SimpleCursorAdapter(this, R.layout.current_layout, c, fields, views, 0);
		lView = (ListView) findViewById(R.id.listView);
		lView.setAdapter(customAdapter);
	}

	/*
	 * This method runs the raw query method within the database
	 * to pull out the information required based on the query 
	 * conditions then returns the cursor containing the data.
	 */
	public Cursor getData() {
		//Retrieves data in all columns in update_reminder table based on the reminder ID position only.
		Cursor c = tvShow_DB.rawQuery("SELECT * FROM update_reminder WHERE " + POSITION + " LIKE '" + AddReminder.id + "%'", null);
		return c;
	}	
}