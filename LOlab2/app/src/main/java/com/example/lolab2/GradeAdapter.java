// GradeAdapter.java
package com.example.lolab2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GradeAdapter extends RecyclerView.Adapter<GradeAdapter.GradeViewHolder> {

    private ArrayList<String> subjects;
    private ArrayList<Integer> grades;
    private OnGradeChangeListener onGradeChangeListener;

    public interface OnGradeChangeListener {
        void onGradeChange();
    }

    public GradeAdapter(ArrayList<String> subjects, ArrayList<Integer> grades, OnGradeChangeListener onGradeChangeListener) {
        this.subjects = subjects;
        this.grades = grades;
        this.onGradeChangeListener = onGradeChangeListener;
    }

    @NonNull
    @Override
    public GradeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grade, parent, false);
        return new GradeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GradeViewHolder holder, int position) {
        holder.subjectTextView.setText(subjects.get(position));
        holder.radioGroup.setOnCheckedChangeListener(null);
        holder.radioGroup.clearCheck();
        int grade = grades.get(position);
        if (grade >= 2) {
            int index = grade - 2;
            if (index >= 0 && index < holder.radioGroup.getChildCount()) {
                ((RadioButton) holder.radioGroup.getChildAt(index)).setChecked(true);
            }
        }
        holder.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int gradeChange = Integer.parseInt(((RadioButton) group.findViewById(checkedId)).getText().toString());
            grades.set(position, gradeChange);
            onGradeChangeListener.onGradeChange();
        });
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    public static class GradeViewHolder extends RecyclerView.ViewHolder {

        TextView subjectTextView;
        RadioGroup radioGroup;

        public GradeViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectTextView = itemView.findViewById(R.id.subjectTextView);
            radioGroup = itemView.findViewById(R.id.radioGroup);
        }
    }
}