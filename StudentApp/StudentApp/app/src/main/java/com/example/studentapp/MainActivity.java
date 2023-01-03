package com.example.studentapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.studentapp.databinding.ActivityMainBinding;
import com.example.studentapp.db.ApiInterface;
import com.example.studentapp.db_local.MyDBManager;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    ApiInterface apiInterface;
    public static MyDBManager myDBManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDBManager = new MyDBManager(this);
        myDBManager.openDB();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.calendarFragment, R.id.listFragment, R.id.anotherFragment)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.bottomNavMenu, navController);


    }

    @Override
    public boolean onSupportNavigateUp() {
        return Navigation.findNavController(this, R.id.nav_host_fragment).navigateUp();
    }
}