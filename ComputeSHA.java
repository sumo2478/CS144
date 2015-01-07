import java.security.*;
import java.io.FileInputStream;

public class ComputeSHA {
	public static void main(String args[]) throws Exception {
		if (args.length > 0) {
			String filename = args[0];
			FileInputStream fis = new FileInputStream(filename);			
			MessageDigest md = MessageDigest.getInstance("SHA-1");			

			byte[] dataBytes = new byte[1024];

			int nread = 0;
			while ((nread = fis.read(dataBytes)) != -1) {
				md.update(dataBytes, 0, nread);
			}

			byte[] mdbytes = md.digest();

			// Convert bytes to hex format
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < mdbytes.length; i++) {
				sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));				
			}

			System.out.println(sb.toString());
		}
		else {
			System.out.println("No args given");
		}		
	}    
}
