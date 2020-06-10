public class City {
    public int code;
    public String name;
    public boolean isCapital;
    public int countCitizen;
    public Country country;

    public City(int code, String name, boolean isCapital, int count, Country country){
        this.code = code;
        this.name = name;
        this.isCapital = isCapital;
        this.countCitizen = count;
        this.country = country;
    }
}
