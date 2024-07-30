package com.github.twoyang0917;

import com.github.twoyang0917.parser.PrestoSqlLexer;
import com.github.twoyang0917.parser.PrestoSqlParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.atn.PredictionMode;
import java.io.IOException;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws IOException {
        String sql = "SELECT *\n" +
                "FROM TEST_DB.TEST_TABLE T\n" +
                "WHERE T.ID = 1";
        Set<String> tableSet = parse(sql);
        for (String table : tableSet) {
            System.out.println(table);
        }
    }

    public static Set<String> parse(String sql) throws IOException {
        // 必须转换为大写才能正确解析
        sql = sql.toUpperCase();
        // 创建字符流
        CharStream charStream = CharStreams.fromString(sql);
        // 创建词法分析器
        PrestoSqlLexer lexer = new PrestoSqlLexer(charStream);
        // 创建词法符号缓冲器
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        // 创建语法解析器
        PrestoSqlParser parser = new PrestoSqlParser(tokens);
        // 指定预测模式
        parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
        // 创建解析器访问对应的方法
        PrestoSqlLineageParser lineageParser = new PrestoSqlLineageParser();
        lineageParser.visit(parser.statement());
        return lineageParser.getTableSet();
    }
}
