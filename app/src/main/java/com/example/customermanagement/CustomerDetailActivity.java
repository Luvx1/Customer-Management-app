package com.example.customermanagement;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.customermanagement.databasehandler.DatabaseHandler;
import com.example.customermanagement.model.Customer;
import com.google.android.material.textfield.TextInputEditText;

public class CustomerDetailActivity extends AppCompatActivity {
    TextInputEditText customerNameEditText, customerAgeEditText, customerAddressEditText, customerSalaryEditText, amountSalaryEditText;
    ImageView customerImage, editIcon, backIcon;
    RadioGroup genderRadioGroup;
    RadioButton radioMale, radioFemale;
    Button editButton, increaseButton;
    ImageButton chevronDobuleLeftBtn, chevronLeftBtn, chevronRightBtn, chevronDobuleRightBtn;
    String customerId;
    DatabaseHandler db;
    Customer currentCustomerData;
    private static final String PREFS_NAME = "MyPrefs";
    private static final String HIDDEN_KEY = "hidden";
    private static final String HIDDEN_ARRAY_KEY = "hidden_array";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        db = new DatabaseHandler(this);
        customerImage = findViewById(R.id.customerImage);
        customerNameEditText = findViewById(R.id.customerNameEditText);
        customerAgeEditText = findViewById(R.id.customerAgeEditText);
        customerAddressEditText = findViewById(R.id.customerAddressEditText);
        customerSalaryEditText = findViewById(R.id.customerSalaryEditText);
        genderRadioGroup = findViewById(R.id.radioGroupGender);
        radioMale = findViewById(R.id.radioMale);
        radioFemale = findViewById(R.id.radioFemale);
        editIcon = findViewById(R.id.editIcon);
        editButton = findViewById(R.id.editInforButton);
        backIcon = findViewById(R.id.backIcon);
        chevronDobuleLeftBtn = findViewById(R.id.chevronDobuleLeft);
        chevronLeftBtn = findViewById(R.id.chevronLeft);
        chevronRightBtn = findViewById(R.id.chevronRight);
        chevronDobuleRightBtn = findViewById(R.id.chevronDobuleRight);
        amountSalaryEditText = findViewById(R.id.customerAmoutSalaryEditText);
        increaseButton = findViewById(R.id.increaseButton);
        customerId = getIntent().getStringExtra("customerId");

        backIconPresses();
        showCustomerDetail();
        editIconPresses();
        showFirstCustomer();
        showLastCustomer();
        showNextCustomer();
        showPreviousCustomer();
        increaseSalary();
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateInformation();
            }
        });
    }

    private void backIconPresses() {
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void showCustomerDetail() {
        Customer customer = db.getCustomer(customerId);
        currentCustomerData = customer;
        fetchData(customer);
    }

    private void editIconPresses() {
        boolean[] hiddenArray = getHiddenArray();
        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!hiddenArray[0]){
                    customerAddressEditText.setEnabled(true);
                }
                if(!hiddenArray[1]) {
                    customerAgeEditText.setEnabled(true);
                }
                if(!hiddenArray[2]){
                    customerSalaryEditText.setEnabled(true);
                }
                customerNameEditText.setEnabled(true);
                radioMale.setEnabled(true);
                radioFemale.setEnabled(true);
                genderRadioGroup.setEnabled(true);
                editButton.setEnabled(true);
            }
        });
    }

    private boolean isMaleSelected() {
        int selectedId = genderRadioGroup.getCheckedRadioButtonId();
        if (selectedId == radioMale.getId()) {
            return true;
        } else {
            return false;
        }
    }

    private void updateInformation() {
        boolean[] hiddenArray = getHiddenArray();
        String customerName = String.valueOf(customerNameEditText.getText()).trim();
        String customerAgeStr;
        String customerAddress;
        String customerSalaryStr;

        boolean isMale = isMaleSelected();

        if(hiddenArray[0]){
            customerAddress = currentCustomerData.get_Address();
        }else{
            customerAddress = String.valueOf(customerAddressEditText.getText()).trim();
        }
        if(hiddenArray[1]) {
            customerAgeStr = currentCustomerData.get_Age();
        }else{
            customerAgeStr = String.valueOf(customerAgeEditText.getText()).trim();
        }
        if(hiddenArray[2]){
            customerSalaryStr = currentCustomerData.get_Salary();
        }else{
            customerSalaryStr = String.valueOf(customerSalaryEditText.getText()).trim();
        }

        if (customerName.isEmpty()) {
            Toast.makeText(CustomerDetailActivity.this, "Please enter a name.", Toast.LENGTH_SHORT).show();
            return;
        }

        int customerAge;
        try {
            customerAge = Integer.parseInt(customerAgeStr);
            if (customerAge < 0 || customerAge > 120) {
                Toast.makeText(CustomerDetailActivity.this, "Please enter a valid age (0-120).", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(CustomerDetailActivity.this, "Please enter a valid age.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (customerAddress.isEmpty()) {
            Toast.makeText(CustomerDetailActivity.this, "Please enter an address.", Toast.LENGTH_SHORT).show();
            return;
        }

        double customerSalary;
        try {
            customerSalary = Double.parseDouble(customerSalaryStr);
            if (customerSalary < 0) {
                Toast.makeText(CustomerDetailActivity.this, "Please enter a valid salary (non-negative).", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(CustomerDetailActivity.this, "Please enter a valid salary.", Toast.LENGTH_SHORT).show();
            return;
        }

        Customer customer = new Customer(customerId, customerName, isMale, customerAgeStr, customerAddress, customerSalaryStr);
        int result = db.updateCustomer(customer);
        if (result != -1) {
            Toast.makeText(CustomerDetailActivity.this, "Customer edit successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(CustomerDetailActivity.this, "Failed to edit customer.", Toast.LENGTH_SHORT).show();
        }
    }


    private void fetchData(Customer customer) {
        String customerName = customer.get_Name();
        String customerAge = customer.get_Age();
        String customerAddress = customer.get_Address();
        String customerSalary = customer.get_Salary();
        boolean[] hiddenArray = getHiddenArray();
        boolean customerGender = customer.getGender();

        if (customerGender) {
            customerImage.setImageResource(R.drawable.male);
            radioMale.setChecked(true);
        } else {
            customerImage.setImageResource(R.drawable.female);
            radioFemale.setChecked(true);
        }
        customerNameEditText.setText(customerName);

        if (hiddenArray[1]) {
            customerAgeEditText.setText("******");
        } else {
            customerAgeEditText.setText(customerAge);
        }

        if (hiddenArray[0]) {
            customerAddressEditText.setText("******");
        } else {
            customerAddressEditText.setText(customerAddress);
        }

        if (hiddenArray[2]) {
            customerSalaryEditText.setText("******");
        } else {
            customerSalaryEditText.setText(customerSalary);
        }

    }

    private void showFirstCustomer() {
        chevronDobuleLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Customer customer = db.getFirstCustomer();
                customerId = customer.get_ID();
                currentCustomerData =customer;
                fetchData(customer);
            }
        });
    }

    private void showLastCustomer() {
        chevronDobuleRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Customer customer = db.getLastCustomer();
                customerId = customer.get_ID();
                currentCustomerData =customer;
                fetchData(customer);
            }
        });
    }

    private void showPreviousCustomer() {
        chevronLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Customer customer = db.getPreviousCustomer(customerId);
                if (customer != null) {
                    customerId = customer.get_ID();
                    currentCustomerData =customer;
                    fetchData(customer);
                }
            }
        });
    }

    private void showNextCustomer() {
        chevronRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Customer customer = db.getNextCustomer(customerId);
                if (customer != null) {
                    customerId = customer.get_ID();
                    currentCustomerData =customer;
                    fetchData(customer);
                }

            }
        });
    }

    private void increaseSalary() {
        boolean[] hiddenArray = getHiddenArray();
        String customerSalaryStr;
        if (hiddenArray[2]) {
            customerSalaryStr = currentCustomerData.get_Salary();
        } else {
            customerSalaryStr = String.valueOf(customerSalaryEditText.getText());
        }
        increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (amountSalaryEditText.getText() != null) {
                    try {
                        double amountToAdd = Double.parseDouble(amountSalaryEditText.getText().toString().replace(".", ""));
                        double currentSalary = Double.parseDouble(customerSalaryStr.replace(".", ""));
                        String amount = String.valueOf((long) (amountToAdd + currentSalary));
                        int result = db.updateCustomerSalary(customerId, amount);

                        if (result != -1) {
                            Customer customer = db.getCustomer(customerId);
                            fetchData(customer);
                            String currentTime = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date());
                            String notificationMessage = "Salary increased by amount " + amountSalaryEditText.getText().toString() + " at " + currentTime;
                            amountSalaryEditText.setText("");
                            new androidx.appcompat.app.AlertDialog.Builder(CustomerDetailActivity.this)
                                    .setTitle("Salary Increase")
                                    .setMessage(notificationMessage)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .setCancelable(false)
                                    .show();

                        } else {
                            Toast.makeText(CustomerDetailActivity.this, "Failed to increase salary.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(CustomerDetailActivity.this, "Invalid number format.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    private boolean getHiddenValue() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return sharedPreferences.getBoolean(HIDDEN_KEY, false);
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