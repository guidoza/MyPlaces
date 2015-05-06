package com.example.gui.myplaces;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    //Menu dashboard
    public void onClickDashboard(View view){
        int id = view.getId();
        if(id == R.id.buttonNuevoSitio) {
            Intent i = new Intent(this, NuevoSitio.class);
            startActivity(i);
        } else if(id == R.id.buttonMisSitios){
            Intent i = new Intent(this, MisSitios.class);
            startActivity(i);
        }
    }


}
