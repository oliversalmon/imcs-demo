package com.trade.injector.enums;

public enum ExerciseStyle {
	EUROPEAN(0), AMERICAN(1), BERMUDAN(2);
	
	private int exerciseStyle;
	
	private ExerciseStyle(int p_exerciseStyle){
		setExerciseStyle(p_exerciseStyle);
	}

	public int getExerciseStyle() {
		return exerciseStyle;
	}

	public void setExerciseStyle(int exerciseStyle) {
		this.exerciseStyle = exerciseStyle;
	}
	
	
}
