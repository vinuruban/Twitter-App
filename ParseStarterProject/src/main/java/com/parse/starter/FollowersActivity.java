package com.parse.starter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

        users = new ArrayList<UserObject>();
        adapter = new UserAdapter(FollowersActivity.this, users);
        listView = (ListView) findViewById(R.id.list);

        setTitle("Followers");

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
                    listView.setAdapter(adapter);
                }
                else {
                    e.printStackTrace();
                }
            }
        });

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

    }
}