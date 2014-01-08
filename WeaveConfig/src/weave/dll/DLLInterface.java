package weave.dll;

public class DLLInterface 
{
	static {
		System.setProperty("java.library.path", ".");
		
		// Load 32 or 64 bit version of DLL depending on system arch
		System.loadLibrary("DLLInterface" + System.getProperty("sun.arch.data.model"));
	}
	public static native void refresh() throws UnsatisfiedLinkError;
	public static native void flashTaskbar(String windowTitle, boolean flash);
}
