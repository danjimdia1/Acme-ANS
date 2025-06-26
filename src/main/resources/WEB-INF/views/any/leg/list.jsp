
<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="any.leg.label.scheduledDeparture" path="scheduledDeparture" width="20%" />
	<acme:list-column code="any.leg.label.scheduledArrival" path="scheduledArrival" width="20%" />
	<acme:list-column sortable="false" code="any.leg.label.departureAirport" path="departureAirport" width="20%" />
	<acme:list-column sortable="false" code="any.leg.label.arrivalAirport" path="arrivalAirport" width="20%" />			
</acme:list>