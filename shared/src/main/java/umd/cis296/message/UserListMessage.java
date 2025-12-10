package umd.cis296.message;

import java.util.Collection;
import java.util.Vector;

import umd.cis296.Message;
import umd.cis296.objects.User;

public class UserListMessage extends Message {
    private Vector<User> users;

    public UserListMessage(Collection<User> users) {
        this.users = new Vector<>(users);
    }

    public Vector<User> getUsers() {
        return users;
    }

    public void setUsers(Vector<User> users) {
        this.users = users;
    }
    
}
