package manager;

import common.GameInfo;
import common.Request;
import common.RequestType;
import common.Response;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ManagerConsoleApp {
    public static void main(String[] args) throws Exception {
        GameInfo game = new GameInfo(
                "LuckyWheel",
                "provider1",
                3,
                15,
                "/images/lucky.png",
                1.0,
                10.0,
                "low",
                "secretKey123"
        );

        Request request = new Request(RequestType.ADD_GAME, game);

        try (Socket socket = new Socket("localhost", 5000);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.writeObject(request);
            out.flush();

            Response response = (Response) in.readObject();
            System.out.println("Response: " + response.getMessage());
        }
    }
}