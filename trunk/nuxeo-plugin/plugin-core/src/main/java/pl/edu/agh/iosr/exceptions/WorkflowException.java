package pl.edu.agh.iosr.exceptions;

/**
 * Oznacza błąd w przepływie dokumentów
 * */
public class WorkflowException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7939291163460491787L;

	public WorkflowException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public WorkflowException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public WorkflowException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public WorkflowException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
	
}
