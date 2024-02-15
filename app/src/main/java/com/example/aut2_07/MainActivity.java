package com.example.aut2_07;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private DatabaseHelper databaseHelper;
    private DrawerLayout drawer;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Establece el contenido de la vista aquí

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Configuración del tema oscuro
        SharedPreferences sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean isDarkTheme = sharedPreferences.getBoolean("isDarkTheme", false);
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_preferences) {
                toggleTheme();
            } else if (id == R.id.nav_about) {
                showAboutDialog();
            } else if (id == R.id.nav_logout) {
                finish();
            }
            drawer.closeDrawer(GravityCompat.START);
            return true;
        });


        databaseHelper = new DatabaseHelper(this);

        // Comprobar si el usuario ya inició sesión
        if (isUserLoggedIn()) {
            Intent intent = new Intent(MainActivity.this, ItemListActivity.class);
            startActivity(intent);
            finish(); // Finalizar MainActivity para evitar volver a esta pantalla
        } else {
            initializeLogin(); // Inicializar la lógica de inicio de sesión si el usuario no está logueado
        }

        Button buttonRegister = findViewById(R.id.buttonGoToRegister);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_activity1) {
            startActivity(new Intent(this, MainActivity.class));
            return true;
        } else if (id == R.id.nav_activity2) {
            startActivity(new Intent(this, AudioRecordActivity.class));
            return true;
        } else if (id == R.id.nav_activity3) {
            startActivity(new Intent(this, RegisterActivity.class));
            return true;
        } else if (id == R.id.nav_activity4) {
            startActivity(new Intent(this, CuartaActividad.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void toggleTheme() {
        SharedPreferences sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean isDarkTheme = sharedPreferences.getBoolean("isDarkTheme", false);

        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            sharedPreferences.edit().putBoolean("isDarkTheme", false).apply();
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            sharedPreferences.edit().putBoolean("isDarkTheme", true).apply();
        }
    }

    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Acerca de")
                .setMessage("Versión de la aplicación: 1.0\nDesarrollado por: Javier")
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private boolean isUserLoggedIn() {
        // Implementa tu lógica de comprobación de inicio de sesión aquí
        return false; // Retorna verdadero si las credenciales existen y son válidas
    }

    private void initializeLogin() {
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> performLogin());
    }

    private void performLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(MainActivity.this, "Please enter both email and password.", Toast.LENGTH_SHORT).show();
        } else {
            if (databaseHelper.checkUser(email, password)) {
                Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, ItemListActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(MainActivity.this, "Login failed. Invalid email or password.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
