import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;

public class MapImpl extends UnicastRemoteObject implements Map {
    private Connection conn;
    private Statement statement;

    protected MapImpl(String DBName, String ip, int port) throws RemoteException, ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {
        super();
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        String url = "jdbc:mysql://" + ip + ":" + port + "/" + DBName;
        conn = DriverManager.getConnection(url, "root", "admin");
        statement = conn.createStatement();
    }

    public void stop() throws SQLException {
        conn.close();
    }

    @Override
    public String addCountry(int id, String name) throws RemoteException {
        String sql = "INSERT INTO COUNTRIES (ID_CO, NAME) " + "VALUES (" + id + ", '" + name + "')";
        try {
            statement.executeUpdate(sql);
            return "Cтрана " + name + " успешно добавлена!";
        } catch (SQLException e) {
            return "ОШИБКА! Страна " + name + " не добавлена!\n     >> " + e.getMessage();
        }
    }

    @Override
    public String addCity(int id, String name, int count, boolean isCapital, int countryID) throws RemoteException {
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
            return "ОШИБКА! Город " + name + " не добавлен!\n     >> " + e.getMessage();
        }
    }

    @Override
    public String deleteCountry(int id) throws RemoteException {
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

    @Override
    public String deleteCity(int id) throws RemoteException {
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

    @Override
    public String updateCity(int id, String name, int count, boolean isCapital, int countryID) throws RemoteException {
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
            return "ОШИБКА! Город " + name + " не обновлен!\n     >> " + e.getMessage();
        }
    }

    @Override
    public String getCitiesOfCountry(int id) throws RemoteException {
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
                result += "\n >>  Город №" + cityId + " " + name + ":\n        Население: " + count + "\n         Столица: " + isCapital;
            }
            rs.close();
            return result;
        } catch (SQLException e) {
            return "Страна с идентификатором " + id +" не найден!";
        }
    }

    @Override
    public String countCitiesOfCountry(int id) throws RemoteException {
        String sql = "SELECT * FROM CITIES WHERE ID_CO = " + id;
        String result = "";
        try {
            ResultSet rs = statement.executeQuery(sql);
            result = "Количество городов страны №" + id + ": ";
            int count = 0;
            while (rs.next()){
               count++;
            }
            rs.close();
            result += count;
            return result;
        } catch (SQLException e) {
            return "Страна с идентификатором " + id +" не найден!";
        }
    }

    @Override
    public String getCountries() throws RemoteException {
        String sql = "SELECT ID_CO, NAME FROM COUNTRIES";
        String result = "";
        try {
            ResultSet rs = statement.executeQuery(sql);
            result = "СПИСОК СТРАН:";
            while (rs.next())
            {
                int id = rs.getInt("ID_CO");
                String name = rs.getString("NAME");
                result += "\n >> "+ id + " - " + name;
            }
            rs.close();
            return result;
        } catch (SQLException e) {
            return "ОШИБКА при получении списка стран\n  >> " +  e.getMessage();
        }
    }
}
