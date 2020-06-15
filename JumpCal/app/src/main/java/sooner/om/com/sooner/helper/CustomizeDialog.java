package sooner.om.com.sooner.helper;

import android.app.Dialog;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import sooner.om.com.sooner.R;


/**
 * Class Must extends with Dialog
 */

/**
 * Implement onClickListener to dismiss dialog when OK Button is pressed
 */
public class CustomizeDialog extends Dialog implements OnClickListener {
    private View v = null;
    private AsyncTaskInterface mAsyncTaskInterface;
    private String type;
    private TextView okButton;
    private Context mContext;
    private TextView mTitle = null;
    private TextView mMessage = null;

    //endregion
    public CustomizeDialog(Context context) {
        super(context);
        mContext = context;
        /** 'Window.FEATURE_NO_TITLE' - Used to hide the mTitle */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /** Design the dialog in main.xml file */
        setContentView(R.layout.dialogue_box);
        v = getWindow().getDecorView();
        v.setBackgroundResource(android.R.color.transparent);
        mTitle = (TextView) findViewById(R.id.tvDialogueHeadding);
        mTitle.setVisibility(View.GONE);
        mMessage = (TextView) findViewById(R.id.tvDialogueMessage);
        okButton = (TextView) findViewById(R.id.btnOk);
        okButton.setOnClickListener(this);
    }

    public CustomizeDialog(Context context, String screenType, AsyncTaskInterface ai) {
        super(context);
        mContext = context;
        type = screenType;
        mAsyncTaskInterface = ai;
        /** 'Window.FEATURE_NO_TITLE' - Used to hide the mTitle */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /** Design the dialog in main.xml file */
        setContentView(R.layout.dialogue_box);
        v = getWindow().getDecorView();
        v.setBackgroundResource(android.R.color.transparent);
        mTitle = (TextView) findViewById(R.id.tvDialogueHeadding);
        mTitle.setVisibility(View.GONE);
        mMessage = (TextView) findViewById(R.id.tvDialogueMessage);
        okButton = (TextView) findViewById(R.id.btnOk);
        okButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        /** When OK Button is clicked, dismiss the dialog */
        if (v == okButton) {

            if (type != null) {
                if (type.equalsIgnoreCase("requestAppointment")) {
                    dismiss();
                    mAsyncTaskInterface.onAsyncTaskInterfaceResponse("requestAppointment"); // pass data what you want here

                } else if (type.equalsIgnoreCase("profile")) {
                    dismiss();
                    mAsyncTaskInterface.onAsyncTaskInterfaceResponse("profile");// pass data what you want here
                } else {
                    dismiss();
                }
            } else {
                //LoginActivityMain.password_Pinid.setText("");
                dismiss();
            }


        }
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        mTitle.setVisibility(View.VISIBLE);
        mTitle.setText(title);
    }

    @Override
    public void setTitle(int titleId) {
        super.setTitle(titleId);
        mTitle.setText(mContext.getResources().getString(titleId));
    }

    /**
     * Set the message text for this dialog's window.
     *
     * @param message - The new message to display in the title.
     */
    public void setMessage(CharSequence message) {
        mMessage.setText(message);
        mMessage.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    /**
     * Set the message text for this dialog's window. The text is retrieved from the resources with the supplied
     * identifier.
     *
     * @param messageId - the message's text resource identifier <br>
     * @see <b>Note : if resourceID wrong application may get crash.</b><br>
     * Exception has not handle.
     */
    public void setMessage(int messageId) {
        mMessage.setText(mContext.getResources().getString(messageId));
        mMessage.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    public interface AsyncTaskInterface {

        public void onAsyncTaskInterfaceResponse(String result);

    }
}