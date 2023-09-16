package store.juin.api.common.config;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.event.JdbcEventListener;
import com.p6spy.engine.spy.P6SpyOptions;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.sql.SQLException;

@Component
public class P6spyFormatter extends JdbcEventListener implements MessageFormattingStrategy {
    private static final String CREATE = "create";
    private static final String ALTER = "alter";
    private static final String COMMENT = "comment";

    private static final String SELECT = "select";
    private static final String INSERT = "insert";
    private static final String UPDATE = "update";
    private static final String DELETE = "delete";

    @Override
    public void onAfterGetConnection(ConnectionInformation connectionInformation, SQLException e) {
        P6SpyOptions.getActiveInstance().setLogMessageFormat(getClass().getName());
    }

    @Override
    public String formatMessage
            (int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {

        StringBuilder sb = new StringBuilder();
        sb.append(category).append(" ").append(elapsed).append("ms");

        if (StringUtils.hasText(sql)) {
            sb.append(highlight(format(sql)));
        }

        return sb.toString();
    }

    private String format(String sql) {
        if (isDDL(sql)) {
            return FormatStyle.DDL.getFormatter().format(sql);
        } else if (isBasic(sql)) {
            return FormatStyle.BASIC.getFormatter().format(sql);
        }

        return sql;
    }

    private String highlight(String sql) {
        return FormatStyle.HIGHLIGHT.getFormatter().format(sql);
    }

    private boolean isDDL(String sql) {
        return sql.startsWith(CREATE)
                || sql.startsWith(ALTER)
                || sql.startsWith(COMMENT);
    }

    private boolean isBasic(String sql) {
        return sql.startsWith(SELECT)
                || sql.startsWith(INSERT)
                || sql.startsWith(UPDATE)
                || sql.startsWith(DELETE);
    }
}