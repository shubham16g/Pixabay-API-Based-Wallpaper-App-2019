package com.samcreators.wakeupwallpaper;


import android.database.Cursor;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class RecentFragment extends Fragment {


    public RecentFragment() {

    }

    private RecyclerView recentRecycler;
    private List<Hit> list = new ArrayList<>();
    private RecyclerAdapter adapter;
    private GridLayoutManager manager;
    private DatabaseHelper database;
    private static final String URL = new Config().getAPI_URL();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        database = new DatabaseHelper(getContext());
        View view = inflater.inflate(R.layout.fragment_recent, container, false);
        recentRecycler = view.findViewById(R.id.recent_recycler);
        list = new ArrayList<>();
        adapter = new RecyclerAdapter(getContext(),R.layout.grid_layout,R.layout.fragment_popular,list);
        manager = new GridLayoutManager(getContext(),GridLayoutManager.chooseSize(2,2,0));
        recentRecycler.setLayoutManager(manager);
        recentRecycler.setAdapter(adapter);

        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        list.clear();
        Cursor c = database.getAllRecent();
        while (c.moveToNext()){
            fetchData(c.getInt(0));
        }
    }
    private void fetchData(int id){
        StringRequest request = new StringRequest(URL + "&id=" + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("CODE",response);
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                ResultFormat result = gson.fromJson(response, ResultFormat.class);
                List<Hit> hit = result.getHits();
                for (int i = 0;i < hit.size(); i++) {
                    list.add(new Hit(hit.get(i).getLargeImageURL(), hit.get(i).getId(), hit.get(i).getTags(), hit.get(i).getWebformatURL(), hit.get(i).getPreviewURL()));
                }
                adapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        });

        RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(getContext()));
        queue.add(request);
    }
}
