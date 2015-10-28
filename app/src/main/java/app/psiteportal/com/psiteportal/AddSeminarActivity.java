package app.psiteportal.com.psiteportal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import app.psiteportal.com.utils.JSONParser;

/**
 * Created by Lawrence on 10/6/2015.
 */
public class AddSeminarActivity extends AppCompatActivity {

    ImageButton dateToActivate, select_time_start, select_time_end;
    TextView textView, time_start, time_end;
    Button add_seminar;
    Button cancel;
    EditText seminar_title;
    EditText seminar_fee;
    EditText discounted_fee;
    EditText seminar_venue;
    EditText about;
    EditText bonus_points;
    EditText point_cost;
    int day, month, year, hourOfDay, minute;
    private Calendar calendar;
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    String hourStart, hourEnd, startTimeOfDay, endTimeOfDay, minuteStart, minuteEnd;
    String pass_day, pass_month, pass_year;
    private static String url_create_seminar = "http://psite7.org/portal/add_seminar.php";
    private static final String TAG_SUCCESS = "success";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_seminar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        dateToActivate = (ImageButton) findViewById(R.id.SelectDate);
        textView = (TextView) findViewById(R.id.selectedDate);
        select_time_start = (ImageButton) findViewById(R.id.select_start_time);
        select_time_end = (ImageButton) findViewById(R.id.select_end_time);
        time_start = (TextView) findViewById(R.id.start_time);
        time_end = (TextView) findViewById(R.id.end_time);
        seminar_title = (EditText) findViewById(R.id.seminar_title);
        seminar_fee = (EditText) findViewById(R.id.seminar_fee);
        about = (EditText) findViewById(R.id.about);
        seminar_venue = (EditText) findViewById(R.id.seminar_venue);
        add_seminar = (Button) findViewById(R.id.update_seminar);
        bonus_points = (EditText) findViewById(R.id.bonus_points);
        point_cost = (EditText) findViewById(R.id.point_cost);
        discounted_fee = (EditText) findViewById(R.id.discount_fee);
        cancel = (Button) findViewById(R.id.cancel_btn);


        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
//        showDate(day, month + 1, year);

        add_seminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new CreateSeminar().execute();
            }
        });

    }


    public void setDate(View view) {
        showDialog(999);
    }

    public void setStartTime(View view) {
        showDialog(888);
    }

    public void setEndTime(View view) {
        showDialog(777);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        } else if (id == 888) {
            return new TimePickerDialog(this, myTimeListener, hourOfDay, minute, true);
        } else if (id == 777) {
            return new TimePickerDialog(this, myTimeListener2, hourOfDay, minute, true);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int year, int month, int day) {
//             TODO Auto-generated method stub
            pass_day = "" + day;
            pass_month = "" + month;
            pass_year = "" + year;
            showDate(year, month + 1, day);
        }
    };


    private TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            showTimeStart(hourOfDay, minute);
        }
    };

    private TimePickerDialog.OnTimeSetListener myTimeListener2 = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            showTimeEnd(hourOfDay, minute);
        }
    };

    private void showDate(int year, int month, int day) {
        textView.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }

    private void showTimeStart(int hourOfDay, int minute) {
        String formattedMin = minute + "";
        if (hourOfDay >= 12) {
            hourOfDay = hourOfDay - 12;
            startTimeOfDay = "PM";
            hourStart = "" + hourOfDay;
        } else if (hourOfDay == 00) {
            startTimeOfDay = "PM";
            hourOfDay = hourOfDay + 12;
            hourStart = "" + hourOfDay;
        } else {
            startTimeOfDay = "AM";
            hourStart = "" + hourOfDay;
        }

        if (formattedMin.length() < 2) {
            time_start.setText(new StringBuilder().append(hourOfDay).append(":")
                    .append("0").append(minute).append(startTimeOfDay));
            minuteStart = "0" + minute;
        } else {
            time_start.setText(new StringBuilder().append(hourOfDay).append(":")
                    .append(minute).append(startTimeOfDay));
            minuteStart = "" + minute;
        }
    }

    private void showTimeEnd(int hourOfDay, int minute) {
        String formattedMin = minute + "";
        if (hourOfDay >= 12) {
            hourOfDay = hourOfDay - 12;
            endTimeOfDay = "PM";
            hourEnd = "" + hourOfDay;
        } else if (hourOfDay == 00) {
            endTimeOfDay = "PM";
            hourOfDay = hourOfDay + 12;
            hourEnd = "" + hourOfDay;
        } else {
            endTimeOfDay = "AM";
            hourEnd = "" + hourOfDay;
        }

        if (formattedMin.length() < 2) {
            time_end.setText(new StringBuilder().append(hourOfDay).append(":")
                    .append("0").append(minute).append(endTimeOfDay));
            minuteEnd = "0" + minute;
        } else {
            time_end.setText(new StringBuilder().append(hourOfDay).append(":")
                    .append(minute).append(endTimeOfDay));
            minuteEnd = "" + minute;
        }
    }


    class CreateSeminar extends AsyncTask<String, String, String> {

        String seminar_title_str = seminar_title.getText().toString();
        String seminar_fee_str = seminar_fee.getText().toString();
        String seminar_venue_str = seminar_venue.getText().toString();
        String about_str = about.getText().toString();
        String discounted_fee_str = discounted_fee.getText().toString();
        String bonus_str = bonus_points.getText().toString();
        String point_cost_str = point_cost.getText().toString();

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(AddSeminarActivity.this);
            pDialog.setMessage("Adding Seminar . . .");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        protected String doInBackground(String... args) {
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("seminar_title", seminar_title_str));
                params.add(new BasicNameValuePair("seminar_month", pass_month));
                params.add(new BasicNameValuePair("seminar_day", pass_day));
                params.add(new BasicNameValuePair("seminar_year", pass_year));
                params.add(new BasicNameValuePair("seminar_start_time_hour", hourStart));
                params.add(new BasicNameValuePair("seminar_start_time_minute", minuteStart));
                params.add(new BasicNameValuePair("seminar_start_time_of_day", startTimeOfDay));
                params.add(new BasicNameValuePair("seminar_end_time_hour", hourEnd));
                params.add(new BasicNameValuePair("seminar_end_time_minute", minuteEnd));
                params.add(new BasicNameValuePair("seminar_end_time_of_day", endTimeOfDay));
                params.add(new BasicNameValuePair("bonus_points", bonus_str));
                params.add(new BasicNameValuePair("seminar_fee", seminar_fee_str));
                params.add(new BasicNameValuePair("discounted_fee", discounted_fee_str));
                params.add(new BasicNameValuePair("points_cost", point_cost_str));
                params.add(new BasicNameValuePair("venue", seminar_venue_str));
//               params.add(new BasicNameValuePair("facilitator_name", ));
                params.add(new BasicNameValuePair("about", about_str));

                Log.e("to be passed", params.toString());

                JSONObject json = jsonParser.makeHttpRequest(url_create_seminar,
                        "POST", params);

                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    finish();
                } else {
                    // failed to create product
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
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
