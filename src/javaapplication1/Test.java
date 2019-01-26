package javaapplication1;

import java.util.ArrayList;
import javaapplication1.Filtfilt;


public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// Filtfilt.doFiltfilt();
		ArrayList<Double> B = new ArrayList<Double>();
		ArrayList<Double> A = new ArrayList<Double>();
		ArrayList<Double> X = new ArrayList<Double>();
		
		B.add(-54.0);
		B.add(5.0);
		
		A.add(1.0);
		A.add(2.0);
		
		X.add(1.0);
		X.add(2.0);
		X.add(3.0);

		
		ArrayList<Double> y = Filtfilt.doFiltfilt(B,A,X);
		for (int i = 0; i < y.size(); i++)
			System.out.println(y.get(i));
                
                
	}

}
