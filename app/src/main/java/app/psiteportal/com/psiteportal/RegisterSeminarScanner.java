package app.psiteportal.com.psiteportal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.psiteportal.com.utils.JSONParser;

/**
 * Created by Lawrence on 9/27/2015.
 */
public class RegisterSeminarScanner extends AppCompatActivity {

    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    Button scanQR;
    Button pay, discount_pay;
    TextView name, address, email, institution, points, usertype;
    ProgressDialog pDialog;
    String pid;
    final static String TAG_SUCCESS = "success";
    final static String TAG_MESSAGE = "message";
    private static final String TAG_USERS = "users";
    private static final String TAG_PID = "pid";
    private static final String TAG_FIRSTNAME = "firstname";
    private static final String TAG_LASTNAME = "lastname";
    private static final String TAG_GENDER = "gender";
    private static final String TAG_ADDRESS = "address";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_INSTITUTION = "institution";
    private static final String TAG_POINTS = "points";
    private static final String TAG_USERTYPE = "usertype";
    private static final String TAG_PAY = "normal";
    private static final String TAG_PAY_DISCOUNT = "discounted";
    JSONParser jsonParser = new JSONParser();
    JSONArray users = null;
    String sid, bonus_points, seminar_fee, discount_fee, points_cost;
    int success;
    String res_id, res_fname, res_lname, res_gender, res_address, res_email, res_institution, res_points, res_usertype;
    String result_message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_registration);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            sid = extras.getString("sid");
            bonus_points = extras.getString("bonus_points");
            seminar_fee = extras.getString("seminar_fee");
            discount_fee = extras.getString("discount_fee");
            points_cost = extras.getString("points_cost");
        }

        scanQR = (Button) findViewById(R.id.qr_scan_btn);
        pay = (Button) findViewById(R.id.pay);
        discount_pay = (Button) findViewById(R.id.discount_pay);
        name = (TextView) findViewById(R.id.fname_holder);
        address = (TextView) findViewById(R.id.address_holder);
        email = (TextView) findViewById(R.id.email_holder);
        institution = (TextView) findViewById(R.id.institution_holder);
        points = (TextView) findViewById(R.id.points_holder);
        usertype = (TextView) findViewById(R.id.usertype_holder);

        scanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(ACTION_SCAN);
                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                    startActivityForResult(intent, 0);
                } catch (ActivityNotFoundException anfe) {
                    showDialog(RegisterSeminarScanner.this, "No Scanner Found", "Download the scanner application?", "Yes", "No").show();
                }
            }
        });



        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PayRegistration().execute();
            }
        });


        discount_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PayDiscountedRegistration().execute();
            }
        });


    }

    private static AlertDialog showDialog(final FragmentActivity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {

                }
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        return downloadDialog.show();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
//                Toast toast = Toast.makeText(RegisterSeminarScanner.this, "Content:" + contents + "Format:" + format, Toast.LENGTH_LONG);
//                toast.show();

                String[] resultArr = contents.split(",");

                pid = resultArr[0];
                Log.e("user ID", resultArr[0]);

                new GetUserTask().execute();

            }
        }
    }


    private class GetUserTask extends AsyncTask<String, String, String> {

        JSONParser jsonParser = new JSONParser();
        Resources res = getResources();
        String GET_USER_URL = res.getString(R.string.get_user_url);


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RegisterSeminarScanner.this);
            pDialog.setTitle("Getting user details . . .");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("pid", pid));

            JSONObject json = jsonParser.makeHttpRequest(
                    GET_USER_URL, "POST", params);

            try {
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("get user success!", json.toString());
                    users = json.getJSONArray(TAG_USERS);

                    for (int i = 0; i < users.length(); i++) {
                        JSONObject c = users.getJSONObject(i);

                        res_id = c.getString(TAG_PID);
                        res_fname = c.getString(TAG_FIRSTNAME);
                        res_lname = c.getString(TAG_LASTNAME);
                        res_gender = c.getString(TAG_GENDER);
                        res_address = c.getString(TAG_ADDRESS);
                        res_email = c.getString(TAG_EMAIL);
                        res_institution = c.getString(TAG_INSTITUTION);
                        res_points = c.getString(TAG_POINTS);
                        res_usertype = c.getString(TAG_USERTYPE);
                    }

//                    return json.getString(TAG_MESSAGE);
                } else {
//                    Log.d("get seminars failure", json.getString(TAG_MESSAGE));
//
//                    return json.getString(TAG_MESSAGE);
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
                Toast.makeText(RegisterSeminarScanner.this, file_url, Toast.LENGTH_SHORT).show();
            }

            if (success == 1) {
                name.setText(res_fname + " " + res_lname);
                address.setText(res_address);
                email.setText(res_email);
                institution.setText(res_institution);
                points.setText(res_points);
                usertype.setText(res_usertype);

                if (Integer.parseInt(res_points) > Integer.parseInt(points_cost)) {
                    pay.setVisibility(View.VISIBLE);
                    discount_pay.setVisibility(View.VISIBLE);
                } else {
                    pay.setVisibility(View.VISIBLE);
                }

            }

        }

    }


    private class PayRegistration extends AsyncTask<String, String, String> {

        JSONParser jsonParser = new JSONParser();
        Resources res = getResources();
        String PAYMENT_URL = res.getString(R.string.payment_url);


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RegisterSeminarScanner.this);
            pDialog.setTitle("Processing transaction details . . .");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("pid", pid));
            params.add(new BasicNameValuePair("sid", sid));
            params.add(new BasicNameValuePair("pay_type", TAG_PAY));

            JSONObject json = jsonParser.makeHttpRequest(
                    PAYMENT_URL, "POST", params);

            try {
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {

                } else {
                    result_message = json.getString(TAG_MESSAGE);
                    Log.e("Failed to register", json.getString(TAG_MESSAGE));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            pDialog.dismiss();

            if (success == 1) {

                Toast.makeText(RegisterSeminarScanner.this, res_fname +" "+ res_lname +" is successfully registered.", Toast.LENGTH_SHORT).show();
                name.setText("");
                address.setText("");
                email.setText("");
                institution.setText("");
                points.setText("");
                usertype.setText("");
                pay.setVisibility(View.GONE);
                discount_pay.setVisibility(View.GONE);
            }else{
                Toast.makeText(RegisterSeminarScanner.this, result_message, Toast.LENGTH_SHORT).show();
            }

        }

    }


    private class PayDiscountedRegistration extends AsyncTask<String, String, String> {

        JSONParser jsonParser = new JSONParser();
        Resources res = getResources();
        String PAYMENT_URL = res.getString(R.string.payment_url);


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RegisterSeminarScanner.this);
            pDialog.setTitle("Processing transaction details . . .");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("pid", pid));
            params.add(new BasicNameValuePair("sid", sid));
            params.add(new BasicNameValuePair("pay_type", TAG_PAY_DISCOUNT));

            JSONObject json = jsonParser.makeHttpRequest(
                    PAYMENT_URL, "POST", params);

            try {
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {

                } else {
                    result_message = json.getString(TAG_MESSAGE);
                    Log.e("Failed to register", result_message);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            pDialog.dismiss();

            if (success == 1) {
                Toast.makeText(RegisterSeminarScanner.this, res_fname +" "+ res_lname +" is successfully registered.", Toast.LENGTH_SHORT).show();

                name.setText("");
                address.setText("");
                email.setText("");
                institution.setText("");
                points.setText("");
                usertype.setText("");
                pay.setVisibility(View.GONE);
                discount_pay.setVisibility(View.GONE);
            }else{
                Toast.makeText(RegisterSeminarScanner.this, result_message, Toast.LENGTH_SHORT).show();
            }

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}

