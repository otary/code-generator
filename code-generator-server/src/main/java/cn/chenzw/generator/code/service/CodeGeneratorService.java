package cn.chenzw.generator.code.service;

import cn.chenzw.generator.code.constants.CodeConstants;
import cn.chenzw.toolkit.commons.FileExtUtils;
import cn.chenzw.toolkit.commons.ZipUtils;
import cn.chenzw.toolkit.datasource.core.factory.TableDefinitionFactory;
import cn.chenzw.toolkit.datasource.entity.TableDefinition;
import cn.chenzw.toolkit.freemarker.FreeMarkerUtils;
import freemarker.template.TemplateException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class CodeGeneratorService {

    @Autowired
    DataSource dataSource;

    public void generate(String tableName, String theme, OutputStream outputStream)
            throws SQLException, InstantiationException, IOException, TemplateException {
        TableDefinition tableDefinition = TableDefinitionFactory.create(dataSource, tableName);

        if (tableDefinition == null) {
            throw new IllegalArgumentException("Table [" + tableName + "] dose not exist!");
        }

        Map<String, Object> dataModel = new HashMap<String, Object>() {
            {
                put(CodeConstants.TABLE_DEFINITION, tableDefinition);
            }
        };

        ClassPathResource templateResource = new ClassPathResource(
                CodeConstants.TEMPLATE_BASE + File.separator + theme);

        // 获取目录下的所有模版文件
        Collection<File> ftlFiles = FileUtils
                .listFiles(templateResource.getFile(), new String[]{CodeConstants.TEMPLATE_SUFFIX}, true);

        File tmpDirectory = new File(CodeConstants.RESULT_TMP_PATH, RandomStringUtils.randomAlphabetic(10));
        for (File ftlFile : ftlFiles) {
            // 获取模版文件相对于模版目录的地址（包路径）
            String relativePath = FileExtUtils.relativePath(ftlFile.getPath(), templateResource.getFile().getPath());
            FreeMarkerUtils.processToFile(ftlFile, dataModel, new File(tmpDirectory.getPath(),
                    FreeMarkerUtils.processToString(relativePath, dataModel)
                            .replaceAll("." + CodeConstants.TEMPLATE_SUFFIX, "." + CodeConstants.RESULT_SUFFIX)));
        }
        // 压缩文件
        ZipUtils.toZip(tmpDirectory, outputStream);

        // 删除文件
        FileUtils.deleteDirectory(tmpDirectory);
    }

}
