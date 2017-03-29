package com.example.tiger.materialtabs.fragments;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.tiger.materialtabs.R;
import com.example.tiger.materialtabs.Realm.RealmHelper;
import com.example.tiger.materialtabs.activity.MainActivity;
import com.example.tiger.materialtabs.adapters.PostAdapter;
import com.example.tiger.materialtabs.models.Post;
import com.example.tiger.materialtabs.receiver.ConnectivityReceiver;
import com.example.tiger.materialtabs.utils.AppController;
import com.example.tiger.materialtabs.utils.Constants;

import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmChangeListener;


public class OneFragment extends Fragment implements ConnectivityReceiver.ConnectivityReceiverListener,
        SwipeRefreshLayout.OnRefreshListener{
    public CoordinatorLayout coordinatorLayout;
    public boolean isConnected;
    public static final String NA = "NA";
    public RecyclerView recycler_post;
    public PostAdapter adapter;

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        super.onPrepareOptionsMenu(menu);
    }



    NavigationView mNavigationView;
    ActionBar mActionBar;
    private Toolbar toolbar;
    private Toolbar searchToolbar;
    private boolean isSearch = false;
    ArrayList<Post> post_array = new ArrayList<>();
    public SwipeRefreshLayout swipeRefreshLayout;
    Realm realm;
    ArrayList<String> array;
    RealmChangeListener realmChangeListener;

    public OneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_one,null);
       // toolbar = (Toolbar) v.findViewById(R.id.toolbar_viewpager);
       // searchToolbar = (Toolbar) v.findViewById(R.id.toolbar_search);
        coordinatorLayout = (CoordinatorLayout) v.findViewById(R.id.coordinatorLayout);
        recycler_post = (RecyclerView) v.findViewById(R.id.recycler_post);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        recycler_post.setLayoutManager(layoutManager);
        recycler_post.setItemAnimator(new DefaultItemAnimator());

        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorRed);




        try {
            getData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }
    public void getData() throws Exception {
        if (checkConnectivity()){
            try {
                swipeRefreshLayout.setRefreshing(true);
                getAllPosts();
            } catch (Exception e) {
                e.printStackTrace();

            }
        }else {

            getAllPosts();
            showSnack();

        }
    }

    public boolean checkConnectivity() {
        return ConnectivityReceiver.isConnected();
    }

    public void showSnack() {

        Snackbar.make(coordinatorLayout, getString(R.string.no_internet_connected), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.settings), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                    }
                }).setActionTextColor(Color.RED)
                .show();
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(isSearch ? R.menu.menu_search_toolbar : R.menu.menu_main, menu);
        if (isSearch) {
            //Toast.makeText(getApplicationContext(), "Search " + isSearch, Toast.LENGTH_SHORT).show();
            final SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();
            search.setIconified(false);
            search.setQueryHint("Search item...");
            search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    try {
                        adapter.getFilter().filter(s);
                    }
                    catch (NullPointerException e)
                    {
                        e.printStackTrace();
                    }
                    return true;
                }
            });
            search.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                   // closeSearch();
                    return true;
                }
            });
        }
        super.onCreateOptionsMenu(menu,inflater);
    }


    @Override
    public void onResume() {
        super.onResume();
        Toast.makeText(getContext(),"u have resumed the app",Toast.LENGTH_SHORT).show();
        AppController.getInstance().setConnectivityReceiver(this);
    }


    @Override
    public void onRefresh() {
        try {
            Toast.makeText(getContext(),"u have refreshed the app",Toast.LENGTH_SHORT).show();

            //when u swipe the app..the getdata method is invoked !
            getData();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onPause() {
        super.onPause();
        Toast.makeText(getContext(),"u have paused the app",Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onNetworkChange(boolean inConnected) {
        this.isConnected = inConnected;
        Toast.makeText(getContext(),"the app network have been changed",Toast.LENGTH_SHORT).show();

    }
    public void getAllPosts() throws Exception{
        String TAG = "POSTS";
        String url = Constants.POSTS_URL;
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("response", response);
                parseJson(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", error.getMessage());
            }
        });


        AppController.getInstance().addToRequestQueue(jsonObjectRequest, TAG);

    }
    public void parseJson(String response){

        try {
            RealmHelper realmHelper = new RealmHelper(realm);

            JSONObject jsonObject = new JSONObject(response);
            JSONArray array = jsonObject.getJSONArray("posts");
            post_array.clear();
            Post p;
            for(int i=0 ; i<array.length() ; i++)
            {
                JSONObject o = array.getJSONObject(i);
                String container= o.getString("excerpt");
                if(container.contains("uploads"))
                {
                    String[] splited = container.split(":");
                    String x = splited[1];
                    String[] y = x.split("jpg");
                    String z = y[0];
                    String output = "https:" + z + "jpg";

                    String id=o.getString("id");
                    String url=o.getString("url");

                    p = new Post();
                    p.setId(id);
                    p.setUrl(url);
                    p.setImage(output);
                    post_array.add(p);
                    //realmHelper.save(p);
                }
                else
                {
                    String id=o.getString("id");
                    String url=o.getString("url");
                    p = new Post();
                    p.setId(id);
                    p.setUrl(url);
                    p.setImage("Empty");
                    post_array.add(p);
                    // realmHelper.save(p);

                }
            }

            adapter = new PostAdapter(getContext(), post_array);
            recycler_post.setAdapter(adapter);
            swipeRefreshLayout.setRefreshing(false);

        } catch (JSONException e) {
            swipeRefreshLayout.setRefreshing(false);
            e.printStackTrace();
        }
    }




}
