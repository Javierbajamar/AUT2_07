package com.example.aut2_07;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;


import java.util.ArrayList;
import java.util.List;

public class ItemListActivity extends AppCompatActivity implements ItemsAdapter.OnItemListener {
    private RecyclerView recyclerView;
    private ItemsAdapter adapter;
    private List<Item> itemList; // Asegúrate de que esta lista sea del tipo correcto

    private DatabaseHelper dbHelper;
    private ExtendedFloatingActionButton fabAddItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        recyclerView = findViewById(R.id.itemsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fabAddItem = findViewById(R.id.fabAddItem);

        dbHelper = new DatabaseHelper(this);
        itemList = new ArrayList<>();

        adapter = new ItemsAdapter(this, itemList, this);
        recyclerView.setAdapter(adapter);

        loadItemsFromDatabase();

        adapter = new ItemsAdapter(this, itemList, this); // 'this' se refiere a ItemListActivity que implementa OnItemListener
        recyclerView.setAdapter(adapter);


        fabAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Iniciar AddItemActivity
                Intent intent = new Intent(ItemListActivity.this, AddItemActivity.class);
                startActivity(intent);
            }
        });


    }


    @Override
    public void onItemClicked(Item item) {
        // Mostrar diálogo de opciones
        new AlertDialog.Builder(this)
                .setTitle("Selecciona una acción")
                .setMessage("¿Quieres borrar o modificar este elemento?")
                .setPositiveButton("Modificar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(ItemListActivity.this, EditItemActivity.class);
                        intent.putExtra("ITEM_ID", item.getId());
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Borrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Borrar el elemento de la base de datos
                        dbHelper.deleteItem(item.getId());
                        loadItemsFromDatabase(); // Recargar la lista
                    }
                })
                .setNeutralButton("Cancelar", null)
                .show();
    }





    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        super.onResume();
        loadItemsFromDatabase(); // Recargar elementos cuando la actividad se reanuda

    }


    // Método para cargar los elementos de la base de datos
    private void loadItemsFromDatabase() {
        if (dbHelper != null) {
            itemList.clear(); // Limpia la lista actual
            itemList.addAll(dbHelper.getAllItems());
            adapter.notifyDataSetChanged(); // Notifica al adaptador que los datos han cambiado
            adapter = new ItemsAdapter(this, itemList, this);
            recyclerView.setAdapter(adapter);


        }
    }

}

