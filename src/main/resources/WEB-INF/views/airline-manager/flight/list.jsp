<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code='airline-manager.flight.list.label.tag' path="tag" width="10%"/>
	<acme:list-column code='airline-manager.flight.list.label.selfTransfer' path="selfTransfer" width="10%"/>
	<acme:list-column code='airline-manager.flight.list.label.cost' path="cost" width="10%"/>
	<acme:list-column code='airline-manager.flight.list.label.description' path="description" width="10%"/>
	<acme:list-payload path="payload"/>
</acme:list>