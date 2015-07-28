package es.davidcampos.yep;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class EditFriendsActivity extends ListActivity {

    final static String TAG = EditFriendsActivity.class.getName();

    private List<ParseUser> mUsers;
    private ArrayList<String> usernames;
    private ArrayList<String> userIds;
    private ArrayAdapter<String> adapter;
    private ParseRelation<ParseUser> mFriendsRelation;
    private ParseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_friends);
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_friends, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        usernames = new ArrayList<String>();
        userIds = new ArrayList<String>();

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, usernames);
        setListAdapter(adapter);

        ParseQuery query = ParseUser.getQuery();
        query.orderByAscending(ParseConstants.KEY_USERNAME);
        query.setLimit(ParseConstants.MAX_USERS);

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List users, ParseException e) {
                if (e == null) {
                    //success
                    mUsers = users;
                    for (ParseUser user : mUsers) {
                        adapter.add(user.getUsername());
                        userIds.add(user.getObjectId());
                    }

                } else {
                    Log.e(TAG, "ParseException caught: ", e);
                }
            }
        });

        cargarAmigos();

        ProgressBar progressbar = (ProgressBar)findViewById(R.id.progressBar);
        progressbar.setVisibility(View.GONE);
    }

    private void cargarAmigos() {
        mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                for(ParseUser user: list) {
                    if(userIds.contains(user.getObjectId())){

                      getListView().setItemChecked(userIds.indexOf(user.getObjectId()),true);
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (getListView().isItemChecked(position)) {



            mFriendsRelation.add(mUsers.get(position));

        } else {
            mFriendsRelation.remove(mUsers.get(position));

        }

        mCurrentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    //success


                } else {
                    Log.e(TAG, "ParseException caught: ", e);
                }
            }
        });

        Log.d(TAG, "Amigo " + usernames.get(position)+" seleccionado");
    }
}
