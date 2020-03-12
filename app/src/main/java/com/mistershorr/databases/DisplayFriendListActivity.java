package com.mistershorr.databases;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DisplayFriendListActivity extends AppCompatActivity {

    private TextView textViewName;
    private TextView textViewClumsiness;
    private TextView textViewMoneyOwed;
    private ListView listView;
    private FloatingActionButton floatingActionButton;
    private FriendAdapter friendAdapter;
    private Comparator<Friend> comparatorName;


    public static final String EXTRA_FRIEND = "friend";

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.ContextMenu_contextMenu_deleteFriend:

                deleteFriend(info.position);

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }



    private void deleteFriend(int itemId) {

        //Backendless.Persistence.of( Friend.class ).remove(friendAdapter.getItem(itemId));

        Backendless.Persistence.of( Friend.class ).remove(friendAdapter.getItem(itemId), new AsyncCallback<Long>() {
            @Override
            public void handleResponse(Long response) {
                loadDataFromBackendless();
                Toast.makeText(DisplayFriendListActivity.this, "Deleted Friend", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void handleFault(BackendlessFault fault) {

            }
        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_friend_list);

        wireWidgets();
        setListeners();

        comparatorName = new Comparator<Friend>() {
            @Override
            public int compare(Friend friend, Friend t1) {
                return friend.getName().compareTo(t1.getName());
            }
        };
        //search only for Friends that have ownerIds that match the user's objectId
        loadDataFromBackendless();

        registerForContextMenu(listView);


    }

    private void setListeners() {

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent newFriendIntent = new Intent(DisplayFriendListActivity.this, FriendDetailActivity.class);
                startActivity(newFriendIntent);

            }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.optionMenu_optionMenu_sortByAlphatbetical:
                sortByAlphabetical();
                friendAdapter.notifyDataSetChanged();
                return true;

            case R.id.optionMenu_optionMenu_sortByMoneyOwed:
                sortByMoneyOwed();
                friendAdapter.notifyDataSetChanged();
                return true;

            case R.id.optionMenu_optionMenu_logOut:
                Backendless.UserService.logout( new AsyncCallback<Void>()
                {
                    public void handleResponse( Void response )
                    {
                        // user has been logged out.
                        Intent intentLogInActivity = new Intent(DisplayFriendListActivity.this, LoginActivity.class);
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

    private void sortByMoneyOwed() {
        Collections.sort(friendAdapter.getFriendList());
    }

    private void sortByAlphabetical() {
        Collections.sort(friendAdapter.getFriendList(), comparatorName);
    }

    private void loadDataFromBackendless(){

        String userId = Backendless.UserService.CurrentUser().getObjectId();

        // ownerId = ''
        String whereClause = "ownerId = '" + userId + "'";

        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause( whereClause );

        Backendless.Data.of( Friend.class).find(queryBuilder, new AsyncCallback<List<Friend>>(){
            @Override
            public void handleResponse(final List<Friend> foundFriend )
            {



                // all Contact instances have been found

                Log.d("LOADED FRIENDS", "handleResponse: " + foundFriend.toString());


                friendAdapter = new FriendAdapter(foundFriend);
                listView.setAdapter(friendAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent friendDetailIntent =
                                new Intent(DisplayFriendListActivity.this, FriendDetailActivity.class);

                        friendDetailIntent.putExtra(EXTRA_FRIEND, foundFriend.get(i));

                        startActivity(friendDetailIntent);
                    }
                });
            }
            @Override
            public void handleFault( BackendlessFault fault )
            {
                // an error has occurred, the error code can be retrieved with fault.getCode()
            }
        });

    }

    @Override
    protected void onResume() {
        loadDataFromBackendless();


        super.onResume();
    }

    private void wireWidgets() {

        listView = findViewById(R.id.ListView_displayFriendList_listView);
        floatingActionButton = findViewById(R.id.floatingActionButton_displayFriendList);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);

    }

    private class FriendAdapter extends ArrayAdapter<Friend>{

        private List<Friend> friendList;
        int position;

        public FriendAdapter(List<Friend> friendList){
            super(DisplayFriendListActivity.this,-1,friendList);
            this.friendList = friendList;
        }

        public List<Friend> getFriendList() {
            return friendList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            this.position = position;

            LayoutInflater inflater = getLayoutInflater();

            if (convertView == null ){
                convertView = inflater.inflate(R.layout.activity_friend_item, parent, false);
            }
            // wire widgets

            textViewClumsiness = convertView.findViewById(R.id.TextView_friendItem_clumsiness);
            textViewMoneyOwed = convertView.findViewById(R.id.TextView_friendItem_moneyOwed);
            textViewName = convertView.findViewById(R.id.textView_friendItem_name);

            // set widgets to values

            textViewName.setText(String.valueOf(friendList.get(position).getName()));
            textViewMoneyOwed.setText(String.valueOf(friendList.get(position).getMoneyOwed()));
            textViewClumsiness.setText(String.valueOf(friendList.get(position).getClumsiness()));
            Log.d("LOADED FRIENDS", "getView: " + friendList.get(position).getName());

            return convertView;
        }
    }
}