package friday.exception;

/**
 * Returns FridayException if the input fails to match
 * the required format.
 */
public class FridayException extends Exception {
    public FridayException(String msg) {
        super(msg);
    }
}
