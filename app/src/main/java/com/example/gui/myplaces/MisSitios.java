package com.example.gui.myplaces;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;


public class MisSitios extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_sitios);
        ListView listPlaces = (ListView) findViewById(R.id.placesList);
        //Itmems
        HashMap<String, Object> itemLocal;
        //Lista de items
        ArrayList<HashMap<String, Object>> dataPlaces = new ArrayList<>();

        MySQLOpenHelper helper = new MySQLOpenHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name, image FROM myplaces", null);
        while (cursor.moveToNext()) {
            itemLocal = new HashMap<>();
            String nombre = cursor.getString(0);
            String imagen = cursor.getString(1);

            Bitmap thumbnail = (BitmapFactory.decodeFile(imagen));

            itemLocal.put("nombre", nombre);
            itemLocal.put("imagen", thumbnail);
            //Añadimos cada itemFriend a la lista
            dataPlaces.add(itemLocal);
        }
        cursor.close();
        db.close();

        //Adaptador que establece el layout para cada itemFriend
        SimpleAdapter adapter = new SimpleAdapter(this, dataPlaces, R.layout.list_places_layout,
                new String[]{"nombre", "imagen"},
                new int[]{ R.id.list_scores_title, R.id.list_scores_icon});
        //new String[]{"nombre", "imagen"}, new int[]{
          //      R.id.list_scores_title, R.id.list_scores_title1});

        listPlaces.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mis_sitios, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
