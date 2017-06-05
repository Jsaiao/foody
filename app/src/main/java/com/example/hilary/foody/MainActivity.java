package com.example.hilary.foody;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hilary.foody.adapters.MyFirebaseAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String SAVED_ADAPTER_ITEMS = "SAVED_ADAPTER_ITEMS";
    private final static String SAVED_ADAPTER_KEYS = "SAVED_ADAPTER_KEYS";
    TextView title;
    TextView price;
    TextView description;
    ImageView foodImage;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mFoodReference = mRootRef.child("foods");
    Query mQuery;
    ArrayList<Food> mAdapterItems;
    ArrayList<String> mAdapterKeys;
    RecyclerView mRecyclerView;
    MyFirebaseAdapter mMyAdapter;
    View mProgressBar;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser!= null){
            title = (TextView) findViewById(R.id.title);
            description = (TextView) findViewById(R.id.description);
            mProgressBar = findViewById(R.id.progressBar);

            handleInstanceState(savedInstanceState);
            setupFirebase();
            setupRecyclerview();
        } else {
            Intent i = new Intent(MainActivity.this, SignUpAcitivity.class);
            startActivity(i);
        }

    }




    private void setupFirebase() {
        mQuery = mFoodReference.limitToLast(10);
        mQuery.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mProgressBar.setVisibility(View.GONE); //Remove progress bar when data has been fully downloaded
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }
    private void handleInstanceState(Bundle savedInstanceState) {

        if (savedInstanceState != null &&
                savedInstanceState.containsKey(SAVED_ADAPTER_ITEMS) &&
                savedInstanceState.containsKey(SAVED_ADAPTER_KEYS)) {
            mAdapterItems = Parcels.unwrap(savedInstanceState.getParcelable(SAVED_ADAPTER_ITEMS));
            mAdapterKeys = savedInstanceState.getStringArrayList(SAVED_ADAPTER_KEYS);
        } else {
            mAdapterItems = new ArrayList<Food>();
            mAdapterKeys = new ArrayList<String>();
        }
    }
    private void setupRecyclerview() {
        mRecyclerView = (RecyclerView) findViewById(R.id.foodListRecyclerView);
        mMyAdapter = new MyFirebaseAdapter(mQuery, Food.class, mAdapterItems, mAdapterKeys);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mMyAdapter);
    };





    @Override
    public void onDestroy() {
        super.onDestroy();
        mMyAdapter.destroy();

    }
}
