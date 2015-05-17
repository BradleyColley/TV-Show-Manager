package com.project.tv_manager;

/*
 * These variables can be accessed by any class within the project to prevent calling
 * unnecessary code.
 */

import java.util.ArrayList;
import com.project.tv_manager.database.SQLOperations;
import com.project.tvshow_reminders.R;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class Global {

	public static SQLiteDatabase tvShow_DB = null;
	public static ListView lView, navView;
	public static Button btnReminder;
	public static SQLOperations readOperations;
	public static TextView txt;
	public static int  tvStatus = 0, nameStatus = 0, seasonStatus = 0;
	public static SimpleCursorAdapter customAdapter;
	public static DrawerLayout navLayout;
	public static String[] navList;
	public static ArrayAdapter<String> mAdapter;
	
	public static final String PATH = "/data/data/com.project.tvshow_reminders/databases/tvShow_data";
	public static final String DATABASE_NAME = "tvShow_data";
	
	public static final String TABLE_NAME_01 = "layout";
	public static final String COLUMN_ID = "_id";
	
	public static final String TABLE_NAME_02 = "tv_names";
	public static final String TV_NAME = "name_of_show";
	
	public static final String TABLE_NAME_03 = "tv_list";
	public static final String SEASON = "season";
	public static final String EP_NUMBER = "ep_num";
	public static final String EP_NAME = "ep_name";
	public static final String RELATED = "related_to";
	
	public static final String TABLE_NAME_04 = "my_shows";
	public static final String POSITION = "position";
	
	public static final String TABLE_NAME_05 = "update_reminder";
	public static final String TIME = "time_set";
	public static final String DATE = "date_set";
	
	public static ArrayList<String> duplicates;
	
	//Contains all of the TV show images.
	public static Integer[] img_all={
			R.drawable.show1, R.drawable.show2, R.drawable.show3, R.drawable.show4, R.drawable.show5, R.drawable.show6, R.drawable.show7, 
			R.drawable.show8, R.drawable.show9, R.drawable.show10, R.drawable.show11, R.drawable.show12, R.drawable.show13, R.drawable.show14,
			R.drawable.show15, R.drawable.show16, R.drawable.show17, R.drawable.show18, R.drawable.show19, R.drawable.show20, R.drawable.show21,
			R.drawable.show22, R.drawable.show23, R.drawable.show24, R.drawable.show25, R.drawable.show26, R.drawable.show27, R.drawable.show28,
			R.drawable.show29, R.drawable.show30, R.drawable.show31, R.drawable.show32, R.drawable.show33, R.drawable.show34, R.drawable.show35,	
			R.drawable.show36, R.drawable.show37, R.drawable.show38, R.drawable.show39, R.drawable.show40, R.drawable.show41, R.drawable.show42,
			R.drawable.show43, R.drawable.show44, R.drawable.show45, R.drawable.show46, R.drawable.show47, R.drawable.show48, R.drawable.show49,
			R.drawable.show50, R.drawable.show51, R.drawable.show52, R.drawable.show53, R.drawable.show54, R.drawable.show55, R.drawable.show56,
			R.drawable.show57, R.drawable.show58, R.drawable.show59, R.drawable.show60, R.drawable.show61, R.drawable.show62 };
	
	/*
	 * Creates the duplicates ArrayList.
	 */
	public Global() {
		duplicates = new ArrayList<String>(); 
		for(int i = 0; i <= 47;) {
			duplicates.add("");
			i++;
		}
	}
}