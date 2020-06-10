package app.servlets;

import app.entities.City;
import app.entities.Country;
import app.entities.DBConnection;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "MapServlet", urlPatterns = "/cities")
public class MapServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<City> cities = new ArrayList<>();
            List<Country> countries = new ArrayList<>();

            Connection conn = DBConnection.initDB();
            Statement statement = conn.createStatement();

            String sql = "SELECT ID_CO, NAME FROM COUNTRIES";
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next())
            {
                int id = rs.getInt("ID_CO");
                String name = rs.getString("NAME");
                Country country = new Country(id, name);
                countries.add(country);
            }
            rs.close();

            sql = "SELECT * FROM CITIES";
            rs = statement.executeQuery(sql);
            while (rs.next()){
                int count = rs.getInt("COUNT");
                int capInt = rs.getInt("ISCAPITAL");
                boolean isCapital = false;
                if (capInt == 1){
                    isCapital = true;
                }
                String name = rs.getString("NAME");
                int cityId = rs.getInt("ID_CI");
                int countryID = rs.getInt("ID_CO");
                City city = null;
                for(Country country : countries){
                    if (country.code == countryID){
                        city = new City(cityId, name, isCapital, count, country);
                    }
                }
                if (city != null){
                    cities.add(city);
                }
            }
            rs.close();

            request.setAttribute("cities", cities);
            request.setAttribute("countries", countries);

            conn.close();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        RequestDispatcher requestDispatcher = request.getRequestDispatcher("views/cities.jsp");
        requestDispatcher.forward(request, response);
    }
}
