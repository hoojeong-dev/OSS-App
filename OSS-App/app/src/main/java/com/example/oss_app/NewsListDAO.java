package com.example.oss_app;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NewsListDAO {

    static List<NewsListModel> allmodels = new ArrayList<>();
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    NewsListModel newsListModel;
    //String[] categories = {"Society", "Sports", "Politics", "Economic", "Foreign", "Culture", "Entertain", "Digital", "Editorial", "Press"};
    String[] categories = {"Society", "Sports"};
    int count = -1;

    public void LoadData() {
        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference = firebaseDatabase.getReference("news");
        databaseReference.addListenerForSingleValueEvent(postListener);
    }

    ValueEventListener postListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            allmodels.clear();

            for(int i=0; i<2; i++){
                for (DataSnapshot snapshot : dataSnapshot.child(categories[i]).getChildren()) {
                    String key = snapshot.getKey();
                    newsListModel = snapshot.getValue(NewsListModel.class);
                    newsListModel.key = key;
                    newsListModel.category = categories[i];

                    allmodels.add(newsListModel);
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.w("MainActivity", "loadPost:onCancelled", databaseError.toException());
        }
    };
}
