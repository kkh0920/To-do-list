package com.example.calendarapplication;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

// 사용자에게 보여지는 RecyclerView 와 데이터를 담고있는 ArrayList 사이를 매개하는 TaskAdapter(일정 관리를 담당)
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder>{
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

        holder.setItem(item);

        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(item.isChecked());
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                Task task = items.get(holder.getAdapterPosition());
                final int sID = task.getId();

                /* 체크박스 상태 업데이트 => 원래 데이터베이스는 메인 스레드에서 접근하면 안되지만, 간단한 구현을 위해
                   allowMainThreadQueries() 구문을 사용.*/
                TaskDB updatedTask = TaskDB.getInstance(compoundButton.getContext());

                updatedTask.taskDao().updateCheckBox(sID, isChecked);

                item.setIsChecked(isChecked);
                notifyDataSetChanged();
            }
        });

        holder.btn_delete.setTag(holder.getAdapterPosition());
        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int pos = (int) view.getTag();

                /* 삭제 => 원래 데이터베이스는 메인 스레드에서 접근하면 안되지만, 간단한 구현을 위해
                   allowMainThreadQueries() 구문을 사용.*/
                TaskDB deletedTask = TaskDB.getInstance(view.getContext());

                deletedTask.taskDao().delete(items.get(pos));

                removeItem(pos);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(Task item){
        items.add(item);
    }

    public void removeItem(int position){
        items.remove(position);
    }

    public void setItems(ArrayList<Task> items) {
        this.items = items;
    }

    public Task getItems(int position) {
        return items.get(position);
    }

    public void setItem(int position, Task item){
        items.set(position, item);
    }

    public void Sorting(){
        items.sort(Task::compareTo);
    }


    static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_task_name;
        private TextView tv_month;
        private TextView tv_day;
        private CheckBox checkBox;
        private Button btn_delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_task_name = itemView.findViewById(R.id.tv_task_name);
            tv_month = itemView.findViewById(R.id.tv_month);
            tv_day = itemView.findViewById(R.id.tv_day);
            checkBox = itemView.findViewById(R.id.checkBox);
            btn_delete = itemView.findViewById(R.id.btn_delete);
        }
        public void setItem(final Task item){
            tv_task_name.setText(item.getTaskName());
            tv_month.setText(item.getMonth());
            tv_day.setText(item.getDay());
        }
    }
}
