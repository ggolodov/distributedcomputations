import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public Client(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    private String sendQuery(int operation, int id_co, String name_country, int id_ci, String name_ci, int count, int isCapital) throws IOException {
        String id_co_s = "", id_ci_s = "", count_s ="", isCapital_s = "";
        if (id_co != 0){
            id_co_s += id_co;
        }
        if (id_ci != 0){
            id_ci_s += id_ci;
        }
        if (count != 0){
            count_s+= count;
        }
        if (isCapital == 0){
            isCapital_s = "false";
        } else if (isCapital ==1) {
            isCapital_s = "true";
        }
        String query = operation + "#" + id_co_s + "#" + name_country + "#" + id_ci_s+ "#" + name_ci + "#" + count_s + "#" + isCapital_s;
        out.println(query);
        String response = in.readLine();
        String[] fields = response.split("#");
        if (fields.length != 2){
            throw new IOException("Invalid response from server");
        }

        try {
            int comp_code = Integer.parseInt(fields[0]);
            String result = fields[1];
            if (comp_code == 0){
                return result;
            } else {
                throw new IOException("Error while processing query - " + comp_code);
            }
        } catch (NumberFormatException e){
            throw new IOException("Invalid response from server");
        }
    }

    public String addCountry(int id, String name) throws IOException {
        return sendQuery(1, id, name, 0, "", 0, -1);
    }

    public String addCity(int id, String name, int count, boolean isCapital, int countryID) throws IOException {
        if (isCapital){
            return sendQuery(2, countryID, "", id, name, count, 1);
        } else {
            return sendQuery(2, countryID, "", id, name, count, 0);
        }
    }

    public String deleteCountry(int id) throws IOException {
        return sendQuery(3, id, "", 0, "", 0, -1);
    }

    public String deleteCity(int id) throws IOException {
        return sendQuery(4, 0, "", id, "", 0, -1);
    }

    public String getCountry(int id) throws IOException {
        return sendQuery(5, id, "", 0, "", 0, -1);
    }

    public String getCity(int id) throws IOException {
        return sendQuery(6, 0, "", id, "", 0, -1);
    }

    public String updateCountry(int id, String name) throws IOException {
        return sendQuery(7, id, name, 0, "", 0, -1);
    }

    public String updateCity(int id, String name, int count, boolean isCapital, int countryID) throws IOException {
        if (isCapital){
            return sendQuery(8, countryID, "", id, name, count, 1);
        } else {
            return sendQuery(8, countryID, "", id, name, count, 0);
        }
    }

    public String getCountries() throws IOException {
        return sendQuery(9, 0, "", 0, "", 0, -1);
    }

    public String getCitiesOfCountry(int id) throws IOException {
        return sendQuery(10, id, "", 0, "", 0, -1);
    }

    public void disconnect() throws IOException {
        socket.close();
    }

    public static void main(String[] argv) throws IOException {
        Client client = new Client("localhost", 9000);
        String response;
        response = client.getCountries();
        System.out.println(response);
        System.out.println("-----------------");
        response = client.getCity(1);
        System.out.println(response);
        System.out.println("-----------------");
        response = client.updateCity(1, "MOSCOW-WOCSOM", 11612934, true, 1);
        System.out.println(response);
        System.out.println("-----------------");
        response = client.getCity(1);
        System.out.println(response);
        System.out.println("-----------------");
        response = client.getCity(4);
        System.out.println(response);
        System.out.println("-----------------");
        response = client.addCity(4, "Kiyv", 3000000, true, 6);
        System.out.println(response);
        System.out.println("-----------------");
        response = client.getCity(4);
        System.out.println(response);
        System.out.println("-----------------");
        response = client.updateCountry(1, "RUSSIA-24");
        System.out.println(response);
        System.out.println("-----------------");
        response = client.getCountries();
        System.out.println(response);
        System.out.println("-----------------");
        response = client.deleteCity(4);
        System.out.println(response);
        System.out.println("-----------------");
        client.getCity(4);
        System.out.println(response);
        System.out.println("-----------------");
        response = client.getCitiesOfCountry(1);
        System.out.println(response);
        System.out.println("-----------------");
        response = client.deleteCountry(7);
        System.out.println(response);
        System.out.println("-----------------");
        response = client.getCountries();
        System.out.println(response);
        client.disconnect();
    }
}
