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


/**
 * Show a list with all categories
 */
public class CategoriesFragment extends ListFragment {
    public ArrayList<String> CATEGORIES = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get categories
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String url = "https://resto.mprog.nl/categories";
        adapter = new ArrayAdapter<String>(
                getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, CATEGORIES);

        this.setListAdapter(adapter);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray categories = response.getJSONArray("categories");
                            for (int i=0; i<categories.length(); i++) {
                                CATEGORIES.add(categories.getString(i).toUpperCase());
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
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        // Open menu with all dishes from chosen category
        MenuFragment menuFragment = new MenuFragment();

        Bundle args = new Bundle();
        args.putString("category", l.getItemAtPosition(position).toString());
        menuFragment.setArguments(args);

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, menuFragment)
                .addToBackStack(null)
                .commit();
    }
}
