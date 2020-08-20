package com.parse.starter;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity {

    String currentUser;
    TweetAdapter adapter;
    ArrayList<TweetObject> tweets;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        //Get current username
        currentUser = ParseUser.getCurrentUser().getUsername();

        setTitle(currentUser + "'s Twitter Feed");

        tweets = new ArrayList<TweetObject>();
        adapter = new TweetAdapter(FeedActivity.this, tweets);
        listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);

        //Query 1 - VIEW TWEETS POSTED BY THE USERS THE CurrentUser FOLLOWERS!
        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Tweet");
        query1.whereContainedIn("username", ParseUser.getCurrentUser().getList("isFollowing"));

        //Query 2 - INCLUDE CURRENT USER'S TWEET TOO
        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Tweet");
        query2.whereEqualTo("username", currentUser);

        //COMBINE QUERIES 1 & 2
        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
        queries.add(query1);
        queries.add(query2);
        ParseQuery<ParseObject> query = ParseQuery.or(queries);

        //ORDERS BY TIME OF UPLOAD!
        query.orderByDescending("createdAt");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects.size() == 0) {
                    tweets.add(new TweetObject("Your followers didn't tweet anything", ""));
                    adapter.notifyDataSetChanged();
                }
                else if (e == null && objects.size() > 0) {
                    for (ParseObject object : objects) { //LOOPS THROUGH EACH TWEETS OF currentUser
                        String username = object.getString("username");
                        String tweet = object.getString("tweet");
                        tweets.add(new TweetObject(username, tweet));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_homepage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_tweet:

                /** AlertDialog - Pop up window **/
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Post a Tweet");
                final EditText editText = new EditText(this);
                builder.setView(editText);

                //Cancel button
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                //Post button
                builder.setPositiveButton("Post tweet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String tweet = editText.getText().toString();
                        addTweet(tweet);
                    }
                });

                builder.show();

                return true;
            case R.id.followers:
                Intent intent = new Intent(FeedActivity.this, FollowersActivity.class);
                startActivity(intent);
                return true;
            case R.id.log_out:
                ParseUser.logOut();
                Intent intent2 = new Intent(FeedActivity.this, MainActivity.class);
                startActivity(intent2);
                Toast.makeText(getApplicationContext(), "Logged out " + currentUser, Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addTweet(String tweet) {
        ParseObject object = new ParseObject("Tweet"); // PASS INTO PARSE
        object.put("username", currentUser); //PASS IN THE USER WHO IS TWEETING
        object.put("tweet", tweet); //PASS IN THEIR TWEET
        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(getApplicationContext(), "Tweet uploaded to Parse!", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Couldn't upload tweet to Parse - check and try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}