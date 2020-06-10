import com.ibm.mq.*;
import javafx.util.Pair;

public class Server {
    private MQQueueManager QM;
    private MQQueue Q1;
    private MQQueue Q2;

    public void start(String QMName, String IP, int port, String channel, String Q1_name, String Q2_name) throws MQException {
        MQEnvironment.hostname = IP;
        MQEnvironment.port = port;
        MQEnvironment.channel = channel;

        QM = new MQQueueManager(QMName);
        Q1 = QM.accessQueue(Q1_name, MQC.MQOO_INPUT_EXCLUSIVE);
        Q2 = QM.accessQueue(Q2_name, MQC.MQOO_OUTPUT);

        int i = 0;
        while (processQuery()) i++;
        Q1.close();
        Q2.close();
        QM.disconnect();
        System.out.println("Обработано " + i + " запросов");
    }

    public boolean processQuery(){
        try{
            MQGetMessageOptions gmo = new MQGetMessageOptions();
            gmo.options = MQC.MQGMO_WAIT;
            gmo.waitInterval = 3000;

            Map map = new Map("map", "localhost", 3306);
            MQMessage query = new MQMessage();
            Q1.get(query, gmo);
            MQMessage response = new MQMessage();
            int operation = query.readInt();
            if (operation == 1){
                int id = query.readInt();
                String name = query.readLine();
                String result = map.addCountry(id, name);
                response.writeInt(operation);
                response.writeString(result);
            } else if (operation == 2){
                int id = query.readInt();
                int count = query.readInt();
                boolean isCapital = query.readBoolean();
                int idCountry = query.readInt();
                String name = query.readLine();
                String result = map.addCity(id, name, count, isCapital, idCountry);
                response.writeInt(operation);
                response.writeString(result);
            } else if (operation == 3){
                int id = query.readInt();
                String result = map.deleteCity(id);
                response.writeInt(operation);
                response.writeString(result);
            } else if (operation == 4){
                Pair<Integer, String> pair = map.getCities();
                int count = pair.getKey();
                String result = pair.getValue();
                response.writeInt(operation);
                response.writeInt(count);
                response.writeString(result);
            } else {
                response.writeInt(operation);
                response.writeChars("Wrong operation");
            }

            Q2.put(response);
            return true;
        } catch (Exception e){
            //e.printStackTrace();
            return false;

        }
    }

    public static void main(String[] argv) throws MQException {
        new Server().start("QM", "localhost", 1414, "SYSTEM.DEF.SVRCONN", "SVR.Q", "CL.Q");
    }
}
