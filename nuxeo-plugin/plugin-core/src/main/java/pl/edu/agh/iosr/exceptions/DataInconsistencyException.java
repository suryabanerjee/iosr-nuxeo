package pl.edu.agh.iosr.exceptions;

/**
 * Rzucane przez mechnizmy trwałości.
 * */
public class DataInconsistencyException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5428684046464445126L;

	public DataInconsistencyException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public DataInconsistencyException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public DataInconsistencyException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public DataInconsistencyException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
	
}
