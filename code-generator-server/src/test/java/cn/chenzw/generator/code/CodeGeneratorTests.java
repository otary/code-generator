package cn.chenzw.generator.code;

import cn.chenzw.generator.code.constants.CodeConstants;
import cn.chenzw.toolkit.commons.FileExtUtils;
import cn.chenzw.toolkit.datasource.entity.TableDefinition;
import cn.chenzw.toolkit.datasource.mysql.builder.MySqlTableDefinitionBuilder;
import cn.chenzw.toolkit.datasource.oracle.builder.OracleTableDefinitionBuilder;
import cn.chenzw.toolkit.freemarker.FreeMarkerUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.template.TemplateException;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = CodeGeneratorApp.class)
@WebAppConfiguration
@ActiveProfiles("oracle")
public class CodeGeneratorTests {

    @Autowired
    DataSource dataSource;

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void stepUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void testOracle() throws SQLException, IOException, TemplateException {
        Connection connection = dataSource.getConnection();

        TableDefinition tableDefinition = new OracleTableDefinitionBuilder(connection, "STAFF").build();


        System.out.println(tableDefinition);
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("tableDefinition", tableDefinition);

        ClassPathResource classPathResource = new ClassPathResource(
                "template/basic/cn/chenzw/repository/${tableDefinition.pascalName}Mapper.ftl");
        String s = FreeMarkerUtils.processToString(classPathResource.getFile(), dataModel);

        System.out.println(s);

        connection.close();

    }

    /**
     * @throws SQLException
     * @throws IOException
     * @throws TemplateException
     */
    @Test
    public void testMySql() throws SQLException, IOException, TemplateException {
        Connection connection = dataSource.getConnection();

        TableDefinition tableDefinition = new MySqlTableDefinitionBuilder(connection, "sys_user").build();

        ObjectMapper objectMapper = new ObjectMapper();

        System.out.println(objectMapper.writeValueAsString(tableDefinition));

        Map<String, Object> dataModel = new HashMap<String, Object>() {
            {
                put(CodeConstants.TABLE_DEFINITION, tableDefinition);
            }
        };

        ClassPathResource classPathResource = new ClassPathResource(CodeConstants.TEMPLATE_BASIC);
        Collection<File> ftlFiles = FileUtils
                .listFiles(classPathResource.getFile(), new String[]{CodeConstants.TEMPLATE_SUFFIX}, true);
        for (File ftlFile : ftlFiles) {
            // 获取模版文件相对于模版目录的地址（包路径）
            String relativePath = FileExtUtils.relativePath(ftlFile.getPath(), classPathResource.getFile().getPath());
            FreeMarkerUtils.processToFile(ftlFile, dataModel, new File(
                    FreeMarkerUtils.processToString(relativePath, dataModel)
                            .replaceAll("." + CodeConstants.TEMPLATE_SUFFIX, ".java")));
        }
        connection.close();
    }


    @Test
    public void testGenerate() throws Exception {
        this.mockMvc.perform(get("/code-generator/generate").param("tableName", "STAFF")).andExpect(status().isOk());
    }


}
