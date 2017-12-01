package com.example.jonathan.meetup;

import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.media.Rating;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // Class-level variables
    boolean playing = false;            // Is media player playing music?
    TextView txtTimeLeft, txtTitle;     // Two TextView controls
    Button btnStart;                    // Start timer button
    Button btnReset;                    // Reset button
    EditText edtMinutes;                // Minutes EditText
    MediaPlayer mpPackers;              // Media player to play sound when timer finishes
    ConstraintLayout mainLayout;        // ConstraintLayout for setting background color
    PackTimer pTimer = null;            // CountDownTimer for timing.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Boilerplate Android code
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up references to screen elements
        txtTimeLeft = findViewById(R.id.txtTimeLeft);
        txtTitle = findViewById(R.id.txtTitle);
        btnStart = findViewById(R.id.btnStart);
        btnReset = findViewById(R.id.btnReset);
        edtMinutes = findViewById(R.id.edtMinutes);
        mpPackers = MediaPlayer.create(getBaseContext(), R.raw.gopackgo);;
        mainLayout = findViewById(R.id.layoutMain);

        // Set up click listeners for buttona
        btnStart.setOnClickListener(btnClickListener);
        btnReset.setOnClickListener(btnClickListener);

        // Make sure screen colors are correct
        changeColors(false);
    }

    private View.OnClickListener btnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnStart:
                    // Start button pressed
                    start();
                    break;
                case R.id.btnReset:
                    // Reset button pressed
                    reset();
                    break;
            }
        }
    };

    private void start() {
        // Get number of minutes from edtMinutes
        int minutes;
        try {
            minutes = Integer.parseInt(edtMinutes.getText().toString());
        }
        catch(Exception e) {
            // User probably did not enter any minutes. Just set to 0 instead of crashing program.
            minutes = 0;
        }
        // ms is number of milliseconds to run the timer.
        // 60 seconds in a minute, 1000 milliseconds in a second
        int mS = minutes * 60000;
        // Green background when the timer is running
        changeColors(true);

        // Disable start button and minutes EditText while timer is running.
        btnStart.setEnabled(false);
        edtMinutes.setEnabled(false);
        // Timer
        pTimer = (PackTimer) new PackTimer(mS, 1000).start();      // Start countdown timer
    }

    private void reset() {
        // Turn off music if it is playing
        if (playing) {
            mpPackers.pause();
            playing = false;
        }
        // Change to a yellow background
        changeColors(false);
        // Set all controls back to their original state
        txtTimeLeft.setText("");
        btnStart.setEnabled(true);
        edtMinutes.setEnabled(true);
        // Cancel timer if it is running
        if (pTimer != null) {
            pTimer.cancel();
            pTimer = null;
        }
    }

    private void changeColors(boolean blnGreenBackground) {
        if (blnGreenBackground) {
            // Change to green background. Set other elements to gold
            mainLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.packersGreen));
            txtTimeLeft.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.packersGold));
            edtMinutes.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.packersGold));
            txtTitle.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.packersGold));
            edtMinutes.getBackground().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.packersGold), PorterDuff.Mode.SRC_IN);

        } else {
            // Change to gold background. Set other elements to green
            mainLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.packersGold));
            txtTimeLeft.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.packersGreen));
            edtMinutes.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.packersGreen));
            txtTitle.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.packersGreen));
            edtMinutes.getBackground().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.packersGreen), PorterDuff.Mode.SRC_IN);
        }
    }

    public class PackTimer extends CountDownTimer {

        public PackTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        /**
         * Android will call onTick() once per second. Update the minutes
         * and seconds display.
         * @param millisUntilFinished Number of milliseconds to go
         */
        public void onTick(long millisUntilFinished) {
            long min = millisUntilFinished / 60000;     // number of full minutes left.
            long sec = (millisUntilFinished - min * 60000)/1000; // number of seconds left.
            // Set up string for display
            String secString = ("" + sec);
            if (sec < 10)
            {
                // leading zero if seconds are less than 10
                secString = "0" + secString;
            }
            // Set the txtTimeLeft display
            txtTimeLeft.setText("" + min + ":" + secString);
        }

        /**
         * Android calls onFinish() when timer is done counting down.
         */
        public void onFinish() {
            // Output "Done!" to txtTimeLeft display
            txtTimeLeft.setText(getString(R.string.done));
            // Loop the song - it will play continuously
            mpPackers.setLooping(true);
            // Start the song if it is not already started
            if (!playing) {
                playing = true;
                mpPackers.start();
            }
            // Change background back to gold
            changeColors(false);
            // Show a toast message
            Toast.makeText(MainActivity.this, getString(R.string.timer_done), Toast.LENGTH_LONG ).show();

        }
    }

}