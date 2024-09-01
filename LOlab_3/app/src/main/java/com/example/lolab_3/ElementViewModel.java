package com.example.lolab_3;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

// ElementViewModel.java
public class ElementViewModel extends AndroidViewModel {
    private final ElementRepository mRepository;
    private final LiveData<List<Element>> mAllElements;

    public ElementViewModel(@NonNull Application application) {
        super(application);
        mRepository = new ElementRepository(application);
        mAllElements = mRepository.getAllElements();
    }
    public LiveData<Element> getElementById(long id) {
        return mRepository.getElementById(id);
    }

    LiveData<List<Element>> getAllElements() {
        return mAllElements;
    }

    public void deleteAll() {
        mRepository.deleteAll();
    }
    public void delete(Element element) {
        mRepository.delete(element);
    }
    public void insert(Element element) {
        mRepository.insert(element);
    }
    public void update(Element element) {
        mRepository.update(element);
    }
}