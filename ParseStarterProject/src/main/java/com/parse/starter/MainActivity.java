/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

  /** Icons made by "https://www.flaticon.com/authors/pixel-perfect"
   *
   * PREREQUISITE: START UP PARSE SERVER - INSTRUCTION ARE FOUND HERE: GDRIVE > AppDev > Udemy > Section 8 - Instagram Clone! > Setting Up Parse On AWS
   * **/

  EditText username;
  EditText password;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    if (ParseUser.getCurrentUser().getUsername() != null) { //IF USER WAS PREVIOUSLY LOGGED IN...
      navigateToLoginMode();
    }
    else { //IF NO USERS WERE LOGGED IN...
      username = (EditText) findViewById(R.id.username);
      password = (EditText) findViewById(R.id.password);

      /** TO LET KEYBOARD DISAPPEAR WHEN CLICKED OUTSIDE **/
      ImageView logoImageView = (ImageView) findViewById(R.id.logo);
      RelativeLayout backgroundLayout = (RelativeLayout) findViewById(R.id.backgroundLayout);
      logoImageView.setOnClickListener(this); //read onClick(View view)
      backgroundLayout.setOnClickListener(this); //read onClick(View view)

      /** WHEN USER HITS ENTER AFTER FILLING PASSWORD... **/
      password.setOnKeyListener(new View.OnKeyListener() {
        @Override
        public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
          if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            loginOrSignup(view);
            closeKeyboard(); //close keyboard after logging in
          }
          return false;
        }
      });
    }

    /** NEEDED FOR THE PARSE SERVER **/
    ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }

  /** TO LET KEYBOARD DISAPPEAR WHEN CLICKED OUTSIDE **/
  @Override
  public void onClick(View view) {
    if (view.getId() == R.id.logo || view.getId() == R.id.backgroundLayout) {
      closeKeyboard();
    }
  }

  /** LOGIN/SIGNUP **/
  public void loginOrSignup(View view) {
    /** LOGIN **/
    ParseUser.logInInBackground(username.getText().toString(), password.getText().toString(), new LogInCallback() {
      @Override
      public void done(ParseUser user, ParseException e) {
        if (user != null) { /** IF USER EXISTS, LOG THEM IN. IF NOT, SIGN THEM UP!!!! **/
          Toast.makeText(getApplicationContext(), "Logged in", Toast.LENGTH_SHORT).show();
          navigateToLoginMode();
        }
        else {
          /** SIGNUP **/
          ParseUser newUser = new ParseUser();

          newUser.setUsername(username.getText().toString());
          newUser.setPassword(password.getText().toString());

          newUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
              if (e == null) {
                Toast.makeText(getApplicationContext(), "Signed up", Toast.LENGTH_SHORT).show();
              }
              else {
                /** VALIDATION CHECK - retrieves message after the space **/
                Toast.makeText(getApplicationContext(), e.getMessage().substring(e.getMessage().indexOf(" ")), Toast.LENGTH_SHORT).show();
              }
            }
          });
        }
      }
    });
  }

  /** NAVIGATE TO LOGGED IN MODE **/
  public void navigateToLoginMode() {
    Intent intent = new Intent(MainActivity.this, FeedActivity.class);
    startActivity(intent);
  }

  /** TO LET KEYBOARD DISAPPEAR WHEN CLICKED OUTSIDE **/
  public void closeKeyboard() {
    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
  }


}