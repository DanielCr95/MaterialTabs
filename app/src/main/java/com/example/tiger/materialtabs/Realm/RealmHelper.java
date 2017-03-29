package com.example.tiger.materialtabs.Realm;


import com.example.tiger.materialtabs.models.Post;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;

/**
 * Created by Tiger on 3/22/2017.
 */

public class RealmHelper {

    Realm realm;
    Boolean saved;
    RealmResults<Post> posts;

    public RealmHelper(Realm realm) {
        this.realm = realm;
    }

    //write
    public boolean save(final Post post) {
        if (post == null) {
            saved = false;
        } else {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    try {
                        Post p = realm.copyToRealm(post);
                        saved = true;
                    }catch (RealmException e)
                    {
                        e.printStackTrace();
                        saved=false;
                    }
                }
            });

        }
        return saved;
    }

    //read
    public void retrieveFromDB() {

        posts = realm.where(Post.class).findAll();
    }
    public ArrayList<Post> justRefresh() {

        ArrayList<Post> latest= new ArrayList<>();
        for(Post p : posts) {
            latest.add(p);

        }

        return latest;
    }



}
