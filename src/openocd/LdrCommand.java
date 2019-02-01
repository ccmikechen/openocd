package openocd;

import java.io.IOException;

public class LdrCommand {

	private OpenocdConnector oc;
	
	private int commandAddress;
	
	private String srcReg;
	
	private String dstReg;
	
	public LdrCommand(OpenocdConnector oc) {
		this.oc = oc;
	}
	
	public void setCommandAddress(int address) {
		this.commandAddress = address;
	}
	
	public void setSrcReg(String reg) {
		this.srcReg = reg;
	}
	
	public void setDstReg(String reg) {
		this.dstReg = reg;
	}
	
	public int execute(int address) throws IOException {
		oc.setRegValue("pc", commandAddress);
		oc.setRegValue(srcReg, address);
		oc.step();
		return oc.getRegValue(dstReg);
	}
	
	public String toString() {
		return String.format("0x%08X ldr %s [%s]", commandAddress, dstReg, srcReg);
	}
	
}
