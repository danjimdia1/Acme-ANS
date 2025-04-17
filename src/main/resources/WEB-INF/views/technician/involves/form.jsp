<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>
 
 <acme:form>
     <acme:input-select code="technician.involves.form.label.task" path="task" choices="${task}"/>
 	<jstl:choose>
 	
 		<jstl:when test="${_command == 'create'}">
 			<acme:button code="technician.task.list.button.create" action ="/technician/task/create"/>
 			
 			<acme:submit code="technician.involves.form.button.create" action="/technician/involves/create?maintenanceRecordId=${maintenanceRecordId}"/>
 		</jstl:when>
 		
 		<jstl:when test="${_command != 'create'}"> 			
 			<acme:submit code="technician.involves.form.button.delete" action="/technician/involves/delete?maintenanceRecordId=${maintenanceRecordId}"/>
 		</jstl:when>
 				
 	</jstl:choose>
 </acme:form>