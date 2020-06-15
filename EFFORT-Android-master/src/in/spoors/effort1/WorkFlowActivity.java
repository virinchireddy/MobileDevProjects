package in.spoors.effort1;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class WorkFlowActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_work_flow);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.work_flow, menu);
		return true;
	}

}
