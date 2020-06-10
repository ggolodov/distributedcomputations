import javafx.util.Pair;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Map {
    private Connection conn;
    private Statement statement;

    protected Map(String DBName, String ip, int port) throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        String url = "jdbc:mysql://" + ip + ":" + port + "/" + DBName;
        conn = DriverManager.getConnection(url, "root", "admin");
        statement = conn.createStatement();
    }

    public void stop() throws SQLException {
        conn.close();
    }

    public String addCountry(int id, String name) {
        String sql = "INSERT INTO COUNTRIES (ID_CO, NAME) " + "VALUES (" + id + ", '" + name + "')";
        try {
            statement.executeUpdate(sql);
            return "Country " + name + " successfully added!";
        } catch (SQLException e) {
            return "ERROR! Country " + name + " not added!     >> " + e.getMessage();
        }
    }

    public String addCity(int id, String name, int count, boolean isCapital, int countryID) {
        String sql = "INSERT INTO CITIES (ID_CI, ID_CO, NAME, COUNT, ISCAPITAL) VALUES (" + id + ", " + countryID + ", '" + name + "', " + count + ", " + isCapital + ")";
        String getIDSql = "SELECT ID_CO FROM COUNTRIES";
        ArrayList<Integer> ids = new ArrayList<>();
        try {
            ResultSet rs = statement.executeQuery(getIDSql);
            while (rs.next()){
                ids.add(rs.getInt("ID_CO"));
            }
            rs.close();

            boolean canAdd = false;
            for(Integer idCo : ids){
                if (idCo == countryID){
                    canAdd = true;
                    break;
                }
            }

            if (!canAdd){
                return "Error! Country number " + countryID + " does not exist!";
            }

            statement.executeUpdate(sql);
            return "City " + name + " successfully added!";
        } catch (SQLException e) {
            return "ERROR! City " + name + " not added!     >> " + e.getMessage();
        }
    }

    public String deleteCity(int id) {
        String sql = "DELETE FROM CITIES WHERE ID_CI = "+ id;
        try {
            int c = statement.executeUpdate(sql);
            if (c > 0){
                return "City number " + id +" successfully deleted!";
            } else {
                return "City number " + id +" not found!";
            }
        } catch (SQLException e) {
            return "City number " + id +" not found!";
        }
    }

    public Pair<Integer, String> getCities() {
        String result = "";
        try {
            int countCities = 0;
            List<Pair<Integer, String>> countries = new ArrayList<>();

            String sql = "SELECT ID_CO, NAME FROM COUNTRIES";
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next())
            {
                int id = rs.getInt("ID_CO");
                String name = rs.getString("NAME");
                countries.add(new Pair<>(id, name));
            }
            rs.close();

            sql = "SELECT * FROM CITIES";
            rs = statement.executeQuery(sql);
            while (rs.next()){
                countCities++;
                int count = rs.getInt("COUNT");
                int capInt = rs.getInt("ISCAPITAL");
                boolean isCapital = false;
                if (capInt == 1){
                    isCapital = true;
                }
                String name = rs.getString("NAME");
                int cityId = rs.getInt("ID_CI");
                int countryID = rs.getInt("ID_CO");
                String countryName = "";
                for(Pair<Integer, String> pair : countries){
                    if (pair.getKey() == countryID){
                        countryName = pair.getValue();
                        break;
                    }
                }
                result += " >>  City #" + cityId + " " + name + ":        Population: " + count + "         Capital: " + isCapital + "       Country: " + countryName + "\n";
            }
            rs.close();
            return new Pair<>(countCities, result);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return new Pair<>(0, "Cities not found!");
        }
    }
}

