import java.security.*;
import java.io.FileInputStream;

public class ComputeSHA {
	public static void main(String args[]) throws Exception {
        // Check to make sure a file to compute SHA for is given
		if (args.length > 0) {
			String filename = args[0]; // Filename of the text to compute SHA for
			FileInputStream fis = new FileInputStream(filename);			
			MessageDigest md = MessageDigest.getInstance("SHA-1");			

            int n = 0;
			byte[] dataBuffer = new byte[1024];

            // Read in the data from the file and add it to the message digest            
			while ((n = fis.read(dataBuffer)) != -1) {
				md.update(dataBuffer, 0, n);
			}

			byte[] mdBuffer = md.digest();

			// Convert bytes to hex format
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < mdBuffer.length; i++) {
				sb.append(Integer.toString((mdBuffer[i] & 0xff) + 0x100, 16).substring(1)); // Added converted bytes to string buffer
			}

            // Print out computed SHA
			System.out.println(sb.toString());
		}
		else {
			System.out.println("No args given");
		}		
	}    
}
