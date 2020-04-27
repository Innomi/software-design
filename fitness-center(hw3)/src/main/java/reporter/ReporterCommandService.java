package reporter;

import reporter.command.AddVisitCommand;
import reporter.command.ReporterCommand;

public class ReporterCommandService {
    public ReporterCommandService(ReporterCommandDao commandDao) {
        this.commandDao = commandDao;
    }

    public String process(ReporterCommand command) {
        try {
            if (command instanceof AddVisitCommand) {
                AddVisitCommand castedCommand = (AddVisitCommand) command;
                commandDao.addVisit(castedCommand.getUserId(), castedCommand.getStartTime(), castedCommand.getEndTime(), castedCommand.getEventId());
                return "OK";
            }
            return "Unknown command";
        } catch (Exception e) {
            return "Error occurred while executing: " + e.getMessage();
        }
    }

    private ReporterCommandDao commandDao;
}