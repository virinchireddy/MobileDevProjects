package com.info.socialnetworking.app.adapters;

import java.util.List;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.info.socialnetworking.app.helper.PrivateQuestionsDetails;
import com.info.socialnetworking.app.interfaces.OnLoadMoreListener;
import com.info.socialnetworking.app.meetatbars.R;

@SuppressWarnings("rawtypes")
public class PrivateQuestionsAdapter extends RecyclerView.Adapter {

	// public
	private List<PrivateQuestionsDetails> privateQuestionsList;
	static Activity _context;
	private int lastVisibleItem, totalItemCount;
	private OnLoadMoreListener onLoadMoreListener;
	private int visibleThreshold = 5;
	private boolean loading;

	public PrivateQuestionsAdapter( 
			List<PrivateQuestionsDetails> privateQuestions,
			RecyclerView recyclerView, Activity context) {
		// TODO Auto-generated constructor stub
		this.privateQuestionsList = privateQuestions;
		PrivateQuestionsAdapter._context = context;
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
							if (!loading&& totalItemCount <= (lastVisibleItem + visibleThreshold)) {
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

	public PrivateQuestionsAdapter(String[] questiosn) {
		
		
	}
	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return privateQuestionsList.size();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		// TODO Auto-generated method stub
		PrivateQuestionsDetails singleQuestionDetails = (PrivateQuestionsDetails) privateQuestionsList
				.get(position);

		((PrivateQuestionsViewHolder) holder).question
				.setText(singleQuestionDetails.getQuestion());
		clearAllViews((PrivateQuestionsViewHolder) holder);
		//Log.v("answer1", singleQuestionDetails.getAnswer1());

		switch (Integer.parseInt(singleQuestionDetails.getAnswer1Value())) {
		
		case -1:
			
			break;
		case 1:
			((PrivateQuestionsViewHolder) holder).answer1
					.setBackgroundDrawable(_context.getResources().getDrawable(
							R.drawable.questions_button_selected));
			((PrivateQuestionsViewHolder) holder).answer1.setTextColor(_context
					.getResources().getColor(R.color.white));
			break;
		case 2:
			((PrivateQuestionsViewHolder) holder).answer2
					.setBackgroundDrawable(_context.getResources().getDrawable(
							R.drawable.questions_button_selected));
			((PrivateQuestionsViewHolder) holder).answer2.setTextColor(_context
					.getResources().getColor(R.color.white));

			break;
		case 3:
			((PrivateQuestionsViewHolder) holder).answer3
					.setBackgroundDrawable(_context.getResources().getDrawable(
							R.drawable.questions_button_selected));
			((PrivateQuestionsViewHolder) holder).answer3.setTextColor(_context
					.getResources().getColor(R.color.white));
			break;
		case 4:
			((PrivateQuestionsViewHolder) holder).answer4
					.setBackgroundDrawable(_context.getResources().getDrawable(
							R.drawable.questions_button_selected));
			((PrivateQuestionsViewHolder) holder).answer4.setTextColor(_context
					.getResources().getColor(R.color.white));
			break;

		default:
			break;
		}
		((PrivateQuestionsViewHolder) holder).answer1
				.setText(singleQuestionDetails.getAnswer1());
		

		((PrivateQuestionsViewHolder) holder).answer2
				.setText(singleQuestionDetails.getAnswer2());
		((PrivateQuestionsViewHolder) holder).answer3
				.setText(singleQuestionDetails.getAnswer3());
		((PrivateQuestionsViewHolder) holder).answer4
				.setText(singleQuestionDetails.getAnswer4());

		((PrivateQuestionsViewHolder) holder).privateQuestionDetails = singleQuestionDetails;

	}

	@SuppressWarnings("deprecation")
	private void clearAllViews(PrivateQuestionsViewHolder holder) {
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
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
			int viewType) {
		// TODO Auto-generated method stub
		RecyclerView.ViewHolder vh;
		View v = LayoutInflater.from(parent.getContext()).inflate(
				R.layout.questions_item, parent, false);

		vh = new PrivateQuestionsViewHolder(v);

		return vh;
	}

	@SuppressWarnings("deprecation")
	public static class PrivateQuestionsViewHolder extends
			RecyclerView.ViewHolder {
		public TextView question;
		public Button answer1;
		public Button answer2;
		public Button answer3;
		public Button answer4;

		public PrivateQuestionsDetails privateQuestionDetails;

		public PrivateQuestionsViewHolder(View v) {
			super(v);
			question = (TextView) v.findViewById(R.id.tvQuestion);
			answer1 = (Button) v.findViewById(R.id.btnAnswer1);
			answer2 = (Button) v.findViewById(R.id.btnAnswer2);
			answer3 = (Button) v.findViewById(R.id.btnAnswer3);
			answer4 = (Button) v.findViewById(R.id.btnAnswer4);

			answer1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					clearAllViews();
					privateQuestionDetails.putAnswer1Value("1");
					answer1.setBackgroundDrawable(_context.getResources().getDrawable(R.drawable.questions_button_selected));
					answer1.setTextColor(_context.getResources().getColor(R.color.white));
				}
			});
			answer2.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					clearAllViews(); 
					privateQuestionDetails.putAnswer1Value("2");
					answer2.setBackgroundDrawable(_context.getResources().getDrawable(R.drawable.questions_button_selected));
					answer2.setTextColor(_context.getResources().getColor(R.color.white));
				}
			});
			answer3.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					clearAllViews();
					privateQuestionDetails.putAnswer1Value("3");
					answer3.setBackgroundDrawable(_context.getResources().getDrawable(R.drawable.questions_button_selected));
					answer3.setTextColor(_context.getResources().getColor(R.color.white));
				}
			});
			answer4.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					clearAllViews();
					privateQuestionDetails.putAnswer1Value("4");
					answer4.setBackgroundDrawable(_context.getResources().getDrawable(R.drawable.questions_button_selected));
					answer4.setTextColor(_context.getResources().getColor(R.color.white));
				}
			});
			

		}

		private void clearAllViews() {
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
		}
	}

}
