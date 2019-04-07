package com.demo;

/**
 * FIXME: Add a description
 * 
 * @author Niranjan Nanda
 */
public class CustomException extends RuntimeException {
	/**  */
	private static final long serialVersionUID = 1L;

	public CustomException(final String message) {
        super(message);
    }
    
    public CustomException(final String message, final Throwable t) {
        super(message, t);
    }
    
    public CustomException(final Throwable t) {
        super(t);
    }
}
