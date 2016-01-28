package userinterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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

}
