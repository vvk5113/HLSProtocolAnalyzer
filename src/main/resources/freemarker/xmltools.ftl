<#--
 * node
 *
 * Render a single XML node, or nothing if no value is present.
 *
 * @param key XML tag name
 * @param value text content as a string
 * @param date text content as a date object
 * @param tc type code.  The text content will be generated accordingly.
 * @param removeDashes remove dashes from the value content (useful for SSN, TIN)
-->
<#macro node key value="" date="" tc="" removeDashes=false>
  <#escape x as x?xml>
    <#local content=value>
    <#if removeDashes && date == "" && tc == "">
      <#local content = content?replace("-", "")/>
    </#if>
    <#if !date?is_string>
      <#local content="${dateFormat.format(date)}">
    </#if>
    <#if tc != "">
      <#local content=valueLookup.describe(key, tc)>
      <${key} tc="${tc}">${content}</${key}>
    <#elseif !content?is_string || content != "">
      <${key}>${content}</${key}>
    </#if>
  </#escape>
</#macro>

<#-- 
 * container
 *
 * Render a container element.  If the nested content is empty, the element will not be rendered.
 *
 * @param name the tag name
 * @param id the optional element id
-->
<#macro container name id="">
  <#local content><#nested/></#local>
  <#local trimmed = content?trim/>
  <#-- Workaround for maven filtering 'name' and 'id' -->
  <#local tagName = name/>
  <#local tagId = id/>
  <#if trimmed?has_content>
    <${tagName}<#if id?has_content> id="${tagId}"</#if>>
      <#nested/>
    </${tagName}>
  </#if>
</#macro>

<#--
 * olifeExtension
 *
 * Render an OLifEExtension.  If the nested content is empty, the element will not be rendered.
 *
 * @param baseName the element base name.  For example, "Person" will create a "PersonExtension".
 * @param vendorCode the OLifEExtension vendor code.
-->
<#macro olifeExtension baseName vendorCode="123">
  <#local content><#nested/></#local>
  <#local trimmed = content?trim/>
  <#if trimmed?has_content>
    <OLifEExtension VendorCode="${vendorCode}" ExtensionCode="${baseName}">
      <${baseName}Extension>
        <#nested/>
      </${baseName}Extension>
    </OLifEExtension>
  </#if>
</#macro>    

<#--
 * numberNode
 *
 * Render a single XML node for a number, or nothing if no value is present.
 * The value will be formatted as a computer-readable number.
 *
 * @param key XML tag name
 * @param number number object
-->
<#macro numberNode key number="">
  <#if !number?is_string>
    <@node key "${number?string.computer}" />
  </#if>
</#macro>

<#--
 * optNumberNode
 *
 * Render a single XML node for a number, or nothing if no value is present or the value is zero.
 * The value will be formatted as a computer-readable number.
 *
 * @param key XML tag name
 * @param number number object
-->
<#macro optNumberNode key number="">
  <#if !number?is_string>
    <#if number != 0>
      <@numberNode key number/>
    </#if>
  </#if>
</#macro>


<#--
 * percentNode
 *
 * Render a single XML node for a number, or nothing if no value is present.
 * The value will be multiplied by 100, as Spring percents are stored as a fraction.
 *
 * @param key XML tag name
 * @number number object
-->
<#macro percentNode key number="">
  <#if !number?is_string>
    <#assign value = number * 100 />
    <@node key "${value?string.computer}" />
  </#if>
</#macro>

<#--
 * dateNode
 *
 * Render a single XML node for a date, or nothing if no value is present.
 *
 * @param key XML tag name
 * @param date date object
-->
<#macro dateNode key date="">
  <@node key=key date=date />
</#macro>

<#--
 * tcNode
 *
 * Render a single XML node for a type code, or nothing if no value is present.
 *
 * @param key XML tag name
 * @param tc type code
-->
<#macro tcNode key tc="">
  <@node key=key tc=tc />
</#macro>

<#--
 * customTcNode
 * 
 * Render a single XML node for a tag whose ACORD type is different than its tag name
 *
 * @param key XML tag name
 * @param customType ACORD tag name
 * @param tc type code
-->
<#macro customTcNode key customType tc="">
  <#escape x as x?xml>
    <#if tc != "">
      <#local content=valueLookup.describe(customType, tc)>
      <${key} tc="${tc}">${content}</${key}>
    </#if>
  </#escape>
</#macro>

<#--
 * descriptionNode
 *
 * Render a single XML node by looking up a description of a type code, but not including a tc parameter.
 *
 * @param key XML tag name
 * @param customType ACORD tag name
 * @param tc type code
-->
<#macro descriptionNode key customType tc="">
  <#escape x as x?xml>
    <#if tc != "">
      <#local content=valueLookup.describe(customType, tc)>
      <${key}>${content}</${key}>
    </#if>
  </#escape>
</#macro>

<#--
 * stateTCNode
 *
 * Render a single XML node for a state, or nothing if no value is present.
 *
 * @param key XML tag name
 * @param state state abbreviation
-->
<#macro stateTCNode key state="">
  <#if state != "">
    <#local tc = valueLookup.getCodeForDescription("HPA_State", state?upper_case)/>
    <#local description = valueLookup.describe("AddressStateTC", tc)/>
    <@optionalTcNode key, description, tc/>
  </#if>
</#macro>

<#--
 * stateDescriptionNode
 *
 * Render a single XML node for a state, transforming the 2 letter state abbreviation into an ACORD state description.
 * No tc parameter will be used.
 *
 * @param key XML tag name
 * @param state state abbreviation
-->
<#macro stateDescriptionNode key state="">
  <#escape x as x?xml>
    <#if state != "">
      <#local tc = valueLookup.getCodeForDescription("HPA_State", state?upper_case)/>
      <#local content = valueLookup.describe("AddressStateTC", tc)/>
      <${key}>${content}</${key}>
    </#if>
  </#escape>
</#macro>
      
<#--
 * optionalTcNode
 *
 * Render a single XML node for a type code, or nothing if no value is present.
 * If no type code is present, use type code 0 (the standard ACORD unknown code).
 *
 * @param key XML tag name
 * @param state state text
-->
<#macro optionalTcNode key content tc="0">
  <#-- ValueLookup is returning blank instead of null now. -->
  <#if tc == "">
    <#local tc="0">
  </#if>
  <#escape x as x?xml>
    <${key} tc="${tc}">${content}</${key}>
  </#escape>
</#macro>

<#--
 * boolNode
 *
 * Render a single XML node for an OLI_LU_BOOLEAN value.
 *
 * @param key XML tag name
 * @param bool boolean value or null to skip the tag, unless default is given
 * @param default default value to use if bool is null
-->
<#macro boolNode key bool="" default="">
  <#escape x as x?xml>
    <#-- Assign default value to bool if it was not given. -->
    <#if bool?is_string>
      <#local bool = default/>
    </#if>
    <#if !bool?is_string>
      <#if !bool>
        <#local tc = "0" />
        <#local content = "False" />
      <#else>
        <#local tc = "1" />
        <#local content = "True" />
      </#if>
      <${key} tc="${tc}">${content}</${key}>
    </#if>
  </#escape>
</#macro>

<#--
 * boolTcNode
 *
 * Render an XML node with the given type code if a boolean is true.
 *
 * @param key XML tag name
 * @param bool boolean value or null to skip the tag
 * @param tc type code
 * @param value description string for the type code, or blank to look it up
-->
<#macro boolTcNode key bool="" tc="" value="">
  <#-- tc parameter is optional only to placate the interpreter.  It is NOT an optional parameter. -->
  <#if !bool?is_string && bool>
    <#if value == "">
      <@tcNode key, tc />
    <#else>
      <#escape x as x?xml>
        <${key} tc="${tc}">${value}</${key}>
      </#escape>
    </#if>
  </#if>
</#macro>
