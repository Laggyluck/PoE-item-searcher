package com.example.zaliczenie;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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


public class MainActivity extends AppCompatActivity {
    // Declarations
    // Texts:
    TextView userHint,
            itemsList;
    EditText searchItem;
    // Strings:
    String url,
            itemsName,
            itemsOwner,
            itemLeague,
            note,
            league;
    // Buttons:
    Button searchButton,
            searchAgainButton;
    // Others:
    RequestQueue queue;
    int ilvl;
    CheckBox standardLeagueCheckBox,
            hardcoreLeagueCheckBox;
    ArrayList<String> leagueArray;
    boolean foundItem;


    public void apiCall(final View view) {
        // Clearing past results
        itemsList.setText(null);
        // Checking what league we want
        setLeague();
        // Making request
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                // Catching response
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Catching value of search item so we know
                        // what's the name of item we look for
                        String search = searchItem.getText().toString();
                        try {
                            // Catching response as JSONArray
                            JSONArray jsonArray = response.getJSONArray("stashes");
                            // Iterating through JSONArray (all stashes) to check
                            // every stash if it contains item we look for
                            for (int i=0; i<jsonArray.length(); i++) {
                                // Object containing current stash
                                JSONObject stash = jsonArray.getJSONObject(i);
                                // Catching value of accountName and league
                                // of this stash owner
                                itemsOwner = stash.getString("accountName");
                                itemLeague = stash.getString("league");
                                // Catching another JSONArray which contains all
                                // items of current stash
                                JSONArray items = stash.getJSONArray("items");
                                // Iterating through JSONArray (all items) to check
                                // if it contains item we look for
                                for (int j=0; j<items.length(); j++) {
                                    // Object containing current item
                                    JSONObject item = items.getJSONObject(j);
                                    // Catching variables: note, ilvl, itemsName
                                    try {
                                        note = item.getString("note");
                                    } catch (org.json.JSONException e) {
                                        note = "unknown";
                                    }
                                    ilvl = item.getInt("ilvl");
                                    itemsName = item.getString("name");
                                    // Checking if it's correct league
                                    if ((itemLeague.equals(league) || league.equals("both"))
                                            && itemsName.equals(search) && !itemsName.equals("")){
                                        // Creating our result
                                        itemsList.append("Item name: " + itemsName + "\n"
                                                + "ilvl: " + ilvl + "\n"
                                                + "note: " + note + "\n"
                                                + "Item owner: " + itemsOwner + "\n"
                                                + "league: " + itemLeague + "\n\n");
                                        foundItem = true;
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // Checking if we got any response
                        // Handling exception when user doesn't check league
                        // or doesn't enter item's name or can't get any response
                        if (searchItem.getText().toString().equals("") && league.equals("")) {
                            Toast.makeText(getApplicationContext(),
                                    "You need to select league and enter item's name!",
                                    Toast.LENGTH_LONG).show();
                            searchAgain(view);
                        } else if (league.equals("")) {
                            Toast.makeText(getApplicationContext(),
                                    "You need to select league!",
                                    Toast.LENGTH_LONG).show();
                            searchAgain(view);
                        } else if (searchItem.getText().toString().equals("")) {
                            Toast.makeText(getApplicationContext(),
                                    "You need to enter item's name!",
                                    Toast.LENGTH_LONG).show();
                            searchAgain(view);
                        } else if (response.isNull(response.toString())) {
                            Toast.makeText(getApplicationContext(),
                                    "Cannot find such item.",
                                    Toast.LENGTH_LONG).show();
                            searchAgain(view);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        // Sending our request
        queue.add(request);

        // Hiding elements and displaying api response:
        searchItem.setVisibility(View.INVISIBLE);
        userHint.setVisibility(View.INVISIBLE);
        searchButton.setVisibility(View.INVISIBLE);
        standardLeagueCheckBox.setVisibility(View.INVISIBLE);
        hardcoreLeagueCheckBox.setVisibility(View.INVISIBLE);
        itemsList.setVisibility(View.VISIBLE);
        searchAgainButton.setVisibility(View.VISIBLE);
    }

    public void searchAgain(View view) {
        // Setting visible all elements we've hidden before and hiding api response
        searchItem.setVisibility(View.VISIBLE);
        userHint.setVisibility(View.VISIBLE);
        searchButton.setVisibility(View.VISIBLE);
        standardLeagueCheckBox.setVisibility(View.VISIBLE);
        hardcoreLeagueCheckBox.setVisibility(View.VISIBLE);
        itemsList.setVisibility(View.INVISIBLE);
        searchAgainButton.setVisibility(View.INVISIBLE);
    }

    // Iterating through ArrayList containing leagues
    // to get league value for league variable
    public void setLeague() {
        if (leagueArray.contains("Hardcore") && leagueArray.contains("Standard")) league = "both";
        else if (leagueArray.contains("Standard")) league = "Standard";
        else if (leagueArray.contains("Hardcore")) league = "Hardcore";
        else league = "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Catching variables:
        queue = Volley.newRequestQueue(this);
        url = "http://api.pathofexile.com/public-stash-tabs";

        searchItem = findViewById(R.id.editTextItem);
        userHint = findViewById(R.id.userHint);
        itemsList = findViewById(R.id.itemsList);

        searchButton = findViewById(R.id.searchButton);
        searchAgainButton = findViewById(R.id.searchAgainButton);

        standardLeagueCheckBox = findViewById(R.id.standardLeagueCheckBox);
        hardcoreLeagueCheckBox = findViewById(R.id.hardcoreLeagueCheckBox);

        leagueArray = new ArrayList<>();
        // Setting league as empty object at the begging
        // so we don't get any result unless we check league we want
        league = "";
        // onClickListener for hardcore league checkbox
        hardcoreLeagueCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hardcoreLeagueCheckBox.isChecked()) leagueArray.add("Hardcore");
                else leagueArray.remove("Hardcore");
            }
        });
        // onClickListener for standard league checkbox
        standardLeagueCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (standardLeagueCheckBox.isChecked()) leagueArray.add("Standard");
                else leagueArray.remove("Standard");
            }
        });
    }
}
