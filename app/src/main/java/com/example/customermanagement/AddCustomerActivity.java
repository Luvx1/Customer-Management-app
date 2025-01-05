package com.example.customermanagement;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.customermanagement.adapter.AdapterCustomer;
import com.example.customermanagement.databasehandler.DatabaseHandler;
import com.example.customermanagement.model.Customer;
import com.google.android.material.textfield.TextInputEditText;

public class AddCustomerActivity extends AppCompatActivity {
    ImageView checkedIcon, backIcon;
    TextInputEditText customerNameEditText, customerAgeEditText, customerAddressEditText, customerSalaryEditText;
    RadioGroup genderRadioGroup;
    RadioButton radioMale, radioFemale;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        db = new DatabaseHandler(this);
        customerNameEditText = findViewById(R.id.customerNameEditText);
        customerAgeEditText = findViewById(R.id.customerAgeEditText);
        customerAddressEditText = findViewById(R.id.customerAddressEditText);
        customerSalaryEditText = findViewById(R.id.customerSalaryEditText);
        genderRadioGroup = findViewById(R.id.radioGroupGender);
        radioMale = findViewById(R.id.radioMale);
        radioFemale = findViewById(R.id.radioFemale);

        backIconPresses();
        addIconPress();
    }

    private boolean isMaleSelected() {
        int selectedId = genderRadioGroup.getCheckedRadioButtonId();
        if (selectedId == radioMale.getId()) {
            return true;
        } else {
            return false;
        }
    }

    private void backIconPresses() {
        backIcon = findViewById(R.id.backIcon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void addIconPress() {
        checkedIcon = findViewById(R.id.checkedIcon);
        checkedIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCustomer();
            }
        });
    }

    private void addCustomer() {
        String customerName = String.valueOf(customerNameEditText.getText()).trim();
        String customerAgeStr = String.valueOf(customerAgeEditText.getText()).trim();
        String customerAddress = String.valueOf(customerAddressEditText.getText()).trim();
        String customerSalaryStr = String.valueOf(customerSalaryEditText.getText()).trim();
        boolean isMale = isMaleSelected();

        if (customerName.isEmpty()) {
            Toast.makeText(AddCustomerActivity.this, "Please enter a name.", Toast.LENGTH_SHORT).show();
            return;
        }

        int customerAge;
        try {
            customerAge = Integer.parseInt(customerAgeStr);
            if (customerAge < 0 || customerAge > 120) {
                Toast.makeText(AddCustomerActivity.this, "Please enter a valid age (0-120).", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(AddCustomerActivity.this, "Please enter a valid age.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (customerAddress.isEmpty()) {
            Toast.makeText(AddCustomerActivity.this, "Please enter an address.", Toast.LENGTH_SHORT).show();
            return;
        }

        double customerSalary;
        try {
            customerSalary = Double.parseDouble(customerSalaryStr);
            if (customerSalary < 0) {
                Toast.makeText(AddCustomerActivity.this, "Please enter a valid salary (non-negative).", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(AddCustomerActivity.this, "Please enter a valid salary.", Toast.LENGTH_SHORT).show();
            return;
        }

        Customer customer = new Customer(customerName, isMale, customerAgeStr, customerAddress, customerSalaryStr);
        long result  = db.addCustomer(customer);
        if (result != -1) {
            Toast.makeText(AddCustomerActivity.this, "Customer added successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(AddCustomerActivity.this, "Failed to add customer.", Toast.LENGTH_SHORT).show();
        }
    }

}