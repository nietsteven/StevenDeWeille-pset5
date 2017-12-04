package com.example.stevendeweille_pset5;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Show the menu with all dishes from a category
 */
public class MenuFragment extends ListFragment {
    public ArrayList<String> DISHES = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    HashMap info = new HashMap();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve category
        Bundle arguments = this.getArguments();
        final String category = arguments.getString("category");

        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String url = "https://resto.mprog.nl/menu";
        adapter = new ArrayAdapter<String>(
                getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, DISHES);

        this.setListAdapter(adapter);

        // Find all dishes
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray items = response.getJSONArray("items");
                            for (int i=0; i<items.length(); i++) {
                                JSONObject dish = items.getJSONObject(i);
                                if (dish.getString("category").equals(category.toLowerCase())) {
                                    DISHES.add(dish.getString("name"));
                                    info.put(dish.getString("name"),dish.getDouble("price"));
                                }
                            }
                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            System.out.println("Oops. Something went wrong.");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Error. No internet connection found.");
            }
        }

        );
        queue.add(jsonRequest);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    // Add item to order when clicked
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        RestoDatabase db = RestoDatabase.getInstance(getContext());
        String name = l.getItemAtPosition(position).toString();
        Double price = (Double) info.get(name);
        db.addItem(name, price);
        Toast.makeText(getContext(), name + " has been added to your order", Toast.LENGTH_SHORT).show();
    }
}
