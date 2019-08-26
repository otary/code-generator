package cn.chenzw.generator.code.controllers;

import cn.chenzw.generator.code.constants.CodeConstants;
import cn.chenzw.generator.code.service.CodeGeneratorService;
import freemarker.template.TemplateException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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
@Api(tags = "代码生成器")
public class CodeGeneratorController {


    @Autowired
    CodeGeneratorService codeGeneratorService;

    @ApiOperation("生成代码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tableName", value = "数据库表名"),
            @ApiImplicitParam(name = "theme", value = "模版主题（默认：baisc）", required = false)
    })
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
