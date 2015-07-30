package es.davidcampos.yep;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class RecipientsActivityWithFragmentFragment extends ListFragment {

    private static final String TAG = RecipientsActivityWithFragmentFragment.class.getName() ;
    private List<ParseUser> mUsers;
    private ArrayList<String> usernames;
    private ArrayList<String> userIds;

    private MenuItem botonEnviar;
    private ArrayAdapter<String> adapter;
    private ParseRelation<ParseUser> mFriendsRelation;
    private ParseUser mCurrentUser;

    private Uri mMediaUri;
    private String mFileType;

    public RecipientsActivityWithFragmentFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        Intent intent = getActivity().getIntent();
        mMediaUri = intent.getData();
        mFileType = intent.getStringExtra(ParseConstants.KEY_TIPO_FICHERO);

        return inflater.inflate(R.layout.fragment_recipients_activity_with, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();


        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        usernames = new ArrayList<String>();
        userIds = new ArrayList<String>();

        adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_checked, usernames);
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
                        userIds.add(user.getObjectId());
                    }

                } else {
                    Log.e(TAG, "ParseException caught: ", e);
                }
            }
        });

        ProgressBar progressbar = (ProgressBar) getActivity().findViewById(R.id.progressBar);
        progressbar.setVisibility(View.GONE);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_recipients, menu);
        botonEnviar= menu.getItem(0);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (l.getCheckedItemCount() > 0) {
            botonEnviar.setVisible(true);
        }else{
            botonEnviar.setVisible(false);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_send) {
            ParseObject message = createMessage();

            if(message == null){
               // mostrar error
            }
            else{
                send(message);
                getActivity().finish();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void send(ParseObject message) {
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null)
                    //Toast.makeText(RecipientsActivityWithFragmentFragment.getActivity(), "¡Mensaje enviado!", Toast.LENGTH_SHORT).show();

            }


        });
    }


    private ParseObject createMessage() {
        ParseObject mensaje = new ParseObject(ParseConstants.CLASE_MENSAJE);
        mensaje.put(ParseConstants.KEY_RECEPTOR_ID,ParseUser.getCurrentUser().getObjectId());
        mensaje.put(ParseConstants.KEY_RECEPTOR_NOMBRE,ParseUser.getCurrentUser().getUsername());
        mensaje.put(ParseConstants.KEY_DESTINATARIOS_IDS,getRecipientIds());
        mensaje.put(ParseConstants.KEY_TIPO_FICHERO,mFileType);

        byte[] fileBytes =  FileHelper.getByteArrayFromFile(getActivity(), mMediaUri);

        if(fileBytes==null){
            return null;
        }
        else {
            if (mFileType.equals(ParseConstants.TYPE_IMAGE)) {
                fileBytes = FileHelper.reduceImageForUpload(fileBytes);
            }

            String fileName = FileHelper.getFileName(getActivity(),mMediaUri,mFileType);

            ParseFile file = new ParseFile(fileName, fileBytes);

            mensaje.put(ParseConstants.KEY_TIPO_FICHERO, file);

            return mensaje;
        }

    }
    private ArrayList<String> getRecipientIds() {

        ArrayList<String> recipientList = new ArrayList<>();

        for(int i=0; i< getListView().getCount();i++){
            if(getListView().isItemChecked(i)){
                recipientList.add(userIds.get(i));
            }
        }
        return recipientList;
    }


}

