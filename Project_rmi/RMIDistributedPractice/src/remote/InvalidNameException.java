package remote;
import java.rmi.RemoteException;
public class InvalidNameException extends RemoteException {
    public InvalidNameException(String message) {
        super(message);
    }
}