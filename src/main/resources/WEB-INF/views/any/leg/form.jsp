<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="any.leg.form.label.flightNumber" path="flightNumber"/>
	<acme:input-moment code="any.leg.form.label.scheduledDeparture" path="scheduledDeparture"/>
	<acme:input-moment code="any.leg.form.label.scheduledArrival" path="scheduledArrival"/>
	<acme:input-textbox code="any.leg.form.label.status" path="status"/>	
	<acme:input-textbox code="any.leg.form.label.duration" path="duration"/>
	<acme:input-textbox code="any.leg.form.label.departureAirport" path="departureAirport"/>
	<acme:input-textbox code="any.leg.form.label.arrivalAirport" path="arrivalAirport"/>
	<acme:input-textbox code="any.leg.form.label.aircraftRegNumber" path="aircraft"/>
</acme:form>