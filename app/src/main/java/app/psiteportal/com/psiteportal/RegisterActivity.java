package app.psiteportal.com.psiteportal;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.psiteportal.com.utils.JSONParser;

/**
 * Created by Personal on 9/7/2015.
 */
public class RegisterActivity extends Activity {

    ProgressDialog pDialog;
    EditText username_txt, password_txt, repassword_txt, fname_txt, lname_txt, contact_txt, email_txt, address_txt, institution_txt;
    RadioGroup gender_radio_group;
    RadioButton gender_radio;
    Button registerUser;
    int selectedRadId;
    String username, password, firstname, lastname, gender, contact, email, address, institution;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    JSONParser jsonParser = new JSONParser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username_txt = (EditText) findViewById(R.id.username_txt);
        password_txt = (EditText) findViewById(R.id.password_txt);
        repassword_txt = (EditText) findViewById(R.id.repassword_txt);
        fname_txt = (EditText) findViewById(R.id.firstname_txt);
        lname_txt = (EditText) findViewById(R.id.lastname_txt);
        contact_txt = (EditText) findViewById(R.id.contact_txt);
        email_txt = (EditText) findViewById(R.id.email_txt);
        address_txt = (EditText) findViewById(R.id.address_txt);
        institution_txt = (EditText) findViewById(R.id.institution_txt);

        addListenerOnButton();
    }

    public void addListenerOnButton() {

        gender_radio_group = (RadioGroup) findViewById(R.id.gender_radio_group);
        registerUser = (Button) findViewById(R.id.register_btn);

        registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedRadId = gender_radio_group.getCheckedRadioButtonId();
                Log.i("radio id", selectedRadId+"<<");
                gender_radio = (RadioButton) findViewById(selectedRadId);
                gender = gender_radio.getText().toString();

                if(password_txt.getText().toString().equals(repassword_txt.getText().toString())){
                    new RegisterTask().execute();
                }else{
                    Toast.makeText(RegisterActivity.this, "Password did not match!", Toast.LENGTH_SHORT).show();
                    password_txt.setText("");
                    repassword_txt.setText("");
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

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

    public void cancelRegister(View v){
        finish();
    }


    private class RegisterTask extends AsyncTask<String, String, String> {

        String username = username_txt.getText().toString();
        String password = password_txt.getText().toString();
        String firstname = fname_txt.getText().toString();
        String lastname = lname_txt.getText().toString();
        String contact = contact_txt.getText().toString();
        String email = email_txt.getText().toString();
        String address = address_txt.getText().toString();
        String institution = institution_txt.getText().toString();

        int success;
        Resources res = getResources();
        String REGISTER_URL = res.getString(R.string.register_url);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RegisterActivity.this);
            pDialog.setTitle("Creating new account . . .");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {

            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("firstname", firstname));
                params.add(new BasicNameValuePair("lastname", lastname));
                params.add(new BasicNameValuePair("gender", gender));
                params.add(new BasicNameValuePair("contact", contact));
                params.add(new BasicNameValuePair("email", email));
                params.add(new BasicNameValuePair("address", address));
                params.add(new BasicNameValuePair("institution", institution));
                params.add(new BasicNameValuePair("password", password));

                Log.i("params", params.toString());

                JSONObject json = jsonParser.makeHttpRequest(
                        REGISTER_URL, "POST", params);

                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("Registration success!", json.toString());
                    finish();

                    return json.getString(TAG_MESSAGE);
                }else{
                    Log.d("Registration failure!", json.getString(TAG_MESSAGE));
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
                Toast.makeText(RegisterActivity.this, file_url, Toast.LENGTH_SHORT).show();
//                Log.d("Login result", file_url);
            }
        }

    }



}
