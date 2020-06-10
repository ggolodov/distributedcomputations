import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;

public class Server {
    public static void main(String[] argv) throws RemoteException, ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        MapImpl map = new MapImpl("map", "localhost", 3306);
        Registry registry = LocateRegistry.createRegistry(123);
        registry.rebind("Map", map);
        System.out.println("Server started");
    }
}
