package cr0s.warpdrive.api;


public class ExceptionChunkNotLoaded extends Exception {
	static final long serialVersionUID = -1594655630L;
	
	public ExceptionChunkNotLoaded(final String message) {
		super(message);
	}
}