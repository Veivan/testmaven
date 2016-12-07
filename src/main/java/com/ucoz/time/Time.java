package com.ucoz.time;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JOptionPane;

public class Time {

    public static void main(String []args) throws FileNotFoundException, IOException{
    	
    	Twitter4jEx.SetPropsINI();
    	
    	TwitterExample twe = new TwitterExample();
    	//twe.twgo();
    	//twe.OperateWithAuthToken();
    	
    	/*
    	ExampleReadINI v = new ExampleReadINI();
    	String dt=new java.text.SimpleDateFormat(("hh:mm aaa")).format(java.util.Calendar.getInstance().getTime());
        JOptionPane.showMessageDialog(null, "������� �����: " + dt + " " + v.SOME_INT_VALUE, "�����", JOptionPane.INFORMATION_MESSAGE);
        */
    }

}
