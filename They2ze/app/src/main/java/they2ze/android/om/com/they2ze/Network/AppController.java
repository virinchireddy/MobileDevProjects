package they2ze.android.om.com.they2ze.Network;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;


public class AppController extends Application {

	public static final String TAG = AppController.class
            .getSimpleName();
 
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
   
    private static AppController mInstance;



	// update_id
	private String projectUpdateIdGlobal;

	public String getUpdateVariable() {
		return projectUpdateIdGlobal;
	}

	public void setUpdateVariable(String projectUpdateIdGlobal) {
		this.projectUpdateIdGlobal = projectUpdateIdGlobal;
	}

// new variable
	private String New ;

	public String getNew(){return New ;}

	public void setNew(String nw) {
		this.New = nw ;
	}




    private MyPreferenceManager pref;

	@Override
	public void onCreate() {
		super.onCreate();
		//Fabric.with(this, new Crashlytics());
		mInstance = this;
	}

	public static synchronized AppController getInstance() {
		return mInstance;
	}

	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
			mRequestQueue.getCache().clear();
			
		}

		return mRequestQueue;
	}

	public MyPreferenceManager getPrefManager() {
        if (pref == null) {
            pref = new MyPreferenceManager(this);
        }
 
        return pref;
    }
	
	public ImageLoader getImageLoader() {
        getRequestQueue();
       // getRequestQueue().getCache().clear();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new MySingleton());
        }
        return this.mImageLoader;
    }
	
	public <T> void addToRequestQueue(Request<T> req, String tag) {
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		getRequestQueue().add(req);
	}

	public <T> void addToRequestQueue(Request<T> req) {
		req.setTag(TAG);
		getRequestQueue().add(req);
	}

	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}
	
	

    public static Context getAppContext() {
        return mInstance.getApplicationContext();
    }

}