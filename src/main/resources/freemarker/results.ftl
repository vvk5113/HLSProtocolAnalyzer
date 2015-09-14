<#include "hpa.ftl"/>
<#assign title="Results"/>
<@page scripts>
    <p>Wired Transfer of <b>$${upload.amount}</b> is submitted to policy <b>${upload.policyNumber}</b></p>
    <table style="text-align: left" class="hparesults">
      <tr>
        <th>Policy #:</th>
        <td>${upload.policyNumber}</td>
      </tr>
      <tr>
        <th>Company Code:</th>
        <td>${companyName}</td>
      </tr>
      <tr>
        <th>1035 Exchange Proceeds:</th>
        <td>${upload.isPayment1035ExchangeProceeds?string("Yes", "No")}</td>
      </tr>
      <tr>
        <th>Amount:</th>
        <td>$${upload.amount}</td>
      </tr>
      <tr>
        <th>Effective Date:</th>
        <td>${upload.effectiveDate?string('MM/dd/yyyy')}</td>
      </tr>
      <tr>
        <th>Remitter Relationship:</th>
        <td>${valueLookup.describe("HPA_RemitterRelationsToCase", upload.remitterRelationshipToCase)}</td>
      </tr>
    </table>
    <br/>
    <div>
      <button type="button" data-inline="true" data-theme="b" data-icon="printer" onclick="window.print();">Print</button>
      <a href="<@spring.url '/'/>" data-role="button" data-theme="b" data-inline="true">OK</a>
    </div>
</@page>