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

import java.util.ArrayList;
import java.util.List;


public class RecipientsActivity extends ListActivity {
    private static final String TAG = FriendFragment.class.getName() ;
    private List<ParseUser> mUsers;
    private ArrayList<String> usernames;
    private MenuItem botonEnviar;
    private ArrayAdapter<String> adapter;
    private ParseRelation<ParseUser> mFriendsRelation;
    private ParseUser mCurrentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipients);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recipients, menu);
        botonEnviar= menu.getItem(0);
        return true;
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        botonEnviar.setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_send) {

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();


        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        usernames = new ArrayList<String>();


        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_checked, usernames);
        setListAdapter(adapter);
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

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

                    }

                } else {
                    Log.e(TAG, "ParseException caught: ", e);
                }
            }
        });

        ProgressBar progressbar = (ProgressBar) findViewById(R.id.progressBar);
        progressbar.setVisibility(View.GONE);
    }


}
