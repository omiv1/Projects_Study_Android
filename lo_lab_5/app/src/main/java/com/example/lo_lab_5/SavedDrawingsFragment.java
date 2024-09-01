package com.example.lo_lab_5;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.io.File;

public class SavedDrawingsFragment extends Fragment {

    private ListView listView;
    private File[] savedDrawings;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saved_drawings, container, false);
        listView = view.findViewById(R.id.savedDrawingsList);
        loadSavedDrawings();

        view.findViewById(R.id.buttonBack).setOnClickListener(v -> getActivity().onBackPressed());

        return view;
    }

    private void loadSavedDrawings() {
        Context context = getContext();
        if (context != null) {
            File directory = context.getFilesDir();
            savedDrawings = directory.listFiles((dir, name) -> name.endsWith(".png"));
            String[] fileNames = new String[savedDrawings.length];
            for (int i = 0; i < savedDrawings.length; i++) {
                fileNames[i] = savedDrawings[i].getName();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, fileNames);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                Intent intent = new Intent(context, ViewDrawingActivity.class);
                intent.putExtra("filePath", savedDrawings[position].getAbsolutePath());
                startActivity(intent);
            });
        }
    }
}
