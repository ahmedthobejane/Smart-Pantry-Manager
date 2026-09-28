package com.example.smartpantrymanager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.AddEditIngredientActivity;
import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.adapter.PantryAdapter;
import com.example.smartpantrymanager.database.PantryDbHelper;
import com.example.smartpantrymanager.model.Pantryitem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class PantryFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView emptyView;
    private PantryAdapter adapter;
    private PantryDbHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pantry, container, false);

        recyclerView = v.findViewById(R.id.recycler_pantry);
        emptyView = v.findViewById(R.id.text_empty_pantry);
        FloatingActionButton fab = v.findViewById(R.id.fab_add_ingredient);

        dbHelper = new PantryDbHelper(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fab.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), AddEditIngredientActivity.class);
            startActivity(intent);
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPantryItems();
    }

    private void loadPantryItems() {
        List<Pantryitem> list = dbHelper.getAllPantryItems();
        if (list.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            adapter = new PantryAdapter(list, new PantryAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Pantryitem item) {
                    Intent intent = new Intent(getActivity(), AddEditIngredientActivity.class);
                    intent.putExtra("EXTRA_ITEM", item);
                    startActivity(intent);
                }

                @Override
                public void onDeleteClick(Pantryitem item) {
                    dbHelper.deletePantryItem(item.getId());
                    loadPantryItems();
                }
            });
            recyclerView.setAdapter(adapter);
        }
    }
}