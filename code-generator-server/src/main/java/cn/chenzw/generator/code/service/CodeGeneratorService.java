package cn.chenzw.generator.code.service;

import cn.chenzw.generator.code.constants.CodeConstants;
import cn.chenzw.toolkit.commons.FileExtUtils;
import cn.chenzw.toolkit.datasource.core.factory.TableDefinitionFactory;
import cn.chenzw.toolkit.datasource.entity.TableDefinition;
import cn.chenzw.toolkit.freemarker.FreeMarkerUtils;
import freemarker.template.TemplateException;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class CodeGeneratorService {

    @Autowired
    DataSource dataSource;

    public void generate(String tableName, String template)
            throws SQLException, InstantiationException, IOException, TemplateException {
        TableDefinition tableDefinition = TableDefinitionFactory.create(dataSource, tableName);

        System.out.println("------------------:" + tableDefinition);


        Map<String, Object> dataModel = new HashMap<String, Object>() {
            {
                put(CodeConstants.TABLE_DEFINITION, tableDefinition);
            }
        };

        ClassPathResource templateResource = new ClassPathResource(
                CodeConstants.TEMPLATE_BASE + File.separator + template);

        // 获取目录下的所有模版文件
        Collection<File> ftlFiles = FileUtils
                .listFiles(templateResource.getFile(), new String[]{CodeConstants.TEMPLATE_SUFFIX}, true);

        for (File ftlFile : ftlFiles) {
            // 获取模版文件相对于模版目录的地址（包路径）
            String relativePath = FileExtUtils.relativePath(ftlFile.getPath(), templateResource.getFile().getPath());
            FreeMarkerUtils.processToFile(ftlFile, dataModel, new File(
                    CodeConstants.RESULT_TMP_PATH + File.separator + FreeMarkerUtils
                            .processToString(relativePath, dataModel)
                            .replaceAll("." + CodeConstants.TEMPLATE_SUFFIX, "." + CodeConstants.RESULT_SUFFIX)));
        }
    }

}
