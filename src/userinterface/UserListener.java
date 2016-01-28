package userinterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import exceptions.InvalidArgumentException;

public class UserListener {
	
	public UserListener() {
		
	}
	
	public static String readString() {
		String antw = null;
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					System.in));
			antw = in.readLine();
		} catch (IOException e) {
		}

		return (antw == null) ? "" : antw;
	}
	
	public static int readInt(String input) throws InvalidArgumentException {
        int integer; 
//        @SuppressWarnings("resource")
        try (Scanner scannerLine = new Scanner(input);) {
            if (scannerLine.hasNextInt()) {
                integer = scannerLine.nextInt();
            } else {
            	throw new InvalidArgumentException();
            }
        }
        return integer;
    }

	public static char readChar(String input) throws InvalidArgumentException {
        char character; 
//        @SuppressWarnings("resource")
        try (Scanner scannerLine = new Scanner(input);) {
            if (scannerLine.hasNext("[A-Za-z]+")) {
                character = scannerLine.next("[A-Za-z]+").charAt(0);
            } else {
            	throw new InvalidArgumentException();
            }
        }
        return character;
    }
	

}
