import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

public class Main {
    public static void main(String[] argv) throws IOException, SAXException, TransformerException, ParserConfigurationException {
        Worldmap wm = new Worldmap("countries.xml");
        wm.printCountries();
        wm.addCountry(4, "Египер");
        wm.addCity(9, "Каир", true, 3000000, 4);
        wm.printCountries();
        wm.deleteCity(2);
        wm.printCitiesOfCountry(1);
        System.out.println("Количество стран: " + wm.countCountries());
        wm.deleteCountry(3);
        wm.printCountries();
        System.out.println("Количество стран: " + wm.countCountries());
    }
}
