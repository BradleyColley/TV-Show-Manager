package com.project.tv_manager;

/*
 * This class acts as the users must watch TV show episodes to gain the
 * ability to start setting reminders for these. MyTVShows class retrieves
 * the users show episodes from a table within my database once the user 
 * adds an episode from the "ListOfShows" categories. 
 */

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.project.tvshow_reminders.R;
import static com.project.tv_manager.Global.*;

public class MyTVShows extends Activity {
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
		navList = new String[] {"Home", "List of Shows", "Calendar", "Reminders"};
		mAdapter = new ArrayAdapter<String>(this,R.layout.nav_layout, navList);
		navView.setAdapter(mAdapter);
		navButtons();
		
		//This sets the activity title using the custom title bar.
		txt = (TextView) findViewById(R.id.txt_title);
		txt.setText("My TV Shows");

		loadListView(); //Loads the users TV shows data.
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
					Intent i = new Intent(getApplicationContext(), MenuScreen.class); //Main screen
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   
					startActivity(i); 
					Toast.makeText(getApplicationContext(), "Menu", Toast.LENGTH_SHORT).show();
				}
				else if(position == 1) {
					Intent i  = new Intent("list_of_shows"); //List of shows
					startActivity(i); 
					Toast.makeText(getApplicationContext(), "List of Shows", Toast.LENGTH_SHORT).show();
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
	 * This method loads the list view containing the users 
	 * show data displaying as a list. The information is 
	 * retrieved by running a query within the specific table 
	 * inside the database.
	 */
	public void loadListView() {
		c = getData(); //Loads getData() method.	

		//Defines the required table fields.
		String[] showFields = new String[] {COLUMN_ID, TV_NAME, SEASON, EP_NUMBER, EP_NAME, POSITION, RELATED};
		//Links up the text views to the specific table fields to display the required content.
		int[] views = new int[] {0, R.id.view_large, R.id.view_small, R.id.view_medium, 0, 0, 0};

		//Maps table columns from the cursor to the TextViews.
		customAdapter = new SimpleCursorAdapter(this, R.layout.episode_layout, c, showFields, views, 0);
		lView = (ListView) findViewById(R.id.listView);
		lView.setAdapter(customAdapter);

		//Once the user presses one of the show episodes within the list, depending on what position 
		//in the list view, it will remove the episode from the screen and delete it from the database table.
		lView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
				c.moveToPosition(position); //Sets the cursor to the current position of the list element 
											//based on users input to remove.

				int pos = c.getInt(5); //Retrieves the data column position depending on the users input.
				readOperations.delete_byID((int) id); //Removes the episode from the table.
				duplicates.remove(pos); //Removes the deleted episode position number from the ArrayList
									//to allow the user to select this episode again within the list of shows.
				Toast.makeText(getApplicationContext(),"Removed",Toast.LENGTH_SHORT).show();
				
				//Reloads the activity to show an updated list.
				Intent i  = new Intent("my_shows");
				startActivity(i); 
			};
		});
	}

	/*
	 * This method runs the raw query method within the database
	 * to pull out the information required based on the query 
	 * conditions then returns the cursor containing the data.
	 */
	public Cursor getData() {
		Cursor c = tvShow_DB.rawQuery("SELECT * FROM my_shows", null); //Retrieves data in all columns within my_shows table.
		//If the table contains no data then display the toast message to inform the user upon loading the activity.
		if(c.getCount() == 0) {
			Toast.makeText(getApplicationContext(),"No Shows Available",Toast.LENGTH_SHORT).show();
		}
		return c;
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