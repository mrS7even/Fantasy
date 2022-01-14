package com.example.myapplication;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    ArrayList <Player> playersArrayList = new ArrayList<>();
    Document doc;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<RecyclerViewItem> recyclerViewItems = new ArrayList<>();
    JSOUPAsyncTask jsoupAsyncTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getArrayListAllPlayersFromJsonRequest("https://www.sports.ru/fantasy/basketball/team/create/150.json", getApplicationContext());
        Log.i("getAveragePointsPerMatchById", "start on create");


        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

    }

    class JSOUPAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            for(int i=0;i<playersArrayList.size();i++){

                doc = null;

                try {
                    doc = Jsoup.newSession().url("https://www.sports.ru/fantasy/basketball/player/info/150/" + playersArrayList.get(i).id + ".html").get();

                    try {
                        playersArrayList.get(i).averagePointsPerMatch = Double.parseDouble(doc.
                                select("table").
                                select("tr").get(4).
                                select("td").first().text());

                        Log.i("getAveragePointsPerMatchById","GOOOODDD   -" + i + "   -" + playersArrayList.get(i).id  + "   -" + playersArrayList.get(i).averagePointsPerMatch);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        playersArrayList.get(i).averagePointsPerMatch = 0;
                        Log.i("getAveragePointsPerMatchById","ERRRRROOOOOOOOOOOOOOOOOOOORRRRRR   -" + playersArrayList.get(i).id + "   -   " + playersArrayList.get(i).averagePointsPerMatch);
                    }




                } catch (IOException e) {
                    e.printStackTrace();
                }

            }



            for(int i=0;i<playersArrayList.size();i++){

                playersArrayList.get(i).shortPrice = playersArrayList.get(i).start_value / 100; //відкидаємо два останні нулі. так Кот сказав

                if (playersArrayList.get(i).averagePointsPerMatch != 0) {
                    playersArrayList.get(i).quantityOfGame = (int) Math.round(playersArrayList.get(i).points / playersArrayList.get(i).averagePointsPerMatch);
                    playersArrayList.get(i).indexQuality = (float) playersArrayList.get(i).points / playersArrayList.get(i).shortPrice / playersArrayList.get(i).quantityOfGame;
                    playersArrayList.get(i).mainIndex = (float) playersArrayList.get(i).indexQuality * playersArrayList.get(i).averagePointsPerMatch;
                } else {
                    playersArrayList.get(i).quantityOfGame = 0;
                    playersArrayList.get(i).indexQuality = 0;
                    playersArrayList.get(i).mainIndex = 0;
                }

            }

            Collections.sort(playersArrayList, new Comparator<Player>() {
                @Override
                public int compare(Player player1, Player player2) {
                    return Double.compare(player2.mainIndex, player1.mainIndex);
                }
            });

            for(int i=0;i<playersArrayList.size();i++){

                recyclerViewItems.add(new RecyclerViewItem(
                        playersArrayList.get(i).sort_surname,
                        playersArrayList.get(i).club,
                        playersArrayList.get(i).amplua + "",
                        String.format("%.2f", playersArrayList.get(i).averagePointsPerMatch),
                        String.format("%.2f", playersArrayList.get(i).indexQuality),
                        String.format("%.2f", playersArrayList.get(i).mainIndex)));
            }






            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            adapter = new RecyclerViewAdapter(recyclerViewItems);
            recyclerView.setAdapter(adapter);

        }
    }

    public void getArrayListAllPlayersFromJsonRequest (String url, Context context){

        RequestQueue requestQueue = Volley.newRequestQueue(context);


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray array = response.getJSONArray("players");


                            for(int i=0;i<array.length();i++){
                                JSONObject players = array.getJSONObject(i);

                                Player player = new Player(
                                        players.getInt("id"),
                                        players.getInt("amplua"),
                                        players.getString("club"),
                                        players.getInt("start_value"),
                                        players.getInt("points"),
                                        players.getString("name"),
                                        players.getString("sort_surname")
                                );

                                playersArrayList.add(player);

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        // Do something when error occurred

                    }
                }
        );

        // Add JsonObjectRequest to the RequestQueue
        requestQueue.add(jsonObjectRequest);


    }

    public void onClickButton(View view) {

        Log.i("getAveragePointsPerMatchById", "start onClickButton. array size" + playersArrayList.size());



        jsoupAsyncTask = new JSOUPAsyncTask();
        jsoupAsyncTask.execute();

    }


}