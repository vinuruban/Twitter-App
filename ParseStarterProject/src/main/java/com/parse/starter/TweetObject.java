package com.parse.starter;

public class TweetObject {

    private String username;
    private String tweet;

    public TweetObject(String username, String tweet) {
        this.username = username;
        this.tweet = tweet;
    }

    public String getUsername() {
        return username;
    }

    public String getTweet() {
        return tweet;
    }
}
