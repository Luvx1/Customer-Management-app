package com.example.customermanagement.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.customermanagement.CustomerDetailActivity;
import com.example.customermanagement.R;
import com.example.customermanagement.model.Customer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class AdapterCustomer extends ArrayAdapter<Customer> {
    Context context;
    ArrayList<Customer> arrayList;
    int layoutResource;

    Set<String> selectedCustomerIds = new HashSet<>();

    public AdapterCustomer(@NonNull Context context, int resource, ArrayList<Customer> objects) {
        super(context, resource, objects);
        this.context = context;
        this.arrayList = objects;
        this.layoutResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Customer customer = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.customer_item, parent, false);
        }

        ImageView customerImage = convertView.findViewById(R.id.customerImage);
        TextView customerName = convertView.findViewById(R.id.customName);
        TextView customerAge = convertView.findViewById(R.id.customAge);
        TextView customerAddress = convertView.findViewById(R.id.customAddress);
        CheckBox deleteCheckBox = convertView.findViewById(R.id.deleteCheckBox);
        boolean[] hiddenArray = getHiddenArray();
        if (customer.getGender()) {
            customerImage.setImageResource(R.drawable.male);
        } else {
            customerImage.setImageResource(R.drawable.female);
        }

        if (hiddenArray[0]) {
            customerAddress.setText("Address: ******");
        } else {
            customerAddress.setText("Address: " + customer.get_Address());
        }

        if (hiddenArray[1]) {
            customerAge.setText("Age: ****** years old");
        } else {
            customerAge.setText("Age: " + customer.get_Age() + " years old");
        }

        customerName.setText(customer.get_Name());


        deleteCheckBox.setOnCheckedChangeListener(null);
        deleteCheckBox.setChecked(selectedCustomerIds.contains(customer.get_ID()));

        deleteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Add customer ID to the set when checked
                    selectedCustomerIds.add(customer.get_ID());
                } else {
                    // Remove customer ID from the set when unchecked
                    selectedCustomerIds.remove(customer.get_ID());
                }
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CustomerDetailActivity.class);
                intent.putExtra("customerId", customer.get_ID());
                intent.putExtra("customerName", customer.get_Name());
                intent.putExtra("customerAge", customer.get_Age());
                intent.putExtra("customerAddress", customer.get_Address());
                intent.putExtra("customerSalary", customer.get_Salary());
                intent.putExtra("customerGender", customer.getGender());
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    public Set<String> getSelectedCustomerIds() {
        return selectedCustomerIds;
    }

    private boolean getHiddenValue() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("hidden", false); // Default is false
    }

    private boolean[] getHiddenArray() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String hiddenString = sharedPreferences.getString("hidden_array", "0,0,0,");

        String[] splitString = hiddenString.split(",");
        boolean[] hiddenArray = new boolean[3];
        for (int i = 0; i < splitString.length && i < hiddenArray.length; i++) {
            hiddenArray[i] = splitString[i].equals("1");
        }

        return hiddenArray;
    }
}
