package com.hudzah.snapapaper;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.TaskExecutor;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;



public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {

    private ArrayList<ListItem> mList;
    private OnItemClickListener mListener;


    public interface OnItemClickListener{

        void OnItemClick(int position);
        void OnDeleteClick(int position);
        void OnLongClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){

        mListener = listener;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false);
        RecyclerViewHolder rvh = new RecyclerViewHolder(v, mListener);
        return rvh;
    }

    public RecyclerViewAdapter(ArrayList<ListItem> listItems){

        mList = listItems;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {

        ListItem currentItem = mList.get(position);

        holder.subjectName.setText(currentItem.getSubjectName());

        holder.examPaperCode.setText(currentItem.getExamCode());

        holder.examLevel.setText(currentItem.getExamLevel());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder{

        public TextView subjectName;
        public TextView examPaperCode;
        public TextView examLevel;
        public CardView cardView;
        public ImageView deleteButton;

        public RecyclerViewHolder(@NonNull View itemView, OnItemClickListener listener) {

            super(itemView);
            subjectName = (TextView)itemView.findViewById(R.id.subjectName);
            examPaperCode = (TextView)itemView.findViewById(R.id.examPaperCode);
            examLevel = (TextView)itemView.findViewById(R.id.examLevel);
            cardView = (CardView)itemView.findViewById(R.id.cardView);
            deleteButton = (ImageView)itemView.findViewById(R.id.deleteButton);


            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(listener != null) {

                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            listener.OnItemClick(position);
                        }
                    }
                }
            });

            cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(listener != null) {

                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            listener.OnLongClick(position);
                            return true;
                        }
                    }

                    return false;
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null) {

                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            listener.OnDeleteClick(position);
                        }
                    }
                }
            });
        }
    }
}




