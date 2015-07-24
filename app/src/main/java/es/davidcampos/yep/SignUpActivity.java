package es.davidcampos.yep;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class SignUpActivity extends ActionBarActivity {

    private final static String TAG = SignUpActivity.class.getName();

    private EditText campoUsuario;
    private EditText campoPassword;
    private EditText campoEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        inicializarCampos();

    }

    public void botonRegistrar(View view) {
        try {
            comprobarCampos();
        } catch (Exception e)
        {
            Log.e(TAG,"Error ",e);

        }
    }

    private void comprobarCampos() {
        String usuario = campoUsuario.getText().toString().trim();
        String password = campoPassword.getText().toString().trim();
        String email = campoEmail.getText().toString().trim();
        if (usuario.isEmpty()) {
           errorFieldDialog(getString(R.string.error_campo_nombre_vacio));
        } else if (password.isEmpty()) {
            errorFieldDialog(getString(R.string.error_campo_contrasena_vacio));
        } else if (email.isEmpty() || !isValidEmail(email)) {
            errorFieldDialog(getString(R.string.error_campo_email_vacio));
        }
        else agnadirUsuario(usuario, password, email);
    }

    private void agnadirUsuario(String usuario, String password, String email) {

        ParseUser user = new ParseUser();
        user.setUsername(usuario);
        user.setPassword(password);
        user.setEmail(email);



        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Añadido usuario correctamente");
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    errorFieldDialog(getString(R.string.error_registrar_usuario));
                }
            }
        });
    }

    private void inicializarCampos() {
        campoUsuario = (EditText) findViewById(R.id.usuario);
        campoPassword = (EditText) findViewById(R.id.password);
        campoEmail = (EditText) findViewById(R.id.email);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
        return true;
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

      public  boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    private void errorFieldDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        builder.setMessage(message)
        .setPositiveButton(android.R.string.ok, null)
       .setTitle("Error")
                .setIcon(android.R.drawable.ic_dialog_alert);


        AlertDialog dialog = builder.create();

        dialog.show();

    }
}
