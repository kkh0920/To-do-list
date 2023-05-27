package com.example.calendarapplication;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

// 사용자에게 보여지는 RecyclerView 와 데이터를 담고있는 ArrayList 사이를 매개하는 TaskAdapter(일정 관리를 담당)
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    public interface OnItemClickListener{
        void onCheckboxClick(int position, CompoundButton compoundButton, boolean isChecked); // 체크박스
        void onEditClick(View v, int position); //수정
        void onDeleteClick(View v, int position); //삭제
    }
    private OnItemClickListener mListener = null;
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    ArrayList<Task> items;

    public TaskAdapter(ArrayList<Task> items) {
        this.items = items;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.task_cell, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Task item = items.get(position);

        int deadline = Integer.parseInt(item.getDeadline());
        int estimatedDay = Integer.parseInt(item.getEstimatedDay());

        taskViewSetting(holder, estimatedDay);
        taskColorSetting(holder, deadline, estimatedDay);

        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(Task task){
        items.add(task);
    }

    public void removeItem(int position){
        items.remove(position);
    }

    public void setItem(int position, Task item){
        items.set(position, item);
    }
    public Task getItem(int position) {
        return items.get(position);
    }

    public void Sorting(){
        items.sort(Task::compareTo);
    }


    public void taskColorSetting(ViewHolder holder, int deadline, int estimatedDay){
        if(deadline - estimatedDay <= 0){
            holder.tv_hour.setTextColor(Color.parseColor("#FF4E4E"));
            holder.tv_time_format.setTextColor(Color.parseColor("#FF4E4E"));
            holder.tv_minute.setTextColor(Color.parseColor("#FF4E4E"));

            holder.tv_estimatedDay.setTextColor(Color.parseColor("#FF4E4E"));
            holder.tv_estimated_day_format.setTextColor(Color.parseColor("#FF4E4E"));

            holder.tv_deadline.setTextColor(Color.parseColor("#FF4E4E"));
            holder.tv_deadline_format.setTextColor(Color.parseColor("#FF4E4E"));
        }
        else{
            holder.tv_hour.setTextColor(Color.parseColor("#9397FB"));
            holder.tv_time_format.setTextColor(Color.parseColor("#9397FB"));
            holder.tv_minute.setTextColor(Color.parseColor("#9397FB"));

            holder.tv_estimatedDay.setTextColor(Color.parseColor("#9397FB"));
            holder.tv_estimated_day_format.setTextColor(Color.parseColor("#9397FB"));

            holder.tv_deadline.setTextColor(Color.parseColor("#7A7777"));
            holder.tv_deadline_format.setTextColor(Color.parseColor("#7A7777"));
        }
    }

    public void taskViewSetting(ViewHolder holder, int estimatedDay){
        if(estimatedDay == 0) {
            holder.tv_month.setVisibility(View.VISIBLE);
            holder.tv_day_format.setVisibility(View.VISIBLE);
            holder.tv_day.setVisibility(View.VISIBLE);
            holder.tv_divider.setVisibility(View.VISIBLE);
            holder.tv_hour.setVisibility(View.VISIBLE);
            holder.tv_time_format.setVisibility(View.VISIBLE);
            holder.tv_minute.setVisibility(View.VISIBLE);

            holder.tv_estimated_day_name.setVisibility(View.GONE);
            holder.tv_estimatedDay.setVisibility(View.GONE);
            holder.tv_estimated_day_format.setVisibility(View.GONE);

            holder.taskCardView.setCardBackgroundColor(Color.parseColor("#F7F7FF"));
        }
        else {
            holder.tv_month.setVisibility(View.GONE);
            holder.tv_day_format.setVisibility(View.GONE);
            holder. tv_day.setVisibility(View.GONE);
            holder.tv_divider.setVisibility(View.GONE);
            holder.tv_hour.setVisibility(View.GONE);
            holder.tv_time_format.setVisibility(View.GONE);
            holder.tv_minute.setVisibility(View.GONE);

            holder.tv_estimated_day_name.setVisibility(View.VISIBLE);
            holder.tv_estimatedDay.setVisibility(View.VISIBLE);
            holder.tv_estimated_day_format.setVisibility(View.VISIBLE);

            holder.taskCardView.setCardBackgroundColor(Color.parseColor("#FFF7F7"));
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardView taskCardView;
        private TextView tv_task_name;
        private TextView tv_month, tv_day_format, tv_day;
        private TextView tv_divider;
        private TextView tv_hour, tv_time_format, tv_minute;
        private TextView tv_deadline, tv_deadline_format;
        private TextView tv_estimated_day_name, tv_estimatedDay, tv_estimated_day_format;
        private CheckBox checkBox;
        private Button btn_delete, btn_edit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            initialized();
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        if (mListener != null){
                            mListener.onCheckboxClick(position, compoundButton, isChecked);
                        }
                    }
                }
            });

            btn_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int position = getAdapterPosition();
                    if (position!=RecyclerView.NO_POSITION){
                        if (mListener!=null){
                            mListener.onEditClick(view,position);
                        }
                    }
                }
            });

            btn_delete.setOnClickListener(new View.OnClickListener () {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position!=RecyclerView.NO_POSITION){
                        if (mListener!=null){
                            mListener.onDeleteClick(view,position);
                        }
                    }
                }
            });
        }

        public void initialized(){
            taskCardView = itemView.findViewById(R.id.task_card_view);

            tv_task_name = itemView.findViewById(R.id.tv_task_name);

            tv_month = itemView.findViewById(R.id.tv_month);
            tv_day_format = itemView.findViewById(R.id.tv_day_format);
            tv_day = itemView.findViewById(R.id.tv_day);

            tv_divider = itemView.findViewById(R.id.tv_divider);

            tv_hour = itemView.findViewById(R.id.tv_hour);
            tv_time_format = itemView.findViewById(R.id.tv_time_format);
            tv_minute = itemView.findViewById(R.id.tv_minute);

            tv_deadline = itemView.findViewById(R.id.tv_deadline);
            tv_deadline_format = itemView.findViewById(R.id.tv_deadline_format);

            tv_estimated_day_name = itemView.findViewById(R.id.tv_estimated_day_name);
            tv_estimatedDay = itemView.findViewById(R.id.tv_estimated_day);
            tv_estimated_day_format = itemView.findViewById(R.id.tv_estimated_day_format);

            checkBox = itemView.findViewById(R.id.checkBox);

            btn_delete = itemView.findViewById(R.id.btn_delete);

            btn_edit = itemView.findViewById(R.id.btn_edit);
        }

        public void setItem(final Task item){
            tv_task_name.setText(item.getTaskName());

            if(item.getDeadline().equals("0")) {
                tv_deadline.setText("Day");
            }
            else {
                tv_deadline.setText(item.getDeadline());
            }

            if(item.getEstimatedDay().equals("0")){
                tv_month.setText(item.getMonth());
                tv_day.setText(item.getDay());
                tv_hour.setText(item.getHour());
                tv_minute.setText(item.getMinute());
            }
            else {
                tv_estimatedDay.setText(item.getEstimatedDay());
            }

            checkBox.setChecked(item.isChecked());
        }
    }
}
