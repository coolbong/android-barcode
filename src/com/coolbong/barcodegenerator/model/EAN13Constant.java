package com.coolbong.barcodegenerator.model;


public class EAN13Constant {

	public static final byte L_CODE = 0;
	public static final byte G_CODE = 1;
	public static final byte R_CODE = 2;

	public static final byte[][] FIRST_DIGIT = {
		{ L_CODE,L_CODE,L_CODE,L_CODE,L_CODE,L_CODE,R_CODE,R_CODE,R_CODE,R_CODE,R_CODE,R_CODE },
		{ L_CODE,L_CODE,G_CODE,L_CODE,G_CODE,G_CODE,R_CODE,R_CODE,R_CODE,R_CODE,R_CODE,R_CODE },
		{ L_CODE,L_CODE,G_CODE,G_CODE,L_CODE,G_CODE,R_CODE,R_CODE,R_CODE,R_CODE,R_CODE,R_CODE },
		{ L_CODE,L_CODE,G_CODE,G_CODE,G_CODE,L_CODE,R_CODE,R_CODE,R_CODE,R_CODE,R_CODE,R_CODE },
		{ L_CODE,G_CODE,L_CODE,L_CODE,G_CODE,G_CODE,R_CODE,R_CODE,R_CODE,R_CODE,R_CODE,R_CODE },
		{ L_CODE,G_CODE,G_CODE,L_CODE,L_CODE,G_CODE,R_CODE,R_CODE,R_CODE,R_CODE,R_CODE,R_CODE },
		{ L_CODE,G_CODE,G_CODE,G_CODE,L_CODE,L_CODE,R_CODE,R_CODE,R_CODE,R_CODE,R_CODE,R_CODE },
		{ L_CODE,G_CODE,L_CODE,G_CODE,L_CODE,G_CODE,R_CODE,R_CODE,R_CODE,R_CODE,R_CODE,R_CODE },
		{ L_CODE,G_CODE,L_CODE,G_CODE,G_CODE,L_CODE,R_CODE,R_CODE,R_CODE,R_CODE,R_CODE,R_CODE },
		{ L_CODE,G_CODE,G_CODE,L_CODE,G_CODE,L_CODE,R_CODE,R_CODE,R_CODE,R_CODE,R_CODE,R_CODE },
	};
	
	public static final byte[] START_PATTERN = { 1, 0, 1 };
	
	public static final byte[] MIDDLE_PATTERN = { 0, 1, 0, 1, 0 };
	
	public static final byte[] END_PATTERN =  { 1, 0, 1 };
	
	
	// l-code
	public static final byte[][] L_CODE_PATTERN = {
		{ 0,0,0,1,1,0,1 },
		{ 0,0,1,1,0,0,1 },
		{ 0,0,1,0,0,1,1 },
		{ 0,1,1,1,1,0,1 },
		{ 0,1,0,0,0,1,1 },
		{ 0,1,1,0,0,0,1 },
		{ 0,1,0,1,1,1,1 },
		{ 0,1,1,1,0,1,1 },
		{ 0,1,1,0,1,1,1 },
		{ 0,0,0,1,0,1,1 },
	};

	// g-code
	public static final byte[][] G_CODE_PATTERN = {
		{ 0,1,0,0,1,1,1 },
		{ 0,1,1,0,0,1,1 },
		{ 0,0,1,1,0,1,1 },
		{ 0,1,0,0,0,0,1 },
		{ 0,0,1,1,1,0,1 },
		{ 0,1,1,1,0,0,1 },
		{ 0,0,0,0,1,0,1 },
		{ 0,0,1,0,0,0,1 },
		{ 0,0,0,1,0,0,1 },
		{ 0,0,1,0,1,1,1 },
	};

	// r-code
	public static final byte[][] R_CODE_PATTERN = {
		{ 1,1,1,0,0,1,0 },
		{ 1,1,0,0,1,1,0 },
		{ 1,1,0,1,1,0,0 },
		{ 1,0,0,0,0,1,0 },
		{ 1,0,1,1,1,0,0 },
		{ 1,0,0,1,1,1,0 },
		{ 1,0,1,0,0,0,0 },
		{ 1,0,0,0,1,0,0 },
		{ 1,0,0,1,0,0,0 },
		{ 1,1,1,0,1,0,0 },
	};
}
