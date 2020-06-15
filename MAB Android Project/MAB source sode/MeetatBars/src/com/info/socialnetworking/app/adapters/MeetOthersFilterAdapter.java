package com.info.socialnetworking.app.adapters;

import java.util.List;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnScrollListener;
import android.widget.TextView;

import com.info.socialnetworking.app.helper.AgeFilterDetails;
import com.info.socialnetworking.app.helper.HeightFilterHelper;
import com.info.socialnetworking.app.helper.LocationFilterDetails;
import com.info.socialnetworking.app.helper.MeetOthersFilterDetails;
import com.info.socialnetworking.app.interfaces.OnLoadMoreListener;
import com.info.socialnetworking.app.meetatbars.R;

@SuppressWarnings("rawtypes")
public class MeetOthersFilterAdapter extends RecyclerView.Adapter {

	private List<MeetOthersFilterDetails> meetOthersQuestionsList;
	private List<LocationFilterDetails> locationFilterDetails;
	private List<HeightFilterHelper> heightFilterDetails;
	private List<AgeFilterDetails> ageFilterDetails;


	private int visibleThreshold = 5;
	private int lastVisibleItem, totalItemCount;
	private boolean loading;
	private OnLoadMoreListener onLoadMoreListener;
	static Activity _context;

	public MeetOthersFilterAdapter(List<MeetOthersFilterDetails> meetOthers,
			List<LocationFilterDetails> location,
			List<HeightFilterHelper> height,List<AgeFilterDetails> age, RecyclerView recyclerView,
			Activity context) {
		this.meetOthersQuestionsList = meetOthers;
		this.locationFilterDetails = location;
		this.heightFilterDetails = height;
		this.ageFilterDetails=age;
		_context = context;
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
		return meetOthersQuestionsList.size();
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		// TODO Auto-generated method stub
		if (holder instanceof FilterItemsViewHolder) {
			MeetOthersFilterDetails singleQuestionDetails = (MeetOthersFilterDetails) meetOthersQuestionsList
					.get(position);

			FilterItemsViewHolder viewHolder = (FilterItemsViewHolder) holder;
			// displaying the filter items dynamically

			viewHolder.question.setText(singleQuestionDetails.getQuestion());
			clearAll(viewHolder, singleQuestionDetails);
			viewHolder.answer1.setText(singleQuestionDetails.getAnswer1());
			viewHolder.answer1.setTag(singleQuestionDetails);
			viewHolder.answer1.setChecked(singleQuestionDetails
					.getAnswersSelected(0));

			viewHolder.answer2.setText(singleQuestionDetails.getAnswer2());
			viewHolder.answer2.setTag(singleQuestionDetails);
			viewHolder.answer2.setChecked(singleQuestionDetails
					.getAnswersSelected(1));

			// conditions for displaying more then 3 answers
			if (singleQuestionDetails.answersSize() >= 3
					&& !singleQuestionDetails.getAnswer3().equals("")) {
				viewHolder.answer3.setText(singleQuestionDetails.getAnswer3());
				viewHolder.answer3.setVisibility(View.VISIBLE);
				viewHolder.answer3.setTag(singleQuestionDetails);
				viewHolder.answer3.setChecked(singleQuestionDetails
						.getAnswersSelected(2));

			}
			if (singleQuestionDetails.answersSize() >= 4
					&& !singleQuestionDetails.getAnswer4().equals("")) {
				viewHolder.answer4.setText(singleQuestionDetails.getAnswer4());
				viewHolder.answer4.setVisibility(View.VISIBLE);
				viewHolder.answer4.setTag(singleQuestionDetails);
				viewHolder.answer4.setChecked(singleQuestionDetails
						.getAnswersSelected(3));
			}
			if (singleQuestionDetails.answersSize() >= 5
					&& !singleQuestionDetails.getAnswer5().equals("")) {
				viewHolder.answer5.setText(singleQuestionDetails.getAnswer5());
				viewHolder.answer5.setVisibility(View.VISIBLE);
				viewHolder.answer5.setTag(singleQuestionDetails);
				viewHolder.answer5.setChecked(singleQuestionDetails
						.getAnswersSelected(4));
			}
			if (singleQuestionDetails.answersSize() >= 6
					&& !singleQuestionDetails.getAnswer6().equals("")) {
				viewHolder.answer6.setText(singleQuestionDetails.getAnswer6());
				viewHolder.answer6.setVisibility(View.VISIBLE);
				viewHolder.answer6.setTag(singleQuestionDetails);
				viewHolder.answer6.setChecked(singleQuestionDetails
						.getAnswersSelected(5));
			}
			if (singleQuestionDetails.answersSize() >= 7
					&& !singleQuestionDetails.getAnswer7().equals("")) {
				viewHolder.answer7.setText(singleQuestionDetails.getAnswer7());
				viewHolder.answer7.setVisibility(View.VISIBLE);
				viewHolder.answer7.setTag(singleQuestionDetails);
				viewHolder.answer7.setChecked(singleQuestionDetails
						.getAnswersSelected(6));
			}
			if (singleQuestionDetails.answersSize() >= 8
					&& !singleQuestionDetails.getAnswer8().equals("")) {
				viewHolder.answer8.setText(singleQuestionDetails.getAnswer8());
				viewHolder.answer8.setVisibility(View.VISIBLE);
				viewHolder.answer8.setTag(singleQuestionDetails);
				viewHolder.answer8.setChecked(singleQuestionDetails
						.getAnswersSelected(7));
			}
			viewHolder.meetOthersFilterDetails = singleQuestionDetails;

			// handling the answers visibality after scrolling down and up
			if (singleQuestionDetails.getArrowStatus()) {
				viewHolder.liAnswers.setVisibility(View.GONE);
				viewHolder.arrowCount = 0;
				viewHolder.ivArrow.setImageResource(R.drawable.arrow_side);

			} else {
				viewHolder.liAnswers.setVisibility(View.VISIBLE);
				viewHolder.arrowCount = 1;
				viewHolder.ivArrow.setImageResource(R.drawable.up_arrow);

			}

			ListenerToItems(viewHolder, position);
		} else if (holder instanceof LocationFilterViewHolder) {
			LocationFilterDetails singleQuestionDetails = (LocationFilterDetails) locationFilterDetails
					.get(position);
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
		} else if(holder instanceof HeightViewHolder){
			HeightFilterHelper singleQuestionDetails = (HeightFilterHelper) heightFilterDetails
					.get(position - 1);
			HeightViewHolder viewHolder = (HeightViewHolder) holder;
			viewHolder.question.setText(singleQuestionDetails.getQuestion());
			viewHolder.heightFilterDetails=singleQuestionDetails;
			viewHolder.npHightFt.setValue(singleQuestionDetails.getHeightFt());
			viewHolder.npHightIn.setValue(singleQuestionDetails.getHeightIn());
			
			viewHolder.npToHightFt.setMaxValue(8);
			viewHolder.npToHightFt.setMinValue(singleQuestionDetails.getHeightFt());
			viewHolder.npToHightFt.setWrapSelectorWheel(false);
			viewHolder.npToHightIn.setMaxValue(11);
			viewHolder.npToHightIn.setMinValue(0);
			
			viewHolder.npToHightFt.setValue(singleQuestionDetails.toGetHeightFt());
			viewHolder.npToHightIn.setValue(singleQuestionDetails.toGetHeightIn());
			
			
			
			
			if (singleQuestionDetails.getArrowStatus()) {
				viewHolder.liAnswers.setVisibility(View.GONE);
				viewHolder.arrowCount = 0;
				viewHolder.ivArrow.setImageResource(R.drawable.arrow_side);

			} else {
				viewHolder.liAnswers.setVisibility(View.VISIBLE);
				viewHolder.arrowCount = 1;
				viewHolder.ivArrow.setImageResource(R.drawable.up_arrow);

			}
		}else if(holder instanceof AgeViewHolder){
			AgeFilterDetails singleQuestion=(AgeFilterDetails)ageFilterDetails.get(0);
			AgeViewHolder viewHolder=(AgeViewHolder)holder;
			viewHolder.question.setText(singleQuestion.getQuestion());
			viewHolder.ageFilterDetails=singleQuestion;
			viewHolder.npFromAge.setValue(singleQuestion.getFromAge());
			
			viewHolder.npToAge.setMaxValue(70);
			viewHolder.npToAge.setMinValue(singleQuestion.getFromAge());
			
			viewHolder.npToAge.setValue(singleQuestion.getToAge());
			if (singleQuestion.getArrowStatus()) {
				viewHolder.liAnswers.setVisibility(View.GONE);
				viewHolder.arrowCount = 0;
				viewHolder.ivArrow.setImageResource(R.drawable.arrow_side);

			} else {
				viewHolder.liAnswers.setVisibility(View.VISIBLE);
				viewHolder.arrowCount = 1;
				viewHolder.ivArrow.setImageResource(R.drawable.up_arrow);

			}
		}
	}

	// adding listenr to selected items
	private void ListenerToItems(FilterItemsViewHolder viewHolder, final int pos) {
		// TODO Auto-generated method stub
		viewHolder.answer1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				CheckBox cb = (CheckBox) v;
				MeetOthersFilterDetails question = (MeetOthersFilterDetails) cb
						.getTag();
				question.setAnswers(0, cb.isChecked());
				meetOthersQuestionsList.get(pos).setAnswers(0, cb.isChecked());

			}
		});
		viewHolder.answer2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				CheckBox cb = (CheckBox) v;
				MeetOthersFilterDetails question = (MeetOthersFilterDetails) cb
						.getTag();

				question.setAnswers(1, cb.isChecked());
				meetOthersQuestionsList.get(pos).setAnswers(1, cb.isChecked());

			}
		});
		viewHolder.answer3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				CheckBox cb = (CheckBox) v;
				MeetOthersFilterDetails question = (MeetOthersFilterDetails) cb
						.getTag();

				question.setAnswers(2, cb.isChecked());
				meetOthersQuestionsList.get(pos).setAnswers(2, cb.isChecked());

			}
		});
		viewHolder.answer4.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				CheckBox cb = (CheckBox) v;
				MeetOthersFilterDetails question = (MeetOthersFilterDetails) cb
						.getTag();

				question.setAnswers(3, cb.isChecked());
				meetOthersQuestionsList.get(pos).setAnswers(3, cb.isChecked());

			}
		});
		viewHolder.answer5.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				CheckBox cb = (CheckBox) v;
				MeetOthersFilterDetails question = (MeetOthersFilterDetails) cb
						.getTag();

				question.setAnswers(4, cb.isChecked());
				meetOthersQuestionsList.get(pos).setAnswers(4, cb.isChecked());

			}
		});
		viewHolder.answer6.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				CheckBox cb = (CheckBox) v;
				MeetOthersFilterDetails question = (MeetOthersFilterDetails) cb
						.getTag();

				question.setAnswers(5, cb.isChecked());
				meetOthersQuestionsList.get(pos).setAnswers(5, cb.isChecked());

			}
		});
		viewHolder.answer7.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				CheckBox cb = (CheckBox) v;
				MeetOthersFilterDetails question = (MeetOthersFilterDetails) cb
						.getTag();

				question.setAnswers(6, cb.isChecked());
				meetOthersQuestionsList.get(pos).setAnswers(6, cb.isChecked());

			}
		});
		viewHolder.answer8.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				CheckBox cb = (CheckBox) v;
				MeetOthersFilterDetails question = (MeetOthersFilterDetails) cb
						.getTag();

				question.setAnswers(7, cb.isChecked());
				meetOthersQuestionsList.get(pos).setAnswers(7, cb.isChecked());

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
		holder.answer5.setChecked(false);
		holder.answer6.setChecked(false);
		holder.answer7.setChecked(false);
		holder.answer8.setChecked(false);
		holder.answer3.setVisibility(View.GONE);
		holder.answer4.setVisibility(View.GONE);
		holder.answer5.setVisibility(View.GONE);
		holder.answer6.setVisibility(View.GONE);
		holder.answer7.setVisibility(View.GONE);
		holder.answer8.setVisibility(View.GONE);

		holder.liAnswers.setVisibility(View.GONE);
		holder.ivArrow.setImageResource(R.drawable.arrow_side);

	}

	@Override
	public int getItemViewType(int position) {

		return position;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
			int viewType) {
		// TODO Auto-generated method stub
		RecyclerView.ViewHolder vh;
		if (viewType == 0) {
			View v = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.location_filter, parent, false);

			vh = new LocationFilterViewHolder(v);

		} else if (viewType == 1) {
			View v = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.height_element_filter, parent, false);

			vh = new HeightViewHolder(v);
		}
		else if (viewType == 2) {
			View v = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.age_filter, parent, false);

			vh = new AgeViewHolder(v);
		}

		else {

			View v = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.meet_other_filter_item, parent, false);

			vh = new FilterItemsViewHolder(v);

		}

		return vh;
	}

	public static class FilterItemsViewHolder extends RecyclerView.ViewHolder {
		int arrowCount = 0;
		public TextView question;
		public CheckBox answer1;
		public CheckBox answer2;
		public CheckBox answer3;
		public CheckBox answer4;
		public CheckBox answer5;
		public CheckBox answer6;
		public CheckBox answer7;
		public CheckBox answer8;
		public LinearLayout liAnswers, liQuestion;
		public ImageView ivArrow;

		public MeetOthersFilterDetails meetOthersFilterDetails;

		public FilterItemsViewHolder(View v) {
			super(v);

			question = (TextView) v.findViewById(R.id.tvQuestion);
			answer1 = (CheckBox) v.findViewById(R.id.cbAnswer1);
			answer2 = (CheckBox) v.findViewById(R.id.cbAnswer2);
			answer3 = (CheckBox) v.findViewById(R.id.cbAnswer3);
			answer4 = (CheckBox) v.findViewById(R.id.cbAnswer4);
			answer5 = (CheckBox) v.findViewById(R.id.cbAnswer5);
			answer6 = (CheckBox) v.findViewById(R.id.cbAnswer6);
			answer7 = (CheckBox) v.findViewById(R.id.cbAnswer7);
			answer8 = (CheckBox) v.findViewById(R.id.cbAnswer8);
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
						meetOthersFilterDetails.setArrowStatus(true);
						ivArrow.setImageResource(R.drawable.arrow_side);
						liAnswers.setVisibility(View.GONE);
					} else if (arrowCount == 1) {
						meetOthersFilterDetails.setArrowStatus(false);
						ivArrow.setImageResource(R.drawable.up_arrow);
						liAnswers.setVisibility(View.VISIBLE);
					}
				}
			});
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

	public static class HeightViewHolder extends RecyclerView.ViewHolder {

		int arrowCount = 0;
		public TextView question;
		public LinearLayout liAnswers, liQuestion;
		public ImageView ivArrow;

		NumberPicker npHightFt, npHightIn,npToHightFt,npToHightIn;
		public HeightFilterHelper heightFilterDetails;

		public HeightViewHolder(View v) {
			super(v);
			question = (TextView) v.findViewById(R.id.tvQuestion);
			liQuestion = (LinearLayout) v.findViewById(R.id.liQuestion);
			liAnswers = (LinearLayout) v.findViewById(R.id.liAnswers);
			ivArrow = (ImageView) v.findViewById(R.id.ivArrow);
			npHightFt = (NumberPicker) v.findViewById(R.id.npHightFt);
			npHightIn = (NumberPicker) v.findViewById(R.id.npHightIn);
			npToHightFt = (NumberPicker) v.findViewById(R.id.npToHightFt);
			npToHightIn = (NumberPicker) v.findViewById(R.id.npToHightIn);
			npHightFt.setMaxValue(8);
			npHightFt.setMinValue(3);
			npHightFt.setWrapSelectorWheel(false);
			npHightIn.setMaxValue(11);
			npHightIn.setMinValue(0);
			
			/*npToHightFt.setMaxValue(8);
			npToHightFt.setMinValue(3);
			npToHightFt.setWrapSelectorWheel(false);
			npToHightIn.setMaxValue(11);
			npToHightIn.setMinValue(0);*/
			

			liQuestion.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					arrowCount++;
					arrowCount = arrowCount % 2;
					if (arrowCount == 0) {
						heightFilterDetails.setArrowStatus(true);
						ivArrow.setImageResource(R.drawable.arrow_side);
						liAnswers.setVisibility(View.GONE);
					} else if (arrowCount == 1) {
						heightFilterDetails.setArrowStatus(false);
						ivArrow.setImageResource(R.drawable.up_arrow);
						liAnswers.setVisibility(View.VISIBLE);
					}
				}
			});
			npHightFt.setOnScrollListener(new OnScrollListener() {

				@Override
				public void onScrollStateChange(NumberPicker view,
						int scrollState) {
					// TODO Auto-generated method stub
					heightFilterDetails.setHeightFt(view.getValue());
					npToHightFt.setMaxValue(8);
					npToHightFt.setMinValue(view.getValue());
					npToHightFt.setWrapSelectorWheel(false);
					npToHightIn.setMaxValue(11);
					npToHightIn.setMinValue(0);
				}
			});
			npHightIn.setOnScrollListener(new OnScrollListener() {
				@Override
				public void onScrollStateChange(NumberPicker view,
						int scrollState) {
					// TODO Auto-generated method stub
					heightFilterDetails.setHeightIn(view.getValue());
					npToHightFt.setMaxValue(8);
					npToHightFt.setMinValue(heightFilterDetails.getHeightFt());
					npToHightFt.setWrapSelectorWheel(false);
					npToHightIn.setMaxValue(11);
					npToHightIn.setMinValue(0);

				}
			});
			npToHightFt.setOnScrollListener(new OnScrollListener() {

				@Override
				public void onScrollStateChange(NumberPicker view,
						int scrollState) {
					// TODO Auto-generated method stub
					heightFilterDetails.toSetHeightFt(view.getValue());

				}
			});
			npToHightIn.setOnScrollListener(new OnScrollListener() {

				@Override
				public void onScrollStateChange(NumberPicker view,
						int scrollState) {
					// TODO Auto-generated method stub
					heightFilterDetails.toSetHeightIn(view.getValue());

				}
			});
		}
	}
	
	public static class AgeViewHolder extends RecyclerView.ViewHolder {

		int arrowCount = 0;
		public TextView question;
		public LinearLayout liAnswers, liQuestion;
		public ImageView ivArrow;

		NumberPicker npFromAge, npToAge;
		public AgeFilterDetails ageFilterDetails;

		public AgeViewHolder(View v) {
			super(v);
			question = (TextView) v.findViewById(R.id.tvQuestion);
			liQuestion = (LinearLayout) v.findViewById(R.id.liQuestion);
			liAnswers = (LinearLayout) v.findViewById(R.id.liAnswers);
			ivArrow = (ImageView) v.findViewById(R.id.ivArrow);
			npFromAge = (NumberPicker) v.findViewById(R.id.npAgeFrom);
			npToAge = (NumberPicker) v.findViewById(R.id.npAgeTo);
			
			npFromAge.setMaxValue(70);
			npFromAge.setMinValue(18);
			/*npToAge.setMaxValue(70);
			npToAge.setMinValue(18);*/
			liQuestion.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					arrowCount++;
					arrowCount = arrowCount % 2;
					if (arrowCount == 0) {
						ageFilterDetails.setArrowStatus(true);
						ivArrow.setImageResource(R.drawable.arrow_side);
						liAnswers.setVisibility(View.GONE);
					} else if (arrowCount == 1) {
						ageFilterDetails.setArrowStatus(false);
						ivArrow.setImageResource(R.drawable.up_arrow);
						liAnswers.setVisibility(View.VISIBLE);
					}
				}
			});
			npFromAge.setOnScrollListener(new OnScrollListener() {

				@Override
				public void onScrollStateChange(NumberPicker view,
						int scrollState) {
					// TODO Auto-generated method stub
					ageFilterDetails.setFromAge(view.getValue());
					npToAge.setMaxValue(70);
					npToAge.setMinValue(view.getValue());
				}
			});
			npToAge.setOnScrollListener(new OnScrollListener() {
				@Override
				public void onScrollStateChange(NumberPicker view,
						int scrollState) {
					// TODO Auto-generated method stub
					ageFilterDetails.setToAge(view.getValue());
				}
			});
			
		}

	}

	
}
