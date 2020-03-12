package com.mistershorr.databases;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

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

        friend = lastIntent.getParcelableExtra(DisplayFriendListActivity.EXTRA_FRIEND);

        wireWidgets();

        if(friend != null){
            //set values
            editTextName.setText(friend.getName());
            seekBarClumsiness.setProgress(friend.getClumsiness());
            switchAwesome.setChecked(friend.isAwesome());
            seekBarGymFreq.setProgress( (int) (friend.getGymFrequency()) * 2);
            ratingBarTrust.setProgress(friend.getTrustworthiness());
            editTextMoneyOwed.setText(String.valueOf(friend.getMoneyOwed()));
            setListeners();
        }
        else{
            buttonSave.setText("Create");
            addNewFriend();
        }








    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu_friend_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.optionMenu_optionMenuFriendDetail_logOut:

                Backendless.UserService.logout( new AsyncCallback<Void>()
                {
                    public void handleResponse( Void response )
                    {
                        // user has been logged out.
                        Intent intentLogInActivity = new Intent(FriendDetailActivity.this, LoginActivity.class);
                        startActivity(intentLogInActivity);
                        finish();
                    }

                    public void handleFault( BackendlessFault fault )
                    {
                        // something went wrong and logout failed, to get the error code call fault.getCode()
                    }
                });

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addNewFriend() {

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Friend friend = new Friend();
                friend.setOwnerId(Backendless.UserService.CurrentUser().getObjectId());

                friend.setClumsiness(seekBarClumsiness.getProgress());
                friend.setAwesome(switchAwesome.isChecked());
                friend.setGymFrequency((double) (seekBarGymFreq.getProgress()) / 2);
                friend.setTrustworthiness(ratingBarTrust.getProgress());
                friend.setMoneyOwed(Double.valueOf(String.valueOf(editTextMoneyOwed.getText())));
                friend.setName(String.valueOf(editTextName.getText()));



                Backendless.Persistence.save( friend, new AsyncCallback<Friend>() {
                    public void handleResponse( Friend response )
                    {

                        //set the values of friend to the new values that are changed
                        finish();



                    }

                    public void handleFault( BackendlessFault fault )
                    {
                        // an error has occurred, the error code can be retrieved with fault.getCode()
                    }
                });

            }
        });


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
        friend.setName(String.valueOf(editTextName.getText()));
        friend.setClumsiness(seekBarClumsiness.getProgress());
        friend.setAwesome(switchAwesome.isChecked());
        friend.setGymFrequency((double) (seekBarGymFreq.getProgress()) / 2);
        friend.setTrustworthiness(ratingBarTrust.getProgress());
        friend.setMoneyOwed(Double.valueOf(String.valueOf(editTextMoneyOwed.getText())));

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                friend.setOwnerId(Backendless.UserService.CurrentUser().getObjectId());

                friend.setClumsiness(seekBarClumsiness.getProgress());
                friend.setAwesome(switchAwesome.isChecked());
                friend.setGymFrequency((double) (seekBarGymFreq.getProgress()) / 2);
                friend.setTrustworthiness(ratingBarTrust.getProgress());
                friend.setMoneyOwed(Double.valueOf(String.valueOf(editTextMoneyOwed.getText())));
                friend.setName(String.valueOf(editTextName.getText()));

                finish();
            }
        });
    }

}
