package app.psiteportal.com.psiteportal;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.psiteportal.com.utils.JSONParser;

public class LoginActivity extends Activity {

    ProgressDialog pDialog;
    private EditText username_et, password_et;
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_ID = "id";
    private static final String TAG_FNAME = "firstname";
    private static final String TAG_LNAME = "lastname";
    private static final String TAG_GENDER = "gender";
    private static final String TAG_CONTACT = "contact";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_ADDRESS = "address";
    private static final String TAG_INSTITUTION = "institution";
    private static final String TAG_POINTS = "points";
    private static final String TAG_QR_ID = "qr_id";
    private static final String TAG_USERTYPE = "usertype";

    JSONParser jsonParser = new JSONParser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username_et = (EditText) findViewById(R.id.username_et);
        password_et = (EditText) findViewById(R.id.password_et);

    }


    public void registerUser(View v) {

        startActivity(new Intent(this, RegisterActivity.class));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void signinUser(View v) {

        new LoginTask().execute();

//        Intent i = new Intent(LoginActivity.this, DrawerFragmentActivity.class);
//        Bundle bundle = new Bundle();
//        i.putExtra("firstname", "Lawrence");
//        i.putExtra("lastname", "Albor");
//        i.putExtras(bundle);
//        finish();
//        startActivity(i);

    }


    private class LoginTask extends AsyncTask<String, String, String> {

        int success;
        String username = username_et.getText().toString();
        String password = password_et.getText().toString();
        Resources res = getResources();
        String LOGIN_URL = res.getString(R.string.login_url);


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setTitle("Logging in . . .");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {

            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("password", password));

                JSONObject json = jsonParser.makeHttpRequest(
                        LOGIN_URL, "POST", params);

//                Log.d("Login attempt. . . ", json.toString());

                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("Login successfull!", json.toString());

                    Intent i = new Intent(LoginActivity.this, DrawerFragmentActivity.class);
                    Bundle bundle = new Bundle();
                    i.putExtra("pid", json.getString(TAG_ID));
                    i.putExtra("firstname", json.getString(TAG_FNAME));
                    i.putExtra("lastname", json.getString(TAG_LNAME));
                    i.putExtra("gender", json.getString(TAG_GENDER));
                    i.putExtra("contact", json.getString(TAG_CONTACT));
                    i.putExtra("email", json.getString(TAG_EMAIL));
                    i.putExtra("address", json.getString(TAG_ADDRESS));
                    i.putExtra("institution", json.getString(TAG_INSTITUTION));
                    i.putExtra("points", json.getString(TAG_POINTS));
                    i.putExtra("qr_id", json.getString(TAG_QR_ID));
                    i.putExtra("usertype", json.getString(TAG_USERTYPE));

                    i.putExtras(bundle);
                    finish();
                    startActivity(i);

                    return json.getString(TAG_MESSAGE);
                }else{
                    Log.d("Login failure", json.getString(TAG_MESSAGE));

                    return json.getString(TAG_MESSAGE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            pDialog.dismiss();

            if (file_url != null) {
                Toast.makeText(LoginActivity.this, file_url, Toast.LENGTH_SHORT).show();

                Log.d("Login result", file_url);

            }
        }

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        LoginActivity.this.finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

}
