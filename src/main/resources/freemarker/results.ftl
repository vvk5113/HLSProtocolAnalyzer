<#include "hpa.ftl"/>
<#assign title="Validation Results"/>
<@page scripts>
    <p>The master and media streams have been validated. The validation results are available at following location.</p>
    <table style="text-align: left" class="hparesults">
      <tr>
        <th>Master Playlist Validation Results:</th>
        <td>${upload.masterPlaylistValidationResult}</td>
      </tr>
      <tr>
        <th>Media Playlist Validation Results:</th>
        <td>${upload.mediaPlaylistValidationResult}</td>
      </tr>
      <tr/>
      <#if upload.userEmail?? && upload.userEmail?has_content>
	  <tr>
        <td colspan="2">The validation results have also attached to the email and sent at address ${upload.userEmail}</td>
      </tr>
      </#if>
    </table>
    <br/>
    <div>
      <button type="button" data-inline="true" data-theme="b" data-icon="printer" onclick="window.print();">Print</button>
      <a href="<@spring.url '/'/>" data-role="button" data-theme="b" data-inline="true">OK</a>
    </div>
</@page>