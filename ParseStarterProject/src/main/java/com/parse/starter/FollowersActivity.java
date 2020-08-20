package com.parse.starter;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
    ArrayAdapter adapter;
    ArrayList<String> users;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);

        setTitle("Followers");

        users = new ArrayList<String>();
        listView = (ListView) findViewById(R.id.list);

        /** enable listView's own checkboxes */
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_checked, users); //listView with checkboxes!

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //To use colours to indicate if isFollowing or not
                CheckedTextView checkedTextView = (CheckedTextView) view;

                if (checkedTextView.isChecked()) {
                    Toast.makeText(getApplicationContext(), "You are now following " + users.get(i), Toast.LENGTH_SHORT).show();
                    ParseUser.getCurrentUser().add("isFollowing", users.get(i)); //add user to the list of followers
                }
                else {
                    Toast.makeText(getApplicationContext(), "You unfollowed " + users.get(i), Toast.LENGTH_SHORT).show();
                    ParseUser.getCurrentUser().getList("isFollowing").remove(users.get(i)); //remove user
                    List newList = ParseUser.getCurrentUser().getList("isFollowing"); //creates a updated list of users
                    ParseUser.getCurrentUser().remove("isFollowing");
                    ParseUser.getCurrentUser().put("isFollowing", newList);
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
                        users.add(username);
                    }
                    adapter.notifyDataSetChanged();

                    /** to reload isFollowing data onCreate **/
                    /** how to make changes to a specific item in a listView!!! **/
                    for (String username: users) {
                        if (ParseUser.getCurrentUser().getList("isFollowing").contains(username)) {
                            listView.setItemChecked(users.indexOf(username), true); //in the listview, the specific item (found using index) gets ticked
                        }
                    }
                }
                else {
                    e.printStackTrace();
                }
            }
        });

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

    }

    /** updating the followers checkboxes and hitting back press on the phone's own tab didn't refresh the FeedActivity's listView, so had to write the following **/
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(FollowersActivity.this, FeedActivity.class);
        startActivity(intent);

        super.onBackPressed();
    }
}