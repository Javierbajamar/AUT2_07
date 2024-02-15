package com.example.aut2_07;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class AddItemActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextModel;
    private Spinner spinnerBrand;
    private Button buttonSave, buttonCancel;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        db = new DatabaseHelper(this);
        editTextTitle = findViewById(R.id.editTextTitle);
        spinnerBrand = findViewById(R.id.spinnerBrand);
        editTextModel = findViewById(R.id.editTextModel);
        buttonSave = findViewById(R.id.buttonSave);
        buttonCancel = findViewById(R.id.buttonCancel);

        // Configurar el spinner para las marcas de coche

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveItem();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cierra la actividad o el fragmento
                finish();
            }
        });
    }



    private void saveItem() {
        String title = editTextTitle.getText().toString().trim();
        String brand = spinnerBrand.getSelectedItem().toString();
        String model = editTextModel.getText().toString().trim();

        if (!title.isEmpty() && !model.isEmpty()) {
            Item newItem = new Item(-1, title, brand + " " + model);
            db.addItem(newItem);
            // Mostrar Snackbar con confirmación
            finish(); // Cierra la actividad
        } else {
            // Mostrar error, campos no pueden estar vacíos
        }
    }
}
