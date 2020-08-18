package com.parse.starter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;

public class FeedActivity extends AppCompatActivity {

    String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        setTitle("Twitter Feed");

        //Get current username
        currentUser = ParseUser.getCurrentUser().getUsername();

        TextView userDetails = (TextView) findViewById(R.id.userTextView);
        userDetails.setText("Logged in as: " + currentUser);
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
                Toast.makeText(getApplicationContext(), "To do", Toast.LENGTH_SHORT).show();
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
}