package com.example.lo_lab_5;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

public class SavedDrawingsFragment extends Fragment {

    private OnDrawingSelectedListener listener;

    public interface OnDrawingSelectedListener {
        void onDrawingSelected(String drawingPath);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDrawingSelectedListener) {
            listener = (OnDrawingSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement SavedDrawingsFragment.OnDrawingSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saved_drawings, container, false);

        ListView listView = view.findViewById(R.id.savedDrawingsList);
        String[] drawingPaths = getSavedDrawings();
        String[] drawingNames = getDrawingNames(drawingPaths);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, drawingNames);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener != null) {
                    listener.onDrawingSelected(drawingPaths[position]); // Use the full path to load the image
                }
            }
        });

        return view;
    }

    private String[] getSavedDrawings() {
        File directory = getContext().getFilesDir();
        File[] files = directory.listFiles();
        ArrayList<String> paths = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".png")) {
                    paths.add(file.getAbsolutePath());
                }
            }
        }
        return paths.toArray(new String[0]);
    }

    private String[] getDrawingNames(String[] paths) {
        ArrayList<String> names = new ArrayList<>();
        for (String path : paths) {
            names.add(new File(path).getName());
        }
        return names.toArray(new String[0]);
    }
}
