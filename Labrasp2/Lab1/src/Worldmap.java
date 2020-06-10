import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Worldmap {
    private ArrayList<Country> countries;
    private ArrayList<City> cities;
    private String fileName;

    public Worldmap(String fileName){
        countries = new ArrayList<>();
        cities = new ArrayList<>();
        this.fileName = fileName;
    }

    public void saveToFile(String filename) throws ParserConfigurationException, TransformerException, IOException, SAXException {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();

        Element root = doc.createElement("map");
        doc.appendChild(root);

        for (int i = 0; i < countries.size(); i++) {
            Element country = doc.createElement("country");
            country.setAttribute("id", String.valueOf(countries.get(i).code));
            country.setAttribute("name", countries.get(i).name);
            root.appendChild(country);
            for (int j = 0; j < cities.size(); j++) {
                if (cities.get(j).country.code == countries.get(i).code){
                    Element city = doc.createElement("city");
                    city.setAttribute("id", String.valueOf(cities.get(j).code));
                    city.setAttribute("name", cities.get(j).name);
                    if (cities.get(j).isCapital){
                        city.setAttribute("isCapital", "1");
                    } else {
                        city.setAttribute("isCapital", "0");
                    }
                    city.setAttribute("count", String.valueOf(cities.get(j).countCitizen));
                    country.appendChild(city);
                }
            }
        }

        Source domSourse = new DOMSource(doc);
        Result fileResult = new StreamResult(new File(filename));
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "WINDOWS-1251");
        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "countries.dtd");
        transformer.transform(domSourse, fileResult);
    }

    public void loadFromFile(String filename) throws IOException, SAXException {
        DocumentBuilderFactory dbf;
        DocumentBuilder db = null;
        countries = new ArrayList<>();
        cities = new ArrayList<>();

        try {
            dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(true);
            db = dbf.newDocumentBuilder();
            db.setErrorHandler(new SimpleErrorHandler());
        } catch (ParserConfigurationException e){
            e.printStackTrace();
        }

        Document doc;

        assert db != null;
        doc = db.parse(new File("countries.xml"));

        Element root = doc.getDocumentElement();
        if (root.getTagName().equals("map")){
            NodeList listCountries = root.getElementsByTagName("country");
            for (int i = 0; i < listCountries.getLength(); i++) {
                Element country = (Element) listCountries.item(i);
                int countryCode = Integer.parseInt(country.getAttribute("id"));
                String countryName = country.getAttribute("name");
                Country cntry = new Country(countryCode, countryName);
                countries.add(cntry);
                NodeList listCities = country.getElementsByTagName("city");
                for (int j = 0; j < listCities.getLength(); j++) {
                    Element city = (Element) listCities.item(j);
                    String cityName = city.getAttribute("name");
                    int cityCode = Integer.parseInt(city.getAttribute("id"));
                    int cityCap = Integer.parseInt(city.getAttribute("isCapital"));
                    boolean isCap = false;
                    if (cityCap == 1){
                        isCap = true;
                    }
                    int cityCount = Integer.parseInt(city.getAttribute("count"));
                    City ct = new City(cityCode, cityName, isCap, cityCount, cntry);
                    cities.add(ct);
                }
            }
        }
    }

    public void addCountry(int code, String name) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        loadFromFile(fileName);

        boolean canAdd = true;
        for (Country value : countries) {
            if (value.code == code) {
                canAdd = false;
                break;
            }
        }

        if (canAdd){
            Country country = new Country(code, name);
            countries.add(country);
            System.out.println("Страна успешно добавлена");
        } else {
            System.out.println("Невозможно добавить страну, так как страна с кодом " + code + " уже существует");
            return;
        }

        saveToFile(fileName);
    }

    public Country getCountry(int code) throws IOException, SAXException {
        loadFromFile(fileName);

        for (Country country : countries) {
            if (country.code == code){
                return country;
            }
        }

        System.out.println("Страна с номером " + code + " не существует");
        return null;
    }

    public Country getCountryInd(int index) throws IOException, SAXException {
        loadFromFile(fileName);

        if (index < countries.size()){
            for (int i = 0; i < countries.size(); i++) {
                if (i == index){
                    return countries.get(i);
                }
            }
        }

        System.out.println("Индекс " + index + " больше чем размер массива со старанами");
        return null;
    }

    public int countCountries() throws IOException, SAXException {
        loadFromFile(fileName);
        return countries.size();
    }

    public void deleteCountry(int code) throws IOException, SAXException, TransformerException, ParserConfigurationException {
        loadFromFile(fileName);

        boolean deleted = false;
        ArrayList<City> citiesToDelete = new ArrayList<>();
        for (Country country : countries){
            if (country.code == code){
                for (City city : cities){
                    if (city.country.code == code){
                        citiesToDelete.add(city);
                    }
                }

                for (City city : citiesToDelete){
                    cities.remove(city);
                }
                countries.remove(country);
                deleted = true;
                break;
            }
        }

        if (!deleted){
            System.out.println("Не удалось удалить страну с кодом " + code + " и ее города, так как страны с кодом " + code + " не существует");
            return;
        } else {
            System.out.println("Страна с кодом " + code + " успешно удалена");
        }

        saveToFile(fileName);
    }

    public void addCity(int code, String name, boolean isCapital, int count, int countryCode) throws IOException, SAXException, TransformerException, ParserConfigurationException {
        loadFromFile(fileName);

        for (City city : cities){
            if (city.code == code){
                System.out.println("Город с кодом " + code + " уже существует");
                return;
            }
        }


        Country cityCountry = null;
        boolean canAdd = false;
        for (Country value : countries) {
            if (value.code == countryCode) {
                canAdd = true;
                cityCountry = value;
                break;
            }
        }

        if (canAdd){
            City city = new City(code, name, isCapital, count, cityCountry);
            cities.add(city);
            System.out.println("Город успешно добавлен");
        } else {
            System.out.println("Невозможно добавить страну, так как страна с кодом " + code + " уже существует");
            return;
        }

        saveToFile(fileName);
    }

    public void deleteCity(int code) throws IOException, SAXException, TransformerException, ParserConfigurationException {
        loadFromFile(fileName);

        boolean deleted = false;
        for (City city : cities){
            if (city.code == code){
                cities.remove(city);
                deleted = true;
                break;
            }
        }

        if (!deleted){
            System.out.println("Не удалось удалить город с кодом " + code + ", так как города с кодом " + code + " не существует");
            return;
        } else {
            System.out.println("Город с кодом " + code + " успешно удален");
        }

        saveToFile(fileName);
    }

    public void printCountries() throws IOException, SAXException {
        loadFromFile(fileName);
        System.out.println("Страны:");
        for (Country country : countries){
            System.out.println("     " + country.name);
        }
    }

    public void printCitiesOfCountry(int code) throws IOException, SAXException {
        loadFromFile(fileName);
        System.out.println("Страна " + code + " с ее городами");
        for (Country country : countries){
            if (country.code == code){
                System.out.println(country.name + ":");
                for (City city : cities){
                    if (city.country.code == code){
                        System.out.println("         " + city.name);
                    }
                }
                break;
            }
        }
    }
}

class SimpleErrorHandler implements ErrorHandler {

    @Override
    public void warning(SAXParseException exception) throws SAXException {
        System.out.println("Строка " + exception.getLineNumber() + ":");
        System.out.println(exception.getMessage());
    }

    @Override
    public void error(SAXParseException exception) throws SAXException {
        System.out.println("Строка " + exception.getLineNumber() + ":");
        System.out.println(exception.getMessage());
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        System.out.println("Строка " + exception.getLineNumber() + ":");
        System.out.println(exception.getMessage());
    }
}
