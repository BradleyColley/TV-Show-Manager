package com.project.tv_manager;

/*
 * This class is responsible for adding new reminders to the database, depending
 * on the users input. The class will call NotificationManager, AlarmManager and
 * Calendar API's to construct the notification reminders.
 * 
 */

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
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
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.project.tv_manager.database.SQLOperations;
import com.project.tvshow_reminders.R;
import static com.project.tv_manager.Global.*;

public class AddReminder extends Activity {
	private Spinner spnShow, spnSeason, spnEpisode;
	private ArrayList<String> list = new ArrayList<String>(); //Contains the show names.
	private ArrayList<String> list2 = new ArrayList<String>(); //Contains the seasons.
	private ArrayList<String> list3 = new ArrayList<String>(); //Contains the episodes.
	private String value, value2 = "";
	private Intent i;
	private Button btnSend;
	private DatePicker dPicker; //Date picker
	private TimePicker tPicker; //Time picker
	public static AlarmManager aManager; //Manages alarms.
	public static NotificationManager nManager; //Manages notifications.
	public static int id;

	/*
	 * Once the activity is called, the onCreate() method
	 * will launch first to load the layout.
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_reminder_layout);

		//Below outlines the navigation side bar layout and defines what gets displayed.
		navView = (ListView)findViewById(R.id.navList);
		navLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
		navList = new String[] { "Home", "My TV Shows", "List of Shows", "Calendar", "Reminders"};
		mAdapter = new ArrayAdapter<String>(this,R.layout.nav_layout, navList);
		navView.setAdapter(mAdapter);
		navButtons();

		//Outlines the drop down boxes and date/time pickers.
		spnShow = (Spinner)findViewById(R.id.spinner);
		spnSeason = (Spinner)findViewById(R.id.spinner2);
		spnEpisode = (Spinner)findViewById(R.id.spinner3);
		btnSend = (Button) findViewById(R.id.send);
		dPicker = (DatePicker) findViewById(R.id.datePicker1);
		tPicker = (TimePicker)findViewById(R.id.timePicker1);

		//This sets the activity title using the custom title bar.
		txt = (TextView) findViewById(R.id.txt_title);
		txt.setText("Add Reminders");

		//Notification and Alarm managers getting their services.
		aManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		loadList1();
		setNotification();
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
	 * This method is responsible for loading the TV show
	 * names in the first spinner by retrieving the data 
	 * from the database table containing the show episodes from 
	 * MyTVShows screen. 
	 */
	public void loadList1() {
		getName(); //Loads the getName() method.
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
		spnShow.setAdapter(adapter);
		spnShow.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				list2.clear(); //Clears the 2nd spinner if selected.
				list3.clear(); //Clears the 3rd spinner if selected.

				value = list.get((int) id);
				loadList2(); //Loads the next spinner list containing the season data depending on which show
				//the user has selected.
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
	}

	/*
	 * This method is responsible for loading the seasons depending
	 * on what contains in My TV Shows. The data will be retrieved
	 * from the database table matching the show selected in the first
	 * spinner.
	 */
	public void loadList2() {
		getSeason(); //Loads the getSeason() method.
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list2);
		spnSeason.setAdapter(adapter);
		spnSeason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				list3.clear(); //Clears the 3rd spinner if selected.

				value2 = list2.get((int) id); //Retrieves the array element position based on user input.

				if(list2.size() >= 2) { //Only allow the drop down to become active until the list contains episode data.
					int n = 2;
					//Updates the "n" variable when the season ArrayList is greater to check which season they have selected
					//below in the if statements to find the correct episodes in the next spinner.
					if(list2.size() == 3) {
						n = 3;
					} else if(list2.size() == 4) {
						n = 4;
					} else if(list2.size() == 5) {
						n = 5;
					}
					for (int i = 1; i < n;) {
						if(list2.get(i).equals("Season 1")) { //If list2 equals season 1 then,
							if(value2.equals(list2.get(i))) { //If value2 position equals to season 1 then,
								value2 = value +" - S1"; //Value2 equals the value TV show name from spinner 1 and contains "- S1"
								//at the end to search for the required data within the database table.
								loadList3(); //Loads method.
							}
						} else if(list2.get(i).equals("Season 2")) {
							if(value2.equals(list2.get(i))) {
								value2 = value +" - S2";
								loadList3();
							}
						} else if(list2.get(i).equals("Season 3")) {
							if(value2.equals(list2.get(i))) {
								value2 = value +" - S3";
								loadList3();
							}
						} else if(list2.get(i).equals("Season 4")) {
							if(value2.equals(list2.get(1))) {
								value2 = value +" - S4";
								loadList3();
							}
						} else if(list2.get(i).equals("Season 5")) {
							if(value2.equals(list2.get(i))) {
								value2 = value +" - S5";
								loadList3();
							}
						}
						i++;
					}
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
	}

	/*
	 * This method is responsible for loading the episodes depending
	 * on what contains in My TV Shows. The data will be retrieved
	 * from the database table matching the show and season selected
	 * in the first and second spinners.
	 */
	public void loadList3() {
		getEpisode(); //Loads the episode data.
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, list3);
		spnEpisode.setAdapter(adapter);
		spnEpisode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
	}

	/*
	 * This method runs the raw query method within the database
	 * to pull out the name of show data based on the query 
	 * conditions then returns the cursor containing the data.
	 */
	public void getName() {
		//Retrieves the name of show column data from my_shows table.
		Cursor c = tvShow_DB.rawQuery("select DISTINCT name_of_show, max(_id) FROM my_shows GROUP BY name_of_show", null);
		list.add(" "); //Adds a blank element to the list.
		c.moveToFirst();
		if (!c.isAfterLast()) { //If the cursor data doesn't equal to the last element in the table then,
			do {
				list.add(c.getString(0)); //Carries on inputting in the column data into the array.
			} while (c.moveToNext()); //Move onto the next element.
		}
	}	

	/*
	 * This method runs the raw query method within the database
	 * to pull out the show seasons data based on the query 
	 * conditions then returns the cursor containing the data.
	 */
	public void getSeason() {
		//Retrieves all of the seasons based on the TV show selected and doesn't display duplicates.
		Cursor c = tvShow_DB.rawQuery("select DISTINCT * FROM my_shows WHERE name_of_show LIKE '"
				+ value + "%' GROUP BY season", null);
		list2.add(" "); //Adds a blank element to the list.
		c.moveToFirst();
		if (!c.isAfterLast()) {
			do {
				list2.add(c.getString(2)); //Carries on inputting in the column data into the array by retrieving 
				//the specific column.
			} while (c.moveToNext()); //Moves onto the next element in the table.
		}
	}

	/*
	 * This method runs the raw query method within the database
	 * to pull out the show season episodes data based on the query 
	 * conditions then returns the cursor containing the data.
	 */
	public void getEpisode() {
		//Retrieves the episode data from my_shows table based on the value2 string.
		Cursor c = tvShow_DB.rawQuery("SELECT _id, ep_num FROM my_shows WHERE related_to LIKE '"+ value2 + "%'", null);
		list3.add(" "); //Adds blank element.
		c.moveToFirst();
		if (!c.isAfterLast()) {
			do {
				list3.add(c.getString(1)); //Carries on inputting in the column data into the array.
			} while (c.moveToNext()); //Moves onto the next element in the table.
		}
	}

	/*
	 * This method acts as setting up the alarm notifications once the user
	 * presses the submit button to confirm their reminder.
	 */
	public void setNotification() {
		btnSend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {	
				//If the spinners don't equal to their first element then proceed.
				if(spnShow.getSelectedItemPosition() != 0 && spnEpisode.getSelectedItemPosition() != 0 
						&& spnSeason.getSelectedItemPosition() != 0) { 

					//Sets up the calendar preferences. 
					Calendar c = Calendar.getInstance();
					c.set(Calendar.MONTH, dPicker.getMonth());
					c.set(Calendar.DAY_OF_MONTH, dPicker.getDayOfMonth());
					c.set(Calendar.YEAR, dPicker.getYear());
					c.set(Calendar.HOUR_OF_DAY, tPicker.getCurrentHour());
					c.set(Calendar.MINUTE, tPicker.getCurrentMinute());
					c.set(Calendar.MILLISECOND, 0);

					id = (int)System.currentTimeMillis(); //Random number to add as an intent.

					//Retrieves the information to input into the database table.
					String name = list.get(spnShow.getSelectedItemPosition());
					String season = list2.get(spnSeason.getSelectedItemPosition());
					String episode = list3.get(spnEpisode.getSelectedItemPosition());
					String hour = tPicker.getCurrentHour().toString();
					String minute = tPicker.getCurrentMinute().toString();
					String day = String.valueOf(dPicker.getDayOfMonth());
					String year = String.valueOf(dPicker.getYear());

					//Converts to a different time format to display onto the screen.
					SimpleDateFormat m	= new SimpleDateFormat("MMMM", Locale.ENGLISH); //Month
					SimpleDateFormat d	= new SimpleDateFormat("EEE", Locale.ENGLISH); //Day
					String month_format = m.format(c.getTime());
					String day_format = d.format(c.getTime());

					String time = hour + ":" + minute;
					String date = day_format + " " + day + " " + month_format + " " + year;

					//Sets up notification
					i = new Intent(getApplicationContext(), NotificationService.class);
					i.putExtra("title", name);
					i.putExtra("message", episode);

					//Database operations
					readOperations = new SQLOperations(getBaseContext());
					readOperations.openDataBase();
					readOperations.addNotification(id, name, season, episode, time, date); //Inputs the data above into the table.

					//Sets up alarm
					PendingIntent pIntent = PendingIntent.getService(getApplicationContext(), id, i, PendingIntent.FLAG_UPDATE_CURRENT);
					aManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pIntent); //Sets the reminder.
					Toast.makeText(getApplicationContext(), "Reminder set", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
}