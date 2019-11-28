package com.example.firstanimations;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class WhatsappCloneInsideChat extends AppCompatActivity {

    String activeUser = "";

    ArrayList<String> messages = new ArrayList<>();
    ArrayAdapter arrayAdapter;

    public void sendChat(View view) {

        final EditText chatEditText = (EditText) findViewById(R.id.whatsappChatText);

        ParseObject message = new ParseObject("Message");

        final String messageContent = chatEditText.getText().toString();

        message.put("sender", ParseUser.getCurrentUser().getUsername());
        message.put("recipient", activeUser);
        message.put("message", messageContent);

        chatEditText.setText("");

        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    messages.add(messageContent);
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whatsapp_clone_inside_chat);


        Intent intent = getIntent();
        activeUser = intent.getStringExtra("usernameSelected");
        setTitle("Chat with " + activeUser);

        ListView chatListView = (ListView) findViewById(R.id.whatsappChatListView);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, messages);
        chatListView.setAdapter(arrayAdapter);
//===========================================================================================================
        //GET WHERE LoggedIN User is sender ,(sent messages)
        ParseQuery<ParseObject> query1 = new ParseQuery<ParseObject>("Message");
        query1.whereEqualTo("sender", ParseUser.getCurrentUser().getUsername());
        query1.whereEqualTo("recipient", activeUser);

        //GET WHERE LoggedIN User is recipent, (received messages)
        ParseQuery<ParseObject> query2 = new ParseQuery<ParseObject>("Message");
        query2.whereEqualTo("recipient", ParseUser.getCurrentUser().getUsername());
        query2.whereEqualTo("sender", activeUser);

        //List of both queries
        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
        queries.add(query1);
        queries.add(query2);

        //YOU CAN EXECUTE BOTH QUERIES - WHERE "or"
        ParseQuery<ParseObject> query = ParseQuery.or(queries);

        query.orderByAscending("createdAt");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null && objects.size() > 0) {
                    messages.clear();//Clear messages, so you can populate the list with new messages
                    //LOOP through all messages (sent AND received).
                    for (ParseObject message : objects) {
                        String messageContent = message.getString("message");
                        //if it is a Received Message add a >
                        if (!message.getString("sender").equals(ParseUser.getCurrentUser().getUsername())) {
                            messageContent = "> " + messageContent;
                        }
                        Log.i("Info", messageContent);
                        messages.add(messageContent);
                    }
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        });

    }
}
