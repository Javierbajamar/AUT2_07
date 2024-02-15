package com.example.aut2_07;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class EditItemActivity extends AppCompatActivity {
    private EditText editItemTitle, editItemDescription;
    private Button btnSaveChanges;
    private DatabaseHelper dbHelper;
    private Item currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        editItemTitle = findViewById(R.id.editItemTitle);
        editItemDescription = findViewById(R.id.editItemDescription);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);

        dbHelper = new DatabaseHelper(this);

        // Obtén el ID del Item a editar
        int itemId = getIntent().getIntExtra("ITEM_ID", -1);
        if (itemId != -1) {
            currentItem = dbHelper.getItem(itemId);
            if (currentItem != null) {
                editItemTitle.setText(currentItem.getTitle());
                editItemDescription.setText(currentItem.getDescription());
            }
        }

        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateItem();
            }
        });
    }

    private void updateItem() {
        String newTitle = editItemTitle.getText().toString();
        String newDescription = editItemDescription.getText().toString();

        if (!newTitle.isEmpty() && !newDescription.isEmpty() && currentItem != null) {
            currentItem.setTitle(newTitle);
            currentItem.setDescription(newDescription);
            dbHelper.updateItem(currentItem);
            finish(); // Cierra la actividad y vuelve a la lista
        }
    }
}

