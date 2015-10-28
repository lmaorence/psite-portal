package app.psiteportal.com.psiteportal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.psiteportal.com.utils.JSONParser;


public class LiquidationFragment extends ListFragment {

    ArrayList<HashMap<String, String>> expensesList;
    JSONArray transactions = null;
    ProgressDialog pDialog;
    final static String TAG_SUCCESS = "success";
    final static String TAG_MESSAGE = "message";
    private static final String TAG_TRANSACTIONS = "transactions";
    private static final String TAG_TRANSACTION_ID = "transaction_id";
    private static final String TAG_AMOUNT = "transaction_amount";
    private static final String TAG_CHECK_NUM = "check_number";
    private static final String TAG_TYPE = "transaction_type";
    private static final String TAG_DATE = "transaction_date";
    private static final String TAG_COLLECTION = "total_collection";
    private static final String TAG_EXPENSES = "total_expenses";
    private static final String TAG_BALANCE = "balance";
    private static final String TAG_ADD = "+";
    private static final String TAG_SUB = "-";
//    private static final String TAG_SUB = "—";
    String type;
    TextView total_collection;
    TextView total_expenses;
    TextView balance;
    String collection, expenses, bal;

    public LiquidationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View rootView = inflater.inflate(R.layout.fragment_liquidation, container, false);

        total_collection = (TextView) rootView.findViewById(R.id.total_collection);
        total_expenses = (TextView) rootView.findViewById(R.id.total_expenses);
        balance = (TextView) rootView.findViewById(R.id.total_balance);

        new GetActiveSeminarsTask().execute();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        expensesList = new ArrayList<HashMap<String, String>>();

        ListView lv = getListView();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String amount = ((TextView) view.findViewById(R.id.amount)).getText()
//                        .toString();
            }
        });

    }


    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.liquidation_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_add_expense:
                startActivity(new Intent(getActivity(), AddExpenseActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private class GetActiveSeminarsTask extends AsyncTask<String, String, String> {

        JSONParser jsonParser = new JSONParser();
        Resources res = getResources();
        String ACTIVE_SEMINARS_URL = res.getString(R.string.transactions_url);
        DecimalFormat formatter = new DecimalFormat("#,###,###");

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setTitle("Getting active seminars . . .");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();

            JSONObject json = jsonParser.makeHttpRequest(
                    ACTIVE_SEMINARS_URL, "GET", params);


            try {
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("get seminars success!", json.toString());
                    transactions = json.getJSONArray(TAG_TRANSACTIONS);

                    for (int i = 0; i < transactions.length(); i++) {
                        JSONObject c = transactions.getJSONObject(i);

                        String id = c.getString(TAG_TRANSACTION_ID);
                        String amount = c.getString(TAG_AMOUNT);

                        String formated_amount = formatter.format(Integer.parseInt(amount));

                        String check_number = c.getString(TAG_CHECK_NUM);
                        type = c.getString(TAG_TYPE);
                        String date = c.getString(TAG_DATE);

                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put(TAG_TRANSACTION_ID, id);
                        map.put(TAG_AMOUNT, formated_amount);
                        map.put(TAG_CHECK_NUM, check_number);
                        if (type.equals("Collection")) {
                            map.put(TAG_TYPE, TAG_ADD);
                        } else {
                            map.put(TAG_TYPE, TAG_SUB);
                        }

                        map.put(TAG_DATE, date);

                        expensesList.add(map);
                    }


                    collection = formatter.format(Integer.parseInt(json.getString(TAG_COLLECTION)));
                    expenses = formatter.format(Integer.parseInt(json.getString(TAG_EXPENSES)));
                    bal = formatter.format(Integer.parseInt(json.getString(TAG_BALANCE)));


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

            total_collection.setText("₱" + collection);
            total_expenses.setText("₱" + expenses);
            balance.setText("₱" + bal);

            if (file_url != null) {
                Toast.makeText(getActivity(), file_url, Toast.LENGTH_SHORT).show();
            }
            getActivity().runOnUiThread(new Runnable() {
                public void run() {

                    ListAdapter adapter = new SimpleAdapter(
                            getActivity(), expensesList,
                            R.layout.expenses_item, new String[]{TAG_TRANSACTION_ID,
                            TAG_AMOUNT, TAG_CHECK_NUM, TAG_TYPE, TAG_DATE},
                            new int[]{R.id.transaction_id, R.id.amount, R.id.cash_num, R.id.type, R.id.date});

                    // updating listview
                    setListAdapter(adapter);

                }
            });

        }

    }

}
