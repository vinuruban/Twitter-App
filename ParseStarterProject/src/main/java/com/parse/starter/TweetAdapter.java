package com.parse.starter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class TweetAdapter extends ArrayAdapter<TweetObject> {

    TweetObject currentUser;

    public TweetAdapter(Activity context, ArrayList<TweetObject> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Here we are overriding a method from the ArrayAdapter class!
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        // Get the {@link AndroidFlavor} object located at this position in the list
        currentUser = getItem(position);

        TextView userTextView = (TextView) listItemView.findViewById(R.id.userTextView);
        TextView tweetTextView = (TextView) listItemView.findViewById(R.id.tweetTextView);

        if (FeedActivity.tweetStatus == 1) { //if there are no tweets...
            userTextView.setText(currentUser.getUsername());
            tweetTextView.setText(currentUser.getTweet());
        } else {
            userTextView.setText("- tweeted by " + currentUser.getUsername());
            tweetTextView.setText("\"" + currentUser.getTweet() + "\"");
        }

        // Return the whole list item layout (containing 2 TextViews and an ImageView)
        // so that it can be shown in the ListView
        return listItemView;
    }

}
