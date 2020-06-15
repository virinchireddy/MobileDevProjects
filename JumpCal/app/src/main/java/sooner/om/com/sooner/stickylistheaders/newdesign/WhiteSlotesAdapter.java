package sooner.om.com.sooner.stickylistheaders.newdesign;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import sooner.om.com.sooner.R;
import sooner.om.com.sooner.helper.NewAlertScreen;
import sooner.om.com.sooner.helper.Others;
import sooner.om.com.sooner.stickylistheaders.StickyListHeadersAdapter;


public class WhiteSlotesAdapter extends BaseAdapter implements
        StickyListHeadersAdapter, SectionIndexer {

    private Context mContext;
    private int[] mSectionIndices;
    private String[] mSectionLetters;
    private LayoutInflater mInflater;
    private List<NewAlertScreen> list;

    WhiteSlotesAdapter(Context context, List<NewAlertScreen> list) {
        // assigning data to local variables
        mContext = context;
        mInflater = LayoutInflater.from(context);
        this.list = list;

        if (this.list.size() > 0) {
            mSectionIndices = getSectionIndices();
            mSectionLetters = getSectionDates();
        }
    }

    // changing the time zone
    @SuppressLint("SimpleDateFormat")
    public static String changeTimeFormat(String dataOld) {


        Date dateObj = null;

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        try {
            dateObj = sdf.parse(dataOld);
        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return new SimpleDateFormat("hh:mm a").format(dateObj);
    }

    private int[] getSectionIndices() {
        ArrayList<Integer> sectionIndices = new ArrayList<>();
        //char lastFirstChar = mCountries[0].charAt(0);
        String lastFirstDate = list.get(0).getDate();

        sectionIndices.add(0);
        //Log.v("swamy testingt",Long.parseLong("2016-08-17")+"");
        for (int i = 1; i < list.size(); i++) {
            if (!list.get(i).getDate().equals(lastFirstDate)) {
                lastFirstDate = list.get(i).getDate();
                sectionIndices.add(i);
                //Log.v("section list",lastFirstChar+"" );

            }
        }
        Log.v("section list", sectionIndices.size() + "");
        int[] sections = new int[sectionIndices.size()];
        for (int i = 0; i < sectionIndices.size(); i++) {
            sections[i] = sectionIndices.get(i);
            Log.v("section list integer", sections[i] + "");
        }
        return sections;
    }

    private String[] getSectionDates() {
        String[] letters = new String[mSectionIndices.length];


        for (int i = 0; i < mSectionIndices.length; i++) {
            letters[i] = list.get(mSectionIndices[i]).getDate();
        }


        return letters;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // linking the ui elements to adapter class
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        // if the view is empty this will run
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.new_notification_item, parent, false);
            holder.tvPhyName = (TextView) convertView.findViewById(R.id.tvPhyName);
            holder.tvLocation = (TextView) convertView.findViewById(R.id.tvLocation);
            holder.tvSpeaciality = (TextView) convertView.findViewById(R.id.tvSpeaciality);
            holder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
            holder.liView1 = (LinearLayout) convertView.findViewById(R.id.liView1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        // add all the data to the view
        holder.tvPhyName.setText(list.get(position).getPhysicanName());
        holder.tvLocation.setText(list.get(position).getLocation());
        holder.tvSpeaciality.setText(list.get(position).getSpecialityName());
        holder.tvTime.setText(changeTimeFormat(list.get(position).getTime()));
        // if the user is already read this appointment change the color
        if (list.get(position).getReadStatus().equals("true")) {
            Others.displayBackGround(holder.liView1, R.drawable.gray_curve_selector, mContext);
        } else {
            holder.liView1.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        }

        return convertView;
    }

    // linking UI of header to java class
    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;

        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = mInflater.inflate(R.layout.header, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.text1);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        // set header text as first char in name
        String headerChar = list.get(position).getDate();
        holder.text.setText(Others.changeDateFormat(headerChar));

        return convertView;
    }

    /**
     * Remember that these have to be static, postion=1 should always return
     * the same Id that is.
     */
    @Override
    public long getHeaderId(int position) {
        // return the first character of the country as ID because this is what
        // headers are based upon
        return Long.parseLong(list.get(position).getDate().replace("-", ""));
    }

    @Override
    public int getPositionForSection(int section) {
        if (mSectionIndices.length == 0) {
            return 0;
        }

        if (section >= mSectionIndices.length) {
            section = mSectionIndices.length - 1;
        } else if (section < 0) {
            section = 0;
        }
        return mSectionIndices[section];
    }

    @Override
    public int getSectionForPosition(int position) {
        for (int i = 0; i < mSectionIndices.length; i++) {
            if (position < mSectionIndices[i]) {
                return i - 1;
            }
        }
        return mSectionIndices.length - 1;
    }

    @Override
    public Object[] getSections() {
        return mSectionLetters;
    }

    public void clear() {
        mSectionIndices = new int[0];
        mSectionLetters = new String[0];
        notifyDataSetChanged();
    }

    public void restore() {
        mSectionIndices = getSectionIndices();
        mSectionLetters = getSectionDates();
        notifyDataSetChanged();
    }

    private class HeaderViewHolder {
        TextView text;

    }

    private class ViewHolder {
        TextView tvPhyName;
        TextView tvLocation;
        TextView tvSpeaciality;
        TextView tvTime;
        LinearLayout liView1;

    }

}
