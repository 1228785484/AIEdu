package ${package.Entity};

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
<#if entitySerialVersionUID>
import java.io.Serializable;
</#if>
<#if hasDateTypes>
import java.time.LocalDateTime;
</#if>

/**
 * ${table.comment!}
 *
 * @author ${author}
 * @since ${date}
 */
@Data
@Accessors(chain = true)
@TableName("${table.name}")
@Schema(name = "${entity}", description = "${table.comment!}")
<#if entitySerialVersionUID>
public class ${entity} implements Serializable {
<#else>
public class ${entity} {
</#if>
<#if entitySerialVersionUID>
    private static final long serialVersionUID = 1L;
</#if>

<#-- ----------  BEGIN 字段循环遍历  ---------->
<#list table.fields as field>
    <#if field.comment!?length gt 0>
    @Schema(description = "${field.comment}")
    </#if>
    <#if field.keyFlag>
    @TableId(value = "${field.annotationColumnName}", type = IdType.AUTO)
    <#else>
    @TableField("${field.annotationColumnName}")
    </#if>
    private ${field.propertyType} ${field.propertyName};

</#list>
<#------------  END 字段循环遍历  ---------->

    public static final String ID = "id";
<#list table.fields as field>
    <#if !field.keyFlag>
    public static final String ${field.name?upper_case} = "${field.name}";
    </#if>
</#list>
}
