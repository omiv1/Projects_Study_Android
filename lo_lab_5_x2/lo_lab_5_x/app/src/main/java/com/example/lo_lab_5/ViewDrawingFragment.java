package com.example.lo_lab_5;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ViewDrawingFragment extends Fragment {
    private static final String ARG_DRAWING_PATH = "drawingPath";

    private String drawingPath;

    public static ViewDrawingFragment newInstance(String drawingPath) {
        ViewDrawingFragment fragment = new ViewDrawingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DRAWING_PATH, drawingPath);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            drawingPath = getArguments().getString(ARG_DRAWING_PATH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drawing, container, false);
        ImageView imageView = view.findViewById(R.id.savedDrawingImage);

        Bitmap bitmap = BitmapFactory.decodeFile(drawingPath);
        imageView.setImageBitmap(bitmap);

        return view;
    }
}
