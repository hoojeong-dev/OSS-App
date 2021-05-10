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

    static List<NewsListModel> models = new ArrayList<>();
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    NewsListModel newsListModel;
    //String[] categories = {"Society", "Sports", "Politics", "Economic", "Foreign", "Culture", "Entertain", "Digital", "Editorial", "Press"};
    String[] categories = {"Society", "Sports"};

    public void LoadData(){
        firebaseDatabase = FirebaseDatabase.getInstance();

        for(int i=0; i<2; i++){
            databaseReference = firebaseDatabase.getReference("news").child(categories[i]);
            databaseReference.addListenerForSingleValueEvent(postListener);
        }
    }

    ValueEventListener postListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            models.clear();

            for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                String key = snapshot.getKey();
                newsListModel = snapshot.getValue(NewsListModel.class);
                newsListModel.key = key;

                models.add(newsListModel);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.w("MainActivity", "loadPost:onCancelled", databaseError.toException());
        }
    };
}
