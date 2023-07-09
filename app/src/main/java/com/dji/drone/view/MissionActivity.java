package com.dji.drone.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.dji.drone.R;
import com.dji.drone.view.fragment.MapFragment;
import com.dji.drone.view.fragment.MissionConfigFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MissionActivity extends AppCompatActivity {

    private BottomNavigationView navigation;
    private FragmentManager fragmentManager;
    private Fragment mapFragment;
    private Fragment missionConfigFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission);

        initUI();
        initData();
        initListener();

        replaceFragment(mapFragment);
    }

    private void initUI(){
        navigation = findViewById(R.id.navigation);
    }
    private void initData(){
        mapFragment = new MapFragment();
        missionConfigFragment = new MissionConfigFragment();
        fragmentManager = getSupportFragmentManager();
    }
    private void initListener(){
        navigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.mapItem){
                    replaceFragment(mapFragment);
                }
                if(item.getItemId() == R.id.missionItem){
                    replaceFragment(missionConfigFragment);
                }
                return true;
            }
        });
    }
    private void replaceFragment(Fragment fragment){
        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit();
    }
}