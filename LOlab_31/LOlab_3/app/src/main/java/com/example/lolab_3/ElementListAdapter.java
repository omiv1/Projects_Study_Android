package com.example.lolab_3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ElementListAdapter extends RecyclerView.Adapter<ElementListAdapter.ElementViewHolder> {
    private LayoutInflater mLayoutInflater;
    private List<Element> mElementList;

    public ElementListAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        this.mElementList = null;
    }

    @NonNull
    @Override
    public ElementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.element_item, parent, false);
        return new ElementViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ElementViewHolder holder, int position) {
        if (mElementList != null) {
            Element current = mElementList.get(position);
            holder.bind(current);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(holder.itemView.getContext(), InsertActivity.class);
                    intent.putExtra("elementId", current.getId());
                    ((Activity) holder.itemView.getContext()).startActivityForResult(intent, MainActivity.NEW_ELEMENT_ACTIVITY_REQUEST_CODE);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (mElementList != null)
            return mElementList.size();
        return 0;
    }

    public void setElementList(List<Element> elementList) {
        this.mElementList = elementList;
        notifyDataSetChanged();
    }

    public Element getElementAt(int position) {
        return mElementList.get(position);
    }

    class ElementViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewElementName;

        ElementViewHolder(View itemView) {
            super(itemView);
            textViewElementName = itemView.findViewById(R.id.textView_element_name);
        }

        void bind(Element element) {
            textViewElementName.setText(element.getManufacturer() + " " + element.getModel());
        }
    }
}