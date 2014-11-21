package org.jitsi.examples;

public class UnitTest 
{
	public static final String audioEncoding = "PCMU";
	public static final String videoEncoding = "H264";
	public static final double audioClockRate = 8000;
	public static final byte audioPayloadType = 73;
	public static final byte videoPayloadType = 99;

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		final String localhost = "192.168.0.154";
		final String remotehost = "192.168.0.154";
		new Thread() {
			public void run() {
				try {
					AVReceive2.main(new String[] { "--local-port-base=10000", "--remote-host=" + remotehost, "--remote-port-base=5000" });
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();

		new Thread() {
			public void run() {
				try {
					AVTransmit2.main(new String[] { "--local-host=" + localhost, "--remote-host=" + remotehost, "--remote-port-base=10000" });
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
}