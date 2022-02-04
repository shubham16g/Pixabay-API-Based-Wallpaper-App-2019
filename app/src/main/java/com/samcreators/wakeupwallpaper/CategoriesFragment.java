package com.samcreators.wakeupwallpaper;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CategoriesFragment extends Fragment {


    public CategoriesFragment() {
    }

    private List<Hit> list;
    private RecyclerView categoriesRecycler;
    private RecyclerAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_categories, container, false);

        list = new ArrayList<>();
        list.add(new Hit("https://cdn.pixabay.com/photo/2019/09/17/18/46/domestic-cat-4484279__340.jpg","ANIMALS"));
        list.add(new Hit("https://cdn.pixabay.com/photo/2019/09/18/17/55/architecture-4487358__340.jpg","ARCHITECTURE"));
        list.add(new Hit("https://cdn.pixabay.com/photo/2019/08/26/12/32/abstract-4431599__340.jpg","TEXTURES"));
        list.add(new Hit("https://cdn.pixabay.com/photo/2019/09/13/17/48/hat-4474522__340.jpg","FASHION"));
        list.add(new Hit("https://cdn.pixabay.com/photo/2018/02/16/10/52/beverage-3157395__340.jpg","BUSINESS"));
        list.add(new Hit("https://cdn.pixabay.com/photo/2019/07/05/06/51/library-4317851__340.jpg","EDUCATION"));
        list.add(new Hit("https://cdn.pixabay.com/photo/2019/09/08/19/13/autumn-4461685__340.jpg","EMOTIONS"));
        list.add(new Hit("https://cdn.pixabay.com/photo/2019/09/19/07/26/coffee-4488464__340.jpg","FOOD+DRINK"));
        list.add(new Hit("https://cdn.pixabay.com/photo/2016/11/10/15/24/runner-1814460__340.jpg","HEALTH"));
        list.add(new Hit("https://cdn.pixabay.com/photo/2019/09/09/21/26/tractor-4464681__340.jpg","INDUSTRY/CRAFT"));
        list.add(new Hit("https://cdn.pixabay.com/photo/2019/09/17/21/32/piano-4484621__340.jpg","MUSIC"));
        list.add(new Hit("https://cdn.pixabay.com/photo/2019/09/19/16/35/landscape-4489716__340.jpg","NATURE"));
        list.add(new Hit("https://cdn.pixabay.com/photo/2019/09/15/18/35/novice-4479081__340.jpg","PEOPLE"));
        list.add(new Hit("https://cdn.pixabay.com/photo/2019/09/19/12/52/eiffel-tower-4489225__340.jpg","PLACES"));
        list.add(new Hit("https://cdn.pixabay.com/photo/2019/08/27/05/04/cross-4433376__340.jpg","RELIGION"));
        list.add(new Hit("https://cdn.pixabay.com/photo/2019/09/15/13/06/aerospace-4478233__340.jpg","SCIENCE"));
        list.add(new Hit("https://cdn.pixabay.com/photo/2019/09/19/07/26/extreme-4488462__340.jpg","SPORTS"));
        list.add(new Hit("https://cdn.pixabay.com/photo/2019/09/09/08/23/internet-4463031__340.jpg","TECHNOLOGY"));
        list.add(new Hit("https://cdn.pixabay.com/photo/2019/09/20/22/30/bmw-4492705__340.jpg","TRANSPORTATION"));
        list.add(new Hit("https://cdn.pixabay.com/photo/2019/09/17/12/54/landscape-4483412__340.jpg","TRAVEL"));
        adapter = new RecyclerAdapter(getContext(),R.layout.categories_frag_layout, R.layout.fragment_categories, list);
        categoriesRecycler = view.findViewById(R.id.categories_frag_recycler);
        categoriesRecycler.setLayoutManager(new GridLayoutManager(getContext(),GridLayoutManager.chooseSize(3,3,0)));
        categoriesRecycler.setAdapter(adapter);
        return view;
    }
}