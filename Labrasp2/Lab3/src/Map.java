import java.sql.*;
import java.util.ArrayList;

public class Map {
    private Connection conn;
    private Statement statement;

    public Map(String DBName, String ip, int port) throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {
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
            return "Cтрана " + name + " успешно добавлена!";
        } catch (SQLException e) {
            return "ОШИБКА! Страна " + name + " не добавлена!     >> " + e.getMessage();
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
                return "Ошибка! Страны номер " + countryID + " не существует!";
            }

            statement.executeUpdate(sql);
            return "Город " + name + " успешно добавлен!";
        } catch (SQLException e) {
            return "ОШИБКА! Город " + name + " не добавлен!     >> " + e.getMessage();
        }
    }

    public String deleteCountry(int id){
        String sql = "DELETE FROM COUNTRIES WHERE ID_CO = "+ id;
        try {
            int c = statement.executeUpdate(sql);
            if (c > 0){
                return "Страна с идентификатором " + id +" успешно удалена!";
            } else {
                return "Страна с идентификатором " + id +" не найдена!";
            }
        } catch (SQLException e) {
            return "Страна с идентификатором " + id +" не найдена!";
        }
    }

    public String deleteCity(int id){
        String sql = "DELETE FROM CITIES WHERE ID_CI = "+ id;
        try {
            int c = statement.executeUpdate(sql);
            if (c > 0){
                return "Город с идентификатором " + id +" успешно удален!";
            } else {
                return "Город с идентификатором " + id +" не найден!";
            }
        } catch (SQLException e) {
            return "Город с идентификатором " + id +" не найден!";
        }
    }

    public String getCountry(int id){
        String sql = "SELECT * FROM COUNTRIES WHERE ID_CO = " + id;
        try {
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()){
                String name = rs.getString("NAME");
                return "Страна №" + id + " " + name;
            }
            rs.close();

            return "Страна с идентификатором " + id +" не найдена!";

        } catch (SQLException e) {
            return "Страна с идентификатором " + id +" не найдена!";
        }
    }

    public String getCity(int id){
        String sql = "SELECT * FROM CITIES WHERE ID_CI = " + id;
        try {
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()){
                int count = rs.getInt("COUNT");
                int capInt = rs.getInt("ISCAPITAL");
                boolean isCapital = false;
                if (capInt == 1){
                    isCapital = true;
                }
                String name = rs.getString("NAME");
                int country = rs.getInt("ID_CO");
                return "Город №" + id + " " + name + ":      Население: " + count + "     Столица: " + isCapital + "     Номер страны: " + country;
            }
            rs.close();
            return "Город с идентификатором " + id +" не найден!";

        } catch (SQLException e) {
            return "Город с идентификатором " + id +" не найден!";
        }
    }

    public String updateCountry(int id, String name){
        String sql = "UPDATE COUNTRIES SET NAME= '" + name + "' WHERE ID_CO = " + id;
        try {
            statement.executeUpdate(sql);
            return "Cтрана " + name + " успешно обновлена!";
        } catch (SQLException e) {
            return "ОШИБКА! Страна " + name + " не обновлена!     >> " + e.getMessage();
        }
    }

    public String updateCity(int id, String name, int count, boolean isCapital, int countryID){
        String sql = "UPDATE CITIES SET ID_CO = " + countryID + ", NAME = '" + name + "', COUNT = " + count + ", ISCAPITAL = " + isCapital + " WHERE ID_CI = " + id;
        String getIDSql = "SELECT ID_CO FROM COUNTRIES";
        ArrayList<Integer> ids = new ArrayList<>();
        try {
            ResultSet rs = statement.executeQuery(getIDSql);
            while (rs.next()){
                ids.add(rs.getInt("ID_CO"));
            }
            rs.close();

            boolean canChange = false;
            for(Integer idCo : ids){
                if (idCo == countryID){
                    canChange = true;
                    break;
                }
            }

            if (!canChange){
                return "Ошибка! Страны номер " + countryID + " не существует!";
            }

            statement.executeUpdate(sql);
            return "Город " + name + " успешно обновлен!";
        } catch (SQLException e) {
            return "ОШИБКА! Город " + name + " не обновлен!     >> " + e.getMessage();
        }
    }

    public String showCountries() {
        String sql = "SELECT ID_CO, NAME FROM COUNTRIES";
        String result = "";
        try {
            ResultSet rs = statement.executeQuery(sql);
            result = "СПИСОК СТРАН:";
            while (rs.next())
            {
                int id = rs.getInt("ID_CO");
                String name = rs.getString("NAME");
                result += " >> "+ id + " - " + name;
            }
            rs.close();
            return result;
        } catch (SQLException e) {
            return "ОШИБКА при получении списка стран  >> " +  e.getMessage();
        }
    }

    public String showCitiesOfCountry(int id){
        String sql = "SELECT * FROM CITIES WHERE ID_CO = " + id;
        String result = "";
        try {
            ResultSet rs = statement.executeQuery(sql);
            result = "Города страны №" + id;
            while (rs.next()){
                int count = rs.getInt("COUNT");
                int capInt = rs.getInt("ISCAPITAL");
                boolean isCapital = false;
                if (capInt == 1){
                    isCapital = true;
                }
                String name = rs.getString("NAME");
                int cityId = rs.getInt("ID_CI");
                result += " >>  Город №" + cityId + " " + name + ":        Население: " + count + "         Столица: " + isCapital;
            }
            rs.close();
            return result;
        } catch (SQLException e) {
            return "Страна с идентификатором " + id +" не найден!";
        }
    }
}

