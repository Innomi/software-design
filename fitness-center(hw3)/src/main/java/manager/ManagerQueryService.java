package manager;

import manager.query.GetUserQuery;
import manager.query.ManagerQuery;
import model.User;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ManagerQueryService {
    public ManagerQueryService(ManagerQueryDao queryDao) {
        this.queryDao = queryDao;
    }

    public CompletableFuture<String> process(ManagerQuery query) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (query instanceof GetUserQuery) {
                    Optional<User> user = queryDao.getUser(((GetUserQuery) query).getUserId()).join();
                    if (user.isPresent()) {
                        return user.toString();
                    } else {
                        return "No user found";
                    }
                }
                return "Unknown query";
            } catch (Exception e) {
                return "Error occured while processing: " + e.getMessage();
            }
        });
    }

    private ManagerQueryDao queryDao;
}