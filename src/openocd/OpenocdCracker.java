package openocd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class OpenocdCracker {

	public static final String BIG_ENDIAN = "big";
	public static final String LITTLE_ENDIAN = "little";
	
	private OpenocdConnector oc;
	
	private int initPC;
	
	private int initSP;
	
	public OpenocdCracker() throws IOException {
		oc = new OpenocdConnector(4444);
		init();
	}
	
	public void init() throws IOException {
		oc.resetHalt();
		initPC = oc.getRegValue("pc");
		initSP = oc.getRegValue("sp");
	}
	
	public void dumpToFile(String path, String endian) throws IOException {
		System.out.println("Finding ldr command...");
		LdrCommand ldrCommand = getLdrCommand();
		System.out.println("Found ldr command:");
		System.out.println("\t" + ldrCommand);
		
		FileOutputStream output = new FileOutputStream(new File(path));
		System.out.println("Dumping to file " + path + "...");
		for (int i = 0; i < 256; i++) {
			System.out.println("Progress " + i + "/256");
			for (int j = 0; j < 1024; j += 4) {
				int address = i * 1024 + j;
				int value = ldrCommand.execute(address);
				byte[] bytes = intToByteArray(value, endian);
				output.write(bytes);
			}
			output.flush();
		}
		output.close();
		System.out.println("Done!");
	}
	
	private byte[] intToByteArray(int n, String endian) {
		 byte[] bytes = new byte[4];
		 if (endian.equals(LITTLE_ENDIAN)) {
			 bytes[0] = (byte) ((n & 0x000000FF) >>  0);
			 bytes[1] = (byte) ((n & 0x0000FF00) >>  8);
			 bytes[2] = (byte) ((n & 0x00FF0000) >> 16);
			 bytes[3] = (byte) ((n & 0xFF000000) >> 24);
		 } else if (endian.equals(BIG_ENDIAN)) {
			 bytes[0] = (byte) ((n & 0xFF000000) >> 24);
			 bytes[1] = (byte) ((n & 0x00FF0000) >> 16);
			 bytes[2] = (byte) ((n & 0x0000FF00) >>  8);
			 bytes[3] = (byte) ((n & 0x000000FF) >>  0);
		 } else {
			 return intToByteArray(n, LITTLE_ENDIAN);
		 }
		 return bytes;
	}
	
	public LdrCommand getLdrCommand() throws IOException {
		LdrCommand ldrCommand = new LdrCommand(oc);
		for (int pc = initPC; pc < 256 * 1024; pc += 2) {
			for (int i = 0; i <= 12; i++) {
				oc.setRegValue("pc", pc);
				oc.setRegValue("r" + i, 0x04);
				oc.step();
				for (int j = 0; j <= 12; j++) {
					int value = oc.getRegValue("r" + j) - 1;
					if (value == initPC) {
						ldrCommand.setCommandAddress(pc);
						ldrCommand.setSrcReg("r" + i);
						ldrCommand.setDstReg("r" + j);
						return ldrCommand;
					}
				}
			}
		}
		return null;
	}
	
}
