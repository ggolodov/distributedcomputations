import com.ibm.mq.*;

import java.io.IOException;

public class Client {
    private MQQueueManager QM;
    private MQQueue Q1;
    private MQQueue Q2;

    public Client(String QMName, String IP, int port, String channel, String Q1_name, String Q2_name) {
        MQEnvironment.hostname = IP;
        MQEnvironment.port = port;
        MQEnvironment.channel = channel;

        try {
            QM = new MQQueueManager(QMName);
            Q1 = QM.accessQueue(Q1_name, MQC.MQOO_OUTPUT);
            Q2 = QM.accessQueue(Q2_name, MQC.MQOO_INPUT_EXCLUSIVE);
        } catch (MQException e){
            System.out.println(e.getMessage());
        }

    }

    public void addCountry(int id, String name) throws IOException, MQException {
        MQMessage query = new MQMessage();
        query.writeInt(1);
        query.writeInt(id);
        query.writeString(name);
        Q1.put(query);
    }

    public void addCity(int id, String name, int count, boolean isCapital, int idCountry) throws IOException, MQException {
        MQMessage query = new MQMessage();
        query.writeInt(2);
        query.writeInt(id);
        query.writeInt(count);
        query.writeBoolean(isCapital);
        query.writeInt(idCountry);
        query.writeString(name);

        Q1.put(query);
    }

    public void deleteCity(int id) throws IOException, MQException {
        MQMessage query = new MQMessage();
        query.writeInt(3);
        query.writeInt(id);
        Q1.put(query);
    }

    public void getCities() throws IOException, MQException {
        MQMessage query = new MQMessage();
        query.writeInt(4);
        Q1.put(query);
    }

    public boolean printResult(){
        try{
            MQMessage response = new MQMessage();
            Q2.get(response);
            int operation = response.readInt();
            String result = "";
            if (operation == 4){
                int count = response.readInt();
                if (count==0){
                    result += response.readLine();
                }
                for (int i = 0; i < count; i++) {
                    result += "\n" + response.readLine();
                }
                System.out.println("Список городов: ");
                System.out.println(result);
            } else {
                result = response.readLine();
                System.out.println(result);
            }
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public void disconnect() throws MQException {
        Q1.close();
        Q2.close();
        QM.disconnect();
    }

    public static void main1() throws MQException, IOException {
        Client client = new Client("QM", "localhost", 1414, "SYSTEM.DEF.SVRCONN", "SVR.Q", "CL.Q");
        int count = 0;
        client.addCountry(23, "New Zealand");
        count++;
        client.getCities();
        count++;
        client.deleteCity(13);
        count++;
        client.getCities();
        count++;
        client.addCity(13, "Auckland", 123456, true, 23);
        count++;
        client.deleteCity(8);
        count++;
        client.getCities();
        count++;
        System.out.println("Отправлено " + count + " запросов");
        client.disconnect();
    }

    public static void main2() throws MQException {
        Client client = new Client("QM", "localhost", 1414, "SYSTEM.DEF.SVRCONN", "SVR.Q", "CL.Q");
        while (client.printResult());
    }

    public static void main(String[] argv) throws MQException, IOException {
        //main1();
        main2();
    }
}
