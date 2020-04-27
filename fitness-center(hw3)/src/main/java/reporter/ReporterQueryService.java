package reporter;

import model.UserReport;
import org.joda.time.Period;
import reporter.query.GetUserReportQuery;
import reporter.query.ReporterQuery;

import java.util.Optional;

public class ReporterQueryService {
    public ReporterQueryService(ReporterQueryDao queryDao) {
        this.queryDao = queryDao;
    }

    public String process(ReporterQuery query) {
        try {
            if (query instanceof GetUserReportQuery) {
                Optional<UserReport> report = queryDao.getUserReport(((GetUserReportQuery) query).getUserId());
                if (report.isPresent()) {
                    Period time = report.get().getTotalTimeSpent().normalizedStandard();
                    int avgVisit = time.toStandardMinutes().dividedBy(report.get().getTotalVisits()).getMinutes();
                    return "Total time spent: " + time.toString() + "\n" +
                            "Total visits: " + report.get().getTotalVisits() + "\n" +
                            "Avg viit: " + String.valueOf(avgVisit) + "min\n";
                } else {
                    return "No user found";
                }
            }
            return "Unknown query";
        } catch (Exception e) {
            return "Error occured while processing: " + e.getMessage();
        }
    }

    private ReporterQueryDao queryDao;
}