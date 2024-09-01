package com.example.lo_lab_5;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class SavedDrawingsFragment extends Fragment {

    private static final String TAG = "SavedDrawingsFragment";
    private OnDrawingSelectedListener listener;
    private ArrayList<Image> mImageList = new ArrayList<>();
    private ArrayAdapter<String> mImageListAdapter;

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
        mImageListAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, new ArrayList<String>());
        listView.setAdapter(mImageListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener != null) {
                    Image image = mImageList.get(position);
                    Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, image.id);
                    listener.onDrawingSelected(imageUri.toString());
                }
            }
        });

        getFileList();

        return view;
    }

    private void getFileList() {
        Log.d(TAG, "getFileList()");
        Uri collection;
        String[] projection;
        String selection;
        String[] selectionArgs;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
            projection = new String[]{
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.OWNER_PACKAGE_NAME
            };
            selection = MediaStore.Images.Media.OWNER_PACKAGE_NAME + " = ?";
            selectionArgs = new String[]{getContext().getPackageName()};
        } else {
            collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            projection = new String[]{
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DISPLAY_NAME,
            };
            selection = null;
            selectionArgs = null;
        }

        String sortOrder = MediaStore.Images.Media.DISPLAY_NAME + " ASC";

        try (Cursor cursor = getContext().getContentResolver().query(
                collection,
                projection,
                selection,
                selectionArgs,
                sortOrder
        )) {
            if (cursor != null) {
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                int packageColumn = 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    packageColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.OWNER_PACKAGE_NAME);
                }
                mImageList.clear();
                while (cursor.moveToNext()) {
                    long id = cursor.getLong(idColumn);
                    String name = cursor.getString(nameColumn);
                    String packageName = cursor.getString(packageColumn);
                    Uri contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                    Log.d(TAG, "id: " + id + " name: " + name + " package: " + packageName);
                    Image image = new Image(id, name);
                    mImageList.add(image);
                }
                ArrayList<String> imageNames = new ArrayList<>();
                for (Image image : mImageList) {
                    imageNames.add(image.name);
                }
                mImageListAdapter.clear();
                mImageListAdapter.addAll(imageNames);
            }
        }
    }
}
