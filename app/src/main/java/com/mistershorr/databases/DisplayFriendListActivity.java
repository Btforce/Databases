package com.mistershorr.databases;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.backendless.Backendless;
import com.backendless.UserService;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class DisplayFriendListActivity extends AppCompatActivity {

    private TextView textViewName;
    private TextView textViewClumsiness;
    private TextView textViewMoneyOwed;
    private ListView listView;

    public static final String EXTRA_FRIEND = "friend";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_friend_list);

        wireWidgets();

        //search only for Friends that have ownerIds that match the user's objectId
        loadDataFromBackendless();
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

                // todo make a custom adapter to display the friends and load the list that
                // is retrieved into the adapter

                //todo make friend parcelable
                //todo when a friend is clicked, it opens the detail activity and loads the info


                FriendAdapter friendAdapter = new FriendAdapter(foundFriend);
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

    }

    private class FriendAdapter extends ArrayAdapter<Friend>{

        private List<Friend> friendList;
        int position;

        public FriendAdapter(List<Friend> friendList){
            super(DisplayFriendListActivity.this,-1,friendList);
            this.friendList = friendList;
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