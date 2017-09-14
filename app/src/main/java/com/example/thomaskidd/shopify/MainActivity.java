package com.example.thomaskidd.shopify;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    //TextView
    private TextView content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        content = (TextView) findViewById(R.id.content);

        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new GetItems().execute();
            }
        });

    }

    public void updateContent(JSONObject obj) {
        double amountSpent = 0;
        int numBags = 0;


        try {

            JSONArray orders = obj.getJSONArray("orders");

            //check all of the orders
            for (int i=0; i<orders.length(); i++) {
                JSONObject currentOrder = orders.getJSONObject(i);

                if (currentOrder.has("customer")) {
                    JSONObject customer = currentOrder.getJSONObject("customer");

                    //Calculate how much Napoleon has bought
                    if (customer.getString("first_name").equals("Napoleon") && customer.getString("last_name").equals("Batz")) {
                        double total_price = currentOrder.getDouble("total_price");
                        amountSpent += total_price;
                    }

                    //Calculate how many Awesome Bronze Bags have been sold
                    JSONArray line_items = currentOrder.getJSONArray("line_items");

                    for (int j = 0; j < line_items.length(); j++) {
                        if (line_items.getJSONObject(j).getString("title").equals("Awesome Bronze Bag")) {
                            int quantity = line_items.getJSONObject(j).getInt("quantity");
                            numBags += quantity;
                        }
                    }
                }
            }

            content.setText("Amount Spent by Napoleon: $"+amountSpent+"\n\n\nQuantity of Awesome Bronze Bags Sold: "+numBags);
        }

        catch (Exception e) {
            Log.e("Parsing", e.toString());
            content.setText("ASASsa");
        }
    }

    //AsyncTask thread to fetch json currency prices
    private class GetItems extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            //url
            String url = "https://shopicruit.myshopify.com/admin/orders.json?page=1&access_token=c32313df0d0ef512ca64d5b336a0d7c6";

            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //create json object
                            try {
                                JSONObject info = new JSONObject(response);
                                updateContent(info);
                            }
                            catch (Exception e) {
                                content.setText("Looks like there\'s an error on one of \nour servers right now!");
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    content.setText("Looks like there\'s an error on one of \nour servers right now!");
                }
            });
            // Add the request to the RequestQueue.
            queue.add(stringRequest);

            return null;
        }
    }
}
