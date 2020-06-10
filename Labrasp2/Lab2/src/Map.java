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

    public boolean addCountry(int id, String name) {
        String sql = "INSERT INTO COUNTRIES (ID_CO, NAME) " + "VALUES (" + id + ", '" + name + "')";
        try {
            statement.executeUpdate(sql);
            System.out.println("Cтрана " + name + " успешно добавлена!");
            return true;
        } catch (SQLException e) {
            System.out.println("ОШИБКА! Страна " + name + " не добавлена!");
            System.out.println("    >> " + e.getMessage());
            return false;
        }
    }

    public boolean addCity(int id, String name, int count, boolean isCapital, int countryID) {
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
                System.out.println("Ошибка! Страны номер " + countryID + " не существует!");
                return false;
            }

            statement.executeUpdate(sql);
            System.out.println("Город " + name + " успешно добавлен!");
            return true;
        } catch (SQLException e) {
            System.out.println("ОШИБКА! Город " + name + " не добавлен!");
            System.out.println("    >> " + e.getMessage());
            return false;
        }
    }

    public boolean deleteCountry(int id){
        String sql = "DELETE FROM COUNTRIES WHERE ID_CO = "+ id;
        try {
            int c = statement.executeUpdate(sql);
            if (c > 0){
                System.out.println("Страна с идентификатором " + id +" успешно удалена!");
                return true;
            } else {
                System.out.println("Страна с идентификатором " + id +" не найдена!");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Страна с идентификатором " + id +" не найдена!");
            return false;
        }
    }

    public boolean deleteCity(int id){
        String sql = "DELETE FROM CITIES WHERE ID_CI = "+ id;
        try {
            int c = statement.executeUpdate(sql);
            if (c > 0){
                System.out.println("Город с идентификатором " + id +" успешно удален!");
                return true;
            } else {
                System.out.println("Город с идентификатором " + id +" не найден!");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Город с идентификатором " + id +" не найден!");
            return false;
        }
    }

    public void getCountry(int id){
        String sql = "SELECT * FROM COUNTRIES WHERE ID_CO = " + id;
        try {
            ResultSet rs = statement.executeQuery(sql);
            int count = 0;
            while (rs.next()){
                count++;
                String name = rs.getString("NAME");
                System.out.println("Страна №" + id + " " + name);
            }
            rs.close();

             if (count == 0){
                System.out.println("Страна с идентификатором " + id +" не найдена!");
            }

        } catch (SQLException e) {
            System.out.println("Страна с идентификатором " + id +" не найдена!");
        }
    }

    public void getCity(int id){
        String sql = "SELECT * FROM CITIES WHERE ID_CI = " + id;
        try {
            ResultSet rs = statement.executeQuery(sql);
            int countCities = 0;
            while (rs.next()){
                countCities++;
                int count = rs.getInt("COUNT");
                int capInt = rs.getInt("ISCAPITAL");
                boolean isCapital = false;
                if (capInt == 1){
                    isCapital = true;
                }
                String name = rs.getString("NAME");
                int country = rs.getInt("ID_CO");
                System.out.println("Город №" + id + " " + name + ":");
                System.out.println("     Население: " + count);
                System.out.println("     Столица: " + isCapital);
                System.out.println("     Номер страны: " + country);
            }
            rs.close();
            if (countCities == 0){
                System.out.println("Город с идентификатором " + id +" не найден!");
            }

        } catch (SQLException e) {
            System.out.println("Город с идентификатором " + id +" не найден!");
        }
    }

    public boolean updateCountry(int id, String name){
        String sql = "UPDATE COUNTRIES SET NAME= '" + name + "' WHERE ID_CO = " + id;
        try {
            statement.executeUpdate(sql);
            System.out.println("Cтрана " + name + " успешно обновлена!");
            return true;
        } catch (SQLException e) {
            System.out.println("ОШИБКА! Страна " + name + " не обновлена!");
            System.out.println("    >> " + e.getMessage());
            return false;
        }
    }

    public boolean updateCity(int id, String name, int count, boolean isCapital, int countryID){
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
                System.out.println("Ошибка! Страны номер " + countryID + " не существует!");
                return false;
            }

            statement.executeUpdate(sql);
            System.out.println("Город " + name + " успешно обновлен!");
            return true;
        } catch (SQLException e) {
            System.out.println("ОШИБКА! Город " + name + " не обновлен!");
            System.out.println("    >> " + e.getMessage());
            return false;
        }
    }

    public void showCountries() {
        String sql = "SELECT ID_CO, NAME FROM COUNTRIES";
        try {
            ResultSet rs = statement.executeQuery(sql);
            System.out.println("СПИСОК СТРАН:");
            while (rs.next())
            {
                int id = rs.getInt("ID_CO");
                String name = rs.getString("NAME");
                System.out.println(" >> "+ id + " - " + name);
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println("ОШИБКА при получении списка стран");
            System.out.println(" >> "+e.getMessage());
        }
    }

    public void showCitiesOfCountry(int id){
        String sql = "SELECT * FROM CITIES WHERE ID_CO = " + id;
        try {
            ResultSet rs = statement.executeQuery(sql);
            System.out.println("Города страны №" + id);
            while (rs.next()){
                int count = rs.getInt("COUNT");
                int capInt = rs.getInt("ISCAPITAL");
                boolean isCapital = false;
                if (capInt == 1){
                    isCapital = true;
                }
                String name = rs.getString("NAME");
                int cityId = rs.getInt("ID_CI");
                System.out.println(" >>  Город №" + cityId + " " + name + ":");
                System.out.println("         Население: " + count);
                System.out.println("         Столица: " + isCapital);
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println("Город с идентификатором " + id +" не найден!");
        }
    }

    public static void main(String[] argv) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        Map map = new Map("map", "localhost", 3306);
        map.showCountries();
        System.out.println("-----------------");
        map.getCity(1);
        System.out.println("-----------------");
        map.updateCity(1, "MOSCOW-WOCSOM", 11612934, true, 1);
        System.out.println("-----------------");
        map.getCity(1);
        System.out.println("-----------------");
        map.getCity(4);
        System.out.println("-----------------");
        map.addCity(4, "Kiyv", 3000000, true, 6);
        System.out.println("-----------------");
        map.getCity(4);
        System.out.println("-----------------");
        map.updateCountry(1, "RUSSIA-24");
        System.out.println("-----------------");
        map.showCountries();
        System.out.println("-----------------");
        map.deleteCity(4);
        System.out.println("-----------------");
        map.getCity(4);
        System.out.println("-----------------");
        map.showCitiesOfCountry(1);
        System.out.println("-----------------");
        map.deleteCountry(7);
        System.out.println("-----------------");
        map.showCountries();
        map.stop();
    }
}
