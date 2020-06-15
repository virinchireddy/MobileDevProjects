package com.info.socialnetworking.app.helper;


public class MeetOthersFilterDetails {

	private String[] questionsData;
	public boolean[] answersSelected;
	private boolean arrowSide = true;
	private static int Question = 0;
	private static int Answer1Text = 1;
	private static int Answer2Text = 2;
	private static int Answer3Text = 3;
	private static int Answer4Text = 4;
	private static int Answer5Text = 5;
	private static int Answer6Text = 6;
	private static int Answer7Text = 7;
	private static int Answer8Text = 8;

	public MeetOthersFilterDetails() {
		// TODO Auto-generated constructor stub
	}

	public MeetOthersFilterDetails(String[] data) {
		// TODO Auto-generated constructor stub

		questionsData = new String[data.length];
		this.questionsData = data;
		answersSelected = new boolean[data.length - 1];
		for (int i = 0; i < answersSelected.length; i++) {
			answersSelected[i] = false;
		}
	}

	public String getQuestion() {
		return questionsData[Question];
	}

	public String getAnswer1() {
		return questionsData[Answer1Text];
	}

	public String getAnswer2() {
		return questionsData[Answer2Text];
	}

	public String getAnswer3() {
		return questionsData[Answer3Text];
	}

	public String getAnswer4() {
		return questionsData[Answer4Text];
	}

	public String getAnswer5() {
		return questionsData[Answer5Text];
	}

	public String getAnswer6() {
		return questionsData[Answer6Text];
	}

	public String getAnswer7() {
		return questionsData[Answer7Text];
	}

	public String getAnswer8() {
		return questionsData[Answer8Text];
	}

	public void setAnswers(int position, boolean value) {
		answersSelected[position] = value;
		//Log.v("in details set", position+""+answersSelected[position]);

	}

	public boolean getAnswersSelected(int position) {
		//Log.v("in details get", position+""+answersSelected[position]);
		return answersSelected[position];
	}

	public int answersSize() {
		return questionsData.length - 1;
	}

	public void setArrowStatus(boolean arrowSide) {
		this.arrowSide = arrowSide;
	}

	public boolean getArrowStatus() {
		return arrowSide;
	}

}
