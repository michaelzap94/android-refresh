package com.example.firstanimations;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.stream.IntStream;

public class ConnectGame extends AppCompatActivity {
    private static final String TAG = "ConnectGame";

    //STATIC VARIABLES, WILL BE RESET/KILLED WHEN THE APP IS CLOSED( OR KILLED BY ANDROID IF TOO LONG IN BACKGROUND )
    public static int redWins = 0;
    public static int yellowWins = 0;

    public TextView winnerTextView;
    public Button playAgainButton;
    public TextView player0;
    public TextView player1;

    // 0: yellow, 1: red, 2: empty
    int activePlayer = 0;
    //Positions of the board that has been used and by who. 2: not used.
    int[] gameState = {2, 2, 2, 2, 2, 2, 2, 2, 2};
    int turnCounter = 0;

    //Since every child in the gridLayout has a tagId.
    // I will use this tagIds to keep track of the winning positions.
    int[][] winningPositions = {{0,1,2}, {3,4,5}, {6,7,8},
            {0,3,6}, {1,4,7}, {2,5,8},
            {2,5,8}, {0,4,8}, {2,4,6}};

    boolean gameActive = true;



    //Every child  element in the GridLayout will have a unique tag.
    public void dropIn(View view){
        ImageView counter = (ImageView) view; // NO NEED TO USE: findViewById(R.id.bartImageView); as we already know/found the View.
        int coinPositionTapped = Integer.parseInt(counter.getTag().toString());//gets the position of the tapped element View in the GridLayout

        // if the position "coinPositionTapped" has been tapped/used already don't do anything.
        if (gameState[coinPositionTapped] == 2 && gameActive) {

            turnCounter++;

            //Specify which player,1||0, used a space of the board,
            gameState[coinPositionTapped] = activePlayer;

            Log.d(TAG, String.format("dropIn: clicked. View tag is %s", coinPositionTapped));
            //Set the view OFF the screen
            counter.setTranslationY(-1500);// it will not move it. It will set the view position.
            if (activePlayer == 0) {
                //set the image in the GridLayout child view, to YELLOW.
                counter.setImageResource(R.drawable.yellow);
                //change turn to RED
                activePlayer = 1;
            } else {
                //set the image in the GridLayout child view, to RED.
                counter.setImageResource(R.drawable.red);
                //change turn to YELLOW
                activePlayer = 0;
            }
            //move the element TO/ON the screen, slowly.
            counter.animate().rotation(360).translationYBy(1500).setDuration(1000);

            String winner = checkWinner();
            //if there is a winner -> gameActive will be false
            //turnCounter == 9, means we have used all spaces.
            if (!gameActive || turnCounter == 9) {
                if (winner == "Yellow") {
                    yellowWins++;
                } else {
                    redWins++;
                }
                winnerTextView.setText("Winner is: " + winner + "!");
                player0.setText(String.format("Yellow Wins: %s", yellowWins));
                player1.setText(String.format("Red Wins: %s", redWins));

                winnerTextView.setVisibility(View.VISIBLE);
                playAgainButton.setVisibility(View.VISIBLE);

                Toast.makeText(this, winner + " has won!", Toast.LENGTH_SHORT).show();
            }

        }
    }

    /**
     * Checks someone is a winner.
     * @return
     */
    public String checkWinner(){
        for (int[] winningPosition : winningPositions) {
            if (gameState[winningPosition[0]] == gameState[winningPosition[1]] && gameState[winningPosition[1]] == gameState[winningPosition[2]] && gameState[winningPosition[0]] != 2) {
                // Somone has won!
                gameActive = false;
                return (activePlayer == 1) ? "Yellow": "Red";
            }
        }
        return "Nobody";
    }

    public void playAgainButtonConnectGame(View view){
        Log.d(TAG, "playAgainButtonConnectGame: clicked");

        playAgainButton.setVisibility(View.INVISIBLE);
        winnerTextView.setVisibility(View.INVISIBLE);

        GridLayout gridLayout = (GridLayout) findViewById(R.id.gridLayoutConnectGame);

        //loop through all elements in gridLayout and remove the image source.
        for(int i=0; i<gridLayout.getChildCount(); i++) {
            ImageView counter = (ImageView) gridLayout.getChildAt(i);
            counter.setImageDrawable(null);
        }

        //reset the gameState to all elements unused: 2
        for (int i=0; i<gameState.length; i++) {
            gameState[i] = 2;
        }

        //reset the active player to 0
        activePlayer = 0;
        //reset the game to active
        gameActive = true;
        //resets the turns
        turnCounter = 0;

    }

    public static void restartActivity(Activity activity){
        if (Build.VERSION.SDK_INT >= 11) {
            activity.recreate();
        } else {
            activity.finish();
            activity.startActivity(activity.getIntent());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_game);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        winnerTextView = (TextView) findViewById(R.id.winnerTextView);
        playAgainButton = (Button) findViewById(R.id.playAgainButtonConnectGame);

        player0 = (TextView) findViewById(R.id.player0);
        player1 = (TextView) findViewById(R.id.player1);

        player0.setText(String.format("Yellow Wins: %s", yellowWins));
        player1.setText(String.format("Red Wins: %s", redWins));
    }



    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "Inside of onRestoreInstanceState");
        //startTime = (Calendar) savedInstanceState.getSerializable("starttime");
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        Log.d(TAG, "Inside of onSaveInstanceState");

        //state.putSerializable("starttime", startTime);
    }

}
