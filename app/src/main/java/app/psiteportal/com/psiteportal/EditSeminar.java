package app.psiteportal.com.psiteportal;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import app.psiteportal.com.utils.JSONParser;

public class EditSeminar extends AppCompatActivity {

    String sid;
    int day, month, year, hourOfDay, minute;
    private Calendar calendar;
    String hourStart, hourEnd, startTimeOfDay, endTimeOfDay, minuteStart, minuteEnd;
    TextView seminar_date, time_start, time_end;
    EditText seminar_title;
    EditText seminar_fee;
    EditText discounted_fee;
    EditText seminar_venue;
    EditText about;
    EditText bonus_points;
    EditText point_cost;
    Button add_seminar;
    Button cancel;
    ImageButton dateToActivate, select_time_start, select_time_end;
    String pass_day, pass_month, pass_year;
    ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_SEMINARS = "seminars";
    private static final String TAG_SID = "sid";
    private static final String TAG_PID = "pid";
    private static final String TAG_TITLE = "seminar_title";
    private static final String TAG_SEMINAR_MONTH = "seminar_month";
    private static final String TAG_SEMINAR_DAY = "seminar_day";
    private static final String TAG_SEMINAR_YEAR = "seminar_year";
    private static final String TAG_TIME_START_HOUR = "seminar_start_time_hour";
    private static final String TAG_TIME_START_MINUTE = "seminar_start_time_minute";
    private static final String TAG_START_TIME_OF_DAY = "seminar_start_time_of_day";
    private static final String TAG_TIME_END_HOUR = "seminar_end_time_hour";
    private static final String TAG_TIME_END_MINUTE = "seminar_end_time_minute";
    private static final String TAG_END_TIME_OF_DAY = "seminar_end_time_of_day";
    private static final String TAG_BONUS_POINTS = "bonus_points";
    private static final String TAG_SEMINAR_FEE = "seminar_fee";
    private static final String TAG_DISCOUNTED_FEE = "discounted_fee";
    private static final String TAG_POINT_COST = "points_cost";
    private static final String TAG_VENUE = "venue";
    private static final String TAG_FACILITATOR = "facilitator_name";
    private static final String TAG_ABOUT = "about";
    private static final String TAG_IS_ACTIVE = "isActive";
    private static final String TAG_OUT_ACTIVATED = "out_activated";

    JSONArray seminars = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_seminar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            sid = extras.getString("sid");
        }

        dateToActivate = (ImageButton) findViewById(R.id.SelectDate);
        seminar_date = (TextView) findViewById(R.id.selectedDate);
        select_time_start = (ImageButton) findViewById(R.id.select_start_time);
        select_time_end = (ImageButton) findViewById(R.id.select_end_time);
        time_start = (TextView) findViewById(R.id.start_time);
        time_end = (TextView) findViewById(R.id.end_time);
        seminar_title = (EditText) findViewById(R.id.seminar_title);
        seminar_fee = (EditText) findViewById(R.id.edit_seminar_fee);
        about = (EditText) findViewById(R.id.edit_about);
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

        new GetSeminarTask().execute();

        add_seminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new UpdateSeminarTask().execute();
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
        seminar_date.setText(new StringBuilder().append(day).append("/")
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


    class UpdateSeminarTask extends AsyncTask<String, String, String> {

        String seminar_title_str = seminar_title.getText().toString();
        String date_contents = seminar_date.getText().toString();
        String seminar_fee_str = seminar_fee.getText().toString();
        String seminar_venue_str = seminar_venue.getText().toString();
        String about_str = about.getText().toString();
        String discounted_fee_str = discounted_fee.getText().toString();
        String bonus_str = bonus_points.getText().toString();
        String point_cost_str = point_cost.getText().toString();
        String time_start_contents = time_start.getText().toString();
        String time_end_contents = time_end.getText().toString();


        String[] resultArr = date_contents.split("/");

        String sem_day = resultArr[0];
        String sem_month = resultArr[1];
        String sem_year = resultArr[2];

        String[] resultArr2 = time_start_contents.split(":");
        String str_start_hour = resultArr2[0];
        String str_start_min = resultArr2[1].substring(0, 2);
        String str_start_timeofday = resultArr2[1].substring(2,4);

        String[] resultArr3 = time_end_contents.split(":");
        String str_end_hour = resultArr3[0];
        String str_end_min = resultArr3[1].substring(0, 2);
        String str_end_timeofday = resultArr3[1].substring(2,4);

        Resources res = getResources();

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(EditSeminar.this);
            pDialog.setMessage("Updating Seminar . . .");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        protected String doInBackground(String... args) {
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("sid", sid));
                params.add(new BasicNameValuePair("seminar_title", seminar_title_str));
                params.add(new BasicNameValuePair("seminar_month", sem_month));
                params.add(new BasicNameValuePair("seminar_day", sem_day));
                params.add(new BasicNameValuePair("seminar_year", sem_year));
                params.add(new BasicNameValuePair("seminar_start_time_hour", str_start_hour));
                params.add(new BasicNameValuePair("seminar_start_time_minute", str_start_min));
                params.add(new BasicNameValuePair("seminar_start_time_of_day", str_start_timeofday));
                params.add(new BasicNameValuePair("seminar_end_time_hour", str_end_hour));
                params.add(new BasicNameValuePair("seminar_end_time_minute", str_end_min));
                params.add(new BasicNameValuePair("seminar_end_time_of_day", str_end_timeofday));
                params.add(new BasicNameValuePair("bonus_points", bonus_str));
                params.add(new BasicNameValuePair("seminar_fee", seminar_fee_str));
                params.add(new BasicNameValuePair("discounted_fee", discounted_fee_str));
                params.add(new BasicNameValuePair("points_cost", point_cost_str));
                params.add(new BasicNameValuePair("venue", seminar_venue_str));
//               params.add(new BasicNameValuePair("facilitator_name", ));
                params.add(new BasicNameValuePair("about", about_str));

                Log.e("to be passed", params.toString());

                JSONObject json = jsonParser.makeHttpRequest(res.getString(R.string.update_seminar_url),
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



    private class GetSeminarTask extends AsyncTask<String, String, String> {

        String id;
        String title;
        String month;
        String day;
        String year;
        String startTime_hour;
        String startTime_mins;
        String startTime_of_day;
        String endTime_hour;
        String endTime_mins;
        String endTime_of_day;
        String bonus_points_str;
        String seminar_fee_str;
        String discounted_fee_str;
        String point_cost_str;
        String venue;
        String facilitator;
        String about_str;
        String isActive;
        String out_activated;

        JSONParser jsonParser = new JSONParser();
        Resources res = getResources();
        String GET_SEMINAR_URL = res.getString(R.string.get_seminar_url);


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditSeminar.this);
            pDialog.setTitle("Getting seminar details . . .");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("sid", sid));

            JSONObject json = jsonParser.makeHttpRequest(
                    GET_SEMINAR_URL, "POST", params);

            try {
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("get seminars success!", json.toString());
                    seminars = json.getJSONArray(TAG_SEMINARS);

                    for (int i = 0; i < seminars.length(); i++) {
                        JSONObject c = seminars.getJSONObject(i);

                        id = c.getString(TAG_SID);
                        title = c.getString(TAG_TITLE);
                        month = c.getString(TAG_SEMINAR_MONTH);
                        day = c.getString(TAG_SEMINAR_DAY);
                        year = c.getString(TAG_SEMINAR_YEAR);
                        startTime_hour = c.getString(TAG_TIME_START_HOUR);
                        startTime_mins = c.getString(TAG_TIME_START_MINUTE);
                        startTime_of_day = c.getString(TAG_START_TIME_OF_DAY);
                        endTime_hour = c.getString(TAG_TIME_END_HOUR);
                        endTime_mins = c.getString(TAG_TIME_END_MINUTE);
                        endTime_of_day = c.getString(TAG_END_TIME_OF_DAY);
                        bonus_points_str = c.getString(TAG_BONUS_POINTS);
                        seminar_fee_str = c.getString(TAG_SEMINAR_FEE);
                        discounted_fee_str = c.getString(TAG_DISCOUNTED_FEE);
                        point_cost_str = c.getString(TAG_POINT_COST);
                        venue = c.getString(TAG_VENUE);
                        facilitator = c.getString(TAG_FACILITATOR);
                        about_str = c.getString(TAG_ABOUT);
                        isActive = c.getString(TAG_IS_ACTIVE);
                        out_activated = c.getString(TAG_OUT_ACTIVATED);

                        Log.e("get title potaaa", title);

                    }

                    Log.e("get seminar potaaa", json.toString());

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
                Toast.makeText(EditSeminar.this, file_url, Toast.LENGTH_SHORT).show();
            }

            seminar_title.setText(title);
            seminar_date.setText(day +"/"+ month +"/"+ year);
            time_start.setText(startTime_hour +":" + startTime_mins + startTime_of_day);
            time_end.setText(endTime_hour +":"+ endTime_mins + endTime_of_day);
            bonus_points.setText(bonus_points_str);
            seminar_fee.setText(seminar_fee_str);
            discounted_fee.setText(discounted_fee_str);
            point_cost.setText(point_cost_str);
            seminar_venue.setText(venue);
            about.setText(about_str);


        }
    }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.seminar_panel_menu, menu);
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