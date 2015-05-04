package com.example.gui.myplaces;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
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


public class MisSitios extends ActionBarActivity {

    private Bitmap foto;
    String[] data;
    private String[] opcionesMenu;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    ActionBarDrawerToggle drawerToggle;
    private ArrayList<Categoria> listaCategorias;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_sitios);

        for(int i=0; i<=listaCategorias.size(); i++){
            opcionesMenu[i]= listaCategorias.get(i).getNombreCategoria();
        }
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerList.setAdapter(new ArrayAdapter<String>(
                getSupportActionBar().getThemedContext(),
                android.R.layout.simple_list_item_activated_1, opcionesMenu));

        final CharSequence tituloApp = getTitle();
        drawerToggle=new ActionBarDrawerToggle(this, drawerLayout,R.drawable.ic_places,
                "Categorías",tituloApp){
                            public void onDrawerClosed(View view){
                                getSupportActionBar().setTitle(tituloSeccion);
                                ActivityCompat.invalidateOptionsMenu(MisSitios.this);
                            }
                            public void onDrawerOpened(View drawerView){
                                getSupportActionBar().setTitle(tituloApp);
                                ActivityCompat.invalidateOptionsMenu(MisSitios.this);

                            }
                        };
        drawerLayout.setDrawerListener(drawerToggle);


        ListView listPlaces = (ListView) findViewById(R.id.placesList);
        listPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                data = new String[5];
                String selected = ((TextView) view.findViewById(R.id.list_scores_title)).getText().toString();
                Log.d("FFFFFF",selected);
                MySQLOpenHelper helper = new MySQLOpenHelper(getApplicationContext());
                SQLiteDatabase db = helper.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT latitud, longitud, name, description, image FROM myplaces WHERE name='"+selected+"'", null);
                while (cursor.moveToNext()) {
                data[0] = String.valueOf(cursor.getDouble(0));
                data[1] = String.valueOf(cursor.getDouble(1));
                data[2]= cursor.getString(2);
                data[3] = cursor.getString(3);
                data[4]= cursor.getString(4);
                }
                cursor.close();
                db.close();

                Intent i = new Intent (getApplicationContext(), Sitio.class);
                i.putExtra("data", data);
                startActivity(i);
            }
        });

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
            String imagenPath = cursor.getString(1);
            Log.d("DDDDD",imagenPath);

            if(!imagenPath.equals("null")) {
                Log.d("DDDDD", "Dentro del if");
                File image = new File(imagenPath);
                if(image.exists()){
                    Log.d("DDDDD", "dentro del exists" + image.getAbsolutePath());
                    Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
                    foto = Bitmap.createScaledBitmap(bitmap, ((int) convertDpToPixel(140, this)), ((int) convertDpToPixel(110, this)), true);
                    Log.d("DDDDD","dentro del exists");

                }

                Log.d("DDDDD","puesta la imagen: "+imagenPath);

            } else {
                foto = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_places);
                Log.d("DDDDD","decode null: ");
            }

            itemLocal.put("nombre", nombre);
            itemLocal.put("imagen", foto);
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
        if (id == R.id.action_addCategory) {
            LayoutInflater factory = LayoutInflater.from(this);
            final View layout = factory.inflate(R.layout.add_category, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(MisSitios.this);
                    builder.setTitle("Indica el nombre de la categoría:")
                    .setView(layout)
                    .setPositiveButton("Crear", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            EditText editText = (EditText) findViewById(R.id.editText_categoria);
                            String nombreCategoria = editText.getText().toString();
                            Categoria nuevaCategoria = new Categoria(nombreCategoria);
                            listaCategorias.add(nuevaCategoria);
                            dialog.dismiss();
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
