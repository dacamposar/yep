package es.davidcampos.yep;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseUser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements ActionBar.TabListener {


    private static final String TAG = MainActivity.class.getName();
    public static final int HAZ_UNA_FOTO_REQUEST = 0;
    public static final int HAZ_UN_VIDEO_REQUEST = 1;
    public static final int SELECCIONAR_FOTO_REQUEST = 2;
    public static final int SELECCIONAR_VIDEO_REQUEST = 3;
    public static final String FILTRO_IMAGENES = "image/*";
    public static final String FILTRO_VIDEOS = "video/*";
    public static final int LIMITE_TAM = 10 * 1024 * 1024;
    private Uri mMediaUri;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            Log.d(TAG, currentUser.getUsername() + " logueado correctamente");
        } else {
            Intent intent = new Intent(this,LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }


        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent;
        switch(id) {
            case R.id.action_logout:
                ParseUser.logOut();
                intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            case R.id.action_edit_friends:
                intent = new Intent(MainActivity.this, EditFriendsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_camera:
                ventanaDialogOpcionesCamera();
        }
        return super.onOptionsItemSelected(item);

        }

    private void ventanaDialogOpcionesCamera() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(R.array.camera_choices, new DialogInterface.OnClickListener() {
            String [] opciones = getResources().getStringArray(R.array.camera_choices);

            public void onClick(DialogInterface dialog, int which) {
                switch(which){
                    case 0:
                        Log.d(TAG,opciones[0]);
                        hazUnaFoto();
                        break;
                    case 1:
                        Log.d(TAG,opciones[1]);
                        hazUnVideo();
                        break;
                    case 2:
                        Log.d(TAG,opciones[2]);
                        seleccionaUnaFoto();
                        break;
                    case 3:
                        Log.d(TAG,opciones[3]);
                        seleccionaUnVideo();
                        break;
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void seleccionaUnVideo() {
        warningFieldDialog("Eliga un video que no exceda de 10MB");

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(FILTRO_VIDEOS);
        startActivityForResult(intent,SELECCIONAR_VIDEO_REQUEST);


    }

    private void seleccionaUnaFoto() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(FILTRO_IMAGENES);
        startActivityForResult(intent,SELECCIONAR_FOTO_REQUEST);
    }

    private void hazUnVideo() {
        Intent hacerVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        mMediaUri = FileUtilities.getOutputMediaFileUri(FileUtilities.MEDIA_TYPE_VIDEO);
        if(mMediaUri == null) {
            Log.e(TAG, "Error al guardar el video");
        } else {
            hacerVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
            hacerVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
            hacerVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);

            startActivityForResult(hacerVideoIntent, HAZ_UN_VIDEO_REQUEST);


        }
    }

    private void hazUnaFoto() {
        Intent hacerFotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mMediaUri = FileUtilities.getOutputMediaFileUri(FileUtilities.MEDIA_TYPE_IMAGE);
        if (mMediaUri == null) {
            Log.e(TAG, "Error al guardar la imagen");
        } else {
            hacerFotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
            startActivityForResult(hacerFotoIntent, HAZ_UNA_FOTO_REQUEST);


        }


    }


    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        boolean error = false;
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECCIONAR_FOTO_REQUEST || requestCode == SELECCIONAR_VIDEO_REQUEST) {
                if (data != null) {
                    mMediaUri = data.getData();

                    if (requestCode == SELECCIONAR_VIDEO_REQUEST) {
                        int tamVideo = 0;
                        InputStream flujoVideo = null;
                        try {
                            flujoVideo = getContentResolver().openInputStream(mMediaUri);
                            tamVideo = flujoVideo.available();
                        } catch (FileNotFoundException e) {
                            Log.e(TAG, "Fichero no encontrado", e);
                        } catch (IOException e) {
                            Log.e(TAG, "Error de I/O", e);
                        } finally {
                            if (flujoVideo != null) {
                                try {
                                    flujoVideo.close();
                                } catch (IOException e) {
                                    Log.e(TAG, "No se ha podido cerrar el fichero", e);
                                }
                            }
                        }

                        if (tamVideo > LIMITE_TAM) {
                            errorFieldDialog(getString(R.string.error_fichero_muy_grande));
                            error = true;
                        }

                    }
                }


            } else if (requestCode == HAZ_UNA_FOTO_REQUEST || requestCode == HAZ_UN_VIDEO_REQUEST) {

                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(mMediaUri);

                sendBroadcast(intent);
            }

            if(!error){

                String fileType = "";

                if (requestCode == HAZ_UNA_FOTO_REQUEST || requestCode == SELECCIONAR_FOTO_REQUEST)
                    fileType = ParseConstants.TYPE_IMAGE;
                else if (requestCode == HAZ_UN_VIDEO_REQUEST || requestCode == SELECCIONAR_VIDEO_REQUEST)
                    fileType = ParseConstants.TYPE_VIDEO;

                Intent intent = new Intent(this, RecipientsActivityWithFragment.class);
                intent.setData(mMediaUri);
                intent.putExtra(ParseConstants.KEY_TIPO_FICHERO, fileType);

                startActivity(intent);
            }
        }
    }
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public static final int TAB_NUMBER = 2;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position == 0)return new InboxFragment();
            else return new FriendFragment();


        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return TAB_NUMBER;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
    private void warningFieldDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setTitle("Warning").setIcon(android.R.drawable.ic_dialog_alert);


        AlertDialog dialog = builder.create();

        dialog.show();

    }
    private void errorFieldDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setTitle("Error").setIcon(android.R.drawable.ic_dialog_alert);


        AlertDialog dialog = builder.create();

        dialog.show();

    }
}
