package com.vkpapps.soundbooster.connection;

import com.vkpapps.soundbooster.handler.SignalHandler;
import com.vkpapps.soundbooster.model.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerHelper implements Runnable {
    private SignalHandler signalHandler;
    private ArrayList<CommandHelperRunnable> commandHelperRunnables;
    private User user;

    public ServerHelper(SignalHandler signalHandler, User user) {
        this.signalHandler = signalHandler;
        this.user = user;
        commandHelperRunnables = new ArrayList<>();
    }

    @Override
    public void run() {
        while (true) {
            try {
                ServerSocket serverSocket = new ServerSocket(1203);
                Socket socket = serverSocket.accept();
                CommandHelperRunnable commandHelper = new CommandHelperRunnable(socket, signalHandler, user);
                commandHelperRunnables.add(commandHelper);
                new Thread(commandHelper).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendCommand(String command) {
        for (CommandHelperRunnable c : commandHelperRunnables) {
            c.write(command);
        }
    }

    public void sendCommandToOnly(String command, String clientId) {
        for (CommandHelperRunnable c : commandHelperRunnables) {
            if (c.id.equals(clientId)) c.write(command);
        }
    }

    public void setUser(User tmp, String id) {
        for (CommandHelperRunnable c : commandHelperRunnables) {
            if (c.id.equals(id)) {
                c.user = tmp;
            }
        }
    }

    public ArrayList<CommandHelperRunnable> getCommandHelperRunnables() {
        return commandHelperRunnables;
    }
}
