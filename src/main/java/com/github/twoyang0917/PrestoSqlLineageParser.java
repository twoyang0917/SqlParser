package com.github.twoyang0917;


import com.github.twoyang0917.parser.PrestoSqlBaseVisitor;
import com.github.twoyang0917.parser.PrestoSqlParser;

import java.util.HashSet;
import java.util.Set;


public class PrestoSqlLineageParser extends PrestoSqlBaseVisitor<Object> {
    private final Set<String> namedQuerySet = new HashSet<>();
    private final Set<String> tableSet = new HashSet<>();

    @Override
    public Object visitNamedQuery(PrestoSqlParser.NamedQueryContext ctx) {
        String namedQuery = ctx.name.getText();
        if (namedQuery != null && !namedQuery.isEmpty()) {
            namedQuerySet.add(namedQuery);
        }
        return super.visitNamedQuery(ctx);
    }

    @Override
    public Object visitTableName(PrestoSqlParser.TableNameContext ctx) {
        String tableName = ctx.qualifiedName().getText();
        if (tableName != null && !tableName.isEmpty()) {
            tableSet.add(tableName);
        }
        return super.visitTableName(ctx);
    }

    public Set<String> getNamedQuerySet() {
        return namedQuerySet;
    }

    public Set<String> getTableSet() {
        tableSet.removeAll(namedQuerySet);
        return tableSet;
    }
}