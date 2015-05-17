package com.project.tv_manager.list_screens;

/*
 * This class is responsible for displaying the seasons based on 
 * the TV show name the user has selected from the previous screen.
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

public class SeasonList extends Activity {
	private String name;
	private int s1, s2, s3, s4, s5; //Season numbers

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

		loadData(); //Loads the required seasons based on selecting a letter from the start.
		loadViewAll(); //Loads the required seasons based on selecting view all category.
		//This sets the activity title using the custom title bar.
		txt = (TextView) findViewById(R.id.txt_title);
		txt.setText(name.substring(1));

		loadListView(); //Loads list view.
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
		Cursor c = getData(); //Loads the data.

		//Defines the required table fields.
		String[] seasonFields = new String[] {COLUMN_ID, SEASON};
		//Links up the text views to the specific table fields to display the required content.
		int[] views = new int[] {0, R.id.view_large};

		//Maps table columns from the cursor to the TextViews.
		customAdapter = new SimpleCursorAdapter(this, R.layout.season_layout, c, seasonFields, views, 0);
		lView = (ListView) findViewById(R.id.listView);
		lView.setAdapter(customAdapter);

		//Sets the nameStatus to the position the user has chosen.
		lView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
				seasonStatus = position;
				//Loads the episode activity.
				Intent i = new Intent("view_ep");
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
		//Retrieves the required seasons related to the TV show name.
		Cursor c = tvShow_DB.rawQuery("SELECT _id, season FROM tv_list WHERE _id = "+s1+" OR _id = "+s2+" OR _id = "+s3+ " OR _id = "+s4+
				" OR _id = "+s5+ " AND related_to" + " LIKE " + name + "%'", null);
		return c;
	}

	/*
	 * This method will load the required season information 
	 * based on the TV show name the user has chosen to view.
	 */
	public void loadData() {
		//If tvStatus equals element 1 from previous screen which would have been based on the letter "A" category then,
		if(tvStatus == 1) {
			if(nameStatus == 0) { //If the user has selected element 0 in the list view then load the required season for
				//Anger Management show.
				s1 = 48; //The number 48 is the position where the season 1 data starts in the table.
				name = "'Anger Management";
			} 
			else if(nameStatus == 1) { //else if the user has selected element 1 then load the required season for Arrow.
				s1 = 1; s2 = 25; //The number 1 and 25 are the row positions within the table where their data is stored.
				name = "'Arrow";
			}
			else if(nameStatus == 2) {
				s1 = 58; s2 = 71;
				name = "'Atlantis";
			}
		} else if(tvStatus == 2) {
			if(nameStatus == 0) {
				s1 = 125; 
				name = "'Banished";
			}
			else if(nameStatus == 1) {
				s1 = 132; 
				name = "'Better Call Saul";
			}
			else if(nameStatus == 2) {
				s1 = 142; 
				name = "'Bitten";
			}
			else if(nameStatus == 3) {
				s1 = 107; s2 = 115;
				name = "'Black Sails";
			}
		} else if(tvStatus == 3) {
			if(nameStatus == 0) {
				s1 = 155; 
				name = "'Charmed";
			}
			else if(nameStatus == 1) {
				s1 = 177; 
				name = "'Constantine";
			}
			else if(nameStatus == 2) {
				s1 = 190; s2 = 200; s3 = 213;
				name = "'Continuum";
			}
		} else if(tvStatus == 4) {
			if(nameStatus == 0) {
				s1 = 226; 
				name = "'Defiance";
			}
			else if(nameStatus == 1) {
				s1 = 238; 
				name = "'Doctor Who";
			}
			else if(nameStatus == 2) {
				s1 = 251; s2 = 258;
				name = "'Downton Abbey";
			}
		} else if(tvStatus == 5) {
			if(nameStatus == 0) {
				s1 = 266; 
				name = "'Extant";
			}
		} else if(tvStatus == 6) {
			if(nameStatus == 0) {
				s1 = 279; s2 = 289; s3 = 299;
				name = "'Falling Skies";
			} 
			else if(nameStatus == 1) {
				s1 = 309; 
				name = "'Fear the Walking Dead";
			}
			else if(nameStatus == 2) {
				s1 = 315; 
				name = "'Forever";
			}
		}  else if(tvStatus == 7) {
			if(nameStatus == 0) {
				s1 = 337; s2 = 347; s3 = 357; s4 = 367; s5 = 377;
				name = "'Game of Thrones";
			} 
			else if(nameStatus == 1) {
				s1 = 388; 
				name = "'Gotham";
			}
			else if(nameStatus == 2) {
				s1 = 409; 
				name = "'Grimm";
			}
		}  else if(tvStatus == 8) {
			if(nameStatus == 0) {
				s1 = 431; s2 = 444;
				name = "'Hannibal";
			}
			else if(nameStatus == 1) {
				s1 = 457; 
				name = "'Helix";
			}
			else if(nameStatus == 2) {
				s1 = 470; 
				name = "'Homeland";
			}
		} else if(tvStatus == 9) {
			if(nameStatus == 0) {
				s1 = 482;
				name = "'IZombie";
			}
		} else if(tvStatus == 10) {
			if(nameStatus == 0) {
				s1 = 493;
				name = "'Justified";
			}
		} else if(tvStatus == 11) {
			if(nameStatus == 0) {
				s1 = 519;
				name = "'Krypto: the Superdog";
			}
		} else if(tvStatus == 12) {
			if(nameStatus == 0) {
				s1 = 545;
				name = "'Lego Star Wars: The Yoda Chronicles";
			}
			else if(nameStatus == 1) {
				s1 = 552; 
				name = "'Luther";
			}
		} else if(tvStatus == 13) {
			if(nameStatus == 0) {
				s1 = 558;
				name = "'Marvel's Agent Carter";
			}
			else if(nameStatus == 1) {
				s1 = 566; 
				name = "'Marvel's Agents of S.H.I.E.L.D.";
			}
		} else if(tvStatus == 14) {
			if(nameStatus == 0) {
				s1 = 588;
				name = "'NCIS: Los Angeles";
			}
			else if(nameStatus == 1) {
				s1 = 598; 
				name = "'New Girl";
			}
		} else if(tvStatus == 15) {
			if(nameStatus == 0) {
				s1 = 622;
				name = "'Orange Is the New Black";
			}
			else if(nameStatus == 1) {
				s1 = 635; 
				name = "'Originals";
			}
			else if(nameStatus == 2) {
				s1 = 657; 
				name = "'Outlander";
			}
		} else if(tvStatus == 16) {
			if(nameStatus == 0) {
				s1 = 670; s2 = 678;
				name = "'Penny Dreadful";
			}
			else if(nameStatus == 1) {
				s1 = 687; 
				name = "'Prison Break";
			}
		} else if(tvStatus == 17) {
			if(nameStatus == 0) {
				s1 = 709;
				name = "'Q Transformers";
			}
		} else if(tvStatus == 18) {
			if(nameStatus == 0) {
				s1 = 722; s2 = 734;
				name = "'Ray Donovan";
			} 
			else if(nameStatus == 1) {
				s1 = 746; 
				name = "'Revenge";
			}
			else if(nameStatus == 2) {
				s1 = 768; 
				name = "'Revolution";
			}
		} else if(tvStatus == 19) {
			if(nameStatus == 0) {
				s1 = 788; s2 = 791; s3 = 794;
				name = "'Sherlock";
			} 
			else if(nameStatus == 1) {
				s1 = 797; 
				name = "'Sleepy Hollow";
			}
			else if(nameStatus == 2) {
				s1 = 810; 
				name = "'Stalker";
			}
			else if(nameStatus == 3) {
				s1 = 830; 
				name = "'Star Wars Rebels";
			}
			else if(nameStatus == 4) {
				s1 = 843; 
				name = "'Star Wars: The Clone Wars";
			}
			else if(nameStatus == 5) {
				s1 = 865; 
				name = "'Supernatural";
			}
		} else if(tvStatus == 20) {
			if(nameStatus == 0) {
				s1 = 887; s2 = 899;
				name = "'Teen Wolf";
			} 
			else if(nameStatus == 1) {
				s1 = 911; 
				name = "'The 100";
			}
			else if(nameStatus == 2) {
				s1 = 924; 
				name = "'The Big Bang Theory";
			}
			else if(nameStatus == 3) {
				s1 = 941; 
				name = "'The Blacklist";
			}
			else if(nameStatus == 4) {
				s1 = 84;
				name = "'The Flash";
			}
			else if(nameStatus == 5) {
				s1 = 963; s2 = 978; s3 = 993; 
				name = "'The Following";
			}
			else if(nameStatus == 6) {
				s1 = 1008; 
				name = "'The Strain";
			}
			else if(nameStatus == 7) {
				s1 = 1021; s2 = 1027;
				name = "'The Walking Dead";
			}
			else if(nameStatus == 8) {
				s1 = 1040; 
				name = "'Two and a Half Men";
			}
		} else if(tvStatus == 21) {
			if(nameStatus == 0) {
				s1 = 1064; 
				name = "'Under the Dome";
			}
		} else if(tvStatus == 22) {
			if(nameStatus == 0) {
				s1 = 1077; 
				name = "'Vikings";
			}
		} else if(tvStatus == 23) {
			if(nameStatus == 0) {
				s1 = 1086; 
				name = "'White Collar";
			}
		} else if(tvStatus == 24) {
			if(nameStatus == 0) {
				s1 = 1100; 
				name = "'X-Men";
			}
		} else if(tvStatus == 25) {
			if(nameStatus == 0) {
				s1 = 1113; 
				name = "'You've Been Framed";
			}
		} else if(tvStatus == 26) {
			if(nameStatus == 0) {
				s1 = 1121; 
				name = "'Z Nation";
			}
		}
	}

	/*
	 * This method is based on the users selection in the previous screen,
	 * if the user selected "View All" category then this method will run instead
	 * of the loadData() method as the tvStatus will equal to element 0 from the list view.
	 */
	public void loadViewAll() {
		if(tvStatus == 0) {
			if(nameStatus == 0) {
				s1 = 48;
				name = "'Anger Management";
			} 
			else if(nameStatus == 1) {
				s1 = 1; s2 = 25;
				name = "'Arrow";
			}
			else if(nameStatus == 2) {
				s1 = 58; s2 = 71;
				name = "'Atlantis";
			}
			else if(nameStatus == 3) {
				s1 = 125; 
				name = "'Banished";
			}
			else if(nameStatus == 4) {
				s1 = 132; 
				name = "'Better Call Saul";
			}
			else if(nameStatus == 5) {
				s1 = 142; 
				name = "'Bitten";
			}
			else if(nameStatus == 6) {
				s1 = 107; s2 = 115;
				name = "'Black Sails";
			}
			else if(nameStatus == 7) {
				s1 = 155; 
				name = "'Charmed";
			}
			else if(nameStatus == 8) {
				s1 = 177; 
				name = "'Constantine";
			}
			else if(nameStatus == 9) {
				s1 = 190; s2 = 200; s3 = 213;
				name = "'Continuum";
			}
			else if(nameStatus == 10) {
				s1 = 226; 
				name = "'Defiance";
			}
			else if(nameStatus == 11) {
				s1 = 238; 
				name = "'Doctor Who";
			}
			else if(nameStatus == 12) {
				s1 = 251; s2 = 258;
				name = "'Downton Abbey";
			}
			else if(nameStatus == 13) {
				s1 = 266; 
				name = "'Extant";
			}
			else if(nameStatus == 14) {
				s1 = 279; s2 = 289; s3 = 299;
				name = "'Falling Skies";
			} 
			else if(nameStatus == 15) {
				s1 = 309; 
				name = "'Fear the Walking Dead";
			}
			else if(nameStatus == 16) {
				s1 = 315; 
				name = "'Forever";
			}
			else if(nameStatus == 17) {
				s1 = 337; s2 = 347; s3 = 357; s4 = 367; s5 = 377;
				name = "'Game of Thrones";
			} 
			else if(nameStatus == 18) {
				s1 = 388; 
				name = "'Gotham";
			}
			else if(nameStatus == 19) {
				s1 = 409; 
				name = "'Grimm";
			}
			else if(nameStatus == 20) {
				s1 = 431; s2 = 444;
				name = "'Hannibal";
			}
			else if(nameStatus == 21) {
				s1 = 457; 
				name = "'Helix";
			}
			else if(nameStatus == 22) {
				s1 = 470; 
				name = "'Homeland";
			}
			else if(nameStatus == 23) {
				s1 = 482;
				name = "'IZombie";
			}
			else if(nameStatus == 24) {
				s1 = 493;
				name = "'Justified";
			}
			else if(nameStatus == 25) {
				s1 = 519;
				name = "'Krypto: the Superdog";
			}
			else if(nameStatus == 26) {
				s1 = 545;
				name = "'Lego Star Wars: The Yoda Chronicles";
			}
			else if(nameStatus == 27) {
				s1 = 552; 
				name = "'Luther";
			}
			else if(nameStatus == 28) {
				s1 = 558;
				name = "'Marvel's Agent Carter";
			}
			else if(nameStatus == 29) {
				s1 = 566; 
				name = "'Marvel's Agents of S.H.I.E.L.D.";
			}
			else if(nameStatus == 30) {
				s1 = 588;
				name = "'NCIS: Los Angeles";
			}
			else if(nameStatus == 31) {
				s1 = 598; 
				name = "'New Girl";
			}
			else if(nameStatus == 32) {
				s1 = 622;
				name = "'Orange Is the New Black";
			}
			else if(nameStatus == 33) {
				s1 = 635; 
				name = "'Originals";
			}
			else if(nameStatus == 34) {
				s1 = 657; 
				name = "'Outlander";
			}
			else if(nameStatus == 35) {
				s1 = 670; s2 = 678;
				name = "'Penny Dreadful";
			}
			else if(nameStatus == 36) {
				s1 = 687; 
				name = "'Prison Break";
			}
			else if(nameStatus == 37) {
				s1 = 709;
				name = "'Q Transformers";
			}
			else if(nameStatus == 38) {
				s1 = 722; s2 = 734;
				name = "'Ray Donovan";
			} 
			else if(nameStatus == 39) {
				s1 = 746; 
				name = "'Revenge";
			}
			else if(nameStatus == 40) {
				s1 = 768; 
				name = "'Revolution";
			}
			else if(nameStatus == 41) {
				s1 = 788; s2 = 791; s3 = 794;
				name = "'Sherlock";
			} 
			else if(nameStatus == 42) {
				s1 = 797; 
				name = "'Sleepy Hollow";
			}
			else if(nameStatus == 43) {
				s1 = 810; 
				name = "'Stalker";
			}
			else if(nameStatus == 44) {
				s1 = 830; 
				name = "'Star Wars Rebels";
			}
			else if(nameStatus == 45) {
				s1 = 843; 
				name = "'Star Wars: The Clone Wars";
			}
			else if(nameStatus == 46) {
				s1 = 865; 
				name = "'Supernatural";
			}
			else if(nameStatus == 47) {
				s1 = 887; s2 = 899;
				name = "'Teen Wolf";
			} 
			else if(nameStatus == 48) {
				s1 = 911; 
				name = "'The 100";
			}
			else if(nameStatus == 49) {
				s1 = 924; 
				name = "'The Big Bang Theory";
			}
			else if(nameStatus == 50) {
				s1 = 941; 
				name = "'The Blacklist";
			}
			else if(nameStatus == 51) {
				s1 = 84;
				name = "'The Flash";
			}
			else if(nameStatus == 52) {
				s1 = 963; s2 = 978; s3 = 993; 
				name = "'The Following";
			}
			else if(nameStatus == 53) {
				s1 = 1008; 
				name = "'The Strain";
			}
			else if(nameStatus == 54) {
				s1 = 1021; s2 = 1027;
				name = "'The Walking Dead";
			}
			else if(nameStatus == 55) {
				s1 = 1040; 
				name = "'Two and a Half Men";
			}
			else if(nameStatus == 56) {
				s1 = 1064; 
				name = "'Under the Dome";
			}
			else if(nameStatus == 57) {
				s1 = 1077; 
				name = "'Vikings";
			}
			else if(nameStatus == 58) {
				s1 = 1086; 
				name = "'White Collar";
			}
			else if(nameStatus == 59) {
				s1 = 1100; 
				name = "'X-Men";
			}
			else if(nameStatus == 60) {
				s1 = 1113; 
				name = "'You've Been Framed";
			}
			else if(nameStatus == 61) {
				s1 = 1121; 
				name = "'Z Nation";
			}
		}
	}
	
	/*
	 * This method is responsible for modifying the back button  
	 * to re-launch the list of shows as the new intent.
	 */
	public void onBackPressed() {
		super.onBackPressed(); 
		Intent i  = new Intent("list_of_shows");
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
		startActivity(i); 
		Toast.makeText(getApplicationContext(), "List of Shows", Toast.LENGTH_SHORT).show();
	}
}