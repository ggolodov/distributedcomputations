import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.Statement;

public interface Map extends Remote {
    Connection conn = null;
    Statement statement = null;
    public String addCountry(int id, String name) throws RemoteException;
    public String addCity(int id, String name, int count, boolean isCapital, int countryID) throws RemoteException;
    public String deleteCountry(int id) throws RemoteException;
    public String deleteCity(int id) throws RemoteException;
    public String updateCity(int id, String name, int count, boolean isCapital, int countryID) throws RemoteException;
    public String getCitiesOfCountry(int id) throws RemoteException;
    public String countCitiesOfCountry(int id) throws RemoteException;
    public String getCountries() throws RemoteException;
}
