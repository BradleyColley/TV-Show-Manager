package com.project.tv_manager.list_screens;

/*
 * This class is responsible for displaying the episodes based on the users
 * selection from choosing a specific TV show season in the previous activity.
 * 
 * Now the user will have the ability to add an episode to their "My TV Shows" 
 * category to begin setting new reminders.
 * 
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import static com.project.tv_manager.Global.*;
import com.project.tv_manager.MenuScreen;
import com.project.tvshow_reminders.R;

public class EpisodeList extends Activity {
	private String name, title, getName;
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
		
		loadData();
		loadViewAll();

		//This sets the activity title using the custom title bar.
		txt = (TextView) findViewById(R.id.txt_title);
		txt.setText(title);

		loadListView();
	}

	/*
	 * This method defines the action upon pressing the 
	 * navigation button to open the side bar.
	 */
	public void navButtons() {
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
		c = getData();	

		//Defines the required table fields.
		String[] tvFields = new String[] {COLUMN_ID, SEASON, EP_NUMBER, EP_NAME, RELATED};
		//Links up the text views to the specific table fields to display the required content.
		int[] views = new int[] {0, R.id.view_small, R.id.view_medium, R.id.view_large, 0};

		//Maps table columns from the cursor to the TextViews.
		customAdapter = new SimpleCursorAdapter(this, R.layout.episode_layout, c, tvFields, views, 0);
		lView = (ListView) findViewById(R.id.listView);
		lView.setAdapter(customAdapter);

		//Once the user presses an episode within the list view it will add the TV show episode data
		//into the database table for My TV Shows to retrieve.
		lView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
				if(duplicates.get(position) != getName) { //If the array position doesn't equal the name then,
					duplicates.add(position, getName); //add the position and name in so they user cannot add twice.

					//Retrieves the TV show episode data to input into the database table.
					String getSeason = c.getString(1);	
					String getNumber = c.getString(2);			
					String getEP = c.getString(3);	
					int getPos = position;	
					String getRelated = c.getString(4);	

					//Inputs the data into the database table.
					readOperations.putInformation(getName, getSeason, getNumber, getEP, getPos, getRelated);

					//Informs the user the episode has been added to their shows.
					Toast.makeText(getApplicationContext(),"Added to my shows",Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getApplicationContext(),"Exists in my shows",Toast.LENGTH_SHORT).show();
				}
			};
		});
	}

	/*
	 * This method runs the raw query method within the database
	 * to pull out the information required based on the query 
	 * conditions then returns the cursor containing the data.
	 */
	public Cursor getData() {
		//Retrieves the required data from the columns in tv_list table related to the tv show name.
		Cursor c = tvShow_DB.rawQuery("SELECT * FROM tv_list " + " WHERE " + RELATED + " LIKE '"
				+ name + "%'", null);
		return c;
	}	

	/*
	 * This method is responsible for loading the correct TV show
	 * episodes based on the name and season selection from the user.
	 */
	public void loadData() {
		if(tvStatus == 1) { //If tvStatus equals element 1 which is letter "A" category then,
			if(nameStatus == 0) { //If nameStatus equals element 0 which is "Anger Management" show then,
				getName = "Anger Management"; //set getName to the show name.
				if(seasonStatus == 0) { //If seasonStatus equals element 0 then set the required variables.
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			} 
			else if(nameStatus == 1) { //else if nameStatus equals element 1 which is the "Arrow" show then,
				getName = "Arrow";  //set getName to the show name.
				if(seasonStatus == 0) { //If seasonStatus equals element 0 then set the required variables.
					title = getName + " - Season 1";
					name = getName + " - S1";
				} else if(seasonStatus == 1) { //else if seasonStatus equals element 1 then set the required variables.
					title = getName + " - Season 2";
					name = getName + " - S2";
				}
			} 
			else if(nameStatus == 2) {
				getName = "Atlantis";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				} else if(seasonStatus == 1) {
					title = getName + " - Season 2";
					name = getName + " - S2";
				}
			}
		} else if(tvStatus == 2) { //If tvStatus equals element 1 which is letter "B" category then,
			if(nameStatus == 0) { //If nameStatus equals element 0 which is the "Banished" show then,
				getName = "Banished"; //set getName to the show name.
				if(seasonStatus == 0) { //If seasonStatus equals element 0 then set the required variables.
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			} 
			else if(nameStatus == 1) {
				getName = "Better Call Saul";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			} 
			else if(nameStatus == 2) {
				getName = "Bitten";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			} 
			else if(nameStatus == 3) {
				getName = "Black Sails";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				} else if(seasonStatus == 1) {
					title = getName + " - Season 2";
					name = getName + " - S2";
				}
			}
		} else if(tvStatus == 3) {
			if(nameStatus == 0) {
				getName = "Charmed";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			} 
			else if(nameStatus == 1) {
				getName = "Constantine";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			} 
			else if(nameStatus == 2) {
				getName = "Continuum";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				} else if(seasonStatus == 1) {
					title = getName + " - Season 2";
					name = getName + " - S2";
				} else if(seasonStatus == 2) {
					title = getName + " - Season 3";
					name = getName + " - S3";
				}
			}
		} else if(tvStatus == 4) {
			if(nameStatus == 0) {
				getName = "Defiance";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			} 
			else if(nameStatus == 1) {
				getName = "Doctor Who";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			} 
			else if(nameStatus == 2) {
				getName = "Downton Abbey";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				} else if(seasonStatus == 1) {
					title = getName + " - Season 2";
					name = getName + " - S2";
				}
			}
		} else if(tvStatus == 5) {
			if(nameStatus == 0) {
				getName = "Extant";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			} 
		} else if(tvStatus == 6) {
			if(nameStatus == 0) {
				getName = "Falling Skies";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				} else if(seasonStatus == 1) {
					title = getName + " - Season 2";
					name = getName + " - S2";
				} else if(seasonStatus == 2) {
					title = getName + " - Season 3";
					name = getName + " - S3";
				}
			}
			else if(nameStatus == 1) {
				getName = "Fear the Walking Dead";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
			else if(nameStatus == 2) {
				getName = "Forever";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
		} else if(tvStatus == 7) {
			if(nameStatus == 0) {
				getName = "Game of Thrones";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				} else if(seasonStatus == 1) {
					title = getName + " - Season 2";
					name = getName + " - S2";
				} else if(seasonStatus == 2) {
					title = getName + " - Season 3";
					name = getName + " - S3";
				} else if(seasonStatus == 3) {
					title = getName + " - Season 4";
					name = getName + " - S4";
				} else if(seasonStatus == 4) {
					title = getName + " - Season 5";
					name = getName + " - S5";
				}
			}
			else if(nameStatus == 1) {
				getName = "Gotham";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
			else if(nameStatus == 2) {
				getName = "Grimm";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
		} else if(tvStatus == 8) {
			if(nameStatus == 0) {
				getName = "Hannibal";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				} else if(seasonStatus == 1) {
					title = getName + " - Season 2";
					name = getName + " - S2";
				}
			}
			else if(nameStatus == 1) {
				getName = "Helix";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
			else if(nameStatus == 2) {
				getName = "Homeland";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
		} else if(tvStatus == 9) {
			if(nameStatus == 0) {
				getName = "IZombie";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
		} else if(tvStatus == 10) {
			if(nameStatus == 0) {
				getName = "Justified";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
		} else if(tvStatus == 11) {
			if(nameStatus == 0) {
				getName = "Krypto: the Superdog";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
		} else if(tvStatus == 12) {
			if(nameStatus == 0) {
				getName = "Lego Star Wars: The Yoda Chronicles";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			} 
			else if(nameStatus == 1) {
				getName = "Luther";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
		} else if(tvStatus == 13) {
			if(nameStatus == 0) {
				getName = "Marvel's Agent Carter";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			} 
			else if(nameStatus == 1) {
				getName = "Marvel's Agents of S.H.I.E.L.D.";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
		} else if(tvStatus == 14) {
			if(nameStatus == 0) {
				getName = "NCIS: Los Angeles";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			} 
			else if(nameStatus == 1) {
				getName = "New Girl";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
		} else if(tvStatus == 15) {
			if(nameStatus == 0) {
				getName = "Orange Is the New Black";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			} 
			else if(nameStatus == 1) {
				getName = "Originals";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
			else if(nameStatus == 2) {
				getName = "Outlander";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
		} else if(tvStatus == 16) {
			if(nameStatus == 0) {
				getName = "Penny Dreadful";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				} else if(seasonStatus == 1) {
					title = getName + " - Season 2";
					name = getName + " - S2";
				}
			} 
			else if(nameStatus == 1) {
				getName = "Prison Break";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
		} else if(tvStatus == 17) {
			if(nameStatus == 0) {
				getName = "Q Transformers";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			} 
		} else if(tvStatus == 18) {
			if(nameStatus == 0) {
				getName = "Ray Donovan";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				} else if(seasonStatus == 1) {
					title = getName + " - Season 2";
					name = getName + " - S2";
				}
			} 
			else if(nameStatus == 1) {
				getName = "Revenge";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
			else if(nameStatus == 2) {
				getName = "Revolution";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
		} else if(tvStatus == 19) {
			if(nameStatus == 0) {
				getName = "Sherlock";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				} else if(seasonStatus == 1) {
					title = getName + " - Season 2";
					name = getName + " - S2";
				}
				else if(seasonStatus == 2) {
					title = getName + " - Season 3";
					name = getName + " - S3";
				}
			} 
			else if(nameStatus == 1) {
				getName = "Sleepy Hollow";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
			else if(nameStatus == 2) {
				getName = "Stalker";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
			else if(nameStatus == 3) {
				getName = "Star Wars Rebels";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
			else if(nameStatus == 4) {
				getName = "Star Wars: The Clone Wars";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
			else if(nameStatus == 5) {
				getName = "Supernatural";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
		} else if(tvStatus == 20) {
			if(nameStatus == 0) {
				getName = "Teen Wolf";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				} else if(seasonStatus == 1) {
					title = getName + " - Season 2";
					name = getName + " - S2";
				}
			} 
			else if(nameStatus == 1) {
				getName = "The 100";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
			else if(nameStatus == 2) {
				getName = "The Big Bang Theory";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
			else if(nameStatus == 3) {
				getName = "The Blacklist";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
			else if(nameStatus == 4) {
				getName = "The Flash";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
			else if(nameStatus == 5) {
				getName = "The Following";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				} else if(seasonStatus == 1) {
					title = getName + " - Season 2";
					name = getName + " - S2";
				} else if(seasonStatus == 2) {
					title = getName + " - Season 3";
					name = getName + " - S3";
				}
			}
			else if(nameStatus == 6) {
				getName = "The Strain";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
			else if(nameStatus == 7) {
				getName = "The Walking Dead";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				} else if(seasonStatus == 1) {
					title = getName + " - Season 2";
					name = getName + " - S2";
				}
			}
			else if(nameStatus == 8) {
				getName = "Two and a Half Men";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
		} else if(tvStatus == 21) {
			if(nameStatus == 0) {
				getName = "Under the Dome";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			} 
		} else if(tvStatus == 22) {
			if(nameStatus == 0) {
				getName = "Vikings";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			} 
		} else if(tvStatus == 23) {
			if(nameStatus == 0) {
				getName = "White Collar";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			} 
		} else if(tvStatus == 24) {
			if(nameStatus == 0) {
				getName = "X-Men";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			} 
		} else if(tvStatus == 25) {
			if(nameStatus == 0) {
				getName = "You've Been Framed";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			} 
		} else if(tvStatus == 26) {
			if(nameStatus == 0) {
				getName = "Z Nation";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			} 
		}
	}

	/*
	 * This method is responsible for loading the correct TV show
	 * episodes based on the name and season selection from the user but
	 * within the View All category as it follows a different conditions compared
	 * to loadData() method.
	 */
	public void loadViewAll() {
		if(tvStatus == 0) { //If tvStatus equals element 0 which is "View all" category then,
			if(nameStatus == 0) { //If nameStatus equals element 0 which is "Anger Management" show then,
				getName = "Anger Management"; //set getName to the show name.
				if(seasonStatus == 0) { //If seasonStatus equals element 0 then set the required variables.
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			} 
			else if(nameStatus == 1) { //else if nameStatus equals element 1 which is "Arrow" category then,
				getName = "Arrow"; //set getName to the show name.
				if(seasonStatus == 0) { //If seasonStatus equals element 0 then set the required variables.
					title = getName + " - Season 1";
					name = getName + " - S1";
				} else if(seasonStatus == 1) { //else if seasonStatus equals element 1 then set the required variables.
					title = getName + " - Season 2";
					name = getName + " - S2";
				}
			} 
			else if(nameStatus == 2) {
				getName = "Atlantis";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				} else if(seasonStatus == 1) {
					title = getName + " - Season 2";
					name = getName + " - S2";
				}
			}
			else if(nameStatus == 3) {
				getName = "Banished";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			} 
			else if(nameStatus == 4) {
				getName = "Better Call Saul";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			} 
			else if(nameStatus == 5) {
				getName = "Bitten";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			} 
			else if(nameStatus == 6) {
				getName = "Black Sails";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				} else if(seasonStatus == 1) {
					title = getName + " - Season 2";
					name = getName + " - S2";
				}
			}
			else if(nameStatus == 7) {
				getName = "Charmed";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			} 
			else if(nameStatus == 8) {
				getName = "Constantine";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			} 
			else if(nameStatus == 9) {
				getName = "Continuum";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				} else if(seasonStatus == 1) {
					title = getName + " - Season 2";
					name = getName + " - S2";
				} else if(seasonStatus == 2) {
					title = getName + " - Season 3";
					name = getName + " - S3";
				}
			}
			else if(nameStatus == 10) {
				getName = "Defiance";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			} 
			else if(nameStatus == 11) {
				getName = "Doctor Who";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			} 
			else if(nameStatus == 12) {
				getName = "Downton Abbey";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				} else if(seasonStatus == 1) {
					title = getName + " - Season 2";
					name = getName + " - S2";
				}
			}
			else if(nameStatus == 13) {
				getName = "Extant";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			} 
			else if(nameStatus == 14) {
				getName = "Falling Skies";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				} else if(seasonStatus == 1) {
					title = getName + " - Season 2";
					name = getName + " - S2";
				} else if(seasonStatus == 2) {
					title = getName + " - Season 3";
					name = getName + " - S3";
				}
			}
			else if(nameStatus == 15) {
				getName = "Fear the Walking Dead";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
			else if(nameStatus == 16) {
				getName = "Forever";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
			else if(nameStatus == 17) {
				getName = "Game of Thrones";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				} else if(seasonStatus == 1) {
					title = getName + " - Season 2";
					name = getName + " - S2";
				} else if(seasonStatus == 2) {
					title = getName + " - Season 3";
					name = getName + " - S3";
				} else if(seasonStatus == 3) {
					title = getName + " - Season 4";
					name = getName + " - S4";
				} else if(seasonStatus == 4) {
					title = getName + " - Season 5";
					name = getName + " - S5";
				}
			}
			else if(nameStatus == 18) {
				getName = "Gotham";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
			else if(nameStatus == 19) {
				getName = "Grimm";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
			else if(nameStatus == 20) {
				getName = "Hannibal";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				} else if(seasonStatus == 1) {
					title = getName + " - Season 2";
					name = getName + " - S2";
				}
			}
			else if(nameStatus == 21) {
				getName = "Helix";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
			else if(nameStatus == 22) {
				getName = "Homeland";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
			else if(nameStatus == 23) {
				getName = "IZombie";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
			else if(nameStatus == 24) {
				getName = "Justified";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
			else if(nameStatus == 25) {
				getName = "Krypto: the Superdog";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
			else if(nameStatus == 26) {
				getName = "Lego Star Wars: The Yoda Chronicles";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			} 
			else if(nameStatus == 27) {
				getName = "Luther";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
			else if(nameStatus == 28) {
				getName = "Marvel's Agent Carter";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			} 
			else if(nameStatus == 29) {
				getName = "Marvel's Agents of S.H.I.E.L.D.";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
			else if(nameStatus == 30) {
				getName = "NCIS: Los Angeles";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			} 
			else if(nameStatus == 31) {
				getName = "New Girl";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
			else if(nameStatus == 32) {
				getName = "Orange Is the New Black";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			} 
			else if(nameStatus == 33) {
				getName = "Originals";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
			else if(nameStatus == 34) {
				getName = "Outlander";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
			else if(nameStatus == 35) {
				getName = "Penny Dreadful";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				} else if(seasonStatus == 1) {
					title = getName + " - Season 2";
					name = getName + " - S2";
				}
			} 
			else if(nameStatus == 36) {
				getName = "Prison Break";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
			else if(nameStatus == 37) {
				getName = "Q Transformers";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			} 
			else if(nameStatus == 38) {
				getName = "Ray Donovan";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				} else if(seasonStatus == 1) {
					title = getName + " - Season 2";
					name = getName + " - S2";
				}
			} 
			else if(nameStatus == 39) {
				getName = "Revenge";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
			else if(nameStatus == 40) {
				getName = "Revolution";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
			else if(nameStatus == 41) {
				getName = "Sherlock";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				} else if(seasonStatus == 1) {
					title = getName + " - Season 2";
					name = getName + " - S2";
				}
				else if(seasonStatus == 2) {
					title = getName + " - Season 3";
					name = getName + " - S3";
				}
			} 
			else if(nameStatus == 42) {
				getName = "Sleepy Hollow";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
			else if(nameStatus == 43) {
				getName = "Stalker";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
			else if(nameStatus == 44) {
				getName = "Star Wars Rebels";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
			else if(nameStatus == 45) {
				getName = "Star Wars: The Clone Wars";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
			else if(nameStatus == 46) {
				getName = "Supernatural";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
			else if(nameStatus == 47) {
				getName = "Teen Wolf";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				} else if(seasonStatus == 1) {
					title = getName + " - Season 2";
					name = getName + " - S2";
				}
			} 
			else if(nameStatus == 48) {
				getName = "The 100";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
			else if(nameStatus == 49) {
				getName = "The Big Bang Theory";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
			else if(nameStatus == 50) {
				getName = "The Blacklist";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
			else if(nameStatus == 51) {
				getName = "The Flash";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
			else if(nameStatus == 52) {
				getName = "The Following";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				} else if(seasonStatus == 1) {
					title = getName + " - Season 2";
					name = getName + " - S2";
				} else if(seasonStatus == 2) {
					title = getName + " - Season 3";
					name = getName + " - S3";
				}
			}
			else if(nameStatus == 53) {
				getName = "The Strain";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
			else if(nameStatus == 54) {
				getName = "The Walking Dead";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				} else if(seasonStatus == 1) {
					title = getName + " - Season 2";
					name = getName + " - S2";
				}
			}
			else if(nameStatus == 55) {
				getName = "Two and a Half Men";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			}
			else if(nameStatus == 56) {
				getName = "Under the Dome";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			} 
			else if(nameStatus == 57) {
				getName = "Vikings";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			} 
			else if(nameStatus == 58) {
				getName = "White Collar";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			} 
			else if(nameStatus == 59) {
				getName = "X-Men";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			} 
			else if(nameStatus == 60) {
				getName = "You've Been Framed";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			} 
			else if(nameStatus == 61) {
				getName = "Z Nation";
				if(seasonStatus == 0) {
					title = getName + " - Season 1";
					name = getName + " - S1";
				}
			} 
		}
	}

	/*
	 * This method is responsible for modifying the back button  
	 * to re-launch the season activity as the new intent.
	 */
	public void onBackPressed() {
		super.onBackPressed(); 
		Intent i = new Intent("view_seasons");
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
		startActivity(i);
	}
}
