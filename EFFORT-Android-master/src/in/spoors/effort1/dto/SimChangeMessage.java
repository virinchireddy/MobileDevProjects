package in.spoors.effort1.dto;

import in.spoors.effort1.Utils;
import in.spoors.effort1.provider.EffortProvider.SimCardChangeMessages;

import java.io.Serializable;

import android.content.ContentValues;
import android.database.Cursor;

public class SimChangeMessage implements Serializable {

	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private static final String TAG = "SimChangeMessage";

	private Long id;
	private String receiverNumber;
	private String messageBody;
	private String sentStatus;

	public SimChangeMessage() {
	}

	public SimChangeMessage(String receiverNumber, String messageBody,
			String sentStatus) {
		this.receiverNumber = receiverNumber;
		this.messageBody = messageBody;
		this.sentStatus = sentStatus;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getReceiverNumber() {
		return receiverNumber;
	}

	public void setReceiverNumber(String receiverNumber) {
		this.receiverNumber = receiverNumber;
	}

	public String getMessageBody() {
		return messageBody;
	}

	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}

	public String getSentStatus() {
		return sentStatus;
	}

	public void setSentStatus(String sentStatus) {
		this.sentStatus = sentStatus;
	}

	@Override
	public String toString() {
		return "SimChangeMessage [id=" + id + ", receiverNumber="
				+ receiverNumber + ", messageBody=" + messageBody
				+ ", sentStatus=" + sentStatus + "]";
	}

	/**
	 * 
	 * @param values
	 *            fills this object, instead of creating a new one
	 * @return
	 */
	public ContentValues getContentValues(ContentValues values) {
		if (values == null) {
			values = new ContentValues();
		}

		if (id != null) {
			values.put(SimCardChangeMessages._ID, id);
		}

		Utils.putNullOrValue(values, SimCardChangeMessages.RECEIVER_NUMBER,
				receiverNumber);
		Utils.putNullOrValue(values, SimCardChangeMessages.MESSAGE_BODY,
				messageBody);
		Utils.putNullOrValue(values, SimCardChangeMessages.STATUS, sentStatus);

		return values;
	}

	public void load(Cursor cursor) {

		id = cursor.isNull(SimCardChangeMessages._ID_INDEX) ? null : cursor
				.getLong(SimCardChangeMessages._ID_INDEX);
		receiverNumber = cursor
				.getString(SimCardChangeMessages.RECEIVER_NUMBER_INDEX);
		messageBody = cursor
				.getString(SimCardChangeMessages.MESSAGE_BODY_INDEX);
		sentStatus = cursor.getString(SimCardChangeMessages.STATUS_INDEX);
	}
}
