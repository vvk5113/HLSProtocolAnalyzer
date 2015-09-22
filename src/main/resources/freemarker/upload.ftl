<#include "hpa.ftl"/>
  
<@page>
  
  <form method="post" enctype="multipart/form-data">
    <@row ["50%", "50%"]>
      <@cell>
        <@label path="upload.streamURL">Stream URL</@label>
        <@formInput path="upload.streamURL"/>
      </@cell>
    </@row>

 	<br/>

	<@row ["50%", "50%"]>
      <@cell>
       	<@label path="upload.userEmail">User Email</@label>
        <@formInput path="upload.userEmail"/>
      </@cell>
    </@row>
          
    <br/> 
  
    <@row ["50%", "50%"]>
      <@cell>
        <p>All fields are required.</p>
      </@cell>
      <@cell>
        <div class="pageButtons">
          <button data-inline="true" data-theme="b" data-icon="arrow-r" data-iconpos="right" type="submit" id="submitBtn">Submit</button>
        </div>
      </@cell>
    </@row>
  </form>
</@page>
