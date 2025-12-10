package umd.cis296.message;

import umd.cis296.Message;
import umd.cis296.objects.User;

public class UserLeaveMessage extends Message {
    private User user;

    public UserLeaveMessage(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
