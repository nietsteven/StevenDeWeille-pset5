package com.example.stevendeweille_pset5;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class OrderFragment extends DialogFragment implements View.OnClickListener {
    RestoDatabase db;
    RestoAdapter adapter;
    ListView orderlist;
    TextView totalPrice;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel_order:
                db.clear();
                // 'Close' fragment and return to main screen
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.remove(this);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                ft.commit();
                break;
            case R.id.send_order:
                placeOrder();
                break;
        }
    }

    public void placeOrder() {
        String url = "https://resto.mprog.nl/order";
        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String time = jsonObject.getString("preparation_time");
                            Toast.makeText(getContext(), "Estimated preparation time: " + time + " minutes.",
                                    Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            System.out.println("Oops. Something went wrong.");
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("No internet connection found.");
                    }
        }
                );
        queue.add(request);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        db = RestoDatabase.getInstance(getContext());
        Cursor cursor = db.selectAll();
        adapter = new RestoAdapter(getContext(), cursor);
        orderlist.setAdapter(adapter);

        // Calculate total price
        double total = 0.0;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            total += cursor.getDouble(cursor.getColumnIndex("price")) *
                    cursor.getInt(cursor.getColumnIndex("amount"));
            cursor.moveToNext();
        }
        totalPrice.setText("Total Price: €"+total);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        orderlist = (ListView) view.findViewById(R.id.orderlist);
        totalPrice = (TextView) view.findViewById(R.id.totalPrice);

        // Add listener to cancel and place order buttons
        Button cancelOrder = view.findViewById(R.id.cancel_order);
        Button sendOrder = view.findViewById(R.id.send_order);
        cancelOrder.setOnClickListener(this);
        sendOrder.setOnClickListener(this);
        return view;
    }

}
