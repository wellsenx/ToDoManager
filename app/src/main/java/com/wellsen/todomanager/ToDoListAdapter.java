package com.wellsen.todomanager;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.wellsen.todomanager.ToDoItem.Status;

public class ToDoListAdapter extends BaseAdapter {
    //private static final String TAG = "ToDoListAdapter";

    private final List<ToDoItem> mItems = new ArrayList<>();
    private final Context mContext;

    public ToDoListAdapter(Context context) {
        mContext = context;
    }

    // Add a ToDoItem to the adapter
    // Notify observers that the data set has changed
    public void add(ToDoItem item) {
        mItems.add(item);
        notifyDataSetChanged();
    }

    // Clears the list adapter of all items.
    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    public void remove(int pos) {
        mItems.remove(pos);
        notifyDataSetChanged();
    }

    // Returns the number of ToDoItems
    @Override
    public int getCount() {
        return mItems.size();
    }

    // Retrieve the number of ToDoItems
    @Override
    public Object getItem(int pos) {
        return mItems.get(pos);
    }

    // Get the ID for the ToDoItem
    // In this case it's just the position
    @Override
    public long getItemId(int pos) {
        return pos;
    }

    // Create a View for the ToDoItem at specified position
    // Remember to check whether convertView holds an already allocated View
    // before created a new View.
    // Consider using the ViewHolder pattern to make scrolling more efficient
    // See: http://developer.android.com/training/improving-layouts/smooth-scrolling.html
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;

        // Get the current ToDoItem
        final ToDoItem toDoItem = (ToDoItem) getItem(position);

        if (convertView == null) {
            // Inflate the View for this ToDoItem
            // from todo_item.xml
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.todo_item, parent, false);

            // Fill in specific ToDoItem data
            // Remember that the data that goes in this View
            // corresponds to the user interface elements defined
            // in the layout file

            // initialize the view holder
            viewHolder = new ViewHolder();

            // Get references to each view
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.titleView);
            viewHolder.tvDescription = (TextView) convertView.findViewById(R.id.descriptionView);
            viewHolder.cbStatus = (CheckBox) convertView.findViewById(R.id.statusCheckBox);
            viewHolder.tvPriority = (TextView) convertView.findViewById(R.id.priorityView);
            viewHolder.tvDate = (TextView) convertView.findViewById(R.id.dateView);

            // Must also set up an OnCheckedChangeListener,
            // which is called when the user toggles the status checkbox
            viewHolder.cbStatus.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        toDoItem.setStatus(Status.DONE);
                    } else
                        toDoItem.setStatus(Status.NOTDONE);
                }
            });

            convertView.setTag(viewHolder);
        } else {
            // recycle the already inflated view
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Display data
        viewHolder.tvTitle.setText(toDoItem.getTitle());
        viewHolder.tvDescription.setText(toDoItem.getDescription());
        viewHolder.cbStatus.setChecked(toDoItem.getStatus() == Status.DONE);
        viewHolder.tvPriority.setText(toDoItem.getPriority().toString());
        viewHolder.tvDate.setText(ToDoItem.TIME_FORMAT.format(toDoItem.getDate()));

        // Return the View you just created
        return convertView;
    }

    private class ViewHolder {
        TextView tvTitle;
        TextView tvDescription;
        CheckBox cbStatus;
        TextView tvPriority;
        TextView tvDate;
    }
}