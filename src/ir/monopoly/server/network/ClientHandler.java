package ir.monopoly.server.network;

import ir.monopoly.server.game.GameState;
import ir.monopoly.server.player.PlayerStatus;
import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread {
    private final Socket socket;
    private final int playerId;
    private final GameServer server;
    private PrintWriter out;
    private BufferedReader in;

    public ClientHandler(Socket socket, int playerId, GameServer server) {
        this.socket = socket;
        this.playerId = playerId;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            sendMessage("{\"type\":\"CONNECTED\",\"playerId\":" + playerId + ",\"message\":\"Welcome to Monopoly!\"}");

            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("Command from Player " + playerId + ": " + line);

                String response = server.getGameController().handleCommand(line.toUpperCase(), playerId, "");

                String jsonResponse = formatToJson(response, line.toUpperCase());
                sendMessage(jsonResponse);

                server.broadcast(formatTurnUpdate());
            }
        } catch (IOException e) {
            handleDisconnect();
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {}
        }
    }

    private void handleDisconnect() {
        System.out.println("Player " + playerId + " disconnected.");
        GameState gs = server.getGameState();
        gs.getPlayerById(playerId).setStatus(PlayerStatus.BANKRUPT);

        server.broadcast("{\"type\":\"PLAYER_DISCONNECTED\",\"playerId\":" + playerId + ",\"message\":\"Player " + playerId + " disconnected. Turn skipped.\"}");

        if (gs.getTurnManager().getCurrentPlayer().getPlayerId() == playerId) {
            gs.getTurnManager().passTurn();
        }
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    private String formatToJson(String response, String command) {
        if (command.equals("ROLL")) {
            return "{\"type\":\"ROLL_UPDATE\",\"playerId\":" + playerId + ",\"dice\":8,\"currentPosition\":14,\"message\":\"" + response + "\"}";
        }
        if (command.equals("BUY")) {
            return "{\"type\":\"BUY_UPDATE\",\"playerId\":" + playerId + ",\"message\":\"" + response + "\"}";
        }
        if (command.equals("GET_STATS")) {
            String wealth = server.getGameState().getWealthReport().replace("\n", "\\n");
            return "{\"type\":\"STATS_UPDATE\",\"wealthList\":\"" + wealth + "\",\"message\":\"Player wealth statistics sent\"}";
        }
        if (command.equals("END_TURN")) {
            return "{\"type\":\"TURN_END\",\"playerId\":" + playerId + ",\"message\":\"Turn passed to next player\"}";
        }
        return "{\"type\":\"GENERAL\",\"playerId\":" + playerId + ",\"message\":\"" + response + "\"}";
    }

    private String formatTurnUpdate() {
        int currentPlayer = server.getGameState().getTurnManager().getCurrentPlayer().getPlayerId();
        return "{\"type\":\"TURN_UPDATE\",\"currentPlayer\":" + currentPlayer + "}";
    }
}