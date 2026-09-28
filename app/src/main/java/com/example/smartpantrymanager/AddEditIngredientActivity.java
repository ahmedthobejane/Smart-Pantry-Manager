package com.example.smartpantrymanager;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.database.PantryDbHelper;
import com.example.smartpantrymanager.model.Pantryitem;

public class AddEditIngredientActivity extends AppCompatActivity {

    private EditText editName, editQty, editExpiry;
    private Spinner spinnerUnit;
    private Button btnSave;
    private PantryDbHelper dbHelper;
    private Pantryitem existingItem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        dbHelper = new PantryDbHelper(this);

        editName = findViewById(R.id.edit_ing_name);
        editQty = findViewById(R.id.edit_ing_qty);
        editExpiry = findViewById(R.id.edit_ing_expiry);
        spinnerUnit = findViewById(R.id.spinner_unit);
        btnSave = findViewById(R.id.btn_save_ingredient);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.units_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(adapter);

        if (getIntent().hasExtra("EXTRA_ITEM")) {
            existingItem = (Pantryitem) getIntent().getSerializableExtra("EXTRA_ITEM");
            if (existingItem != null) {
                setTitle("Edit Ingredient");
                editName.setText(existingItem.getName());
                editQty.setText(String.valueOf(existingItem.getQuantity()));
                editExpiry.setText(existingItem.getExpiryDate());

                int pos = adapter.getPosition(existingItem.getUnit());
                if (pos >= 0) spinnerUnit.setSelection(pos);
            }
        } else {
            setTitle("Add Ingredient");
        }

        btnSave.setOnClickListener(v -> saveItem());
    }

    private void saveItem() {
        String name = editName.getText().toString().trim();
        String qtyStr = editQty.getText().toString().trim();
        String expiry = editExpiry.getText().toString().trim();
        String unit = spinnerUnit.getSelectedItem().toString();

        // Input Validation
        if (TextUtils.isEmpty(name)) {
            editName.setError("Ingredient name is required");
            return;
        }

        if (TextUtils.isEmpty(qtyStr)) {
            editQty.setError("Quantity is required");
            return;
        }

        double qty;
        try {
            qty = Double.parseDouble(qtyStr);
            if (qty <= 0) {
                editQty.setError("Quantity must be greater than zero");
                return;
            }
        } catch (NumberFormatException e) {
            editQty.setError("Enter a valid number");
            return;
        }

        if (existingItem == null) {
            Pantryitem newItem = new Pantryitem(name, qty, unit, expiry);
            dbHelper.addPantryItem(newItem);
            Toast.makeText(this, "Ingredient added", Toast.LENGTH_SHORT).show();
        } else {
            Pantryitem updated = new Pantryitem(existingItem.getId(), name, qty, unit, expiry);
            dbHelper.updatePantryItem(updated);
            Toast.makeText(this, "Ingredient updated", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}