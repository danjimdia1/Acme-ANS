<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code='any.flight.list.label.tag' path="tag" width="25%"/>
	<acme:list-column code='any.flight.list.label.selfTransfer' path="selfTransfer" width="25%"/>
	<acme:list-column code='any.flight.list.label.cost' path="cost" width="25%"/>
</acme:list>