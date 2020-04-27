package manager;

import manager.command.ManagerCommand;
import manager.command.RegisterUserCommand;
import manager.command.UpdateSubscriptionCommand;

import java.util.concurrent.CompletableFuture;

public class ManagerCommandService {
    public ManagerCommandService(ManagerCommandDao commandDao) {
        this.commandDao = commandDao;
    }

    public CompletableFuture<String> process(ManagerCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (command instanceof RegisterUserCommand) {
                    int id = commandDao.registerUser(((RegisterUserCommand) command).getName()).join();
                    return "User " + ((RegisterUserCommand) command).getName() + " has id = " + String.valueOf(id);
                } else if (command instanceof UpdateSubscriptionCommand) {
                    commandDao.updateSubscription(((UpdateSubscriptionCommand) command).getUserId(), ((UpdateSubscriptionCommand) command).getEndTime()).join();
                    return "Subscription updated";
                }
                return "Unknown command";
            } catch (Exception e) {
                return "Error while processing: " + e.getMessage();
            }
        });
    }

    private ManagerCommandDao commandDao;
}