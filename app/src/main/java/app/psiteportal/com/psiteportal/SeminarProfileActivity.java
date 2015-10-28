package app.psiteportal.com.psiteportal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import app.psiteportal.com.utils.JSONParser;

/**
 * Created by Lawrence on 10/4/2015.
 */
public class SeminarProfileActivity extends AppCompatActivity implements View.OnClickListener{

    RatingBar rating;
    TextView rate;
    TextView title_seminar;
    TextView seminar_date;
    TextView seminar_about;
    TextView seminar_time;
    TextView seminar_venue;
    TextView seminar_facilitator;
    TextView seminar_bonus;
    TextView seminar_fee;
    TextView discounted_fee;
    TextView seminar_points_cost;
    EditText comment;
    LinearLayout rating_layout;
    Button mSubmit;
    ProgressDialog pDialog;
    String post, post_rate, post_comm;
    JSONParser jsonParser = new JSONParser();
    String seminar_id, user_id, seminar_title, month, day, year, start_hour, start_min, start_time_of_day,
            end_hour, end_min, end_time_of_day, bonus, fee, discounted, points_cost, venue, facilitator, about, isActive, outActivated;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seminar_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            seminar_id = extras.getString("seminar_id");
            user_id = extras.getString("user_id");
            seminar_title = extras.getString("seminar_title");
            month = extras.getString("seminar_month");
            day = extras.getString("seminar_day");
            year = extras.getString("seminar_year");
            start_hour = extras.getString("seminar_start_hour");
            start_min = extras.getString("seminar_start_min");
            start_time_of_day = extras.getString("seminar_start_time_of_day");
            end_hour = extras.getString("seminar_end_hour");
            end_min = extras.getString("seminar_end_min");
            end_time_of_day = extras.getString("seminar_end_time_of_day");
            bonus = extras.getString("seminar_bonus");
            fee = extras.getString("seminar_fee");
            discounted = extras.getString("seminar_discounted");
            points_cost = extras.getString("seminar_points_cost");
            venue = extras.getString("seminar_venue");
            facilitator = extras.getString("seminar_facilitator");
            about = extras.getString("seminar_about");
            isActive = extras.getString("seminar_is_active");
            outActivated = extras.getString("seminar_out_activated");
        }

        title_seminar = (TextView) findViewById(R.id.seminar_title);
        rating = (RatingBar) findViewById(R.id.ratingBar);
        rate = (TextView) findViewById(R.id.txtRatingValue);
        seminar_date = (TextView) findViewById(R.id.seminar_date);
        seminar_time = (TextView) findViewById(R.id.seminar_time);
        seminar_venue = (TextView) findViewById(R.id.seminar_venue);
        seminar_about = (TextView) findViewById(R.id.seminar_about);
        seminar_fee = (TextView) findViewById(R.id.seminar_profile_fee);
        seminar_bonus = (TextView) findViewById(R.id.seminar_points);
        discounted_fee = (TextView) findViewById(R.id.seminar_discounted_fee);
        seminar_points_cost = (TextView) findViewById(R.id.seminar_points_cost);
        seminar_facilitator = (TextView) findViewById(R.id.seminar_facilitator);
        comment = (EditText) findViewById(R.id.comm);
        rating_layout = (LinearLayout) findViewById(R.id.rating_holder);

        mSubmit = (Button) findViewById(R.id.btnSubmit);
        mSubmit.setOnClickListener(this);
        addListenerOnRatingBar();

        int mon = Integer.parseInt(month);
        String month_name = formatMonth(mon, Locale.getDefault());

        //set textview to seminar title from bundle
        title_seminar.setText(seminar_title);
        seminar_about.setText(about);
        seminar_date.setText(month_name + " " + day + ", " + year);
        seminar_bonus.setText(bonus);
        seminar_fee.setText(fee);
        discounted_fee.setText(discounted);
        seminar_points_cost.setText(points_cost);
        seminar_venue.setText(venue);
        seminar_time.setText(start_hour + ":" + start_min + start_time_of_day + " - " + end_hour + ":" + end_min + end_time_of_day);


        if(outActivated.equals("0")){
            rating_layout.setVisibility(View.GONE);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

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

    public void addListenerOnRatingBar() {

        rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {

                rate.setText(String.valueOf(rating));

            }
        });
    }

    public String formatMonth(int month, Locale locale) {
        DateFormatSymbols symbols = new DateFormatSymbols(locale);
        String[] monthNames = symbols.getMonths();
        return monthNames[month - 1];
    }

    @Override
    public void onClick(View v) {

        new PostComment().execute();
    }

    class PostComment extends AsyncTask<String, String, String> {

        JSONParser jsonParser = new JSONParser();
        Resources res = getResources();
        String ATTENDANCE_URL = res.getString(R.string.attendance_url);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            post_rate = String.valueOf(rating.getRating());
            post_comm = comment.getText().toString();

            pDialog = new ProgressDialog(SeminarProfileActivity.this);
            pDialog.setMessage("Submitting your ratings . . .");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success;

            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("sid", seminar_id));
                params.add(new BasicNameValuePair("pid", user_id));
                params.add(new BasicNameValuePair("rating", post_rate));
                params.add(new BasicNameValuePair("comment", post_comm));

                Log.d("request!", params.toString());

                // Posting user data to script
                JSONObject json = jsonParser.makeHttpRequest(ATTENDANCE_URL,
                        "POST", params);

                // full json response
                Log.d("Post Comment attempt", json.toString());

                // json success element
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("Comment Added!", json.toString());
                    return json.getString(TAG_MESSAGE);
                } else {
                    Log.d("Comment Failure!", json.getString(TAG_MESSAGE));
                    return json.getString(TAG_MESSAGE);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            if (file_url != null) {
                Toast.makeText(SeminarProfileActivity.this, file_url, Toast.LENGTH_LONG)
                        .show();
            }

        }

    }


}
