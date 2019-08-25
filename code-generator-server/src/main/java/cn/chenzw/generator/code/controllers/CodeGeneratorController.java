package cn.chenzw.generator.code.controllers;

import cn.chenzw.generator.code.constants.CodeConstants;
import cn.chenzw.toolkit.commons.FileExtUtils;
import cn.chenzw.toolkit.datasource.entity.TableDefinition;
import cn.chenzw.toolkit.datasource.mysql.builder.MySqlTableDefinitionBuilder;
import cn.chenzw.toolkit.freemarker.FreeMarkerUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chenzw
 */
@RestController
@RequestMapping("/code-generator")
public class CodeGeneratorController {

    @Autowired
    DataSource dataSource;

    @GetMapping("/generate")
    public void generate(String tableName) throws SQLException {
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
        Collection<File> ftlFiles = FileUtils.listFiles(classPathResource.getFile(), new String[]{CodeConstants.TEMPLATE_SUFFIX}, true);
        for (File ftlFile : ftlFiles) {
            // 获取模版文件相对于模版目录的地址（包路径）
            String relativePath = FileExtUtils.relativePath(ftlFile.getPath(), classPathResource.getFile().getPath());
            FreeMarkerUtils.processToFile(ftlFile, dataModel, new File(FreeMarkerUtils.processToString(relativePath, dataModel).replaceAll("." + CodeConstants.TEMPLATE_SUFFIX, ".java")));
        }
        connection.close();

    }

}
