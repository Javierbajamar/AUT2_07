package com.example.aut2_07;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.Manifest;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AudioRecordActivity extends AppCompatActivity {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200; // Puedes usar cualquier valor entero.


    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String lastRecordedAudioFilePath;
    private boolean isRecording = false;

    private Button btnRecordPause;
    private ExtendedFloatingActionButton fabPlayLastRecord;
    private SeekBar seekBarVolume;

    private String audioFilePath = null;

    private Button btnRecord;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audiorecordactivity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Habilita el botón de regreso en el Toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btnRecord = findViewById(R.id.btnRecord);
        fabPlayLastRecord = findViewById(R.id.fabPlayLastRecord);
        seekBarVolume = findViewById(R.id.seekBarVolume);

        btnRecord.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
            } else {
                toggleRecording();
            }
        });

        fabPlayLastRecord.setOnClickListener(v -> {
            if (lastRecordedAudioFilePath != null) {
                if (mediaPlayer == null) {
                    startPlayback(lastRecordedAudioFilePath);
                } else {
                    stopPlayback();
                }
            } else {
                Toast.makeText(AudioRecordActivity.this, "No hay grabación para reproducir", Toast.LENGTH_SHORT).show();
            }
        });


        // Manejadores para los SeekBar
        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null) { // Verifica que mediaPlayer no sea null
                    float volume = progress / 100f;
                    mediaPlayer.setVolume(volume, volume);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        Button btnBackToMain = findViewById(R.id.btnBackToMain);
        btnBackToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crea el Intent para volver a MainActivity
                Intent intent = new Intent(AudioRecordActivity.this, MainActivity.class);
                // Inicia la actividad
                startActivity(intent);

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    private void toggleRecording() {
        if (isRecording) {
            stopRecording();
            btnRecord.setText(getString(R.string.grabar));
        } else {
            startRecording();
            btnRecord.setText(getString(R.string.detener));
        }
    }

    private void startRecording() {

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            lastRecordedAudioFilePath = createAudioFile().getAbsolutePath();
            mediaRecorder.setOutputFile(lastRecordedAudioFilePath);
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
            Toast.makeText(this, "Grabación iniciada", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private File createAudioFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        return File.createTempFile("AUDIO_" + timeStamp + "_", ".3gp", storageDir);
    }

    private void stopRecording() {
        if (isRecording) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
            Toast.makeText(this, "Grabación detenida", Toast.LENGTH_SHORT).show();
        }
    }

    private void completeRecording() {
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
        isRecording = false;
        // Aquí asumes que lastRecordedAudioFilePath ya tiene el valor correcto
        Toast.makeText(this, "Grabación completada", Toast.LENGTH_LONG).show();
    }

    private void startPlayback(String audioFilePath) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(audioFilePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
            Toast.makeText(this, "Reproduciendo audio", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopPlayback() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.release();
        mediaPlayer = null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isRecording) {
            completeRecording();
        }
        if (mediaPlayer != null) {
            stopPlayback();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                toggleRecording();
            } else {
                Toast.makeText(this, "Permiso de grabación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
