<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox readonly="true" code="airline-manager.leg.form.label.airlineIata" path="airlineIata"/>
	<acme:input-textbox code="airline-manager.leg.form.label.flightNumber" path="flightNumber"/>
	<acme:input-moment code="airline-manager.leg.form.label.scheduledDeparture" path="scheduledDeparture"/>
	<acme:input-moment code="airline-manager.leg.form.label.scheduledArrival" path="scheduledArrival"/>
	<acme:input-select code="airline-manager.leg.form.label.status" path="status" choices="${statuses}"/>	

	<acme:input-select code="airline-manager.leg.form.label.departureAirportIata" path="departureAirport" choices="${departureAirports}"/>
	<acme:input-select code="airline-manager.leg.form.label.arrivalAirportIata" path="arrivalAirport" choices="${arrivalAirports}" />
	<acme:input-select code="airline-manager.leg.form.label.aircraftRegNumber" path="aircraft" choices="${aircrafts}"/>
	
	<jstl:if test="${acme:anyOf(_command, 'show|update|delete|publish')}">
		<acme:input-double readonly="true" code="airline-manager.leg.form.label.duration" path="duration"/>
	</jstl:if>
	
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete|publish') && draftMode == true}">
			<acme:submit code="airline-manager.leg.form.button.update" action="/airline-manager/leg/update"/>
			<acme:submit code="airline-manager.leg.form.button.delete" action="/airline-manager/leg/delete"/>
			<acme:submit code="airline-manager.leg.form.button.publish" action="/airline-manager/leg/publish"/>
		</jstl:when>
    	<jstl:when test="${_command == 'create'}">
        	<acme:submit code="airline-manager.leg.form.button.create" action="/airline-manager/leg/create?flightId=${flightId}"/>
    	</jstl:when>
	</jstl:choose>
</acme:form>