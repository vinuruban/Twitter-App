package com.parse.starter;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class FollowersActivity extends AppCompatActivity {

    String currentUser;
    UserAdapter adapter;
    ArrayList<UserObject> users;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);

        setTitle("Followers");

        users = new ArrayList<UserObject>();
        adapter = new UserAdapter(FollowersActivity.this, users);
        listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //To use colours to indicate if isFollowing or not
                ColorDrawable viewColor = (ColorDrawable) view.getBackground();

                if (viewColor.getColor() == Color.WHITE) {
                    Toast.makeText(getApplicationContext(), "Checked " + users.get(i).getUsername(), Toast.LENGTH_SHORT).show();
                    ParseUser.getCurrentUser().add("isFollowing", users.get(i).getUsername()); //add user to the list of followers
                    view.setBackgroundColor(Color.CYAN);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Unchecked " + users.get(i).getUsername(), Toast.LENGTH_SHORT).show();
                    ParseUser.getCurrentUser().getList("isFollowing").remove(users.get(i).getUsername()); //remove user
                    List newList = ParseUser.getCurrentUser().getList("isFollowing"); //creates a updated list of users
                    ParseUser.getCurrentUser().remove("isFollowing");
                    ParseUser.getCurrentUser().put("isFollowing", newList);
                    view.setBackgroundColor(Color.WHITE);
                }
                ParseUser.getCurrentUser().saveInBackground(); //actually updates the list in Parse
            }
        });

        //Get current username
        currentUser = ParseUser.getCurrentUser().getUsername();

        ParseQuery<ParseUser> query = ParseUser.getQuery(); //INSTEAD OF <ParseObject> & ParseQuery.getQuery("ExampleObject"), ITS <ParseUser> & ParseUser.getQuery() respectively!!!!!!!

        query.whereNotEqualTo("username", currentUser); //DON'T INCLUDE CURRENT USER
        query.addAscendingOrder("username"); //SORT BY ORDER

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null && objects.size() > 0) {
                    for (ParseUser user : objects) {
                        String username = user.getUsername();
                        users.add(new UserObject(username));
                    }
                    adapter.notifyDataSetChanged();
                }
                else {
                    e.printStackTrace();
                }
            }
        });

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

    }
}