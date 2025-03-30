<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="airline-manager.leg.form.label.flightNumber" path="flightNumber"/>
	<acme:input-moment code="airline-manager.leg.form.label.scheduledDeparture" path="scheduledDeparture"/>
	<acme:input-moment code="airline-manager.leg.form.label.scheduledArrival" path="scheduledArrival"/>
	<acme:input-select code="airline-manager.leg.form.label.status" path="status" choices="${statuses}"/>	

	<acme:input-double code="airline-manager.leg.form.label.duration" path="duration"/>
	<acme:input-select code="airline-manager.leg.form.label.departureAirportIata" path="departureAirport" choices="${departureAirports}"/>
	<acme:input-select code="airline-manager.leg.form.label.arrivalAirportIata" path="arrivalAirport" choices="${arrivalAirports}" />
	<acme:input-select code="airline-manager.leg.form.label.aircraftRegNumber" path="aircraftRegNumber" choices="${aircrafts}"/>
	
</acme:form>