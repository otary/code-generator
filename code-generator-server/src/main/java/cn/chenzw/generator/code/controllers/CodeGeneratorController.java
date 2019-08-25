package cn.chenzw.generator.code.controllers;

import cn.chenzw.generator.code.constants.CodeConstants;
import cn.chenzw.generator.code.service.CodeGeneratorService;
import cn.chenzw.toolkit.http.ResponseUtils;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            codeGeneratorService.generate(tableName, theme, baos);
            ResponseUtils.download("packages.zip", new ByteArrayInputStream(baos.toByteArray()));
        }
    }

}
