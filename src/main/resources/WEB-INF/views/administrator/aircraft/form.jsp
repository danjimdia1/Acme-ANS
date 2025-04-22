<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="administrator.aicraft.form.label.model" path="model"/>
	<acme:input-textbox code="administrator.aicraft.form.label.registrationNumber" path="registrationNumber"/>

	<acme:input-integer code="administrator.aicraft.form.label.capacity" path="capacity"/>
	
<jstl:choose>
  <jstl:when test="${_command == 'create'}">
    <acme:input-select
      code="administrator.aicraft.form.label.status"
      path="status"
      choices="${statuses}"/>
  </jstl:when>
  <jstl:otherwise>
    <acme:input-textbox
      readonly="true"
      code="administrator.aicraft.form.label.status"
      path="status"/>
  </jstl:otherwise>
</jstl:choose>
	<acme:input-integer code="administrator.aicraft.form.label.cargoWeight" path="cargoWeight" />
	
	<acme:input-textbox code="administrator.aicraft.form.label.details" path="details"/>
	<acme:input-select code="administrator.aicraft.form.label.airline" path="airline" choices="${airlines}"/>
	
	
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete') && status == 'ACTIVE'}">
			<acme:input-checkbox code="administrator.aircraft.form.label.confirmation" path="confirmation"/>
			<acme:submit code="administrator.aircraft.form.button.update" action="/administrator/aircraft/update"/>
			<acme:submit code="administrator.aircraft.form.button.disable" action="/administrator/aircraft/disable"/>
		</jstl:when>
			<jstl:when test="${acme:anyOf(_command, 'show|update|delete') && status == 'MAINTENANCE'}">
			<acme:input-checkbox code="administrator.aircraft.form.label.confirmation" path="confirmation"/>
			<acme:submit code="administrator.aircraft.form.button.update" action="/administrator/aircraft/update"/>
			<acme:submit code="administrator.aircraft.form.button.enable" action="/administrator/aircraft/disable"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:input-checkbox code="administrator.aircraft.form.label.confirmation" path="confirmation"/>
			<acme:submit code="administrator.aircraft.form.button.create" action="/administrator/aircraft/create"/>
		</jstl:when>		
	</jstl:choose>

</acme:form>