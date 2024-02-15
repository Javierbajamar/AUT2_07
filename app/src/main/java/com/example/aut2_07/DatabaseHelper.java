package com.example.aut2_07;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Nombre de la base de datos y versión
    private static final String DATABASE_NAME = "itemsDatabase";
    private static final int DATABASE_VERSION = 1;

    // Nombre de la tabla y columnas
    private static final String TABLE_ITEMS = "items";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_IMAGE_PATH = "imagePath";

    // Creación de la tabla SQL


    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creación de la tabla de elementos
        db.execSQL(TABLE_CREATE);

        // Creación de la tabla de usuarios
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_EMAIL + " TEXT,"
                + COLUMN_PASSWORD + " TEXT" + ")";
        db.execSQL(CREATE_USERS_TABLE);


    }

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_ITEMS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_DESCRIPTION + " TEXT, " +
                    COLUMN_IMAGE_PATH + " TEXT" +
                    ");";

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        onCreate(db);
    }

    // Métodos CRUD a continuación...

    // Método para agregar un elemento
    public void addItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, item.getTitle());
        values.put(COLUMN_DESCRIPTION, item.getDescription());
        // Agrega la ruta de la imagen si estás manejando imágenes
        long id = db.insert(TABLE_ITEMS, null, values);
        db.close();

        // Muestra un Snackbar si estás en una Activity o Fragment
        // Snackbar.make(view, "Item added", Snackbar.LENGTH_LONG).show();
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_USER_ID}; // Asegúrate de que esto coincida con la definición de tu columna.
        String selection = COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {email, password};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    public void addUser(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);

        db.insert(TABLE_USERS, null, values);
        db.close();
    }

    public void deleteItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEMS, COLUMN_ID + " = ?", new String[] { String.valueOf(id) });
        db.close();
    }

    public void updateItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, item.getTitle());
        values.put(COLUMN_DESCRIPTION, item.getDescription());
        // Actualizar más campos si es necesario

        // Actualizar fila
        db.update(TABLE_ITEMS, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(item.getId())});
    }
    public List<Item> getAllItems() {
        List<Item> itemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                COLUMN_ID,
                COLUMN_TITLE,
                COLUMN_DESCRIPTION,
                COLUMN_IMAGE_PATH // Asegúrate de que este campo exista en tu tabla si decides incluirlo
        };

        Cursor cursor = db.query(
                TABLE_ITEMS,  // La tabla de la que quieres obtener los datos
                projection,   // Las columnas que quieres obtener
                null,         // Columnas para la cláusula WHERE
                null,         // Los valores para la cláusula WHERE
                null,         // group by
                null,         // having
                null          // order by
        );

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COLUMN_ID);
            int titleIndex = cursor.getColumnIndex(COLUMN_TITLE);
            int descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION);
            int imagePathIndex = cursor.getColumnIndex(COLUMN_IMAGE_PATH);

            do {
                int id = cursor.getInt(idIndex);
                String title = cursor.getString(titleIndex);
                String description = cursor.getString(descriptionIndex);
                String imagePath = imagePathIndex != -1 ? cursor.getString(imagePathIndex) : null; // Maneja el caso en que la columna de la imagen podría no existir

                Item item = new Item(id, title, description); // Ajusta el constructor de acuerdo a tu clase Item
                itemList.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return itemList;
    }

    public Item getItem(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Define una proyección que especifica las columnas que utilizarás después de esta consulta
        String[] projection = {
                COLUMN_ID,
                COLUMN_TITLE,
                COLUMN_DESCRIPTION,
                COLUMN_IMAGE_PATH
        };

        // Filtra los resultados WHERE "id" = 'id'
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

        Cursor cursor = db.query(
                TABLE_ITEMS,   // La tabla para consultar
                projection,    // Las columnas a retornar
                selection,     // Las columnas para la cláusula WHERE
                selectionArgs, // Los valores para la cláusula WHERE
                null,  // No agrupar las filas
                null,   // No filtrar por grupos de filas
                null    // El orden del sorteo
        );

        Item item = null;
        if (cursor.moveToFirst()) {
            int itemId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
            String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH));

            item = new Item(itemId, title, description);
        }
        cursor.close();
        return item;
    }

}




