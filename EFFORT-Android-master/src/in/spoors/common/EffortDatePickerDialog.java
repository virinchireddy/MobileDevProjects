package in.spoors.common;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;

public class EffortDatePickerDialog extends DatePickerDialog {

	private int usedForViewId;
	private Calendar uncommittedTime;

	public EffortDatePickerDialog(int usedForViewId, Context context,
			int theme, OnDateSetListener callBack, Calendar calendar) {
		super(context, theme, callBack, calendar.get(Calendar.YEAR), calendar
				.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		this.usedForViewId = usedForViewId;
		this.uncommittedTime = (Calendar) calendar.clone();
	}

	public EffortDatePickerDialog(int usedForViewId, Context context,
			OnDateSetListener callBack, Calendar calendar) {
		super(context, callBack, calendar.get(Calendar.YEAR), calendar
				.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		this.usedForViewId = usedForViewId;
		this.uncommittedTime = (Calendar) calendar.clone();
	}

	@Override
	public void onDateChanged(DatePicker view, int year, int month, int day) {
		super.onDateChanged(view, year, month, day);
		uncommittedTime.set(Calendar.YEAR, year);
		uncommittedTime.set(Calendar.MONTH, month);
		uncommittedTime.set(Calendar.DAY_OF_MONTH, day);
	}

	public Calendar getUncommittedTime() {
		return uncommittedTime;
	}

	public void setUncommittedTime(Calendar uncommittedDate) {
		this.uncommittedTime = uncommittedDate;
	}

	public int getUsedForViewId() {
		return usedForViewId;
	}

	public void setUsedForViewId(int usedForViewId) {
		this.usedForViewId = usedForViewId;
	}
}
