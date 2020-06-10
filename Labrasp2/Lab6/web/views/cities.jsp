<%@ page import="java.util.List" %>
<%@ page import="app.entities.City" %>
<%@ page import="app.entities.Country" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Lab15</title>
    <link rel="stylesheet" href="./res/cities.css">
    <link rel="stylesheet" href="./res/style.css">
</head>
<body>
<h2 class="ttl">List of Cities</h2>

<%
    List<City> cities = (List<City>) request.getAttribute("cities");
    List<Country> countries = (List<Country>) request.getAttribute("countries");

    if (countries != null){
        out.println("<div class='list'>");
        for (Country country : countries){
            out.println("<p class='country'>Country " + country.name + "</p>");
            out.println("<div>");

            for (City city : cities) {
                if (city.country.code == country.code){
                    out.println("<p class='city'> -- City " + city.name + "</p>");
                }
            }
            out.println("</div>");
        }
        out.println("</div>");
    }
%>

</body>
</html>
