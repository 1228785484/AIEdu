package org.sevengod.javabe.codegen;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.builder.Entity;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.sql.Types;
import java.util.Collections;

public class CodeGenerator {
    public static void main(String[] args) {
        String projectPath = System.getProperty("user.dir");
        String templatePath = projectPath + "/src/main/java/org/sevengod/javabe/codegen/templates";
        
        // 策略配置
        StrategyConfig strategyConfig = new StrategyConfig.Builder()
                .addInclude("users") // 设置需要生成的表名
                .entityBuilder()
                    .enableLombok() // 开启Lombok
                    .enableTableFieldAnnotation() // 开启字段注解
                    .javaTemplate(templatePath + "/entity.java")
                    .disable()
                .controllerBuilder()
                    .enableRestStyle() // 开启生成@RestController
                    .template(templatePath+"/controller.java")
                .serviceBuilder()
                    .disableService()
                    .formatServiceFileName("%sService") // service接口命名方式
                    .formatServiceImplFileName("%sServiceImpl") // service实现类命名方式
                    .serviceTemplate(templatePath + "/service.java")
                    .serviceImplTemplate(templatePath + "/serviceImpl.java")
                .mapperBuilder()
                .enableMapperAnnotation() // 开启@Mapper注解
                .superClass(templatePath + "/mapper.java")
                .build();

        FastAutoGenerator.create("jdbc:mysql://localhost:3306/stu_review?useSSL=false&serverTimezone=UTC", "root", "123456")
                .globalConfig(builder -> {
                    builder.author("SevenGod") // 设置作者
                            .enableSwagger() // 开启 swagger 模式
                            .outputDir("D://code_output") // 指定输出目录
                            .enableSpringdoc(); // 开启 springdoc 注解
                })
                .dataSourceConfig(builder ->
                        builder.typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                            int typeCode = metaInfo.getJdbcType().TYPE_CODE;
                            if (typeCode == Types.SMALLINT) {
                                return DbColumnType.INTEGER;
                            }
                            return typeRegistry.getColumnType(metaInfo);
                        })
                )
                .packageConfig(builder ->
                        builder.parent("org.sevengod.javabe") // 设置父包名
                                .moduleName("entity") // 设置模块名
                                .entity("User")
                )
                .strategyConfig(builder -> {
                    builder.addInclude("users") // 设置需要生成的表名
                            .entityBuilder()
                            .enableLombok() // 开启Lombok
                            .enableTableFieldAnnotation() // 开启字段注解
                            .javaTemplate(templatePath + "/entity.java")
                            .disable()
                            .controllerBuilder()
                            .enableRestStyle() // 开启生成@RestController
                            .template(templatePath+"/controller.java")
                            .serviceBuilder()
                            .disableService()
                            .formatServiceFileName("%sService") // service接口命名方式
                            .formatServiceImplFileName("%sServiceImpl") // service实现类命名方式
                            .serviceTemplate(templatePath + "/service.java")
                            .serviceImplTemplate(templatePath + "/serviceImpl.java")
                            .mapperBuilder()
                            .enableMapperAnnotation() // 开启@Mapper注解
                            .superClass(templatePath + "/mapper.java")
                            .build();
                })
//                .strategyConfig(builder -> {
//                    builder.addInclude("users") // 设置需要生成的表名
//                            .entityBuilder()
//                            .enableLombok() // 启用 Lombok
//                            .enableTableFieldAnnotation() // 启用字段注解
//                            .controllerBuilder()
//                            .enableRestStyle(); // 启用 REST 风格
//                })
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}
