package com.info.socialnetworking.app.helper;

public class PrivateQuestionsDetails {

	private String[] questionsData;
	private static int Question = 0;
	private static int QuestionNumber = 1;
	private static int Answer1Text = 2;
	private static int Answer2Text = 3;
	private static int Answer3Text = 4;
	private static int Answer4Text = 5;
	private static int Answer1Value = 6;
	private static int Answer2Value = 7;
	private static int Answer3Value = 8;
	private static int Answer4Value = 9;

	public PrivateQuestionsDetails() {
		// TODO Auto-generated constructor stub
	}

	public PrivateQuestionsDetails(String[] data) {
		// TODO Auto-generated constructor stub

		questionsData = new String[data.length];
		this.questionsData = data;
	}

	public String getQuestion() {
		return questionsData[Question];
	}

	public void putQuestion(String question) {
		this.questionsData[Question] = question;
	}

	public String getAnswer1() {
		return questionsData[Answer1Text];
	}

	public void putAnswer1(String answer1) {
		this.questionsData[Answer1Text] = answer1;
	}

	public String getAnswer2() {
		return questionsData[Answer2Text];
	}

	public void putAnswer2(String answer2) {
		this.questionsData[Answer2Text] = answer2;
	}

	public String getAnswer3() {
		return questionsData[Answer3Text];
	}

	public void putAnswer3(String answer3) {
		this.questionsData[Answer3Text] = answer3;
	}

	public String getAnswer4() {
		return questionsData[Answer4Text];
	}

	public void putAnswer4(String answer4) {
		this.questionsData[Answer4Text] = answer4;
	}
	
	public String getQuestionNumber() {
		return questionsData[QuestionNumber];
	}

	public void putQuestionNumber(String questionNumber) {
		this.questionsData[QuestionNumber] = questionNumber;
	}
	
	public String getAnswer1Value() {
		return questionsData[Answer1Value];
	}

	public void putAnswer1Value(String answer1Value) {
		this.questionsData[Answer1Value] = answer1Value;
	}
	
	public String getAnswer2Value() {
		return questionsData[Answer2Value];
	}

	public void putAnswer2Value(String answer2Value) {
		this.questionsData[Answer2Value] = answer2Value;
	}
	public String getAnswer3Value() {
		return questionsData[Answer3Value];
	}

	public void putAnswer3Value(String answer3Value) {
		this.questionsData[Answer3Value] = answer3Value;
	}
	public String getAnswer4Value() {
		return questionsData[Answer4Value];
	}

	public void putAnswer4Value(String answer4Value) {
		this.questionsData[Answer4Value] = answer4Value;
	}	
	
}
