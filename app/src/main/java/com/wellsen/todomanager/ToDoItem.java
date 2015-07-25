package com.wellsen.todomanager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Intent;

public class ToDoItem {
    public enum Priority {
        LOW, MED, HIGH
    }

    public enum Status {
        NOTDONE, DONE
    }

    public final static String TITLE = "title";
    public final static String DESCRIPTION = "description";
    public final static String PRIORITY = "priority";
    public final static String STATUS = "status";
    public final static String DATE = "date";
    public final static String EDIT = "edit";
    public final static String POSITION = "position";

    public final static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", Locale.US);

    public static final String ITEM_SEP = System.getProperty("line.separator");

    private String mTitle;
    private String mDescription;
    private Priority mPriority = Priority.LOW;
    private Status mStatus = Status.NOTDONE;
    private Date mDate = new Date();

    ToDoItem(String title, String description, Priority priority, Status status, Date date) {
        this.mTitle = title;
        this.mDescription = description;
        this.mPriority = priority;
        this.mStatus = status;
        this.mDate = date;
    }

    // Create a new ToDoItem from data packaged in an Intent
    ToDoItem(Intent intent) {
        mTitle = intent.getStringExtra(ToDoItem.TITLE);
        mDescription = intent.getStringExtra(ToDoItem.DESCRIPTION);
        mPriority = Priority.valueOf(intent.getStringExtra(ToDoItem.PRIORITY));
        mStatus = Status.valueOf(intent.getStringExtra(ToDoItem.STATUS));

        try {
            mDate = ToDoItem.TIME_FORMAT.parse(intent.getStringExtra(ToDoItem.DATE));
        } catch (ParseException e) {
            mDate = new Date();
        }
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDescription(){
        return mDescription;
    }

//    public void setDescription(String description) {
//        mDescription = description;
//    }

    public Priority getPriority() {
        return mPriority;
    }

//    public void setPriority(Priority priority) {
//        mPriority = priority;
//    }

    public Status getStatus() {
        return mStatus;
    }

    public void setStatus(Status status) {
        mStatus = status;
    }

    public Date getDate() {
        return mDate;
    }

//    public void setDate(Date date) {
//        mDate = date;
//    }

    // Take a set of String data values and
    // package them for transport in an Intent
    public static void packageIntent(Intent intent, String title, String description,
                                     Priority priority, Status status, String date,
                                     boolean edit, int pos) {
        intent.putExtra(ToDoItem.TITLE, title);
        intent.putExtra(ToDoItem.DESCRIPTION, description);
        intent.putExtra(ToDoItem.PRIORITY, priority.toString());
        intent.putExtra(ToDoItem.STATUS, status.toString());
        intent.putExtra(ToDoItem.DATE, date);
        intent.putExtra(ToDoItem.EDIT, edit);
        intent.putExtra(ToDoItem.POSITION, pos);
    }

    public String toString() {
        return mTitle + ITEM_SEP + mDescription + ITEM_SEP + mPriority + ITEM_SEP +
                mStatus + ITEM_SEP + TIME_FORMAT.format(mDate);
    }

    public String toLog() {
        return "Title:" + mTitle + ITEM_SEP + "Description:" + mDescription + ITEM_SEP +
                "Priority:" + mPriority + ITEM_SEP + "Status:" + mStatus + ITEM_SEP +
                "Date:" + TIME_FORMAT.format(mDate) + "\n";
    }
}