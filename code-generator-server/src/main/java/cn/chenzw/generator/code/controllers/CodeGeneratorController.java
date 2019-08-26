package cn.chenzw.generator.code.controllers;

import cn.chenzw.generator.code.constants.CodeConstants;
import cn.chenzw.generator.code.service.CodeGeneratorService;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

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
                         @RequestParam(required = false, defaultValue = CodeConstants.TEMPLATE_BASIC) String theme)
            throws SQLException, IOException, InstantiationException, TemplateException {

        ClassPathResource templateResource = new ClassPathResource(
                CodeConstants.TEMPLATE_BASE + File.separator + theme);

        if (!templateResource.exists()) {
            throw new IllegalArgumentException("Theme [" + theme + "] dose not exist!");
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            codeGeneratorService.generateAndDownload(tableName, theme, baos);
        }
    }

}
