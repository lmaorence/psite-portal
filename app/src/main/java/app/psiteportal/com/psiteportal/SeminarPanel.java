package app.psiteportal.com.psiteportal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import app.psiteportal.com.utils.JSONParser;

/**
 * Created by Lawrence on 10/6/2015.
 */
public class SeminarPanel extends AppCompatActivity{

    TextView seminar_title, seminar_date, seminar_time, seminar_venue, seminar_fee, seminar_about, reward_points, points_cost, discount_fee;
    String sem_id, sem_title, sem_month, sem_day, sem_year, sem_start_hour, sem_start_min,
            sem_end_hour, sem_end_min, sem_bonus_points, sem_fee, sem_discounted, sem_points_cost, sem_venue, sem_facilitator, sem_about,
            isActive, outActivated;
    CheckBox deactivate;
    CheckBox activateOut;
    String deactivate_seminar, activate_out_seminar;
    ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    int success;
    public final String TAG_MESSAGE = "message";
    public final String TAG_SUCCESS = "success";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seminar_panel_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            sem_id = extras.getString("seminar_id");
//            user_id = extras.getString("user_id");
            sem_title = extras.getString("seminar_title");
            sem_month = extras.getString("seminar_month");
            sem_day = extras.getString("seminar_day");
            sem_year = extras.getString("seminar_year");
            sem_start_hour = extras.getString("seminar_start_hour");
            sem_start_min = extras.getString("seminar_start_min");
            sem_end_hour = extras.getString("seminar_end_hour");
            sem_end_min = extras.getString("seminar_end_min");
            sem_bonus_points = extras.getString("seminar_bonus_points");
            sem_fee = extras.getString("seminar_fee");
            sem_discounted = extras.getString("seminar_discounted");
            sem_points_cost = extras.getString("seminar_points_cost");
            sem_venue = extras.getString("seminar_venue");
            sem_facilitator = extras.getString("seminar_facilitator");
            sem_about = extras.getString("seminar_about");
            isActive = extras.getString("seminar_is_active");
            outActivated = extras.getString("seminar_out_activated");
        }

        seminar_title = (TextView) findViewById(R.id.seminar_title);
        seminar_date = (TextView) findViewById(R.id.seminar_date);
        seminar_time = (TextView) findViewById(R.id.seminar_time);
        seminar_venue = (TextView) findViewById(R.id.seminar_venue);
        seminar_about = (TextView) findViewById(R.id.seminar_about);
        seminar_fee = (TextView) findViewById(R.id.seminar_fee);
        reward_points = (TextView) findViewById(R.id.seminar_reward_points);
        seminar_about = (TextView) findViewById(R.id.seminar_about);
        points_cost = (TextView) findViewById(R.id.points_cost);
        discount_fee = (TextView) findViewById(R.id.discounted_fee);

        //switches
        deactivate = (CheckBox) findViewById(R.id.deactivate);
        activateOut = (CheckBox) findViewById(R.id.attendance_out);

        int mon = Integer.parseInt(sem_month);
        String month_name = formatMonth(mon, Locale.getDefault());

        seminar_title.setText(sem_title);
        seminar_date.setText(month_name +" "+ sem_day +", "+ sem_year);
        seminar_time.setText(sem_start_hour +":"+sem_start_min +"AM - "+ sem_end_hour +":"+ sem_end_min +"PM");
        seminar_venue.setText(sem_venue);
        seminar_about.setText(sem_about);
        seminar_fee.setText(sem_fee);
        reward_points.setText(sem_bonus_points);
        points_cost.setText(sem_points_cost);
        discount_fee.setText(sem_discounted);

        //is Seminar active
        if(isActive.equals("1")){
            deactivate.setChecked(false);
        }else{
            deactivate.setChecked(true);
        }

        //attendance
        if(outActivated.equals("0")){
            activateOut.setChecked(false);
        }else{
            activateOut.setChecked(true);
        }

    }

    public void saveBtn(View v){
        //check if seminar is deactivated
        if(deactivate.isChecked()){
            deactivate_seminar  = "0";
        }else{
            deactivate_seminar = "1";
        }

        //check if seminar attendance is activated
        if(activateOut.isChecked()){
            activate_out_seminar = "1";
        }else{
            activate_out_seminar = "0";
        }

        //start asynctask
        new UpdateConfig().execute();

    }

    public void cancelBtn(View v){
        //finish activity
        finish();
    }

    public String formatMonth(int month, Locale locale) {
        DateFormatSymbols symbols = new DateFormatSymbols(locale);
        String[] monthNames = symbols.getMonths();
        return monthNames[month - 1];
    }


    class UpdateConfig extends AsyncTask<String, String, String> {

        JSONParser jsonParser = new JSONParser();
        Resources res = getResources();
        String UPDATE_CONFIG_URL = res.getString(R.string.update_config_url);

//        String dateValue =  deactivate_seminar.getText().toString();
//        String amountValue = amount.getText().toString();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SeminarPanel.this);
            pDialog.setMessage("Uploading transaction. . . ");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            try{
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("sid", sem_id));
                params.add(new BasicNameValuePair("isActive", deactivate_seminar));
                params.add(new BasicNameValuePair("out_activated", activate_out_seminar));

                Log.e("params to be passed", params.toString());

                JSONObject json = jsonParser.makeHttpRequest(UPDATE_CONFIG_URL, "POST", params);

                success = json.getInt(TAG_SUCCESS);
                if(success == 1){
//                    Toast.makeText(AddExpenseActivity.this, json.getString(TAG_MESSAGE), Toast.LENGTH_SHORT).show();
                    finish();
                }else{
//                    Toast.makeText(AddExpenseActivity.this, json.getString(TAG_MESSAGE), Toast.LENGTH_SHORT).show();
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            pDialog.dismiss();
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
            case R.id.action_edit_seminar:
                Intent i = new Intent(this, EditSeminar.class);
                i.putExtra("sid", sem_id);
                startActivity(i);
                return true;
            case R.id.action_seminar_registration:
                Intent intent = new Intent(this, RegisterSeminarScanner.class);
                intent.putExtra("sid", sem_id);
                intent.putExtra("bonus_points", sem_bonus_points);
                intent.putExtra("seminar_fee", sem_fee);
                intent.putExtra("points_cost", sem_points_cost);
                intent.putExtra("discount_fee", sem_discounted);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
