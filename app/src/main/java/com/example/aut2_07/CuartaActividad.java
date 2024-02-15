package com.example.aut2_07;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.Manifest;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class CuartaActividad extends AppCompatActivity {
    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fourthactivity);

        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tabs);

        // Configura el adaptador del ViewPager
        viewPager.setAdapter(new FragmentAdapter(this));

        // Conecta el TabLayout con el ViewPager
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText("Tab " + (position + 1)) // Configura los títulos de los tabs aquí
        ).attach();
    }

    // Adaptador para los fragmentos
    private static class FragmentAdapter extends FragmentStateAdapter {
        public FragmentAdapter(CuartaActividad fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {
            // Retorna un fragmento específico según la posición
            switch (position) {
                case 0:
                    return new DeviceResolutionFragment(); // Fragmento para la resolución del dispositivo
                case 1:
                    return new BatteryStatusFragment(); // Fragmento para el estado de la batería
                case 2:
                    return new LightSensorFragment(); // Fragmento para el sensor de luz
                case 3:
                    return new LocationFragment(); // Fragmento para la localización
                default:
                    return new Fragment(); // Fragmento por defecto
            }
        }

        public static class DeviceResolutionFragment extends Fragment {
            @Nullable
            @Override
            public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
                View view = inflater.inflate(R.layout.fragment_device_resolution, container, false);
                TextView resolutionTextView = view.findViewById(R.id.resolution_text_view);

                DisplayMetrics displayMetrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height = displayMetrics.heightPixels;
                int width = displayMetrics.widthPixels;

                resolutionTextView.setText("Resolución: " + width + "x" + height + " píxeles");
                return view;
            }
        }

        public static class BatteryStatusFragment extends Fragment {
            private TextView batteryStatusTextView;

            private BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                    int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                    float batteryPct = level * 100 / (float) scale;

                    batteryStatusTextView.setText("Nivel de batería: " + batteryPct + "%");
                }
            };

            @Nullable
            @Override
            public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
                View view = inflater.inflate(R.layout.fragment_battery_status, container, false);
                batteryStatusTextView = view.findViewById(R.id.battery_status_text_view);
                getActivity().registerReceiver(batteryLevelReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                return view;
            }

            @Override
            public void onDestroyView() {
                super.onDestroyView();
                getActivity().unregisterReceiver(batteryLevelReceiver);
            }
        }

        public static class LightSensorFragment extends Fragment implements SensorEventListener {

            private TextView lightSensorTextView;
            private SensorManager sensorManager;
            private Sensor lightSensor;

            @Nullable
            @Override
            public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
                View view = inflater.inflate(R.layout.fragment_light_sensor, container, false);
                lightSensorTextView = view.findViewById(R.id.light_sensor_text_view);

                sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
                lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

                return view;
            }

            @Override
            public void onResume() {
                super.onResume();
                sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }

            @Override
            public void onPause() {
                super.onPause();
                sensorManager.unregisterListener(this);
            }

            @Override
            public void onSensorChanged(SensorEvent event) {
                float lux = event.values[0];
                String luminosity;
                if (lux < 100) luminosity = "Oscuro";
                else if (lux >= 100 && lux <= 2000) luminosity = "Normal";
                else luminosity = "Brillante";

                lightSensorTextView.setText("Luz ambiental: " + lux + " lx (" + luminosity + ")");
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // Implementar si es necesario
            }
        }

        public static class LocationFragment extends Fragment {


            private TextView locationTextView;
            private LocationManager locationManager;
            private String locationProvider = LocationManager.NETWORK_PROVIDER;

            @Nullable
            @Override
            public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
                View view = inflater.inflate(R.layout.fragment_location, container, false);
                locationTextView = view.findViewById(R.id.location_text_view);

                locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
                    if (lastKnownLocation != null) {
                        double lat = lastKnownLocation.getLatitude();
                        double lon = lastKnownLocation.getLongitude();
                        locationTextView.setText("Latitud: " + lat + ", Longitud: " + lon);
                    } else {
                        locationTextView.setText("Ubicación no disponible");
                    }
                }
                return view;
            }
        }

        @Override
        public int getItemCount() {
            return 4; // Número total de fragmentos/páginas
        }
    }
}


