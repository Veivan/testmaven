package com.ucoz.time;

import java.io.FileNotFoundException;
import java.io.IOException;

import twitter4j.TwitterException;

public class Time {

    public static void main(String []args) throws FileNotFoundException, IOException, TwitterException{
    	
    	Twitter4jEx tw4j = new Twitter4jEx();   	
    	//tw4j.getOAuthAccessToken();
    	Twitter4jEx.OperateWithAuthToken();
    	
    	// Works well
    	//TwitterExample twe = new TwitterExample();
    	//twe.twgo();
    	//TwitterExample.OperateWithAuthToken();
    	
    	/*
    	ExampleReadINI v = new ExampleReadINI();
    	String dt=new java.text.SimpleDateFormat(("hh:mm aaa")).format(java.util.Calendar.getInstance().getTime());
        JOptionPane.showMessageDialog(null, "������� �����: " + dt + " " + v.SOME_INT_VALUE, "�����", JOptionPane.INFORMATION_MESSAGE);
        */
    }

}
