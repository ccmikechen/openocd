package openocd;

public class Main {

	public static void main(String[] args) throws Exception {
		
		String endian = "little";
		String file = "";
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-l") || args[i].equals("--little")) {
				endian = "little";
			} else if (args[i].equals("-b") || args[i].equals("--big")) {
				endian = "big";
			} else if (file.equals("")) {
				file = args[i];
			} else {
				usage();
				System.exit(0);
			}
		}

		if (file.equals("")) {
			usage();
			System.exit(0);
		}
		
		OpenocdCracker cracker = new OpenocdCracker();
		cracker.dumpToFile(file, endian);
	}
	
	public static void usage() {
		System.out.println("Usage:");
		System.out.println("-b --big      Big endian");
		System.out.println("-l --little   Little endian");
		System.out.println("Example:");
		System.out.println("java -jar oc.jar -l dump.bin");
	}
	
}