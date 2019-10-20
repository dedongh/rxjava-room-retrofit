package com.engineerskasa.rxj.Activity.InstantSearch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.engineerskasa.rxj.R;

public class InstantSearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instant_search);
    }

    public void launchLocalSearch(View view) {
        startActivity(new Intent(InstantSearchActivity.this, LocalSearchActivity.class));
    }

    public void launchRemoteSearch(View view) {
        startActivity(new Intent(InstantSearchActivity.this, RemoteSearchActivity.class));
    }
}
