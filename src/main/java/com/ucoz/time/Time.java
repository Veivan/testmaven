package com.ucoz.time;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JOptionPane;

public class Time {

    public static void main(String []args) throws FileNotFoundException, IOException{
    	
    	//Twitter4jEx tw4j = new Twitter4jEx();   	
    	//tw4j.getOAuthAccessToken();
    	
    	TwitterExample twe = new TwitterExample();
    	//twe.twgo();
    	TwitterExample.OperateWithAuthToken();
    	
    	/*
    	ExampleReadINI v = new ExampleReadINI();
    	String dt=new java.text.SimpleDateFormat(("hh:mm aaa")).format(java.util.Calendar.getInstance().getTime());
        JOptionPane.showMessageDialog(null, "������� �����: " + dt + " " + v.SOME_INT_VALUE, "�����", JOptionPane.INFORMATION_MESSAGE);
        */
    }

}
