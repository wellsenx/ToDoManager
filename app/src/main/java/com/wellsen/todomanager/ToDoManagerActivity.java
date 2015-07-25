package com.wellsen.todomanager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wellsen.todomanager.ToDoItem.Priority;
import com.wellsen.todomanager.ToDoItem.Status;

public class ToDoManagerActivity extends ListActivity {
    private static final int ADD_TODO_REQUEST = 100;
    private static final String FILE_NAME = "ToDoManagerData.txt";
    private static final String TAG = "ToDoManagerActivity";
    private static final String TITLE_KEY = "Title";
    private static final String DESCRIPTION_KEY = "Description";
    private static final String STATUS_KEY = "Status";
    private static final String PRIORITY_KEY = "Priority";
    private static final String DATE_KEY = "Date";
    private static final String TIME_KEY = "Time";
    private static final String POSITION_KEY = "Position";
    private static final String EDIT_KEY = "edit";

    private Bundle extras;

    public final static SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    public final static SimpleDateFormat TIME_FORMAT =
            new SimpleDateFormat("HH:mm:ss", Locale.US);

    private ToDoListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.todo_manager);

        // Create a new TodoListAdapter for this ListActivity's ListView
        mAdapter = new ToDoListAdapter(getApplicationContext());

        // Attach the adapter to this ListActivity's ListView
        setListAdapter(mAdapter);
        registerForContextMenu(getListView());
    }

    @Override
    protected void onListItemClick (ListView l, View v, int position, long id){
        editToDoActivity(position);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "Entered onActivityResult");

        // Check result code and request code
        // if user submitted a new ToDoItem
        // Create a new ToDoItem from the data Intent
        // and then add it to the adapter
        if (requestCode == ADD_TODO_REQUEST) {
            if (resultCode == RESULT_OK) {
                extras = data.getExtras();
                if (extras.getBoolean(EDIT_KEY)) {
                    mAdapter.remove(extras.getInt(POSITION_KEY));
                    mAdapter.notifyDataSetChanged();
                }
                ToDoItem todoItem = new ToDoItem(data);
                mAdapter.add(todoItem);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    // Do not modify below here
    @Override
    public void onResume() {
        super.onResume();

        // Load saved ToDoItems, if necessary
        if (mAdapter.getCount() == 0)
            loadItems();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Save ToDoItems
        saveItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_todo_manager, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Intent intent = new Intent(getApplicationContext(), AddToDoActivity.class);
                startActivityForResult(intent, ADD_TODO_REQUEST);
                return true;
            case R.id.action_delete:
                mAdapter.clear();
                return true;
            case R.id.action_dump:
                dump();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.action_edit:
                editToDoActivity(info.position);
                return true;
            case R.id.action_delete:
                mAdapter.remove(info.position);
                mAdapter.notifyDataSetChanged();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void editToDoActivity (int pos) {
        Intent intent = new Intent(getApplicationContext(), AddToDoActivity.class);

        ToDoItem toDoItem = (ToDoItem) mAdapter.getItem(pos);
        intent.putExtra(TITLE_KEY, toDoItem.getTitle());
        intent.putExtra(DESCRIPTION_KEY, toDoItem.getDescription());
        intent.putExtra(STATUS_KEY, toDoItem.getStatus().toString());
        intent.putExtra(PRIORITY_KEY, toDoItem.getPriority().toString());

        Date date = toDoItem.getDate();
        intent.putExtra(DATE_KEY, DATE_FORMAT.format(date));
        intent.putExtra(TIME_KEY, TIME_FORMAT.format(date));
        intent.putExtra(POSITION_KEY, pos);

        startActivityForResult(intent, ADD_TODO_REQUEST);
    }

    private void dump() {
        for (int i = 0; i < mAdapter.getCount(); i++) {
            String data = ((ToDoItem) mAdapter.getItem(i)).toLog();
            Log.i(TAG,	"Item " + i + ": " + data.replace(ToDoItem.ITEM_SEP, ","));
        }
    }

    // Load stored ToDoItems
    private void loadItems() {
        BufferedReader reader = null;
        try {
            FileInputStream fis = openFileInput(FILE_NAME);
            reader = new BufferedReader(new InputStreamReader(fis));

            String title;
            String description;
            String priority;
            String status;
            Date date;

            while (null != (title = reader.readLine())) {
                description = reader.readLine();
                priority = reader.readLine();
                status = reader.readLine();
                date = ToDoItem.TIME_FORMAT.parse(reader.readLine());
                mAdapter.add(new ToDoItem(title, description, Priority.valueOf(priority),
                        Status.valueOf(status), date));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Save ToDoItems to file
    private void saveItems() {
        PrintWriter writer = null;
        try {
            FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(fos)));

            for (int idx = 0; idx < mAdapter.getCount(); idx++) {
                writer.println(mAdapter.getItem(idx));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != writer) {
                writer.close();
            }
        }
    }
}