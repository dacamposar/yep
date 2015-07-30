package es.davidcampos.yep;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;


public class LoginActivity extends ActionBarActivity {
    private final static String TAG = LoginActivity.class.getName();
    private EditText usuario;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_login);
        usuario = (EditText) findViewById(R.id.usuario);
        password = (EditText) findViewById(R.id.password);
    }
    public void clicRegistro(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    public void accionIngresar(View view) {
        //TODO comprobarCampos()
        String sUsuario = usuario.getText().toString().trim();
        String sPassword = password.getText().toString().trim();
        LoggingUser(sUsuario, sPassword);
    }

    public void accionOlvidarPassword(View view) {
        Intent intent = new Intent(LoginActivity.this, ForgetActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        final ProgressDialog dialog = ProgressDialog.show(LoginActivity.this,
                getString(R.string.mensaje_cargando),
                getString(R.string.mensaje_espera), true);
        ParseUser.requestPasswordResetInBackground("myemail@example.com", new RequestPasswordResetCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // An email was successfully sent with reset instructions.
                } else {
                    // Something went wrong. Look at the ParseException to see what's up.
                }
            }
        });
    }

    private void LoggingUser(String sUsuario, String sPassword) {
       // setProgressBarIndeterminateVisibility(true);

        final ProgressDialog dialog = ProgressDialog.show(LoginActivity.this,
                getString(R.string.mensaje_logueando),
                getString(R.string.mensaje_espera), true);

        ParseUser.logInInBackground(sUsuario, sPassword, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
               // setProgressBarIndeterminateVisibility(false);
                dialog.dismiss();
                if (user != null) {
                    Log.d(TAG, user.getUsername() + " logueado correctamente");
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    errorFieldDialog(getString(R.string.error_loguear_usuario));
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

    private void errorFieldDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setTitle("Error")
                .setIcon(android.R.drawable.ic_dialog_alert);


        AlertDialog dialog = builder.create();

        dialog.show();

    }
}
