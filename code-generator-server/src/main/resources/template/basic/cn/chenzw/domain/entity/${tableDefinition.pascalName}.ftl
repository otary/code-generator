package com.domain.entity;

<#list tableDefinition.javaTypes as javaType>
import ${javaType};
</#list>

/**
 * ${tableDefinition.remarks}
 *
 * @auther chenzw
 */
public class ${tableDefinition.pascalName} {

<#list tableDefinition.columnDefinitions as columnDefinition>
    /**
     * ${columnDefinition.remarks}
     */
    private ${columnDefinition.javaType.simpleName} ${columnDefinition.camelCaseName};

</#list>

}