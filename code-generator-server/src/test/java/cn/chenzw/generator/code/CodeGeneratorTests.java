package cn.chenzw.generator.code;

import cn.chenzw.toolkit.datasource.entity.TableDefinition;
import cn.chenzw.toolkit.datasource.mysql.builder.MySqlTableDefinitionBuilder;
import cn.chenzw.toolkit.datasource.oracle.builder.OracleTableDefinitionBuilder;
import cn.chenzw.toolkit.freemarker.FreeMarkerUtils;
import freemarker.template.TemplateException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = CodeGeneratorApp.class)
@WebAppConfiguration
public class CodeGeneratorTests {

    @Autowired
    DataSource dataSource;

    @Autowired
    ApplicationContext applicationContext;

    @Test
    public void test() throws SQLException, IOException, TemplateException {

        Connection connection = dataSource.getConnection();

        TableDefinition tableDefinition = new OracleTableDefinitionBuilder(connection, "STAFF").build();


        System.out.println(tableDefinition);
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("tableDefinition", tableDefinition);

        ClassPathResource classPathResource = new ClassPathResource("template/basic/repository.ftl");
        System.out.println(classPathResource.getFile().getPath());
         String s = FreeMarkerUtils.processToString(classPathResource.getFile(), dataModel);

         System.out.println(s);

        connection.close();


    }
}
