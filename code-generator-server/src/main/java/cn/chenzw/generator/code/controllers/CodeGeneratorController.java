package cn.chenzw.generator.code.controllers;

import cn.chenzw.generator.code.constants.CodeConstants;
import cn.chenzw.generator.code.service.CodeGeneratorService;
import cn.chenzw.toolkit.commons.FileExtUtils;
import cn.chenzw.toolkit.datasource.core.factory.TableDefinitionFactory;
import cn.chenzw.toolkit.datasource.entity.TableDefinition;
import cn.chenzw.toolkit.freemarker.FreeMarkerUtils;
import freemarker.template.TemplateException;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
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
    CodeGeneratorService codeGeneratorService;

    @GetMapping("/generate")
    public void generate(String tableName,
            @RequestParam(required = false, defaultValue = CodeConstants.TEMPLATE_BASIC) String template)
            throws SQLException, IOException, InstantiationException, TemplateException {
        codeGeneratorService.generate(tableName, template);
    }

}
