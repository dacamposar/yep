package es.davidcampos.yep;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David on 26/07/2015.
 */
public class FriendFragment extends ListFragment {
    private static final String TAG = FriendFragment.class.getName() ;
    private List<ParseUser> mUsers;
    private ArrayList<String> usernames;

    private ArrayAdapter<String> adapter;
    private ParseRelation<ParseUser> mFriendsRelation;
    private ParseUser mCurrentUser;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.friend_fragment, container, false);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();


        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        usernames = new ArrayList<String>();


        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, usernames);
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

                    }

                } else {
                    Log.e(TAG, "ParseException caught: ", e);
                }
            }
        });

        ProgressBar progressbar = (ProgressBar) getActivity().findViewById(R.id.progressBar);
        progressbar.setVisibility(View.GONE);
    }
}
