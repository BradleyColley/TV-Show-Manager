package com.project.tv_manager;

/*
 * This class acts as the main activity for the application. Once the app loads it will
 * display this class as the current screen, to navigate the users to their shows or
 * set new reminders.
 * 
 */

import com.project.tv_manager.database.SQLOperations;
import com.project.tvshow_reminders.R;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import static com.project.tv_manager.Global.*;

public class MenuScreen extends ListActivity {
	private Button MyShows, ListShows, Calendar;
	
	/*
	 * Once the activity is called, the onCreate() method
	 * will launch first to load the layout.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		new Global(); //Loads the ArrayList within Global class.
		
		//Below outlines the navigation side bar layout and defines what gets displayed.
		navView = (ListView)findViewById(R.id.navList);
		navLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
		navList = new String[] {"My TV Shows", "List of Shows", "Calendar", "Reminders"};
		mAdapter = new ArrayAdapter<String>(this,R.layout.nav_layout, navList);
		navView.setAdapter(mAdapter);
		navButtons();
		
		//This sets the activity title due to ListActivity not supporting a title bar.
		txt = (TextView) findViewById(R.id.txt_title);
		txt.setText("TV Show Manager");

		readOperations = new SQLOperations(this); 
		readOperations.generateDB(); //Generates the SQLiteDatabase.
		tvShow_DB = readOperations.openDataBase(); //Opens the SQLiteDatabase.
		Log.d("Main operations", "Database loaded");

		menuButtons(); //Menu navigation.
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
				//If position 0 of the ListView has been pressed then start the intent of 
				//loading MyTVShows activity else check the other positions.
				if(position == 0) {
					Intent i  = new Intent("my_shows");
					startActivity(i); 
					Toast.makeText(getApplicationContext(), "My TV Shows", Toast.LENGTH_SHORT).show();
				}
				else if(position == 1) {
					Intent i  = new Intent("list_of_shows");
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
	 * This method defines the menu navigation screen to 
	 * direct the user to certain places within the application.
	 */
	public void menuButtons() {
		//Defines which button to an element within the layout.
		MyShows = (Button) findViewById(R.id.btnMyShows);
		ListShows = (Button) findViewById(R.id.btnList);
		Calendar = (Button) findViewById(R.id.btnCalendar);

		MyShows.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i  = new Intent("my_shows");
				startActivity(i); 
			}
		});

		ListShows.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i  = new Intent("list_of_shows"); //"i" is declared as the ListOfShows class.
				startActivity(i);  //Start the intent activity which will change the current screen.
			}
		});
		
		Calendar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i2  = new Intent("view_calendar");
				startActivity(i2); 
			}
		});
	}
}