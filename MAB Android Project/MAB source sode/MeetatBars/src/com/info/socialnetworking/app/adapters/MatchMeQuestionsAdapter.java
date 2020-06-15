package com.info.socialnetworking.app.adapters;

import java.util.List;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.info.socialnetworking.app.helper.MatchMeQuestions;
import com.info.socialnetworking.app.interfaces.OnLoadMoreListener;
import com.info.socialnetworking.app.meetatbars.R;

@SuppressWarnings("rawtypes")
public class MatchMeQuestionsAdapter extends RecyclerView.Adapter {

	private List<MatchMeQuestions> matchMeQuestionsList;
	static Activity _context;
	private int lastVisibleItem, totalItemCount;
	private OnLoadMoreListener onLoadMoreListener;
	private int visibleThreshold = 5;
	private boolean loading;

	public MatchMeQuestionsAdapter(List<MatchMeQuestions> matchMeQuestions,
			RecyclerView recyclerView, Activity context) {
		// TODO Auto-generated constructor stub
		this.matchMeQuestionsList = matchMeQuestions;
		MatchMeQuestionsAdapter._context = context;

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
		return matchMeQuestionsList.size();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		// TODO Auto-generated method stub

		MatchMeQuestions singleQuestionDetails = (MatchMeQuestions) matchMeQuestionsList
				.get(position);

		((MatchMeQuestionsViewHolder) holder).question
				.setText(singleQuestionDetails.getQuestion());
		clearAllViews((MatchMeQuestionsViewHolder) holder);
		hideAllViews((MatchMeQuestionsViewHolder) holder);
		switch (Integer.parseInt(singleQuestionDetails.getAnswerSelected())) {
		case -1:
			clearAllViews((MatchMeQuestionsViewHolder) holder);
			break;
		case 1:
			((MatchMeQuestionsViewHolder) holder).answer1
					.setBackgroundDrawable(_context.getResources().getDrawable(
							R.drawable.questions_button_selected));
			((MatchMeQuestionsViewHolder) holder).answer1.setTextColor(_context
					.getResources().getColor(R.color.white));
			break;
		case 2:
			((MatchMeQuestionsViewHolder) holder).answer2
					.setBackgroundDrawable(_context.getResources().getDrawable(
							R.drawable.questions_button_selected));
			((MatchMeQuestionsViewHolder) holder).answer2.setTextColor(_context
					.getResources().getColor(R.color.white));

			break;
		case 3:
			((MatchMeQuestionsViewHolder) holder).answer3
					.setBackgroundDrawable(_context.getResources().getDrawable(
							R.drawable.questions_button_selected));
			((MatchMeQuestionsViewHolder) holder).answer3.setTextColor(_context
					.getResources().getColor(R.color.white));
			break;
		case 4:
			((MatchMeQuestionsViewHolder) holder).answer4
					.setBackgroundDrawable(_context.getResources().getDrawable(
							R.drawable.questions_button_selected));
			((MatchMeQuestionsViewHolder) holder).answer4.setTextColor(_context
					.getResources().getColor(R.color.white));
			break;
		case 5:
			((MatchMeQuestionsViewHolder) holder).answer5
					.setBackgroundDrawable(_context.getResources().getDrawable(
							R.drawable.questions_button_selected));
			((MatchMeQuestionsViewHolder) holder).answer5.setTextColor(_context
					.getResources().getColor(R.color.white));
			break;
		case 6:
			((MatchMeQuestionsViewHolder) holder).answer6
					.setBackgroundDrawable(_context.getResources().getDrawable(
							R.drawable.questions_button_selected));
			((MatchMeQuestionsViewHolder) holder).answer6.setTextColor(_context
					.getResources().getColor(R.color.white));
			break;
		case 7:
			((MatchMeQuestionsViewHolder) holder).answer7
					.setBackgroundDrawable(_context.getResources().getDrawable(
							R.drawable.questions_button_selected));
			((MatchMeQuestionsViewHolder) holder).answer7.setTextColor(_context
					.getResources().getColor(R.color.white));
			break;
		case 8:
			((MatchMeQuestionsViewHolder) holder).answer8
					.setBackgroundDrawable(_context.getResources().getDrawable(
							R.drawable.questions_button_selected));
			((MatchMeQuestionsViewHolder) holder).answer8.setTextColor(_context
					.getResources().getColor(R.color.white));
			break;

		default:
			break;
		}

		if (singleQuestionDetails.getAnswersSize() >= 1) {

			((MatchMeQuestionsViewHolder) holder).answer1
					.setVisibility(View.VISIBLE);
			((MatchMeQuestionsViewHolder) holder).answer1
					.setText(singleQuestionDetails.getAnswer1());
			
		}
		if (singleQuestionDetails.getAnswersSize() >= 2) {
			((MatchMeQuestionsViewHolder) holder).answer2
					.setVisibility(View.VISIBLE);

			((MatchMeQuestionsViewHolder) holder).answer2
					.setText(singleQuestionDetails.getAnswer2());
			

		}
		if (singleQuestionDetails.getAnswersSize() >= 3) {
			((MatchMeQuestionsViewHolder) holder).answer3
					.setVisibility(View.VISIBLE);

			((MatchMeQuestionsViewHolder) holder).answer3
					.setText(singleQuestionDetails.getAnswer3());
			
		}
		if (singleQuestionDetails.getAnswersSize() >= 4) {
			((MatchMeQuestionsViewHolder) holder).answer4
					.setVisibility(View.VISIBLE);

			((MatchMeQuestionsViewHolder) holder).answer4
					.setText(singleQuestionDetails.getAnswer4());
			
		}
		if (singleQuestionDetails.getAnswersSize() >= 5) {
			((MatchMeQuestionsViewHolder) holder).answer5
					.setVisibility(View.VISIBLE);

			((MatchMeQuestionsViewHolder) holder).answer5
					.setText(singleQuestionDetails.getAnswer5());
			
		}
		if (singleQuestionDetails.getAnswersSize() >= 6) {
			((MatchMeQuestionsViewHolder) holder).answer6
					.setVisibility(View.VISIBLE);

			((MatchMeQuestionsViewHolder) holder).answer6
					.setText(singleQuestionDetails.getAnswer6());
			
		}
		if (singleQuestionDetails.getAnswersSize() >= 7) {
			((MatchMeQuestionsViewHolder) holder).answer7
					.setVisibility(View.VISIBLE);

			((MatchMeQuestionsViewHolder) holder).answer7
					.setText(singleQuestionDetails.getAnswer7());
			
		}
		if (singleQuestionDetails.getAnswersSize() >= 8) {
			((MatchMeQuestionsViewHolder) holder).answer8
					.setVisibility(View.VISIBLE);

			((MatchMeQuestionsViewHolder) holder).answer8
					.setText(singleQuestionDetails.getAnswer8());
			
		}
		((MatchMeQuestionsViewHolder) holder).matchMeQuestionDetails = singleQuestionDetails;

	}

	@SuppressWarnings("deprecation")
	private void clearAllViews(MatchMeQuestionsViewHolder holder) {
		// TODO Auto-generated method stub
		holder.answer1.setBackgroundDrawable(_context.getResources()
				.getDrawable(R.drawable.blue_stoke));
		holder.answer1.setTextColor(_context.getResources().getColor(
				R.color.black));

		holder.answer2.setBackgroundDrawable(_context.getResources()
				.getDrawable(R.drawable.blue_stoke));
		holder.answer2.setTextColor(_context.getResources().getColor(
				R.color.black));
		holder.answer3.setBackgroundDrawable(_context.getResources()
				.getDrawable(R.drawable.blue_stoke));
		holder.answer3.setTextColor(_context.getResources().getColor(
				R.color.black));
		holder.answer4.setBackgroundDrawable(_context.getResources()
				.getDrawable(R.drawable.blue_stoke));
		holder.answer4.setTextColor(_context.getResources().getColor(
				R.color.black));
		holder.answer5.setBackgroundDrawable(_context.getResources()
				.getDrawable(R.drawable.blue_stoke));
		holder.answer5.setTextColor(_context.getResources().getColor(
				R.color.black));
		holder.answer6.setBackgroundDrawable(_context.getResources()
				.getDrawable(R.drawable.blue_stoke));
		holder.answer6.setTextColor(_context.getResources().getColor(
				R.color.black));
		holder.answer7.setBackgroundDrawable(_context.getResources()
				.getDrawable(R.drawable.blue_stoke));
		holder.answer7.setTextColor(_context.getResources().getColor(
				R.color.black));
		holder.answer8.setBackgroundDrawable(_context.getResources()
				.getDrawable(R.drawable.blue_stoke));
		holder.answer8.setTextColor(_context.getResources().getColor(
				R.color.black));
	}

	private void hideAllViews(MatchMeQuestionsViewHolder holder) {
		holder.answer1.setVisibility(View.GONE);
		holder.answer2.setVisibility(View.GONE);
		holder.answer3.setVisibility(View.GONE);
		holder.answer4.setVisibility(View.GONE);
		holder.answer5.setVisibility(View.GONE);
		holder.answer6.setVisibility(View.GONE);
		holder.answer7.setVisibility(View.GONE);
		holder.answer8.setVisibility(View.GONE);
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
			int viewType) {
		// TODO Auto-generated method stub
		RecyclerView.ViewHolder vh;
		View v = LayoutInflater.from(parent.getContext()).inflate(
				R.layout.matchme_questions_item, parent, false);

		vh = new MatchMeQuestionsViewHolder(v);

		return vh;

	}

	@SuppressWarnings("deprecation")
	public static class MatchMeQuestionsViewHolder extends
			RecyclerView.ViewHolder {

		public TextView question;
		public Button answer1;
		public Button answer2;
		public Button answer3;
		public Button answer4;
		public Button answer5;
		public Button answer6;
		public Button answer7;
		public Button answer8;

		public MatchMeQuestions matchMeQuestionDetails;

		public MatchMeQuestionsViewHolder(View v) {
			super(v);
			// TODO Auto-generated constructor stub

			question = (TextView) v.findViewById(R.id.tvQuestion);
			answer1 = (Button) v.findViewById(R.id.btnAnswer1);
			answer2 = (Button) v.findViewById(R.id.btnAnswer2);
			answer3 = (Button) v.findViewById(R.id.btnAnswer3);
			answer4 = (Button) v.findViewById(R.id.btnAnswer4);
			answer5 = (Button) v.findViewById(R.id.btnAnswer5);
			answer6 = (Button) v.findViewById(R.id.btnAnswer6);
			answer7 = (Button) v.findViewById(R.id.btnAnswer7);
			answer8 = (Button) v.findViewById(R.id.btnAnswer8);

			answer1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					clearAllViews();
					matchMeQuestionDetails.putAnswerSelected(v.getTag()
							.toString());

					answer1.setBackgroundDrawable(_context.getResources()
							.getDrawable(R.drawable.questions_button_selected));
					answer1.setTextColor(_context.getResources().getColor(
							R.color.white));
				}
			});
			answer2.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					clearAllViews();
					matchMeQuestionDetails.putAnswerSelected(v.getTag()
							.toString());
					answer2.setBackgroundDrawable(_context.getResources()
							.getDrawable(R.drawable.questions_button_selected));
					answer2.setTextColor(_context.getResources().getColor(
							R.color.white));
				}
			});
			answer3.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					clearAllViews();
					matchMeQuestionDetails.putAnswerSelected(v.getTag()
							.toString());

					answer3.setBackgroundDrawable(_context.getResources()
							.getDrawable(R.drawable.questions_button_selected));
					answer3.setTextColor(_context.getResources().getColor(
							R.color.white));
				}
			});
			answer4.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					clearAllViews();
					matchMeQuestionDetails.putAnswerSelected(v.getTag()
							.toString());

					answer4.setBackgroundDrawable(_context.getResources()
							.getDrawable(R.drawable.questions_button_selected));
					answer4.setTextColor(_context.getResources().getColor(
							R.color.white));
				}
			});
			answer5.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					clearAllViews();
					matchMeQuestionDetails.putAnswerSelected(v.getTag()
							.toString());

					answer5.setBackgroundDrawable(_context.getResources()
							.getDrawable(R.drawable.questions_button_selected));
					answer5.setTextColor(_context.getResources().getColor(
							R.color.white));
				}
			});
			answer6.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					clearAllViews();
					matchMeQuestionDetails.putAnswerSelected(v.getTag()
							.toString());

					answer6.setBackgroundDrawable(_context.getResources()
							.getDrawable(R.drawable.questions_button_selected));
					answer6.setTextColor(_context.getResources().getColor(
							R.color.white));
				}
			});
			answer7.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					clearAllViews();
					matchMeQuestionDetails.putAnswerSelected(v.getTag()
							.toString());

					answer7.setBackgroundDrawable(_context.getResources()
							.getDrawable(R.drawable.questions_button_selected));
					answer7.setTextColor(_context.getResources().getColor(
							R.color.white));
				}
			});

			answer8.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					clearAllViews();
					matchMeQuestionDetails.putAnswerSelected(v.getTag()
							.toString());

					answer8.setBackgroundDrawable(_context.getResources()
							.getDrawable(R.drawable.questions_button_selected));
					answer8.setTextColor(_context.getResources().getColor(
							R.color.white));
				}
			});

		}

		protected void clearAllViews() {
			// TODO Auto-generated method stub

			answer1.setBackgroundDrawable(_context.getResources().getDrawable(
					R.drawable.blue_stoke));
			answer1.setTextColor(_context.getResources()
					.getColor(R.color.black));
			answer2.setBackgroundDrawable(_context.getResources().getDrawable(
					R.drawable.blue_stoke));
			answer2.setTextColor(_context.getResources()
					.getColor(R.color.black));
			answer3.setBackgroundDrawable(_context.getResources().getDrawable(
					R.drawable.blue_stoke));
			answer3.setTextColor(_context.getResources()
					.getColor(R.color.black));
			answer4.setBackgroundDrawable(_context.getResources().getDrawable(
					R.drawable.blue_stoke));
			answer4.setTextColor(_context.getResources()
					.getColor(R.color.black));
			answer5.setBackgroundDrawable(_context.getResources().getDrawable(
					R.drawable.blue_stoke));
			answer5.setTextColor(_context.getResources()
					.getColor(R.color.black));

			answer6.setBackgroundDrawable(_context.getResources().getDrawable(
					R.drawable.blue_stoke));
			answer6.setTextColor(_context.getResources()
					.getColor(R.color.black));

			answer7.setBackgroundDrawable(_context.getResources().getDrawable(
					R.drawable.blue_stoke));
			answer7.setTextColor(_context.getResources()
					.getColor(R.color.black));
			answer8.setBackgroundDrawable(_context.getResources().getDrawable(
					R.drawable.blue_stoke));
			answer8.setTextColor(_context.getResources()
					.getColor(R.color.black));
		}

	}
}
