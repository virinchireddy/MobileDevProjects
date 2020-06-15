package com.info.socialnetworking.app.adapters;

import java.util.List;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

import com.info.socialnetworking.app.helper.BarsFilterDetails;
import com.info.socialnetworking.app.helper.LocationFilterDetails;
import com.info.socialnetworking.app.helper.MeetOthersFilterDetails;
import com.info.socialnetworking.app.helper.Others;
import com.info.socialnetworking.app.interfaces.OnLoadMoreListener;
import com.info.socialnetworking.app.meetatbars.R;
import com.info.socialnetworking.app.network.ConnectionDetector;

@SuppressWarnings("rawtypes")
public class BarsFilterAdapter extends RecyclerView.Adapter {

	private List<BarsFilterDetails> barsFilterList;
	private List<LocationFilterDetails> barsLocationFilter;
	private List<MeetOthersFilterDetails> barsCapacityFilter;

	private int visibleThreshold = 5;
	private int lastVisibleItem, totalItemCount;
	private boolean loading;
	private OnLoadMoreListener onLoadMoreListener;
	static Activity _context;
	static Others others;
	static ConnectionDetector con;

	public BarsFilterAdapter(List<BarsFilterDetails> barsFilter,List<LocationFilterDetails> location,List<MeetOthersFilterDetails> barsCapacityFilter,
			RecyclerView recyclerView, Activity context) {
		this.barsFilterList = barsFilter;
		this.barsLocationFilter=location;
		this.barsCapacityFilter=barsCapacityFilter;
		_context = context;
		others = new Others(_context);
		con = new ConnectionDetector(_context);

		if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

			final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
					.getLayoutManager();
			recyclerView
					.setOnScrollListener(new RecyclerView.OnScrollListener() {
						@Override
						public void onScrollStateChanged(int arg0) {
							// TODO Auto-generated method stub
						}

						@Override
						public void onScrolled(int arg0, int arg1) {
							// TODO Auto-generated method stub
							totalItemCount = linearLayoutManager.getItemCount();
							lastVisibleItem = linearLayoutManager
									.findLastVisibleItemPosition();
							if (!loading
									&& totalItemCount <= (lastVisibleItem + visibleThreshold)) {
								// End has been reached
								// Do something
								if (onLoadMoreListener != null) {
									onLoadMoreListener.onLoadMore();
								}
								loading = true;
							}
						}
					});
		}
	}

	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return barsFilterList.size();
	}

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder,
			final int position) {
		// TODO Auto-generated method stub
		if (holder instanceof BarsFilterItemViewHolder) {

			final BarsFilterDetails singleFacilityDetails = (BarsFilterDetails) barsFilterList
					.get(position);
			BarsFilterItemViewHolder viewHolder = (BarsFilterItemViewHolder) holder;

			viewHolder.swFacilities
					.setText(singleFacilityDetails.getQuestion());
			viewHolder.swFacilities.setChecked(singleFacilityDetails
					.getIsSelected());
			viewHolder.swFacilities
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							// TODO Auto-generated method stub
							singleFacilityDetails.setIsSelected(isChecked);
							barsFilterList.get(position).setIsSelected(
									isChecked);

						}
					});
			viewHolder.barsFilterDetails = singleFacilityDetails;
		}else if(holder instanceof LocationFilterViewHolder){
			LocationFilterDetails singleQuestionDetails = (LocationFilterDetails) barsLocationFilter.get(position);
			LocationFilterViewHolder viewHolder = (LocationFilterViewHolder) holder;
			viewHolder.locationFilterDetails = singleQuestionDetails;
			viewHolder.etCity.setText(singleQuestionDetails.getCity());
			viewHolder.etState.setText(singleQuestionDetails.getState());
			viewHolder.etDistance.setText(singleQuestionDetails.getDistance());
			if (singleQuestionDetails.getArrowStatus()) {
				viewHolder.liLocationFilter.setVisibility(View.GONE);
				viewHolder.arrowCount = 0;
				viewHolder.ivLocation.setImageResource(R.drawable.arrow_side);

			} else {
				viewHolder.liLocationFilter.setVisibility(View.VISIBLE);
				viewHolder.arrowCount = 1;
				viewHolder.ivLocation.setImageResource(R.drawable.up_arrow);
			}
		}else{
			
			MeetOthersFilterDetails singQuestion=(MeetOthersFilterDetails)barsCapacityFilter.get(position-1);
			FilterItemsViewHolder viewHolder=(FilterItemsViewHolder)holder;
			viewHolder.question.setText(singQuestion.getQuestion());
			clearAll(viewHolder, singQuestion);
			viewHolder.answer1.setText(singQuestion.getAnswer1());
			viewHolder.answer1.setTag(singQuestion);
			viewHolder.answer1.setChecked(singQuestion
					.getAnswersSelected(0));

			viewHolder.answer2.setText(singQuestion.getAnswer2());
			viewHolder.answer2.setTag(singQuestion);
			viewHolder.answer2.setChecked(singQuestion
					.getAnswersSelected(1));

			// conditions for displaying more then 3 answers
			if (singQuestion.answersSize() >= 3
					&& !singQuestion.getAnswer3().equals("")) {
				viewHolder.answer3.setText(singQuestion.getAnswer3());
				viewHolder.answer3.setVisibility(View.VISIBLE);
				viewHolder.answer3.setTag(singQuestion);
				viewHolder.answer3.setChecked(singQuestion
						.getAnswersSelected(2));

			}
			viewHolder.BarsCapacityFilterList = singQuestion;
			
			// handling the answers visibality after scrolling down and up
			if (singQuestion.getArrowStatus()) {
					viewHolder.liAnswers.setVisibility(View.GONE);
							viewHolder.arrowCount = 0;
							viewHolder.ivArrow.setImageResource(R.drawable.arrow_side);

						} else {
							viewHolder.liAnswers.setVisibility(View.VISIBLE);
							viewHolder.arrowCount = 1;
							viewHolder.ivArrow.setImageResource(R.drawable.up_arrow); 

						}
			
			ListenerToItems(viewHolder, position-1);

		}
	}
	
	// adding listenr to selected items
		private void ListenerToItems(FilterItemsViewHolder viewHolder,final int pos) {
			// TODO Auto-generated method stub
			viewHolder.answer1.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					CheckBox cb = (CheckBox) v;
					MeetOthersFilterDetails question = (MeetOthersFilterDetails) cb
							.getTag();
					question.setAnswers(0, cb.isChecked());
					barsCapacityFilter.get(pos).setAnswers(0, cb.isChecked());

				}
			});
			viewHolder.answer2.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					CheckBox cb = (CheckBox) v;
					MeetOthersFilterDetails question = (MeetOthersFilterDetails) cb
							.getTag();

					question.setAnswers(1, cb.isChecked());
					barsCapacityFilter.get(pos).setAnswers(1, cb.isChecked());

				}
			});
			viewHolder.answer3.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					CheckBox cb = (CheckBox) v;
					MeetOthersFilterDetails question = (MeetOthersFilterDetails) cb
							.getTag();

					question.setAnswers(2, cb.isChecked());
					barsCapacityFilter.get(pos).setAnswers(2, cb.isChecked());

				}
			});
			viewHolder.answer4.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					CheckBox cb = (CheckBox) v;
					MeetOthersFilterDetails question = (MeetOthersFilterDetails) cb
							.getTag();

					question.setAnswers(3, cb.isChecked());
					barsCapacityFilter.get(pos).setAnswers(3, cb.isChecked());

				}
			});
		}
		
		// clearing all view for scrolling down and up
		private void clearAll(FilterItemsViewHolder holder,
				MeetOthersFilterDetails singleQuestionDetails) {

			// TODO Auto-generated method stub
			holder.answer1.setChecked(false);
			holder.answer2.setChecked(false);
			holder.answer3.setChecked(false);
			holder.answer4.setChecked(false);
			
			holder.answer3.setVisibility(View.GONE);
			holder.answer4.setVisibility(View.GONE);
			

			holder.liAnswers.setVisibility(View.GONE);
			holder.ivArrow.setImageResource(R.drawable.arrow_side);

		}
		
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		// TODO Auto-generated method stub
		RecyclerView.ViewHolder vh;
		if (viewType == 0) {
			View v = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.location_filter, parent, false);

			vh = new LocationFilterViewHolder(v);

		}else if (viewType == 1) {
			View v = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.meet_other_filter_item, parent, false);

			vh = new FilterItemsViewHolder(v);

		}
		else {

			View v = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.bars_filter_item, parent, false);

			vh = new BarsFilterItemViewHolder(v);

		}

		return vh;
	}

	public static class BarsFilterItemViewHolder extends
			RecyclerView.ViewHolder {

		Switch swFacilities;

		public BarsFilterDetails barsFilterDetails;

		public BarsFilterItemViewHolder(View v) {
			super(v);
			swFacilities = (Switch) v.findViewById(R.id.swFacilities);

		}

	}

	public static class LocationFilterViewHolder extends
			RecyclerView.ViewHolder {

		int arrowCount = 0;
		LinearLayout liLocation;
		ImageView ivLocation;
		LinearLayout liLocationFilter;
		EditText etCity;
		EditText etState;
		EditText etDistance;

		public LocationFilterDetails locationFilterDetails;

		public LocationFilterViewHolder(View v) {
			super(v);
			liLocation = (LinearLayout) v.findViewById(R.id.liLocation);
			ivLocation = (ImageView) v.findViewById(R.id.ivLocation);
			liLocationFilter = (LinearLayout) v
					.findViewById(R.id.liLocationFilter);
			etCity = (EditText) v.findViewById(R.id.etCity);
			etState = (EditText) v.findViewById(R.id.etState);
			etDistance = (EditText) v.findViewById(R.id.etDistance);

			liLocation.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					arrowCount++;
					arrowCount = arrowCount % 2;
					if (arrowCount == 0) {
						locationFilterDetails.setArrowStatus(true);
						ivLocation.setImageResource(R.drawable.arrow_side);
						liLocationFilter.setVisibility(View.GONE);
					} else if (arrowCount == 1) {
						locationFilterDetails.setArrowStatus(false);
						ivLocation.setImageResource(R.drawable.up_arrow);
						liLocationFilter.setVisibility(View.VISIBLE);
					}
				}
			});
			etCity.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					// TODO Auto-generated method stub

				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
					// TODO Auto-generated method stub

				}

				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub
					locationFilterDetails.setCity(etCity.getText().toString());
				}
			});
			etState.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					// TODO Auto-generated method stub

				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
					// TODO Auto-generated method stub

				}

				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub
					locationFilterDetails
							.setState(etState.getText().toString());

				}
			});
			etDistance.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					// TODO Auto-generated method stub

				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
					// TODO Auto-generated method stub

				}

				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub
					locationFilterDetails.setDistance(etDistance.getText()
							.toString());
				}
			});
		}
	}

	public static class FilterItemsViewHolder extends RecyclerView.ViewHolder {
		int arrowCount = 0;
		public TextView question;
		public CheckBox answer1;
		public CheckBox answer2;
		public CheckBox answer3;
		public CheckBox answer4;
		
		public LinearLayout liAnswers, liQuestion;
		public ImageView ivArrow;

		public MeetOthersFilterDetails BarsCapacityFilterList;

		public FilterItemsViewHolder(View v) {
			super(v);

			question = (TextView) v.findViewById(R.id.tvQuestion);
			answer1 = (CheckBox) v.findViewById(R.id.cbAnswer1);
			answer2 = (CheckBox) v.findViewById(R.id.cbAnswer2);
			answer3 = (CheckBox) v.findViewById(R.id.cbAnswer3);
			answer4 = (CheckBox) v.findViewById(R.id.cbAnswer4);
			
			liQuestion = (LinearLayout) v.findViewById(R.id.liQuestion);
			liAnswers = (LinearLayout) v.findViewById(R.id.liAnswers);
			ivArrow = (ImageView) v.findViewById(R.id.ivArrow);

			// answer1.setOnCheckedChangeListener(this);
			// answer2.setOnCheckedChangeListener(this);
			// answer3.setOnCheckedChangeListener(this);
			// answer4.setOnCheckedChangeListener(this);
			// answer5.setOnCheckedChangeListener(this);
			// answer6.setOnCheckedChangeListener(this);
			// answer7.setOnCheckedChangeListener(this);
			// answer8.setOnCheckedChangeListener(this);

			liQuestion.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					arrowCount++;
					arrowCount = arrowCount % 2;
					if (arrowCount == 0) {
						BarsCapacityFilterList.setArrowStatus(true);
						ivArrow.setImageResource(R.drawable.arrow_side);
						liAnswers.setVisibility(View.GONE);
					} else if (arrowCount == 1) {
						BarsCapacityFilterList.setArrowStatus(false);
						ivArrow.setImageResource(R.drawable.up_arrow);
						liAnswers.setVisibility(View.VISIBLE);
					}
				}
			});
		}

	}
}

