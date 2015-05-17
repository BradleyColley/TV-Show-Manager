package com.project.tv_manager;

/*
 * This class displays all current TV show reminders set by the user and 
 * an option to add new reminders.
 * 
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.AdapterView.OnItemClickListener;

import com.project.tvshow_reminders.R;

import static com.project.tv_manager.Global.*;

public class Reminders extends Activity {
	private Cursor c;
	private int _id;
	
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
		navList = new String[] { "Home", "My TV Shows", "List of Shows", "Calendar"};
		mAdapter = new ArrayAdapter<String>(this,R.layout.nav_layout, navList);
		navView.setAdapter(mAdapter);
		navButton();

		//This sets the activity title using the custom title bar.
		txt = (TextView) findViewById(R.id.txt_title);
		txt.setText("Reminders");

		loadListView(); //Loads the users TV show reminders.
	}

	/*
	 * This method defines the action upon pressing the 
	 * navigation button to open the side bar.
	 */
	private void navButton() {
		Button btnNav = (Button)findViewById(R.id.nav);

		btnNav.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addNavData();
				navLayout.openDrawer(Gravity.LEFT);
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
					Intent i  = new Intent("my_shows"); //My TV shows
					startActivity(i); 
					Toast.makeText(getApplicationContext(), "My TV Shows", Toast.LENGTH_SHORT).show();
				}
				else if(position == 2) {
					Intent i  = new Intent("list_of_shows"); //List of shows
					startActivity(i); 
					Toast.makeText(getApplicationContext(), "List of Shows", Toast.LENGTH_SHORT).show();
				}
				else if(position == 3) {
					Intent i  = new Intent("view_calendar"); //Calendar
					startActivity(i); 
					Toast.makeText(getApplicationContext(), "Calendar", Toast.LENGTH_SHORT).show();
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
		c = getData(); //Loads table data
		
		//Defines the required table fields.
		String[] reminderFields = new String[] {COLUMN_ID, POSITION, TV_NAME, SEASON, EP_NUMBER, TIME, DATE};
		//Links up the text views to the specific table fields to display the required content.
		int[] views = new int[] {0, 0, R.id.view_large, R.id.view_small, R.id.view_small2, R.id.view_medium2, R.id.view_medium};

		//Maps table columns from the cursor to the TextViews.
		customAdapter = new SimpleCursorAdapter(this, R.layout.reminder_layout, c, reminderFields, views, 0);
		lView = (ListView) findViewById(R.id.listView);
		lView.setAdapter(customAdapter);
		
		//Once the user presses a current reminder in the list view, it will retrieve the position
		//selected and display a dialog window informing the user if they are sure to delete the 
		//reminder which will remove it from the database.
		lView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				_id = (int) id; //Retrieves the id the user has selected.
				
				int count = lView.getAdapter().getCount(); //Calculates how many lists there are.
				int value = count-1; //Reduces the count by 1 so "ADD REMINDER" list can contain an action.
				
				//If the position within the list equals the "ADD REMINDER" value then,
				if(position == value) {
					//status = position; 
					Intent i = new Intent("add_reminder"); //Sets a new intent activity.
					startActivity(i); //Loads the add reminder activity.
					Toast.makeText(getApplicationContext(), "Add Reminder", Toast.LENGTH_SHORT).show(); 
				} else { 
				//If position doesn't equal to the value then run the dialog to remove the reminder.
					createDialog();
				}
			}
		});
	}

	/*
	 * This method runs the raw query method within the database
	 * to pull out the information required based on the query 
	 * conditions then returns the cursor containing the data.
	 */
	public Cursor getData() {
		//Retrieves data in all columns in the order of descending the TV_NAME column within update_reminder table.
		Cursor c = tvShow_DB.rawQuery("SELECT * FROM update_reminder ORDER BY " + TV_NAME +" DESC", null);
		return c;
	}	
	
	/*
	 * This method will create a dialog to get the user to confirm
	 * if they want to delete the current reminder that has been selected.
	 * If yes, then the reminder will be removed from the database table.
	 */
	private void createDialog() {
		AlertDialog.Builder confirmDelete = new AlertDialog.Builder(this);
		confirmDelete.setMessage("Are you sure you want to delete?");
		confirmDelete.setCancelable(false); //Avoids the dialog to be cancelled, forcing the user to choose one of the options.
		
		confirmDelete.setPositiveButton("Yes", new DialogInterface.OnClickListener() {		
			@Override
			public void onClick(DialogInterface dialog, int which) {
				readOperations.deleteRow(_id); //Removes the selected reminder from the _id "position" row.
				
				//Reloads the activity to show an updated reminder list.
				Intent i  = new Intent("view_reminder");
				startActivity(i); 
			}
		});
		
		confirmDelete.setNegativeButton("No", new DialogInterface.OnClickListener() {		
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		confirmDelete.create().show(); //Creates the dialog.
	}
}