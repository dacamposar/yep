package es.davidcampos.yep;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by David on 28/07/2015.
 */
public class FileUtilities {
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final String APP_NAME = "Yep";
    private static final String TAG = FileUtilities.class.getName();

    /** Create a file Uri for saving an image or video */
    public static Uri getOutputMediaFileUri(int mediaType){

        if(isExternalStorageAvailable()) {
            // 1 Obtenemos el directorio del almacenamiento externo
            File mediaStorageDir = null;

            switch (mediaType) {
                case MEDIA_TYPE_IMAGE:
                    mediaStorageDir = Environment.
                            getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    break;
                case MEDIA_TYPE_VIDEO:
                    mediaStorageDir = Environment.
                            getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
                    break;
            }

            // 2 Crear nuestro subdirecttorio
            File appDir = new File(mediaStorageDir, APP_NAME);

            if (!appDir.exists()) {
                Log.d(TAG, appDir.getAbsolutePath() + " not exist");
                if (!appDir.mkdirs()) {
                    Log.d(TAG, "Directory "+appDir.getAbsolutePath()+" not created");
                    return null;
                }
            }

            // 3 Crear un nombre del fichero
            String fileName ="";
            Date now = new Date();
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", new Locale("es", "ES")).format(now);

            switch (mediaType) {
                case MEDIA_TYPE_IMAGE:
                    fileName = "IMG_" + timestamp + ".jpg";
                    break;
                case MEDIA_TYPE_VIDEO:
                    fileName = "VID_" + timestamp + ".mp4";
                    break;
            }

            Log.d(TAG, "File name : " + fileName);

            // 4 Crear un objeto File con el nombre del fichero

            String pathFile = appDir.getAbsolutePath() + File.separator + fileName;
            File mediaFile = new File(pathFile);
            Log.d(TAG, "File : " + mediaFile.getAbsolutePath());

            // 5 Devolver el URI del fichero.
            return Uri.fromFile(mediaFile);

        }
        return null;

    }



    private static boolean isExternalStorageAvailable() {

        String state = Environment.getExternalStorageState();

        return state.equals(Environment.MEDIA_MOUNTED);

    }
}
