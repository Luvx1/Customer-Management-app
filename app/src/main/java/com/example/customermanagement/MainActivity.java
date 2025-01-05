package com.example.customermanagement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.example.customermanagement.adapter.AdapterCustomer;
import com.example.customermanagement.databasehandler.DatabaseHandler;
import com.example.customermanagement.model.Customer;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    ImageView addIcon, deleteIcon, emptyBoxImage, dropDownIcon;
    TextView noDataTextView;
    ListView lv_HienThiDS;
    ArrayList<Customer> Customerlist = new ArrayList<>();
    AdapterCustomer adapterCustomer;
    DatabaseHandler db;
    LinearLayout noDataLayout;
    TextInputEditText searchEditText;
    ImageButton searchButton;
    MediaPlayer mediaPlayer; // MediaPlayer for music
    private static final String PREFS_NAME = "MyPrefs";
    private static final String HIDDEN_ARRAY_KEY = "hidden_array";
    private static final String HIDDEN_KEY = "hidden";
    private static final String MUSIC_KEY = "music_playing"; // Key for music state

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        addIcon = findViewById(R.id.addIcon);
        deleteIcon = findViewById(R.id.deleteIcon);
        dropDownIcon = findViewById(R.id.dropdownIcon);
        emptyBoxImage = findViewById(R.id.noDataImageView);
        noDataTextView = findViewById(R.id.noDataTextView);
        noDataLayout = findViewById(R.id.noDataLayout);
        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);
        db = new DatabaseHandler(this);

        addIconPressed();
        getAllCustomer();
        deletePressed();
        searchCustomerByNameOrAddress();

        dropDownIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDropdownMenu(v);
            }
        });

        if (getMusicPlayingValue()) {
            playMusic();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllCustomer();
        getHiddenArray();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            saveMusicPlayingValue(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void addIconPressed() {
        addIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToAddCustomer = new Intent(MainActivity.this, AddCustomerActivity.class);
                startActivity(intentToAddCustomer);
            }
        });
    }

    private void getAllCustomer() {
        lv_HienThiDS = findViewById(R.id.listCustomer);
        Customerlist.clear();

        ArrayList<Customer> allCustomers = db.getAllCustomers();

        for (Customer customer : allCustomers) {
            if (customer.get_DelteDate() == null || customer.get_DelteDate().isEmpty()) {
                Customerlist.add(customer);
            }
        }

        if (Customerlist.isEmpty()) {
            lv_HienThiDS.setVisibility(View.GONE);
            noDataLayout.setVisibility(View.VISIBLE);
        } else {
            lv_HienThiDS.setVisibility(View.VISIBLE);
            noDataLayout.setVisibility(View.GONE);
            adapterCustomer = new AdapterCustomer(MainActivity.this, R.layout.customer_item, Customerlist);
            lv_HienThiDS.setAdapter(adapterCustomer);
        }
    }

    private void deletePressed() {
        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<String> selectedIds = adapterCustomer.getSelectedCustomerIds();

                if (!selectedIds.isEmpty()) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Delete Confirmation")
                            .setMessage("Are you sure you want to delete the selected customers?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                for (String id : selectedIds) {
                                    db.deleteCustomer(id);
                                }
                                Customerlist.clear();
                                getAllCustomer();
                            })
                            .setNegativeButton("No", (dialog, which) -> {
                                dialog.dismiss();
                            })
                            .show();
                } else {
                    Log.d("SelectedCustomerID", "No customer selected.");
                }
            }
        });
    }

    private void searchCustomerByNameOrAddress() {
        if (searchEditText.getText() != null) {
            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String searchText = String.valueOf(searchEditText.getText()).trim();
                    ArrayList<Customer> customers = db.searchCustomers(searchText);
                    Customerlist.clear();
                    if (customers.isEmpty()) {
                        lv_HienThiDS.setVisibility(View.GONE);
                        noDataLayout.setVisibility(View.VISIBLE);
                    } else {
                        lv_HienThiDS.setVisibility(View.VISIBLE);
                        noDataLayout.setVisibility(View.GONE);
                        Customerlist.addAll(customers);
                        adapterCustomer.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    private void showDropdownMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.dropdown_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                boolean[] hiddenArray = getHiddenArray(); // Get the current hidden array

                switch (item.toString()) {
                    case "Hide/show Address":
                        hiddenArray[0] = !hiddenArray[0];
                        saveHiddenArray(hiddenArray);
                        getAllCustomer();
                        return true;

                    case "Hide/show Age":
                        hiddenArray[1] = !hiddenArray[1];
                        saveHiddenArray(hiddenArray);
                        getAllCustomer();
                        return true;

                    case "Hide/show Salary":
                        hiddenArray[2] = !hiddenArray[2];
                        saveHiddenArray(hiddenArray);
                        getAllCustomer();
                        return true;

                    case "Play Music":
                        playMusic();
                        return true;

                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }

    private void playMusic() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.littleroottown);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
            saveMusicPlayingValue(true);
        } else if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            saveMusicPlayingValue(false);
        } else {
            mediaPlayer.start();
            saveMusicPlayingValue(true);
        }
    }

    private void saveMusicPlayingValue(boolean isPlaying) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(MUSIC_KEY, isPlaying);
        editor.apply();
    }

    private boolean getMusicPlayingValue() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return sharedPreferences.getBoolean(MUSIC_KEY, false);
    }

    private void saveHiddenArray(boolean[] hiddenArray) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        StringBuilder hiddenString = new StringBuilder();
        for (boolean value : hiddenArray) {
            hiddenString.append(value ? "1" : "0").append(",");
        }

        editor.putString(HIDDEN_ARRAY_KEY, hiddenString.toString());
        editor.apply();
    }

    private boolean[] getHiddenArray() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String hiddenString = sharedPreferences.getString(HIDDEN_ARRAY_KEY, "0,0,0,");

        String[] splitString = hiddenString.split(",");
        boolean[] hiddenArray = new boolean[3];
        for (int i = 0; i < splitString.length && i < hiddenArray.length; i++) {
            hiddenArray[i] = splitString[i].equals("1");
        }

        return hiddenArray;
    }

}
