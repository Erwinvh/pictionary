package comms;

import comms.GameUpdates.ChatUpdate;
import comms.GameUpdates.StateUpdate;
import comms.GameUpdates.TimerUpdate;
import comms.GameUpdates.UserUpdate;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static comms.Server.LEAVE_MESSAGE;

class Clients {

    private HashMap<User, ObjectOutputStream> connectedUsers = new LinkedHashMap<>();
    private HashMap<User, StateUpdate.stateType> stateMap = new HashMap<>();

    Clients() {
    }

    void addClient(User user, ObjectOutputStream objectOutputStream) {
        // First notify the new user about all other users
        notifyNewUser(objectOutputStream);

        // Then add our new user with their output stream to our list
        this.connectedUsers.put(user, objectOutputStream);

        // Notify all other users this player has joined
        sendToAllClients(new UserUpdate(user, false));
        sendToAllClients(new ChatUpdate(user, Server.JOIN_MESSAGE));
    }

    private void notifyNewUser(ObjectOutputStream objectOutputStream) {
        // Each user
        connectedUsers.keySet().forEach(userInstance -> {
            try {
                objectOutputStream.writeObject(new UserUpdate(userInstance, false));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    void removeClient(User user) {
        if (!this.connectedUsers.containsKey(user))
            return;

        this.connectedUsers.remove(user);

        sendToAllClients(new ChatUpdate(user, LEAVE_MESSAGE));
        sendToAllClients(new UserUpdate(user, true));
    }

    void sendToAllClients(Object obj) {
        if (!(obj instanceof TimerUpdate))
            System.out.println("Sending \"" + obj.toString() + "\" to " + connectedUsers.size() + " clients...");

        this.connectedUsers.values().forEach(objectOutputStream -> {
            try {
                objectOutputStream.writeObject(obj);
            } catch (IOException e) {
                System.out.println("Something went wrong whilst trying to send " + obj.toString() + " to " + objectOutputStream.toString());
                e.printStackTrace();
            }
        });
    }

    void sendToSpecificClient(Object object, User user) {
        try {
            this.connectedUsers.get(user).writeObject(object);
        } catch (IOException e) {
            System.out.println("Something went wrong whilst trying to send " + object.toString() + " to " + user.getName());
            e.printStackTrace();
        }
    }

    void adjustState(StateUpdate stateUpdate) {
        User user = stateUpdate.getUser();
        if (!stateMap.containsKey(user)) {
            stateMap.put(user, stateUpdate.getState());
        } else {
            stateMap.replace(user, stateUpdate.getState());
        }
    }

    HashMap<User, ObjectOutputStream> getConnectedUsers() {
        return connectedUsers;
    }

    HashMap<User, StateUpdate.stateType> getStateMap() {
        return stateMap;
    }
}