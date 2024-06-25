package project.Server;

import project.util.Food;
import project.util.NetworkUtil;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class ServerReadThread implements Runnable {
    NetworkUtil networkUtil;
    Map<Integer, NetworkUtil> idToNetworkUtil;
    Thread t;

    public ServerReadThread(NetworkUtil networkUtil, Map<Integer, NetworkUtil> idToNetworkUtil) {
        this.networkUtil = networkUtil;
        this.idToNetworkUtil = idToNetworkUtil;
        t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        while (true){
            try {
                List<Food> temp = (List<Food>) networkUtil.read();
                for(var x: temp){
                    NetworkUtil networkUtilTemp = idToNetworkUtil.get(x.getRestaurantId());
                    if(networkUtilTemp== null){
                        Socket socket = new Socket("127.0.0.1", 55555);
                        NetworkUtil networkUtil1 = new NetworkUtil(socket);
                        networkUtil1.write(x);
                    }else
                        networkUtilTemp.write(x);
                }
            } catch (IOException | ClassNotFoundException ignored) {
            }
        }
    }
}
