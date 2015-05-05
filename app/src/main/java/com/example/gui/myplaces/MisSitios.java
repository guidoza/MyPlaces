package com.example.gui.myplaces;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;


public class MisSitios extends ActionBarActivity {

    private Bitmap foto;
    String[] data;
    private String[] opcionesMenu;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    ActionBarDrawerToggle drawerToggle;
    private ArrayList<Categoria> listaCategorias;
    ListView listPlaces;
    ArrayList<HashMap<String, Object>> dataPlaces;
    ArrayList<HashMap<String, Object>> dataPlacesAux;
    SimpleAdapter adapter;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_sitios);
        listPlaces = (ListView) findViewById(R.id.placesList);



        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView parent,
                                    View view, int position, long id){
                String cat = parent.getItemAtPosition(position).toString();
                dataPlaces.clear();
                for(int i=0; i<dataPlacesAux.size(); i++){
                HashMap<String, Object> aux = new HashMap<>();
                aux.put("nombre", dataPlacesAux.get(i).get("nombre"));
                aux.put("imagen", dataPlacesAux.get(i).get("imagen"));
                dataPlaces.add(aux);
                }
                if(cat.equals("Todos")){
                    listPlaces.setAdapter(adapter);
                } else {
                    MySQLOpenHelper helper = new MySQLOpenHelper(getApplicationContext());
                    SQLiteDatabase db = helper.getReadableDatabase();
                    Iterator<HashMap<String, Object>> iter = dataPlaces.iterator();
                    while(iter.hasNext()){
                        String nombre = iter.next().get("nombre").toString();
                        Cursor cursor = db.rawQuery("SELECT categoria FROM myplaces WHERE name='"+nombre+"'", null);
                        while (cursor.moveToNext()) {
                            if(!cursor.getString(0).equals(cat)){
                                iter.remove();
                            }
                        }
                        cursor.close();
                    }
                    db.close();
                    listPlaces.setAdapter(adapter);
                }
                drawerList.setItemChecked(position, true);
                drawerLayout.closeDrawer(drawerList);
            }
        });


        final CharSequence tituloApp = getTitle();
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.categoria,
                R.string.title_activity_mis_sitios){
            public void onDrawerClosed(View view){
                getSupportActionBar().setTitle(tituloApp);
                ActivityCompat.invalidateOptionsMenu(MisSitios.this);
            }
            public void onDrawerOpened(View drawerView){
                getSupportActionBar().setTitle("Selecciona una categoría");
                ActivityCompat.invalidateOptionsMenu(MisSitios.this);

            }
        };
        drawerToggle.setDrawerIndicatorEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerLayout.setDrawerListener(drawerToggle);



        listPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                data = new String[6];
                String selected = ((TextView) view.findViewById(R.id.list_scores_title)).getText().toString();
                Log.d("FFFFFF",selected);
                MySQLOpenHelper helper = new MySQLOpenHelper(getApplicationContext());
                SQLiteDatabase db = helper.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT latitud, longitud, name, description, image, categoria FROM myplaces WHERE name='"+selected+"'", null);
                while (cursor.moveToNext()) {
                data[0] = String.valueOf(cursor.getDouble(0));
                data[1] = String.valueOf(cursor.getDouble(1));
                data[2]= cursor.getString(2);
                data[3] = cursor.getString(3);
                data[4]= cursor.getString(4);
                data[5]= cursor.getString(5);
                }
                cursor.close();
                db.close();

                Intent i = new Intent (getApplicationContext(), Sitio.class);
                i.putExtra("data", data);
                startActivity(i);
            }
        });

        listPlaces.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int pos, long id) {
                final CharSequence[] options = { "Eliminar este sitio", "Cancelar" };
                final String[] args = new String[]{dataPlaces.get(pos).get("nombre").toString()};
                AlertDialog.Builder builder = new AlertDialog.Builder(MisSitios.this);
                builder.setTitle("Opciones de sitio:");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Eliminar este sitio")){

                            dataPlaces.remove(pos);
                            dataPlacesAux.remove(pos);
                            MySQLOpenHelper helper = new MySQLOpenHelper(getApplicationContext());
                            SQLiteDatabase db = helper.getReadableDatabase();
                            db.execSQL("DELETE FROM myplaces WHERE name=?", args);
                            db.close();
                            Log.d("WWWWWWw","Despues de borrar");
                            listPlaces.setAdapter(adapter);

                        }else if (options[item].equals("Cancelar")) {

                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
                return true;
            }
        });

        //Itmems
        HashMap<String, Object> itemLocal;
        //Lista de items
        dataPlaces = new ArrayList<>();

        MySQLOpenHelper helper = new MySQLOpenHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name, image FROM myplaces", null);
        while (cursor.moveToNext()) {
            itemLocal = new HashMap<>();
            String nombre = cursor.getString(0);
            String imagenPath = cursor.getString(1);

            if(!imagenPath.equals("null")) {
                File image = new File(imagenPath);
                if(image.exists()){
                    Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
                    foto = Bitmap.createScaledBitmap(bitmap, ((int) convertDpToPixel(140, this)), ((int) convertDpToPixel(110, this)), true);

                }


            } else {
                foto = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_places);
            }

            itemLocal.put("nombre", nombre);
            itemLocal.put("imagen", foto);
            //Añadimos cada itemFriend a la lista
            dataPlaces.add(itemLocal);
        }
        cursor.close();
        db.close();

        dataPlacesAux = new ArrayList<>();
        for(int i=0; i<dataPlaces.size(); i++){
            HashMap<String, Object> aux = new HashMap<>();
            aux.put("nombre", dataPlaces.get(i).get("nombre"));
            aux.put("imagen", dataPlaces.get(i).get("imagen"));
            dataPlacesAux.add(aux);
        }


        //Adaptador que establece el layout para cada itemFriend
        adapter = new SimpleAdapter(this, dataPlaces, R.layout.list_places_layout,
                new String[]{"nombre", "imagen"},
                new int[]{ R.id.list_scores_title, R.id.list_scores_icon});
        //new String[]{"nombre", "imagen"}, new int[]{
          //      R.id.list_scores_title, R.id.list_scores_title1});

        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {

            @Override
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                if ((view instanceof ImageView) & (data instanceof Bitmap)) {
                    ImageView iv = (ImageView) view;
                    Bitmap bm = (Bitmap) data;
                    iv.setImageBitmap(bm);
                    return true;
                }
                return false;

            }

        });

        listPlaces.setAdapter(adapter);
    }
    @Override
    protected void onResume() {
        super.onResume();
        listaCategorias = new ArrayList<>();
        listaCategorias.add(0,new Categoria("Todos"));
        MySQLOpenHelper helper = new MySQLOpenHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM categories", null);
        while (cursor.moveToNext()) {
            Categoria nuevaCategoria = new Categoria(cursor.getString(0));
            listaCategorias.add(nuevaCategoria);
        }
        cursor.close();
        db.close();


        opcionesMenu = new String[listaCategorias.size()];
        Log.d("RRRRRR",String.valueOf(listaCategorias.size()));
        for(int i=1; i<=listaCategorias.size(); i++){
            opcionesMenu[i-1]= listaCategorias.get(i-1).getNombreCategoria();
        }
        drawerList.setAdapter(new ArrayAdapter<String>(
                getSupportActionBar().getThemedContext(),
                android.R.layout.simple_list_item_activated_1, opcionesMenu));



    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        boolean menuAbierto = drawerLayout.isDrawerOpen(drawerList);
        if(menuAbierto) menu.findItem(R.id.action_addCategory).setVisible(false);
        else menu.findItem(R.id.action_addCategory).setVisible(true);

        return super.onPrepareOptionsMenu(menu);
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
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (id == R.id.action_addCategory) {
            LayoutInflater factory = LayoutInflater.from(this);
            final View layout = factory.inflate(R.layout.add_category, null);
            final EditText editText = (EditText) layout.findViewById(R.id.editText_categoria);
            AlertDialog.Builder builder = new AlertDialog.Builder(MisSitios.this);
                    builder.setTitle("Indica el nombre de la categoría:")
                    .setView(layout)
                    .setPositiveButton("Crear", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                            String nombreCategoria = editText.getText().toString();
                            Log.d("RRRRR", nombreCategoria);
                            MySQLOpenHelper helper = new MySQLOpenHelper(getApplicationContext());
                            SQLiteDatabase db = helper.getWritableDatabase();
                            db.execSQL("INSERT INTO categories (name) VALUES ('"+nombreCategoria+"');");
                            db.close();
                            onResume();
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                        }
                    })
                    .create();
            builder.show();
        }

        return super.onOptionsItemSelected(item);
    }

    public float convertDpToPixel(float dp, Activity context)
    {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }
}

