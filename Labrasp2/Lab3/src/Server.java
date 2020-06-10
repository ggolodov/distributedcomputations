import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class Server {
    private ServerSocket server;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Map map;

    public void start(int port) throws IOException, ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        server = new ServerSocket(port);
        int count = Integer.MIN_VALUE;
        while (true){
            count++;
            socket = server.accept();
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            while (processQuery());
            if (count == Integer.MAX_VALUE) break;
        }
    }

    private boolean processQuery() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        String result = "";
        int comp_code = 0;
        try{

            String query = in.readLine();
            if (query == null) return false;
            String[] fields = query.split("#");
            int operation = -1;
            try {
                operation = Integer.parseInt(fields[0]);
            } catch (NumberFormatException e){
                comp_code = 3;
            }

            map = new Map("map", "localhost", 3306);

            if (operation == 1){
                if (fields.length != 3){
                    comp_code = 1;
                } else {
                    try{
                        int id = Integer.parseInt(fields[1]);
                        String name = fields[2];
                        result = map.addCountry(id, name);
                    } catch (NumberFormatException e){
                        comp_code = 3;
                    }
                }
            } else if (operation == 2){
                if (fields.length != 7){
                    comp_code = 1;
                } else {
                    try{
                        int Countryid = Integer.parseInt(fields[1]);
                        int id = Integer.parseInt(fields[3]);
                        String name = fields[4];
                        int count = Integer.parseInt(fields[5]);
                        boolean isCapital = Boolean.parseBoolean(fields[6]);
                        result = map.addCity(id, name, count, isCapital, Countryid);
                    } catch (NumberFormatException e){
                        comp_code = 3;
                    }
                }
            } else if (operation == 3){
                if (fields.length != 2){
                    comp_code = 1;
                } else {
                    try{
                        int id = Integer.parseInt(fields[1]);
                        result = map.deleteCountry(id);
                    } catch (NumberFormatException e){
                        comp_code = 3;
                    }
                }
            } else if (operation == 4){
                if (fields.length != 4){
                    comp_code = 1;
                } else {
                    try{
                        int id = Integer.parseInt(fields[3]);
                        result = map.deleteCity(id);
                    } catch (NumberFormatException e){
                        comp_code = 3;
                    }
                }
            } else if (operation == 5){
                if (fields.length != 2){
                    comp_code = 1;
                } else {
                    try{
                        int id = Integer.parseInt(fields[1]);
                        result = map.getCountry(id);
                    } catch (NumberFormatException e){
                        comp_code = 3;
                    }
                }
            } else if (operation == 6){
                if (fields.length != 4){
                    comp_code = 1;
                } else {
                    try{
                        int id = Integer.parseInt(fields[3]);
                        result = map.getCity(id);
                    } catch (NumberFormatException e){
                        comp_code = 3;
                    }
                }
            } else if (operation == 7){
                if (fields.length != 3){
                    comp_code = 1;
                } else {
                    try{
                        int id = Integer.parseInt(fields[1]);
                        String name = fields[2];
                        result = map.updateCountry(id, name);
                    } catch (NumberFormatException e){
                        comp_code = 3;
                    }
                }
            } else if (operation == 8){
                if (fields.length != 7){
                    comp_code = 1;
                } else {
                    try{
                        int Countryid = Integer.parseInt(fields[1]);
                        int id = Integer.parseInt(fields[3]);
                        String name = fields[4];
                        int count = Integer.parseInt(fields[5]);
                        boolean isCapital = Boolean.parseBoolean(fields[6]);
                        result = map.updateCity(id, name, count, isCapital, Countryid);
                    } catch (NumberFormatException e){
                        comp_code = 3;
                    }
                }
            } else if (operation == 9){
                if (fields.length != 1){
                    comp_code = 1;
                } else {
                    try{
                        result = map.showCountries();
                    } catch (NumberFormatException e){
                        comp_code = 3;
                    }
                }
            } else if (operation == 10){
                if (fields.length != 2){
                    comp_code = 1;
                } else {
                    try{
                        int id = Integer.parseInt(fields[1]);
                        result = map.showCitiesOfCountry(id);
                    } catch (NumberFormatException e){
                        comp_code = 3;
                    }
                }
            }
            else {
                comp_code = 2;
            }

            map.stop();

            String response = comp_code + "#" + result;
            out.println(response);
            return true;
        } catch (IOException eq) {
            return false;
        }

    }

    public static void main(String[] argv) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        try {
            Server server = new Server();
            System.out.println("Server has started");
            server.start(9000);

        } catch (IOException e){
            System.out.println("Возникла ошибка");
        }
    }
}
