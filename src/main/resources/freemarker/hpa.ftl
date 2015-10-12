<#import "spring.ftl" as spring/>
<#macro page scripts=[]>
<#local title = "${title!'HLS Protocol Analyzer'}"/>
<!DOCTYPE html>
<html>
  <head>
    <title>${title}</title>
  </head>
  <body>
    <div data-role="page">
      <div data-role="header" data-theme="a">
        <h1>${title}</h1>
      </div>
      <div data-role="content">
        <div id="errorSection" data-role="collapsible" data-collapsed="false" data-theme="e" data-content-theme="c" <#if !errors??>style="display: none"</#if>>
          <h3>Errors</h3>
          <ul>
          <#if errors??>
          <#list errors as error>
            <li>${error}</li>
          </#list>
          </#if>
          </ul>
        </div>
        <#nested/>
      </div>
      <#-- <#if errors??>
        <@submitNavigationWarning errors/>
      </#if>
      <@busyPopup/>
	  -->
    </div>
  </body>
</html>
</#macro>

<#--
 * row
 *
 * Begin a form row.  The row's contents are nested within this macro.
 *
 * @param sizes list of cell sizes within the row, e.g. ["50%", "30%", "20%"].
 *        If no sizes are given, the sizes from the last row will be used.
-->
<#macro row sizes=[]>
  <#if sizes?size != 0>
    <#assign hpaRowSizes = sizes/>
  </#if>
  <#assign hpaCellIndex = 0/>
  <div class="formrow">
    <#nested/>
  </div>
</#macro>

<#--
 * cell
 *
 * Render a form cell.  Its width is specified by the last call to @row.
 * The cell's contents are nested within this macro.
-->
<#macro cell>
  <#local size = hpaRowSizes[hpaCellIndex]/>
  <#assign hpaCellIndex = hpaCellIndex + 1/>
  <div class="formcell" style="width: ${size}">
    <#nested/>
  </div>  
</#macro>


<#--
 * submitNavigationWarning
 *
 * Render the dialog for submitting with errors.
-->
<#macro submitNavigationWarning messages>
  <div id="submitError" data-role="popup" data-overlay-theme="a">
    <div data-role="header">
      <h1>Error</h1>
    </div>
    <div data-role="content">
      <h3 class="ui-title">Problems were found:</h3>
      <ul>
        <#list messages as message>
        <li>${message}</li>
        </#list>
      </ul>
      <div class="pageButtons">
        <a href="#" data-role="button" data-inline="true" data-theme="b" data-rel="back">OK</a>
      </div>
    </div>
  </div>
</#macro>



<#--
 * modalBusyPopup
 *
 * Render the modal "busy" popup.
-->
<#macro busyPopup>
<div id="busyPopup" data-role="popup" data-overlay-theme="a" data-dismissible="false" style="width: 400px; height: 175px;">
  <div data-role="content">
    <#-- <p class="message">Loading...</p> -->
  </div>
</div>
</#macro>

<#--
 * label
 *
 * Render a form input bound to this path.
 *
 * @param path the name of the field to bind to
 * @param class the class attribute
 * @param attrs extra attributes to add to the tag, except for class
-->
<#macro label path class='' attrs=''>
  <@spring.bind path/>
   
  <#if bindingResult?? && bindingResult.hasFieldErrors(spring.status.expression)>
    <#local class = 'error ' + class>
  </#if>

  <#if class != ''>
    <#local attrs = 'class="' + class + '" ' + attrs/>
  </#if>

  <label for="${fieldId(path)?html}" ${attrs}><#nested/></label>
</#macro>

<#--
 * formInput
 *
 * Render a form input bound to this path.
 *
 * @param path the name of the field to bind to
 * @param class the class attribute of the field
 * @param attrs extra attributes to add to the tag, except class
-->
<#macro formInput path attributes='' class='' autocomplete=false>
  <@spring.bind path/>
  <#local maxlength = fieldInfo.getMaxLength(springMacroRequestContext, path) />
  <#if maxlength != -1>
    <#local attributes = 'maxlength="${maxlength}" ${attributes}' />
  </#if>
  <#if !autocomplete>
    <#local attributes = attributes + ' autocomplete="off"' />
  </#if>
  <#if bindingResult?? && bindingResult.hasFieldErrors(spring.status.expression)>
    <#local class='error ' + class/>
  </#if>
  <#if class != ''>
    <#local attributes = 'class="' + class + '" ' + attributes/>
  </#if>
  <@spring.formInput path, attributes />
</#macro>

<#--
 * fieldId
 *
 * Return the ID attribute that spring generates for this model path.
 * The path will be bound as a side-effect.
 *
 * @param path the name of the field to bind to
-->
<#function fieldId path>
  <@spring.bind path/>
  <#return spring.status.expression?replace('[','')?replace(']','')/>
</#function>