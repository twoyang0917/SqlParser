package com.github.twoyang0917;

import com.github.twoyang0917.parser.PrestoSqlLexer;
import com.github.twoyang0917.parser.PrestoSqlParser;
import org.antlr.v4.runtime.*;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PrestoSqlLineageParserTest {

    private final PrestoSqlLineageParser lineageParser = new PrestoSqlLineageParser();

    @Test
    public void testNamedQuery() throws Exception {
        String sql = "SELECT *\n" +
                "FROM \"test\".\"test_table2\" t\n" +
                "WHERE t.\"id\" = 1\n" +
                "WITH DATA";
        CharStream charStream = CharStreams.fromString(sql);
        PrestoSqlLexer lexer = new PrestoSqlLexer(charStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PrestoSqlParser parser = new PrestoSqlParser(tokens);
        lineageParser.visit(parser.statement());

        Set<String> namedQuerySet = lineageParser.getNamedQuerySet();
        assertEquals(0, namedQuerySet.size());
    }

    @Test
    public void testTableName() throws Exception {
        String sql = "CREATE TABLE \"test\".\"test_table\" AS\n" +
                "SELECT *\n" +
                "FROM \"test\".\"test_table2\" t\n" +
                "WHERE t.\"id\" = 1\n" +
                "WITH DATA";
        CharStream charStream = CharStreams.fromString(sql);
        PrestoSqlLexer lexer = new PrestoSqlLexer(charStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PrestoSqlParser parser = new PrestoSqlParser(tokens);
        lineageParser.visit(parser.statement());

        Set<String> tables = lineageParser.getTableSet();
        assertEquals(1, tables.size());
        assertTrue(tables.contains("\"test\".\"test_table2\""));
    }

    @Test
    public void testNamedQueryAndTableName() throws Exception {
        String sql = "SELECT *\n" +
                "FROM (SELECT * FROM \"test\".\"test_table2\") t\n" +
                "WHERE t.\"id\" = 1\n" +
                "WITH DATA";
        CharStream charStream = CharStreams.fromString(sql);
        PrestoSqlLexer lexer = new PrestoSqlLexer(charStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PrestoSqlParser parser = new PrestoSqlParser(tokens);
        lineageParser.visit(parser.statement());

        Set<String> namedQuerySet = lineageParser.getNamedQuerySet();
        assertEquals(1, namedQuerySet.size());
        assertTrue(namedQuerySet.contains("test.test_table2"));

        Set<String> tables = lineageParser.getTableSet();
        assertEquals(1, tables.size());
        assertTrue(tables.contains("test.test_table"));
    }

    @Test
    public void testEmptyQuery() throws Exception {
        String sql = "";
        CharStream charStream = CharStreams.fromString(sql);
        PrestoSqlLexer lexer = new PrestoSqlLexer(charStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PrestoSqlParser parser = new PrestoSqlParser(tokens);
        lineageParser.visit(parser.statement());

        Set<String> namedQuerySet = lineageParser.getNamedQuerySet();
        assertEquals(0, namedQuerySet.size());

        Set<String> tables = lineageParser.getTableSet();
        assertEquals(0, tables.size());
    }
}
