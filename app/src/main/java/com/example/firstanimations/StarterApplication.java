/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.example.firstanimations;

import android.app.Application;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.List;


public class StarterApplication extends Application {
    private static final String TAG = "StarterApplication";

  @Override
  public void onCreate() {
    super.onCreate();
      //###########################################################################################################
      // Enable Local Datastore.
      Parse.enableLocalDatastore(this);

      // Add your initialization code here - get this from Putty, OR connection to Parse Server
      Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
              .applicationId("73791b7613df70ce21d8f9bc84107f7592d89784")//appId
              .clientKey("0e1dd2ee7c81c817931a164c0927ae85e53bcb28")//masterKey
              .server("http://3.17.4.96:80/parse/")//serverURL - add a slash at the end .../parse/
              .build()
      );

//        //Create a Class Object/Table/Schema - e.g: Score.============================
//        ParseObject score = new ParseObject("Score");
//        score.put("username", "mike");//String
//        score.put("score", 123);//Number
//
//        //score.save();//WILL PAUSE YOUR APP, RUN ON MAIN THREAD
//
//        score.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException ex) { //ONCE done
//                if (ex == null) {
//                    Log.i("Parse Result", "Successful!");
//                } else {
//                    Log.i("Parse Result", "Failed" + ex.toString());
//                }
//            }
//        });
//        //============================================================================

      //Query=========================================================================

      ParseQuery<ParseObject> query = ParseQuery.getQuery("Score"); //Gets the TABLE/object class

      //1) FIND by objectId
//        query.getInBackground("HGYeMAAHJt", new GetCallback<ParseObject>() {
//            @Override
//            public void done(ParseObject object, ParseException e) {
//                //if e == null -> no errors
//                if(e==null && object!=null){
//                    String username = object.getString("username");
//                    Log.d(TAG, "query: "+ username);
//                    int score = object.getInt("score");//123
//                    Log.d(TAG, "score: "+ username);
//
//                    //UPDATE
//                    object.put("score", 555);
//                    object.saveInBackground();//This will update the object in the server
//                    int updatedScore = object.getInt("score");//555
//
//                    Log.d(TAG, "updated score: "+ username);
//
//
//                }
//            }
//        });

      //FIND by WHERE, INCLUDE THIS===========================================================
      query.whereEqualTo("username", "mike"); //where
      query.setLimit(1); //limit
      query.whereGreaterThan("score", 50);//greater than

      //FIND ALL
      query.findInBackground(new FindCallback<ParseObject>() {
          @Override
          public void done(List<ParseObject> allObjects, ParseException e) {
              //if e == null -> no errors
              if(e==null){
                  if(allObjects.size() > 0){
                      for(ParseObject oneObject: allObjects){
                          Log.d("findInBackground", "username: "+ oneObject.getString("username"));
                          Log.d("findInBackground", "score: "+ oneObject.getInt("score"));
                      }
                  }

              }
          }
      });



      //============================================================================

      //ParseUser.enableAutomaticUser();//Creates an automatic User, so we keep track of the session

      //CUSTOM USER CREATION/sign up
      ParseUser user = new ParseUser();
      user.setUsername("mike");
      user.setPassword("admin");
      user.signUpInBackground(new SignUpCallback() {
          @Override
          public void done(ParseException e) {
              if(e==null){
                  Log.d(TAG, "Sign up OK!");
              } else {
                  e.printStackTrace();
              }
          }
      });

      //CUSTOM login
      ParseUser.logInInBackground("mike", "admin", new LogInCallback() {
          @Override
          public void done(ParseUser user, ParseException e) {
              if(e==null && user != null){ //user exists
                  Log.d(TAG, "User Logged in");
              } else {
                  //Password or user is wrong/does not exist
                  e.printStackTrace();
              }
          }
      });

      //CUSTOM logout
      //ParseUser.getCurrentUser()//GETS logged in user INFO
      if(ParseUser.getCurrentUser() != null){
          Log.d(TAG, "Signed in user info: " + ParseUser.getCurrentUser().getUsername());
          //LOG user out
          ParseUser.logOut();
      } else {
          Log.d(TAG, "No user is Logged in");
      }

      //GET USERs list
//          ParseQuery<ParseUser> query = ParseQuery.getQuery("User"); //Gets the TABLE/object class
//
//          query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());//where current user is not in list
//          query.addAscendingOrder("username");
//
//          query.findInBackground(new FindCallback<ParseUser>() {
//              @Override
//              public void done(List<ParseUser> allUsers, ParseException e) {
//                  //if e == null -> no errors
//                  if(e==null){
//                      if(allUsers.size() > 0){
//                          for(ParseUser oneUser: allUsers){
//                              //usernames.add(oneUser.getUsername());
//                          }
//                      }
//
//                  }
//              }
//          });


      //======================================
    ParseUser.enableAutomaticUser();

    ParseACL defaultACL = new ParseACL();
    defaultACL.setPublicReadAccess(true);
    defaultACL.setPublicWriteAccess(true);
    ParseACL.setDefaultACL(defaultACL, true);
      //###########################################################################################################
      //Tracks the Activity OR Application usage
      //ParseAnalytics.trackAppOpenedInBackground(getIntent());
      //####################################################################################################

  }
}
