package com.info.socialnetworking.app.helper;

import java.io.Serializable;

public class MatchMeQuestions implements Serializable {

	private static final long serialVersionUID = 1L;

	private String[] Question;

	private static int QuestionText = 0;

	private static int AnswerSelected = 1;
	private static int Answer1 = 2;
	private static int Answer2 = 3;
	private static int Answer3 = 4;
	private static int Answer4 = 5;
	private static int Answer5 = 6;
	private static int Answer6 = 7;
	private static int Answer7 = 8;
	private static int Answer8 = 9;

	public MatchMeQuestions() {

	}

	public MatchMeQuestions(String[] questionArray) {

		Question = new String[questionArray.length];

		Question = questionArray;

	}

	public String getQuestion() {
		return Question[QuestionText];
	}

	public String getAnswer1() {
		return Question[Answer1];
	}

	public String getAnswer2() {
		return Question[Answer2];
	}

	public String getAnswer3() {
		return Question[Answer3];
	}

	public String getAnswer4() {
		return Question[Answer4];
	}

	public String getAnswer5() {
		return Question[Answer5];
	}

	public String getAnswer6() {
		return Question[Answer6];
	}

	public String getAnswer7() {
		return Question[Answer7];
	}

	public String getAnswer8() {
		return Question[Answer8];
	}

	public String getAnswerSelected() {
		return Question[AnswerSelected];
	}
	
	public void putAnswerSelected(String AnswerSelectedValue) {

		this.Question[AnswerSelected] = AnswerSelectedValue;
	}
	
	public int getAnswersSize() {

		return Question.length - 2;
	}

}
