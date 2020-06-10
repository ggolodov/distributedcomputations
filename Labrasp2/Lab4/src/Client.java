import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Client {
    public static void main(String[] argv) throws RemoteException, NotBoundException, MalformedURLException {
        String url = "//localhost:123/Map";
        Map map = (Map) Naming.lookup(url);
        System.out.println("RMI object found");
        String response = map.getCountries();
        System.out.println(response);
        System.out.println("-----------------");
        response = map.getCitiesOfCountry(1);
        System.out.println(response);
        System.out.println("-----------------");
        response = map.updateCity(1, "MOSCOW-WOCSOM", 11612934, true, 1);
        System.out.println(response);
        System.out.println("-----------------");
        response = map.getCitiesOfCountry(1);
        System.out.println(response);
        System.out.println("-----------------");
        response = map.getCitiesOfCountry(6);
        System.out.println(response);
        System.out.println("-----------------");
        response = map.addCity(4, "Kiyv", 3000000, true, 6);
        System.out.println(response);
        System.out.println("-----------------");
        response = map.getCitiesOfCountry(6);
        System.out.println(response);
        System.out.println("-----------------");
        response = map.countCitiesOfCountry(1);
        System.out.println(response);
        System.out.println("-----------------");
        response = map.deleteCity(4);
        System.out.println(response);
        System.out.println("-----------------");
        response = map.getCitiesOfCountry(6);
        System.out.println(response);
        System.out.println("-----------------");
        response = map.getCitiesOfCountry(1);
        System.out.println(response);
        System.out.println("-----------------");
        response = map.deleteCountry(10);
        System.out.println(response);
        System.out.println("-----------------");
        response = map.getCountries();
        System.out.println(response);
    }
}
