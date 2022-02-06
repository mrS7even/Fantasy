package com.example.fantasy;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    ArrayList <Player> playersArrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<RecyclerViewItem> recyclerViewItems = new ArrayList<>();
    JSOUPAsyncTask jsoupAsyncTask;
    int requiredNumberOfCycles;
    int numbersCompletedCycles;
    ProgressBar progressBar;
    TextView progressBarTextView;
    RelativeLayout relativeLayoutProgressBar;
    Spinner spinner;
    ArrayAdapter spinnerAdapter;
    int selectedAmplua;
    int numbersCompletedRequest;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getArrayListAllPlayersFromJsonRequest("https://www.sports.ru/fantasy/basketball/team/create/150.json", getApplicationContext());

        progressBar = findViewById(R.id.progress_bar);
        progressBarTextView =findViewById(R.id.progress_bar_text_view);
        relativeLayoutProgressBar = findViewById(R.id.relative_layout_progress_bar);

        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        spinner = findViewById(R.id.spinner);
        spinnerAdapter = ArrayAdapter.createFromResource (this, R.array.amplua_array_list, R.layout.spiner_text);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(this);


        adapter = new RecyclerViewAdapter(recyclerViewItems);
        recyclerView.setAdapter(adapter);


    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

        TextView selectedText = (TextView) adapterView.getChildAt(0);
        selectedText.setText(getResources().getStringArray(R.array.abbreviation_amplua_array_list)[position]);

        selectedAmplua = position;                ;
        outputArrayListAllPlayersInRecyclerView(selectedAmplua);


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    class JSOUPAsyncTask extends AsyncTask<Integer, Integer, Void> {

        @Override
        protected void onPreExecute(){

//            progressBar.setVisibility(ProgressBar.VISIBLE);
//            progressBarTextView.setVisibility(TextView.VISIBLE);
            progressBarTextView.setText("0 / " + playersArrayList.size());
            relativeLayoutProgressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected Void doInBackground(Integer... integers) {

            int q = integers[0].intValue();
            int maxSize = q * 50;
            if (q*50>playersArrayList.size()){
                maxSize = playersArrayList.size();
            }


            for (int i=q*50-50;i<maxSize;i++){
                Document doc = null;
                try {
                    doc = Jsoup.newSession().url("https://www.sports.ru/fantasy/basketball/player/info/150/" + playersArrayList.get(i).id + ".html").get();

                    try {
                        playersArrayList.get(i).averagePointsPerMatch = Double.parseDouble(doc.
                                select("table").
                                select("tr").get(4).
                                select("td").first().text());



                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        playersArrayList.get(i).averagePointsPerMatch = 0;
                    }

                    numbersCompletedRequest++;
                    Log.i("progress-numbersCompletedRequest", "progress - " + numbersCompletedRequest);
                    publishProgress(numbersCompletedRequest);


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }





            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {

            int p = progress[0].intValue();
            Log.i("progress", "progress work!");
            Log.i("progress", String.valueOf(p));
            progressBar.setProgress(p);
            progressBarTextView.setText(p + " / " + playersArrayList.size());




        }




        @Override
        protected void onPostExecute(Void result) {

            numbersCompletedCycles++;
            if (numbersCompletedCycles==requiredNumberOfCycles){
                sortingArrayListAllPlayers();
                outputArrayListAllPlayersInRecyclerView(selectedAmplua);
//                progressBar.setVisibility(ProgressBar.INVISIBLE);
//                progressBarTextView.setVisibility(TextView.INVISIBLE);
                relativeLayoutProgressBar.setVisibility(View.INVISIBLE);
            }

        }
    }

    public void onClickButton(View view) {

        requiredNumberOfCycles = (int) Math.ceil((double) playersArrayList.size()/50); // визначається скільки потрібно AsyncTask, якщо групувати запити по 50шт
        requiredNumberOfCycles = 2; // для тестування тільки 1 пакета запитів
        numbersCompletedCycles = 0;
        numbersCompletedRequest = 0;
        progressBar.setMax(playersArrayList.size());

        for(int i=1;i<=requiredNumberOfCycles;i++){

            jsoupAsyncTask = new JSOUPAsyncTask();
            jsoupAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, i);

        }

    }

    //виконується Json-запит для того, щоб стягнути базу всіх гравців і засунути їх в масив playersArrayList
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

    //обрахунки по формулам Кота і сортування масиву по головному індексу
    public void sortingArrayListAllPlayers () {

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

    }

    //наповення RecyclerView гравцями вибраного амплуа
    public void outputArrayListAllPlayersInRecyclerView (int position){

        int number = 0;
        recyclerViewItems.clear();
        for(int i=0;i<playersArrayList.size();i++){

            String amplua;

            switch(playersArrayList.get(i).amplua){

                case 1:
                    amplua = "PG"; //Разыгрывающий защитник
                    break;
                case 2:
                    amplua = "SG"; //Атакующий защитник
                    break;
                case 3:
                    amplua = "SF"; //Легкий форвард
                    break;
                case 4:
                    amplua = "PF"; //Тяжелый форвард
                    break;
                case 5:
                    amplua = "C"; //Центровой
                    break;
                default:
                    amplua = "--"; // don't found
            }

            if (position==0|position==playersArrayList.get(i).amplua){
                number++;
                recyclerViewItems.add(new RecyclerViewItem(
                        String.valueOf(number),// і починається з нуля
                        playersArrayList.get(i).sort_surname,
                        playersArrayList.get(i).club,
                        amplua,
                        String.format("%.2f", playersArrayList.get(i).averagePointsPerMatch),
                        String.format("%.2f", playersArrayList.get(i).indexQuality),
                        String.format("%.2f", playersArrayList.get(i).mainIndex)));
            }


        }

        adapter = new RecyclerViewAdapter(recyclerViewItems);
        recyclerView.setAdapter(adapter);


    }

}