package in.spoors.common;

import java.util.Calendar;

import android.app.TimePickerDialog;
import android.content.Context;
import android.text.format.DateFormat;
import android.widget.TimePicker;

public class EffortTimePickerDialog extends TimePickerDialog {

	private int usedForViewId;
	private Calendar uncommittedTime;

	public EffortTimePickerDialog(int usedForViewId, Context context,
			OnTimeSetListener callBack, Calendar calendar) {
		super(context, callBack, calendar.get(Calendar.HOUR_OF_DAY), calendar
				.get(Calendar.MINUTE), DateFormat.is24HourFormat(context));

		this.usedForViewId = usedForViewId;
		this.uncommittedTime = (Calendar) calendar.clone();
	}

	public EffortTimePickerDialog(int usedForViewId, Context context,
			int theme, OnTimeSetListener callBack, Calendar calendar) {
		super(context, theme, callBack, calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE), DateFormat
						.is24HourFormat(context));
		this.usedForViewId = usedForViewId;
		this.uncommittedTime = (Calendar) calendar.clone();
	}

	@Override
	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
		super.onTimeChanged(view, hourOfDay, minute);
		uncommittedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
		uncommittedTime.set(Calendar.MINUTE, minute);
	}

	public int getUsedForViewId() {
		return usedForViewId;
	}

	public void setUsedForViewId(int usedForViewId) {
		this.usedForViewId = usedForViewId;
	}

	public Calendar getUncommittedTime() {
		return uncommittedTime;
	}

	public void setUncommittedTime(Calendar uncommittedTime) {
		this.uncommittedTime = uncommittedTime;
	}
}
