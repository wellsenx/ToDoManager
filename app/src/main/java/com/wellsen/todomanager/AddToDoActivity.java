package com.wellsen.todomanager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import com.wellsen.todomanager.ToDoItem.Priority;
import com.wellsen.todomanager.ToDoItem.Status;

public class AddToDoActivity extends Activity {
    //private static final String TAG = "AddToDoActivity";

    // 7 days in milliseconds - 7 * 24 * 60 * 60 * 1000
    private static final int SEVEN_DAYS = 604800000;
    public final static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", Locale.US);

    private static String timeString;
    private static String dateString;
    private static TextView dateView;
    private static TextView timeView;

    private RadioGroup mPriorityRadioGroup;
    private RadioGroup mStatusRadioGroup;
    private EditText mTitleText;
    private EditText mDescriptionText;
    private RadioButton mStatusButtonDone;
    private RadioButton mStatusButtonNotDone;
    private RadioButton mPriorityButtonHigh;
    private RadioButton mPriorityButtonMed;
    private RadioButton mPriorityButtonLow;

    private static final String TITLE_KEY = "Title";
    private static final String DESCRIPTION_KEY = "Description";
    private static final String STATUS_KEY = "Status";
    private static final String PRIORITY_KEY = "Priority";
    private static final String DATE_KEY = "Date";
    private static final String TIME_KEY = "Time";
    private static final String POSITION_KEY = "Position";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_todo);

        initializeVariables();

        if (savedInstanceState==null) {
            Bundle extras = getIntent().getExtras();
            if (extras!=null) {
                mTitleText.setText(extras.getString(TITLE_KEY));
                mDescriptionText.setText(extras.getString(DESCRIPTION_KEY));
                switch (extras.getString(STATUS_KEY)) {
                    case "DONE":
                        mStatusButtonDone.setChecked(true);
                        break;
                    case "NOTDONE":
                        mStatusButtonNotDone.setChecked(true);
                        break;
                    default:
                        break;
                }
                switch (extras.getString(PRIORITY_KEY)) {
                    case "LOW":
                        mPriorityButtonLow.setChecked(true);
                        break;
                    case "MED":
                        mPriorityButtonMed.setChecked(true);
                        break;
                    case "HIGH":
                        mPriorityButtonHigh.setChecked(true);
                        break;
                    default:
                        break;
                }
                dateView.setText(extras.getString(DATE_KEY));
                timeView.setText(extras.getString(TIME_KEY));
            } else {
                // Set the default date and time
                setDefaultDateTime();
            }
        }

        // OnClickListener for the Date TextView, calls showDatePickerDialog() to
        // show the Date dialog
        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // OnClickListener for the Time TextView, calls showDatePickerDialog() to
        // show the Date dialog
        timeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        // OnClickListener for the Cancel Button,
        final Button cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Indicate result and finish
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        // Set up OnClickListener for the Reset Button
        final Button resetButton = (Button) findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reset data to default values
                mTitleText.setText("");
                mDescriptionText.setText("");
                mPriorityButtonMed.setChecked(true);
                mStatusButtonNotDone.setChecked(true);

                // reset date and time
                setDefaultDateTime();
            }
        });

        // Set up OnClickListener for the Submit Button
        final Button submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // gather ToDoItem data
                // Get the current Priority
                Priority priority = getPriority();

                // Get the current Status
                Status status = getStatus();

                // Get the current ToDoItem Title
                String titleString = getToDoTitle();

                // Get the current ToDoItem Description
                String descriptionString = getToDoDescription();

                // Construct the Date string
                String fullDate = dateString + " " + timeString;

                Bundle extras = getIntent().getExtras();
                boolean edit = false;
                int pos = 0;
                if (extras != null) {
                    edit = true;
                    pos = extras.getInt(POSITION_KEY);
                }

                // Package ToDoItem data into an Intent
                Intent data = new Intent();
                ToDoItem.packageIntent(data, titleString, descriptionString,
                        priority, status, fullDate, edit, pos);

                // return data Intent and finish
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }

    private void initializeVariables() {
        mTitleText = (EditText) findViewById(R.id.title);
        mDescriptionText = (EditText) findViewById(R.id.description);
        mStatusButtonDone = (RadioButton) findViewById(R.id.statusDone);
        mStatusButtonNotDone = (RadioButton) findViewById(R.id.statusNotDone);
        mPriorityButtonHigh = (RadioButton) findViewById(R.id.highPriority);
        mPriorityButtonMed = (RadioButton) findViewById(R.id.medPriority);
        mPriorityButtonLow = (RadioButton) findViewById(R.id.lowPriority);
        mPriorityRadioGroup = (RadioGroup) findViewById(R.id.priorityGroup);
        mStatusRadioGroup = (RadioGroup) findViewById(R.id.statusGroup);
        dateView = (TextView) findViewById(R.id.date);
        timeView = (TextView) findViewById(R.id.time);
    }

    // Do not modify below this point.
    private void setDefaultDateTime() {
        // Default is current time + 7 days
        Date mDate = new Date();
        mDate = new Date(mDate.getTime() + SEVEN_DAYS);

        Calendar c = Calendar.getInstance();
        c.setTime(mDate);

        setDateString(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        dateView.setText(dateString);

        setTimeString(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));

        timeView.setText(timeString);
    }

    private static void setDateString(int year, int monthOfYear, int dayOfMonth) {
        // Increment monthOfYear for Calendar/Date -> Time Format setting
        monthOfYear++;
        String mon = "" + monthOfYear;
        String day = "" + dayOfMonth;

        if (monthOfYear < 10)
            mon = "0" + monthOfYear;
        if (dayOfMonth < 10)
            day = "0" + dayOfMonth;

        dateString = year + "-" + mon + "-" + day;
    }

    private static void setTimeString(int hourOfDay, int minute) {
        String hour = "" + hourOfDay;
        String min = "" + minute;

        if (hourOfDay < 10)
            hour = "0" + hourOfDay;
        if (minute < 10)
            min = "0" + minute;

        timeString = hour + ":" + min + ":00";
    }

    private Priority getPriority() {
        switch (mPriorityRadioGroup.getCheckedRadioButtonId()) {
            case R.id.lowPriority: {
                return Priority.LOW;
            }
            case R.id.highPriority: {
                return Priority.HIGH;
            }
            default: {
                return Priority.MED;
            }
        }
    }

    private Status getStatus() {
        switch (mStatusRadioGroup.getCheckedRadioButtonId()) {
            case R.id.statusDone: {
                return Status.DONE;
            }
            default: {
                return Status.NOTDONE;
            }
        }
    }

    private String getToDoTitle() {
        return mTitleText.getText().toString();
    }

    private String getToDoDescription() {
        return mDescriptionText.getText().toString();
    }

    // DialogFragment used to pick a ToDoItem deadline date
    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            setDateString(year, monthOfYear, dayOfMonth);

            dateView.setText(dateString);
        }
    }

    // DialogFragment used to pick a ToDoItem deadline time
    public static class TimePickerFragment extends DialogFragment implements
            TimePickerDialog.OnTimeSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return
            return new TimePickerDialog(getActivity(), this, hour, minute, true);
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            setTimeString(hourOfDay, minute);

            timeView.setText(timeString);
        }
    }

    private void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    private void showTimePickerDialog() {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }
}