package com.mistershorr.databases;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

public class FriendDetailActivity extends AppCompatActivity {

    private EditText editTextName;
    private TextView textViewClumsiness;
    private TextView textViewGymFreq;
    private TextView textViewTrust;
    private SeekBar seekBarClumsiness;
    private Switch switchAwesome;
    private SeekBar seekBarGymFreq;
    private RatingBar ratingBarTrust;
    private EditText editTextMoneyOwed;
    private TextView textViewMoneyOwed;
    private Button buttonSave;
    private Friend friend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_detail);

        Intent lastIntent = getIntent();

        Friend friend = lastIntent.getParcelableExtra(DisplayFriendListActivity.EXTRA_FRIEND);

        wireWidgets();
        setListeners();

        if(friend != null){
            //set values
            editTextName.setText(friend.getName());
            editTextMoneyOwed.setText(String.valueOf(friend.getMoneyOwed()));
            ratingBarTrust.setProgress(friend.getTrustworthiness());
            switchAwesome.setChecked(friend.isAwesome());
            seekBarClumsiness.setProgress(friend.getClumsiness());
            seekBarGymFreq.setProgress((int) (friend.getGymFrequency()*2));
            switchAwesome.setText("Awesome");
            textViewClumsiness.setText("Clumsiness");
            textViewGymFreq.setText("Gym Frequency");
            textViewMoneyOwed.setText("Money Owed");
            textViewTrust.setText("Trust");



        }
        else{
            friend = new Friend();
            //set the ownerId here to the current user Id
        }






    }

    private void wireWidgets() {

        editTextName = findViewById(R.id.editText_friendDetail_name);
        seekBarClumsiness = findViewById(R.id.seekBar_friendDetail_clumsiness);
        seekBarGymFreq = findViewById(R.id.seekBar_friendDetail_gymFreq);
        textViewClumsiness = findViewById(R.id.textView_friendDetail_clumsiness);
        textViewGymFreq = findViewById(R.id.textView_friendDetail_gymFreq);
        textViewMoneyOwed = findViewById(R.id.textView_friendDetail_moneyOwed);
        textViewTrust = findViewById(R.id.textView_friendDetail_trust);
        ratingBarTrust = findViewById(R.id.ratingBar_friendDetail_trust);
        editTextMoneyOwed = findViewById(R.id.editText_friendDetail_moneyOwed);
        switchAwesome = findViewById(R.id.switch_friendDetail_isAwesome);
        buttonSave = findViewById(R.id.button_friendDetail_save);


    }

    private void setListeners() {

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

}
